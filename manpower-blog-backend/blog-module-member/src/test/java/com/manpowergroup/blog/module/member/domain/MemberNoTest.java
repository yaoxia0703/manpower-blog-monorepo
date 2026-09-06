package com.manpowergroup.blog.module.member.domain;

import com.manpowergroup.blog.module.member.domain.model.member.MemberNo;
import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 会員番号の生成規則と不変条件を検証する。
 *
 * <p>会員番号は外部公開用の識別子であり、書式の逸脱がそのまま
 * 推測可能性の低下につながるため、生成側・復元側の双方を検証する。</p>
 */
class MemberNoTest {

    private static final LocalDate GENERATED_ON = LocalDate.of(2026, 8, 27);

    /** 書式を満たす既知の値。復元系の検証で基準として用いる。 */
    private static final String VALID_VALUE = "M202608270123456789ABCDEF";

    private static final int TOTAL_LENGTH = 25;

    /* ============ 生成 ============ */

    @Test
    void generatesNumberInExpectedFormat() {
        final MemberNo memberNo = MemberNo.generate(GENERATED_ON);

        assertThat(memberNo.value()).hasSize(TOTAL_LENGTH);
        assertThat(memberNo.value()).startsWith("M20260827");
    }

    /**
     * 生成日は引数で決まり、実行日に依存しないことを保証する。
     *
     * <p>内部で {@code now()} を呼ぶ実装へ戻した場合、このテストが破れる。</p>
     */
    @Test
    void reflectsGivenDateInsteadOfCurrentDate() {
        final MemberNo memberNo = MemberNo.generate(LocalDate.of(2020, 1, 5));

        assertThat(memberNo.value()).startsWith("M20200105");
    }

    /**
     * 乱数部が実際に分散していることを保証する。
     *
     * <p>乱数生成が固定値・低エントロピーな実装へ退化した場合、
     * 重複が発生してこのテストが破れる。</p>
     */
    @Test
    void generatesDistinctNumbers() {
        final Set<String> generated = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            generated.add(MemberNo.generate(GENERATED_ON).value());
        }

        assertThat(generated).hasSize(1000);
    }

    /**
     * 誤読しやすい文字が生成結果に現れないことを保証する。
     *
     * <p>会員番号は口頭伝達や手入力を想定するため、
     * {@code I} {@code L} {@code O} {@code U} を含めない。</p>
     */
    @Test
    void excludesAmbiguousCharacters() {
        for (int i = 0; i < 500; i++) {
            assertThat(MemberNo.generate(GENERATED_ON).value())
                    .doesNotContain("I", "L", "O", "U");
        }
    }

    @Test
    void rejectsNullGenerationDate() {
        assertThatThrownBy(() -> MemberNo.generate(null))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getCode())
                .isEqualTo(ErrorCode.BAD_REQUEST);
    }

    /* ============ 復元 ============ */

    @Test
    void restoresValidValue() {
        assertThat(MemberNo.of(VALID_VALUE).value()).isEqualTo(VALID_VALUE);
    }

    /**
     * 大小の表記ゆれが同一の値として扱われることを保証する。
     *
     * <p>Crockford Base32 は大小を区別しない。正規化しない実装では
     * 同じ番号が別値となり、等価判定と一意制約の両方が破れる。</p>
     */
    @Test
    void normalizesLowerCaseValue() {
        assertThat(MemberNo.of(VALID_VALUE.toLowerCase()))
                .isEqualTo(MemberNo.of(VALID_VALUE));
    }

    /** ログ出力で値そのものが得られることを保証する。 */
    @Test
    void exposesRawValueAsString() {
        assertThat(MemberNo.of(VALID_VALUE)).hasToString(VALID_VALUE);
    }

    /* ============ 不変条件違反 ============ */

    /**
     * 桁数一致だけでは通過してしまう暦上の不正日を拒否することを保証する。
     *
     * <p>13月40日は書式上8桁だが実在しない。正規表現による桁数検査のみの
     * 実装へ退化した場合、このテストが破れる。</p>
     */
    @Test
    void rejectsNonExistentDate() {
        assertBadRequest("M202613400123456789ABCDEF");
    }

    @Test
    void rejectsAmbiguousCharacterInRandomPart() {
        assertBadRequest("M20260827I123456789ABCDEF");
    }

    @Test
    void rejectsWrongPrefix() {
        assertBadRequest("X202608270123456789ABCDEF");
    }

    @Test
    void rejectsTooShortValue() {
        assertBadRequest("M20260827012345");
    }

    @Test
    void rejectsTooLongValue() {
        assertBadRequest(VALID_VALUE + "Z");
    }

    @Test
    void rejectsBlankValue() {
        assertBadRequest("   ");
    }

    @Test
    void rejectsNullValue() {
        assertBadRequest(null);
    }

    /**
     * 不変条件違反が業務例外として送出されることを保証する。
     *
     * <p>標準例外のままでは GlobalExceptionHandler に捕捉されず、
     * 入力不正にもかかわらず HTTP 500 を返してしまう。</p>
     */
    private static void assertBadRequest(String value) {
        assertThatThrownBy(() -> MemberNo.of(value))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getCode())
                .isEqualTo(ErrorCode.BAD_REQUEST);
    }
}

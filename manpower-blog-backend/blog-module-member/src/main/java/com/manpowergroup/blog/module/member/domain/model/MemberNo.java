package com.manpowergroup.blog.module.member.domain.model;

import com.manpowergroup.blog.shared.support.DomainGuard;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * 会員番号。
 *
 * <p>外部公開用の識別子であり、自増IDの代わりに URL や API へ露出させる。
 * このため、他者の会員番号を推測・列挙できないことが要件となる。</p>
 *
 * <p>形式：{@code M} + 生成日（yyyyMMdd） + 乱数16桁 = 25桁</p>
 *
 * <p>生成規則そのものは秘匿しない。利用者は自身の会員番号を見られるため、
 * 規則は事実上公開されているものとして扱う。安全性は乱数部の探索空間のみに依存させ、
 * 日付部には秘匿の役割を持たせない。</p>
 *
 * @author YAOXIA
 * @since 2026-08-27
 */
public record MemberNo(String value) {

    /** 種別プレフィックス。ログ・調査時に他の採番と区別するために付与する。 */
    private static final String PREFIX = "M";

    /** 生成日部の桁数（yyyyMMdd）。 */
    private static final int DATE_LENGTH = 8;

    /**
     * 乱数部の桁数。
     *
     * <p>32文字から16桁を選ぶため、探索空間は 32^16（80ビット相当）となる。
     * 日付部が既知であっても、この空間の総当たりは現実的でない。
     * 逆に乱数部を数桁に切り詰めると、日付を固定した総当たりで
     * 実在の会員番号を発見できてしまうため短縮しない。</p>
     */
    private static final int RANDOM_LENGTH = 16;

    /** 会員番号全体の桁数。桁数変更時の修正漏れを避けるため各部から算出する。 */
    private static final int TOTAL_LENGTH = PREFIX.length() + DATE_LENGTH + RANDOM_LENGTH;

    /**
     * 乱数部に使用する文字集合（Crockford Base32）。
     *
     * <p>{@code I} {@code L} {@code O} {@code U} を除いた32文字。
     * 会員番号は URL への露出や口頭伝達を想定するため、
     * {@code 0} と {@code O}、{@code 1} と {@code I} の誤読を構造的に排除する。</p>
     */
    private static final String ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";

    /** 生成日部の書式（yyyyMMdd）。 */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    /**
     * 乱数生成器。
     *
     * <p>{@link java.util.Random} は線形合同法であり、出力を数個観測すれば
     * 内部状態を復元して以降の値を予測できる。会員番号の用途上これは致命的なため、
     * 暗号論的に安全な {@link SecureRandom} を使用する。</p>
     *
     * <p>{@link SecureRandom} はスレッドセーフであり、インスタンス生成のコストも高いため、
     * 単一インスタンスを共有する。</p>
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 不変条件を強制する。
     *
     * <p>{@link #generate} 経由か復元経由かを問わず、この検証を必ず通す。
     * 大文字へ正規化するのは、Crockford Base32 が大小同一の文字集合であり、
     * 表記ゆれによって同一の番号が別値として扱われることを防ぐため。</p>
     */
    public MemberNo {
        value = DomainGuard.requireText(value, "会員番号").toUpperCase(Locale.ROOT);

        DomainGuard.requireTrue(value.length() == TOTAL_LENGTH,
                "会員番号は%d桁でなければなりません".formatted(TOTAL_LENGTH));
        DomainGuard.requireTrue(value.startsWith(PREFIX),
                "会員番号は「%s」で始まる必要があります".formatted(PREFIX));
        DomainGuard.requireTrue(isValidDatePart(datePartOf(value)),
                "会員番号の生成日部が不正です");
        DomainGuard.requireTrue(isValidRandomPart(randomPartOf(value)),
                "会員番号の乱数部に使用できない文字が含まれています");
    }

    /**
     * 新しい会員番号を生成する。
     *
     * <p>生成日は呼び出し側から受け取る。内部で {@code now()} を呼ぶと
     * テストで日付部を固定できず、書式の検証ができないため。
     * 乱数部は本質的に不定であり、テストでは書式のみを検証する。</p>
     *
     * @param generatedOn 生成日
     */
    public static MemberNo generate(LocalDate generatedOn) {
        DomainGuard.requireNonNull(generatedOn, "生成日");

        final StringBuilder builder = new StringBuilder(TOTAL_LENGTH)
                .append(PREFIX)
                .append(generatedOn.format(DATE_FORMAT));
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new MemberNo(builder.toString());
    }

    /**
     * 既存の文字列から会員番号を復元する。
     *
     * <p>永続化層からの再構築および外部入力の受け取りに使用する。
     * 生成時と同一の不変条件を適用するため、書式を満たさない値は復元できない。</p>
     *
     * @param value 会員番号の文字列表現
     */
    public static MemberNo of(String value) {
        return new MemberNo(value);
    }

    private static String datePartOf(String value) {
        return value.substring(PREFIX.length(), PREFIX.length() + DATE_LENGTH);
    }

    private static String randomPartOf(String value) {
        return value.substring(PREFIX.length() + DATE_LENGTH);
    }

    /**
     * 生成日部が実在する日付か検証する。
     *
     * <p>桁数の一致だけでは {@code 20261340} のような値を通してしまうため、
     * 実際に解析して暦上の妥当性まで確認する。</p>
     */
    private static boolean isValidDatePart(String datePart) {
        try {
            LocalDate.parse(datePart, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isValidRandomPart(String randomPart) {
        for (int i = 0; i < randomPart.length(); i++) {
            if (ALPHABET.indexOf(randomPart.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 文字列表現を返す。
     *
     * <p>ログ出力や文字列連結で {@code MemberNo[value=...]} が出ることを避け、
     * 番号そのものを返す。</p>
     */
    @Override
    public String toString() {
        return value;
    }
}

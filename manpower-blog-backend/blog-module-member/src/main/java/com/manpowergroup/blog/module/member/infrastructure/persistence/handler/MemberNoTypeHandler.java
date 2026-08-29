package com.manpowergroup.blog.module.member.infrastructure.persistence.handler;

import com.manpowergroup.blog.module.member.domain.model.MemberNo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link MemberNo} と {@code varchar} 列を相互変換する TypeHandler。
 *
 * <p>{@link MemberNo} は record であり、MyBatis の既定では対応する TypeHandler が存在しない。
 * 未登録のまま扱うと {@code UnknownTypeHandler} が選択され、JDBC が型を判別できず
 * insert が実行時に失敗する。</p>
 *
 * <p>読み取り時も {@link MemberNo#of} を通す。DB から復元した値であっても
 * 書式検証を省略しない。省略すると、不正な値が混入した場合に
 * 「生成時のみ保証される不変条件」へ後退し、値オブジェクトの意味が失われるため。</p>
 */
@MappedTypes(MemberNo.class)
public class MemberNoTypeHandler extends BaseTypeHandler<MemberNo> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MemberNo parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.value());
    }

    @Override
    public MemberNo getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toMemberNo(rs.getString(columnName));
    }

    @Override
    public MemberNo getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toMemberNo(rs.getString(columnIndex));
    }

    @Override
    public MemberNo getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toMemberNo(cs.getString(columnIndex));
    }

    private static MemberNo toMemberNo(String value) {
        return value == null ? null : MemberNo.of(value);
    }
}

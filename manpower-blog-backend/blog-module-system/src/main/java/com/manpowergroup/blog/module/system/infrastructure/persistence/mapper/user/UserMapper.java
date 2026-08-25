package com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manpowergroup.blog.module.system.domain.model.user.User;
import com.manpowergroup.blog.module.system.domain.model.user.UserView;
import com.manpowergroup.blog.module.system.domain.model.user.UserSearchCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 検索条件に一致するユーザーを1ページ分取得する。
     *
     * <p>ページングプラグインではなく LIMIT / OFFSET を明示する。
     * プラグインが生成する count は JOIN を保持したままとなり最適化の余地がなく、
     * また件数0件での短絡もできないため、件数取得を分離している。</p>
     */
    List<UserView> selectUserList(
            @Param("criteria") UserSearchCriteria criteria,
            @Param("offset") long offset,
            @Param("limit") long limit
    );

    /** 検索条件に一致する件数を取得する。 */
    long countUsers(@Param("criteria") UserSearchCriteria criteria);

    UserView getUserDetail(
            @Param("userId") Long userId,
            @Param("accountId") Long accountId
    );
}

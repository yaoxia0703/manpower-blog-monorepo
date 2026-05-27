package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginUser;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.PageRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserDetailQueryRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserQueryRequest;
import com.manpowergroup.springboot.springboot3web.system.application.vo.user.UserPageVo;

import java.util.Optional;


public interface UserRepository {

    /**
     * 指定ユーザーIDによりログインユーザー詳細情報を取得する
     */
    LoginUser getCurrentUserContext(Long userId, Long accountId);

    /**
     * ユーザー一覧ページング検索
     */
    JoinPageResult<UserPageVo> selectUserPage(UserQueryRequest query, PageRequest pageRequest);

    /**
     * 指定ユーザーIDによりユーザー詳細情報を取得する
     */
    Optional<UserPageVo> getUserDetail(UserDetailQueryRequest request);
}

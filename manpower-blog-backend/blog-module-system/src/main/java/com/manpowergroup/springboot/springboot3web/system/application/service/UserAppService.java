package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginUser;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.PageRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.*;
import com.manpowergroup.springboot.springboot3web.system.application.vo.user.UserPageVo;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * ユーザーマスタ サービス実装クラス
 * </p>
 *
 * @author YAOXIA
 * @since 2025-12-18
 */
public interface UserAppService extends IService<User> {
    /**
     * ユーザーIDによりログインユーザー詳細情報を取得する
     *
     * @param userId ユーザーID
     * @return ログインユーザー詳細情報
     */
    LoginUser getCurrentUserContext(Long userId, Long accountId);

    /**
     * ユーザー一覧をページングで取得する
     *
     * @param pageRequest ページ情報
     * @param query       検索条件
     * @return ユーザー一覧（ページング）
     */
    JoinPageResult<UserPageVo> pageUsers(PageRequest pageRequest, UserQueryRequest query);

    /**
     * ユーザー追加
     *
     * @param userCreateRequest ユーザー作成リクエスト
     * @return 作成されたユーザーID
     */
    Long createUser(UserCreateRequest userCreateRequest);

    /**
     * ユーザー情報を更新する
     *
     * @param userUpdateRequest ユーザー更新リクエスト
     */
    void updateUser(UserUpdateRequest userUpdateRequest);

    /**
     * ユーザーを削除する
     *
     * @param userDeleteRequest ユーザー削除リクエスト
     */
    void deleteUser(UserDeleteRequest userDeleteRequest);

    void updateUserStatus(UserChangeStatusRequest userChangeStatusRequest);

    UserPageVo getUserDetail(UserDetailQueryRequest query);

}

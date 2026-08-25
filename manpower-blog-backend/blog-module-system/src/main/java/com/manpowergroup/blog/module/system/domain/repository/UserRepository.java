package com.manpowergroup.blog.module.system.domain.repository;

import com.manpowergroup.blog.module.system.domain.model.user.User;
import com.manpowergroup.blog.module.system.domain.model.user.UserView;
import com.manpowergroup.blog.module.system.domain.model.user.UserSearchCriteria;
import com.manpowergroup.blog.shared.dto.PageQuery;

import java.util.List;
import java.util.Optional;

/** ユーザー永続化ポート。 */
public interface UserRepository {

    Optional<User> findById(Long id);

    void create(User user);

    void update(User user);

    void delete(Long id);

    /**
     * 検索条件に一致するユーザーを1ページ分取得する。
     *
     * <p>ページング値は {@link PageQuery} として受け取る。
     * 個別の数値で受けると隣接する同型引数の取り違えを検出できないため、
     * 検証済みの単一の値として扱う。</p>
     */
    List<UserView> list(UserSearchCriteria criteria, PageQuery page);

    /**
     * 検索条件に一致する件数を取得する。
     *
     * <p>一覧取得と分離することで、件数0件の場合に一覧のSQLを発行せずに済む。</p>
     */
    long count(UserSearchCriteria criteria);

    Optional<UserView> findProfile(Long userId, Long accountId);
}

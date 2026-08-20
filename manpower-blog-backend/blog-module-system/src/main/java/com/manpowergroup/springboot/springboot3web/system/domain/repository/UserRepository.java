package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.system.domain.model.user.User;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserProfile;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserSearchCriteria;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserSearchPage;

import java.util.Optional;

/** ユーザー永続化ポート。 */
public interface UserRepository {

    Optional<User> findById(Long id);

    void save(User user);

    void update(User user);

    void deleteById(Long id);

    UserSearchPage search(UserSearchCriteria criteria, Long pageNum, Long pageSize);

    Optional<UserProfile> findProfile(Long userId, Long accountId);
}

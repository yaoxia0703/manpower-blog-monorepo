package com.manpowergroup.blog.module.system.infrastructure.persistence.repository;

import com.manpowergroup.blog.shared.dto.PageQuery;
import com.manpowergroup.blog.module.system.domain.model.user.User;
import com.manpowergroup.blog.module.system.domain.model.user.UserView;
import com.manpowergroup.blog.module.system.domain.model.user.UserSearchCriteria;
import com.manpowergroup.blog.module.system.domain.repository.UserRepository;
import com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.selectById(id));
    }

    @Override
    public void create(User user) {
        userMapper.insert(user);
    }

    @Override
    public void update(User user) {
        userMapper.updateById(user);
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public List<UserView> list(UserSearchCriteria criteria, PageQuery page) {
        return userMapper.selectUserList(criteria, page.offset(), page.limit());
    }

    @Override
    public long count(UserSearchCriteria criteria) {
        return userMapper.countUsers(criteria);
    }

    @Override
    public Optional<UserView> findProfile(Long userId, Long accountId) {
        return Optional.ofNullable(userMapper.getUserDetail(userId, accountId));
    }
}

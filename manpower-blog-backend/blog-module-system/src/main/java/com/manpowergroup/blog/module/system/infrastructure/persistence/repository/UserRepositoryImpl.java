package com.manpowergroup.blog.module.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manpowergroup.blog.shared.dto.PageRequest;
import com.manpowergroup.blog.shared.util.PageUtil;
import com.manpowergroup.blog.module.system.domain.model.user.User;
import com.manpowergroup.blog.module.system.domain.model.user.UserView;
import com.manpowergroup.blog.module.system.domain.model.user.UserSearchCriteria;
import com.manpowergroup.blog.module.system.domain.model.user.UserSearchPage;
import com.manpowergroup.blog.module.system.domain.repository.UserRepository;
import com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final PageUtil pageUtil;

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
    public UserSearchPage page(UserSearchCriteria criteria, Long pageNum, Long pageSize) {
        final Page<UserView> page = pageUtil.toPage(new PageRequest(pageNum, pageSize));
        final IPage<UserView> result = userMapper.selectUserPage(page, criteria);
        return new UserSearchPage(
                result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Optional<UserView> findProfile(Long userId, Long accountId) {
        return Optional.ofNullable(userMapper.getUserDetail(userId, accountId));
    }
}

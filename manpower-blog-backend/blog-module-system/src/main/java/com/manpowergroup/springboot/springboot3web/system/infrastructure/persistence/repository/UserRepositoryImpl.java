package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.PageRequest;
import com.manpowergroup.springboot.springboot3web.blog.common.util.PageUtil;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.User;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserProfile;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserSearchCriteria;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserSearchPage;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.user.UserMapper;
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
        final Page<UserProfile> page = pageUtil.toPage(new PageRequest(pageNum, pageSize));
        final IPage<UserProfile> result = userMapper.selectUserPage(page, criteria);
        return new UserSearchPage(
                result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Optional<UserProfile> findProfile(Long userId, Long accountId) {
        return Optional.ofNullable(userMapper.getUserDetail(userId, accountId));
    }
}

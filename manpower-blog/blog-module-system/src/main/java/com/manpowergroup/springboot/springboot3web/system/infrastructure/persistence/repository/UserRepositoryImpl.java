package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginUser;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.PageRequest;
import com.manpowergroup.springboot.springboot3web.blog.common.util.PageUtil;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserDetailQueryRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserQueryRequest;
import com.manpowergroup.springboot.springboot3web.system.application.vo.user.UserPageVo;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.user.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class UserRepositoryImpl  implements UserRepository {

    private final UserMapper userMapper;
    private final PageUtil pageUtil;

    public UserRepositoryImpl(UserMapper userMapper, PageUtil pageUtil) {
        this.userMapper = userMapper;
        this.pageUtil = pageUtil;
    }

    @Override
    public LoginUser getCurrentUserContext(Long userId, Long accountId) {
        return userMapper.getCurrentUserContext(userId, accountId);
    }

    @Override
    public JoinPageResult<UserPageVo> selectUserPage(UserQueryRequest query, PageRequest pageRequest) {
        Page<UserPageVo> page = pageUtil.toPage(pageRequest);
        IPage<UserPageVo> result = userMapper.selectUserPage(page, query);
        return JoinPageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Optional<UserPageVo> getUserDetail(UserDetailQueryRequest request) {
        return Optional.ofNullable(userMapper.getUserDetail(request));
    }


}

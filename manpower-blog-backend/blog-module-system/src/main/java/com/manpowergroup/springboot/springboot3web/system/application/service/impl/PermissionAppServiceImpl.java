package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.PageRequest;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.blog.common.util.PageUtil;
import com.manpowergroup.springboot.springboot3web.blog.common.util.StringUtils;
import com.manpowergroup.springboot.springboot3web.blog.common.util.TreeUtils;
import com.manpowergroup.springboot.springboot3web.framework.security.authority.ApiPermission;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.PermissionAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionQueryRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.service.PermissionAppService;
import com.manpowergroup.springboot.springboot3web.system.application.vo.permission.PermissionDetailVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.permission.PermissionOptionVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.permission.PermissionTreeVo;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.Permission;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.PermissionRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.permission.PermissionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 権限マスタ（MENU/BUTTON/API）サービス実装
 *
 * @author YAOXIA
 * @since 2025-12-18
 */
@Service
@AllArgsConstructor
@Slf4j
public class PermissionAppServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionAppService {

    private final PageUtil pageUtil;
    private final PermissionRepository permissionRepository;

    @Override
    public List<String> selectPermissionCodesByUserId(Long userId) {
        return permissionRepository.selectPermissionCodesByUserId(userId);
    }

    @Override
    public List<ApiPermission> selectApiPermissionsByUserId(Long userId) {
        return permissionRepository.selectApiPermissionsByUserId(userId);
    }

    @Override
    public JoinPageResult<Permission> pagePermission(PermissionQueryRequest queryRequest, PageRequest pageRequest) {
        pageRequest = pageRequest != null ? pageRequest : new PageRequest();
        final Page<Permission> page = pageUtil.toPage(pageRequest);

        final var qw = new LambdaQueryWrapper<Permission>()
                .orderByAsc(Permission::getSort)
                .orderByDesc(Permission::getId);

        if (queryRequest != null) {
            final var keyword = StringUtils.normalize(queryRequest.keyword());
            qw.and(keyword != null, w ->
                    w.like(Permission::getCode, keyword)
                            .or()
                            .like(Permission::getName, keyword)
            ).eq(queryRequest.status() != null, Permission::getStatus, queryRequest.status());
        }

        final var result = baseMapper.selectPage(page, qw);
        return JoinPageResult.of(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
    }

    @Override
    public List<PermissionTreeVo> getPermissionTree() {
        final var voList = baseMapper.selectList(
                        new LambdaQueryWrapper<Permission>()
                                .orderByAsc(Permission::getSort)
                                .orderByAsc(Permission::getId)
                ).stream()
                .map(PermissionAssembler::toTreeVo)
                .toList();

        return TreeUtils.buildTree(voList, 0L);
    }

    @Override
    public List<PermissionOptionVo> getPermissionOptions() {
        return baseMapper.selectList(
                        new LambdaQueryWrapper<Permission>()
                                .eq(Permission::getStatus, Status.ENABLED)
                                .orderByAsc(Permission::getSort)
                                .orderByAsc(Permission::getId)
                ).stream()
                .map(PermissionAssembler::toOptionVo)
                .toList();
    }

    @Override
    public PermissionDetailVo getPermissionDetail(Long id) {
        final var permission = baseMapper.selectById(id);
        if (permission == null) {
            throw BizException.withDetail(ErrorCode.NOT_FOUND, "権限が存在しません。id=" + id);
        }
        return PermissionAssembler.toDetailVo(permission);
    }

    @Override
    @Transactional
    public Long createPermission(PermissionCreateRequest request) {
        log.info("[PermissionAppService#createPermission] start: request={}", request);

        final var entity = PermissionAssembler.toCreateEntity(request);
        // 1. コード重複チェック
        checkDuplicateCode(entity);
        // 2. ドメインバリデーション
        entity.validate();
        baseMapper.insert(entity);

        log.info("[PermissionAppService#createPermission] success: id={}", entity.getId());
        return entity.getId();
    }

    @Override
    @Transactional
    public void updatePermission(Long id, PermissionUpdateRequest request) {
        log.info("[PermissionAppService#updatePermission] start: id={}, request={}", id, request);

        final var existing = ensurePermissionExists(id);

        // 更新前のステータスを退避
        final var oldStatus = existing.getStatus();
        PermissionAssembler.toUpdateEntity(request, existing);

        // 1. コード重複チェック
        checkDuplicateCode(existing);
        // 2. ドメインバリデーション
        existing.validate();
        baseMapper.updateById(existing);

        // ステータスが「無効」に変更された場合のみ → 全子孫を連動して無効化
        if (request.status() == Status.DISABLED && request.status() != oldStatus) {
            final var descendantIds = permissionRepository.selectAllDescendantIds(id);
            if (!descendantIds.isEmpty()) {
                permissionRepository.updateStatusBatch(descendantIds, Status.DISABLED);
                log.info("[PermissionAppService#updatePermission] cascade disable: count={}, ids={}",
                        descendantIds.size(), descendantIds);
            }
        }

        log.info("[PermissionAppService#updatePermission] success: id={}", id);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        final var existing = ensurePermissionExists(id);
        baseMapper.deleteById(id);
        log.info("[PermissionAppService#deletePermission] success: id={}, code={}, name={}",
                existing.getId(), existing.getCode(), existing.getName());
    }

    @Override
    @Transactional
    public void changeStatus(Long id, Status status) {
        log.info("[PermissionAppService#changeStatus] start: id={}, status={}", id, status);

        final var existing = ensurePermissionExists(id);

        // ステータスが変更なしの場合はスキップ
        final var oldStatus = existing.getStatus();
        if (oldStatus == status) {
            log.info("[PermissionAppService#changeStatus] no change, skip: id={}, status={}", id, oldStatus);
            return;
        }

        // 自身のステータスを更新
        existing.setStatus(status);
        baseMapper.updateById(existing);

        // 無効化の場合のみ → 全子孫を連動して無効化
        if (status == Status.DISABLED) {
            final var descendantIds = permissionRepository.selectAllDescendantIds(id);
            if (!descendantIds.isEmpty()) {
                permissionRepository.updateStatusBatch(descendantIds, Status.DISABLED);
                log.info("[PermissionAppService#changeStatus] cascade disable: count={}, ids={}",
                        descendantIds.size(), descendantIds);
            }
        }

        log.info("[PermissionAppService#changeStatus] success: id={}, {} → {}", id, oldStatus, status);
    }

    /**
     * 権限制御コードの重複チェック
     */
    private void checkDuplicateCode(Permission entity) {
        final boolean exists = lambdaQuery()
                .eq(Permission::getCode, entity.getCode())
                .ne(entity.getId() != null, Permission::getId, entity.getId())
                .exists();

        if (exists) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "権限制御コードは既に存在しています");
        }
    }

    /**
     * 権限存在チェック（存在しない場合は例外をスロー）
     */
    private Permission ensurePermissionExists(Long id) {
        final var permission = baseMapper.selectById(id);
        if (permission == null) {
            log.warn("[PermissionAppService#ensurePermissionExists] not found: id={}", id);
            throw BizException.withDetail(ErrorCode.NOT_FOUND, "権限が存在しません。id=" + id);
        }
        return permission;
    }
}

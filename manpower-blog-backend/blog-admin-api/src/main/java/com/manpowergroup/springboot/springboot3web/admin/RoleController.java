package com.manpowergroup.springboot.springboot3web.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.Result;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.role.RoleAssignMenuRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.role.RoleAssignPermissionRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.role.RoleSaveOrUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.role.RoleStatusUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.service.RoleAppMenuService;
import com.manpowergroup.springboot.springboot3web.system.application.service.RolePermissionAppService;
import com.manpowergroup.springboot.springboot3web.system.domain.model.role.Role;
import com.manpowergroup.springboot.springboot3web.system.application.service.RoleAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * ロール関連のAPIコントローラー
 * </p>
 *
 * @author YAOXIA
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/api/system/role")
@AllArgsConstructor
public class RoleController {

    private final RoleAppService roleService;
    private final RolePermissionAppService rolePermissionAppService;
    private final RoleAppMenuService roleMenuAppService;


    /**
     * ロールのリストを取得するAPI（ページングなし）
     *
     * @return ロールのリストを含むレスポンス
     */
    @GetMapping("/list")
    public Result<List<Role>> listRoles() {
        List<Role> list = roleService.list(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getIsDeleted, 0)
                        .orderByAsc(Role::getSort)
                        .orderByDesc(Role::getUpdatedAt)
        );
        return Result.ok(list);
    }


    /**
     * 指定したIDのロールの詳細情報を取得するAPI
     *
     * @param id ロールID
     * @return ロールの詳細情報を含むレスポンス
     */
    @GetMapping("/{id}")
    public Result<Role> detail(@PathVariable @NotNull(message = "ロールIDは必須です") Long id) {
        return Result.ok(roleService.getRoleById(id));
    }

    /**
     * 新しいロールを作成するAPI
     *
     * @param request ロールの作成に必要な情報を含むリクエストオブジェクト
     * @return 作成されたロールのIDを含むレスポンス
     */
    @PostMapping
    public Result<Long> create(@RequestBody @Valid RoleSaveOrUpdateRequest request) {
        return Result.ok(roleService.createRole(request));
    }

    /**
     * 指定したIDのロールを更新するAPI
     *
     * @param id      ロールID
     * @param request ロールの更新に必要な情報を含むリクエストオブジェクト
     * @return 更新成功を示すレスポンス（データはnull）
     */
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable @NotNull(message = "ロールIDは必須です") Long id,
            @RequestBody @Valid RoleSaveOrUpdateRequest request
    ) {
        roleService.updateRole(id, request);
        return Result.ok();
    }


    /**
     * 指定したIDのロールを削除するAPI
     *
     * @param id ロールID
     * @return 削除成功を示すレスポンス（データはnull）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull(message = "ロールIDは必須です") Long id) {
        roleService.deleteRole(id);
        return Result.ok();
    }

    /**
     * 指定したIDのロールのステータスを変更するAPI
     *
     * @param id      ロールID
     * @param request ロールのステータス変更に必要な情報を含むリクエストオブジェクト
     * @return ステータス変更成功を示すレスポンス（データはnull）
     */
    @PatchMapping("/{id}/status")
    public Result<Void> changeStatus(
            @PathVariable @NotNull(message = "ロールIDは必須です") Long id,
            @RequestBody @Valid RoleStatusUpdateRequest request
    ) {
        roleService.changeStatus(id, request.status());
        return Result.ok();
    }


    /**
     * ロールに紐づく権限IDリストを取得するAPI
     *
     * @param id ロールID
     * @return 権限IDリスト
     */
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissions(
            @PathVariable @NotNull(message = "ロールIDは必須です") Long id
    ) {
        return Result.ok(rolePermissionAppService.getPermissionIdsByRoleId(id));
    }

    /**
     * ロールに権限を割り当てるAPI
     *
     * @param id      ロールID
     * @param request 権限ID配列
     * @return 保存成功を示すレスポンス
     */
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(
            @PathVariable @NotNull(message = "ロールIDは必須です") Long id,
            @RequestBody @Valid RoleAssignPermissionRequest request
    ) {
        rolePermissionAppService.saveOrUpdate(id, request.permissionIds());
        return Result.ok();
    }
    @GetMapping("/{id}/menus")
    public Result<List<Long>> getMenus(
            @PathVariable @NotNull(message = "ロールIDは必須です") Long id
    ) {
        return Result.ok(roleMenuAppService.getMenuIdsByRoleId(id));
    }
    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(
            @PathVariable @NotNull(message = "ロールIDは必須です") Long id,
            @RequestBody @Valid RoleAssignMenuRequest request
    ) {
        roleMenuAppService.saveOrUpdate(id, request.menuIds());
        return Result.ok();
    }
}

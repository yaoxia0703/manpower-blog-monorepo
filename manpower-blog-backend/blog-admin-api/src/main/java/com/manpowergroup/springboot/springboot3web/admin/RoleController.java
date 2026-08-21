package com.manpowergroup.springboot.springboot3web.admin;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.Result;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.RoleAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.role.RoleAuthorizationSaveRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.role.RoleCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.role.RoleUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.role.RoleStatusUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.role.RoleAuthorizationResponse;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.role.RoleResponse;
import com.manpowergroup.springboot.springboot3web.system.application.service.RoleAuthorizationAppService;
import com.manpowergroup.springboot.springboot3web.system.application.service.RoleAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RoleController {

    private final RoleAppService roleService;
    private final RoleAuthorizationAppService roleAuthorizationAppService;


    /**
     * ロールのリストを取得するAPI（ページングなし）
     *
     * @return ロールのリストを含むレスポンス
     */
    @GetMapping("/list")
    public Result<List<RoleResponse>> list() {
        return Result.ok(roleService.list());
    }


    /**
     * 指定したIDのロールの詳細情報を取得するAPI
     *
     * @param id ロールID
     * @return ロールの詳細情報を含むレスポンス
     */
    @GetMapping("/{id}")
    public Result<RoleResponse> findById(@PathVariable @NotNull(message = "ロールIDは必須です") Long id) {
        return Result.ok(roleService.findById(id));
    }

    /**
     * 新しいロールを作成するAPI
     *
     * @param request ロールの作成に必要な情報を含むリクエストオブジェクト
     * @return 作成されたロールのIDを含むレスポンス
     */
    @PostMapping
    public Result<Long> create(@RequestBody @Valid RoleCreateRequest request) {
        return Result.ok(roleService.create(RoleAssembler.toCommand(request)));
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
            @RequestBody @Valid RoleUpdateRequest request
    ) {
        roleService.update(RoleAssembler.toCommand(id, request));
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
        roleService.delete(id);
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
        roleService.changeStatus(RoleAssembler.toCommand(id, request));
        return Result.ok();
    }


    @GetMapping("/{id}/authorization")
    public Result<RoleAuthorizationResponse> getAuthorization(
            @PathVariable @NotNull(message = "ロールIDは必須です") Long id
    ) {
        return Result.ok(roleAuthorizationAppService.getAuthorization(id));
    }

    @PutMapping("/{id}/authorization")
    public Result<Void> saveAuthorization(
            @PathVariable @NotNull(message = "ロールIDは必須です") Long id,
            @RequestBody @Valid RoleAuthorizationSaveRequest request
    ) {
        roleAuthorizationAppService.saveAuthorization(RoleAssembler.toCommand(id, request));
        return Result.ok();
    }
}

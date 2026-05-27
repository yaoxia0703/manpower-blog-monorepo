package com.manpowergroup.springboot.springboot3web.admin;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.Result;
import com.manpowergroup.springboot.springboot3web.framework.security.SecurityUtils;
import com.manpowergroup.springboot.springboot3web.framework.security.jwt.LoginPrincipal;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuStatusUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.service.MenuAppService;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuDetailVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuOptionVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuTreeVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * メニュー関連のAPIコントローラー
 * </p>
 *
 * @author YAOXIA
 * @since 2026-03-01
 */
@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/system/menu")
public class MenuController {

    private final MenuAppService menuAppService;

    /**
     * 管理用：全メニューツリー取得（管理画面のメニュー管理ページ用）
     */
    @PreAuthorize("hasAuthority('sys:menu:list')")
    @GetMapping("/tree")
    public Result<List<MenuTreeVo>> getAllMenuTree() {
        log.info("[MenuController#getAllMenuTree] request received");
        return Result.ok(menuAppService.getAllMenuTree());
    }

    /**
     * ログインユーザー用：自分の権限に応じたメニューツリー取得
     */
    @PreAuthorize("hasAuthority('sys:menu:list')")
    @GetMapping("/my-tree")
    public Result<List<MenuTreeVo>> getMyMenuTree() {
        log.info("[MenuController#getMyMenuTree] request received");
        final LoginPrincipal principal = SecurityUtils.getLoginPrincipal();
        return Result.ok(menuAppService.selectMenusByUserId(principal.userId()));
    }


    @PreAuthorize("hasAnyAuthority('sys:menu:list', 'sys:role:assignMenu')")
    @GetMapping("/active-tree")
    public Result<List<MenuTreeVo>> getActiveMenuTree() {
        return Result.ok(menuAppService.getActiveMenuTree());
    }

    @PreAuthorize("hasAnyAuthority( 'sys:menu:create', 'sys:menu:update')")
    @GetMapping("/parent-options")
    public Result<List<MenuOptionVo>> getMenuOptions() {
        return Result.ok(menuAppService.getMenuOptions());
    }
    /**
     * 指定したIDのメニューの詳細情報を取得するAPI
     *
     * @param id メニューID
     * @return メニューの詳細情報を含むレスポンス
     */
    @PreAuthorize("hasAuthority('sys:menu:detail')")
    @GetMapping("/{id}")
    public Result<MenuDetailVo> detail(@PathVariable @NotNull(message = "メニューIDは必須です") Long id) {
        log.info("[MenuController#detail] request received: id={}", id);
        return Result.ok(menuAppService.getMenuDetail(id));
    }



    /**
     * 新しいメニューを作成するAPI
     *
     * @param request メニュー作成のリクエストデータ
     * @return 作成されたメニューのIDを含むレスポンス
     */
    @PreAuthorize("hasAuthority('sys:menu:create')")
    @PostMapping
    public Result<Long> create(@RequestBody @Valid MenuCreateRequest request) {
        log.info("[MenuController#create] request received: request={}", request);
        return Result.ok(menuAppService.createMenu(request));
    }


    /**
     * 指定したIDのメニューを更新するAPI
     *
     * @param id      メニューID
     * @param request メニュー更新のリクエストデータ
     * @return 更新成功のレスポンス
     */
    @PreAuthorize("hasAuthority('sys:menu:update')")
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable @NotNull(message = "メニューIDは必須です") Long id,
            @RequestBody @Valid MenuUpdateRequest request
    ) {
        log.info("[MenuController#update] request received: id={}, request={}", id, request);
        menuAppService.updateMenu(id, request);
        return Result.ok();
    }

    /**
     * 指定したIDのメニューを削除するAPI
     *
     * @param id メニューID
     * @return 削除成功のレスポンス
     */
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull(message = "メニューIDは必須です") Long id) {
        log.info("[MenuController#delete] request received: id={}", id);
        menuAppService.deleteMenu(id);
        return Result.ok();
    }

    /**
     * 指定したIDのメニューのステータスを変更するAPI
     *
     * @param id      メニューID
     * @param request メニューステータス更新のリクエストデータ
     * @return ステータス変更成功のレスポンス
     */
    @PreAuthorize("hasAuthority('sys:menu:changeStatus')")
    @PatchMapping("/{id}/status")
    public Result<Void> changeStatus(
            @PathVariable @NotNull(message = "メニューIDは必須です") Long id,
            @RequestBody @Valid MenuStatusUpdateRequest request
    ) {
        log.info("[MenuController#changeStatus] request received: id={}, status={}", id, request.status());
        menuAppService.changeMenuStatus(id, request);
        return Result.ok();
    }
}

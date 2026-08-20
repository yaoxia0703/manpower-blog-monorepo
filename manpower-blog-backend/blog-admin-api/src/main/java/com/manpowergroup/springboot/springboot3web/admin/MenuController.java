package com.manpowergroup.springboot.springboot3web.admin;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.Result;
import com.manpowergroup.springboot.springboot3web.framework.security.SecurityUtils;
import com.manpowergroup.springboot.springboot3web.framework.security.jwt.LoginPrincipal;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.MenuAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuStatusUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.service.MenuAppService;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuDetailResponse;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuOptionResponse;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuTreeResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@RequestMapping("/api/system/menu")
public class MenuController {

    private final MenuAppService menuAppService;

    /**
     * 管理用：全メニューツリー取得（管理画面のメニュー管理ページ用）
     */
    @GetMapping("/tree")
    public Result<List<MenuTreeResponse>> getAllMenuTree() {
        log.info("[MenuController#getAllMenuTree] request received");
        return Result.ok(menuAppService.getAllMenuTree());
    }

    /**
     * ログインユーザー用：自分の権限に応じたメニューツリー取得
     */
    @GetMapping("/my-tree")
    public Result<List<MenuTreeResponse>> getMyMenuTree() {
        log.info("[MenuController#getMyMenuTree] request received");
        final LoginPrincipal principal = SecurityUtils.getLoginPrincipal();
        return Result.ok(menuAppService.selectMenusByUserId(principal.userId()));
    }
    @GetMapping("/active-tree")
    public Result<List<MenuTreeResponse>> getActiveMenuTree() {
        return Result.ok(menuAppService.getActiveMenuTree());
    }
    @GetMapping("/parent-options")
    public Result<List<MenuOptionResponse>> getMenuOptions() {
        return Result.ok(menuAppService.getMenuOptions());
    }
    /**
     * 指定したIDのメニューの詳細情報を取得するAPI
     *
     * @param id メニューID
     * @return メニューの詳細情報を含むレスポンス
     */
    @GetMapping("/{id}")
    public Result<MenuDetailResponse> detail(@PathVariable @NotNull(message = "メニューIDは必須です") Long id) {
        log.info("[MenuController#detail] request received: id={}", id);
        return Result.ok(menuAppService.getMenuDetail(id));
    }



    /**
     * 新しいメニューを作成するAPI
     *
     * @param request メニュー作成のリクエストデータ
     * @return 作成されたメニューのIDを含むレスポンス
     */
    @PostMapping
    public Result<Long> create(@RequestBody @Valid MenuCreateRequest request) {
        log.info("[MenuController#create] request received: request={}", request);
        return Result.ok(menuAppService.createMenu(MenuAssembler.toCommand(request)));
    }


    /**
     * 指定したIDのメニューを更新するAPI
     *
     * @param id      メニューID
     * @param request メニュー更新のリクエストデータ
     * @return 更新成功のレスポンス
     */
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable @NotNull(message = "メニューIDは必須です") Long id,
            @RequestBody @Valid MenuUpdateRequest request
    ) {
        log.info("[MenuController#update] request received: id={}, request={}", id, request);
        menuAppService.updateMenu(MenuAssembler.toCommand(id, request));
        return Result.ok();
    }

    /**
     * 指定したIDのメニューを削除するAPI
     *
     * @param id メニューID
     * @return 削除成功のレスポンス
     */
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
    @PatchMapping("/{id}/status")
    public Result<Void> changeStatus(
            @PathVariable @NotNull(message = "メニューIDは必須です") Long id,
            @RequestBody @Valid MenuStatusUpdateRequest request
    ) {
        log.info("[MenuController#changeStatus] request received: id={}, status={}", id, request.status());
        menuAppService.changeMenuStatus(MenuAssembler.toCommand(id, request));
        return Result.ok();
    }
}

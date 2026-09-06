package com.manpowergroup.blog.api.admin;

import com.manpowergroup.blog.shared.api.Result;
import com.manpowergroup.blog.framework.security.SecurityUtils;
import com.manpowergroup.blog.framework.security.jwt.LoginPrincipal;
import com.manpowergroup.blog.module.system.application.assembler.MenuAssembler;
import com.manpowergroup.blog.module.system.application.dto.request.menu.MenuCreateRequest;
import com.manpowergroup.blog.module.system.application.dto.request.menu.MenuStatusUpdateRequest;
import com.manpowergroup.blog.module.system.application.dto.request.menu.MenuUpdateRequest;
import com.manpowergroup.blog.module.system.application.service.MenuAppService;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuDetailResponse;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuOptionResponse;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuTreeResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system/menu")
public class MenuController {

    private final MenuAppService menuAppService;

    /**
     * 管理用：全メニューツリー取得（管理画面のメニュー管理ページ用）
     */
    @GetMapping("/tree")
    public Result<List<MenuTreeResponse>> listTree() {
        log.info("[MenuController#listTree] リクエストを受信しました");
        return Result.ok(menuAppService.listTree());
    }

    /**
     * ログインユーザー用：自分の権限に応じたメニューツリー取得
     */
    @GetMapping("/my-tree")
    public Result<List<MenuTreeResponse>> listMyTree() {
        log.info("[MenuController#listMyTree] リクエストを受信しました");
        final LoginPrincipal principal = SecurityUtils.getLoginPrincipal();
        return Result.ok(menuAppService.listTreeByUserId(principal.userId()));
    }
    @GetMapping("/tree/enabled")
    public Result<List<MenuTreeResponse>> listEnabledTree() {
        return Result.ok(menuAppService.listEnabledTree());
    }
    @GetMapping("/options")
    public Result<List<MenuOptionResponse>> listOptions() {
        return Result.ok(menuAppService.listOptions());
    }
    /**
     * 指定したIDのメニューの詳細情報を取得するAPI
     *
     * @param id メニューID
     * @return メニューの詳細情報を含むレスポンス
     */
    @GetMapping("/{id}")
    public Result<MenuDetailResponse> findById(@PathVariable @Positive(message = "メニューIDが不正です") Long id) {
        log.info("[MenuController#findById] リクエストを受信しました: id={}", id);
        return Result.ok(menuAppService.findById(id));
    }



    /**
     * 新しいメニューを作成するAPI
     *
     * @param request メニュー作成のリクエストデータ
     * @return 作成されたメニューのIDを含むレスポンス
     */
    @PostMapping
    public Result<Long> create(@RequestBody @Valid MenuCreateRequest request) {
        log.info("[MenuController#create] リクエストを受信しました: request={}", request);
        return Result.ok(menuAppService.create(MenuAssembler.toCommand(request)));
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
            @PathVariable @Positive(message = "メニューIDが不正です") Long id,
            @RequestBody @Valid MenuUpdateRequest request
    ) {
        log.info("[MenuController#update] リクエストを受信しました: id={}, request={}", id, request);
        menuAppService.update(MenuAssembler.toCommand(id, request));
        return Result.ok();
    }

    /**
     * 指定したIDのメニューを削除するAPI
     *
     * @param id メニューID
     * @return 削除成功のレスポンス
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @Positive(message = "メニューIDが不正です") Long id) {
        log.info("[MenuController#delete] リクエストを受信しました: id={}", id);
        menuAppService.delete(id);
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
            @PathVariable @Positive(message = "メニューIDが不正です") Long id,
            @RequestBody @Valid MenuStatusUpdateRequest request
    ) {
        log.info("[MenuController#changeStatus] リクエストを受信しました: id={}, status={}", id, request.status());
        menuAppService.changeStatus(MenuAssembler.toCommand(id, request));
        return Result.ok();
    }
}

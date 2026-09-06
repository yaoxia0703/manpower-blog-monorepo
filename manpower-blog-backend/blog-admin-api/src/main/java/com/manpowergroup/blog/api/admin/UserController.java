package com.manpowergroup.blog.api.admin;

import com.manpowergroup.blog.shared.api.PageResult;
import com.manpowergroup.blog.shared.dto.PageRequest;
import com.manpowergroup.blog.shared.api.Result;
import com.manpowergroup.blog.module.system.application.assembler.UserAssembler;
import com.manpowergroup.blog.module.system.application.dto.request.user.UserChangeStatusRequest;
import com.manpowergroup.blog.module.system.application.dto.request.user.UserCreateRequest;
import com.manpowergroup.blog.module.system.application.dto.request.user.UserQueryRequest;
import com.manpowergroup.blog.module.system.application.dto.request.user.UserUpdateRequest;
import com.manpowergroup.blog.module.system.application.dto.response.user.UserResponse;
import com.manpowergroup.blog.module.system.application.service.UserAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * ユーザー関連のAPIコントローラー
 * </p>
 *
 * @author YAOXIA
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/api/system/user")
@Slf4j
public class UserController {

    private final UserAppService userService;

    public UserController(UserAppService userService) {
        this.userService = userService;
    }


    /**
     * ユーザーのページリストを取得するAPI
     *
     * @param pageRequest ページリクエスト（ページ番号、ページサイズなど）
     * @param query       ユーザー検索クエリ（キーワード、状態など）
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult<UserResponse>> page(PageRequest pageRequest, UserQueryRequest query) {
        return Result.ok(userService.page(UserAssembler.toQuery(pageRequest, query)));
    }


    /**
     * 指定したIDのユーザーの詳細情報を取得するAPI
     *
     * @param id        ユーザーID
     * @param accountId アカウントID
     * @return ユーザーの詳細情報を含むレスポンス
     */
    @GetMapping("/{id}")
    public Result<UserResponse> findById(
            @PathVariable @Positive(message = "ユーザーIDが不正です") Long id,
            @RequestParam("accountId") @Positive(message = "アカウントIDが不正です") Long accountId) {
        log.info("[UserController#findById] userId={}, accountId={}", id, accountId);
        return Result.ok(userService.findById(UserAssembler.toDetailQuery(id, accountId)));
    }

    @PostMapping
    public Result<Long> create(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        // ユーザーの作成処理を実装
        log.info("[UserController#create] リクエストを受信しました: accountValue={}", userCreateRequest.accountValue());
        return Result.ok(userService.create(UserAssembler.toCommand(userCreateRequest)));
    }

    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable @Positive(message = "ユーザーIDが不正です") Long id,
            @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        log.info("[UserController#update] userId={}", id);
        userService.update(UserAssembler.toCommand(id, userUpdateRequest));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable @Positive(message = "ユーザーIDが不正です") Long id,
            @RequestParam("accountId") @Positive(message = "アカウントIDが不正です") Long accountId) {
        // ユーザーの削除処理を実装
        log.info("[UserController#delete] リクエストを受信しました: userId={}", id);
        userService.delete(UserAssembler.toDeleteCommand(id, accountId));
        return Result.ok();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> changeStatus(
            @PathVariable @Positive(message = "ユーザーIDが不正です") Long id,
            @RequestBody @Valid UserChangeStatusRequest userChangeStatusRequest) {
        log.info("[UserController#changeStatus] リクエストを受信しました: userId={}, status={}", id, userChangeStatusRequest.status());
        userService.changeStatus(UserAssembler.toCommand(id, userChangeStatusRequest));
        return Result.ok();
    }
}

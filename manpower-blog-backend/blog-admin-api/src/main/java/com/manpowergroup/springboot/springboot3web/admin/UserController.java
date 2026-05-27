package com.manpowergroup.springboot.springboot3web.admin;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.PageRequest;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.Result;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.*;
import com.manpowergroup.springboot.springboot3web.system.application.vo.user.UserPageVo;
import com.manpowergroup.springboot.springboot3web.system.application.service.UserAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public Result<JoinPageResult<UserPageVo>> page(PageRequest pageRequest, UserQueryRequest query) {
        return Result.ok(userService.pageUsers(pageRequest, query));
    }


    /**
     * 指定したIDのユーザーの詳細情報を取得するAPI
     *
     * @param userId ユーザーID
     * @return ユーザーの詳細情報を含むレスポンス
     */
    @GetMapping("/detail")
    public Result<UserPageVo> detail(
            @RequestParam("userId") @NotNull(message = "ユーザーIDは必須です") Long userId,
            @RequestParam("accountId") Long accountId) {
        // ユーザーの詳細情報を取得する処理を実装
        log.info("[UserController#detail] userId={}, accountId={}", userId, accountId);
        return Result.ok(userService.getUserDetail(new UserDetailQueryRequest(userId, accountId)));
    }
    @PostMapping
    public Result<Long> create(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        // ユーザーの作成処理を実装
        log.info("[UserController#create] request received accountValue={}  ", userCreateRequest.accountValue());
        return Result.ok(userService.createUser(userCreateRequest));
    }
    @PutMapping()
    public Result<Void> update(
            @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        // ユーザーの更新処理を実装
        log.info("[UserController#update] userId={}", userUpdateRequest.userId());
        userService.updateUser(userUpdateRequest);
        return Result.ok();
    }
    @DeleteMapping
    public Result<Void> delete(@RequestParam("userId") @NotNull(message ="ユーザーIDは必須です。") Long userId,
                               @RequestParam("accountId") Long accountId) {
        // ユーザーの削除処理を実装
        log.info("[UserController#delete] request received: userId={}", userId);
        userService.deleteUser(new UserDeleteRequest(userId, accountId));
        return Result.ok();
    }
    @PatchMapping("/status")
    public Result<Void> changeStatus(@RequestBody @Valid UserChangeStatusRequest userChangeStatusRequest) {
        // ユーザーステータスの変更処理を実装
        log.info("[UserController#changeStatus] request received: userId={}, status={}", userChangeStatusRequest.userId(), userChangeStatusRequest.status());
        userService.updateUserStatus(userChangeStatusRequest);
        return Result.ok();
    }
}

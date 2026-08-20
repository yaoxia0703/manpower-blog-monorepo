package com.manpowergroup.springboot.springboot3web.admin;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.Result;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.PermissionAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.service.PermissionAppService;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.permission.PermissionResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/permission")
@Slf4j
public class PermissionController {

    private final PermissionAppService permissionAppService;

    public PermissionController(PermissionAppService permissionAppService) {
        this.permissionAppService = permissionAppService;
    }
    @GetMapping("/list")
    public Result<List<PermissionResponse>> list() {
        return Result.ok(permissionAppService.getPermissionList());
    }
    @PostMapping
    public Result<Long> create(@RequestBody @Valid PermissionCreateRequest permissionCreateRequest) {
        log.info("[PermissionController#create] request received name={}  ", permissionCreateRequest.name());
        return Result.ok(permissionAppService.createPermission(PermissionAssembler.toCommand(permissionCreateRequest)));
    }
    @GetMapping("/{id}")
    public Result<PermissionResponse> detail(@PathVariable @NotNull(message = "権限IDは必須です") Long id) {
        return Result.ok(permissionAppService.getPermissionDetail(id));
    }
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable @NotNull(message = "権限IDは必須です") Long id, @RequestBody @Valid PermissionUpdateRequest permissionUpdateRequest) {
        log.info("[PermissionController#update] request received id={}  ", id);
        permissionAppService.updatePermission(PermissionAssembler.toCommand(id, permissionUpdateRequest));
        return Result.ok();
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull(message = "権限IDは必須です") Long id) {
        log.info("[PermissionController#delete] request received id={}  ", id);
        permissionAppService.deletePermission(id);
        return Result.ok();
    }
}

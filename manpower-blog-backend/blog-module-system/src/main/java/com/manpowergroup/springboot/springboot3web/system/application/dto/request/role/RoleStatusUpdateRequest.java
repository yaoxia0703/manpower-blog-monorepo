package com.manpowergroup.springboot.springboot3web.system.application.dto.request.role;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import jakarta.validation.constraints.NotNull;

public record RoleStatusUpdateRequest(
        @NotNull(message = "状態は必須です")
        Status status
) {}
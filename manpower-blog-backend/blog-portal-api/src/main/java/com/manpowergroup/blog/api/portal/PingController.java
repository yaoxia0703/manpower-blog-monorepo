package com.manpowergroup.blog.api.portal;

import com.manpowergroup.blog.shared.dto.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    @GetMapping("/api/portal/ping")
    public Result<String> ping() {
        return Result.ok("pong");
    }
}

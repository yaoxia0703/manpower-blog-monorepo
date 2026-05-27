package com.manpowergroup.springboot.springboot3web.framework.security;

import com.manpowergroup.springboot.springboot3web.framework.security.authority.ApiPermission;
import com.manpowergroup.springboot.springboot3web.framework.security.authority.UserAuthorityProvider;
import com.manpowergroup.springboot.springboot3web.framework.security.jwt.LoginPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public class PermissionAuthorizationFilter extends OncePerRequestFilter {

    private final UserAuthorityProvider userAuthorityProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public PermissionAuthorizationFilter(UserAuthorityProvider userAuthorityProvider) {
        this.userAuthorityProvider = userAuthorityProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        final String path = requestPath(request);
        return path.startsWith("/api/system/auth/")
                || path.startsWith("/error/")
                || path.equals("/favicon.ico")
                || (!path.startsWith("/api/system/") && !path.startsWith("/api/admin/"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginPrincipal principal)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String requestMethod = normalize(request.getMethod());
        final String requestPath = requestPath(request);

        final List<ApiPermission> permissions = userAuthorityProvider.loadApiPermissions(principal.userId());
        final boolean allowed = permissions.stream()
                .filter(Objects::nonNull)
                .anyMatch(permission -> matches(permission, requestMethod, requestPath));

        if (!allowed) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"permission denied\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean matches(ApiPermission permission, String requestMethod, String requestPath) {
        final String permissionMethod = normalize(permission.getMethod());
        final String permissionPath = normalizePath(permission.getPath());
        if (permissionMethod == null || permissionPath == null) {
            return false;
        }
        if (!permissionMethod.equals(requestMethod)) {
            return false;
        }
        if (pathMatcher.match(permissionPath, requestPath)) {
            return true;
        }
        final String variableStrippedPath = stripPathVariables(permissionPath);
        if (!variableStrippedPath.equals(permissionPath) && pathMatcher.match(variableStrippedPath, requestPath)) {
            return true;
        }
        return !containsPatternToken(permissionPath) && requestPath.startsWith(permissionPath + "/");
    }

    private boolean containsPatternToken(String path) {
        return path.contains("*") || path.contains("?") || path.contains("{");
    }

    private String stripPathVariables(String path) {
        return path.replaceAll("/\\{[^/]+}", "");
    }

    private String requestPath(HttpServletRequest request) {
        final String contextPath = request.getContextPath();
        final String uri = request.getRequestURI();
        if (contextPath != null && !contextPath.isBlank() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizePath(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        final String path = value.trim();
        return path.startsWith("/") ? path : "/" + path;
    }
}

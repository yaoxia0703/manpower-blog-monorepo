package com.manpowergroup.springboot.springboot3web.framework.security;

import com.manpowergroup.springboot.springboot3web.framework.security.authority.ApiPermission;
import com.manpowergroup.springboot.springboot3web.framework.security.authority.PermissionRuleProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * method + path で権限ルールを特定し、対応する code を Authority と照合する。
 * ルール未登録のリクエストは拒否する（default deny）。
 */
@Component
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final Set<String> SUPER_ADMIN_AUTHORITIES = Set.of(
            "ROLE_SUPER_ADMIN",
            "ROLE_ADMIN",
            "*"
    );

    private static final List<String> AUTHENTICATED_ONLY_PATHS = List.of(
            "/api/system/auth/me",
            "/api/system/auth/logout",
            "/api/system/menu/my-tree"
    );

    private final PermissionRuleProvider permissionRuleProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public DynamicAuthorizationManager(PermissionRuleProvider permissionRuleProvider) {
        this.permissionRuleProvider = permissionRuleProvider;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AuthorizationDecision check(
            Supplier<Authentication> authentication,
            RequestAuthorizationContext context
    ) {
        final Authentication current = authentication.get();
        if (current == null || !current.isAuthenticated() || current instanceof AnonymousAuthenticationToken) {
            return new AuthorizationDecision(false);
        }

        if (SUPER_ADMIN_AUTHORITIES.stream().anyMatch(authority -> hasAuthority(current, authority))) {
            return new AuthorizationDecision(true);
        }

        final HttpServletRequest request = context.getRequest();
        final String requestMethod = normalizeMethod(request.getMethod());
        final String requestPath = requestPath(request);

        if (AUTHENTICATED_ONLY_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath))) {
            return new AuthorizationDecision(true);
        }

        final List<String> matchedCodes = permissionRuleProvider.loadEnabledRules().stream()
                .filter(Objects::nonNull)
                .filter(rule -> methodMatches(rule, requestMethod))
                .filter(rule -> pathMatches(rule, requestPath))
                .map(ApiPermission::code)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .toList();

        final boolean granted = !matchedCodes.isEmpty()
                && matchedCodes.stream().anyMatch(code -> hasAuthority(current, code));
        return new AuthorizationDecision(granted);
    }

    private boolean methodMatches(ApiPermission rule, String requestMethod) {
        final String ruleMethod = normalizeMethod(rule.method());
        return ruleMethod != null && ruleMethod.equals(requestMethod);
    }

    private boolean pathMatches(ApiPermission rule, String requestPath) {
        final String rulePath = normalizePath(rule.path());
        return rulePath != null && pathMatcher.match(rulePath, requestPath);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }

    private String requestPath(HttpServletRequest request) {
        final String uri = request.getRequestURI();
        final String contextPath = request.getContextPath();
        if (uri == null || uri.isBlank()) {
            return "/";
        }
        if (contextPath != null && !contextPath.isBlank() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private String normalizeMethod(String value) {
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

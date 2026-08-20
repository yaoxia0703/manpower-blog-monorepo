package com.manpowergroup.springboot.springboot3web.framework.security;

import com.manpowergroup.springboot.springboot3web.framework.security.authority.ApiPermission;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicAuthorizationManagerTest {

    @Test
    void deniesAnonymousUser() {
        DynamicAuthorizationManager manager = managerWith(
                rule("sys:user:list", "GET", "/api/system/user/page"));

        AuthorizationResult decision = manager.authorize(
                () -> new AnonymousAuthenticationToken(
                        "anonymous",
                        "anonymousUser",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")),
                context("GET", "/api/system/user/page"));

        assertFalse(decision.isGranted());
    }

    @Test
    void grantsAdminWithoutLoadingRules() {
        DynamicAuthorizationManager manager = new DynamicAuthorizationManager(() -> {
            throw new AssertionError("admin should not need permission rules");
        });

        assertTrue(manager.authorize(
                authenticated("ROLE_ADMIN"),
                context("DELETE", "/api/articles/10")).isGranted());
    }

    @Test
    void grantsOnlyWhenMethodPathAndCodeMatch() {
        DynamicAuthorizationManager manager = managerWith(
                rule("content:article:delete", "DELETE", "/api/articles/{id}"));

        assertTrue(manager.authorize(
                authenticated("content:article:delete"),
                context("DELETE", "/api/articles/10")).isGranted());
        assertFalse(manager.authorize(
                authenticated("content:article:update"),
                context("DELETE", "/api/articles/10")).isGranted());
        assertFalse(manager.authorize(
                authenticated("content:article:delete"),
                context("PUT", "/api/articles/10")).isGranted());
    }

    @Test
    void deniesUnregisteredChildPathWithoutLegacyFallback() {
        DynamicAuthorizationManager manager = managerWith(
                rule("sys:menu:list", "GET", "/api/system/menu"));

        assertFalse(manager.authorize(
                authenticated("sys:menu:list"),
                context("GET", "/api/system/menu/tree")).isGranted());
    }

    @Test
    void supportsExplicitAntPatterns() {
        DynamicAuthorizationManager manager = managerWith(
                rule("sys:menu:read", "GET", "/api/system/menu/**"));

        assertTrue(manager.authorize(
                authenticated("sys:menu:read"),
                context("GET", "/api/system/menu/tree")).isGranted());
    }

    @Test
    void grantsAuthenticatedOnlyEndpointsWithoutPermissionRule() {
        DynamicAuthorizationManager manager = new DynamicAuthorizationManager(List::of);

        assertTrue(manager.authorize(
                authenticated("ROLE_AUDITOR"),
                context("GET", "/api/system/auth/me")).isGranted());
        assertTrue(manager.authorize(
                authenticated("ROLE_AUDITOR"),
                context("POST", "/api/system/auth/logout")).isGranted());
        assertFalse(manager.authorize(
                authenticated("ROLE_AUDITOR"),
                context("POST", "/api/articles/add")).isGranted());
    }

    private static DynamicAuthorizationManager managerWith(ApiPermission... rules) {
        return new DynamicAuthorizationManager(() -> List.of(rules));
    }

    private static ApiPermission rule(String code, String method, String path) {
        return new ApiPermission(code, path, method);
    }

    private static Supplier<Authentication> authenticated(String... authorities) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "user",
                null,
                AuthorityUtils.createAuthorityList(authorities));
        authentication.setAuthenticated(true);
        return () -> authentication;
    }

    private static RequestAuthorizationContext context(String method, String path) {
        HttpServletRequest request = new MockHttpServletRequest(method, path);
        return new RequestAuthorizationContext(request);
    }
}

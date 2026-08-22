package com.manpowergroup.springboot.springboot3web.framework.security.authority;

import java.util.List;

/**
 * 有効な HTTP 権限ルールをロードするための framework 境界。
 */
public interface PermissionRuleProvider {

    List<ApiPermission> loadEnabledRules();
}

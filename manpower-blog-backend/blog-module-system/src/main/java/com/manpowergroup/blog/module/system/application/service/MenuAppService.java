package com.manpowergroup.blog.module.system.application.service;

import com.manpowergroup.blog.module.system.application.command.menu.MenuCreateCommand;
import com.manpowergroup.blog.module.system.application.command.menu.MenuStatusChangeCommand;
import com.manpowergroup.blog.module.system.application.command.menu.MenuUpdateCommand;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuDetailResponse;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuOptionResponse;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuTreeResponse;

import java.util.Collection;
import java.util.List;

/**
 * メニューのユースケースを提供する。
 */
public interface MenuAppService {

    List<MenuTreeResponse> listTree();

    List<MenuTreeResponse> listTreeByUserId(Long userId);

    List<MenuOptionResponse> listOptions();

    MenuDetailResponse findById(Long id);

    Long create(MenuCreateCommand command);

    void update(MenuUpdateCommand command);

    void delete(Long id);

    void changeStatus(MenuStatusChangeCommand command);

    List<MenuTreeResponse> listEnabledTree();

    boolean allExist(Collection<Long> ids);
}

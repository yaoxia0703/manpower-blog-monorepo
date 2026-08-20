package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.system.application.command.menu.MenuCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.menu.MenuStatusChangeCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.menu.MenuUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuDetailResponse;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuOptionResponse;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuTreeResponse;

import java.util.Collection;
import java.util.List;

/**
 * メニューのユースケースを提供する。
 */
public interface MenuAppService {

    List<MenuTreeResponse> getAllMenuTree();

    List<MenuTreeResponse> selectMenusByUserId(Long userId);

    List<MenuOptionResponse> getMenuOptions();

    MenuDetailResponse getMenuDetail(Long id);

    Long createMenu(MenuCreateCommand command);

    void updateMenu(MenuUpdateCommand command);

    void deleteMenu(Long id);

    void changeMenuStatus(MenuStatusChangeCommand command);

    List<MenuTreeResponse> getActiveMenuTree();

    boolean allExist(Collection<Long> ids);
}

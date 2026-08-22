package com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.menu;

import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.module.system.domain.model.menu.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * システムメニュー管理テーブル
 * </p>
 *
 * @author YAOXIA
 * @since 2026-03-01
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * ユーザIDに基づいてメニューを選択する
     * @param  userId ユーザID
     * @return ユーザIDに基づいて選択されたメニューのリスト
     */
    List<Menu> selectMenusByUserId(@Param("userId") Long userId);



    /**
     * 指定した親メニューIDに紐づく全ての子メニューIDを取得する
     *
     * @param parentId 親メニューID
     * @return 子メニューIDのリスト
     */
    List<Long> selectAllDescendantIds(@Param("parentId") Long parentId);

    /**
     * メニューIDのリストに基づいてメニューの状態を一括更新する
     *
     * @param ids メニューIDのリスト
     * @param status 更新後の状態
     */
    void updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Status status);
}

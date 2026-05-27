package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.menu;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuTreeVo;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;
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
    List<MenuTreeVo> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 同一階層におけるメニュー名称の存在有無を取得する
     *
     * @param parentId 親メニューID
     * @param name メニュー名称
     * @return 件数（0: 存在しない / 1以上: 存在する）
     */
    int existsByParentIdAndName(@Param("parentId") Long parentId,
                                @Param("name") String name);


    /**
     * 指定した親メニューIDに紐づく子メニューの件数を取得する
     *
     * @param parentId 親メニューID
     * @return 子メニュー件数
     */
    int countByParentId(@Param("parentId") Long parentId);


    /**
     * 同一階層におけるメニュー名称の重複件数を取得する（自身を除く）
     *
     * @param parentId 親メニューID
     * @param name メニュー名称
     * @param id 除外対象のメニューID（自身）
     * @return 重複件数
     */
    int countByParentIdAndNameExcludeId(@Param("parentId") Long parentId,
                                        @Param("name") String name,
                                        @Param("id") Long id);

    /**
     * 指定したパスのメニュー件数を取得する
     *
     * @param path フロントエンドのルートパス
     * @return 件数
     */
    int countByPath(@Param("path") String path);


    /**
     * 指定したパスのメニュー件数を取得する（自身を除く）
     *
     * @param path フロントエンドのルートパス
     * @param id 除外対象のメニューID（自身）
     * @return 件数
     */
    int countByPathExcludeId(@Param("path") String path,
                             @Param("id") Long id);



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

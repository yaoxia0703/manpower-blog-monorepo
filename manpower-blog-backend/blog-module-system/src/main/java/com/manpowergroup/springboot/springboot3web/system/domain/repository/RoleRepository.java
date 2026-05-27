package com.manpowergroup.springboot.springboot3web.system.domain.repository;


public interface RoleRepository {

    /**
     * コードに基づいて論理削除されたロールを物理削除する
     *
     * @param code ロールコード
     * @return 削除件数
     */
    int deleteDeletedByCode( String code);
}

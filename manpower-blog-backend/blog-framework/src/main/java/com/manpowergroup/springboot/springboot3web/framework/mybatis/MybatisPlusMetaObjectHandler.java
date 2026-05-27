package com.manpowergroup.springboot.springboot3web.framework.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject m) {
        // 作成日時（未設定の場合のみ自動設定）
        if (getFieldValByName("createdAt", m) == null) {
            setFieldValByName("createdAt", LocalDateTime.now(), m);
        }

        // 更新日時（未設定の場合のみ自動設定）
        if (getFieldValByName("updatedAt", m) == null) {
            setFieldValByName("updatedAt", LocalDateTime.now(), m);
        }

        // 論理削除フラグ（Byte 型に注意、未設定の場合は 0 を設定）
        if (getFieldValByName("isDeleted", m) == null) {
            setFieldValByName("isDeleted", (byte) 0, m);
        }
    }

    @Override
    public void updateFill(MetaObject m) {
        // 更新時は常に更新日時を上書きする
        setFieldValByName("updatedAt", LocalDateTime.now(), m);
    }
}

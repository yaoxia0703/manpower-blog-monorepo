package com.manpowergroup.blog.module.content.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;

import java.util.Arrays;

/** 記事の公開状態。 */
public enum ArticleStatus {
    DRAFT((byte) 0),
    PUBLISHED((byte) 1),
    UNPUBLISHED((byte) 2);

    @EnumValue
    private final byte code;

    ArticleStatus(byte code) {
        this.code = code;
    }

    @JsonValue
    public int toJson() {
        return code;
    }

    @JsonCreator
    public static ArticleStatus fromJson(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == (byte) code)
                .findFirst()
                .orElseThrow(() -> BizException.withDetail(
                        ErrorCode.BAD_REQUEST, "無効な記事状態: " + code));
    }
}

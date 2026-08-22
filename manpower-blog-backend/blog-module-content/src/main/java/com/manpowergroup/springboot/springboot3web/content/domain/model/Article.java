package com.manpowergroup.springboot.springboot3web.content.domain.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manpowergroup.springboot.springboot3web.blog.common.support.DomainGuard;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 記事エンティティ。 */
@Getter
@TableName("t_content_article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    // 記事ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 記事タイトル
    private String title;

    // 記事概要
    private String summary;

    // 記事本文
    private String content;

    // カテゴリID
    private Long categoryId;

    // 作成者ID
    private Long authorId;

    // 記事状態
    private ArticleStatus status;

    // 作成日時
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 更新日時
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 論理削除フラグ
    @TableLogic
    @TableField(value = "is_deleted")
    private Byte isDeleted;

    protected Article() {
    }

    private Article(String title, String summary, String content, Long categoryId,
                    Long authorId, ArticleStatus status) {
        this.authorId = DomainGuard.requireNonNull(authorId, "作成者ID");
        update(title, summary, content, categoryId, status);
        this.isDeleted = 0;
    }

    /** 新しい記事を作成する。 */
    public static Article create(String title, String summary, String content, Long categoryId,
                                 Long authorId, ArticleStatus status) {
        return new Article(title, summary, content, categoryId, authorId, status);
    }

    /** 記事内容を更新する。 */
    public void update(String title, String summary, String content,
                       Long categoryId, ArticleStatus status) {
        this.title = DomainGuard.requireText(title, "記事タイトル");
        this.summary = DomainGuard.normalizeText(summary);
        this.content = DomainGuard.requireText(content, "記事本文");
        this.categoryId = DomainGuard.requireNonNull(categoryId, "カテゴリID");
        this.status = DomainGuard.requireNonNull(status, "記事状態");
    }

    /** 記事を公開する。 */
    public void publish() {
        this.status = ArticleStatus.PUBLISHED;
    }

    /** 記事を非公開にする。 */
    public void unpublish() {
        this.status = ArticleStatus.UNPUBLISHED;
    }

    /** 記事を下書きへ戻す。 */
    public void returnToDraft() {
        this.status = ArticleStatus.DRAFT;
    }

    /** 記事状態を変更する。 */
    public void changeStatus(ArticleStatus status) {
        this.status = DomainGuard.requireNonNull(status, "記事状態");
    }

    /** 記事のカテゴリを変更する。 */
    public void changeCategory(Long categoryId) {
        this.categoryId = DomainGuard.requireNonNull(categoryId, "カテゴリID");
    }
}

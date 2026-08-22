package com.manpowergroup.blog.module.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manpowergroup.blog.module.content.domain.model.Article;
import com.manpowergroup.blog.module.content.domain.model.ArticleSearchCriteria;
import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;
import com.manpowergroup.blog.module.content.domain.model.ArticleView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    List<ArticleView> search(
            @Param("criteria") ArticleSearchCriteria criteria,
            @Param("offset") long offset,
            @Param("size") long size
    );

    long count(@Param("criteria") ArticleSearchCriteria criteria);

    ArticleView findViewById(@Param("id") Long id, @Param("status") ArticleStatus status);
}

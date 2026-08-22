package com.manpowergroup.blog.shared.util;

import java.util.List;

public interface TreeNode<T> {
    Long getId();
    Long getParentId();
    Integer getSort();
    List<T> getChildren();
    void setChildren(List<T> children);
}
package com.manpowergroup.springboot.springboot3web.blog.common.util;

import java.util.Comparator;
import java.util.List;

public class TreeUtils {
    private TreeUtils() {}


    public static <T extends TreeNode<T>> List<T> buildTree(List<T> all, Long parentId) {
        return all.stream()
                .filter(node -> node.getParentId().equals(parentId))
                .peek(node -> node.setChildren(buildTree(all, node.getId())))
                .sorted(Comparator.comparing(TreeNode::getSort))
                .toList();
    }
}

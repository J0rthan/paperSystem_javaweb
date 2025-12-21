package com.bjfu.paperSystem.superAdmin.utils;

import java.util.*;

/*
    sys_admin 系统管理员
    editdpt_admin 编辑部管理员
    chief_editor 主编
    editor 编辑
    reviewer 审稿人
    author 作者

    所有权限如下：
    submit_paper 提交新稿件
    view_all_submissions 查看所有稿件
    invite_recruit 邀请/指派人员
    view_reviewer_identity 查看审稿人身份
    fill_review_comments 填写审稿意见
    make_decision 做出录用/拒稿决定
    edit_sys_setting 修改系统配置
 */

public final class permissionList {

    // userType -> permissions
    // default permission map
    private static final Map<String, Set<String>> PERMISSION_MAP = new HashMap<>();

    static {
        // ===== 编辑部管理员 =====
        PERMISSION_MAP.put("editdpt_admin", Set.of(
                "view_all_submissions",
                "invite_recruit",
                "view_reviewer_identity",
                "edit_sys_setting"
        ));

        // ===== 作者 =====
        PERMISSION_MAP.put("author", Set.of(
                "submit_paper"
        ));

        // ===== 审稿人 =====
        PERMISSION_MAP.put("reviewer", Set.of(
                "fill_review_comments"
        ));

        // ===== 编辑 =====
        PERMISSION_MAP.put("editor", Set.of(
                "invite_recruit",
                "view_reviewer_identity"
        ));

        // ===== 主编(EIC) =====
        PERMISSION_MAP.put("chief_editor", Set.of(
                "view_all_submissions",
                "invite_recruit",
                "view_reviewer_identity",
                "make_decision"
        ));
    }

    /**
     * 获取某个用户类型的权限集合
     */
    public static Set<String> getPermissions(String userType) {
        return PERMISSION_MAP.getOrDefault(userType, Collections.emptySet());
    }

    /**
     * 判断用户类型是否拥有某个权限
     */
    public static boolean hasPermission(String userType, String permission) {
        return getPermissions(userType).contains(permission);
    }
}
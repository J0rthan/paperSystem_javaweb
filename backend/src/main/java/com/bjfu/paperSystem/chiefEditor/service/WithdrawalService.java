package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.javabeans.Manuscript;

import java.util.List;

public interface WithdrawalService {

    // 可执行撤稿操作的稿件列表
    List<Manuscript> listWithdrawalCandidates();

    // 执行撤稿
    void withdrawManuscript(int manuscriptId, String reason);
}
package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.javabeans.ClientMessage;
import com.bjfu.paperSystem.javabeans.User;
import java.util.List;

public interface EditorMessageService {
    /**
     * 获取某个稿件的沟通历史（Editor和作者之间的所有消息）
     * @param manuscriptId 稿件ID
     * @param editorId 编辑ID（用于权限验证）
     * @return 消息列表，按时间升序排列
     */
    List<ClientMessage> getCommunicationHistory(int manuscriptId, int editorId);
}
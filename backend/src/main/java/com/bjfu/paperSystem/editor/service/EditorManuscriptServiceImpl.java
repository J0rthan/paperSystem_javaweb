package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.dto.EditorManuscriptDTO;
import com.bjfu.paperSystem.editor.dao.EditorManuscriptDao;
import com.bjfu.paperSystem.editor.dao.EditorReviewDao;
import com.bjfu.paperSystem.editor.dao.EditorRecordAllocationDao;
import com.bjfu.paperSystem.editor.dao.ManuscriptAuthorsDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.javabeans.Record_Allocation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class EditorManuscriptServiceImpl implements EditorManuscriptService {

    @Autowired private EditorManuscriptDao manuscriptDao;
    @Autowired private EditorReviewDao reviewDao;
    @Autowired private EditorRecordAllocationDao recordAllocationDao; // 新注入
    @Autowired private ManuscriptAuthorsDao manuscriptAuthorsDao; // 新注入

    @Override
    public List<EditorManuscriptDTO> getMyManuscripts(int editorId) {
        // 1. 查找所有指派给该编辑的稿件
        List<Manuscript> rawList = manuscriptDao.findByEditorId(editorId);
        List<EditorManuscriptDTO> dtoList = new ArrayList<>();

        for (Manuscript m : rawList) {
            String status = m.getStatus();

            // 2. 状态过滤：只保留 "With Editor" 和 "Under Review"
            // (注意：数据库中存储的状态字符串需要和这里匹配，比如 "With Editor" 或 "WITH_EDITOR")
            if (status == null || (!status.equalsIgnoreCase("With Editor") && !status.equalsIgnoreCase("Under Review"))) {
                continue;
            }

            // 3. 获取分配时间
            LocalDateTime assignTime = null;
            Record_Allocation allocation = recordAllocationDao.findLatestByManuscriptId(m.getManuscriptId());
            if (allocation != null) {
                assignTime = allocation.getAssignTime();
            } else {
                assignTime = m.getSubmitTime(); // 兜底：如果没查到分配记录，暂时用提交时间
            }

            // 4. 获取匿名作者信息 (只获取单位)
            String affiliation = manuscriptAuthorsDao.findAuthorAffiliationByManuscriptId(m.getManuscriptId());
            String authorInfo = (affiliation != null) ? "Affiliation: " + affiliation : "Hidden Author";

            // 5. 计算是否紧急 (针对 Under Review)
            boolean isUrgent = false;
            if ("Under Review".equalsIgnoreCase(status)) {
                List<Review> reviews = reviewDao.findByManuId(m.getManuscriptId());
                LocalDateTime now = LocalDateTime.now();

                for (Review r : reviews) {
                    // 只检查未完成的审稿 (PENDING 或 ACCEPTED)
                    if (r.getDeadline() != null && !"FINISHED".equalsIgnoreCase(r.getStatus())) {
                        long daysLeft = ChronoUnit.DAYS.between(now, r.getDeadline());
                        // 如果逾期 或者 剩余时间不足3天
                        if (daysLeft < 3) {
                            isUrgent = true;
                            break;
                        }
                    }
                }
            }

            // 6. 存入 DTO
            dtoList.add(new EditorManuscriptDTO(
                    m.getManuscriptId(),
                    m.getTitle(),
                    status, // 保持原样或格式化
                    assignTime,
                    authorInfo,
                    isUrgent
            ));
        }
        return dtoList;
    }
}
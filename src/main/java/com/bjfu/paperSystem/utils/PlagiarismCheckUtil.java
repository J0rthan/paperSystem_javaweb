package com.bjfu.paperSystem.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PlagiarismCheckUtil {

    private final Random random = new Random();

    /**
     * 模拟查重功能，生成查重率
     * @param manuscriptId 稿件ID（用于生成基于稿件ID的伪随机数，确保同一稿件的查重率一致）
     * @param textContent 稿件文本内容（用于增加随机性）
     * @return 查重率（百分比，保留一位小数）
     */
    public double checkPlagiarism(int manuscriptId, String textContent) {
        // 使用稿件ID和文本长度作为种子，确保同一稿件的查重率一致
        long seed = manuscriptId + (textContent != null ? textContent.length() : 0);
        Random localRandom = new Random(seed);

        // 生成1%到35%之间的查重率，以模拟真实情况
        double plagiarismRate = 1.0 + localRandom.nextDouble() * 34.0;
        
        // 保留一位小数
        return Math.round(plagiarismRate * 10.0) / 10.0;
    }

    /**
     * 检查查重率是否超过阈值
     * @param plagiarismRate 查重率
     * @return 是否为高相似度（超过20%）
     */
    public boolean isHighSimilarity(double plagiarismRate) {
        return plagiarismRate > 20.0;
    }

    /**
     * 获取查重结果描述
     * @param plagiarismRate 查重率
     * @return 查重结果描述
     */
    public String getPlagiarismResultDescription(double plagiarismRate) {
        if (isHighSimilarity(plagiarismRate)) {
            return "高相似度";
        } else {
            return "正常";
        }
    }

    /**
     * 生成模拟的查重报告URL（用于演示）
     * @param manuscriptId 稿件ID
     * @return 模拟的查重报告URL
     */
    public String generatePlagiarismReportUrl(int manuscriptId) {
        // 在实际项目中，这里应该返回真实的查重报告URL
        // 现在返回一个模拟的URL
        return "/plagiarism-report/" + manuscriptId;
    }
}

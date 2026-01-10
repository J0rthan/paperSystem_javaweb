package com.bjfu.paperSystem.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PdfProcessingUtil {

    /**
     * 从PDF文件中提取文本内容
     * @param filePath PDF文件路径
     * @return 提取的文本内容
     */
    public String extractTextFromPdf(String filePath) throws IOException {
        // 处理相对路径，转换为绝对路径
        Path pdfPath = Paths.get(filePath);
        if (!pdfPath.isAbsolute()) {
            // 如果是相对路径，尝试从项目根目录和target目录查找
            String projectPath = System.getProperty("user.dir");
            
            // 尝试在static目录查找
            Path projectRelativePath = Paths.get(projectPath, "src", "main", "resources", "static", filePath);
            if (projectRelativePath.toFile().exists()) {
                pdfPath = projectRelativePath;
            } else {
                // 尝试在target目录查找
                Path targetPath = Paths.get(projectPath, "target", "classes", "static", filePath);
                if (targetPath.toFile().exists()) {
                    pdfPath = targetPath;
                } else {
                    // 尝试直接使用文件路径
                    pdfPath = Paths.get(filePath);
                }
            }
        }

        File pdfFile = pdfPath.toFile();
        if (!pdfFile.exists()) {
            throw new IOException("PDF file not found: " + pdfFile.getAbsolutePath());
        }

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * 计算文本字数（包括中文、英文单词和标点符号）
     * @param text 要计算的文本内容
     * @return 字数统计结果
     */
    public int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        // 移除多余的空白字符
        text = text.trim().replaceAll("\\s+|\\n|\\t|\\r", " ");
        
        // 计算中文汉字和其他字符
        int chineseCount = 0;
        // 计算英文单词数
        int englishWordCount = 0;

        // 统计中文汉字
        for (char c : text.toCharArray()) {
            if (isChineseCharacter(c)) {
                chineseCount++;
            }
        }

        // 统计英文单词
        String[] words = text.split(" ");
        for (String word : words) {
            if (word.matches("[a-zA-Z]+")) {
                englishWordCount++;
            }
        }

        // 总字数 = 中文汉字数 + 英文单词数
        return chineseCount + englishWordCount;
    }

    /**
     * 判断字符是否为中文汉字
     * @param c 要判断的字符
     * @return 是否为中文汉字
     */
    private boolean isChineseCharacter(char c) {
        // 中文汉字的Unicode范围
        return c >= 0x4E00 && c <= 0x9FA5;
    }

    /**
     * 从PDF文件路径直接计算字数
     * @param filePath PDF文件路径
     * @return 字数统计结果
     */
    public int countWordsFromPdf(String filePath) throws IOException {
        String text = extractTextFromPdf(filePath);
        return countWords(text);
    }
}

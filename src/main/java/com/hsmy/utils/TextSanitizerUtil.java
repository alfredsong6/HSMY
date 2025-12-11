package com.hsmy.utils;

import org.springframework.util.StringUtils;

/**
 * 文本清洗工具，去除 Markdown/HTML，仅保留纯文本。
 */
public final class TextSanitizerUtil {

    private TextSanitizerUtil() {
    }

    /**
     * 将内容转换为纯文本，移除常见的 Markdown/HTML 标记并折叠多余空白。
     *
     * @param content 原始内容
     * @return 纯文本内容
     */
    public static String toPlainText(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }

        String text = content.replace("\r", "");
        // 代码块与行内代码
        text = text.replaceAll("(?s)```.*?```", " ");
        text = text.replaceAll("(?s)`([^`]*)`", "$1");
        // 图片与链接保留可读文字
        text = text.replaceAll("!\\[([^\\]]*)]\\([^)]*\\)", "$1");
        text = text.replaceAll("\\[([^\\]]+)]\\(([^)]+)\\)", "$1");
        // HTML 标签
        text = text.replaceAll("<[^>]+>", " ");
        // 标题、引用、列表前缀
        text = text.replaceAll("(?m)^\\s{0,3}[>#*-]\\s+", "");
        text = text.replaceAll("(?m)^\\s*\\d+\\.\\s+", "");
        text = text.replaceAll("(?m)^#{1,6}\\s*", "");
        // 其余 Markdown 标记符
        text = text.replaceAll("[*_~`]+", "");
        text = text.replace("&nbsp;", " ");
        // 折叠空白
        text = text.replaceAll("\\s+", " ");
        return text.trim();
    }
}

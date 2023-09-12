package com.yang.util;

import java.util.ArrayList;
import java.util.List;

/**
 * String相关处理工具
 * @author shanfy
 * @date 2023-09-11 17:00
 */
public class StrUtils {

    /**
     * 将一段字符串按照";"字符分割开来，同时排除位于单引号或双引号之内
     */
    public static List<String> splitStringBySemicolon(String input) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean withinQuotes = false;

        for (char c : input.toCharArray()) {
            if (c == ';') {
                if (withinQuotes) {
                    sb.append(c);
                } else {
                    // 分隔符在引号外，将结果添加到列表中
                    result.add(sb.toString().trim());
                    // 清空StringBuilder
                    sb.setLength(0);
                }
            } else if (c == '\"') {
                withinQuotes = !withinQuotes;
                sb.append(c);
            } else {
                sb.append(c);
            }
        }

        // 添加最后一个分割字符串
        result.add(sb.toString().trim());
        return result;
    }

    /**
     * 连续的空格替换为单个空格
     */
    public static String removeBlank(String originalSql) {
        return originalSql.replaceAll("\\s+", " ");
    }
}

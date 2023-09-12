package com.yang.enums;

/**
 * 人大金仓 日期各元素位置枚举
 * @author shanfy
 * @date 2023-08-25 11:08
 */
public enum KSDateFormatEnum {

    /**
     * 年符号位置
     */
    YEAR("%Y", 4),

    /**
     * 月符号位置
     */
    MONTH("%m", 7),

    /**
     * 日符号位置
     */
    DAY("%d", 10),

    /**
     * 秒符号位置
     */
    SECOND("%s", 19);

    private String originalSuffix;
    private int endIndex;

    KSDateFormatEnum(String originalSuffix, int endIndex) {
        this.originalSuffix = originalSuffix;
        this.endIndex = endIndex;
    }

    public String getOriginalSuffix() {
        return originalSuffix;
    }

    public void setOriginalSuffix(String originalSuffix) {
        this.originalSuffix = originalSuffix;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
}
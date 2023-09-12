package com.yang.constant;

/**
 * 人大金仓 kingBase常量类
 * @author shanfy
 * @date 2023-08-25 11:08
 */
public class KingBaseConstant {

    public static final String DB_DRIVER_NAME = "kingbase";

    public static final String SYMBOL_POINT = ".";

    public static final String SYMBOL_MARK = "'";

    public static final String LEVEL_STR = "level";

    /**
     * 匹配select|.|where|when|and|or level
     */
    public static final String LEVEL_REGEX = "(?i)(select|\\.|\\,|where|when|and|or|by|distinct|then|else|as)(\\s*)level";

    public static final String LEVEL_NEW = "\"level\"";

    public static final String IF_NULL_STR = "ifnull";

    public static final String IF_NULL_REGEX = "(?i)ifnull\\s*\\(";

    public static final String IF_NULL_NEW = "nvl(";

    public static final String DATE_FORMAT_STR = "date_format";

    public static final String DATE_FORMAT_REGEX = "(?i)date_format\\(.+?('\\))";

    public static final String DATE_FORMAT_NEW = "substring(";

    public static final String DATE_FORMAT_JOIN_STR = "0,";

    public static final String DATE_FORMAT_END_STR = ")";

    public static final String CUR_DATE_STR = "curdate()";

    public static final String CUR_DATE_REGEX = "(?i)curdate()";

    public static final String CUR_DATE_NEW = "current_date";

    public static final String DATE_DIFF_REGEX = "(?i)datediff\\(.+?\\,.+?\\)\\)?";

    public static final String TO_CHAR_STR = "to_char(";

    public static final String YYYY_MM_DD = "'YYYY-MM-DD'";

    /**
     * YEAR()函数替换
     */
    public static final String YEAR_FORMAT = "year(";

    public static final String YEAR_FORMAT_REGEX = "year\\(";

    public static final String DATE_PART = "date_part('year',";

    public static final String DATE_DIFF = "datediff";

}
package com.yang.constant;

/**
 * 神通 OScar常量类
 * @author shanfy
 * @date 2023-08-25 11:08
 */
public class OscarConstant {

        public static final String DB_DRIVER_NAME = "oscar";

        /**
         * 匹配 and|or|where|(  xxx like concat('%',?'%')
         */
        public static final String LIKE_REGEX = "(?i)(and|or|where|\\(){1}[^=\\)\\(]+like concat(.+?)\\)";

        public static final String LIKE_KEYWORD_PREFIX = " instr(";

        public static final String LIKE_KEYWORD_SUFFIX = ")>0 ";

        public static final String LIKE_STR = "like";

        public static final String SQL_PARAM = "?";

        public static final String DATE_DIFF_REGEX = "datediff\\(";

        public static final String DATE_DIFF_NEW = "datediff\\(9,";

        public static final String DATE_DIFF = "datediff";


    }
package com.yang.adapter;

import com.yang.constant.CommonConstant;
import com.yang.constant.OscarConstant;
import com.yang.enums.SqlCommandType;
import com.yang.exception.BusinessException;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.List;
import java.util.Map;

/**
 * 神通适配
 * @author shanfy
 * @date 2023-08-25 11:38
 */
public class OscarAdapter extends TransferAdapter {


    @Override
    protected Map<String, List<String>> transferColumnDefinitions(CreateTable createTable, List<ColumnDefinition> columnDefinitions) {
        throw new BusinessException("Oscar not support limit type sql");
    }

    @Override
    public String doTransfer(String newSql) {
        String lowerCase = newSql.toLowerCase();
        if (lowerCase.contains(OscarConstant.LIKE_STR)) {
            newSql = likeReplaceLoop(newSql);
        }
        if (lowerCase.contains(OscarConstant.DATE_DIFF)){
            newSql = dateDiffReplace(newSql);
        }

        return newSql;
    }

    /**
     * 依据神通语法替换datediff
     * datediff(now(), office_room.idle_start_time) ==> datediff(9,now(), office_room.idle_start_time)
     * 9为天数差，
     * @param originalSql 原sql
     * @return 替换后的sql
     */
    private String dateDiffReplace(String originalSql) {
        return originalSql.replaceAll(OscarConstant.DATE_DIFF_REGEX,OscarConstant.DATE_DIFF_NEW);
    }

    /**
     * 递归替换用instr替换like函数
     * nature like concat('%','BUSINESS','%') ==> instr(nature,'BUSINESS')>0
     * @param sql 原始sql
     * @return 替换后的sql
     */
    private static String likeReplaceLoop(String sql) {
        // 去除换行符
        sql = sql.replaceAll(CommonConstant.SYMBOL_NEW_LINES, CommonConstant.SPACE);
        // 用正则将sql中的第一个 like语句替换为@，并以此分隔后长度不为2，则改语句没有like函数
        String replaceSql = sql.replaceFirst(OscarConstant.LIKE_REGEX, CommonConstant.SEPARATOR).trim();
        // sql语句去除头部空格且尾部加结束符；，防止正则刚好适配到尾部，无法分隔
        replaceSql = replaceSql + CommonConstant.SYMBOL_NEW_LINES;
        String[] split = replaceSql.split(CommonConstant.SEPARATOR);
        if (split.length != 2) {
            return sql;
        }
        // 去除前面加的换行符；防止会面截取需要替换的sql时，截取多或截取不到
        split[1] = split[1].trim();
        // 找出替换出来的原sql片段
        String substring = sql.substring(split[0].length() - 1);
        String needReplaceStr = substring.replace(split[1], CommonConstant.EMPTY).trim();
        // 逻辑运算符and/or/where/(
        String logicalOperator;
        // 数据库字段
        String dbField;
        // 参数   ？或者字符串
        String sqlParam = needReplaceStr.contains(OscarConstant.SQL_PARAM) ? OscarConstant.SQL_PARAM : needReplaceStr.substring(
                needReplaceStr.indexOf(CommonConstant.COMMA) + 1, needReplaceStr.lastIndexOf(CommonConstant.COMMA));
        int likeIndex = needReplaceStr.toLowerCase().indexOf(OscarConstant.LIKE_STR);
        if (needReplaceStr.startsWith(CommonConstant.LEFT_BRACKET)) {
            logicalOperator = CommonConstant.LEFT_BRACKET;
            dbField = needReplaceStr.substring(1, likeIndex);
        } else {
            int spaceIndex = needReplaceStr.indexOf(CommonConstant.SPACE);
            logicalOperator = needReplaceStr.substring(0, spaceIndex);
            dbField = needReplaceStr.substring(spaceIndex, likeIndex);
        }
        String afterReplaceStr =
                logicalOperator + OscarConstant.LIKE_KEYWORD_PREFIX + dbField + CommonConstant.COMMA + sqlParam + OscarConstant.LIKE_KEYWORD_SUFFIX;
        return likeReplaceLoop(split[0] + CommonConstant.SPACE + afterReplaceStr + CommonConstant.SPACE + split[1]);
    }

}

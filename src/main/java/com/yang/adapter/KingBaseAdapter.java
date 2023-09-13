package com.yang.adapter;


import com.yang.constant.CommonConstant;
import com.yang.constant.KingBaseConstant;
import com.yang.enums.KSDateFormatEnum;
import com.yang.enums.SqlCommandType;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人大金仓适配
 * @author shanfy
 * @date 2023-08-25 11:38
 */
public class KingBaseAdapter extends TransferAdapter {

    @Override
    protected Map<String, List<String>> transferColumnDefinitions(CreateTable createTable, List<ColumnDefinition> columnDefinitions) {
        List<ColumnDefinition> newColumnDefinitions = new ArrayList<>(columnDefinitions.size());
        Map<String, List<String>> commentMap = new HashMap<>(4);
        List<String> comments = new ArrayList<>(columnDefinitions.size());
        columnDefinitions.forEach(columnDefinition -> {
            ColumnDefinition newColumnDefinition = new ColumnDefinition();
            newColumnDefinition.setColumnName(columnDefinition.getColumnName());
            ColDataType colDataType = getColDataType(columnDefinition.getColDataType());
            newColumnDefinition.setColDataType(colDataType);
            List<String> columnSpecStrings = getColumnSpecStrings(createTable, columnDefinition, comments);
            newColumnDefinition.setColumnSpecStrings(columnSpecStrings);
            newColumnDefinitions.add(newColumnDefinition);
        });

        // 收集各个索引信息
        List<Index> indexList = createTable.getIndexes();
        if(CollectionUtils.isNotEmpty(indexList)){
            List<String> indexes = new ArrayList<>(indexList.size());
            // 最终建表sql中 只保留主键索引，其它索引拼接到最终输出的sql中
            List<Index> newIndexes = new ArrayList<>(1);
            indexList.forEach(index->{
                if(index.getType().equalsIgnoreCase("PRIMARY KEY")){
                    newIndexes.add(index);
                }else{
                    indexes.add("create index " + index.getName() + " on " + createTable.getTable().getName() + " (" + StringUtils.join(index.getColumnsNames(), ",") +" );\n");
                }
            });
            createTable.setIndexes(newIndexes);
            if(CollectionUtils.isNotEmpty(indexes)){
                commentMap.put("indexes", indexes);
            }
        }

        // 收集表注释
        List<?> tableOptionsStrings = createTable.getTableOptionsStrings();
        if(CollectionUtils.isNotEmpty(tableOptionsStrings)){
            int createSize = tableOptionsStrings.size();
            for (int i = 0; i < createSize; i++) {
                if((tableOptionsStrings.get(i)+"").equalsIgnoreCase("comment")){
                    if( ++i < createSize ){
                        // 收集表注释内容
                        comments.add("comment on table " + createTable.getTable().getName() + " is '" + tableOptionsStrings.get(i) +"';\n");
                    }
                }
            }
            createTable.setTableOptionsStrings(null);
        }
        createTable.setColumnDefinitions(newColumnDefinitions);
        commentMap.put("comments", comments);
        return commentMap;
    }


    /**
     *收集comment注释
     */
    private List<String> getColumnSpecStrings(CreateTable createTable, ColumnDefinition columnDefinition, List<String> columnComments) {
        String columnName = columnDefinition.getColumnName();
        Table table = createTable.getTable();
        String tableName = table.getName();

        List<String> columnSpecStrings = columnDefinition.getColumnSpecStrings();
        // 收集列注释
        if(CollectionUtils.isNotEmpty(columnSpecStrings)){
            int size = columnSpecStrings.size();
            List<String> newColumaSpecs = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                if((columnSpecStrings.get(i)+"").equalsIgnoreCase("comment")){
                    if( ++i < size ){
                        // 收集列注释内容
                        columnComments.add("comment on column " + tableName + "." + columnName + " is " + columnSpecStrings.get(i) +";\n");
                    }
                }else{
                    newColumaSpecs.add(columnSpecStrings.get(i));
                }
            }
            return newColumaSpecs;
        }
        return columnSpecStrings;
    }


    /**
     * 替换数据类型
     */
    private ColDataType getColDataType(ColDataType colDataType) {
        String dataTypeStr = colDataType.getDataType();
        if(StringUtils.equals("varchar", dataTypeStr)){
            List<String> arguments = colDataType.getArgumentsStringList();
            if(CollectionUtils.isNotEmpty(arguments)){
                List<String> newArguments = new ArrayList<>(2);
                // 固定为char 不然默认bytes, 编码不一样，容纳的字符数量不一致
                newArguments.add(arguments.get(0) + " char");
                colDataType.setArgumentsStringList(newArguments);
            }
        }else if(StringUtils.equals("datetime", dataTypeStr)){
            colDataType.setDataType("timestamp");
        }else if(StringUtils.equals("decimal", dataTypeStr)){
            colDataType.setDataType("numeric");
        } else if(StringUtils.equals("tinyint", dataTypeStr)
                || StringUtils.equals("int", dataTypeStr)
                || StringUtils.equals("smallint", dataTypeStr)
                || StringUtils.equals("mediumint", dataTypeStr)
                || StringUtils.equals("bigint", dataTypeStr)){
            // kingBase不能指定tiny或者int具体的位数
            colDataType.setArgumentsStringList(null);
        } else{
            // 有需要再加上其他类型
            return colDataType;
        }
        return colDataType;
    }

    @Override
    public String doTransfer(String newSql) {
        String lowerCase = newSql.toLowerCase();


        if (lowerCase.contains(KingBaseConstant.LEVEL_STR)) {
            newSql = levelReplaceLoop(newSql);
        }
        if (lowerCase.contains(KingBaseConstant.IF_NULL_STR)) {
            newSql = ifNullReplace(newSql);
        }
        if (lowerCase.contains(KingBaseConstant.DATE_FORMAT_STR)) {
            newSql = dateFormatReplaceLoop(newSql);
        }
        if (lowerCase.contains(KingBaseConstant.CUR_DATE_STR)) {
            newSql = curDateReplace(newSql);
        }
        if (lowerCase.contains(KingBaseConstant.DATE_DIFF)) {
            newSql = dateDiffReplaceLoop(newSql);
        }
        if(lowerCase.contains(KingBaseConstant.YEAR_FORMAT)){
            newSql = yearFormatReplace(newSql);
        }
        return newSql;
    }

    /**
     * YEAR()函数适配
     *  YEAR('2022-11-11 11:11:11') -> date_part('year','2022-11-11 11:11:11')
     * @param sql 待处理sql
     */
    private String yearFormatReplace(String sql) {
        sql = sql.replaceAll(KingBaseConstant.YEAR_FORMAT_REGEX, KingBaseConstant.DATE_PART);
        return sql;
    }

    /**
     * 替换datediff
     * datediff(now(), idle_start_time)==>now()-idle_start_time
     * @param sql 原始sql
     * @return 替换后的sql
     */
    private String dateDiffReplaceLoop(String sql) {
        // 去除换行符
        sql = sql.replaceAll(CommonConstant.SYMBOL_NEW_LINES, CommonConstant.SPACE);
        // 用正则将sql中的第一个 like语句替换为@，并以此分隔后长度不为2，则改语句没有like函数
        String replaceSql = sql.replaceFirst(KingBaseConstant.DATE_DIFF_REGEX, CommonConstant.SEPARATOR);
        String[] split = replaceSql.split(CommonConstant.SEPARATOR);
        if (split.length != 2) {
            return sql;
        }
        // 找出替换出来的原sql片段
        String substring = sql.substring(split[0].length() - 1);
        String needReplaceStr = substring.replace(split[1], CommonConstant.EMPTY).trim();
        int commaIndex = needReplaceStr.indexOf(CommonConstant.COMMA);
        String date1 = KingBaseConstant.CUR_DATE_NEW;
        String date2 = KingBaseConstant.TO_CHAR_STR+needReplaceStr.substring(commaIndex+1,needReplaceStr.length()-1)
                +CommonConstant.COMMA+ KingBaseConstant.YYYY_MM_DD+CommonConstant.RIGHT_BRACKET;
        return dateDiffReplaceLoop(split[0] + CommonConstant.SPACE + date1 + CommonConstant.REDUCE +
                date2 + CommonConstant.SPACE + split[1]);
    }

    /**
     * 替换curDate()==>current_date
     * @param newSql 原始sql
     * @return 替换后的sql
     */
    private String curDateReplace(String newSql) {
        return newSql.replaceAll(KingBaseConstant.CUR_DATE_REGEX, KingBaseConstant.CUR_DATE_NEW);
    }

    /**
     * 递归替换date_format(now(), '%Y-%m-%d')
     * date_format(now(), '%Y-%m-%d')==>substring(now(),0,10)
     * @param sql 原始sql
     * @return 替换后的sql
     */
    private static String dateFormatReplaceLoop(String sql) {
        // 去除换行符
        sql = sql.replaceAll(CommonConstant.SYMBOL_NEW_LINES, CommonConstant.SPACE);
        // 用正则将sql中的第一个 dateformat语句替换为@，并以此分隔后长度不为2，则改语句没有like函数
        String replaceSql = sql.replaceFirst(KingBaseConstant.DATE_FORMAT_REGEX, CommonConstant.SEPARATOR);
        // sql语句去除头部空格且尾部加结束符；，防止正则刚好适配到尾部，无法分隔
        replaceSql = replaceSql.trim() + CommonConstant.SYMBOL_NEW_LINES;
        // sql语句去除头尾部空格
        String[] split = replaceSql.split(CommonConstant.SEPARATOR);
        if (split.length != 2) {
            return sql;
        }
        split[1] = split[1].trim();
        // 找出替换出来的原sql片段
        String substring = sql.substring(split[0].length() - 1);
        String needReplaceStr = substring.replace(split[1], CommonConstant.EMPTY).trim();
        // 取日期格式化部分，判断是年/月/日，返回substring函数截取结束index
        int endIndex = getSubstringEndIndex(needReplaceStr);
        String sqlParam =
                needReplaceStr.substring(needReplaceStr.indexOf(CommonConstant.LEFT_BRACKET) + 1,
                needReplaceStr.indexOf(KingBaseConstant.SYMBOL_MARK));
        return dateFormatReplaceLoop(split[0] + CommonConstant.SPACE + KingBaseConstant.DATE_FORMAT_NEW + sqlParam +
                KingBaseConstant.DATE_FORMAT_JOIN_STR + endIndex + KingBaseConstant.DATE_FORMAT_END_STR + CommonConstant.SPACE + split[1]);
    }

    private static int getSubstringEndIndex(String needReplaceStr) {
        String dateFormatStr = needReplaceStr.substring(needReplaceStr.indexOf(KingBaseConstant.SYMBOL_MARK) + 1,
                needReplaceStr.lastIndexOf(KingBaseConstant.SYMBOL_MARK));
        if (dateFormatStr.endsWith(KSDateFormatEnum.YEAR.getOriginalSuffix())) {
            return KSDateFormatEnum.YEAR.getEndIndex();
        }
        if (dateFormatStr.endsWith(KSDateFormatEnum.MONTH.getOriginalSuffix())) {
            return KSDateFormatEnum.MONTH.getEndIndex();
        }
        if (dateFormatStr.endsWith(KSDateFormatEnum.DAY.getOriginalSuffix())) {
            return KSDateFormatEnum.DAY.getEndIndex();
        }
        if (dateFormatStr.endsWith(KSDateFormatEnum.SECOND.getOriginalSuffix())) {
            return KSDateFormatEnum.SECOND.getEndIndex();
        }
        return 0;
    }

    /**
     * 替换ifnull(->nvl(
     */
    private String ifNullReplace(String newSql) {
        return newSql.replaceAll(KingBaseConstant.IF_NULL_REGEX, KingBaseConstant.IF_NULL_NEW);
    }

    /**
     * 递归替换用“level”替换level函数
     * @param sql 原始sql
     * @return 替换后的sql
     */
    private String levelReplaceLoop(String sql) {
        // 去除换行符
        sql = sql.replaceAll(CommonConstant.SYMBOL_NEW_LINES, CommonConstant.SPACE);
        // 用正则将sql中的第一个 level语句替换为@，并以此分隔后长度不为2，则改语句没有level函数
        String replaceSql = sql.replaceFirst(KingBaseConstant.LEVEL_REGEX, CommonConstant.SEPARATOR);
        // sql语句去除头尾部空格
        String[] split = replaceSql.trim().split(CommonConstant.SEPARATOR);
        if (split.length != 2) {
            return sql;
        }
        // 找出替换出来的原sql片段
        String substring = sql.substring(split[0].length());
        String needReplaceStr = substring.replace(split[1], CommonConstant.EMPTY).trim();
        // 逻辑运算符select|.|，|where|when|and|or
        String logicalOperator = needReplaceStr.startsWith(CommonConstant.COMMA) ? CommonConstant.COMMA :
                needReplaceStr.startsWith(KingBaseConstant.SYMBOL_POINT) ? KingBaseConstant.SYMBOL_POINT :
                        needReplaceStr.substring(0, needReplaceStr.indexOf(CommonConstant.SPACE)) + CommonConstant.SPACE;

        return levelReplaceLoop(split[0] + CommonConstant.SPACE + logicalOperator + KingBaseConstant.LEVEL_NEW + split[1]);
    }
}

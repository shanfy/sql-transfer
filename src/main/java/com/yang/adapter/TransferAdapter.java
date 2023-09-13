package com.yang.adapter;


import com.yang.constant.CommonConstant;
import com.yang.enums.SqlCommandType;
import com.yang.exception.BusinessException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

/**
 * sql抽象适配器
 * @author shanfy
 * @date 2023-08-25 11:38
 */
public abstract class TransferAdapter {


    public String transfer(String originalSql){
        String lowerCase = originalSql.toLowerCase();
        if (lowerCase.contains(CommonConstant.SORT_STR)){
            originalSql = sortStrReplace(originalSql);
        }
        if (lowerCase.contains(CommonConstant.SYMBOL_TOP_QUOTE)){
            originalSql = topQuoteReplace(originalSql);
        }
        try {
            Statement statement = CCJSqlParserUtil.parse(originalSql);
            if (statement instanceof CreateTable) {
                CreateTable createTable = (CreateTable) statement;
                // 各个列的定义，这里需要将列的类型转为具体数据库对象的类型
                List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
                Map<String, List<String>> listMap = transferColumnDefinitions(createTable, columnDefinitions);

                // 拼装建表sql+ 创建索引 + 注释
                StringBuilder sqlBuilder = new StringBuilder(createTable +";\n");
                if(MapUtils.isNotEmpty(listMap) && listMap.containsKey("comments")){
                    List<String> comments = listMap.get("comments");
                    if(CollectionUtils.isNotEmpty(comments)){
                        for (String comment : comments){
                            sqlBuilder.append(comment);
                        }
                    }
                }

                if(listMap.containsKey("indexes")){
                    List<String> indexes = listMap.get("indexes");
                    if(CollectionUtils.isNotEmpty(indexes)){
                        for (String index : indexes){
                            sqlBuilder.append(index);
                        }
                    }
                }
                System.out.println(">>>>:"+ sqlBuilder);
                return sqlBuilder.toString();
            } else if (statement instanceof Select) {
                // SQL语句为SELECT查询
                Select select = (Select) statement;

            }else if(statement instanceof Insert){
                // SQL语句为INSERT语句
                Insert insert = (Insert) statement;

            } else {
                // 其他未知类型
                // throw new BusinessException("can not support commandType : " + statement);
            }
        } catch (JSQLParserException ignore) {
            // throw new BusinessException("TransferAdapter Exception: " + e.getMessage());
        }
        return doTransfer(originalSql);
    }

    protected abstract Map<String, List<String>> transferColumnDefinitions(CreateTable createTable, List<ColumnDefinition> columnDefinitions);

    /**
     * 获取操作命令类型
     */
    private SqlCommandType getCommandType(String command) {
        Map<String, SqlCommandType> sqlCommandTypeMap = SqlCommandType.getCommandMap();
        if(sqlCommandTypeMap.containsKey(command)){
            return sqlCommandTypeMap.get(command);
        }
        return SqlCommandType.UNKNOWN;
    }

    protected abstract String doTransfer(String originSql);

    /**
     * 替换反引号`
     * meterRoom.`meter_id` = meter.`id` ==> meterRoom.meter_id = meter.id
     * @param originalSql 原sql
     * @return 替换后的sql
     */
    public String topQuoteReplace(String originalSql) {
        return originalSql.replaceAll(CommonConstant.SYMBOL_TOP_QUOTE, CommonConstant.EMPTY);
    }

    /**
     * 替换SIGNED
     * convert(room.parent_room_id ,SIGNED) ASC ==> convert(room.room_no USING gbk) ASC
     * @param originalSql 原sql
     * @return 替换后的sql
     */
    public String sortStrReplace(String originalSql) {
        return originalSql.replaceAll(CommonConstant.SORT_STR_REGEX,CommonConstant.SORT_STR_NEW);
    }
}

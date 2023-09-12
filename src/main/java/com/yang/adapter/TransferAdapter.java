package com.yang.adapter;


import com.yang.constant.CommonConstant;
import com.yang.enums.SqlCommandType;

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
        // 获取首字母到第一个空格之间的字符
        int firstSpaceIndex = originalSql.indexOf(" ");
        String command = originalSql.substring(0, firstSpaceIndex);
        SqlCommandType commandType = getCommandType(command);

        return doTransfer(originalSql, commandType);
    }

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

    protected abstract String doTransfer(String originSql, SqlCommandType commandType);

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

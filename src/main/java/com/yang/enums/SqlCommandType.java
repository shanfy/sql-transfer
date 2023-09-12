package com.yang.enums;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * sql操作枚举
 * @author shanfy
 * @date 2023-08-25 11:08
 */
public enum SqlCommandType {

    /**
     * 无法处理的未知类型
     */
    UNKNOWN,

    /**
     * 建表
     */
    CREATE,

    /**
     * 更改表结构
     */
    ALTER,

    /**
     * 插入
     */
    INSERT,

    /**
     * 更新
     */
    UPDATE,

    /**
     * 删除
     */
    DELETE,

    /**
     * 查询
     */
    SELECT,

    /**
     * 刷新
     */
    FLUSH,
    ;

    /**
     * 获取所有key值集合
     */
    public static Set<String> keySet(){
        Set<String> keys = new HashSet<>();
        for (SqlCommandType value : values()) {
            keys.add(value.name());
        }
        return keys;
    }

    public static Map<String, SqlCommandType> getCommandMap(){
        Map<String, SqlCommandType> resultMap = new HashMap<>(16);
        for (SqlCommandType value : values()) {
            resultMap.put(value.name(), value);
        }
        return resultMap;
    }

}
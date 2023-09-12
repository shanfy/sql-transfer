package com.yang.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库类型枚举
 * @author shanfy
 * @date 2023-08-25 11:08
 */
public enum DBTypeEnum {

    /**
     * 人大金仓
     */
    KINGBASE("kingbase", "人大金仓"),

    /**
     * 神通
     */
    OSCAR("oscar", "神通"),
    ;

    private String code;

    private String desc;

    DBTypeEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 获取所有key值集合
     */
    public static List<String> keys(){
        List<String> keys = new ArrayList<>();
        for (DBTypeEnum value : values()) {
            keys.add(value.getCode());
        }
        return keys;
    }
}

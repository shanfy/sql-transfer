package com.yang.adapter;

import com.yang.constant.CommonConstant;
import com.yang.enums.DBTypeEnum;
import com.yang.enums.SqlCommandType;
import com.yang.exception.BusinessException;
import com.yang.util.StrUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 国产化db类型配置工厂
 * @author shanfy
 * @date 2023-08-25 11:46
 */
@Component
public class TransferAdapterFactory {

    /**
     * 数据库类型-> 对应处理类
     */
    private static final Map<String, TransferAdapter> DB_TYPE_MAP;

    public static final int LIMIT_LENGTH = 5000;


    static{
        DB_TYPE_MAP = new HashMap<>(6);
        DB_TYPE_MAP.put(DBTypeEnum.KINGBASE.getCode(), new KingBaseAdapter());
        DB_TYPE_MAP.put(DBTypeEnum.OSCAR.getCode(), new OscarAdapter());
    }

    /**
     * 获取实际的数据库类型适配器
     */
    public static TransferAdapter getAdapter(String dbType) {
        if(StringUtils.isEmpty(dbType)){
            throw new BusinessException("dbType is null");
        }
        if(!DB_TYPE_MAP.containsKey(dbType)){
            throw new BusinessException("dbType adapter not exist");
        }
        return DB_TYPE_MAP.get(dbType);
    }

    /**
     * 转换sql
     * 暂不支持ddl
     * 注释也未排除，
     */
    public String transferSql(String originalSql, String dbType){
        if(StringUtils.isEmpty(originalSql)){
            return CommonConstant.EMPTY;
        }

        if(originalSql.length() > LIMIT_LENGTH){
            throw new BusinessException("sql is too long, exceed limited " + LIMIT_LENGTH);
        }

        if(StringUtils.isEmpty(dbType)){
            throw new BusinessException("dbType can not be null");
        }

        List<String> keys = DBTypeEnum.keys();
        if(!keys.contains(dbType)){
            throw new BusinessException("not support this dbType!");
        }

        if(originalSql.contains("--") || originalSql.contains("#")){
            throw new BusinessException("please remove -- or #");
        }

        // 去除多余空格
        originalSql = StrUtils.removeBlank(originalSql);

        // 暂简单以分号区分每一句sql
        List<String> oldSqlList = StrUtils.splitStringBySemicolon(originalSql);

        int size = oldSqlList.size();
        TransferAdapter adapter = getAdapter(dbType);
        List<String> newSqlList = new ArrayList<>(size);
        oldSqlList.stream().filter(StringUtils::isNotEmpty).forEach(originalSqlChild->{
            newSqlList.add(adapter.transfer(originalSqlChild));
        });
        return StringUtils.join(newSqlList, CommonConstant.SYMBOL_NEW_LINES);
    }

}

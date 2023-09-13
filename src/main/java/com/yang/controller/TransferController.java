package com.yang.controller;

import com.yang.adapter.TransferAdapterFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 转换controller
 * @author shanfy
 * @date 2023-09-13 10：53
 */
@Api(value = "sql转化接口", tags = "sql转化接口")
@RestController
@RequestMapping("/transfer")
public class TransferController {

    private static final Logger log = LoggerFactory.getLogger(TransferController.class);

    @Resource
    private TransferAdapterFactory transferAdapterFactory;

    @ApiOperation("获取转化后sql")
    @GetMapping("/sql")
    public String getTransferSql(@RequestParam("sql") String sql, @RequestParam("dbType") String dbType){
        log.info("sql: {}, dbType:{}" , sql, dbType);
        return transferAdapterFactory.transferSql(sql, dbType);
    }
}

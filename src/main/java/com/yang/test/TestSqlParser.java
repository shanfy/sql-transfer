package com.yang.test;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.io.StringReader;

public class TestSqlParser {

    public static void main(String[] args) throws JSQLParserException {
        String sql = "CREATE TABLE zk.office_staff_question (\n" +
                "                                       id varchar(36) NOT NULL COMMENT '主键',\n" +
                "                                       tag varchar(50) NOT NULL COMMENT '本次问卷申请标识',\n" +
                "                                       staff_id varchar(80) NOT NULL COMMENT '员工id',\n" +
                "                                       staff_org_id varchar(80) NOT NULL COMMENT '员工单位id',\n" +
                "                                       operate_type tinyint(1) DEFAULT NULL COMMENT '操作类型：1-立即参加，2-下次再说，3-不感兴趣',\n" +
                "                                       next_date datetime DEFAULT NULL COMMENT '下次日期',\n" +
                "                                       created_at datetime DEFAULT NULL COMMENT '创建时间',\n" +
                "                                       updated_at datetime DEFAULT NULL COMMENT '更新时间',\n" +
                "                                       created_by varchar(36) DEFAULT NULL COMMENT '创建人',\n" +
                "                                       updated_by varchar(36) DEFAULT NULL COMMENT '修改人',\n" +
                "                                       logic_delete tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0-否 1-是',\n" +
                "                                       PRIMARY KEY (id)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户问卷调查表';";

        String sql1 = "create index idx_organ_tree_area_id\n" +
                "    on sys_organ_tree (area_id);";
        //2、创建解析器
        // CCJSqlParserManager mgr = new CCJSqlParserManager();
        //3、使用解析器解析sql生成具有层次结构的java类
        // Statement stmt = mgr.parse(new StringReader(sql));

        Statement parse = CCJSqlParserUtil.parse(sql);
        System.out.println(parse);

    }


}

package com.yang.test;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.io.StringReader;

public class TestSqlParser {

    public static void main(String[] args) throws JSQLParserException {
        String sql = "CREATE TABLE office_special_organ\n" +
                "(\n" +
                "    id            varchar(32) not null comment '主键',\n" +
                "    organ_id      varchar(32) not null comment '对应单位id',\n" +
                "    organ_name    varchar(100) not null comment '单位名称',\n" +
                "    reply_organ   varchar(50) not null comment '批复单位',\n" +
                "    reply_date    date        not null comment '批复日期',\n" +
                "    leader_organ  varchar(50) not null comment '牵头单位',\n" +
                "    special_time  varchar(20) not null default '' comment '专班时限',\n" +
                "    leader_name   varchar(20) not null default '' comment '专班领导名称',\n" +
                "    leader_duty   varchar(20) not null default '' comment '专班领导职务',\n" +
                "    remark        varchar(500) comment '备注',\n" +
                "    create_org_id varchar(32) not null comment '创建单位id',\n" +
                "    created_by    varchar(36)          default null comment '创建人',\n" +
                "    updated_by    varchar(36)          default null comment '修改人',\n" +
                "    created_at    datetime             default null comment '创建时间',\n" +
                "    updated_at    datetime             default null comment '更新时间',\n" +
                "    logic_delete  tinyint(1)           default 0 comment '逻辑删除:0-否 1-是',\n" +
                "    primary KEY (id),\n" +
                "    INDEX idx_username (leader_name)\n" +
                ") ENGINE = InnoDB\n" +
                "  DEFAULT CHARSET = utf8 COMMENT ='专班管理信息表';";

        String sql1 = "create index idx_organ_tree_area_id\n" +
                "    on sys_organ_tree (area_id);";

        String sql3 = "CREATE TABLE office_special_organ\n" +
                "(\n" +
                "    id            VARCHAR(32 char) NOT NULL,\n" +
                "    organ_id      varchar(32 char) not null,\n" +
                "    organ_name    varchar(100 char) not null,\n" +
                "    reply_organ   varchar(50 char) not null,\n" +
                "    reply_date    date        not null,\n" +
                "    use_area       decimal(10, 2)       null,\n" +
                "    leader_organ  varchar(50 char) not null,\n" +
                "    special_time  varchar(20 char) not null,\n" +
                "    leader_name   varchar(20 char) not null,\n" +
                "    leader_duty   varchar(20 char) not null,\n" +
                "    remark        varchar(500 char),\n" +
                "    create_org_id varchar(32 char) not null,\n" +
                "    created_by    varchar(36 char),\n" +
                "    updated_by    varchar(36 char),\n" +
                "    created_at    TIMESTAMP(6) NULL,\n" +
                "    updated_at    TIMESTAMP(6) NULL,\n" +
                "    logic_delete  tinyint   default 0 ,\n" +
                "   CONSTRAINT \"office_special_organ_PKEY\" PRIMARY KEY (\"id\")\n" +
                ")WITH (\n" +
                "\tOIDS=FALSE\n" +
                ");";
        //2、创建解析器
        // CCJSqlParserManager mgr = new CCJSqlParserManager();
        //3、使用解析器解析sql生成具有层次结构的java类
        // Statement stmt = mgr.parse(new StringReader(sql));

        Statement parse = CCJSqlParserUtil.parse(sql);
        CreateTable createTable = (CreateTable) parse;
        System.out.println(parse);

    }


}

package com.yang.test;

import com.yang.adapter.TransferAdapterFactory;
import com.yang.enums.DBTypeEnum;

public class TestTransfer {
    public static void main(String[] args) {
        String sql = "INSERT INTO sys_content_show (id, platform, organ_id, type, dic_value, dic_name, is_editable, is_show, sort_no,\n" +
                "                              logic_delete, remark, created_at, updated_at, updated_by, created_by, dic_type)\n" +
                "VALUES ('100', null, 'default', 'MANAGER_MENU_LIST', '/office/room-record', '房间备案审批', 1, 'YES', 10, 'NO',\n" +
                "        'icon_11.png', null, null, null, null, null);";
        TransferAdapterFactory adapterFactory = new TransferAdapterFactory();
        System.out.println(adapterFactory.transferSql(sql, DBTypeEnum.KINGBASE.getCode()));
    }
}

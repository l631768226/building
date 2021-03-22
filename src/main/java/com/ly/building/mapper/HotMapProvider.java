package com.ly.building.mapper;

import com.ly.building.model.HotRule;
import org.apache.ibatis.jdbc.SQL;

public class HotMapProvider {

    public String getList(String tableName, HotRule hotRule){
        return new SQL(){
            {
                SELECT("*");
                FROM(tableName);
                if(hotRule.getLabel() != null){
                    WHERE("labelStr = " + hotRule.getLabel());
                }
                if(hotRule.getUserId() != null){
                    WHERE("userId = " + hotRule.getUserId());
                }
                if(hotRule.getMonth() != null){
                    WHERE("month = " + hotRule.getMonth());
                }
            }
        }.toString();
    }

}

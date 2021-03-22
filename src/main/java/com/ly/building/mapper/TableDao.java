package com.ly.building.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface TableDao {


    @Insert("insert into tableInfo (tableName, dataCount, type) values (#{tableName}, #{count}, #{type})")
    int insertIntoInfo(@Param("tableName")String tableName, @Param("count")int count, @Param("type")int type);
}

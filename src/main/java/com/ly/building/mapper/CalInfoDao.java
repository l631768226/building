package com.ly.building.mapper;

import com.ly.building.model.CalInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CalInfoDao {

    @Select("select * from calinfo where md5 = #{md5} and threshold = #{threshold}")
    CalInfo findByMd5(@Param("md5")String md5, @Param("threshold")String threshold);

    @Insert("insert into calinfo (md5, json, deeledJson, threshold) values (#{md5}, #{json}, #{deeledJson}, #{threshold})")
    int insertCal(@Param("md5")String md5,
                  @Param("json")String json,
                  @Param("deeledJson")String deeledJson,
                  @Param("threshold")String threshold);

}

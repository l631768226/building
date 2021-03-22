package com.ly.building.mapper;

import com.ly.building.model.SFData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface SFDataDao {

    @Select("select * from sfData where md5 = #{md5}")
    SFData findByMd5(@Param("md5")String md5);


    @Insert("insert into sfData (md5, anaResult, pointMap, countMap, userList) " +
            "values (#{md5}, #{anaResult}, #{pointMap}, #{countMap}, #{userList})")
    int insertSFData(@Param("md5")String md5,
                     @Param("anaResult")String anaResult,
                     @Param("pointMap")String pointMap,
                     @Param("countMap")String countMap,
                     @Param("userList")String userList);

    @Update("update sfData set periodInfo = #{periodInfo} where md5 = #{md5}")
    int updateSFData(@Param("md5")String md5,
                     @Param("periodInfo")String periodInfo);
}

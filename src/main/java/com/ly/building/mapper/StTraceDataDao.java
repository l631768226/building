package com.ly.building.mapper;

import com.ly.building.model.TraceData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface StTraceDataDao {

    @Select("select * from stTraceData where md5 = #{md5}")
    TraceData findByMd5(@Param("md5") String md5);


    @Insert("insert into stTraceData (md5, pathList, pointList, userPath, preData) values " +
            "(#{md5}, #{pathList}, #{pointList}, #{userPath}, #{preData})")
    int insertPreData(@Param("md5") String md5, @Param("pathList") String pathList,
                      @Param("pointList") String pointList, @Param("userPath") String userPath,
                      @Param("preData") String preData);

    @Update("update stTraceData set proData = #{proData}, deeledPathData = #{deeledPathData}, deeledPointData = #{deeledPointData} " +
            "where md5 = #{md5}")
    int updateProData(@Param("md5") String md5,
                      @Param("proData") String proData,
                      @Param("deeledPathData") String deeledPathData,
                      @Param("deeledPointData") String deeledPointData);

}

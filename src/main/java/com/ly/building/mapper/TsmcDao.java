package com.ly.building.mapper;

import com.ly.building.model.Tsmc;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface TsmcDao {

    @Select("select * from ${tableName} where userId = #{userId} order by loca ASC")
    List<Tsmc> findByUserId(@Param("tableName") String tableName,
            @Param("userId")String userId);

    @Select("select * from ${tableName}")
    List<Tsmc> findList(@Param("tableName") String tableName);

    @Update("update ${tableName} set loca = #{loca} where id = ${id}")
    int updateLoca(@Param("tableName")String tableName,
                   @Param("loca")String loca,
                   @Param("id")String id);

    @Update({
        "<script>",
            "<foreach collection='tsmcList' item='item' index='index' separator=';'>",
                "update ${tableName} set loca = #{item.loca} where id = ${item.id}",
            "</foreach>",
        "</script>"
    })
    int updateDateTime(@Param("tableName") String tableName,
                       @Param("tsmcList") List<Tsmc> tsmcList);

    @Insert({
       "<script>",
            "insert into ${tableName} (userId, venueId, venueCateId, venueCateName, latitude, " +
                    "longtitude, timeZone, utc, loca, lat, lon) values",
            "<foreach collection='tsmcList' item = 'item' index='index' separator= ','>",
                "(#{item.userId}, #{item.venueId}, #{item.venueCateId}, #{item.venueCateName}, " +
                        "#{item.latitude}, #{item.longtitude}, #{item.timeZone}, #{item.utc}, #{item.loca}, " +
                        "#{item.lat}, #{item.lon})",
            "</foreach>",
       "</script>"
    })
    int insertDateTime(@Param("tableName")String tableName,
                       @Param("tsmcList")List<Tsmc> tsmcList);


}

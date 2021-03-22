package com.ly.building.mapper;

import com.ly.building.model.HotMapInfo;
import com.ly.building.model.Trace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TraceDao {

    @Insert({
            "<script>",
            "insert into ${tableName} (userId, lat, lon, dateS, timeS, " +
                    "timeStr, truth, pred, serialNum) values",
            "<foreach collection='traceList' item = 'item' index='index' separator= ','>",
            "(#{item.userId}, #{item.lat}, #{item.lon}, #{item.dateS}, " +
                    "#{item.timeS}, #{item.timeStr}, #{item.truth}, #{item.pred}, #{item.serialNum})",
            "</foreach>",
            "</script>"
    })
    int insertHopMap(@Param("tableName")String tableName,
                     @Param("traceList") List<Trace> traceList);

    @Select("select * from trace where userId = ${userId} limit 1000")
    List<Trace> findList(@Param("userId")int userId);

    @Select("select * from trace limit 1000")
    List<Trace> findAllList();

    @Select({
            "<script>",
                "select * from ${tableName} where 1 = 1 ",
                "<when test = 'userId!= null'>",
                    "AND userId = #{userId}",
                "</when>",
            "</script>"
    })
    List<Trace> findListByUser(@Param("tableName") String tableName, @Param("userId")Integer userId);

    @Select("select count(*) from trace")
    int selectCount();


    @Select({
            "<script>",
            "select * from trace where 1 = 1 ",
                "<when test = 'userId!= null'>",
                    "AND userId = #{userId}",
                "</when>",
                "<when test = 'travelType!= null'>",
                    "AND pred = #{travelType}",
                "</when>",
            "</script>"
    })
    List<Trace> findByRule(@Param("userId")Integer userId, @Param("travelType")Integer travelType);

    /**
     * 获取某一个日期之前的日期字符串列表
     * @param dateStr
     * @return
     */
    @Select({
        "<script>",
            "select dateStr from trace ",
            "<when test = 'dateS != null'>",
                "dateS &lt;= #{dateS} ",
            "</when>",
            " group by dateS order by dateS ",
        "</script>"
    })
    List<String> findDateList(@Param("dateStr")String dateStr);

}

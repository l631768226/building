package com.ly.building.mapper;

import com.ly.building.model.HotMapInfo;
import com.ly.building.model.HotRule;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

public interface HotMapDao {

    @Insert({
            "<script>",
            "insert into ${tableName} (userId, year, season, month, day, " +
                    "week, workday, hour1, hour2, lat, lon, placeName, labelStr, " +
                    "weight, cluster, tag, timeStr, dateStr) values",
            "<foreach collection='hotList' item = 'item' index='index' separator= ','>",
            "(#{item.userId}, #{item.year}, #{item.season}, #{item.month}, " +
                    "#{item.day}, #{item.week}, #{item.workday}, #{item.hour1}, #{item.hour2}, " +
                    "#{item.lat}, #{item.lon}, #{item.placeName}, #{item.labelStr}, #{item.weight}, " +
                    "#{item.cluster}, #{item.tag}, #{item.timeStr}, #{item.dateStr})",
            "</foreach>",
            "</script>"
    })
    int insertHopMap(@Param("tableName")String tableName,
                       @Param("hotList")List<HotMapInfo> hotList);


    @Select("select * from ${tabelName} where userId = ${userId}")
    List<HotMapInfo> findListByUser(@Param("tabelName") String tabelName, @Param("userId") Integer userId);

    @Select("select * from ${tabelName} where labelStr = ${label}")
    List<HotMapInfo> findListByLabel(@Param("tabelName") String tabelName, @Param("label") String label);

    @Select("select * from ${tabelName} where userId = ${userId} and labelStr = #{label}")
    List<HotMapInfo> findList(@Param("tabelName") String tabelName,
                              @Param("userId") Integer userId,
                              @Param("label") String label);

    @SelectProvider(type = HotMapProvider.class, method = "getList")
    List<HotMapInfo> findListByRule(HotRule hotRule);



    @Select({
      "<script>",
        "select * from ${tableName} where 1 = 1 ",
            "<when test = 'labelStr!=null' >",
                "AND labelStr = #{labelStr}",
            "</when>",
            "<when test = 'userId!=null' >",
                "AND userId = #{userId}",
            "</when>",
            "<when test = 'month!=null' >",
                "AND month = #{month}",
            "</when>",
            "<when test = 'dateStr!=null' >",
                "AND dateStr = #{dateStr}",
            "</when>",
            " order by userId asc, timeStr asc ",
      "</script>"
    })
    List<HotMapInfo> findListByRules(@Param("tableName")String tableName,
                                     @Param("userId")Integer userId,
                                     @Param("labelStr")String labelStr,
                                     @Param("month")Integer month,
                                     @Param("dateStr")String dateStr);


    @Select("select * from ${tableName} limit 500")
    List<HotMapInfo> findAllListByRules(@Param("tableName")String tableName);

    /**
     * 获取所有行动标签
     * @param tableName 表名
     * @return
     */
    @Select("select labelStr from #{tableName} group by labelStr ORDER BY labelStr ASC")
    List<String> findLabelList(@Param("tableName")String tableName);

    /**
     * 查询某个日期之前的日期信息列表
     * @param tableName 表名
     * @param dateStr 日期信息字符串
     * @return
     */
    @Select({
        "<script>",
            "select dateStr from ${tableName} ",
            "<when test = 'dateStr != null'>",
                "where dateStr &lt;= #{dateStr}",
            "</when>",
            "<when test = 'userId != null'>",
                "where userId = #{userId}",
            "</when>",
            "<when test = 'labelStr != null'>",
                "where labelStr = #{labelStr}",
            "</when>",
            "group by dateStr order by dateStr ASC",
        "</script>"
    })
    List<String> findDateList(@Param("tableName")String tableName,
                              @Param("dateStr")String dateStr,
                              @Param("userId")Integer userId,
                              @Param("labelStr")String labelStr);

}

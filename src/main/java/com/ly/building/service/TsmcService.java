package com.ly.building.service;

import com.ly.building.common.CommonMethod;
import com.ly.building.mapper.HotMapDao;
import com.ly.building.mapper.TraceDao;
import com.ly.building.mapper.TsmcDao;
import com.ly.building.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
public class TsmcService {

    @Autowired
    private TsmcDao tsmcDao;

    @Autowired
    private TraceDao traceDao;

    @Autowired
    private HotMapDao hotMapDao;

    public String uploadFile(HttpServletRequest request) {

        String tableName = request.getParameter("tableName");
        try {
            Collection<Part> parts = request.getParts();
            Iterator<Part> it = parts.iterator();

            Part tmpPart = it.next();
            InputStream ins = tmpPart.getInputStream();
            InputStreamReader read = new InputStreamReader(
                    ins, "UTF-8");//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            List<Tsmc> tsmcList = new ArrayList<>();
            while ((lineTxt = bufferedReader.readLine()) != null) {
                String data[] = lineTxt.split("\t");
                if (data.length > 0) {
                    Tsmc tsmc = new Tsmc();
                    tsmc.setUserId(data[0]);
                    tsmc.setVenueId(data[1]);
                    tsmc.setVenueCateId(data[2]);
                    tsmc.setVenueCateName(data[3]);
                    double latitude = Double.valueOf(data[4]);
                    tsmc.setLatitude(latitude);
                    double longtitude = Double.valueOf(data[5]);
                    tsmc.setLongtitude(longtitude);
                    tsmc.setTimeZone(data[6]);
                    tsmc.setUtc(data[7]);
                    String dateStr = CommonMethod.UTCtoStr(data[7]);
                    tsmc.setLoca(dateStr);

                    BigInteger lat = BigInteger.valueOf(Long.valueOf((long) (latitude * Math.pow(10, 14))));
                    tsmc.setLat(lat);
                    BigInteger lon = BigInteger.valueOf(Long.valueOf((long) (longtitude * Math.pow(10, 14))));
                    tsmc.setLon(lon);
                    tsmcList.add(tsmc);
                } else {
                    System.out.println("error");
                }
            }
            read.close();

            int result = tsmcDao.insertDateTime(tableName, tsmcList);
            if (result > 0) {
                return "OK";
            } else {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public List<List<Integer>> processTracList(TraceRule traceRule) {
        List<List<Integer>> resultList = new ArrayList<>();

        Integer zoneType = traceRule.getZoneType();

        String tableName = "";

        if (zoneType == null || zoneType == 1) {
            tableName = "nyinfo";
        } else {
            tableName = "tkinfo";
        }

        Integer userId = traceRule.getUserId();

        List<HotMapInfo> hotMapInfos = hotMapDao.findListByRules(tableName, userId, null, null, null);


        List<Integer> longList = new ArrayList<>();
        Integer oldLat = null;
        Integer oldLon = null;
        boolean startTag = true;
        boolean changeTag = false;
        String startTime = "";

        if (!hotMapInfos.isEmpty()) {
            for (HotMapInfo hi : hotMapInfos) {
                Double la = hi.getLat()*10000;
                Double lo = hi.getLon()* 10000;
                Integer lat = la.intValue();
                Integer lon = lo.intValue();
                String nowTime = hi.getTimeStr();

//                if (nowTime.equals(startTime)) {
//                    startTag = false;
//                    changeTag = false;
//                } else {
//                    startTag = true;
//                    changeTag = true;
//                }


                if (startTag) {
//                    if(changeTag){
//                        if(!longList.isEmpty()){
//                            List<Integer> newList = new ArrayList<>();
//                            for(Integer bi : longList){
//                                newList.add(bi);
//                            }
//                            resultList.add(newList);
//
//                            longList.clear();
//                        }
//                    }
                    longList.add(lon);
                    longList.add(lat);
                    startTag = false;
                } else {
                    Integer latDiff = lat - oldLat;

                    Integer lonDiff = lon - oldLon;

                    longList.add(lonDiff);
                    longList.add(latDiff);

                }
                startTime = nowTime;
                oldLat = lat;
                oldLon = lon;
            }

            if (!changeTag) {
                resultList.add(longList);
            }

        }

        return resultList;
    }


    public List<List<Integer>> processList(TraceRule traceRule) {
//        RstData<List<List<Integer>>> resultList = new RstData<>();
        List<List<Integer>> points = new ArrayList<>();

        List<Integer> longList = new ArrayList<>();

        List<Trace> traces = new ArrayList<>();

        Integer userId = traceRule.getUserId();

        if (userId == null || userId == -1) {
            userId = null;
        }

        Integer travelType = traceRule.getTravelType();
        if(travelType == null || travelType == -1){
            travelType = null;
        }

        traces = traceDao.findListByUser("trace", userId);

        boolean startTag = true;
        boolean changeTag = false;

        Integer oldLat = null;
        Integer oldLon = null;

        int oldPred = -1;

        if (traces != null) {
            for (Trace trace : traces) {
                double lng = trace.getLon() * 100000;
                double lat = trace.getLat() * 100000;

                Integer biLng = ((Double) Math.ceil(lng)).intValue();
                Integer biLat = ((Double) Math.ceil(lat)).intValue();

                int pred = trace.getPred();

                if (oldPred == pred) {
                    startTag = false;
                    changeTag = false;
                } else {
                    startTag = true;
                    changeTag = true;
                }

                if (startTag) {

                    if (changeTag) {
                        if (!longList.isEmpty()) {
                            List<Integer> longListCache = new ArrayList<>();
                            for (Integer bi : longList) {
                                longListCache.add(bi);
                            }
                            points.add(longListCache);
                            longList.clear();
                        }
                    }

                    longList.add(biLng);
                    longList.add(biLat);

                } else {
                    Integer latDiff = biLat - oldLat;
                    Integer lonDiff = biLng - oldLon;

                    longList.add(lonDiff);
                    longList.add(latDiff);
                }
                oldLat = biLat;
                oldLon = biLng;
                oldPred = pred;
            }
            if (!changeTag) {
                points.add(longList);
            }

//            resultList.setCode(1);
//            resultList.setData(points);

        } else {
//            resultList.setCode(-1);
        }

        return points;
    }
}

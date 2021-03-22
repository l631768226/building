package com.ly.building.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ly.building.common.CommonMethod;
import com.ly.building.mapper.StTraceDataDao;
import com.ly.building.mapper.TraceDao;
import com.ly.building.mapper.TraceDataDao;
import com.ly.building.model.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.*;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class TraceService {

    private Gson gson = new Gson();

    @Autowired
    private TraceDao traceDao;

    @Autowired
    private TraceDataDao traceDataDao;

    @Autowired
    private StTraceDataDao stTraceDataDao;

    @Value("${tra.filePath}")
    String filePath;

    @Value("${tra.flaskUrl}")
    String flaskUrl;

    @Value("${tra.flaskUrl37}")
    String flaskUrl37;

    @Value("${tra.pyModelPath}")
    String pyModelPath;

    public RstData<String> processInit(HttpServletRequest request) {
        RstData<String> rstData = new RstData<>();
        try {
            Part part = request.getPart("file");
//            String value = request.getParameter("value");
////
////            System.out.println("the value name = " + value);
////
////            UploadData uploadData = gson.fromJson(value, new TypeToken<UploadData>(){}.getType());
////            int type = uploadData.getType();
            String tableName = "trace";
////
////            System.out.println("the table name = " + tableName);

            InputStream ins = part.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            String line = null;

            List<Trace> traces = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                try {
                    String item[] = line.split(",");//CSV格式文件时候的分割符,我使用的是,号
                    Trace trace = new Trace();
                    trace.setUserId(Integer.valueOf(item[0]));

                    int serialNum = Integer.valueOf(item[1]);

                    trace.setSerialNum(serialNum);
                    trace.setLat(Double.valueOf(item[2]));
                    trace.setLon(Double.valueOf(item[3]));
                    String dateS = item[4];
                    String timeS = item[5];
//                    String timeStr = CommonMethod.getTheTime(dateS, timeS);
                    String timeStr = dateS + " " + timeS;
//                    System.out.println("timeS " + timeS + " timeStgr = " + timeStr );
                    trace.setTimeStr(timeStr);
                    trace.setDateS(dateS);
                    trace.setTimeS(timeS);
                    trace.setTruth(Integer.valueOf(item[6]));
                    trace.setPred(Integer.valueOf(item[7]));
                    traces.add(trace);

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

            if (!traces.isEmpty()) {
                //创建对应的数据表
//                boolean tableTag = CommonMethod.createHotMapTable(dbAddress, dbName, userName, password, tableName);

//                if(tableTag){
                //建表成功
//                int resultTag = tableDao.insertIntoInfo(tableName, hotMapInfos.size(), type);

//                if(resultTag > 0){
                //批量插入

                int resultCount = traceDao.insertHopMap(tableName, traces);
                if (resultCount > 0) {
                    rstData.setCode(1);
                } else {
                    rstData.setCode(-4);
                }

            } else {
                rstData.setCode(-3);
            }


//                }else{
//
//                }


//            }else{
//                rstData.setCode(-1);
//            }


        } catch (IOException e) {
            e.printStackTrace();
            rstData.setCode(-2);
        } catch (ServletException e) {
            e.printStackTrace();
            rstData.setCode(-2);
        }
        return rstData;
    }

    public RstData<TraceResult> processList(TraceRule data) {
        RstData<TraceResult> rstData = new RstData<>();
        String tableName = "";
        Integer userId = data.getUserId();

        TraceResult traceResult = new TraceResult();

        int zoneType = data.getZoneType();
        if (zoneType == 1) {
            tableName = "nyinfo";
        } else {
            tableName = "tkinfo";
        }

        List<Trace> traces = new ArrayList<>();

        if (userId != null) {
            traces = traceDao.findList(userId);
        } else {
            traces = traceDao.findAllList();
        }

        if (traces.isEmpty()) {
            rstData.setCode(-1);
        } else {

            List<List<GeoPoint>> points = new ArrayList<>();
            List<TracePoint> tracePoints = new ArrayList<>();

            List<GeoPoint> geos = new ArrayList<>();
            boolean startTag = true;
            boolean changeTag = false;
            int oldPred = traces.get(0).getPred();

            for (Trace trace : traces) {
                double lng = trace.getLon();
                double lat = trace.getLat();

                int pred = trace.getPred();

                GeoPoint geoPoint = new GeoPoint();
                geoPoint.setLng(lng);
                geoPoint.setLat(lat);

                Geo geo = new Geo();
                geo.setCount(trace.getPred());
                geo.setLat(lat);
                geo.setLng(lng);


                if (oldPred == pred) {
                    startTag = true;
                    changeTag = false;
                } else {
                    startTag = false;
                    changeTag = true;
                }

                oldPred = pred;

                if (startTag) {
                    geos.add(geoPoint);
                } else {
                    List<GeoPoint> newGeos = new ArrayList<>();
                    if (geos.size() > 1) {
                        for (GeoPoint geo1 : geos) {
                            GeoPoint newGeo = new GeoPoint();
                            BeanUtils.copyProperties(geo1, newGeo);
                            newGeos.add(newGeo);
                        }
                        points.add(newGeos);
                    }

                    geos.clear();
                }

                TracePoint tracePoint = new TracePoint();
                tracePoint.setGeo(geo);
                tracePoint.setInfo(trace.getTimeStr());

                tracePoints.add(tracePoint);
            }
            if (!changeTag) {
                points.add(geos);
            }
            traceResult.setMarkers(tracePoints);
            traceResult.setPoints(points);

            rstData.setCode(1);
            rstData.setData(traceResult);
        }

        return rstData;
    }

    public List<List<Double>> processTest() {

        List<List<Double>> resultList = new ArrayList<>();

        List<Trace> traceList = traceDao.findAllList();


        int count = 0;

        DecimalFormat df = new DecimalFormat("#.0000000");

        if (!traceList.isEmpty()) {
            for (Trace trace : traceList) {

//                if(count % 10 == 0){
                List<Double> lonlatList = new ArrayList<>();

                double lat = trace.getLat();
                double lon = trace.getLon();

                lonlatList.add(Double.valueOf(df.format(lat)));
                lonlatList.add(Double.valueOf(df.format(lon)));

                resultList.add(lonlatList);
//                }
                count++;
            }
        }

        return resultList;

    }

    public RstPointData<List<List<TravelPoint>>, List<TravelPoint>> processTraceList(TraceRule data) {

        RstPointData<List<List<TravelPoint>>, List<TravelPoint>> rstData = new RstPointData<>();

        List<List<TravelPoint>> resultList = new ArrayList<>();

        List<TravelPoint> pointResultList = new ArrayList<>();

        Integer userId = data.getUserId();
        Integer travelType = data.getTravelType();

        if (userId == null || userId == -1) {
            userId = null;
        }
        //0 walk  1 bike  2 car  3 taxi  4 subway/train
        if (travelType == null || travelType == -1) {
            travelType = null;
        }
        List<Trace> traceList = traceDao.findByRule(userId, travelType);
        if (!traceList.isEmpty()) {

            List<TravelPoint> tpList = new ArrayList<>();

            boolean changeTag = false;
            int oldNum = traceList.get(0).getSerialNum();
            int oldUId = traceList.get(0).getUserId();
            int count = 0;
            for (Trace trace : traceList) {
                TravelPoint tp = new TravelPoint();
                tp.setLat(trace.getLat());
                tp.setLng(trace.getLon());
                tp.setWeight(trace.getPred());
                tp.setUserId(trace.getUserId());
                tp.setDateS(trace.getDateS());
                tp.setTimeS(trace.getTimeS());
                tp.setTimeStr(trace.getTimeStr());

                int serialNum = trace.getSerialNum();
                int uId = trace.getUserId();
                if (oldNum == serialNum && oldUId == uId) {
                    //当前点与前一个点属于同一个人同一条路径
                    tpList.add(tp);
                    changeTag = false;
                } else {
                    changeTag = true;
                    //当前点与前一个点属于不同路径，则需要将之前的路径视为完结并保存到结果集中，当前点另起一个list进行收录
                    List<TravelPoint> newTPList = new ArrayList<>();
                    for (TravelPoint travelPoint : tpList) {
                        newTPList.add(travelPoint);
                    }
                    TravelPoint startPoint = newTPList.get(0);
                    startPoint.setFromTag(count);
                    startPoint.setLineTag("start");
                    pointResultList.add(startPoint);

                    TravelPoint endPoint = newTPList.get(newTPList.size() - 1);
                    endPoint.setFromTag(count);
                    endPoint.setLineTag("end");
                    pointResultList.add(endPoint);
                    resultList.add(newTPList);
                    tpList.clear();

                    count++;
                }
                oldNum = serialNum;
                oldUId = uId;
            }
            if (!changeTag) {
                //最后的整理的路径需要加入结果集中
                resultList.add(tpList);

                TravelPoint startPoint = tpList.get(0);
                startPoint.setFromTag(count);
                startPoint.setLineTag("start");
                pointResultList.add(startPoint);

                TravelPoint endPoint = tpList.get(tpList.size() - 1);
                endPoint.setFromTag(count);
                endPoint.setLineTag("end");
                pointResultList.add(endPoint);

            }
            resultList.add(new ArrayList<TravelPoint>());
            rstData.setCode(1);
            rstData.setData(resultList);
            rstData.setPoints(pointResultList);
        } else {
            System.out.println("empty");
            rstData.setCode(1);
            rstData.setData(resultList);
            rstData.setPoints(pointResultList);
        }

        return rstData;
    }

    public RstData<PathInfo> processUploadFile(HttpServletRequest request) {

        RstData<PathInfo> rstData = new RstData<>();
        PathInfo pathInfo = new PathInfo();

        String md5 = "";

        try {
            Collection<Part> parts = request.getParts();
            Iterator<Part> it = parts.iterator();

            boolean tag = true;

            while (it.hasNext()) {
                Part tmpPart = it.next();

                if (tmpPart.getName().equals("file") && tmpPart.getSize() > 0) {

                    String tmpFileName = tmpPart.getSubmittedFileName();

                    System.out.println("fileName = " + tmpFileName);

                    if (tmpFileName == null) {
                        tag = false;
                        continue;

                    }

                    InputStream ins = tmpPart.getInputStream();
                    OutputStream os = null;

                    try {
                        byte[] buffer = new byte[1000];
                        int len;

                        File file = new File(filePath);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        os = new FileOutputStream(filePath);
                        while ((len = ins.read(buffer)) > -1) {
                            os.write(buffer, 0, len);
                        }

                        InputStream insCopy = new FileInputStream(file);

                        md5 = DigestUtils.md5Hex(insCopy);

                        System.out.println("md5 = " + md5);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            os.close();
                            ins.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            File file = new File(filePath);
            if (!file.exists() || !tag) {
                rstData.setCode(-1);
                return rstData;
            }

            List<List<TravelPoint>> resultList = new ArrayList<>();
            List<TravelPoint> pointResultList = new ArrayList<>();
            List<UserPath> userPaths = new ArrayList<>();
            List<TraceInfo> traceInfos = new ArrayList<>();
            pathInfo.setMd5(md5);

            TraceData traceData = traceDataDao.findByMd5(md5);

            if(traceData == null) {

                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                List<TravelPoint> tpList = new ArrayList<>();

                boolean changeTag = false;
                String oldNum = "";
                String oldUId = "";
                int count = 0;
                int startTag = 0;
                String curUserId = "";
                Set<String> traceSet = new HashSet<>();

                while ((lineTxt = bufferedReader.readLine()) != null) {
//                System.out.println(lineTxt);
                    String data[] = lineTxt.split(",");
                    String userId = data[0];
                    if (userId == null || "user_id".equals(userId)) {
                        continue;
                    }
                    String traceId = data[1];
                    Double latitude = Double.valueOf(data[2]);
                    Double longtitude = Double.valueOf(data[3]);
                    Double relative = Double.valueOf(data[4]);
                    String date = data[5];
                    String time = data[6];
                    String dateStr = date + " " + time;

                    TraceInfo ti = new TraceInfo();
                    ti.setUserId(userId);
                    ti.setDate(date);
                    ti.setTime(time);
                    ti.setDateStr(dateStr);
                    ti.setLatitude(latitude);
                    ti.setRelative(relative);
                    ti.setLongtitude(longtitude);
                    ti.setTraceId(traceId);
                    traceInfos.add(ti);

                    TravelPoint tp = new TravelPoint();
                    tp.setLat(latitude);
                    tp.setLng(longtitude);
                    tp.setWeight(-1);
                    tp.setFromTag(Integer.valueOf(traceId));
                    tp.setUserId(Integer.valueOf(userId));
                    tp.setDateS(date);
                    tp.setTimeS(time);
                    tp.setTimeStr(dateStr);

                    if (oldNum.equals(traceId) && oldUId.equals(userId) || startTag == 0) {
                        //当前点与前一个点属于同一个人同一条路径
                        tpList.add(tp);
                        changeTag = false;
                        if (startTag == 0) {
                            traceSet.add(traceId);
                            startTag = -1;
                        }
                    } else {
                        changeTag = true;

                        if (!oldUId.equals(userId)) {
                            //若人员id不同，则需要将人与路径信息录入到结果中
                            UserPath userPath = new UserPath();
                            userPath.setUserId(oldUId);
                            List<Integer> cacheList = new ArrayList<>();
                            for (String str : traceSet) {
                                cacheList.add(Integer.valueOf(str));
                            }
                            Collections.sort(cacheList);
                            userPath.setPathIds(cacheList);
                            traceSet.clear();
                            userPaths.add(userPath);
                        }

                        if (!oldNum.equals(traceId)) {
                            traceSet.add(traceId);
                        }

                        //当前点与前一个点属于不同路径，则需要将之前的路径视为完结并保存到结果集中，当前点另起一个list进行收录
                        List<TravelPoint> newTPList = new ArrayList<>();
                        for (TravelPoint travelPoint : tpList) {
                            newTPList.add(travelPoint);
                        }
                        TravelPoint startPoint = newTPList.get(0);
//                        startPoint.setFromTag(count);
                        startPoint.setLineTag("start");
                        pointResultList.add(startPoint);

                        TravelPoint endPoint = newTPList.get(newTPList.size() - 1);
//                        endPoint.setFromTag(count);
                        endPoint.setLineTag("end");
                        pointResultList.add(endPoint);
                        resultList.add(newTPList);

                        tpList.clear();

                        count++;

                    }
                    oldNum = traceId;
                    oldUId = userId;
                }
                if (!changeTag) {
                    //最后的整理的路径需要加入结果集中
                    resultList.add(tpList);

                    TravelPoint startPoint = tpList.get(0);
                    startPoint.setFromTag(count);
                    startPoint.setLineTag("start");
                    pointResultList.add(startPoint);

                    TravelPoint endPoint = tpList.get(tpList.size() - 1);
                    endPoint.setFromTag(count);
                    endPoint.setLineTag("end");
                    pointResultList.add(endPoint);

                    UserPath userPath = new UserPath();
                    userPath.setUserId(oldUId);
                    List<Integer> cacheList = new ArrayList<>();
                    for (String str : traceSet) {
                        cacheList.add(Integer.valueOf(str));
                    }
                    Collections.sort(cacheList);
                    userPath.setPathIds(cacheList);
                    userPaths.add(userPath);
                }

                read.close();

                String preDataStr = gson.toJson(traceInfos);
                String userPathStr = gson.toJson(userPaths);
                String pathListStr = gson.toJson(resultList);
                String pointListStr = gson.toJson(pointResultList);

                traceDataDao.insertPreData(md5, pathListStr, pointListStr, userPathStr, preDataStr);
            }else{
                String preDataStr = traceData.getPreData();
                String userPathStr = traceData.getUserPath();
                String pathListStr = traceData.getPathList();
                String pointListStr = traceData.getPointList();


                resultList = gson.fromJson(pathListStr, new TypeToken<List<List<TravelPoint>>>(){}.getType());
                userPaths = gson.fromJson(userPathStr, new TypeToken<List<UserPath>>(){}.getType());
                traceInfos = gson.fromJson(preDataStr, new TypeToken<List<TraceInfo>>(){}.getType());
                pointResultList = gson.fromJson(pointListStr, new TypeToken<List<TravelPoint>>(){}.getType());
            }
            pathInfo.setPathInfos(resultList);
            pathInfo.setPathPoints(pointResultList);
            pathInfo.setUserPaths(userPaths);

            rstData.setCode(1);
            rstData.setData(pathInfo);

        } catch (ServletException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rstData;
    }

    public RstData<PathInfo> processDeel(QTraceDeelData data) {

        RstData<PathInfo> rstData = new RstData<>();

        String md5 = data.getMd5();
        PathInfo pathInfo = new PathInfo();
        List<List<TravelPoint>> resultList = new ArrayList<>();
        List<TravelPoint> pointResultList = new ArrayList<>();
        List<UserPath> userPaths = new ArrayList<>();
        List<TraceInfo> traceInfos = new ArrayList<>();

        TraceData traceData = traceDataDao.findByMd5(md5);
        if(traceData == null){
            rstData.setCode(-1);
            rstData.setMsg("上传文件MD5有误");
        }else{
            String preDataStr = traceData.getPreData();
            String userPathStr = traceData.getUserPath();

            String pathListStr = traceData.getPathList();
            String pointListStr = traceData.getPointList();

            List<List<TravelPoint>> pathList = gson.fromJson(pathListStr, new TypeToken<List<List<TravelPoint>>>(){}.getType());
            List<TravelPoint> pointList = gson.fromJson(pointListStr, new TypeToken<List<TravelPoint>>(){}.getType());

            String proDataStr = traceData.getProData();
            if(proDataStr == null || "".equals(proDataStr)){
                traceInfos = gson.fromJson(preDataStr, new TypeToken<List<TraceInfo>>(){}.getType());
                List<FormParam> formParams = new ArrayList<>();
                FormParam fileFormParam = new FormParam();
                fileFormParam.setKey("filePath");
                fileFormParam.setValue(filePath);
                formParams.add(fileFormParam);
                String resultStr = CommonMethod.postFormJson(flaskUrl + "/path", formParams);

                System.out.println("res = " + resultStr);
                if ("fail".equals(resultStr) || "error".equals(resultStr)) {
                    System.out.println(resultStr);
                    rstData.setCode(-1);
                    return rstData;
                } else {
                    //[{"用户ID": 0, "轨迹编号": 1, "出行方式": "car", "平均速度": 27.3399, "平均加速度": -0.3701,
                    // "平均加加速度": 0.089, "平均方位变化角": 32.4568, "移动总距离": 343328.8162,
                    // "起始时间": "2007-04-29/22:27:11", "结束时间": "2007-04-30/04:24:12", "持续时间": "05:57:00"}
                    resultStr = resultStr.replace("用户ID", "userId");
                    resultStr = resultStr.replace("轨迹编号", "traceId");
                    resultStr = resultStr.replace("出行方式", "travelMode");
                    resultStr = resultStr.replace("平均速度", "aveV");
                    resultStr = resultStr.replace("平均加速度", "aveA");
                    resultStr = resultStr.replace("平均加加速度", "aveAA");
                    resultStr = resultStr.replace("平均方位变化角", "aveDV");
                    resultStr = resultStr.replace("移动总距离", "distance");
                    resultStr = resultStr.replace("起始时间", "startTime");
                    resultStr = resultStr.replace("结束时间", "endTime");
                    resultStr = resultStr.replace("持续时间", "duringTime");
                    proDataStr = resultStr;
                    List<TraceRes> traceResList = gson.fromJson(resultStr, new TypeToken<List<TraceRes>>() {
                    }.getType());

                    if (traceResList.isEmpty()) {
                        rstData.setCode(-1);
                        return rstData;
                    } else {
                        for (TraceRes traceRes : traceResList) {
                            Integer userId = traceRes.getUserId();
                            Integer traceId = traceRes.getTraceId();
                            Integer travelMode = getMode(traceRes.getTravelMode());
                            //循环处理路径端点
                            for (TravelPoint travelPoint : pointList) {
                                if (userId == travelPoint.getUserId() && traceId == travelPoint.getFromTag()) {
                                    TravelPoint proTravelPoint = new TravelPoint();
                                    BeanUtils.copyProperties(travelPoint, proTravelPoint);
                                    proTravelPoint.setWeight(travelMode);
                                    pointResultList.add(proTravelPoint);
                                } else {
                                    continue;
                                }
                            }
                            //循环处理路径上的点
                            for (List<TravelPoint> tps : pathList) {
                                Integer tpUserId = tps.get(0).getUserId();
                                Integer tpTraceId = tps.get(0).getFromTag();
                                if (tpUserId == userId && tpTraceId == traceId) {
                                    List<TravelPoint> tpList = new ArrayList<>();
                                    for (TravelPoint tp : tps) {
                                        TravelPoint proTp = new TravelPoint();
                                        BeanUtils.copyProperties(tp, proTp);
                                        proTp.setWeight(travelMode);
                                        tpList.add(proTp);
                                    }
                                    resultList.add(tpList);
                                } else {
                                    continue;
                                }
                            }

                        }
                    }

                    String deeledPointDataStr = gson.toJson(pointResultList);
                    String deeledPathDataStr = gson.toJson(resultList);

                    traceDataDao.updateProData(md5, proDataStr, deeledPathDataStr, deeledPointDataStr);
                }
            }else{
                String deeledPointDataStr = traceData.getDeeledPointData();
                String deeledPathDataStr = traceData.getDeeledPathData();

                resultList = gson.fromJson(deeledPathDataStr, new TypeToken<List<List<TravelPoint>>>(){}.getType());
                pointResultList = gson.fromJson(deeledPointDataStr, new TypeToken<List<TravelPoint>>(){}.getType());
            }

            userPaths = gson.fromJson(userPathStr, new TypeToken<List<UserPath>>(){}.getType());

            pathInfo.setMd5(md5);
            pathInfo.setUserPaths(userPaths);
            pathInfo.setPathInfos(resultList);
            pathInfo.setPathPoints(pointResultList);
            rstData.setData(pathInfo);
            rstData.setCode(1);
        }

        return rstData;
    }





    private Integer getMode(String type){
        //0 walk  1 bike  2 car  3 taxi  4 subway/train
        Integer result = -1;
        switch(type){
            case "walk":
                result = 0;
                break;
            case "bike":
                result = 1;
                break;
            case "car":
                result = 2;
                break;
            case "taxi":
                result = 3;
                break;
            case "subway":
                result = 4;
                break;
            case "train":
                result = 4;
                break;
            case "hybrid mode":
                result = 5;
                break;
            default:
                result = -1;
        }
        return result;
    }

    public RstData<TraceRes> processInfo(TraceInfoReq data) {

        RstData<TraceRes> rstData = new RstData<>();
        TraceRes traceRes = new TraceRes();

        String md5 = data.getMd5();
        Integer userId = data.getUserId();
        Integer traceId = data.getTraceId();

        if(md5 == null || userId == null || traceId == null){
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }

        TraceData traceData = traceDataDao.findByMd5(md5);

        String proData = traceData.getProData();

        if(proData == null || "".equals(proData)){
            rstData.setCode(-2);
            rstData.setMsg("传入数据有误");
            return rstData;
        }else{
            List<TraceRes> traceResList = gson.fromJson(proData, new TypeToken<List<TraceRes>>(){}.getType());

            for(TraceRes tr : traceResList){
                Integer curUserId = tr.getUserId();
                Integer curTraceId = tr.getTraceId();
                if(curTraceId == traceId && curUserId == userId){
                    BeanUtils.copyProperties(tr, traceRes);
                    break;
                }
            }
        }

        rstData.setCode(1);
        rstData.setData(traceRes);
        return rstData;
    }

    public RstData<PathInfo> processSearch(TraceInfoReq data) {

        RstData<PathInfo> rstData = new RstData<>();
        PathInfo pathInfo = new PathInfo();
        List<List<TravelPoint>> resultList = new ArrayList<>();
        List<TravelPoint> pointResultList = new ArrayList<>();
        String md5 = data.getMd5();
        Integer userId = data.getUserId();
        Integer traceId = data.getTraceId();

        if(md5 == null){
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }

        TraceData traceData = traceDataDao.findByMd5(md5);

        String deeledPointDataStr = traceData.getDeeledPointData();
        String deeledPathDataStr = traceData.getDeeledPathData();

        List<List<TravelPoint>> pathList = gson.fromJson(deeledPathDataStr, new TypeToken<List<List<TravelPoint>>>(){}.getType());
        List<TravelPoint> pointList = gson.fromJson(deeledPointDataStr, new TypeToken<List<TravelPoint>>(){}.getType());

        if((userId == null || "".equals(userId)) && (traceId == null || "".equals(traceId))){
            resultList = pathList;
            pointResultList = pointList;
        }else if((userId == null || "".equals(userId)) && (traceId != null && !"".equals(traceId))){
            for (TravelPoint travelPoint : pointList) {
                if (traceId == travelPoint.getFromTag()) {
                    TravelPoint proTravelPoint = new TravelPoint();
                    BeanUtils.copyProperties(travelPoint, proTravelPoint);
                    pointResultList.add(proTravelPoint);
                } else {
                    continue;
                }
            }
            //循环处理路径上的点
            for (List<TravelPoint> tps : pathList) {
                Integer tpUserId = tps.get(0).getUserId();
                Integer tpTraceId = tps.get(0).getFromTag();
                if (tpTraceId == traceId) {
                    List<TravelPoint> tpList = new ArrayList<>();
                    for (TravelPoint tp : tps) {
                        TravelPoint proTp = new TravelPoint();
                        BeanUtils.copyProperties(tp, proTp);
                        tpList.add(proTp);
                    }
                    resultList.add(tpList);
                } else {
                    continue;
                }
            }
        }else if((userId != null && !"".equals(userId)) && (traceId == null || "".equals(traceId))){
            for (TravelPoint travelPoint : pointList) {
                if (userId == travelPoint.getUserId()) {
                    TravelPoint proTravelPoint = new TravelPoint();
                    BeanUtils.copyProperties(travelPoint, proTravelPoint);
                    pointResultList.add(proTravelPoint);
                } else {
                    continue;
                }
            }
            //循环处理路径上的点
            for (List<TravelPoint> tps : pathList) {
                Integer tpUserId = tps.get(0).getUserId();
                Integer tpTraceId = tps.get(0).getFromTag();
                if (tpUserId == userId) {
                    List<TravelPoint> tpList = new ArrayList<>();
                    for (TravelPoint tp : tps) {
                        TravelPoint proTp = new TravelPoint();
                        BeanUtils.copyProperties(tp, proTp);
                        tpList.add(proTp);
                    }
                    resultList.add(tpList);
                } else {
                    continue;
                }
            }
        }else{
            for (TravelPoint travelPoint : pointList) {
                if (userId == travelPoint.getUserId() && traceId == travelPoint.getFromTag()) {
                    TravelPoint proTravelPoint = new TravelPoint();
                    BeanUtils.copyProperties(travelPoint, proTravelPoint);
                    pointResultList.add(proTravelPoint);
                } else {
                    continue;
                }
            }
            //循环处理路径上的点
            for (List<TravelPoint> tps : pathList) {
                Integer tpUserId = tps.get(0).getUserId();
                Integer tpTraceId = tps.get(0).getFromTag();
                if (tpUserId == userId && tpTraceId == traceId) {
                    List<TravelPoint> tpList = new ArrayList<>();
                    for (TravelPoint tp : tps) {
                        TravelPoint proTp = new TravelPoint();
                        BeanUtils.copyProperties(tp, proTp);
                        tpList.add(proTp);
                    }
                    resultList.add(tpList);
                } else {
                    continue;
                }
            }
        }

        pathInfo.setPathPoints(pointResultList);
        pathInfo.setPathInfos(resultList);

        rstData.setData(pathInfo);
        rstData.setCode(1);
        return  rstData;
    }

    public RstData<PathInfo> processStUploadFile(HttpServletRequest request) {
        RstData<PathInfo> rstData = new RstData<>();
        PathInfo pathInfo = new PathInfo();

        String md5 = "";

        try {
            Collection<Part> parts = request.getParts();
            Iterator<Part> it = parts.iterator();

            boolean tag = true;

            String imgPath = pyModelPath + "/trajectory.csv";

            while (it.hasNext()) {
                Part tmpPart = it.next();

                if (tmpPart.getName().equals("file") && tmpPart.getSize() > 0) {

                    String tmpFileName = tmpPart.getSubmittedFileName();

                    System.out.println("fileName = " + tmpFileName);

                    if (tmpFileName == null) {
                        tag = false;
                        continue;

                    }

                    InputStream ins = tmpPart.getInputStream();
                    OutputStream os = null;

                    try {
                        byte[] buffer = new byte[1000];
                        int len;

                        File file = new File(imgPath);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        os = new FileOutputStream(imgPath);
                        while ((len = ins.read(buffer)) > -1) {
                            os.write(buffer, 0, len);
                        }

                        InputStream insCopy = new FileInputStream(file);

                        md5 = DigestUtils.md5Hex(insCopy);

                        System.out.println("md5 = " + md5);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            os.close();
                            ins.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            File file = new File(imgPath);
            if (!file.exists() || !tag) {
                rstData.setCode(-1);
                return rstData;
            }

            List<List<TravelPoint>> resultList = new ArrayList<>();
            List<TravelPoint> pointResultList = new ArrayList<>();
            List<UserPath> userPaths = new ArrayList<>();
            List<TraceInfo> traceInfos = new ArrayList<>();
            pathInfo.setMd5(md5);

            TraceData traceData = stTraceDataDao.findByMd5(md5);

            if(traceData == null) {

                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                List<TravelPoint> tpList = new ArrayList<>();

                boolean changeTag = false;
                String oldNum = "";
                String oldUId = "";
                int count = 0;
                int startTag = 0;
                String curUserId = "";
                Set<String> traceSet = new HashSet<>();

                while ((lineTxt = bufferedReader.readLine()) != null) {
//                System.out.println(lineTxt);
                    String data[] = lineTxt.split(",");
                    String userId = data[0];
                    if (userId == null || "user_id".equals(userId)) {
                        continue;
                    }
                    String traceId = data[1];
                    Double latitude = Double.valueOf(data[2]);
                    Double longtitude = Double.valueOf(data[3]);
                    Double relative = Double.valueOf(data[4]);
                    String date = data[5];
                    String time = data[6];
                    String dateStr = date + " " + time;

                    TraceInfo ti = new TraceInfo();
                    ti.setUserId(userId);
                    ti.setDate(date);
                    ti.setTime(time);
                    ti.setDateStr(dateStr);
                    ti.setLatitude(latitude);
                    ti.setRelative(relative);
                    ti.setLongtitude(longtitude);
                    ti.setTraceId(traceId);
                    traceInfos.add(ti);

                    TravelPoint tp = new TravelPoint();
                    tp.setLat(latitude);
                    tp.setLng(longtitude);
                    tp.setWeight(-1);
                    tp.setFromTag(Integer.valueOf(traceId));
                    tp.setUserId(Integer.valueOf(userId));
                    tp.setDateS(date);
                    tp.setTimeS(time);
                    tp.setTimeStr(dateStr);

                    if (oldNum.equals(traceId) && oldUId.equals(userId) || startTag == 0) {
                        //当前点与前一个点属于同一个人同一条路径
                        tpList.add(tp);
                        changeTag = false;
                        if (startTag == 0) {
                            traceSet.add(traceId);
                            startTag = -1;
                        }
                    } else {
                        changeTag = true;

                        if (!oldUId.equals(userId)) {
                            //若人员id不同，则需要将人与路径信息录入到结果中
                            UserPath userPath = new UserPath();
                            userPath.setUserId(oldUId);
                            List<Integer> cacheList = new ArrayList<>();
                            for (String str : traceSet) {
                                cacheList.add(Integer.valueOf(str));
                            }
                            Collections.sort(cacheList);
                            userPath.setPathIds(cacheList);
                            traceSet.clear();
                            userPaths.add(userPath);
                        }

                        if (!oldNum.equals(traceId)) {
                            traceSet.add(traceId);
                        }

                        //当前点与前一个点属于不同路径，则需要将之前的路径视为完结并保存到结果集中，当前点另起一个list进行收录
                        List<TravelPoint> newTPList = new ArrayList<>();
                        for (TravelPoint travelPoint : tpList) {
                            newTPList.add(travelPoint);
                        }
                        TravelPoint startPoint = newTPList.get(0);
//                        startPoint.setFromTag(count);
                        startPoint.setLineTag("start");
                        pointResultList.add(startPoint);

                        TravelPoint endPoint = newTPList.get(newTPList.size() - 1);
//                        endPoint.setFromTag(count);
                        endPoint.setLineTag("end");
                        pointResultList.add(endPoint);
                        resultList.add(newTPList);

                        tpList.clear();

                        count++;

                    }
                    oldNum = traceId;
                    oldUId = userId;
                }
                if (!changeTag) {
                    //最后的整理的路径需要加入结果集中
                    resultList.add(tpList);

                    TravelPoint startPoint = tpList.get(0);
                    startPoint.setFromTag(count);
                    startPoint.setLineTag("start");
                    pointResultList.add(startPoint);

                    TravelPoint endPoint = tpList.get(tpList.size() - 1);
                    endPoint.setFromTag(count);
                    endPoint.setLineTag("end");
                    pointResultList.add(endPoint);

                    UserPath userPath = new UserPath();
                    userPath.setUserId(oldUId);
                    List<Integer> cacheList = new ArrayList<>();
                    for (String str : traceSet) {
                        cacheList.add(Integer.valueOf(str));
                    }
                    Collections.sort(cacheList);
                    userPath.setPathIds(cacheList);
                    userPaths.add(userPath);
                }

                read.close();

                String preDataStr = gson.toJson(traceInfos);
                String userPathStr = gson.toJson(userPaths);
                String pathListStr = gson.toJson(resultList);
                String pointListStr = gson.toJson(pointResultList);

                stTraceDataDao.insertPreData(md5, pathListStr, pointListStr, userPathStr, preDataStr);
            }else{
                String preDataStr = traceData.getPreData();
                String userPathStr = traceData.getUserPath();
                String pathListStr = traceData.getPathList();
                String pointListStr = traceData.getPointList();


                resultList = gson.fromJson(pathListStr, new TypeToken<List<List<TravelPoint>>>(){}.getType());
                userPaths = gson.fromJson(userPathStr, new TypeToken<List<UserPath>>(){}.getType());
                traceInfos = gson.fromJson(preDataStr, new TypeToken<List<TraceInfo>>(){}.getType());
                pointResultList = gson.fromJson(pointListStr, new TypeToken<List<TravelPoint>>(){}.getType());
            }
            pathInfo.setPathInfos(resultList);
            pathInfo.setPathPoints(pointResultList);
            pathInfo.setUserPaths(userPaths);

            rstData.setCode(1);
            rstData.setData(pathInfo);

        } catch (ServletException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rstData;
    }

    public RstData<PathInfo> processStDeel(QTraceDeelData data) {

        RstData<PathInfo> rstData = new RstData<>();

        String md5 = data.getMd5();
        PathInfo pathInfo = new PathInfo();
        List<List<TravelPoint>> resultList = new ArrayList<>();
        List<TravelPoint> pointResultList = new ArrayList<>();
        List<UserPath> userPaths = new ArrayList<>();
        List<TraceInfo> traceInfos = new ArrayList<>();

        TraceData traceData = stTraceDataDao.findByMd5(md5);
        if(traceData == null){
            rstData.setCode(-1);
            rstData.setMsg("上传文件MD5有误");
        }else{
            String preDataStr = traceData.getPreData();
            String userPathStr = traceData.getUserPath();

            String pathListStr = traceData.getPathList();
            String pointListStr = traceData.getPointList();

            List<List<TravelPoint>> pathList = gson.fromJson(pathListStr, new TypeToken<List<List<TravelPoint>>>(){}.getType());
            List<TravelPoint> pointList = gson.fromJson(pointListStr, new TypeToken<List<TravelPoint>>(){}.getType());

            String proDataStr = traceData.getProData();
            if(proDataStr == null || "".equals(proDataStr)){
                traceInfos = gson.fromJson(preDataStr, new TypeToken<List<TraceInfo>>(){}.getType());
                List<FormParam> formParams = new ArrayList<>();
                FormParam fileFormParam = new FormParam();
                fileFormParam.setKey("path");
                fileFormParam.setValue(pyModelPath);
                formParams.add(fileFormParam);
                String resultStr = CommonMethod.postFormJson(flaskUrl37 + "/path", formParams);
//                String resultStr = "[{\"user_ID\": \"0\", \"slice_ID\": \"1\", \"mode\": \"hybrid mode\", \"meanV\": 25.6538, " +
//                        "\"meanA\": -0.0146, \"meanTA\": 0.1442, \"meanSinuosity\": 1000.0001, \"var_V\": 48.5231, \"var_A\": 19.3887, " +
//                        "\"var_TA\": 3298.6454, \"var_Sinuosity\": 0.0, \"mode_V\": 63.7807, \"mode_A\": -2.6945, \"mode_TA\": -1.8265, " +
//                        "\"mode_Sinuosity\": 1000.0, \"Min1_V\": 0.0161, \"Min2_V\": 0.0457, \"Min3_V\": 0.1308, \"Max1_V\": 698.6809, " +
//                        "\"Max2_V\": 63.7807, \"Max3_V\": 56.0264, \"Min1_A\": -17.7505, \"Min2_A\": -15.028, \"Min3_A\": -12.698, " +
//                        "\"Max1_A\": 57.0723, \"Max2_A\": 2.5419, \"Max3_A\": 2.0454, \"Min1_TA\": -318.9452, \"Min2_TA\": -312.0643, " +
//                        "\"Min3_TA\": -310.6617, \"Max1_TA\": 204.8153, \"Max2_TA\": 184.9109, \"Max3_TA\": 181.2246, \"Min1_Sinuosity\": 999.9936, " +
//                        "\"Min2_Sinuosity\": 999.9973, \"Min3_Sinuosity\": 999.9976, \"Max1_Sinuosity\": 1000.0115, \"Max2_Sinuosity\": 1000.0049, \"Max3_Sinuosity\": 1000.0042, \"ValueRange_V\": 698.6648, \"ValueRange_A\": 74.8228, \"ValueRange_TA\": 523.7605, \"ValueRange_Sinuosity\": 0.0179, \"lowQua_V\": 18.7926, \"upQua_V\": 31.4997, \"RangeQua_V\": 12.7071, \"lowQua_A\": -0.0951, \"upQua_A\": 0.0459, \"RangeQua_A\": 0.141, \"lowQua_TA\": -13.0535, \"upQua_TA\": 12.6319, \"RangeQua_TA\": 25.6854, \"lowQua_Sinuosity\": 1000.0, \"upQua_Sinuosity\": 1000.0, \"RangeQua_Sinuosity\": 0.0, \"Skew_V\": 12.9723, \"Skew_A\": 9.9974, \"Skew_TA\": -1.5493, \"Skew_Sinuosity\": 3.9008, \"Kurt_V\": 180.2918, \"Kurt_A\": 139.7791, \"Kurt_TA\": 14.5456, \"Kurt_Sinuosity\": 44.5968, \"CV_V\": 0.5287, \"CV_A\": -0.0008, \"CV_TA\": 0.0, \"CV_Sinuosity\": 688497760.668, \"AutoCC_V\": 0.0848, \"AutoCC_A\": -0.1661, \"AutoCC_TA\": -0.3254, \"AutoCC_Sinuosity\": -0.0686, \"HCR\": 0.1304, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 207}, {\"user_ID\": \"0\", \"slice_ID\": \"2\", \"mode\": \"car\", \"meanV\": 33.2904, \"meanA\": -0.0173, \"meanTA\": 0.4882, \"meanSinuosity\": 1000.0, \"var_V\": 7.812, \"var_A\": 0.0302, \"var_TA\": 200.8962, \"var_Sinuosity\": 0.0, \"mode_V\": 38.6202, \"mode_A\": -0.0204, \"mode_TA\": 19.3941, \"mode_Sinuosity\": 1000.0, \"Min1_V\": 7.9187, \"Min2_V\": 9.805, \"Min3_V\": 10.9288, \"Max1_V\": 42.3508, \"Max2_V\": 41.6716, \"Max3_V\": 41.2251, \"Min1_A\": -0.9585, \"Min2_A\": -0.4222, \"Min3_A\": -0.3232, \"Max1_A\": 0.4669, \"Max2_A\": 0.4131, \"Max3_A\": 0.2363, \"Min1_TA\": -33.7031, \"Min2_TA\": -28.1766, \"Min3_TA\": -26.0905, \"Max1_TA\": 27.314, \"Max2_TA\": 27.2197, \"Max3_TA\": 24.9654, \"Min1_Sinuosity\": 999.9999, \"Min2_Sinuosity\": 1000.0, \"Min3_Sinuosity\": 1000.0, \"Max1_Sinuosity\": 1000.0006, \"Max2_Sinuosity\": 1000.0004, \"Max3_Sinuosity\": 1000.0004, \"ValueRange_V\": 34.432, \"ValueRange_A\": 1.4254, \"ValueRange_TA\": 61.0171, \"ValueRange_Sinuosity\": 0.0006, \"lowQua_V\": 29.6871, \"upQua_V\": 37.2875, \"RangeQua_V\": 7.6004, \"lowQua_A\": -0.0765, \"upQua_A\": 0.0488, \"RangeQua_A\": 0.1253, \"lowQua_TA\": -10.6705, \"upQua_TA\": 11.689, \"RangeQua_TA\": 22.3595, \"lowQua_Sinuosity\": 1000.0, \"upQua_Sinuosity\": 1000.0, \"RangeQua_Sinuosity\": 0.0, \"Skew_V\": -1.3407, \"Skew_A\": -1.8608, \"Skew_TA\": -0.2102, \"Skew_Sinuosity\": 2.9783, \"Kurt_V\": 1.6294, \"Kurt_A\": 11.2754, \"Kurt_TA\": -0.7463, \"Kurt_Sinuosity\": 9.1868, \"CV_V\": 4.2614, \"CV_A\": -0.5722, \"CV_TA\": 0.0024, \"CV_Sinuosity\": 86044511036.0425, \"AutoCC_V\": 0.7669, \"AutoCC_A\": -0.0765, \"AutoCC_TA\": 0.2792, \"AutoCC_Sinuosity\": -0.1793, \"HCR\": 0.0779, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 77}, {\"user_ID\": \"0\", \"slice_ID\": \"3\", \"mode\": \"hybrid mode\", \"meanV\": -5309.8799, \"meanA\": -0.0581, \"meanTA\": -0.1633, \"meanSinuosity\": 1000.0009, \"var_V\": 14.1058, \"var_A\": 0.3195, \"var_TA\": 5996.6078, \"var_Sinuosity\": 0.0001, \"mode_V\": 5.847, \"mode_A\": -0.3155, \"mode_TA\": -57.9044, \"mode_Sinuosity\": 1000.0009, \"Min1_V\": -14.7604, \"Min2_V\": 0.0308, \"Min3_V\": 0.0388, \"Max1_V\": 41.8902, \"Max2_V\": 41.217, \"Max3_V\": 40.3205, \"Min1_A\": -3.9574, \"Min2_A\": -3.0394, \"Min3_A\": -2.8519, \"Max1_A\": 3.5304, \"Max2_A\": 2.174, \"Max3_A\": 1.7248, \"Min1_TA\": -290.556, \"Min2_TA\": -262.6845, \"Min3_TA\": -256.0875, \"Max1_TA\": 344.8271, \"Max2_TA\": 318.4064, \"Max3_TA\": 310.6525, \"Min1_Sinuosity\": 999.9767, \"Min2_Sinuosity\": 999.9909, \"Min3_Sinuosity\": 999.9922, \"Max1_Sinuosity\": 1000.1595, \"Max2_Sinuosity\": 1000.0189, \"Max3_Sinuosity\": 1000.0108, \"ValueRange_V\": 56.6507, \"ValueRange_A\": 7.4878, \"ValueRange_TA\": 635.3831, \"ValueRange_Sinuosity\": 0.1828, \"lowQua_V\": 3.7607, \"upQua_V\": 31.1913, \"RangeQua_V\": 27.4307, \"lowQua_A\": -0.0914, \"upQua_A\": 0.0491, \"RangeQua_A\": 0.1405, \"lowQua_TA\": -16.5584, \"upQua_TA\": 15.5069, \"RangeQua_TA\": 32.0652, \"lowQua_Sinuosity\": 1000.0, \"upQua_Sinuosity\": 1000.0002, \"RangeQua_Sinuosity\": 0.0002, \"Skew_V\": -0.0405, \"Skew_A\": -1.6728, \"Skew_TA\": 0.3601, \"Skew_Sinuosity\": 14.3291, \"Kurt_V\": -1.5434, \"Kurt_A\": 22.7734, \"Kurt_TA\": 5.7091, \"Kurt_Sinuosity\": 220.2884, \"CV_V\": -376.4321, \"CV_A\": -0.1818, \"CV_TA\": -0.0, \"CV_Sinuosity\": 9295815.9622, \"AutoCC_V\": 0.996, \"AutoCC_A\": -0.1147, \"AutoCC_TA\": -0.5493, \"AutoCC_Sinuosity\": -0.0399, \"HCR\": 0.2183, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 252}, {\"user_ID\": \"0\", \"slice_ID\": \"4\", \"mode\": \"car\", \"meanV\": 23.2166, \"meanA\": -0.1507, \"meanTA\": 6.3355, \"meanSinuosity\": 1000.0001, \"var_V\": 9.7961, \"var_A\": 1.079, \"var_TA\": 3627.9523, \"var_Sinuosity\": 0.0, \"mode_V\": 0.2616, \"mode_A\": 0.1079, \"mode_TA\": 217.7162, \"mode_Sinuosity\": 999.9996, \"Min1_V\": 0.1883, \"Min2_V\": 0.2616, \"Min3_V\": 2.3635, \"Max1_V\": 35.4677, \"Max2_V\": 33.2236, \"Max3_V\": 32.7473, \"Min1_A\": -5.0884, \"Min2_A\": -3.5031, \"Min3_A\": -2.189, \"Max1_A\": 3.0676, \"Max2_A\": 0.7726, \"Max3_A\": 0.6925, \"Min1_TA\": -178.2406, \"Min2_TA\": -145.5481, \"Min3_TA\": -40.8656, \"Max1_TA\": 217.7162, \"Max2_TA\": 154.7369, \"Max3_TA\": 50.4058, \"Min1_Sinuosity\": 999.9993, \"Min2_Sinuosity\": 999.9994, \"Min3_Sinuosity\": 999.9996, \"Max1_Sinuosity\": 1000.005, \"Max2_Sinuosity\": 1000.0006, \"Max3_Sinuosity\": 1000.0002, \"ValueRange_V\": 35.2795, \"ValueRange_A\": 8.156, \"ValueRange_TA\": 395.9569, \"ValueRange_Sinuosity\": 0.0057, \"lowQua_V\": 17.1366, \"upQua_V\": 29.4358, \"RangeQua_V\": 12.2992, \"lowQua_A\": -0.0979, \"upQua_A\": 0.1243, \"RangeQua_A\": 0.2222, \"lowQua_TA\": -13.206, \"upQua_TA\": 12.9392, \"RangeQua_TA\": 26.1452, \"lowQua_Sinuosity\": 1000.0, \"upQua_Sinuosity\": 1000.0, \"RangeQua_Sinuosity\": 0.0, \"Skew_V\": -1.0633, \"Skew_A\": -2.3498, \"Skew_TA\": 1.1309, \"Skew_Sinuosity\": 6.4313, \"Kurt_V\": -0.0492, \"Kurt_A\": 12.5338, \"Kurt_TA\": 7.1848, \"Kurt_Sinuosity\": 44.7754, \"CV_V\": 2.37, \"CV_A\": -0.1397, \"CV_TA\": 0.0017, \"CV_Sinuosity\": 1941453427.3913, \"AutoCC_V\": 0.7701, \"AutoCC_A\": -0.1193, \"AutoCC_TA\": -0.2407, \"AutoCC_Sinuosity\": 0.0218, \"HCR\": 0.1731, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 52}, {\"user_ID\": \"0\", \"slice_ID\": \"5\", \"mode\": \"hybrid mode\", \"meanV\": -2918.1919, \"meanA\": -0.2518, \"meanTA\": -0.0044, \"meanSinuosity\": 1000.0002, \"var_V\": 15.9653, \"var_A\": 1.2444, \"var_TA\": 2669.2206, \"var_Sinuosity\": 0.0, \"mode_V\": 32.2093, \"mode_A\": 0.0794, \"mode_TA\": 7.4623, \"mode_Sinuosity\": 1000.0, \"Min1_V\": -22.4216, \"Min2_V\": 0.0441, \"Min3_V\": 0.2491, \"Max1_V\": 69.7187, \"Max2_V\": 46.7858, \"Max3_V\": 44.1439, \"Min1_A\": -7.4527, \"Min2_A\": -4.8372, \"Min3_A\": -2.9923, \"Max1_A\": 0.4139, \"Max2_A\": 0.4135, \"Max3_A\": 0.3906, \"Min1_TA\": -188.9189, \"Min2_TA\": -185.5128, \"Min3_TA\": -159.2744, \"Max1_TA\": 152.6925, \"Max2_TA\": 105.173, \"Max3_TA\": 89.9677, \"Min1_Sinuosity\": 999.9961, \"Min2_Sinuosity\": 999.9989, \"Min3_Sinuosity\": 999.9991, \"Max1_Sinuosity\": 1000.0146, \"Max2_Sinuosity\": 1000.0019, \"Max3_Sinuosity\": 1000.0016, \"ValueRange_V\": 92.1403, \"ValueRange_A\": 7.8666, \"ValueRange_TA\": 341.6113, \"ValueRange_Sinuosity\": 0.0185, \"lowQua_V\": 9.923, \"upQua_V\": 36.4557, \"RangeQua_V\": 26.5327, \"lowQua_A\": -0.1148, \"upQua_A\": 0.0794, \"RangeQua_A\": 0.1942, \"lowQua_TA\": -12.0737, \"upQua_TA\": 11.2801, \"RangeQua_TA\": 23.3537, \"lowQua_Sinuosity\": 1000.0, \"upQua_Sinuosity\": 1000.0, \"RangeQua_Sinuosity\": 0.0, \"Skew_V\": -0.2601, \"Skew_A\": -4.9276, \"Skew_TA\": -1.2247, \"Skew_Sinuosity\": 7.0492, \"Kurt_V\": 0.024, \"Kurt_A\": 27.1217, \"Kurt_TA\": 5.8435, \"Kurt_Sinuosity\": 58.2863, \"CV_V\": -182.7831, \"CV_A\": -0.2023, \"CV_TA\": -0.0, \"CV_Sinuosity\": 310593791.9093, \"AutoCC_V\": 0.9863, \"AutoCC_A\": -0.0879, \"AutoCC_TA\": -0.5581, \"AutoCC_Sinuosity\": -0.0243, \"HCR\": 0.1622, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 74}, {\"user_ID\": \"0\", \"slice_ID\": \"7\", \"mode\": \"hybrid mode\", \"meanV\": 0.6347, \"meanA\": -0.0107, \"meanTA\": -0.7062, \"meanSinuosity\": 999.9999, \"var_V\": 0.9791, \"var_A\": 0.0163, \"var_TA\": 5752.5276, \"var_Sinuosity\": 0.0, \"mode_V\": 1.1867, \"mode_A\": -0.002, \"mode_TA\": 0, \"mode_Sinuosity\": 1000.0026, \"Min1_V\": 0.009, \"Min2_V\": 0.0172, \"Min3_V\": 0.0645, \"Max1_V\": 15.7427, \"Max2_V\": 5.4212, \"Max3_V\": 3.3846, \"Min1_A\": -1.7202, \"Min2_A\": -1.6658, \"Min3_A\": -0.0841, \"Max1_A\": 0.1671, \"Max2_A\": 0.0407, \"Max3_A\": 0.0341, \"Min1_TA\": -352.875, \"Min2_TA\": -351.7872, \"Min3_TA\": -350.6724, \"Max1_TA\": 353.6598, \"Max2_TA\": 344.0912, \"Max3_TA\": 343.5065, \"Min1_Sinuosity\": 999.9893, \"Min2_Sinuosity\": 999.9912, \"Min3_Sinuosity\": 999.9917, \"Max1_Sinuosity\": 1000.0098, \"Max2_Sinuosity\": 1000.0096, \"Max3_Sinuosity\": 1000.0089, \"ValueRange_V\": 15.7337, \"ValueRange_A\": 1.8873, \"ValueRange_TA\": 706.5348, \"ValueRange_Sinuosity\": 0.0204, \"lowQua_V\": 0.7511, \"upQua_V\": 1.2232, \"RangeQua_V\": 0.4721, \"lowQua_A\": -0.0051, \"upQua_A\": 0.0042, \"RangeQua_A\": 0.0094, \"lowQua_TA\": -7.6475, \"upQua_TA\": 8.485, \"RangeQua_TA\": 16.1325, \"lowQua_Sinuosity\": 999.9982, \"upQua_Sinuosity\": 1000.0018, \"RangeQua_Sinuosity\": 0.0036, \"Skew_V\": 9.8569, \"Skew_A\": -12.9568, \"Skew_TA\": -0.2999, \"Skew_Sinuosity\": 0.0867, \"Kurt_V\": 141.8496, \"Kurt_A\": 169.8609, \"Kurt_TA\": 14.2062, \"Kurt_Sinuosity\": 0.7957, \"CV_V\": 0.6482, \"CV_A\": -0.6576, \"CV_TA\": -0.0001, \"CV_Sinuosity\": 107334140.0113, \"AutoCC_V\": 0.5238, \"AutoCC_A\": 0.4377, \"AutoCC_TA\": -0.3338, \"AutoCC_Sinuosity\": 0.1327, \"HCR\": 0.1324, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 355}, {\"user_ID\": \"0\", \"slice_ID\": \"8\", \"mode\": \"car\", \"meanV\": 12.1009, \"meanA\": -0.0128, \"meanTA\": -2.3898, \"meanSinuosity\": 1000.0, \"var_V\": 6.1363, \"var_A\": 0.2126, \"var_TA\": 14589.2399, \"var_Sinuosity\": 0.0, \"mode_V\": 3.9251, \"mode_A\": -0.1194, \"mode_TA\": 3.4921, \"mode_Sinuosity\": 999.9995, \"Min1_V\": 0.2588, \"Min2_V\": 0.8821, \"Min3_V\": 1.6969, \"Max1_V\": 37.4068, \"Max2_V\": 29.9743, \"Max3_V\": 27.4109, \"Min1_A\": -1.4341, \"Min2_A\": -1.3796, \"Min3_A\": -0.9895, \"Max1_A\": 1.4984, \"Max2_A\": 1.2626, \"Max3_A\": 1.1732, \"Min1_TA\": -353.7537, \"Min2_TA\": -351.2739, \"Min3_TA\": -342.2535, \"Max1_TA\": 349.5487, \"Max2_TA\": 348.3898, \"Max3_TA\": 347.1027, \"Min1_Sinuosity\": 999.9956, \"Min2_Sinuosity\": 999.9988, \"Min3_Sinuosity\": 999.9989, \"Max1_Sinuosity\": 1000.0027, \"Max2_Sinuosity\": 1000.0018, \"Max3_Sinuosity\": 1000.0016, \"ValueRange_V\": 37.1481, \"ValueRange_A\": 2.9324, \"ValueRange_TA\": 703.3024, \"ValueRange_Sinuosity\": 0.0072, \"lowQua_V\": 6.7224, \"upQua_V\": 15.2113, \"RangeQua_V\": 8.4889, \"lowQua_A\": -0.2042, \"upQua_A\": 0.1372, \"RangeQua_A\": 0.3415, \"lowQua_TA\": -16.5556, \"upQua_TA\": 13.118, \"RangeQua_TA\": 29.6736, \"lowQua_Sinuosity\": 999.9999, \"upQua_Sinuosity\": 1000.0, \"RangeQua_Sinuosity\": 0.0001, \"Skew_V\": 0.919, \"Skew_A\": 0.1659, \"Skew_TA\": -0.009, \"Skew_Sinuosity\": -2.0748, \"Kurt_V\": 2.1179, \"Kurt_A\": 2.2399, \"Kurt_TA\": 4.6588, \"Kurt_Sinuosity\": 25.0139, \"CV_V\": 1.972, \"CV_A\": -0.06, \"CV_TA\": -0.0002, \"CV_Sinuosity\": 2578706451.739, \"AutoCC_V\": 0.6243, \"AutoCC_A\": -0.2896, \"AutoCC_TA\": -0.3279, \"AutoCC_Sinuosity\": 0.2046, \"HCR\": 0.1966, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 117}, {\"user_ID\": \"0\", \"slice_ID\": \"9\", \"mode\": \"hybrid mode\", \"meanV\": 0.6282, \"meanA\": -0.1177, \"meanTA\": 2.62, \"meanSinuosity\": 999.9994, \"var_V\": 2.2829, \"var_A\": 0.9977, \"var_TA\": 9280.469, \"var_Sinuosity\": 0.0, \"mode_V\": 0.8948, \"mode_A\": -0.0369, \"mode_TA\": 0, \"mode_Sinuosity\": 999.9953, \"Min1_V\": 0.0195, \"Min2_V\": 0.0347, \"Min3_V\": 0.1167, \"Max1_V\": 24.5136, \"Max2_V\": 7.2583, \"Max3_V\": 7.0624, \"Min1_A\": -11.2219, \"Min2_A\": -2.1472, \"Min3_A\": -1.1414, \"Max1_A\": 0.5241, \"Max2_A\": 0.1499, \"Max3_A\": 0.1358, \"Min1_TA\": -357.5104, \"Min2_TA\": -352.025, \"Min3_TA\": -333.4349, \"Max1_TA\": 355.6013, \"Max2_TA\": 322.9435, \"Max3_TA\": 312.4792, \"Min1_Sinuosity\": 999.9871, \"Min2_Sinuosity\": 999.9883, \"Min3_Sinuosity\": 999.9918, \"Max1_Sinuosity\": 1000.0072, \"Max2_Sinuosity\": 1000.0068, \"Max3_Sinuosity\": 1000.0065, \"ValueRange_V\": 24.4941, \"ValueRange_A\": 11.746, \"ValueRange_TA\": 713.1117, \"ValueRange_Sinuosity\": 0.02, \"lowQua_V\": 0.8103, \"upQua_V\": 1.1812, \"RangeQua_V\": 0.3709, \"lowQua_A\": -0.0108, \"upQua_A\": 0.0064, \"RangeQua_A\": 0.0172, \"lowQua_TA\": -19.0878, \"upQua_TA\": 20.7477, \"RangeQua_TA\": 39.8355, \"lowQua_Sinuosity\": 999.9977, \"upQua_Sinuosity\": 1000.0014, \"RangeQua_Sinuosity\": 0.0037, \"Skew_V\": 8.4122, \"Skew_A\": -10.6429, \"Skew_TA\": -0.0235, \"Skew_Sinuosity\": -0.6085, \"Kurt_V\": 82.2818, \"Kurt_A\": 118.1771, \"Kurt_TA\": 6.3199, \"Kurt_Sinuosity\": 1.4946, \"CV_V\": 0.2752, \"CV_A\": -0.118, \"CV_TA\": 0.0003, \"CV_Sinuosity\": 89075868.7047, \"AutoCC_V\": 0.1715, \"AutoCC_A\": -0.0606, \"AutoCC_TA\": -0.2793, \"AutoCC_Sinuosity\": 0.0982, \"HCR\": 0.2727, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 132}, {\"user_ID\": \"0\", \"slice_ID\": \"10\", \"mode\": \"hybrid mode\", \"meanV\": 38.7954, \"meanA\": 0.107, \"meanTA\": -1.0948, \"meanSinuosity\": 1000.0004, \"var_V\": 68.437, \"var_A\": 3.7779, \"var_TA\": 4224.246, \"var_Sinuosity\": 0.0, \"mode_V\": 94.0358, \"mode_A\": -0.2687, \"mode_TA\": -16.168, \"mode_Sinuosity\": 1000.001, \"Min1_V\": 0.05, \"Min2_V\": 0.0702, \"Min3_V\": 0.0755, \"Max1_V\": 634.4236, \"Max2_V\": 162.2993, \"Max3_V\": 94.0358, \"Min1_A\": -4.7897, \"Min2_A\": -2.4828, \"Min3_A\": -1.6382, \"Max1_A\": 16.8784, \"Max2_A\": 2.9941, \"Max3_A\": 1.7073, \"Min1_TA\": -232.125, \"Min2_TA\": -186.4473, \"Min3_TA\": -162.7076, \"Max1_TA\": 228.5323, \"Max2_TA\": 146.4454, \"Max3_TA\": 146.1039, \"Min1_Sinuosity\": 999.9938, \"Min2_Sinuosity\": 999.9955, \"Min3_Sinuosity\": 999.9963, \"Max1_Sinuosity\": 1000.0129, \"Max2_Sinuosity\": 1000.011, \"Max3_Sinuosity\": 1000.007, \"ValueRange_V\": 634.3737, \"ValueRange_A\": 21.668, \"ValueRange_TA\": 460.6573, \"ValueRange_Sinuosity\": 0.0192, \"lowQua_V\": 4.0333, \"upQua_V\": 14.9135, \"RangeQua_V\": 10.8802, \"lowQua_A\": -0.1638, \"upQua_A\": 0.1242, \"RangeQua_A\": 0.288, \"lowQua_TA\": -24.1323, \"upQua_TA\": 24.0393, \"RangeQua_TA\": 48.1716, \"lowQua_Sinuosity\": 999.9999, \"upQua_Sinuosity\": 1000.0001, \"RangeQua_Sinuosity\": 0.0001, \"Skew_V\": 8.3303, \"Skew_A\": 7.0302, \"Skew_TA\": -0.1906, \"Skew_Sinuosity\": 2.6327, \"Kurt_V\": 74.1544, \"Kurt_A\": 62.5674, \"Kurt_TA\": 3.5432, \"Kurt_Sinuosity\": 12.6399, \"CV_V\": 0.5669, \"CV_A\": 0.0283, \"CV_TA\": -0.0003, \"CV_Sinuosity\": 175345232.2363, \"AutoCC_V\": 0.0759, \"AutoCC_A\": -0.3799, \"AutoCC_TA\": -0.3309, \"AutoCC_Sinuosity\": 0.251, \"HCR\": 0.3187, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 91}, {\"user_ID\": \"0\", \"slice_ID\": \"11\", \"mode\": \"hybrid mode\", \"meanV\": 0.9516, \"meanA\": -0.4499, \"meanTA\": -3.7936, \"meanSinuosity\": 999.9997, \"var_V\": 17.6003, \"var_A\": 5.5639, \"var_TA\": 6394.3032, \"var_Sinuosity\": 0.0, \"mode_V\": 113.3551, \"mode_A\": -12.4515, \"mode_TA\": 0, \"mode_Sinuosity\": 1000.0, \"Min1_V\": 0.0743, \"Min2_V\": 0.0947, \"Min3_V\": 0.1352, \"Max1_V\": 113.3551, \"Max2_V\": 12.4027, \"Max3_V\": 3.5902, \"Min1_A\": -12.4515, \"Min2_A\": -0.69, \"Min3_A\": -0.3529, \"Max1_A\": 1.0165, \"Max2_A\": 0.6758, \"Max3_A\": 0.0935, \"Min1_TA\": -310.2364, \"Min2_TA\": -178.0908, \"Min3_TA\": -144.4623, \"Max1_TA\": 349.6952, \"Max2_TA\": 335.6589, \"Max3_TA\": 96.9112, \"Min1_Sinuosity\": 999.9884, \"Min2_Sinuosity\": 999.9901, \"Min3_Sinuosity\": 999.9931, \"Max1_Sinuosity\": 1000.0253, \"Max2_Sinuosity\": 1000.0081, \"Max3_Sinuosity\": 1000.0071, \"ValueRange_V\": 113.2808, \"ValueRange_A\": 13.4681, \"ValueRange_TA\": 659.9315, \"ValueRange_Sinuosity\": 0.0369, \"lowQua_V\": 0.5147, \"upQua_V\": 1.2202, \"RangeQua_V\": 0.7055, \"lowQua_A\": -0.0099, \"upQua_A\": 0.0066, \"RangeQua_A\": 0.0165, \"lowQua_TA\": -26.5651, \"upQua_TA\": 19.9069, \"RangeQua_TA\": 46.4719, \"lowQua_Sinuosity\": 999.997, \"upQua_Sinuosity\": 1000.0023, \"RangeQua_Sinuosity\": 0.0053, \"Skew_V\": 6.1805, \"Skew_A\": -4.9896, \"Skew_TA\": 1.0828, \"Skew_Sinuosity\": 1.8468, \"Kurt_V\": 37.3583, \"Kurt_A\": 23.6207, \"Kurt_TA\": 10.076, \"Kurt_Sinuosity\": 9.802, \"CV_V\": 0.0541, \"CV_A\": -0.0809, \"CV_TA\": -0.0006, \"CV_Sinuosity\": 44497642.3623, \"AutoCC_V\": 0.4967, \"AutoCC_A\": 0.6346, \"AutoCC_TA\": -0.0484, \"AutoCC_Sinuosity\": -0.2611, \"HCR\": 0.2593, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 81}, {\"user_ID\": \"0\", \"slice_ID\": \"12\", \"mode\": \"car\", \"meanV\": -383.0919, \"meanA\": -0.0893, \"meanTA\": -0.0454, \"meanSinuosity\": 999.9999, \"var_V\": 5.3093, \"var_A\": 0.706, \"var_TA\": 7863.9973, \"var_Sinuosity\": 0.0, \"mode_V\": 12.9306, \"mode_A\": 0.3699, \"mode_TA\": -14.2205, \"mode_Sinuosity\": 1000.0001, \"Min1_V\": -5.5091, \"Min2_V\": 0.0957, \"Min3_V\": 0.3163, \"Max1_V\": 21.2596, \"Max2_V\": 20.2402, \"Max3_V\": 20.022, \"Min1_A\": -5.7401, \"Min2_A\": -4.9446, \"Min3_A\": -1.2344, \"Max1_A\": 1.2959, \"Max2_A\": 0.5655, \"Max3_A\": 0.563, \"Min1_TA\": -336.9733, \"Min2_TA\": -322.4925, \"Min3_TA\": -249.1455, \"Max1_TA\": 349.2357, \"Max2_TA\": 316.259, \"Max3_TA\": 218.726, \"Min1_Sinuosity\": 999.9915, \"Min2_Sinuosity\": 999.9958, \"Min3_Sinuosity\": 999.9962, \"Max1_Sinuosity\": 1000.0056, \"Max2_Sinuosity\": 1000.0016, \"Max3_Sinuosity\": 1000.0015, \"ValueRange_V\": 26.7687, \"ValueRange_A\": 7.036, \"ValueRange_TA\": 686.209, \"ValueRange_Sinuosity\": 0.0141, \"lowQua_V\": 9.9878, \"upQua_V\": 15.107, \"RangeQua_V\": 5.1192, \"lowQua_A\": -0.1449, \"upQua_A\": 0.2268, \"RangeQua_A\": 0.3717, \"lowQua_TA\": -19.7302, \"upQua_TA\": 25.3462, \"RangeQua_TA\": 45.0764, \"lowQua_Sinuosity\": 999.9999, \"upQua_Sinuosity\": 1000.0001, \"RangeQua_Sinuosity\": 0.0002, \"Skew_V\": -1.0056, \"Skew_A\": -5.1798, \"Skew_TA\": -0.1707, \"Skew_Sinuosity\": -2.8631, \"Kurt_V\": 0.8348, \"Kurt_A\": 31.739, \"Kurt_TA\": 7.5885, \"Kurt_Sinuosity\": 26.1755, \"CV_V\": -72.155, \"CV_A\": -0.1265, \"CV_TA\": -0.0, \"CV_Sinuosity\": 633174740.0901, \"AutoCC_V\": 0.9896, \"AutoCC_A\": -0.0604, \"AutoCC_TA\": -0.072, \"AutoCC_Sinuosity\": 0.1976, \"HCR\": 0.3196, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 97}, {\"user_ID\": \"0\", \"slice_ID\": \"15\", \"mode\": \"hybrid mode\", \"meanV\": 0.4892, \"meanA\": -0.1081, \"meanTA\": -1.9635, \"meanSinuosity\": 999.9997, \"var_V\": 2.2722, \"var_A\": 0.5033, \"var_TA\": 7957.5621, \"var_Sinuosity\": 0.0, \"mode_V\": 5.9458, \"mode_A\": -1.6261, \"mode_TA\": -228.3665, \"mode_Sinuosity\": 1000.003, \"Min1_V\": 0.0171, \"Min2_V\": 0.038, \"Min3_V\": 0.0483, \"Max1_V\": 21.5787, \"Max2_V\": 20.3929, \"Max3_V\": 11.0857, \"Min1_A\": -7.1237, \"Min2_A\": -6.6428, \"Min3_A\": -1.701, \"Max1_A\": 0.3297, \"Max2_A\": 0.2201, \"Max3_A\": 0.1666, \"Min1_TA\": -327.0069, \"Min2_TA\": -272.4896, \"Min3_TA\": -259.2611, \"Max1_TA\": 301.4389, \"Max2_TA\": 290.3764, \"Max3_TA\": 273.3665, \"Min1_Sinuosity\": 999.9859, \"Min2_Sinuosity\": 999.9909, \"Min3_Sinuosity\": 999.9926, \"Max1_Sinuosity\": 1000.009, \"Max2_Sinuosity\": 1000.0078, \"Max3_Sinuosity\": 1000.0074, \"ValueRange_V\": 21.5616, \"ValueRange_A\": 7.4534, \"ValueRange_TA\": 628.4458, \"ValueRange_Sinuosity\": 0.0231, \"lowQua_V\": 0.4637, \"upQua_V\": 1.0085, \"RangeQua_V\": 0.5448, \"lowQua_A\": -0.0138, \"upQua_A\": 0.0079, \"RangeQua_A\": 0.0217, \"lowQua_TA\": -41.4552, \"upQua_TA\": 32.4712, \"RangeQua_TA\": 73.9264, \"lowQua_Sinuosity\": 999.9978, \"upQua_Sinuosity\": 1000.0019, \"RangeQua_Sinuosity\": 0.0041, \"Skew_V\": 6.9914, \"Skew_A\": -8.5281, \"Skew_TA\": 0.0244, \"Skew_Sinuosity\": -0.246, \"Kurt_V\": 55.9613, \"Kurt_A\": 78.4504, \"Kurt_TA\": 2.6288, \"Kurt_Sinuosity\": 0.5887, \"CV_V\": 0.2153, \"CV_A\": -0.2149, \"CV_TA\": -0.0002, \"CV_Sinuosity\": 81333563.0188, \"AutoCC_V\": 0.0891, \"AutoCC_A\": -0.0151, \"AutoCC_TA\": -0.4027, \"AutoCC_Sinuosity\": -0.04, \"HCR\": 0.3474, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 213}, {\"user_ID\": \"0\", \"slice_ID\": \"17\", \"mode\": \"walk\", \"meanV\": -219.0714, \"meanA\": -0.003, \"meanTA\": 0.0878, \"meanSinuosity\": 999.9998, \"var_V\": 0.3623, \"var_A\": 0.0003, \"var_TA\": 10975.578, \"var_Sinuosity\": 0.0, \"mode_V\": 1.2355, \"mode_A\": -0.0003, \"mode_TA\": 0, \"mode_Sinuosity\": 999.9952, \"Min1_V\": -0.0322, \"Min2_V\": 0.0423, \"Min3_V\": 0.0506, \"Max1_V\": 2.6373, \"Max2_V\": 2.0433, \"Max3_V\": 1.8823, \"Min1_A\": -0.0949, \"Min2_A\": -0.092, \"Min3_A\": -0.0879, \"Max1_A\": 0.0721, \"Max2_A\": 0.0457, \"Max3_A\": 0.0399, \"Min1_TA\": -347.4712, \"Min2_TA\": -346.139, \"Min3_TA\": -342.8973, \"Max1_TA\": 352.6096, \"Max2_TA\": 349.6952, \"Max3_TA\": 349.6111, \"Min1_Sinuosity\": 999.9806, \"Min2_Sinuosity\": 999.9898, \"Min3_Sinuosity\": 999.9903, \"Max1_Sinuosity\": 1000.0096, \"Max2_Sinuosity\": 1000.0088, \"Max3_Sinuosity\": 1000.0078, \"ValueRange_V\": 2.6695, \"ValueRange_A\": 0.167, \"ValueRange_TA\": 700.0808, \"ValueRange_Sinuosity\": 0.029, \"lowQua_V\": 0.8707, \"upQua_V\": 1.2727, \"RangeQua_V\": 0.4021, \"lowQua_A\": -0.0069, \"upQua_A\": 0.0055, \"RangeQua_A\": 0.0124, \"lowQua_TA\": -17.0102, \"upQua_TA\": 15.4797, \"RangeQua_TA\": 32.4899, \"lowQua_Sinuosity\": 999.9982, \"upQua_Sinuosity\": 1000.0017, \"RangeQua_Sinuosity\": 0.0036, \"Skew_V\": -0.5881, \"Skew_A\": -1.6068, \"Skew_TA\": 0.0978, \"Skew_Sinuosity\": -0.5376, \"Kurt_V\": 1.0863, \"Kurt_A\": 6.3253, \"Kurt_TA\": 5.9065, \"Kurt_Sinuosity\": 3.5633, \"CV_V\": -604.6176, \"CV_A\": -9.7842, \"CV_TA\": 0.0, \"CV_Sinuosity\": 109880052.2741, \"AutoCC_V\": 0.9979, \"AutoCC_A\": -0.2435, \"AutoCC_TA\": -0.5005, \"AutoCC_Sinuosity\": 0.0497, \"HCR\": 0.2215, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 474}, {\"user_ID\": \"1\", \"slice_ID\": \"10\", \"mode\": \"car\", \"meanV\": 6.8077, \"meanA\": -0.0473, \"meanTA\": -0.7798, \"meanSinuosity\": 1000.0001, \"var_V\": 7.4728, \"var_A\": 0.2182, \"var_TA\": 15934.0055, \"var_Sinuosity\": 0.0, \"mode_V\": 24.9864, \"mode_A\": 0.0052, \"mode_TA\": 11.4821, \"mode_Sinuosity\": 1000.0001, \"Min1_V\": 0.0878, \"Min2_V\": 0.1781, \"Min3_V\": 0.2064, \"Max1_V\": 26.7795, \"Max2_V\": 26.3831, \"Max3_V\": 25.9503, \"Min1_A\": -3.4588, \"Min2_A\": -0.3545, \"Min3_A\": -0.3205, \"Max1_A\": 0.4212, \"Max2_A\": 0.2904, \"Max3_A\": 0.2713, \"Min1_TA\": -328.206, \"Min2_TA\": -309.2263, \"Min3_TA\": -270.8612, \"Max1_TA\": 314.6223, \"Max2_TA\": 294.6338, \"Max3_TA\": 265.6561, \"Min1_Sinuosity\": 999.9997, \"Min2_Sinuosity\": 999.9998, \"Min3_Sinuosity\": 999.9999, \"Max1_Sinuosity\": 1000.0015, \"Max2_Sinuosity\": 1000.0006, \"Max3_Sinuosity\": 1000.0004, \"ValueRange_V\": 26.6917, \"ValueRange_A\": 3.88, \"ValueRange_TA\": 642.8283, \"ValueRange_Sinuosity\": 0.0018, \"lowQua_V\": 6.1542, \"upQua_V\": 14.3514, \"RangeQua_V\": 8.1972, \"lowQua_A\": -0.0201, \"upQua_A\": 0.0438, \"RangeQua_A\": 0.0639, \"lowQua_TA\": -33.6532, \"upQua_TA\": 22.2723, \"RangeQua_TA\": 55.9255, \"lowQua_Sinuosity\": 1000.0, \"upQua_Sinuosity\": 1000.0, \"RangeQua_Sinuosity\": 0.0, \"Skew_V\": 0.5277, \"Skew_A\": -6.9228, \"Skew_TA\": -0.1524, \"Skew_Sinuosity\": 4.8781, \"Kurt_V\": -0.3082, \"Kurt_A\": 51.2913, \"Kurt_TA\": 1.5702, \"Kurt_Sinuosity\": 29.0691, \"CV_V\": 0.911, \"CV_A\": -0.217, \"CV_TA\": -0.0, \"CV_Sinuosity\": 18980075050.7398, \"AutoCC_V\": 0.6854, \"AutoCC_A\": -0.0935, \"AutoCC_TA\": -0.4164, \"AutoCC_Sinuosity\": -0.095, \"HCR\": 0.2759, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 58}, {\"user_ID\": \"2\", \"slice_ID\": \"16\", \"mode\": \"car\", \"meanV\": 7.7005, \"meanA\": -0.0731, \"meanTA\": -1.3818, \"meanSinuosity\": 1000.0, \"var_V\": 5.5831, \"var_A\": 0.1299, \"var_TA\": 14642.4196, \"var_Sinuosity\": 0.0, \"mode_V\": 1.445, \"mode_A\": -0.0792, \"mode_TA\": -15.8324, \"mode_Sinuosity\": 999.9935, \"Min1_V\": 0.1839, \"Min2_V\": 0.6274, \"Min3_V\": 0.8494, \"Max1_V\": 22.8378, \"Max2_V\": 19.9461, \"Max3_V\": 19.2768, \"Min1_A\": -1.5068, \"Min2_A\": -1.1537, \"Min3_A\": -1.0247, \"Max1_A\": 0.5996, \"Max2_A\": 0.4891, \"Max3_A\": 0.4867, \"Min1_TA\": -355.3645, \"Min2_TA\": -338.1986, \"Min3_TA\": -326.9846, \"Max1_TA\": 357.2519, \"Max2_TA\": 354.2894, \"Max3_TA\": 351.2538, \"Min1_Sinuosity\": 999.9935, \"Min2_Sinuosity\": 999.9966, \"Min3_Sinuosity\": 999.9972, \"Max1_Sinuosity\": 1000.0066, \"Max2_Sinuosity\": 1000.0056, \"Max3_Sinuosity\": 1000.0053, \"ValueRange_V\": 22.654, \"ValueRange_A\": 2.1063, \"ValueRange_TA\": 712.6164, \"ValueRange_Sinuosity\": 0.0131, \"lowQua_V\": 2.3449, \"upQua_V\": 10.8458, \"RangeQua_V\": 8.501, \"lowQua_A\": -0.1149, \"upQua_A\": 0.1186, \"RangeQua_A\": 0.2334, \"lowQua_TA\": -16.7316, \"upQua_TA\": 11.3493, \"RangeQua_TA\": 28.0809, \"lowQua_Sinuosity\": 999.9996, \"upQua_Sinuosity\": 1000.0001, \"RangeQua_Sinuosity\": 0.0005, \"Skew_V\": 0.8227, \"Skew_A\": -1.4672, \"Skew_TA\": 0.0824, \"Skew_Sinuosity\": 0.0903, \"Kurt_V\": -0.1918, \"Kurt_A\": 3.686, \"Kurt_TA\": 4.4358, \"Kurt_Sinuosity\": 6.3817, \"CV_V\": 1.3792, \"CV_A\": -0.563, \"CV_TA\": -0.0001, \"CV_Sinuosity\": 292784573.0344, \"AutoCC_V\": 0.7454, \"AutoCC_A\": -0.1192, \"AutoCC_TA\": -0.5025, \"AutoCC_Sinuosity\": 0.333, \"HCR\": 0.1842, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 76}, {\"user_ID\": \"2\", \"slice_ID\": \"17\", \"mode\": \"walk\", \"meanV\": 0.4929, \"meanA\": -0.0072, \"meanTA\": -0.619, \"meanSinuosity\": 999.9999, \"var_V\": 0.4868, \"var_A\": 0.008, \"var_TA\": 7032.4679, \"var_Sinuosity\": 0.0, \"mode_V\": 0.04, \"mode_A\": 0.0015, \"mode_TA\": -136.7899, \"mode_Sinuosity\": 1000.0, \"Min1_V\": 0.04, \"Min2_V\": 0.0515, \"Min3_V\": 0.0586, \"Max1_V\": 6.6528, \"Max2_V\": 2.5159, \"Max3_V\": 2.3337, \"Min1_A\": -1.5657, \"Min2_A\": -0.2117, \"Min3_A\": -0.1429, \"Max1_A\": 0.2399, \"Max2_A\": 0.0524, \"Max3_A\": 0.0308, \"Min1_TA\": -347.9492, \"Min2_TA\": -313.0908, \"Min3_TA\": -301.569, \"Max1_TA\": 341.2948, \"Max2_TA\": 336.0375, \"Max3_TA\": 328.134, \"Min1_Sinuosity\": 999.9875, \"Min2_Sinuosity\": 999.9881, \"Min3_Sinuosity\": 999.9896, \"Max1_Sinuosity\": 1000.012, \"Max2_Sinuosity\": 1000.0114, \"Max3_Sinuosity\": 1000.0091, \"ValueRange_V\": 6.6128, \"ValueRange_A\": 1.8056, \"ValueRange_TA\": 689.244, \"ValueRange_Sinuosity\": 0.0244, \"lowQua_V\": 0.4586, \"upQua_V\": 0.9351, \"RangeQua_V\": 0.4764, \"lowQua_A\": -0.0057, \"upQua_A\": 0.0042, \"RangeQua_A\": 0.0099, \"lowQua_TA\": -24.3045, \"upQua_TA\": 21.8014, \"RangeQua_TA\": 46.106, \"lowQua_Sinuosity\": 999.9977, \"upQua_Sinuosity\": 1000.0025, \"RangeQua_Sinuosity\": 0.0048, \"Skew_V\": 5.9026, \"Skew_A\": -16.2481, \"Skew_TA\": 0.1887, \"Skew_Sinuosity\": -0.076, \"Kurt_V\": 68.0456, \"Kurt_A\": 283.4872, \"Kurt_TA\": 6.8055, \"Kurt_Sinuosity\": 0.6695, \"CV_V\": 1.0126, \"CV_A\": -0.89, \"CV_TA\": -0.0001, \"CV_Sinuosity\": 69217448.3977, \"AutoCC_V\": 0.4061, \"AutoCC_A\": -0.1538, \"AutoCC_TA\": -0.4547, \"AutoCC_Sinuosity\": 0.1249, \"HCR\": 0.2738, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 325}, {\"user_ID\": \"2\", \"slice_ID\": \"18\", \"mode\": \"car\", \"meanV\": 9.4778, \"meanA\": 0.0141, \"meanTA\": -2.7495, \"meanSinuosity\": 999.9997, \"var_V\": 6.2802, \"var_A\": 0.1253, \"var_TA\": 3199.8307, \"var_Sinuosity\": 0.0, \"mode_V\": 5.5163, \"mode_A\": -0.1049, \"mode_TA\": -79.5377, \"mode_Sinuosity\": 1000.0, \"Min1_V\": 0.569, \"Min2_V\": 0.6371, \"Min3_V\": 0.6864, \"Max1_V\": 30.0737, \"Max2_V\": 21.5941, \"Max3_V\": 20.9988, \"Min1_A\": -1.232, \"Min2_A\": -0.619, \"Min3_A\": -0.6042, \"Max1_A\": 0.9578, \"Max2_A\": 0.6981, \"Max3_A\": 0.6064, \"Min1_TA\": -86.672, \"Min2_TA\": -86.4261, \"Min3_TA\": -83.5084, \"Max1_TA\": 277.6225, \"Max2_TA\": 169.9618, \"Max3_TA\": 43.3634, \"Min1_Sinuosity\": 999.9846, \"Min2_Sinuosity\": 999.9952, \"Min3_Sinuosity\": 999.9987, \"Max1_Sinuosity\": 1000.002, \"Max2_Sinuosity\": 1000.0011, \"Max3_Sinuosity\": 1000.0009, \"ValueRange_V\": 29.5047, \"ValueRange_A\": 2.1898, \"ValueRange_TA\": 364.2945, \"ValueRange_Sinuosity\": 0.0174, \"lowQua_V\": 5.7224, \"upQua_V\": 13.9161, \"RangeQua_V\": 8.1937, \"lowQua_A\": -0.1286, \"upQua_A\": 0.1782, \"RangeQua_A\": 0.3068, \"lowQua_TA\": -17.2195, \"upQua_TA\": 8.6412, \"RangeQua_TA\": 25.8607, \"lowQua_Sinuosity\": 1000.0, \"upQua_Sinuosity\": 1000.0, \"RangeQua_Sinuosity\": 0.0001, \"Skew_V\": 0.696, \"Skew_A\": -0.2503, \"Skew_TA\": 2.7251, \"Skew_Sinuosity\": -6.0072, \"Kurt_V\": 0.7042, \"Kurt_A\": 2.7004, \"Kurt_TA\": 12.584, \"Kurt_Sinuosity\": 39.2927, \"CV_V\": 1.5091, \"CV_A\": 0.1126, \"CV_TA\": -0.0009, \"CV_Sinuosity\": 193376838.2667, \"AutoCC_V\": 0.4352, \"AutoCC_A\": -0.0413, \"AutoCC_TA\": -0.0857, \"AutoCC_Sinuosity\": 0.03, \"HCR\": 0.1346, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 52}, {\"user_ID\": \"2\", \"slice_ID\": \"25\", \"mode\": \"hybrid mode\", \"meanV\": 1.4118, \"meanA\": -0.4151, \"meanTA\": 2.4161, \"meanSinuosity\": 1000.0001, \"var_V\": 6.0341, \"var_A\": 4.5922, \"var_TA\": 15889.2796, \"var_Sinuosity\": 0.0, \"mode_V\": 1.0571, \"mode_A\": 0.2383, \"mode_TA\": 314.6869, \"mode_Sinuosity\": 1000.0015, \"Min1_V\": 0.0315, \"Min2_V\": 0.0906, \"Min3_V\": 0.1147, \"Max1_V\": 47.9628, \"Max2_V\": 40.3951, \"Max3_V\": 40.3735, \"Min1_A\": -17.828, \"Min2_A\": -15.4399, \"Min3_A\": -9.0421, \"Max1_A\": 1.1088, \"Max2_A\": 0.9423, \"Max3_A\": 0.5563, \"Min1_TA\": -352.3719, \"Min2_TA\": -332.3442, \"Min3_TA\": -325.0129, \"Max1_TA\": 344.5287, \"Max2_TA\": 338.9098, \"Max3_TA\": 325.4148, \"Min1_Sinuosity\": 999.9902, \"Min2_Sinuosity\": 999.9941, \"Min3_Sinuosity\": 999.9941, \"Max1_Sinuosity\": 1000.0108, \"Max2_Sinuosity\": 1000.0083, \"Max3_Sinuosity\": 1000.0079, \"ValueRange_V\": 47.9313, \"ValueRange_A\": 18.9368, \"ValueRange_TA\": 696.9005, \"ValueRange_Sinuosity\": 0.0206, \"lowQua_V\": 1.6552, \"upQua_V\": 5.1013, \"RangeQua_V\": 3.4461, \"lowQua_A\": -0.0736, \"upQua_A\": 0.0519, \"RangeQua_A\": 0.1255, \"lowQua_TA\": -32.296, \"upQua_TA\": 24.6459, \"RangeQua_TA\": 56.9419, \"lowQua_Sinuosity\": 999.9991, \"upQua_Sinuosity\": 1000.0003, \"RangeQua_Sinuosity\": 0.0013, \"Skew_V\": 5.2109, \"Skew_A\": -6.225, \"Skew_TA\": 0.3319, \"Skew_Sinuosity\": 1.068, \"Kurt_V\": 31.3549, \"Kurt_A\": 42.5774, \"Kurt_TA\": 2.1163, \"Kurt_Sinuosity\": 6.1457, \"CV_V\": 0.234, \"CV_A\": -0.0904, \"CV_TA\": 0.0002, \"CV_Sinuosity\": 174235553.7196, \"AutoCC_V\": 0.1436, \"AutoCC_A\": -0.0955, \"AutoCC_TA\": -0.3747, \"AutoCC_Sinuosity\": 0.0286, \"HCR\": 0.2622, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 164}, {\"user_ID\": \"2\", \"slice_ID\": \"26\", \"mode\": \"hybrid mode\", \"meanV\": 1.8203, \"meanA\": -0.5063, \"meanTA\": 1.6003, \"meanSinuosity\": 999.9998, \"var_V\": 5.5056, \"var_A\": 3.6999, \"var_TA\": 13058.2158, \"var_Sinuosity\": 0.0, \"mode_V\": 3.2717, \"mode_A\": -0.2668, \"mode_TA\": 36.1703, \"mode_Sinuosity\": 999.9983, \"Min1_V\": 0.0685, \"Min2_V\": 0.1258, \"Min3_V\": 0.1816, \"Max1_V\": 37.9916, \"Max2_V\": 25.5658, \"Max3_V\": 20.3353, \"Min1_A\": -11.9511, \"Min2_A\": -8.7909, \"Min3_A\": -8.355, \"Max1_A\": 1.2202, \"Max2_A\": 0.8389, \"Max3_A\": 0.5432, \"Min1_TA\": -339.5196, \"Min2_TA\": -315.0, \"Min3_TA\": -249.2744, \"Max1_TA\": 337.5731, \"Max2_TA\": 318.9452, \"Max3_TA\": 276.3944, \"Min1_Sinuosity\": 999.988, \"Min2_Sinuosity\": 999.9885, \"Min3_Sinuosity\": 999.9924, \"Max1_Sinuosity\": 1000.0086, \"Max2_Sinuosity\": 1000.0065, \"Max3_Sinuosity\": 1000.0064, \"ValueRange_V\": 37.9231, \"ValueRange_A\": 13.1713, \"ValueRange_TA\": 677.0927, \"ValueRange_Sinuosity\": 0.0206, \"lowQua_V\": 1.0537, \"upQua_V\": 3.7702, \"RangeQua_V\": 2.7165, \"lowQua_A\": -0.1111, \"upQua_A\": 0.0321, \"RangeQua_A\": 0.1431, \"lowQua_TA\": -32.4522, \"upQua_TA\": 45.9962, \"RangeQua_TA\": 78.4484, \"lowQua_Sinuosity\": 999.9991, \"upQua_Sinuosity\": 1000.001, \"RangeQua_Sinuosity\": 0.0019, \"Skew_V\": 3.8109, \"Skew_A\": -4.2187, \"Skew_TA\": -0.1379, \"Skew_Sinuosity\": -0.9217, \"Kurt_V\": 17.3947, \"Kurt_A\": 18.482, \"Kurt_TA\": 1.8759, \"Kurt_Sinuosity\": 3.2419, \"CV_V\": 0.3306, \"CV_A\": -0.1368, \"CV_TA\": 0.0001, \"CV_Sinuosity\": 99022260.176, \"AutoCC_V\": 0.0776, \"AutoCC_A\": -0.1207, \"AutoCC_TA\": -0.2052, \"AutoCC_Sinuosity\": -0.0151, \"HCR\": 0.4078, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 103}, {\"user_ID\": \"3\", \"slice_ID\": \"1\", \"mode\": \"car\", \"meanV\": 5.6346, \"meanA\": -0.0888, \"meanTA\": -0.575, \"meanSinuosity\": 999.9998, \"var_V\": 4.8562, \"var_A\": 0.4731, \"var_TA\": 15291.8158, \"var_Sinuosity\": 0.0, \"mode_V\": 9.0577, \"mode_A\": -0.2187, \"mode_TA\": 15.8839, \"mode_Sinuosity\": 1000.0, \"Min1_V\": 0.0782, \"Min2_V\": 0.0868, \"Min3_V\": 0.1667, \"Max1_V\": 18.2568, \"Max2_V\": 16.3634, \"Max3_V\": 16.1022, \"Min1_A\": -6.114, \"Min2_A\": -2.5303, \"Min3_A\": -0.9525, \"Max1_A\": 0.9859, \"Max2_A\": 0.4481, \"Max3_A\": 0.4061, \"Min1_TA\": -356.6596, \"Min2_TA\": -349.2157, \"Min3_TA\": -346.6075, \"Max1_TA\": 352.0841, \"Max2_TA\": 348.6901, \"Max3_TA\": 336.4568, \"Min1_Sinuosity\": 999.9939, \"Min2_Sinuosity\": 999.9949, \"Min3_Sinuosity\": 999.9952, \"Max1_Sinuosity\": 1000.0048, \"Max2_Sinuosity\": 1000.0038, \"Max3_Sinuosity\": 1000.0025, \"ValueRange_V\": 18.1786, \"ValueRange_A\": 7.1, \"ValueRange_TA\": 708.7437, \"ValueRange_Sinuosity\": 0.0109, \"lowQua_V\": 1.6921, \"upQua_V\": 9.3087, \"RangeQua_V\": 7.6166, \"lowQua_A\": -0.0764, \"upQua_A\": 0.0931, \"RangeQua_A\": 0.1695, \"lowQua_TA\": -14.0877, \"upQua_TA\": 15.9741, \"RangeQua_TA\": 30.0618, \"lowQua_Sinuosity\": 999.9998, \"upQua_Sinuosity\": 1000.0001, \"RangeQua_Sinuosity\": 0.0003, \"Skew_V\": 0.5292, \"Skew_A\": -7.0697, \"Skew_TA\": -0.0982, \"Skew_Sinuosity\": -1.1286, \"Kurt_V\": -0.6873, \"Kurt_A\": 59.2648, \"Kurt_TA\": 3.8386, \"Kurt_Sinuosity\": 6.2287, \"CV_V\": 1.1603, \"CV_A\": -0.1876, \"CV_TA\": -0.0, \"CV_Sinuosity\": 487905295.3734, \"AutoCC_V\": 0.7271, \"AutoCC_A\": -0.0715, \"AutoCC_TA\": -0.2379, \"AutoCC_Sinuosity\": -0.246, \"HCR\": 0.233, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 103}, {\"user_ID\": \"3\", \"slice_ID\": \"2\", \"mode\": \"car\", \"meanV\": 8.5991, \"meanA\": -0.0183, \"meanTA\": 0.4058, \"meanSinuosity\": 1000.0002, \"var_V\": 3.8919, \"var_A\": 0.1211, \"var_TA\": 5688.6144, \"var_Sinuosity\": 0.0, \"mode_V\": 2.63, \"mode_A\": -0.0066, \"mode_TA\": -1.4057, \"mode_Sinuosity\": 999.9997, \"Min1_V\": 0.2884, \"Min2_V\": 0.3088, \"Min3_V\": 0.391, \"Max1_V\": 17.6657, \"Max2_V\": 15.3618, \"Max3_V\": 13.3572, \"Min1_A\": -1.6504, \"Min2_A\": -1.0165, \"Min3_A\": -0.8628, \"Max1_A\": 0.8904, \"Max2_A\": 0.7249, \"Max3_A\": 0.5788, \"Min1_TA\": -244.1147, \"Min2_TA\": -94.5857, \"Min3_TA\": -90.5405, \"Max1_TA\": 293.9569, \"Max2_TA\": 266.436, \"Max3_TA\": 264.8241, \"Min1_Sinuosity\": 999.9983, \"Min2_Sinuosity\": 999.9992, \"Min3_Sinuosity\": 999.9992, \"Max1_Sinuosity\": 1000.0041, \"Max2_Sinuosity\": 1000.0032, \"Max3_Sinuosity\": 1000.0024, \"ValueRange_V\": 17.3773, \"ValueRange_A\": 2.5408, \"ValueRange_TA\": 538.0716, \"ValueRange_Sinuosity\": 0.0058, \"lowQua_V\": 3.3561, \"upQua_V\": 9.7105, \"RangeQua_V\": 6.3543, \"lowQua_A\": -0.0743, \"upQua_A\": 0.0771, \"RangeQua_A\": 0.1514, \"lowQua_TA\": -23.9757, \"upQua_TA\": 9.1085, \"RangeQua_TA\": 33.0843, \"lowQua_Sinuosity\": 999.9998, \"upQua_Sinuosity\": 1000.0001, \"RangeQua_Sinuosity\": 0.0004, \"Skew_V\": 0.5495, \"Skew_A\": -1.7651, \"Skew_TA\": 1.7918, \"Skew_Sinuosity\": 2.177, \"Kurt_V\": -0.2349, \"Kurt_A\": 8.8053, \"Kurt_TA\": 8.6766, \"Kurt_Sinuosity\": 6.3221, \"CV_V\": 2.2095, \"CV_A\": -0.151, \"CV_TA\": 0.0001, \"CV_Sinuosity\": 1168932956.7799, \"AutoCC_V\": 0.6106, \"AutoCC_A\": -0.1015, \"AutoCC_TA\": -0.0514, \"AutoCC_Sinuosity\": 0.0428, \"HCR\": 0.1774, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 62}, {\"user_ID\": \"3\", \"slice_ID\": \"8\", \"mode\": \"car\", \"meanV\": 4.1573, \"meanA\": -0.0949, \"meanTA\": 1.324, \"meanSinuosity\": 999.9999, \"var_V\": 3.8028, \"var_A\": 0.4306, \"var_TA\": 983.7532, \"var_Sinuosity\": 0.0, \"mode_V\": 1.7976, \"mode_A\": 0.1463, \"mode_TA\": 3.7823, \"mode_Sinuosity\": 1000.0003, \"Min1_V\": 0.1006, \"Min2_V\": 0.7297, \"Min3_V\": 0.8049, \"Max1_V\": 21.9681, \"Max2_V\": 13.8389, \"Max3_V\": 13.1871, \"Min1_A\": -4.372, \"Min2_A\": -2.3617, \"Min3_A\": -1.36, \"Max1_A\": 1.9093, \"Max2_A\": 0.5626, \"Max3_A\": 0.5038, \"Min1_TA\": -165.306, \"Min2_TA\": -72.0262, \"Min3_TA\": -38.8147, \"Max1_TA\": 163.9145, \"Max2_TA\": 61.4225, \"Max3_TA\": 48.1134, \"Min1_Sinuosity\": 999.9932, \"Min2_Sinuosity\": 999.9952, \"Min3_Sinuosity\": 999.9965, \"Max1_Sinuosity\": 1000.0022, \"Max2_Sinuosity\": 1000.0018, \"Max3_Sinuosity\": 1000.0012, \"ValueRange_V\": 21.8675, \"ValueRange_A\": 6.2812, \"ValueRange_TA\": 329.2205, \"ValueRange_Sinuosity\": 0.0089, \"lowQua_V\": 3.0875, \"upQua_V\": 8.0115, \"RangeQua_V\": 4.9239, \"lowQua_A\": -0.1376, \"upQua_A\": 0.1186, \"RangeQua_A\": 0.2563, \"lowQua_TA\": -3.1861, \"upQua_TA\": 5.5488, \"RangeQua_TA\": 8.735, \"lowQua_Sinuosity\": 999.9999, \"upQua_Sinuosity\": 1000.0002, \"RangeQua_Sinuosity\": 0.0003, \"Skew_V\": 1.2856, \"Skew_A\": -3.7986, \"Skew_TA\": -0.1521, \"Skew_Sinuosity\": -3.2193, \"Kurt_V\": 3.0011, \"Kurt_A\": 24.5782, \"Kurt_TA\": 18.4615, \"Kurt_Sinuosity\": 14.2216, \"CV_V\": 1.0932, \"CV_A\": -0.2204, \"CV_TA\": 0.0013, \"CV_Sinuosity\": 679638567.949, \"AutoCC_V\": 0.3539, \"AutoCC_A\": -0.3425, \"AutoCC_TA\": -0.2813, \"AutoCC_Sinuosity\": 0.1087, \"HCR\": 0.1, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 80}, {\"user_ID\": \"3\", \"slice_ID\": \"12\", \"mode\": \"hybrid mode\", \"meanV\": -733.3989, \"meanA\": -1.1973, \"meanTA\": 3.4423, \"meanSinuosity\": 999.9997, \"var_V\": 19.0397, \"var_A\": 73.0452, \"var_TA\": 10724.2737, \"var_Sinuosity\": 0.0, \"mode_V\": 5.3355, \"mode_A\": -0.2013, \"mode_TA\": -57.4005, \"mode_Sinuosity\": 999.9988, \"Min1_V\": -0.9307, \"Min2_V\": 0.0082, \"Min3_V\": 0.3077, \"Max1_V\": 145.6316, \"Max2_V\": 57.1597, \"Max3_V\": 30.6549, \"Min1_A\": -69.1325, \"Min2_A\": -9.8349, \"Min3_A\": -4.4175, \"Max1_A\": 4.11, \"Max2_A\": 1.3345, \"Max3_A\": 0.6999, \"Min1_TA\": -335.7723, \"Min2_TA\": -286.645, \"Min3_TA\": -198.4349, \"Max1_TA\": 341.1616, \"Max2_TA\": 324.1623, \"Max3_TA\": 274.8523, \"Min1_Sinuosity\": 999.9922, \"Min2_Sinuosity\": 999.9958, \"Min3_Sinuosity\": 999.9963, \"Max1_Sinuosity\": 1000.0019, \"Max2_Sinuosity\": 1000.0018, \"Max3_Sinuosity\": 1000.0016, \"ValueRange_V\": 146.5623, \"ValueRange_A\": 73.2425, \"ValueRange_TA\": 676.9338, \"ValueRange_Sinuosity\": 0.0097, \"lowQua_V\": 2.4981, \"upQua_V\": 9.3663, \"RangeQua_V\": 6.8682, \"lowQua_A\": -0.1346, \"upQua_A\": 0.072, \"RangeQua_A\": 0.2066, \"lowQua_TA\": -21.636, \"upQua_TA\": 12.0079, \"RangeQua_TA\": 33.6439, \"lowQua_Sinuosity\": 999.9997, \"upQua_Sinuosity\": 1000.0001, \"RangeQua_Sinuosity\": 0.0003, \"Skew_V\": 6.1088, \"Skew_A\": -7.8537, \"Skew_TA\": 0.3635, \"Skew_Sinuosity\": -2.8815, \"Kurt_V\": 42.1075, \"Kurt_A\": 63.1225, \"Kurt_TA\": 4.8123, \"Kurt_Sinuosity\": 12.6384, \"CV_V\": -38.5194, \"CV_A\": -0.0164, \"CV_TA\": 0.0003, \"CV_Sinuosity\": 505258917.2899, \"AutoCC_V\": 0.9845, \"AutoCC_A\": -0.0785, \"AutoCC_TA\": -0.3653, \"AutoCC_Sinuosity\": -0.016, \"HCR\": 0.2424, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 66}, {\"user_ID\": \"3\", \"slice_ID\": \"14\", \"mode\": \"hybrid mode\", \"meanV\": -707.7421, \"meanA\": -1.4707, \"meanTA\": 4.2073, \"meanSinuosity\": 999.9998, \"var_V\": 20.6992, \"var_A\": 88.8623, \"var_TA\": 9635.9744, \"var_Sinuosity\": 0.0, \"mode_V\": 5.3355, \"mode_A\": -0.2013, \"mode_TA\": -57.4005, \"mode_Sinuosity\": 999.9988, \"Min1_V\": -4.5487, \"Min2_V\": 0.6014, \"Min3_V\": 0.7028, \"Max1_V\": 145.6316, \"Max2_V\": 57.1597, \"Max3_V\": 30.6549, \"Min1_A\": -69.1325, \"Min2_A\": -9.8349, \"Min3_A\": -4.4175, \"Max1_A\": 4.11, \"Max2_A\": 1.3345, \"Max3_A\": 0.6999, \"Min1_TA\": -335.7723, \"Min2_TA\": -173.446, \"Min3_TA\": -121.4721, \"Max1_TA\": 341.1616, \"Max2_TA\": 324.1623, \"Max3_TA\": 261.4518, \"Min1_Sinuosity\": 999.9963, \"Min2_Sinuosity\": 999.9985, \"Min3_Sinuosity\": 999.9985, \"Max1_Sinuosity\": 1000.0015, \"Max2_Sinuosity\": 1000.0013, \"Max3_Sinuosity\": 1000.0012, \"ValueRange_V\": 150.1803, \"ValueRange_A\": 73.2425, \"ValueRange_TA\": 676.9338, \"ValueRange_Sinuosity\": 0.0052, \"lowQua_V\": 4.0969, \"upQua_V\": 9.9175, \"RangeQua_V\": 5.8206, \"lowQua_A\": -0.2013, \"upQua_A\": 0.0851, \"RangeQua_A\": 0.2864, \"lowQua_TA\": -21.636, \"upQua_TA\": 6.4594, \"RangeQua_TA\": 28.0954, \"lowQua_Sinuosity\": 999.9998, \"upQua_Sinuosity\": 1000.0001, \"RangeQua_Sinuosity\": 0.0003, \"Skew_V\": 5.6236, \"Skew_A\": -7.1124, \"Skew_TA\": 0.9157, \"Skew_Sinuosity\": -1.7901, \"Kurt_V\": 35.3314, \"Kurt_A\": 51.7731, \"Kurt_TA\": 6.6285, \"Kurt_Sinuosity\": 7.5671, \"CV_V\": -34.1917, \"CV_A\": -0.0166, \"CV_TA\": 0.0004, \"CV_Sinuosity\": 1673935677.8892, \"AutoCC_V\": 0.9811, \"AutoCC_A\": -0.0838, \"AutoCC_TA\": -0.3035, \"AutoCC_Sinuosity\": -0.2593, \"HCR\": 0.2037, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 54}, {\"user_ID\": \"3\", \"slice_ID\": \"23\", \"mode\": \"car\", \"meanV\": -609.9964, \"meanA\": -0.0142, \"meanTA\": -0.2266, \"meanSinuosity\": 1000.0001, \"var_V\": 5.418, \"var_A\": 0.1015, \"var_TA\": 3493.0975, \"var_Sinuosity\": 0.0, \"mode_V\": 10.2797, \"mode_A\": -0.0366, \"mode_TA\": 0.8515, \"mode_Sinuosity\": 1000.0, \"Min1_V\": -6.9393, \"Min2_V\": 0.2483, \"Min3_V\": 0.4344, \"Max1_V\": 18.2857, \"Max2_V\": 17.6537, \"Max3_V\": 17.6061, \"Min1_A\": -1.1901, \"Min2_A\": -0.8081, \"Min3_A\": -0.7925, \"Max1_A\": 0.959, \"Max2_A\": 0.8293, \"Max3_A\": 0.4974, \"Min1_TA\": -301.5425, \"Min2_TA\": -198.2232, \"Min3_TA\": -48.5861, \"Max1_TA\": 159.7589, \"Max2_TA\": 155.3717, \"Max3_TA\": 84.885, \"Min1_Sinuosity\": 999.9954, \"Min2_Sinuosity\": 999.9975, \"Min3_Sinuosity\": 999.999, \"Max1_Sinuosity\": 1000.0052, \"Max2_Sinuosity\": 1000.0047, \"Max3_Sinuosity\": 1000.0023, \"ValueRange_V\": 25.2251, \"ValueRange_A\": 2.1491, \"ValueRange_TA\": 461.3014, \"ValueRange_Sinuosity\": 0.0097, \"lowQua_V\": 3.8304, \"upQua_V\": 11.7144, \"RangeQua_V\": 7.884, \"lowQua_A\": -0.1026, \"upQua_A\": 0.1093, \"RangeQua_A\": 0.2119, \"lowQua_TA\": -4.4321, \"upQua_TA\": 7.7478, \"RangeQua_TA\": 12.1799, \"lowQua_Sinuosity\": 999.9999, \"upQua_Sinuosity\": 1000.0, \"RangeQua_Sinuosity\": 0.0001, \"Skew_V\": -0.1861, \"Skew_A\": -0.6237, \"Skew_TA\": -2.2696, \"Skew_Sinuosity\": 1.056, \"Kurt_V\": -0.1634, \"Kurt_A\": 4.7514, \"Kurt_TA\": 14.2671, \"Kurt_Sinuosity\": 10.2846, \"CV_V\": -112.5867, \"CV_A\": -0.1401, \"CV_TA\": -0.0001, \"CV_Sinuosity\": 654343558.5633, \"AutoCC_V\": 0.9829, \"AutoCC_A\": -0.0696, \"AutoCC_TA\": -0.2209, \"AutoCC_Sinuosity\": 0.0386, \"HCR\": 0.1017, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 59}, {\"user_ID\": \"3\", \"slice_ID\": \"24\", \"mode\": \"car\", \"meanV\": 9.4546, \"meanA\": -0.1384, \"meanTA\": -4.9762, \"meanSinuosity\": 1000.0001, \"var_V\": 6.0952, \"var_A\": 1.033, \"var_TA\": 4781.0219, \"var_Sinuosity\": 0.0, \"mode_V\": 5.1891, \"mode_A\": -0.9835, \"mode_TA\": -270, \"mode_Sinuosity\": 999.9998, \"Min1_V\": 0.1282, \"Min2_V\": 0.1648, \"Min3_V\": 0.2513, \"Max1_V\": 23.224, \"Max2_V\": 22.2397, \"Max3_V\": 20.1368, \"Min1_A\": -9.0175, \"Min2_A\": -1.4146, \"Min3_A\": -0.9835, \"Max1_A\": 1.0939, \"Max2_A\": 0.5998, \"Max3_A\": 0.5071, \"Min1_TA\": -270, \"Min2_TA\": -98.4572, \"Min3_TA\": -95.2481, \"Max1_TA\": 315.0, \"Max2_TA\": 303.074, \"Max3_TA\": 150.5241, \"Min1_Sinuosity\": 999.995, \"Min2_Sinuosity\": 999.9959, \"Min3_Sinuosity\": 999.9984, \"Max1_Sinuosity\": 1000.0089, \"Max2_Sinuosity\": 1000.0037, \"Max3_Sinuosity\": 1000.003, \"ValueRange_V\": 23.0958, \"ValueRange_A\": 10.1113, \"ValueRange_TA\": 585.0, \"ValueRange_Sinuosity\": 0.014, \"lowQua_V\": 3.975, \"upQua_V\": 11.8135, \"RangeQua_V\": 7.8386, \"lowQua_A\": -0.1171, \"upQua_A\": 0.1301, \"RangeQua_A\": 0.2472, \"lowQua_TA\": -13.413, \"upQua_TA\": 5.5157, \"RangeQua_TA\": 18.9286, \"lowQua_Sinuosity\": 999.9998, \"upQua_Sinuosity\": 1000.0001, \"RangeQua_Sinuosity\": 0.0003, \"Skew_V\": 0.7029, \"Skew_A\": -7.8481, \"Skew_TA\": 0.9476, \"Skew_Sinuosity\": 2.3887, \"Kurt_V\": -0.5083, \"Kurt_A\": 68.7091, \"Kurt_TA\": 13.3406, \"Kurt_Sinuosity\": 20.4585, \"CV_V\": 1.5512, \"CV_A\": -0.134, \"CV_TA\": -0.001, \"CV_Sinuosity\": 503277209.2904, \"AutoCC_V\": 0.6764, \"AutoCC_A\": 0.0782, \"AutoCC_TA\": -0.0258, \"AutoCC_Sinuosity\": -0.0691, \"HCR\": 0.0575, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 87}, {\"user_ID\": \"3\", \"slice_ID\": \"25\", \"mode\": \"bike\", \"meanV\": -717.6837, \"meanA\": 0.001, \"meanTA\": 0.6675, \"meanSinuosity\": 1000.0001, \"var_V\": 2.9942, \"var_A\": 0.0341, \"var_TA\": 6744.0982, \"var_Sinuosity\": 0.0, \"mode_V\": 2.6816, \"mode_A\": 0.1377, \"mode_TA\": 19.829, \"mode_Sinuosity\": 1000.0038, \"Min1_V\": -3.1985, \"Min2_V\": 0.2247, \"Min3_V\": 0.2541, \"Max1_V\": 10.2076, \"Max2_V\": 9.9656, \"Max3_V\": 9.8869, \"Min1_A\": -0.603, \"Min2_A\": -0.4475, \"Min3_A\": -0.3718, \"Max1_A\": 0.4723, \"Max2_A\": 0.4065, \"Max3_A\": 0.2449, \"Min1_TA\": -349.4761, \"Min2_TA\": -81.8318, \"Min3_TA\": -76.5009, \"Max1_TA\": 321.4347, \"Max2_TA\": 294.1455, \"Max3_TA\": 177.0263, \"Min1_Sinuosity\": 999.9962, \"Min2_Sinuosity\": 999.9972, \"Min3_Sinuosity\": 999.9972, \"Max1_Sinuosity\": 1000.0048, \"Max2_Sinuosity\": 1000.0039, \"Max3_Sinuosity\": 1000.0038, \"ValueRange_V\": 13.4062, \"ValueRange_A\": 1.0753, \"ValueRange_TA\": 670.9108, \"ValueRange_Sinuosity\": 0.0086, \"lowQua_V\": 2.6781, \"upQua_V\": 6.9528, \"RangeQua_V\": 4.2747, \"lowQua_A\": -0.0614, \"upQua_A\": 0.119, \"RangeQua_A\": 0.1804, \"lowQua_TA\": -28.9945, \"upQua_TA\": 7.925, \"RangeQua_TA\": 36.9195, \"lowQua_Sinuosity\": 999.9996, \"upQua_Sinuosity\": 1000.0003, \"RangeQua_Sinuosity\": 0.0007, \"Skew_V\": 0.0779, \"Skew_A\": -0.5814, \"Skew_TA\": 0.6253, \"Skew_Sinuosity\": 0.7267, \"Kurt_V\": -0.5119, \"Kurt_A\": 1.608, \"Kurt_TA\": 9.6942, \"Kurt_Sinuosity\": 1.9058, \"CV_V\": -239.6896, \"CV_A\": 0.0282, \"CV_TA\": 0.0001, \"CV_Sinuosity\": 406977736.0472, \"AutoCC_V\": 0.9849, \"AutoCC_A\": -0.1454, \"AutoCC_TA\": -0.181, \"AutoCC_Sinuosity\": 0.0365, \"HCR\": 0.197, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 66}, {\"user_ID\": \"3\", \"slice_ID\": \"28\", \"mode\": \"hybrid mode\", \"meanV\": -219.3726, \"meanA\": -1.0624, \"meanTA\": -0.3008, \"meanSinuosity\": 1000.0002, \"var_V\": 22.1029, \"var_A\": 38.471, \"var_TA\": 7210.1052, \"var_Sinuosity\": 0.0, \"mode_V\": 4.6872, \"mode_A\": -0.302, \"mode_TA\": 2.7263, \"mode_Sinuosity\": 1000.0009, \"Min1_V\": -2.3332, \"Min2_V\": 0.1295, \"Min3_V\": 0.1434, \"Max1_V\": 170.9094, \"Max2_V\": 29.5108, \"Max3_V\": 14.8335, \"Min1_A\": -47.1329, \"Min2_A\": -9.7076, \"Min3_A\": -3.4563, \"Max1_A\": 1.7923, \"Max2_A\": 0.368, \"Max3_A\": 0.3088, \"Min1_TA\": -257.2919, \"Min2_TA\": -219.7961, \"Min3_TA\": -179.0195, \"Max1_TA\": 348.5034, \"Max2_TA\": 251.5651, \"Max3_TA\": 188.2998, \"Min1_Sinuosity\": 999.9924, \"Min2_Sinuosity\": 999.9954, \"Min3_Sinuosity\": 999.9979, \"Max1_Sinuosity\": 1000.0127, \"Max2_Sinuosity\": 1000.0058, \"Max3_Sinuosity\": 1000.0034, \"ValueRange_V\": 173.2426, \"ValueRange_A\": 48.9251, \"ValueRange_TA\": 605.7953, \"ValueRange_Sinuosity\": 0.0203, \"lowQua_V\": 2.0447, \"upQua_V\": 6.0092, \"RangeQua_V\": 3.9645, \"lowQua_A\": -0.2185, \"upQua_A\": 0.0829, \"RangeQua_A\": 0.3014, \"lowQua_TA\": -10.0953, \"upQua_TA\": 5.5696, \"RangeQua_TA\": 15.6649, \"lowQua_Sinuosity\": 999.9998, \"upQua_Sinuosity\": 1000.0004, \"RangeQua_Sinuosity\": 0.0006, \"Skew_V\": 7.2228, \"Skew_A\": -7.2273, \"Skew_TA\": 0.8176, \"Skew_Sinuosity\": 2.2453, \"Kurt_V\": 54.0215, \"Kurt_A\": 54.0291, \"Kurt_TA\": 7.357, \"Kurt_Sinuosity\": 16.7296, \"CV_V\": -9.925, \"CV_A\": -0.0276, \"CV_TA\": -0.0, \"CV_Sinuosity\": 186538568.6969, \"AutoCC_V\": 0.9755, \"AutoCC_A\": 0.1357, \"AutoCC_TA\": -0.5666, \"AutoCC_Sinuosity\": -0.0564, \"HCR\": 0.1356, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 59}, {\"user_ID\": \"4\", \"slice_ID\": \"0\", \"mode\": \"hybrid mode\", \"meanV\": 5.2076, \"meanA\": -0.0691, \"meanTA\": 0.0686, \"meanSinuosity\": 1000.0, \"var_V\": 6.6951, \"var_A\": 0.9039, \"var_TA\": 9091.5783, \"var_Sinuosity\": 0.0, \"mode_V\": 2.1227, \"mode_A\": -0.0922, \"mode_TA\": 43.0355, \"mode_Sinuosity\": 999.9919, \"Min1_V\": 0.0084, \"Min2_V\": 0.0142, \"Min3_V\": 0.0158, \"Max1_V\": 45.6927, \"Max2_V\": 31.238, \"Max3_V\": 24.2186, \"Min1_A\": -17.9565, \"Min2_A\": -11.979, \"Min3_A\": -8.5417, \"Max1_A\": 2.0067, \"Max2_A\": 1.8577, \"Max3_A\": 1.4395, \"Min1_TA\": -356.0548, \"Min2_TA\": -355.1647, \"Min3_TA\": -354.5067, \"Max1_TA\": 357.6218, \"Max2_TA\": 352.1096, \"Max3_TA\": 351.096, \"Min1_Sinuosity\": 999.9797, \"Min2_Sinuosity\": 999.9884, \"Min3_Sinuosity\": 999.989, \"Max1_Sinuosity\": 1000.0173, \"Max2_Sinuosity\": 1000.0111, \"Max3_Sinuosity\": 1000.0104, \"ValueRange_V\": 45.6843, \"ValueRange_A\": 19.9633, \"ValueRange_TA\": 713.6766, \"ValueRange_Sinuosity\": 0.0376, \"lowQua_V\": 2.1227, \"upQua_V\": 10.3662, \"RangeQua_V\": 8.2435, \"lowQua_A\": -0.0815, \"upQua_A\": 0.0555, \"RangeQua_A\": 0.137, \"lowQua_TA\": -19.8201, \"upQua_TA\": 14.9698, \"RangeQua_TA\": 34.7899, \"lowQua_Sinuosity\": 999.9999, \"upQua_Sinuosity\": 1000.0002, \"RangeQua_Sinuosity\": 0.0003, \"Skew_V\": 1.2573, \"Skew_A\": -13.1655, \"Skew_TA\": 0.4673, \"Skew_Sinuosity\": -0.5029, \"Kurt_V\": 1.5071, \"Kurt_A\": 212.004, \"Kurt_TA\": 5.1066, \"Kurt_Sinuosity\": 20.2434, \"CV_V\": 0.7778, \"CV_A\": -0.0764, \"CV_TA\": 0.0, \"CV_Sinuosity\": 204760167.0469, \"AutoCC_V\": 0.7722, \"AutoCC_A\": -0.0767, \"AutoCC_TA\": -0.3084, \"AutoCC_Sinuosity\": 0.0496, \"HCR\": 0.2157, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 751}, {\"user_ID\": \"4\", \"slice_ID\": \"1\", \"mode\": \"hybrid mode\", \"meanV\": 4.5291, \"meanA\": -0.0782, \"meanTA\": 0.0189, \"meanSinuosity\": 1000.0, \"var_V\": 22.4779, \"var_A\": 11.1795, \"var_TA\": 10289.7834, \"var_Sinuosity\": 0.0, \"mode_V\": 1.8258, \"mode_A\": -0.1037, \"mode_TA\": 135.0, \"mode_Sinuosity\": 1000.0016, \"Min1_V\": 0.0048, \"Min2_V\": 0.0077, \"Min3_V\": 0.0092, \"Max1_V\": 563.5034, \"Max2_V\": 213.2335, \"Max3_V\": 98.0166, \"Min1_A\": -46.2367, \"Min2_A\": -28.9917, \"Min3_A\": -14.4205, \"Max1_A\": 67.802, \"Max2_A\": 17.0536, \"Max3_A\": 3.7452, \"Min1_TA\": -356.1775, \"Min2_TA\": -352.332, \"Min3_TA\": -347.4712, \"Max1_TA\": 355.3141, \"Max2_TA\": 352.6994, \"Max3_TA\": 352.6548, \"Min1_Sinuosity\": 999.9827, \"Min2_Sinuosity\": 999.9862, \"Min3_Sinuosity\": 999.9884, \"Max1_Sinuosity\": 1000.016, \"Max2_Sinuosity\": 1000.0133, \"Max3_Sinuosity\": 1000.0122, \"ValueRange_V\": 563.4986, \"ValueRange_A\": 114.0388, \"ValueRange_TA\": 711.4916, \"ValueRange_Sinuosity\": 0.0333, \"lowQua_V\": 2.6128, \"upQua_V\": 10.3467, \"RangeQua_V\": 7.7339, \"lowQua_A\": -0.1037, \"upQua_A\": 0.0845, \"RangeQua_A\": 0.1882, \"lowQua_TA\": -23.1125, \"upQua_TA\": 17.8799, \"RangeQua_TA\": 40.9924, \"lowQua_Sinuosity\": 999.9998, \"upQua_Sinuosity\": 1000.0002, \"RangeQua_Sinuosity\": 0.0003, \"Skew_V\": 20.5731, \"Skew_A\": 6.5179, \"Skew_TA\": 0.2005, \"Skew_Sinuosity\": -0.1091, \"Kurt_V\": 489.9727, \"Kurt_A\": 274.1355, \"Kurt_TA\": 3.9554, \"Kurt_Sinuosity\": 18.6313, \"CV_V\": 0.2015, \"CV_A\": -0.007, \"CV_TA\": 0.0, \"CV_Sinuosity\": 212267485.2884, \"AutoCC_V\": 0.0704, \"AutoCC_A\": -0.3263, \"AutoCC_TA\": -0.3572, \"AutoCC_Sinuosity\": -0.1681, \"HCR\": 0.2413, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 775}, {\"user_ID\": \"4\", \"slice_ID\": \"2\", \"mode\": \"hybrid mode\", \"meanV\": 3.333, \"meanA\": -0.1827, \"meanTA\": -0.1519, \"meanSinuosity\": 999.9999, \"var_V\": 7.2816, \"var_A\": 4.0622, \"var_TA\": 12008.9395, \"var_Sinuosity\": 0.0, \"mode_V\": 3.0915, \"mode_A\": -0.133, \"mode_TA\": 92.2906, \"mode_Sinuosity\": 999.9998, \"Min1_V\": 0.006, \"Min2_V\": 0.008, \"Min3_V\": 0.0112, \"Max1_V\": 98.8924, \"Max2_V\": 50.4619, \"Max3_V\": 45.0075, \"Min1_A\": -22.9684, \"Min2_A\": -22.4473, \"Min3_A\": -21.8918, \"Max1_A\": 2.1385, \"Max2_A\": 1.8273, \"Max3_A\": 1.7876, \"Min1_TA\": -356.5834, \"Min2_TA\": -353.3015, \"Min3_TA\": -343.5065, \"Max1_TA\": 356.5585, \"Max2_TA\": 356.4657, \"Max3_TA\": 356.2696, \"Min1_Sinuosity\": 999.9906, \"Min2_Sinuosity\": 999.9909, \"Min3_Sinuosity\": 999.9912, \"Max1_Sinuosity\": 1000.0167, \"Max2_Sinuosity\": 1000.0097, \"Max3_Sinuosity\": 1000.0095, \"ValueRange_V\": 98.8864, \"ValueRange_A\": 25.1069, \"ValueRange_TA\": 713.1419, \"ValueRange_Sinuosity\": 0.0261, \"lowQua_V\": 3.1047, \"upQua_V\": 10.5722, \"RangeQua_V\": 7.4675, \"lowQua_A\": -0.1087, \"upQua_A\": 0.1052, \"RangeQua_A\": 0.2139, \"lowQua_TA\": -25.9961, \"upQua_TA\": 20.3645, \"RangeQua_TA\": 46.3607, \"lowQua_Sinuosity\": 999.9998, \"upQua_Sinuosity\": 1000.0002, \"RangeQua_Sinuosity\": 0.0004, \"Skew_V\": 5.3491, \"Skew_A\": -9.9569, \"Skew_TA\": 0.2919, \"Skew_Sinuosity\": 0.777, \"Kurt_V\": 57.3571, \"Kurt_A\": 103.9999, \"Kurt_TA\": 3.927, \"Kurt_Sinuosity\": 20.4288, \"CV_V\": 0.4577, \"CV_A\": -0.045, \"CV_TA\": -0.0, \"CV_Sinuosity\": 273515278.2692, \"AutoCC_V\": 0.467, \"AutoCC_A\": -0.064, \"AutoCC_TA\": -0.3619, \"AutoCC_Sinuosity\": -0.0469, \"HCR\": 0.2588, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 456}, {\"user_ID\": \"5\", \"slice_ID\": \"0\", \"mode\": \"hybrid mode\", \"meanV\": 1.0557, \"meanA\": -0.015, \"meanTA\": -0.9331, \"meanSinuosity\": 1000.0, \"var_V\": 3.0411, \"var_A\": 0.0124, \"var_TA\": 9427.1006, \"var_Sinuosity\": 0.0, \"mode_V\": 1.6222, \"mode_A\": -0.0116, \"mode_TA\": 0.5943, \"mode_Sinuosity\": 1000.0, \"Min1_V\": 0.3807, \"Min2_V\": 0.4024, \"Min3_V\": 0.4365, \"Max1_V\": 28.0036, \"Max2_V\": 2.6054, \"Max3_V\": 1.9872, \"Min1_A\": -1.0006, \"Min2_A\": -0.0808, \"Min3_A\": -0.0419, \"Max1_A\": 0.0199, \"Max2_A\": 0.0167, \"Max3_A\": 0.0111, \"Min1_TA\": -324.4573, \"Min2_TA\": -274.3594, \"Min3_TA\": -223.9584, \"Max1_TA\": 272.4309, \"Max2_TA\": 267.2522, \"Max3_TA\": 259.5534, \"Min1_Sinuosity\": 999.9993, \"Min2_Sinuosity\": 999.9994, \"Min3_Sinuosity\": 999.9995, \"Max1_Sinuosity\": 1000.0007, \"Max2_Sinuosity\": 1000.0006, \"Max3_Sinuosity\": 1000.0006, \"ValueRange_V\": 27.6229, \"ValueRange_A\": 1.0205, \"ValueRange_TA\": 596.8883, \"ValueRange_Sinuosity\": 0.0014, \"lowQua_V\": 0.689, \"upQua_V\": 1.3995, \"RangeQua_V\": 0.7105, \"lowQua_A\": -0.0051, \"upQua_A\": 0.0027, \"RangeQua_A\": 0.0078, \"lowQua_TA\": -31.0869, \"upQua_TA\": 21.4394, \"RangeQua_TA\": 52.5263, \"lowQua_Sinuosity\": 999.9999, \"upQua_Sinuosity\": 1000.0002, \"RangeQua_Sinuosity\": 0.0003, \"Skew_V\": 8.6535, \"Skew_A\": -8.842, \"Skew_TA\": 0.0314, \"Skew_Sinuosity\": 0.0937, \"Kurt_V\": 76.489, \"Kurt_A\": 79.0287, \"Kurt_TA\": 3.4874, \"Kurt_Sinuosity\": 0.154, \"CV_V\": 0.3472, \"CV_A\": -1.2026, \"CV_TA\": -0.0001, \"CV_Sinuosity\": 12712333046.0794, \"AutoCC_V\": 0.0394, \"AutoCC_A\": 0.0365, \"AutoCC_TA\": -0.2492, \"AutoCC_Sinuosity\": 0.1305, \"HCR\": 0.275, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 80}, {\"user_ID\": \"5\", \"slice_ID\": \"4\", \"mode\": \"bike\", \"meanV\": 1.7141, \"meanA\": -0.0059, \"meanTA\": -3.2075, \"meanSinuosity\": 1000.0, \"var_V\": 1.2283, \"var_A\": 0.0007, \"var_TA\": 17617.8806, \"var_Sinuosity\": 0.0, \"mode_V\": 4.0797, \"mode_A\": -0.0179, \"mode_TA\": -19.44, \"mode_Sinuosity\": 1000.0, \"Min1_V\": 0.2948, \"Min2_V\": 0.3107, \"Min3_V\": 0.3317, \"Max1_V\": 4.6834, \"Max2_V\": 4.4206, \"Max3_V\": 4.0797, \"Min1_A\": -0.1024, \"Min2_A\": -0.0683, \"Min3_A\": -0.0621, \"Max1_A\": 0.0509, \"Max2_A\": 0.0301, \"Max3_A\": 0.0298, \"Min1_TA\": -345.4087, \"Min2_TA\": -319.0177, \"Min3_TA\": -277.0127, \"Max1_TA\": 349.5712, \"Max2_TA\": 282.3636, \"Max3_TA\": 265.7819, \"Min1_Sinuosity\": 999.9991, \"Min2_Sinuosity\": 999.9994, \"Min3_Sinuosity\": 999.9994, \"Max1_Sinuosity\": 1000.0007, \"Max2_Sinuosity\": 1000.0004, \"Max3_Sinuosity\": 1000.0004, \"ValueRange_V\": 4.3886, \"ValueRange_A\": 0.1533, \"ValueRange_TA\": 694.9799, \"ValueRange_Sinuosity\": 0.0016, \"lowQua_V\": 0.9688, \"upQua_V\": 3.1406, \"RangeQua_V\": 2.1717, \"lowQua_A\": -0.0168, \"upQua_A\": 0.0079, \"RangeQua_A\": 0.0247, \"lowQua_TA\": -40.3102, \"upQua_TA\": 35.0943, \"RangeQua_TA\": 75.4045, \"lowQua_Sinuosity\": 1000.0, \"upQua_Sinuosity\": 1000.0001, \"RangeQua_Sinuosity\": 0.0002, \"Skew_V\": -0.0031, \"Skew_A\": -1.233, \"Skew_TA\": 0.0138, \"Skew_Sinuosity\": -0.9655, \"Kurt_V\": -1.2185, \"Kurt_A\": 2.4182, \"Kurt_TA\": 1.4714, \"Kurt_Sinuosity\": 2.8678, \"CV_V\": 1.3954, \"CV_A\": -8.2982, \"CV_TA\": -0.0002, \"CV_Sinuosity\": 15874844014.2726, \"AutoCC_V\": 0.4083, \"AutoCC_A\": -0.1848, \"AutoCC_TA\": -0.3007, \"AutoCC_Sinuosity\": 0.1354, \"HCR\": 0.2903, \"SR\": 1.0, \"VCR\": 0.0, \"length\": 62}]";

                System.out.println("res = " + resultStr);
                if ("fail".equals(resultStr) || "error".equals(resultStr)) {
                    System.out.println(resultStr);
                    rstData.setCode(-1);
                    return rstData;
                } else {
                    proDataStr = resultStr;
                    List<STResult> traceResList = gson.fromJson(resultStr, new TypeToken<List<STResult>>() {
                    }.getType());

                    if (traceResList.isEmpty()) {
                        rstData.setCode(-1);
                        return rstData;
                    } else {
                        for (STResult traceRes : traceResList) {
                            Integer userId = Integer.valueOf(traceRes.getUser_ID());
                            Integer traceId = Integer.valueOf(traceRes.getSlice_ID());
                            Integer travelMode = getMode(traceRes.getMode());
                            //循环处理路径端点
                            for (TravelPoint travelPoint : pointList) {
                                if (userId == travelPoint.getUserId() && traceId == travelPoint.getFromTag()) {
                                    TravelPoint proTravelPoint = new TravelPoint();
                                    BeanUtils.copyProperties(travelPoint, proTravelPoint);
                                    proTravelPoint.setWeight(travelMode);
                                    pointResultList.add(proTravelPoint);
                                } else {
                                    continue;
                                }
                            }
                            //循环处理路径上的点
                            for (List<TravelPoint> tps : pathList) {
                                Integer tpUserId = tps.get(0).getUserId();
                                Integer tpTraceId = tps.get(0).getFromTag();
                                if (tpUserId == userId && tpTraceId == traceId) {
                                    List<TravelPoint> tpList = new ArrayList<>();
                                    for (TravelPoint tp : tps) {
                                        TravelPoint proTp = new TravelPoint();
                                        BeanUtils.copyProperties(tp, proTp);
                                        proTp.setWeight(travelMode);
                                        tpList.add(proTp);
                                    }
                                    resultList.add(tpList);
                                } else {
                                    continue;
                                }
                            }
                        }
                    }
                    String deeledPointDataStr = gson.toJson(pointResultList);
                    String deeledPathDataStr = gson.toJson(resultList);
                    stTraceDataDao.updateProData(md5, proDataStr, deeledPathDataStr, deeledPointDataStr);
                }
            }else{
                String deeledPointDataStr = traceData.getDeeledPointData();
                String deeledPathDataStr = traceData.getDeeledPathData();

                resultList = gson.fromJson(deeledPathDataStr, new TypeToken<List<List<TravelPoint>>>(){}.getType());
                pointResultList = gson.fromJson(deeledPointDataStr, new TypeToken<List<TravelPoint>>(){}.getType());
            }
            userPaths = gson.fromJson(userPathStr, new TypeToken<List<UserPath>>(){}.getType());
            pathInfo.setMd5(md5);
            pathInfo.setUserPaths(userPaths);
            pathInfo.setPathInfos(resultList);
            pathInfo.setPathPoints(pointResultList);
            rstData.setData(pathInfo);
            rstData.setCode(1);
        }
        return rstData;
    }

    public RstData<STResult> processStInfo(TraceInfoReq data) {

        RstData<STResult> rstData = new RstData<>();
        STResult stResult = new STResult();
        String md5 = data.getMd5();
        Integer userId = data.getUserId();
        Integer traceId = data.getTraceId();

        if(md5 == null || userId == null || traceId == null){
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }

        TraceData traceData = stTraceDataDao.findByMd5(md5);

        String proData = traceData.getProData();

        if(proData == null || "".equals(proData)){
            rstData.setCode(-2);
            rstData.setMsg("传入数据有误");
            return rstData;
        }else{
            List<STResult> traceResList = gson.fromJson(proData, new TypeToken<List<STResult>>(){}.getType());

            for(STResult tr : traceResList){
                Integer curUserId = Integer.valueOf(tr.getUser_ID());
                Integer curTraceId = Integer.valueOf(tr.getSlice_ID());
                if(curTraceId == traceId && curUserId == userId){
                    BeanUtils.copyProperties(tr, stResult);
                    stResult.setStHCR(tr.getHCR());
                    stResult.setStLength(tr.getLength());
                    stResult.setStSR(tr.getSR());
                    stResult.setStVCR(tr.getVCR());
                    break;
                }
            }
        }
        rstData.setCode(1);
        rstData.setData(stResult);
        return rstData;
    }


    public RstData<PathInfo> processStSearch(TraceInfoReq data) {

        RstData<PathInfo> rstData = new RstData<>();
        PathInfo pathInfo = new PathInfo();
        List<List<TravelPoint>> resultList = new ArrayList<>();
        List<TravelPoint> pointResultList = new ArrayList<>();
        String md5 = data.getMd5();
        Integer userId = data.getUserId();
        Integer traceId = data.getTraceId();

        if(md5 == null){
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }

        TraceData traceData = stTraceDataDao.findByMd5(md5);

        String deeledPointDataStr = traceData.getDeeledPointData();
        String deeledPathDataStr = traceData.getDeeledPathData();

        List<List<TravelPoint>> pathList = gson.fromJson(deeledPathDataStr, new TypeToken<List<List<TravelPoint>>>(){}.getType());
        List<TravelPoint> pointList = gson.fromJson(deeledPointDataStr, new TypeToken<List<TravelPoint>>(){}.getType());

        if((userId == null || "".equals(userId)) && (traceId == null || "".equals(traceId))){
            resultList = pathList;
            pointResultList = pointList;
        }else if((userId == null || "".equals(userId)) && (traceId != null && !"".equals(traceId))){
            for (TravelPoint travelPoint : pointList) {
                if (traceId == travelPoint.getFromTag()) {
                    TravelPoint proTravelPoint = new TravelPoint();
                    BeanUtils.copyProperties(travelPoint, proTravelPoint);
                    pointResultList.add(proTravelPoint);
                } else {
                    continue;
                }
            }
            //循环处理路径上的点
            for (List<TravelPoint> tps : pathList) {
                Integer tpUserId = tps.get(0).getUserId();
                Integer tpTraceId = tps.get(0).getFromTag();
                if (tpTraceId == traceId) {
                    List<TravelPoint> tpList = new ArrayList<>();
                    for (TravelPoint tp : tps) {
                        TravelPoint proTp = new TravelPoint();
                        BeanUtils.copyProperties(tp, proTp);
                        tpList.add(proTp);
                    }
                    resultList.add(tpList);
                } else {
                    continue;
                }
            }
        }else if((userId != null && !"".equals(userId)) && (traceId == null || "".equals(traceId))){
            for (TravelPoint travelPoint : pointList) {
                if (userId == travelPoint.getUserId()) {
                    TravelPoint proTravelPoint = new TravelPoint();
                    BeanUtils.copyProperties(travelPoint, proTravelPoint);
                    pointResultList.add(proTravelPoint);
                } else {
                    continue;
                }
            }
            //循环处理路径上的点
            for (List<TravelPoint> tps : pathList) {
                Integer tpUserId = tps.get(0).getUserId();
                Integer tpTraceId = tps.get(0).getFromTag();
                if (tpUserId == userId) {
                    List<TravelPoint> tpList = new ArrayList<>();
                    for (TravelPoint tp : tps) {
                        TravelPoint proTp = new TravelPoint();
                        BeanUtils.copyProperties(tp, proTp);
                        tpList.add(proTp);
                    }
                    resultList.add(tpList);
                } else {
                    continue;
                }
            }
        }else{
            for (TravelPoint travelPoint : pointList) {
                if (userId == travelPoint.getUserId() && traceId == travelPoint.getFromTag()) {
                    TravelPoint proTravelPoint = new TravelPoint();
                    BeanUtils.copyProperties(travelPoint, proTravelPoint);
                    pointResultList.add(proTravelPoint);
                } else {
                    continue;
                }
            }
            //循环处理路径上的点
            for (List<TravelPoint> tps : pathList) {
                Integer tpUserId = tps.get(0).getUserId();
                Integer tpTraceId = tps.get(0).getFromTag();
                if (tpUserId == userId && tpTraceId == traceId) {
                    List<TravelPoint> tpList = new ArrayList<>();
                    for (TravelPoint tp : tps) {
                        TravelPoint proTp = new TravelPoint();
                        BeanUtils.copyProperties(tp, proTp);
                        tpList.add(proTp);
                    }
                    resultList.add(tpList);
                } else {
                    continue;
                }
            }
        }

        pathInfo.setPathPoints(pointResultList);
        pathInfo.setPathInfos(resultList);

        rstData.setData(pathInfo);
        rstData.setCode(1);
        return  rstData;

    }
}

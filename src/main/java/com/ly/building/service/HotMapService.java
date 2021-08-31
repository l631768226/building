package com.ly.building.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ly.building.common.CommonMethod;
import com.ly.building.mapper.HotMapDao;
import com.ly.building.mapper.SFDataDao;
import com.ly.building.mapper.TableDao;
import com.ly.building.model.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class HotMapService {

    private Gson gson = new Gson();

    @Autowired
    private TableDao tableDao;

    @Autowired
    private HotMapDao hotMapDao;

    @Autowired
    private SFDataDao sfDataDao;


    @Value("${hm.flaskUrl}")
    String flaskUrl;

    @Value("${hm.sourceFilePath}")
    String sourceFilePath;

    @Value("${hm.zbSourceFilePath}")
    String zbSourceFilePath;

    @Value("${hm.zbResultPath}")
    String zbResultPath;

    @Value("${hm.zbResultFilePath}")
    String zbResultFilePath;

    @Value("${hm.distFilePath}")
    String distFilePath;

    @Value("${hm.analyseResultPath}")
    String analyseResultPath;

    @Value("${hm.distFilePath2}")
    String distFilePath2;

    @Value("${hm.periodResultPath}")
    String periodResultPath;

    @Value("${hm.analysisFilePath}")
    String analysisFilePath;

    @Value("${hm.activeResultPath}")
    String activeResultPath;

    @Value("${hm.allUserPath}")
    String allUserPath;

    @Value("${hm.userCountPath}")
    String userCountPath;

    public RstData<String> processInit(HttpServletRequest request) {
        RstData<String> rstData = new RstData<>();
        try {
            Part part = request.getPart("file");
            String value = request.getParameter("value");

            System.out.println("the value name = " + value);

            UploadData uploadData = gson.fromJson(value, new TypeToken<UploadData>() {
            }.getType());
            int type = uploadData.getType();
            String tableName = uploadData.getTableName();

            System.out.println("the table name = " + tableName);

            InputStream ins = part.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            String line = null;

            List<HotMapInfo> hotMapInfos = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                try {
                    String item[] = line.split(",");//CSV格式文件时候的分割符,我使用的是,号
                    HotMapInfo hotMapInfo = new HotMapInfo();
                    hotMapInfo.setUserId(Integer.valueOf(item[0]));
                    String yearStr = item[1];
                    int year = Integer.valueOf(yearStr);
                    hotMapInfo.setYear(year);
                    hotMapInfo.setSeason(Integer.valueOf(item[2]));

                    String monthStr = item[3];
                    int month = Integer.valueOf(monthStr);
                    hotMapInfo.setMonth(month);

                    String dayStr = item[4];
                    int day = Integer.valueOf(dayStr);
                    hotMapInfo.setDay(day);
                    hotMapInfo.setWeek(Integer.valueOf(item[5]));
                    hotMapInfo.setWorkday(Integer.valueOf(item[6]));

                    String hourStr = item[7];
                    int hour = Integer.valueOf(hourStr);
                    hotMapInfo.setHour1(hour);
                    hotMapInfo.setHour2(Integer.valueOf(item[8]));
                    hotMapInfo.setLat(Double.valueOf(item[9]));
                    hotMapInfo.setLon(Double.valueOf(item[10]));
                    hotMapInfo.setPlaceName(item[11]);
                    hotMapInfo.setLabelStr(item[12]);
                    hotMapInfo.setWeight(Double.valueOf(item[13]));
                    hotMapInfo.setCluster(Integer.valueOf(item[14]));
                    if (day < 10) {
                        dayStr = "0" + dayStr;
                    }
                    if (month < 10) {
                        monthStr = "0" + monthStr;
                    }
                    if (hour < 10) {
                        hourStr = "0" + hourStr;
                    }
                    String dateStr = yearStr + "-" + monthStr + "-" + dayStr;
                    String timeStr = dateStr + " " + hourStr;
                    hotMapInfo.setDateStr(dateStr);
                    hotMapInfo.setTimeStr(timeStr);
                    hotMapInfos.add(hotMapInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

            if (!hotMapInfos.isEmpty()) {
                //创建对应的数据表
//                boolean tableTag = CommonMethod.createHotMapTable(dbAddress, dbName, userName, password, tableName);

//                if(tableTag){
                //建表成功
                int resultTag = tableDao.insertIntoInfo(tableName, hotMapInfos.size(), type);

                if (resultTag > 0) {
                    //批量插入

                    int resultCount = hotMapDao.insertHopMap(tableName, hotMapInfos);
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


            } else {
                rstData.setCode(-1);
            }


        } catch (IOException e) {
            e.printStackTrace();
            rstData.setCode(-2);
        } catch (ServletException e) {
            e.printStackTrace();
            rstData.setCode(-2);
        }
        return rstData;
    }


    public RstData<List<MapResult>> processList(HotRule hotRule) {
        RstData<List<MapResult>> rstData = new RstData<>();

        String label = hotRule.getLabel();
        if (label == null || "".equals(label)) {
            label = null;
        }

        Integer userId = hotRule.getUserId();
        if (userId == null || userId == 0) {
            userId = null;
        }

        int zoneType = hotRule.getZoneType();

        Integer month = hotRule.getMonth();
        if (month == null || month == 0) {
            month = null;
        }
        String tableName = "";

        if (zoneType == 1) {
            tableName = "nyinfo";
        } else {
            tableName = "tkinfo";
        }

        String dateStr = hotRule.getDateStr();
        if (dateStr == null || "".equals(dateStr)) {
            dateStr = null;
        }

        List<HotMapInfo> hotMapInfos = new ArrayList<>();

        if (userId == null && label == null && dateStr == null) {
            hotMapInfos = hotMapDao.findAllListByRules(tableName);
        } else {
            hotMapInfos = hotMapDao.findListByRules(tableName, userId, label, month, dateStr);
        }

//        hotMapInfos = hotMapDao.findListByRules(tableName, userId, label, month, dateStr);

        if (hotMapInfos.isEmpty()) {
            rstData.setCode(1);
            rstData.setData(new ArrayList<>());
            return rstData;
        } else {
            List<MapResult> mapResults = new ArrayList<>();
            for (HotMapInfo hotMapInfo : hotMapInfos) {
                MapResult mapResult = new MapResult();
                Geo geo = new Geo();
                geo.setCount(hotMapInfo.getWeight().intValue() * 10000);
                geo.setLat(hotMapInfo.getLat());
                geo.setLng(hotMapInfo.getLon());
                mapResult.setGeo(geo);

                mapResult.setUserId(hotMapInfo.getUserId());
                mapResult.setLabelStr(hotMapInfo.getLabelStr());
                mapResult.setLat(String.valueOf(hotMapInfo.getLat()));
                mapResult.setLon(String.valueOf(hotMapInfo.getLon()));
                mapResult.setPlaceName(hotMapInfo.getPlaceName());
                mapResult.setPoi("");
                mapResult.setTimeStr(hotMapInfo.getTimeStr());
                mapResult.setStayTime("");

                mapResult.setInfo("");
                mapResults.add(mapResult);
            }

            rstData.setCode(1);
            rstData.setData(mapResults);
            return rstData;
        }
    }


    public RstData<List<Geo>> processHotList(HotRule data) {

        RstData<List<Geo>> rstData = new RstData<>();

        String label = data.getLabel();
        if (label == null || "".equals(label)) {
            label = null;
        }

        Integer userId = data.getUserId();
        if (userId == null || userId == 0) {
            userId = null;
        }

        Integer zoneType = data.getZoneType();

        Integer month = data.getMonth();
        if (month == null || month == 0) {
            month = null;
        }
        String tableName = "";

        if (zoneType == null || zoneType == 1) {
            tableName = "nyinfo";
        } else {
            tableName = "tkinfo";
        }

        String dateStr = data.getDateStr();
        if (dateStr == null || "".equals(dateStr)) {
            dateStr = null;
        }

        List<HotMapInfo> hotMapInfos = new ArrayList<>();

        if (userId == null && label == null && dateStr == null) {
            hotMapInfos = hotMapDao.findAllListByRules(tableName);
        } else {
            hotMapInfos = hotMapDao.findListByRules(tableName, userId, label, month, dateStr);
        }


        if (hotMapInfos.isEmpty()) {
            rstData.setCode(1);
            rstData.setData(new ArrayList<Geo>());
            return rstData;
        } else {
            List<Geo> mapResults = new ArrayList<>();
            for (HotMapInfo hotMapInfo : hotMapInfos) {
                Geo geo = new Geo();
                geo.setCount((int) (hotMapInfo.getWeight() * 10000));
                geo.setLat(hotMapInfo.getLat());
                geo.setLng(hotMapInfo.getLon());
                mapResults.add(geo);
            }

            rstData.setCode(1);
            rstData.setData(mapResults);
            return rstData;
        }

    }

    /**
     * 获取label列表
     *
     * @param listModel
     * @return
     */
    public RstData<List<String>> processLabelList(ListModel listModel) {

        RstData<List<String>> rstData = new RstData<>();

        String tableName = "";

        Integer zoneType = listModel.getZoneType();

        if (zoneType == null || zoneType == 1) {
            tableName = "nyinfo";
        } else {
            tableName = "tkinfo";
        }

        List<String> resultList = new ArrayList<>();

        List<String> labelList = hotMapDao.findLabelList(tableName);

        resultList.add("");
        if (!labelList.isEmpty()) {
            resultList.addAll(labelList);
        }

        rstData.setCode(1);
        rstData.setData(resultList);

        return rstData;
    }

    /**
     * 查询日期，组成列表返回
     *
     * @param listModel
     * @return
     */
    public RstData<List<String>> processDateList(ListModel listModel) {

        RstData<List<String>> rstData = new RstData<>();

        String dateStr = listModel.getDateStr();
        if (dateStr == null || "".equals(dateStr)) {
            dateStr = null;
        }

        Integer userId = listModel.getUserId();
        if (userId == null || userId == 0) {
            userId = null;
        }

        String labelStr = listModel.getLabelStr();
        if (labelStr == null || "".equals(labelStr)) {
            labelStr = null;
        }

        Integer zoneType = listModel.getZoneType();
        String tableName = "";
        if (zoneType == null || zoneType == 1) {
            tableName = "nyinfo";
        } else {
            tableName = "tkinfo";
        }

        List<String> resultList = hotMapDao.findDateList(tableName, dateStr, userId, labelStr);

        rstData.setCode(1);
        rstData.setData(resultList);
        return rstData;
    }

    public RstData<String> processUploadFile(HttpServletRequest request) {

        RstData<String> rstData = new RstData<>();

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
                        System.out.println(sourceFilePath);
                        File file = new File(sourceFilePath);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        os = new FileOutputStream(sourceFilePath);
                        while ((len = ins.read(buffer)) > -1) {
                            os.write(buffer, 0, len);
                        }

                        InputStream insCopy = new FileInputStream(file);
                        md5 = DigestUtils.md5Hex(insCopy);
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

            File file = new File(sourceFilePath);
            if (!file.exists() || !tag) {
                rstData.setCode(-1);
                return rstData;
            }
            rstData.setCode(1);
            rstData.setData(md5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rstData;
    }


    public RstData<AnalysisRes> processAnalysis(HMapInfo data) {

        RstData<AnalysisRes> rstData = new RstData<>();

        String md5 = data.getMd5();

        if (md5 == null || "".equals(md5)) {
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }
        AnalysisRes analysisRes = new AnalysisRes();
        SFData sfData = sfDataDao.findByMd5(md5);

        String anaResult = "";
        String pointMap = "";
        String countMap = "";
        String userListStr = "";

        if (sfData == null) {
            List<FormParam> formParams = new ArrayList<>();
            FormParam formParam = new FormParam();
            formParam.setKey("filePath");
            formParam.setValue(sourceFilePath);
            formParams.add(formParam);

            FormParam distFormParam = new FormParam();
            distFormParam.setKey("distPath");
            distFormParam.setValue(distFilePath);
            formParams.add(distFormParam);

            String resultStr = CommonMethod.postFormJson(flaskUrl + "/analysis", formParams);
            System.out.println("res = " + resultStr);
            if ("fail".equals(resultStr) || "error".equals(resultStr)) {
                System.out.println(resultStr);
                rstData.setCode(-1);
                return rstData;
            } else {
                List<Integer> userIdList = gson.fromJson(resultStr, new TypeToken<List<Integer>>() {
                }.getType());
                Collections.sort(userIdList);
                analysisRes.setUserIdList(userIdList);
                analysisRes.setTag(1);

                List<String> imgList = new ArrayList<>();
                List<AnsCsv> ansCsvList = new ArrayList<>();
                try {
                    File file = new File(analysisFilePath);
                    InputStreamReader read = new InputStreamReader(
                            new FileInputStream(file), StandardCharsets.UTF_8);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;

                    Map<Integer, Integer> placeCount = new HashMap<>();
                    Map<Integer, PlaceLocation> placeLocate = new HashMap<>();
                    while ((lineTxt = bufferedReader.readLine()) != null) {
//                        System.out.println(lineTxt);
                        String dataS[] = lineTxt.split(",");
                        if ("user_id".equals(dataS[0])) {
                            continue;
                        }
                        Integer userId = Integer.valueOf(dataS[0]);
                        Integer placeId = Integer.valueOf(dataS[1]);
                        Double lat = Double.valueOf(dataS[2]);
                        Double lng = Double.valueOf(dataS[3]);
                        String timeStr = dataS[4];
                        String label = dataS[5];

                        AnsCsv ansCsv = new AnsCsv();
                        ansCsv.setUserId(userId);
                        ansCsv.setPlaceId(placeId);
                        ansCsv.setLabel(label);
                        ansCsv.setLat(lat);
                        ansCsv.setLng(lng);
                        ansCsv.setTimeStr(timeStr);
                        ansCsvList.add(ansCsv);

                        Integer count = placeCount.get(placeId);
                        if (count == null || count == 0) {
                            count = 1;
                        } else {
                            count++;
                        }
                        placeCount.put(placeId, count);

                        PlaceLocation placeLocation = placeLocate.get(placeId);
                        if (placeLocation == null) {
                            placeLocation = new PlaceLocation();
                            placeLocation.setLat(lat);
                            placeLocation.setLng(lng);
                            placeLocation.setUserId(userId);
                            placeLocation.setLabel(label);
                            placeLocation.setTimeStr(timeStr);
                            placeLocate.put(placeId, placeLocation);
                        } else {
                            continue;
                        }
                    }

                    List<MapResult> mapResults = new ArrayList<>();
                    for (Integer key : placeLocate.keySet()) {
//                        System.out.println("key = " + key);
                        PlaceLocation placeLocation = placeLocate.get(key);
                        MapResult mapResult = new MapResult();
                        mapResult.setLat(placeLocation.getLat().toString());
                        mapResult.setLon(placeLocation.getLng().toString());
                        mapResult.setLabelStr(placeLocation.getLabel());
                        mapResult.setUserId(placeLocation.getUserId());
                        mapResult.setTimeStr(placeLocation.getTimeStr());
                        mapResults.add(mapResult);
                    }
                    analysisRes.setMapResults(mapResults);

                    String imgStr = CommonMethod.getImageStr(allUserPath);
                    imgList.add(imgStr);
                    analysisRes.setImgList(imgList);

                    countMap = gson.toJson(placeCount);
                    pointMap = gson.toJson(placeLocate);
                    anaResult = gson.toJson(ansCsvList);
                    userListStr = gson.toJson(userIdList);

                    sfDataDao.insertSFData(md5, anaResult, pointMap, countMap, userListStr);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                rstData.setData(analysisRes);
            }
        } else {
            pointMap = sfData.getPointMap();
            userListStr = sfData.getUserList();
            System.out.println(pointMap);
            List<Integer> userIdList = gson.fromJson(userListStr, new TypeToken<List<Integer>>() {
            }.getType());
            HashMap<Integer, PlaceLocation> placeLocate = gson.fromJson(pointMap, new TypeToken<HashMap<Integer, PlaceLocation>>() {
            }.getType());

            List<MapResult> mapResults = new ArrayList<>();
            for (Integer key : placeLocate.keySet()) {
//                System.out.println("key = " + key);
                PlaceLocation placeLocation = placeLocate.get(key);
                MapResult mapResult = new MapResult();
                mapResult.setLat(placeLocation.getLat().toString());
                mapResult.setLon(placeLocation.getLng().toString());
                mapResult.setLabelStr(placeLocation.getLabel());
                mapResult.setUserId(placeLocation.getUserId());
                mapResult.setTimeStr(placeLocation.getTimeStr());
                mapResults.add(mapResult);
            }
            analysisRes.setMapResults(mapResults);
            analysisRes.setUserIdList(userIdList);
            analysisRes.setTag(1);

            String imgStr = CommonMethod.getImageStr(allUserPath);
            List<String> imgList = new ArrayList<>();
            imgList.add(imgStr);
            analysisRes.setImgList(imgList);

            rstData.setCode(1);
            rstData.setData(analysisRes);
        }

        return rstData;
    }

    public RstData<SFHotMaptRst> processSFHotMap(SFHotMapReq data) {

        RstData<SFHotMaptRst> rstData = new RstData<>();

        String md5 = data.getMd5();

        if (md5 == null || "".equals(md5)) {
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }


        SFData sfData = sfDataDao.findByMd5(md5);

        if (sfData == null) {
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }

        List<String> imgList = new ArrayList<>();
        List<Geo> geoList = new ArrayList<>();

        Integer userId = data.getUserId();
        if (userId == null || userId == -1) {
            String pointMap = sfData.getPointMap();
            String countMap = sfData.getCountMap();

            HashMap<Integer, PlaceLocation> placeLocate = gson.fromJson(pointMap, new TypeToken<HashMap<Integer, PlaceLocation>>() {
            }.getType());
            HashMap<Integer, Integer> placeCount = gson.fromJson(countMap, new TypeToken<HashMap<Integer, Integer>>() {
            }.getType());
            for (Integer key : placeLocate.keySet()) {
//                System.out.println("key = " + key);
                PlaceLocation placeLocation = placeLocate.get(key);
                Integer count = placeCount.get(key);
                Geo geo = new Geo();
                geo.setLng(placeLocation.getLng());
                geo.setLat(placeLocation.getLat());
                geo.setCount(count);
                geoList.add(geo);
            }

            String imgStr = CommonMethod.getImageStr(allUserPath);
            imgList.add(imgStr);
        } else {
            String imgPath = distFilePath + userId + ".png";
            String imgStr = CommonMethod.getImageStr(imgPath);
            imgList.add(imgStr);

            String anaResult = sfData.getAnaResult();

            List<AnsCsv> ansCsvList = gson.fromJson(anaResult, new TypeToken<List<AnsCsv>>() {
            }.getType());

            HashMap<Integer, PlaceLocation> placeLocate = new HashMap<>();
            HashMap<Integer, Integer> placeCount = new HashMap<>();
            for (AnsCsv ansCsv : ansCsvList) {
                Integer csvUserId = ansCsv.getUserId();
                Integer placeId = ansCsv.getPlaceId();
                if (userId == csvUserId) {
                    Integer count = placeCount.get(placeId);
                    if (count == null || count == 0) {
                        count = 1;
                    } else {
                        count++;
                    }
                    placeCount.put(placeId, count);

                    PlaceLocation placeLocation = placeLocate.get(placeId);
                    if (placeLocation == null) {
                        placeLocation = new PlaceLocation();
                        placeLocation.setLat(ansCsv.getLat());
                        placeLocation.setLng(ansCsv.getLng());
                        placeLocation.setLabel(ansCsv.getLabel());
                        placeLocation.setUserId(ansCsv.getUserId());
                        placeLocation.setTimeStr(ansCsv.getTimeStr());
                        placeLocate.put(placeId, placeLocation);
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }

            for (Integer key : placeLocate.keySet()) {
                PlaceLocation placeLocation = placeLocate.get(key);
                Integer count = placeCount.get(key);
                Geo geo = new Geo();
                geo.setLng(placeLocation.getLng());
                geo.setLat(placeLocation.getLat());
                geo.setCount(count);
                geoList.add(geo);
            }
        }

        SFHotMaptRst sfHotMaptRst = new SFHotMaptRst();
        sfHotMaptRst.setImgList(imgList);
        sfHotMaptRst.setGeoList(geoList);

        rstData.setCode(1);
        rstData.setData(sfHotMaptRst);
        return rstData;
    }

    public RstData<SFPointRst> processSFPoint(SFPointReq data) {

        RstData<SFPointRst> rstData = new RstData<>();

        String md5 = data.getMd5();

        if (md5 == null || "".equals(md5)) {
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }


        SFData sfData = sfDataDao.findByMd5(md5);

        if (sfData == null) {
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }

        List<String> imgList = new ArrayList<>();
        List<MapResult> mapResults = new ArrayList<>();

        Integer userId = data.getUserId();
        if (userId == null || userId == -1) {
            String pointMap = sfData.getPointMap();
            String countMap = sfData.getCountMap();

            HashMap<Integer, PlaceLocation> placeLocate = gson.fromJson(pointMap, new TypeToken<HashMap<Integer, PlaceLocation>>() {
            }.getType());
            HashMap<Integer, Integer> placeCount = gson.fromJson(countMap, new TypeToken<HashMap<Integer, Integer>>() {
            }.getType());
            for (Integer key : placeLocate.keySet()) {
                PlaceLocation placeLocation = placeLocate.get(key);
                MapResult mapResult = new MapResult();
                mapResult.setLat(placeLocation.getLat().toString());
                mapResult.setLon(placeLocation.getLng().toString());
                mapResult.setLabelStr(placeLocation.getLabel());
                mapResult.setUserId(placeLocation.getUserId());
                mapResult.setTimeStr(placeLocation.getTimeStr());
                mapResults.add(mapResult);
            }

            String imgStr = CommonMethod.getImageStr(allUserPath);
            imgList.add(imgStr);
        } else {

            String imgPath = distFilePath + userId + ".png";
            String imgStr = CommonMethod.getImageStr(imgPath);
            imgList.add(imgStr);

            String anaResult = sfData.getAnaResult();

            List<AnsCsv> ansCsvList = gson.fromJson(anaResult, new TypeToken<List<AnsCsv>>() {
            }.getType());

            HashMap<Integer, PlaceLocation> placeLocate = new HashMap<>();

            for (AnsCsv ansCsv : ansCsvList) {
                Integer csvUserId = ansCsv.getUserId();
                Integer placeId = ansCsv.getPlaceId();
                if (userId == csvUserId) {

                    PlaceLocation placeLocation = placeLocate.get(placeId);
                    if (placeLocation == null) {
                        placeLocation = new PlaceLocation();
                        placeLocation.setLat(ansCsv.getLat());
                        placeLocation.setLng(ansCsv.getLng());
                        placeLocation.setLabel(ansCsv.getLabel());
                        placeLocation.setUserId(ansCsv.getUserId());
                        placeLocation.setTimeStr(ansCsv.getTimeStr());
                        placeLocate.put(placeId, placeLocation);
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            for (Integer key : placeLocate.keySet()) {
                PlaceLocation placeLocation = placeLocate.get(key);
                MapResult mapResult = new MapResult();
                mapResult.setLat(placeLocation.getLat().toString());
                mapResult.setLon(placeLocation.getLng().toString());
                mapResult.setLabelStr(placeLocation.getLabel());
                mapResult.setUserId(placeLocation.getUserId());
                mapResult.setTimeStr(placeLocation.getTimeStr());
                mapResults.add(mapResult);
            }
        }

        SFPointRst sfPointRst = new SFPointRst();
        sfPointRst.setImgList(imgList);
        sfPointRst.setMapResultList(mapResults);

        rstData.setCode(1);
        rstData.setData(sfPointRst);
        return rstData;
    }

    public RstData<PeriodRst> processSFPeriod(PeriodReq data) {

        RstData<PeriodRst> rstData = new RstData<>();

        PeriodRst periodRst = new PeriodRst();

        String md5 = data.getMd5();

        if (md5 == null || "".equals(md5)) {
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }

        Integer inputUserId = data.getUserId();

        SFData sfData = sfDataDao.findByMd5(md5);

        if (sfData == null) {
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }

        List<Integer> placeIdList = new ArrayList<>();
        HashMap<Integer, Double> periodMap = new HashMap<>();

        String periodStr = sfData.getPeriodInfo();

        if (periodStr == null || "".equals(periodStr)) {
            List<FormParam> formParams = new ArrayList<>();
            FormParam formParam = new FormParam();
            formParam.setKey("distPath1");
            formParam.setValue(analyseResultPath);
            formParams.add(formParam);

            FormParam distFormParam = new FormParam();
            distFormParam.setKey("distPath2");
            distFormParam.setValue(distFilePath2);
            formParams.add(distFormParam);

            String resultStr = CommonMethod.postFormJson(flaskUrl + "/period", formParams);
            System.out.println("res = " + resultStr);
            if ("fail".equals(resultStr) || "error".equals(resultStr)) {
                System.out.println(resultStr);
                rstData.setCode(-1);
                return rstData;
            } else {
                File file = new File(periodResultPath);
                if (!file.exists()) {
                    rstData.setCode(-1);
                    return rstData;
                } else {
                    try {

                        InputStreamReader read = new InputStreamReader(
                                new FileInputStream(file), StandardCharsets.UTF_8);//考虑到编码格式
                        BufferedReader bufferedReader = new BufferedReader(read);
                        String lineTxt = null;
                        List<PeriodInfo> periodInfos = new ArrayList<>();
                        while ((lineTxt = bufferedReader.readLine()) != null) {
//                        System.out.println(lineTxt);
                            String dataS[] = lineTxt.split(",");
                            if ("user_id".equals(dataS[0])) {
                                continue;
                            } else {
                                Integer userId = Integer.valueOf(dataS[0]);
                                Integer placeId = Integer.valueOf(dataS[1]);
                                Double lat = Double.valueOf(dataS[2]);
                                Double lng = Double.valueOf(dataS[3]);
                                String timeStr = dataS[4];
                                Double period = Double.valueOf(dataS[5]);
                                String label = dataS[6];

                                PeriodInfo periodInfo = new PeriodInfo();
                                periodInfo.setUserId(userId);
                                periodInfo.setPlaceId(placeId);
                                periodInfo.setLat(lat);
                                periodInfo.setLng(lng);
                                periodInfo.setLabelStr(label);
                                periodInfo.setTimeStr(timeStr);
                                periodInfo.setPeriod(period);
                                periodInfos.add(periodInfo);

                                if(inputUserId == null || inputUserId == -1 || inputUserId == userId) {
                                    if (placeIdList.contains(placeId)) {
                                        continue;
                                    } else {
                                        placeIdList.add(placeId);
                                        periodMap.put(placeId, period);
                                    }
                                }else {
                                    continue;
                                }
                            }
                        }

                        periodStr = gson.toJson(periodInfos);

                        sfDataDao.updateSFData(md5, periodStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            List<PeriodInfo> periodInfos = gson.fromJson(periodStr, new TypeToken<List<PeriodInfo>>(){}.getType());
            for(PeriodInfo periodInfo : periodInfos){
                Integer placeId = periodInfo.getPlaceId();
                Integer userId = periodInfo.getUserId();
                if(inputUserId == null || inputUserId == -1 || inputUserId == userId) {
                    Double period = periodInfo.getPeriod();
                    if (placeIdList.contains(placeId)) {
                        continue;
                    } else {
                        placeIdList.add(placeId);
                        periodMap.put(placeId, period);
                    }
                }else {
                    continue;
                }
            }
        }

        Collections.sort(placeIdList);
        List<Double> periodList = new ArrayList<>();
        for (Integer intPlaceId : placeIdList) {
            Double douPeriod = periodMap.get(intPlaceId);
            periodList.add(douPeriod);
        }
        periodRst.setPeriodList(periodList);
        periodRst.setPlaceIdList(placeIdList);

        rstData.setData(periodRst);
        rstData.setCode(1);
        return rstData;

    }


    public RstData<ActRst> processSFActive(ActiveInfo data) {

        RstData<ActRst> rstData = new RstData<>();

        Integer userId = data.getUserId();

        if(userId == null || userId == -1){
            rstData.setCode(-1);
            return rstData;
        }

        ActRst actRst = new ActRst();

        List<FormParam> formParams = new ArrayList<>();
        FormParam formParam = new FormParam();
        formParam.setKey("distPath2");
        formParam.setValue(periodResultPath);
        formParams.add(formParam);

        FormParam distFormParam = new FormParam();
        distFormParam.setKey("distPath3");
        distFormParam.setValue(distFilePath);
        formParams.add(distFormParam);

        FormParam userForm = new FormParam();
        userForm.setKey("userId");
        userForm.setValue(userId.toString());
        formParams.add(userForm);

        String resultStr = CommonMethod.postFormJson(flaskUrl + "/active", formParams);
        System.out.println("res = " + resultStr);
        if ("fail".equals(resultStr) || "error".equals(resultStr)) {
            System.out.println(resultStr);
            rstData.setCode(-1);
            return rstData;
        } else {
            File file = new File(activeResultPath);
            if(!file.exists()){
                rstData.setCode(-1);
                return rstData;
            }
            try {

                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                List<ActiveRes> activeResList = new ArrayList<>();

                List<MapResult> allPoints = new ArrayList<>();

                List<MapResult> rightPoints = new ArrayList<>();
                List<MapResult> errorPoints = new ArrayList<>();
                while ((lineTxt = bufferedReader.readLine()) != null) {
//                        System.out.println(lineTxt);
                    String dataS[] = lineTxt.split(",");
                    if ("user_id".equals(dataS[0])) {
                        continue;
                    } else {
                        String real = dataS[12];
                        String pred = dataS[11];
                        Double lat = Double.valueOf(dataS[2]);
                        Double lng = Double.valueOf(dataS[1]);
//                        ActiveRes activeRes = new ActiveRes();
//                        activeRes.setUserId(userId);
//                        activeRes.setYearTime(dataS[0]);
//                        activeRes.setMonthTime(dataS[1]);
//                        activeRes.setDayTime(dataS[2]);
//                        activeRes.setWeekTime(dataS[3]);
//                        activeRes.setLng(lng);
//                        activeRes.setLat(lat);
//                        activeRes.setPeriod(Double.valueOf(dataS[6]));
//                        activeRes.setReal(dataS[7]);
//                        activeRes.setPred(dataS[8]);
//                        activeResList.add(activeRes);

                        MapResult mapResult = new MapResult();
                        mapResult.setLat(dataS[2]);
                        mapResult.setLon(dataS[1]);

                        mapResult.setLabelStr(real);
                        mapResult.setPred(pred);
                        mapResult.setUserId(userId);
                        allPoints.add(mapResult);
                        if(real.equals(pred)){
                            rightPoints.add(mapResult);
                        }else{
                            errorPoints.add(mapResult);
                        }
                    }
                }

                actRst.setAllPoints(allPoints);
                actRst.setRightPoints(rightPoints);
                actRst.setErrorPoints(errorPoints);
                actRst.setSum(allPoints.size());
                actRst.setErrorCount(errorPoints.size());
                actRst.setRightCount(rightPoints.size());
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        rstData.setCode(1);
        rstData.setData(actRst);
        return rstData;
    }

    public RstData<ZbActRst> processZbActive(ZbActReq data) {
        RstData<ZbActRst> rstData = new RstData<>();
        ZbActRst zbActRst = new ZbActRst();
        List<UserCount> userCountList = new ArrayList<>();
        String md5 = data.getMd5();
        if(md5 == null || "".equals(md5)){
            rstData.setCode(-1);
            rstData.setMsg("传入参数有误");
            return rstData;
        }

        Integer userId = data.getUserId();
        Integer count = data.getCount();

        List<FormParam> formParams = new ArrayList<>();
        FormParam formParam = new FormParam();
        formParam.setKey("userId");
        formParam.setValue(userId.toString());
        formParams.add(formParam);

        FormParam countformParam = new FormParam();
        countformParam.setKey("count");
        countformParam.setValue(count.toString());
        formParams.add(countformParam);

        FormParam fileformParam = new FormParam();
        fileformParam.setKey("filePath");
        fileformParam.setValue(zbSourceFilePath);
        formParams.add(fileformParam);

        FormParam resultformParam = new FormParam();
        resultformParam.setKey("resultPath");
        resultformParam.setValue(zbResultPath);
        formParams.add(resultformParam);

        String resultStr = CommonMethod.postFormJson(flaskUrl + "/zbactive", formParams);
        System.out.println("res = " + resultStr);
        if ("fail".equals(resultStr) || "error".equals(resultStr)) {
            System.out.println(resultStr);
            rstData.setCode(-1);
            return rstData;
        } else {
            File file = new File(zbResultFilePath);
            if (!file.exists()) {
                rstData.setCode(-1);
                return rstData;
            }
            try {

                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                List<MapResult> allResult = new ArrayList<>();
                List<MapResult> errorResult = new ArrayList<>();
                List<MapResult> rightResult = new ArrayList<>();
                while ((lineTxt = bufferedReader.readLine()) != null) {
//                        System.out.println(lineTxt);
                    String dataS[] = lineTxt.split(",");
                    if("user".equals(dataS[0])){
                        continue;
                    }else{
                        MapResult mapResult = new MapResult();
                        mapResult.setUserId(Integer.valueOf(dataS[0]));
                        mapResult.setLat(dataS[1]);
                        mapResult.setLon(dataS[2]);
                        mapResult.setPlaceName(dataS[4]);
                        String real = dataS[5];
                        String pred = dataS[6];
                        mapResult.setLabelStr(real);
                        mapResult.setPred(pred);

                        String hotValue = dataS[3];

                        mapResult.setHotValue(hotValue);

                        String vector = dataS[7];
                        List<String> vectorList = Arrays.asList(vector.split(" "));
//                        vectorList.remove("");
                        mapResult.setVectorList(vectorList);
                        mapResult.setVector(vector);

                        allResult.add(mapResult);
                        if(real.equals(pred)){
                            rightResult.add(mapResult);
                        }else{
                            errorResult.add(mapResult);
                        }
                    }
                    zbActRst.setAllPoints(allResult);
                    zbActRst.setErrorPoints(errorResult);
                    zbActRst.setRightPoints(rightResult);
                    zbActRst.setSum(allResult.size());
                    zbActRst.setErrorCount(errorResult.size());
                    zbActRst.setRightCount(rightResult.size());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        File file = new File(userCountPath);
        if (!file.exists()) {
            rstData.setCode(-1);
            return rstData;
        }
        try {
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8);//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            String headline[] = null;

            while ((lineTxt = bufferedReader.readLine()) != null) {
                String dataS[] = lineTxt.split(",");
                if("user".equals(dataS[0])){
                    headline = dataS;
                    continue;
                }else{
                    if(userId == Integer.valueOf(dataS[0])){
                        for(int i = 1; i < dataS.length; i ++){
                            UserCount userCount = new UserCount();
                            userCount.setCount(Integer.valueOf(dataS[i]));
                            userCount.setLabel(headline[i]);
                            userCountList.add(userCount);
                        }
                        break;
                    }else{
                        continue;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        userCountList.sort(Comparator.comparing(UserCount::getCount));
        Collections.reverse(userCountList);

        //这里需要展示排名前三的用户 不足三个的展示全部
        int userCount = userCountList.size();
        if(userCount > 3){
            zbActRst.setUserCounts(userCountList.subList(0,3));
        }else{
            zbActRst.setUserCounts(userCountList);
        }

        rstData.setCode(1);
        rstData.setData(zbActRst);
        return  rstData;
    }

    public RstData<String> processZbUploadFile(HttpServletRequest request) {

        RstData<String> rstData = new RstData<>();

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

                        File file = new File(zbSourceFilePath);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        os = new FileOutputStream(zbSourceFilePath);
                        while ((len = ins.read(buffer)) > -1) {
                            os.write(buffer, 0, len);
                        }

                        InputStream insCopy = new FileInputStream(file);
                        md5 = DigestUtils.md5Hex(insCopy);
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

            File file = new File(zbSourceFilePath);
            if (!file.exists() || !tag) {
                rstData.setCode(-1);
                return rstData;
            }
            rstData.setCode(1);
            rstData.setData(md5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rstData;
    }

    public RstData<List<Geo>> processZBHotMap(SFHotMapReq data) {
        RstData<List<Geo>> rstData = new RstData<>();

        String md5 = data.getMd5();
        if(md5 == null || "".equals(md5)){
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }

        List<Geo> geoList = new ArrayList<>();
        File file = new File(zbResultFilePath);
        if (!file.exists()) {
            rstData.setCode(-1);
            return rstData;
        }
        try {

            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8);//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;

            while ((lineTxt = bufferedReader.readLine()) != null) {
//                        System.out.println(lineTxt);
                String dataS[] = lineTxt.split(",");
                if ("user".equals(dataS[0])) {
                    continue;
                } else {
                    Geo geo = new Geo();
                    geo.setLat(Double.valueOf(dataS[1]));
                    geo.setLng(Double.valueOf(dataS[2]));
                    Integer weight = (int) (Double.valueOf(dataS[3]) * 100);
                    geo.setCount(weight);
                    geoList.add(geo);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        rstData.setCode(1);
        rstData.setData(geoList);
        return rstData;
    }

    public RstData<List<MapResult>> processZBPoint(SFHotMapReq data) {

        RstData<List<MapResult>> rstData = new RstData<>();

        String md5 = data.getMd5();
        if(md5 == null || "".equals(md5)){
            rstData.setCode(-1);
            rstData.setMsg("传入数据有误");
            return rstData;
        }

        List<MapResult> mapResultList = new ArrayList<>();
        File file = new File(zbResultFilePath);
        if (!file.exists()) {
            rstData.setCode(-1);
            return rstData;
        }
        try {

            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8);//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;

            while ((lineTxt = bufferedReader.readLine()) != null) {
//                        System.out.println(lineTxt);
                String dataS[] = lineTxt.split(",");
                if ("user".equals(dataS[0])) {
                    continue;
                } else {
                    MapResult mapResult = new MapResult();
                    mapResult.setUserId(Integer.valueOf(dataS[0]));
                    mapResult.setLat(dataS[1]);
                    mapResult.setLon(dataS[2]);
                    mapResult.setPlaceName(dataS[4]);
                    String real = dataS[5];
                    String pred = dataS[6];
                    mapResult.setLabelStr(real);
                    mapResult.setPred(pred);
                    mapResultList.add(mapResult);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        rstData.setCode(1);
        rstData.setData(mapResultList);
        return rstData;
    }



}

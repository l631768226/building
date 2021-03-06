package com.ly.building.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ly.building.common.CommonMethod;
import com.ly.building.mapper.CalInfoDao;
import com.ly.building.model.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.*;
import java.util.*;

@Service
public class CalService {

    private Gson gson = new Gson();

    @Value("${cal.filePath}")
    String filePath;

    @Value("${cal.resultPath}")
    String resultPath;

    @Value("${cal.flaskUrl}")
    String flaskUrl;

    @Value("${cal.baseDataPath}")
    String baseDataPath;

    @Value("${cal.fileResultPath}")
    String fileResultPath;

    @Value("${cal.buildingPath}")
    String buildingPath;

    @Value("${cal.realFilePath}")
    String realFilePath;

    @Value("${cal.flaskBuildingUrl}")
    String flaskBuildingUrl;

    @Value("${cal.qnSourceFilePath}")
    String qnSourceFilePath;

    @Value("${cal.qnTmpFilePath}")
    String qnTmpFilePath;

    @Value("${cal.qnBasePath}")
    String qnBasePath;

    @Value("${cal.qnImgPath}")
    String qnImgPath;

    @Value("${cal.qnShowPath}")
    String qnShowPath;

    @Value("${cal.qnValPath}")
    String qnValPath;

    @Value("${cal.qnVal1Path}")
    String qnVal1Path;

    @Value("${cal.qnResultPath}")
    String qnResultPath;

    @Autowired
    private CalInfoDao calInfoDao;

    public RstData<List<String>> uploadFile(HttpServletRequest request) {

        RstData<List<String>> rstData = new RstData<>();

        List<String> picStrList = new ArrayList<>();

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

            List<FormParam> formParams = new ArrayList<>();
            FormParam formParam = new FormParam();
            formParam.setKey("filePath");
            formParam.setValue(filePath);
            formParams.add(formParam);

            String resultStr = CommonMethod.postFormJson(flaskUrl + "/cal", formParams);
            System.out.println("res = " + resultStr);
            if ("fail".equals(resultStr) || "error".equals(resultStr)) {
                System.out.println(resultStr);
                rstData.setCode(-1);
                return rstData;
            } else {
                file.delete();
                String zskPath = resultPath;

                String zskStr = CommonMethod.getImageStr(zskPath);
                picStrList.add(zskStr);
                rstData.setCode(1);
                rstData.setData(picStrList);
                return rstData;
            }

        } catch (Exception e) {
            e.printStackTrace();
            rstData.setCode(-1);
            return rstData;
        }
    }


    public RstData<SearchResult> searchFile(HttpServletRequest request) {

        RstData<SearchResult> rstData = new RstData<>();

        SearchResult searchResult = new SearchResult();

        String value = request.getParameter("num");
        if (value == null || "".equals(value)) {
            value = "0.999";
        }

        try {
            Collection<Part> parts = request.getParts();
            Iterator<Part> it = parts.iterator();

            boolean tag = true;

            String md5 = "";

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
            String resultStr = "error";
            String deeledStr = "";
            if (md5 == null || "".equals(md5)) {

            } else {
                CalInfo calInfo = calInfoDao.findByMd5(md5, value);
                if (calInfo == null) {
                    List<FormParam> formParams = new ArrayList<>();
                    FormParam fileFormParam = new FormParam();
                    fileFormParam.setKey("filePath");
                    fileFormParam.setValue(filePath);
                    formParams.add(fileFormParam);

                    FormParam baseFormParam = new FormParam();
                    baseFormParam.setKey("basePath");
                    baseFormParam.setValue(baseDataPath);
                    formParams.add(baseFormParam);

                    FormParam threFormParam = new FormParam();
                    threFormParam.setKey("threshold");
                    Threshold threshold = new Threshold();
                    threshold.setThreshold(Double.valueOf(value));
                    threFormParam.setValue(gson.toJson(threshold));
                    formParams.add(threFormParam);

                    resultStr = CommonMethod.postFormJson(flaskUrl + "/search", formParams);

                    System.out.println("res = " + resultStr);
                    if ("fail".equals(resultStr) || "error".equals(resultStr)) {
                        System.out.println(resultStr);
                        rstData.setCode(-1);
                        return rstData;
                    } else {
                        String resultStrCopy = resultStr;
                        resultStrCopy = resultStrCopy.replace(" ", "");
                        resultStrCopy = resultStrCopy.replace("\\n", "");
                        resultStrCopy = resultStrCopy.replace("\\", "");
                        resultStrCopy = resultStrCopy.replace("\"{", "{");
                        resultStrCopy = resultStrCopy.replace("}\"", "}");
                        resultStrCopy = resultStrCopy.replace("??????", "price");
                        resultStrCopy = resultStrCopy.replace("????????????", "placeN");
                        resultStrCopy = resultStrCopy.replace("???????????????", "placeL");
                        resultStrCopy = resultStrCopy.replace("????????????", "placeD");
                        resultStrCopy = resultStrCopy.replace("????????????", "placeA");
                        resultStrCopy = resultStrCopy.replace("?????????", "rate");
                        resultStrCopy = resultStrCopy.replace("????????????", "time");
                        resultStrCopy = resultStrCopy.replace("?????????", "greeningRate");
                        resultStrCopy = resultStrCopy.replace("??????ID", "ID");
                        resultStrCopy = resultStrCopy.replace("nan", "");
                        resultStrCopy = resultStrCopy.replace(".0", "");
                        System.out.println(resultStrCopy);

                        deeledStr = resultStrCopy;

                        calInfoDao.insertCal(md5, resultStr, deeledStr, value);
                    }
                } else {
                    resultStr = calInfo.getJson();
                    deeledStr = calInfo.getDeeledJson();
                }
            }


//            String resultStr = "{\n" +
//                    "    \"30\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 78774.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 91,\\n    \\\"????????????\\\": 54578.666970119535,\\n    \\\"?????????\\\": 0.0,\\n    \\\"????????????\\\": \\\"nan\\\",\\n    \\\"?????????\\\": 0.0,\\n    \\\"??????ID\\\": 30.0\\n}\",\n" +
//                    "    \"114\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 122281.0,\\n    \\\"????????????\\\": \\\"??????????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 91,\\n    \\\"????????????\\\": 64687.59232928281,\\n    \\\"?????????\\\": 0.0,\\n    \\\"????????????\\\": \\\"nan\\\",\\n    \\\"?????????\\\": 0.0,\\n    \\\"??????ID\\\": 114.0\\n}\",\n" +
//                    "    \"237\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 73000.0,\\n    \\\"????????????\\\": \\\"????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 86,\\n    \\\"????????????\\\": 42538.32340448014,\\n    \\\"?????????\\\": 3.9,\\n    \\\"????????????\\\": \\\"2004-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 22.0,\\n    \\\"??????ID\\\": 237.0\\n}\",\n" +
//                    "    \"287\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 48000.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 90,\\n    \\\"????????????\\\": 64742.24835764785,\\n    \\\"?????????\\\": 1.6,\\n    \\\"????????????\\\": \\\"1984-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 30.0,\\n    \\\"??????ID\\\": 287.0\\n}\",\n" +
//                    "    \"424\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 107000.0,\\n    \\\"????????????\\\": \\\"?????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 87,\\n    \\\"????????????\\\": 45003.303155363385,\\n    \\\"?????????\\\": 2.3,\\n    \\\"????????????\\\": \\\"2004-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 32.0,\\n    \\\"??????ID\\\": 424.0\\n}\",\n" +
//                    "    \"490\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 68000.0,\\n    \\\"????????????\\\": \\\"??????????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 90,\\n    \\\"????????????\\\": 51189.734812317394,\\n    \\\"?????????\\\": 4.1,\\n    \\\"????????????\\\": \\\"2005-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 29.0,\\n    \\\"??????ID\\\": 490.0\\n}\",\n" +
//                    "    \"522\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 81000.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 93,\\n    \\\"????????????\\\": 44109.665615814585,\\n    \\\"?????????\\\": 3.0,\\n    \\\"????????????\\\": \\\"1991-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 20.0,\\n    \\\"??????ID\\\": 522.0\\n}\",\n" +
//                    "    \"771\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 77000.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 90,\\n    \\\"????????????\\\": 65017.50087512024,\\n    \\\"?????????\\\": 2.8,\\n    \\\"????????????\\\": \\\"1988-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 33.0,\\n    \\\"??????ID\\\": 771.0\\n}\"\n" +
//                    "}";


            searchResult.setJson(resultStr);

            file.delete();


            List<SearchFileInfo> searchFileInfos = new ArrayList<>();

            try {
                Map<String, PlaceModel> map = gson.fromJson(deeledStr, new TypeToken<Map<String, PlaceModel>>() {
                }.getType());
                Set set = map.keySet();
                for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();
                    PlaceModel placeModel = (PlaceModel)map.get(key);
                    String fileStr = CommonMethod.getImageStr(fileResultPath + key + ".jpg");
                    SearchFileInfo searchFileInfo = new SearchFileInfo();
                    searchFileInfo.setFileName(key);

                    searchFileInfo.setFileStr(fileStr);

                    List<TableData> tableDataList = new ArrayList<>();

                    TableData idTableData = new TableData();
                    idTableData.setId("????????????");
                    idTableData.setName("??????ID");
                    idTableData.setAmount(placeModel.getID());
                    tableDataList.add(idTableData);

                    TableData pnTableData = new TableData();
                    pnTableData.setId("????????????");
                    pnTableData.setName("????????????");
                    pnTableData.setAmount(placeModel.getPlaceN());
                    tableDataList.add(pnTableData);

                    TableData plTableData = new TableData();
                    plTableData.setId("????????????");
                    plTableData.setName("???????????????");
                    plTableData.setAmount(placeModel.getPlaceL());
                    tableDataList.add(plTableData);

                    TableData paTableData = new TableData();
                    paTableData.setId("????????????");
                    paTableData.setName("????????????");
                    paTableData.setAmount(placeModel.getPlaceA());
                    tableDataList.add(paTableData);

                    TableData pdTableData = new TableData();
                    pdTableData.setId("????????????");
                    pdTableData.setName("????????????");
                    pdTableData.setAmount(placeModel.getPlaceD());
                    tableDataList.add(pdTableData);

                    TableData rateTableData = new TableData();
                    rateTableData.setId("????????????");
                    rateTableData.setName("?????????");
                    rateTableData.setAmount(placeModel.getRate());
                    tableDataList.add(rateTableData);

                    TableData priceTableData = new TableData();
                    priceTableData.setId("????????????");
                    priceTableData.setName("??????");
                    priceTableData.setAmount(placeModel.getPrice());
                    tableDataList.add(priceTableData);

                    TableData grTableData = new TableData();
                    grTableData.setId("????????????");
                    grTableData.setName("?????????");
                    grTableData.setAmount(placeModel.getGreeningRate());
                    tableDataList.add(grTableData);

                    TableData timeTableData = new TableData();
                    timeTableData.setId("????????????");
                    timeTableData.setName("????????????");
                    timeTableData.setAmount(placeModel.getTime());
                    tableDataList.add(timeTableData);

                    TableData schTableData = new TableData();
                    schTableData.setId("????????????");
                    schTableData.setName("??????");
                    schTableData.setAmount(placeModel.getSchool());
                    tableDataList.add(schTableData);

                    TableData mallTableData = new TableData();
                    mallTableData.setId("????????????");
                    mallTableData.setName("??????");
                    mallTableData.setAmount(placeModel.getMall());
                    tableDataList.add(mallTableData);

                    TableData resTableData = new TableData();
                    resTableData.setId("????????????");
                    resTableData.setName("??????");
                    resTableData.setAmount(placeModel.getRestaurant());
                    tableDataList.add(resTableData);

                    TableData hosTableData = new TableData();
                    hosTableData.setId("????????????");
                    hosTableData.setName("??????");
                    hosTableData.setAmount(placeModel.getHospitol());
                    tableDataList.add(hosTableData);

                    TableData subTableData = new TableData();
                    subTableData.setId("????????????");
                    subTableData.setName("??????");
                    subTableData.setAmount(placeModel.getSubway());
                    tableDataList.add(subTableData);

                    TableData bankTableData = new TableData();
                    bankTableData.setId("????????????");
                    bankTableData.setName("??????");
                    bankTableData.setAmount(placeModel.getBank());
                    tableDataList.add(bankTableData);

                    TableData parkTableData = new TableData();
                    parkTableData.setId("????????????");
                    parkTableData.setName("??????");
                    parkTableData.setAmount(placeModel.getPark());
                    tableDataList.add(parkTableData);

                    TableData waterTableData = new TableData();
                    waterTableData.setId("????????????");
                    waterTableData.setName("??????");
                    waterTableData.setAmount(placeModel.getWater());
                    tableDataList.add(waterTableData);

                    searchFileInfo.setTableData(tableDataList);
                    searchFileInfo.setFileInfo(placeModel);
                    searchFileInfos.add(searchFileInfo);
                }
                searchResult.setFileList(searchFileInfos);

                rstData.setCode(1);
                rstData.setData(searchResult);
                return rstData;
            } catch (Exception e) {
                e.printStackTrace();
                rstData.setCode(-1);
                return rstData;
            }

        } catch (Exception e) {
            e.printStackTrace();
            rstData.setCode(-1);
            return rstData;
        }

    }

    public RstData<List<DeepResult>> deep(HttpServletRequest request) {

        RstData<List<DeepResult>> rstData = new RstData<>();

        List<DeepResult> deepResults = new ArrayList<>();

        String inputData = request.getParameter("inputData");

        System.out.println("inputData = " + inputData);

        if(inputData == null || "".equals(inputData)){
            rstData.setCode(-1);
            return rstData;
        }

        inputData = inputData.replace("\"\"", "0");

        InputData inputData1 = gson.fromJson(inputData, new TypeToken<InputData>(){}.getType());


        String infoData = request.getParameter("infoData");

        System.out.println("infoData = " + infoData);

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

            List<FormParam> formParams = new ArrayList<>();
            FormParam fileForm = new FormParam();
            fileForm.setKey("filePath");
            fileForm.setValue(filePath);
            formParams.add(fileForm);

            FormParam inputForm = new FormParam();
            inputForm.setKey("inputData");
//            inputData = "{\"FAR\":1.7,\"price\":80000,\"greeningrate\":30,\"school\":1,\"mall\":0,\"restaurant\":1,\"hospital\":0,\"subway\":0,\"bank\":0,\"park\":0,\"water\":0}";
//                    "{\"FAR\":\"1.7\",\"price\":\"80000\",\"greeningrate\":\"30\",\"school\":\"1\",\"mall\":\"1\",\"restaurant\":\"0\",\"hospital\":\"0\",\"subway\":\"0\",\"bank\":\"0\",\"park\":\"0\",\"water\":\"0\"}"

            String inputDataStr = gson.toJson(inputData1);

            System.out.println("input data json = " + inputDataStr);

            inputForm.setValue(inputDataStr);
            formParams.add(inputForm);

            String testStr = "{\n" +
                    "    \"30\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 78774.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 91,\\n    \\\"????????????\\\": 54578.666970119535,\\n    \\\"?????????\\\": 0.0,\\n    \\\"????????????\\\": \\\"nan\\\",\\n    \\\"?????????\\\": 0.0,\\n    \\\"??????ID\\\": 30.0\\n}\",\n" +
                    "    \"114\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 122281.0,\\n    \\\"????????????\\\": \\\"??????????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 91,\\n    \\\"????????????\\\": 64687.59232928281,\\n    \\\"?????????\\\": 0.0,\\n    \\\"????????????\\\": \\\"nan\\\",\\n    \\\"?????????\\\": 0.0,\\n    \\\"??????ID\\\": 114.0\\n}\",\n" +
                    "    \"237\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 73000.0,\\n    \\\"????????????\\\": \\\"????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 86,\\n    \\\"????????????\\\": 42538.32340448014,\\n    \\\"?????????\\\": 3.9,\\n    \\\"????????????\\\": \\\"2004-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 22.0,\\n    \\\"??????ID\\\": 237.0\\n}\",\n" +
                    "    \"287\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 48000.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 90,\\n    \\\"????????????\\\": 64742.24835764785,\\n    \\\"?????????\\\": 1.6,\\n    \\\"????????????\\\": \\\"1984-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 30.0,\\n    \\\"??????ID\\\": 287.0\\n}\",\n" +
                    "    \"424\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 107000.0,\\n    \\\"????????????\\\": \\\"?????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 87,\\n    \\\"????????????\\\": 45003.303155363385,\\n    \\\"?????????\\\": 2.3,\\n    \\\"????????????\\\": \\\"2004-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 32.0,\\n    \\\"??????ID\\\": 424.0\\n}\",\n" +
                    "    \"490\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 68000.0,\\n    \\\"????????????\\\": \\\"??????????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 90,\\n    \\\"????????????\\\": 51189.734812317394,\\n    \\\"?????????\\\": 4.1,\\n    \\\"????????????\\\": \\\"2005-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 29.0,\\n    \\\"??????ID\\\": 490.0\\n}\",\n" +
                    "    \"522\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 81000.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 93,\\n    \\\"????????????\\\": 44109.665615814585,\\n    \\\"?????????\\\": 3.0,\\n    \\\"????????????\\\": \\\"1991-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 20.0,\\n    \\\"??????ID\\\": 522.0\\n}\",\n" +
                    "    \"771\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 77000.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 90,\\n    \\\"????????????\\\": 65017.50087512024,\\n    \\\"?????????\\\": 2.8,\\n    \\\"????????????\\\": \\\"1988-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 33.0,\\n    \\\"??????ID\\\": 771.0\\n}\"\n" +
                    "}";

            FormParam infoForm = new FormParam();
            infoForm.setKey("infoData");
            infoForm.setValue(infoData);
            formParams.add(infoForm);

            String resultStr = CommonMethod.postFormJson(flaskUrl + "/deep", formParams);
            System.out.println("resDeep = " + resultStr);
            if ("fail".equals(resultStr) || "error".equals(resultStr)) {
                System.out.println(resultStr);
                rstData.setCode(-1);
                return rstData;
            } else {
                file.delete();

                resultStr = resultStr.replace(" ", "");
                resultStr = resultStr.replace("\n", "");
                resultStr = resultStr.replace("\\n", "");
                resultStr = resultStr.replace("\\", "");
                resultStr = resultStr.replace("\"{", "{");
                resultStr = resultStr.replace("}\"", "}");
                resultStr = resultStr.replace("??????", "price");
                resultStr = resultStr.replace("????????????", "vironment");
                resultStr = resultStr.replace("?????????", "rate");
                resultStr = resultStr.replace("??????", "direct");
                resultStr = resultStr.replace("?????????", "greeningRate");
                resultStr = resultStr.replace("??????", "area");

                System.out.println("deeled = " + resultStr);

                Map<String, DeepModel> map = gson.fromJson(resultStr, new TypeToken<Map<String, DeepModel>>() {
                }.getType());
                Set set = map.keySet();
                for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();
                    DeepModel deepModel = map.get(key);
                    DeepResult deepResult = new DeepResult();
                    deepResult.setName(key);
                    List<Double> doubles = new ArrayList<>();
                    doubles.add(deepModel.getPrice());
                    doubles.add(deepModel.getVironment());
                    doubles.add(deepModel.getRate());
                    doubles.add(deepModel.getDirect());
                    doubles.add(deepModel.getGreeningRate());
                    doubles.add(deepModel.getArea());
                    deepResult.setValue(doubles);

                    deepResults.add(deepResult);
                }


                rstData.setCode(1);
                rstData.setData(deepResults);
                return rstData;
            }

        } catch (Exception e) {
            e.printStackTrace();
            rstData.setCode(-1);
            return rstData;
        }

    }

    public RstData<List<SearchFileInfo>> realFile(HttpServletRequest request) {

        RstData<List<SearchFileInfo>> rstData = new RstData<>();

        List<SearchFileInfo> searchFileInfos = new ArrayList<>();

        String value = request.getParameter("json");

        if (value == null || "".equals(value)) {
            rstData.setCode(-1);
            return rstData;
        }


        try {
            List<String> fileNameList = Arrays.asList(value.split(","));

            if (fileNameList == null || fileNameList.isEmpty()) {
                rstData.setCode(-1);
                return rstData;
            }

            List<FormParam> formParams = new ArrayList<>();
            FormParam fileFormParam = new FormParam();
            fileFormParam.setKey("filePath");
            fileFormParam.setValue(buildingPath);
            formParams.add(fileFormParam);

            FormParam baseFormParam = new FormParam();
            baseFormParam.setKey("casePath");
            baseFormParam.setValue(baseDataPath);
            formParams.add(baseFormParam);


            String testStr = "{\n" +
                    "    \"30\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 78774.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 91,\\n    \\\"????????????\\\": 54578.666970119535,\\n    \\\"?????????\\\": 0.0,\\n    \\\"????????????\\\": \\\"nan\\\",\\n    \\\"?????????\\\": 0.0,\\n    \\\"??????ID\\\": 30.0\\n}\",\n" +
                    "    \"114\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 122281.0,\\n    \\\"????????????\\\": \\\"??????????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 91,\\n    \\\"????????????\\\": 64687.59232928281,\\n    \\\"?????????\\\": 0.0,\\n    \\\"????????????\\\": \\\"nan\\\",\\n    \\\"?????????\\\": 0.0,\\n    \\\"??????ID\\\": 114.0\\n}\",\n" +
                    "    \"237\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 73000.0,\\n    \\\"????????????\\\": \\\"????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 86,\\n    \\\"????????????\\\": 42538.32340448014,\\n    \\\"?????????\\\": 3.9,\\n    \\\"????????????\\\": \\\"2004-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 22.0,\\n    \\\"??????ID\\\": 237.0\\n}\",\n" +
                    "    \"287\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 48000.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 90,\\n    \\\"????????????\\\": 64742.24835764785,\\n    \\\"?????????\\\": 1.6,\\n    \\\"????????????\\\": \\\"1984-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 30.0,\\n    \\\"??????ID\\\": 287.0\\n}\",\n" +
                    "    \"424\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 107000.0,\\n    \\\"????????????\\\": \\\"?????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 87,\\n    \\\"????????????\\\": 45003.303155363385,\\n    \\\"?????????\\\": 2.3,\\n    \\\"????????????\\\": \\\"2004-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 32.0,\\n    \\\"??????ID\\\": 424.0\\n}\",\n" +
                    "    \"490\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 68000.0,\\n    \\\"????????????\\\": \\\"??????????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 90,\\n    \\\"????????????\\\": 51189.734812317394,\\n    \\\"?????????\\\": 4.1,\\n    \\\"????????????\\\": \\\"2005-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 29.0,\\n    \\\"??????ID\\\": 490.0\\n}\",\n" +
                    "    \"522\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 81000.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 93,\\n    \\\"????????????\\\": 44109.665615814585,\\n    \\\"?????????\\\": 3.0,\\n    \\\"????????????\\\": \\\"1991-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 20.0,\\n    \\\"??????ID\\\": 522.0\\n}\",\n" +
                    "    \"771\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"??????\\\": 77000.0,\\n    \\\"????????????\\\": \\\"???????????????\\\",\\n    \\\"???????????????\\\": \\\"??????\\\",\\n    \\\"????????????\\\": 90,\\n    \\\"????????????\\\": 65017.50087512024,\\n    \\\"?????????\\\": 2.8,\\n    \\\"????????????\\\": \\\"1988-01-01 00:00:00+08\\\",\\n    \\\"?????????\\\": 33.0,\\n    \\\"??????ID\\\": 771.0\\n}\"\n" +
                    "}";

            FormParam threFormParam = new FormParam();
            threFormParam.setKey("infoData");
            threFormParam.setValue(testStr);
            formParams.add(threFormParam);
            String resultStr = CommonMethod.postFormJson(flaskUrl + "/real", formParams);

            System.out.println("res = " + resultStr);
            if ("fail".equals(resultStr) || "error".equals(resultStr)) {
                System.out.println(resultStr);
                rstData.setCode(-1);
                return rstData;
            } else {

                System.out.println(resultStr);

                for (String str : fileNameList) {
                    SearchFileInfo searchFileInfo = new SearchFileInfo();
                    searchFileInfo.setFileName(str);
                    String filePath = realFilePath + str + ".jpg";
                    String fileStr = CommonMethod.getImageStr(filePath);
                    searchFileInfo.setFileStr(fileStr);
                    searchFileInfos.add(searchFileInfo);
                }

                rstData.setCode(1);
                rstData.setData(searchFileInfos);
                return rstData;

            }
        } catch (Exception e) {
            e.printStackTrace();
            rstData.setCode(-1);
            return rstData;
        }

    }

    public RstData<String> qnUploadFile(HttpServletRequest request) {

        RstData<String> rstData = new RstData<>();

        String imgStr = "";
        try {
            Collection<Part> parts = request.getParts();
            Iterator<Part> it = parts.iterator();
            String newFilePath = "";
            boolean tag = true;

            CommonMethod.deleteAll(qnShowPath);
            CommonMethod.deleteAll(qnResultPath);
            File tmpfile = new File(qnImgPath);
            tmpfile.mkdir();
            tmpfile = new File(qnShowPath);
            tmpfile.mkdir();
            tmpfile = new File(qnVal1Path);
            tmpfile.mkdir();

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
                        newFilePath = qnSourceFilePath + tmpFileName;

                        System.out.println(newFilePath);

                        File file = new File(newFilePath);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        os = new FileOutputStream(newFilePath);
                        while ((len = ins.read(buffer)) > -1) {
                            os.write(buffer, 0, len);
                        }

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

            File file = new File(newFilePath);
            if (!file.exists() || !tag) {
                rstData.setCode(-1);
                return rstData;
            }
            imgStr = CommonMethod.fileToBase64(file);
            rstData.setData(imgStr);
            rstData.setCode(1);
            return rstData;
        }catch (Exception e){
            e.printStackTrace();
            rstData.setCode(-1);
            return rstData;
        }
    }


    public RstData<String> qnGetImg() throws IOException {

        RstData<String> rstData = new RstData<>();
        String imgStr = "";

//        sourcePath = request.form.get("sourcePath")
//        txtPath = request.form.get("txtPath")
//        basePath = request.form.get("basePath")
//        imgPath = request.form.get("imgPath")
//        showPath = request.form.get("showPath")
//        valPath = request.form.get("valPath")
//        val1Path = request.form.get("val1Path")

        List<FormParam> formParams = new ArrayList<>();
        FormParam sourceFormParam = new FormParam();
        sourceFormParam.setKey("sourcePath");
        sourceFormParam.setValue(qnSourceFilePath);
        formParams.add(sourceFormParam);

        FormParam txtFormParam = new FormParam();
        txtFormParam.setKey("txtPath");
        txtFormParam.setValue(qnTmpFilePath);
        formParams.add(txtFormParam);

        FormParam baseFormParam = new FormParam();
        baseFormParam.setKey("basePath");
        baseFormParam.setValue(qnBasePath);
        formParams.add(baseFormParam);

        FormParam imgFormParam = new FormParam();
        imgFormParam.setKey("imgPath");
        imgFormParam.setValue(qnImgPath);
        formParams.add(imgFormParam);

        FormParam showFormParam = new FormParam();
        showFormParam.setKey("showPath");
        showFormParam.setValue(qnShowPath);
        formParams.add(showFormParam);

        FormParam valFormParam = new FormParam();
        valFormParam.setKey("valPath");
        valFormParam.setValue(qnValPath);
        formParams.add(valFormParam);

        FormParam val1FormParam = new FormParam();
        val1FormParam.setKey("val1Path");
        val1FormParam.setValue(qnVal1Path);
        formParams.add(val1FormParam);

        String resultStr = CommonMethod.postFormJson(flaskBuildingUrl + "/liucheng", formParams);
        System.out.println("res = " + resultStr);
        if ("fail".equals(resultStr) || "error".equals(resultStr)) {
            System.out.println(resultStr);
            rstData.setCode(-1);
            return rstData;
        } else {

            File filePar = new File(qnShowPath);
            if (filePar.exists()) {
                File files[] = filePar.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        imgStr = CommonMethod.fileToBase64(files[i]);
                        break;
                    }
                }
            }

            if(imgStr == null || "".equals(imgStr)){
                rstData.setCode(-1);
                return rstData;
            }else{
                rstData.setCode(1);
                rstData.setData(imgStr);
                return rstData;
            }

        }
    }

}

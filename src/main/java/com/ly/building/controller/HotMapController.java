package com.ly.building.controller;

import com.ly.building.model.*;
import com.ly.building.service.HotMapService;
import org.python.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/hotmap")
public class HotMapController {

    @Autowired
    private HotMapService hotMapService;

    /**
     * 上传热力图相关的csv文件并读取入库
     * @param request
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public RstData<String> uploadFile(HttpServletRequest request){
        return hotMapService.processInit(request);
    }

    /**
     * 散点图
     * @param reqData
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RstData<List<MapResult>> processList(@RequestBody ReqData<HotRule> reqData){
        return hotMapService.processList(reqData.getData());
    }


    @RequestMapping(value = "/hotList", method = RequestMethod.POST)
    public RstData<List<Geo>> processHotList(@RequestBody ReqData<HotRule> reqData){
        return hotMapService.processHotList(reqData.getData());
    }

    /**
     * 获取label列表
     * @param reqData
     * @return
     */
    @RequestMapping(value = "/labelList", method = RequestMethod.POST)
    public RstData<List<String>> processLabelList(@RequestBody ReqData<ListModel> reqData){
        return hotMapService.processLabelList(reqData.getData());
    }

    /**
     * 获取日期列表
     * @param reqData
     * @return
     */
    @RequestMapping(value = "/dateList", method = RequestMethod.POST)
    public RstData<List<String>> processDateList(@RequestBody ReqData<ListModel> reqData){
        return hotMapService.processDateList(reqData.getData());
    }


    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public RstData<String> processUploadFile(HttpServletRequest request){
        return hotMapService.processUploadFile(request);
    }


    @RequestMapping(value = "/analysis", method = RequestMethod.POST)
    public RstData<AnalysisRes> processAnalysis(@RequestBody ReqData<HMapInfo> reqData){
        return hotMapService.processAnalysis(reqData.getData());
    }

    @RequestMapping(value = "/sfHotMap", method = RequestMethod.POST)
    public RstData<SFHotMaptRst> processSFHotMap(@RequestBody ReqData<SFHotMapReq> reqData){
        return hotMapService.processSFHotMap(reqData.getData());
    }

    @RequestMapping(value = "/sfPoint", method = RequestMethod.POST)
    public RstData<SFPointRst> processSFPoint(@RequestBody ReqData<SFPointReq> reqData){
        return hotMapService.processSFPoint(reqData.getData());
    }

    @RequestMapping(value = "/sfPeriod", method = RequestMethod.POST)
    public RstData<PeriodRst> processSFPeriod(@RequestBody ReqData<PeriodReq> reqData){
        return hotMapService.processSFPeriod(reqData.getData());
    }

    @RequestMapping(value = "/sfActive", method = RequestMethod.POST)
    public RstData<ActRst> processSFActive(@RequestBody ReqData<ActiveInfo> reqData){
        return hotMapService.processSFActive(reqData.getData());
    }


    @RequestMapping(value = "/zbActive", method = RequestMethod.POST)
    public RstData<ZbActRst> processZbActive(@RequestBody ReqData<ZbActReq> reqData){
        return hotMapService.processZbActive(reqData.getData());
    }

    @RequestMapping(value = "/zbUploadFile", method = RequestMethod.POST)
    public RstData<String> processZbUploadFile(HttpServletRequest request){
        return hotMapService.processZbUploadFile(request);
    }

    @RequestMapping(value = "/zbHotMap", method = RequestMethod.POST)
    public RstData<List<Geo>> processZBHotMap(@RequestBody ReqData<SFHotMapReq> reqData){
        return hotMapService.processZBHotMap(reqData.getData());
    }

    @RequestMapping(value = "/zbPoint", method = RequestMethod.POST)
    public RstData<List<MapResult>> processZBPoint(@RequestBody ReqData<SFHotMapReq> reqData){
        return hotMapService.processZBPoint(reqData.getData());
    }


}

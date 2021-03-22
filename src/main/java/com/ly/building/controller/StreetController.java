package com.ly.building.controller;

import com.ly.building.model.*;
import com.ly.building.service.TraceService;
import org.python.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/trace")
public class StreetController {

    @Autowired
    private TraceService traceService;

    /**
     * 上传路径相关的csv文件并读取入库
     * @param request
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public RstData<String> uploadFile(HttpServletRequest request){
        return traceService.processInit(request);
    }

//    @RequestMapping(value = "/list", method = RequestMethod.POST)
//    public RstData<TraceResult> processList(@RequestBody ReqData<TraceRule> reqData){
//        return traceService.processList(reqData.getData());
//    }

    /**
     * 根据人员id和出行方式查询路径信息
     * @param reqData
     * @return
     */
    @RequestMapping(value = "/traceList", method = RequestMethod.POST)
    public RstPointData<List<List<TravelPoint>>, List<TravelPoint>> processTraceList(@RequestBody ReqData<TraceRule> reqData){
        return traceService.processTraceList(reqData.getData());
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public RstData<PathInfo> processUploadFile(HttpServletRequest request){
        return traceService.processUploadFile(request);
    }

    @RequestMapping(value = "/deel", method = RequestMethod.POST)
    public RstData<PathInfo> processDeel(@RequestBody ReqData<QTraceDeelData> reqData){
        return traceService.processDeel(reqData.getData());
    }

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public RstData<TraceRes> processInfo(@RequestBody ReqData<TraceInfoReq> reqData){
        return traceService.processInfo(reqData.getData());
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public RstData<PathInfo> processSearch(@RequestBody ReqData<TraceInfoReq> reqData){
        return traceService.processSearch(reqData.getData());
    }

    @RequestMapping(value = "/stUploadFile", method = RequestMethod.POST)
    public RstData<PathInfo> processStUploadFile(HttpServletRequest request){
        return traceService.processStUploadFile(request);
    }

    @RequestMapping(value = "/stDeel", method = RequestMethod.POST)
    public RstData<PathInfo> processStDeel(@RequestBody ReqData<QTraceDeelData> reqData){
        return traceService.processStDeel(reqData.getData());
    }

    @RequestMapping(value = "/stInfo", method = RequestMethod.POST)
    public RstData<STResult> processStInfo(@RequestBody ReqData<TraceInfoReq> reqData){
        return traceService.processStInfo(reqData.getData());
    }

    
    @RequestMapping(value = "/stSearch", method = RequestMethod.POST)
    public RstData<PathInfo> processStSearch(@RequestBody ReqData<TraceInfoReq> reqData){
        return traceService.processStSearch(reqData.getData());
    }
}

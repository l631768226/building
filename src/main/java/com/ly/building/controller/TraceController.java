package com.ly.building.controller;

import com.ly.building.mapper.TraceDao;
import com.ly.building.mapper.TsmcDao;
import com.ly.building.model.*;
import com.ly.building.service.TraceService;
import com.ly.building.service.TsmcService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/tr")
public class TraceController {

    @Autowired
    private TsmcService tsmcService;

    @Autowired
    private TraceService traceService;

    @Autowired
    private TsmcDao tsmcDao;

    @RequestMapping(value = "/list/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Tsmc> processList(@PathVariable("id")String id){
        return tsmcDao.findByUserId("tsmcNY2014", id);
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public List<List<Integer>> processPostTracList(@RequestBody ReqData<TraceRule> reqData){
        return tsmcService.processTracList(reqData.getData());
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(HttpServletRequest request){
        return tsmcService.uploadFile(request);
    }

    @RequestMapping(value = "/traceList", method = RequestMethod.POST)
    public List<List<Integer>> processList(@RequestBody ReqData<TraceRule> reqData){
        return tsmcService.processList(reqData.getData());
    }

    /**
     * 测试路径相关内容的数据
     * @return
     */
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public List<List<Double>> processTest(){
        return traceService.processTest();
    }

}

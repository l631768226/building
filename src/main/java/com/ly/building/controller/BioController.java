package com.ly.building.controller;

import com.ly.building.model.BioActiveReq;
import com.ly.building.model.BioActiveRst;
import com.ly.building.model.ReqData;
import com.ly.building.model.RstData;
import com.ly.building.service.BioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 生物信息
 */
@RestController
@RequestMapping("/bio")
public class BioController {

    BioService bioService;

    @Autowired
    BioController(BioService bioService){
        this.bioService = bioService;
    }

    /**
     * 上传文件
     * @param request
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public RstData<String> processUpload(HttpServletRequest request){
        return bioService.processUpload(request);
    }

    /**
     * 分析上传文件
     * @param reqData
     * @return
     */
    @RequestMapping(value = "/active", method = RequestMethod.POST)
    public RstData<BioActiveRst> processActive(@RequestBody ReqData<BioActiveReq> reqData){
        return bioService.processActive(reqData.getData());
    }


}

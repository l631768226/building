package com.ly.building.controller;

import com.ly.building.model.DeepResult;
import com.ly.building.model.RstData;
import com.ly.building.model.SearchFileInfo;
import com.ly.building.model.SearchResult;
import com.ly.building.service.CalService;
import org.python.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cal")
public class CalController {

    @Autowired
    private CalService calService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public RstData<List<String>> uploadFile(HttpServletRequest request){
        return calService.uploadFile(request);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public RstData<SearchResult> searchFile(HttpServletRequest request){
        return calService.searchFile(request);
    }

    @RequestMapping(value = "/deep", method = RequestMethod.POST)
    public RstData<List<DeepResult>> deepFile(HttpServletRequest request){
        return calService.deep(request);
    }


    @RequestMapping(value = "/real", method = RequestMethod.POST)
    public RstData<List<SearchFileInfo>> realFile(HttpServletRequest request){
        return calService.realFile(request);
    }


    @RequestMapping(value = "/qnUploadFile", method = RequestMethod.POST)
    public RstData<String> qnUploadFile(HttpServletRequest request){
        return calService.qnUploadFile(request);
    }

    @RequestMapping(value = "/qnGetImg", method = RequestMethod.POST)
    public RstData<String> qnGetImg() throws IOException {
        return calService.qnGetImg();
    }

}

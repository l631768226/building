package com.ly.building.controller;

import com.ly.building.common.CommonMethod;
import com.ly.building.mapper.TraceDao;
import com.ly.building.mapper.TsmcDao;
import com.ly.building.model.Tsmc;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private TsmcDao tsmcDao;

    @Autowired
    private TraceDao traceDao;

    @RequestMapping(value = "/1", method = RequestMethod.GET)
    public String test(){
        int count = traceDao.selectCount();
        return "OK " + count;
    }

    @RequestMapping(value = "updateTime", method = RequestMethod.POST)
    public String initDate(){

        String tableName = "tsmc2014NY";

        List<Tsmc> tsmcList = tsmcDao.findList(tableName);
        List<Tsmc> newList = new ArrayList<>();
        int count = 0;
        if(tsmcList != null && !tsmcList.isEmpty()){
            for(Tsmc tsmc : tsmcList){
                String loca = tsmc.getLoca();
                if(loca == null || "".equals(loca)){
                    continue;
                }
                Tsmc newTsmc = new Tsmc();
                String utc = tsmc.getUtc();

                String dateStr = CommonMethod.UTCtoStr(utc);
                BeanUtils.copyProperties(tsmc, newTsmc);
                newTsmc.setLoca(dateStr);
                newList.add(newTsmc);

                int result = tsmcDao.updateLoca(tableName, dateStr, newTsmc.getId());
                if(result <= 0){
                    count ++;
                }
            }

//            int  result = tsmcDao.updateDateTime(tableName, newList);

            if(count == 0){
                return "OK";
            }else{
                return "fail";
            }
        }else{
            return "empty";
        }
    }

    @RequestMapping(value = "init", method = RequestMethod.POST)
    public String updateDate(){
        String tableName = "tsmcNY2014";
        try {
            String encoding="GBK";
            File file=new File("E:\\wst\\dataset_tsmc2014\\dataset_tsmc2014\\dataset_TSMC2014_NYC.txt");
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                List<Tsmc> tsmcList = new ArrayList<>();
                while((lineTxt = bufferedReader.readLine()) != null){
//                    System.out.println(lineTxt);
                    String data[] = lineTxt.split("\t");
                    if(data.length > 0){
//                        for(String str : data){
//                            System.out.println(str);
//                        }
                        Tsmc tsmc = new Tsmc();
                        tsmc.setUserId(data[0]);
                        tsmc.setVenueId(data[1]);
                        tsmc.setVenueCateId(data[2]);
                        tsmc.setVenueCateName(data[3]);
                        tsmc.setLatitude(Double.valueOf(data[4]));
                        tsmc.setLongtitude(Double.valueOf(data[5]));
                        tsmc.setTimeZone(data[6]);
                        tsmc.setUtc(data[7]);
                        String dateStr = CommonMethod.UTCtoStr(data[7]);
                        tsmc.setLoca(dateStr);
                        tsmcList.add(tsmc);
                    }else{
                        System.out.println("error");
                    }
//                    break;
                }
                read.close();

                int result = tsmcDao.insertDateTime(tableName, tsmcList);
                if(result > 0){
                    return "OK";
                }else{
                    return "fail";
                }
            }else{
                System.out.println("找不到指定的文件");
                return "empty";
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
            return "error";
        }
    }

}

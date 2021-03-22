package com.ly.building.controller;

import com.ly.building.common.FBase64;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class TestMain {

    public static void main(String[] args){
        Date date = new Date();

        long dateNum = date.getTime();

        String dataStr = "45.1234" + "&" + "123.3456" + "&" + dateNum;

        String undeel = dataStr;

        String deeled = "";

        int key = 189;

//        for(int i = 0; i < undeelByte.length; i ++){
//            byte a = undeelByte[i];
//            int b = (int)a;
//            int c = b ^ key;
//            char d = (char)c;
//
//        }

        char[] chars = undeel.toCharArray();

        for(int i = 0; i < chars.length; i ++){
            char a = chars[i];
            int c = (a ^ key);
            char d = (char)c;
            chars[i] = d;
        }

        String resultStr = String.valueOf(chars);

        System.out.println(resultStr);

        String deeledStr = FBase64.encode(resultStr.getBytes(StandardCharsets.UTF_8));
        System.out.println(deeledStr);



    }

}

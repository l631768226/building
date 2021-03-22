package com.ly.building.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ly.building.common.CommonMethod;
import com.ly.building.model.PlaceModel;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {

        Gson gson = new Gson();

//        String qnSourceFilePath = "E/building/yqn/source/";
//        String qnTmpFilePath = "E:/building/yqn/middle/tmp.txt";
//        String qnBasePath = "E:/building/yqn/";
//        String qnImgPath = "E:/building/yqn/picture_test/images/";
//        String qnShowPath = "E:/building/yqn/picture_test/show/";
//        String qnValPath = "E:/building/yqn/val/";
//        String qnVal1Path = "E:/building/yqn/picture_test/val1/";
//
//        String qnResultPath = "E:/building/yqn/picture_test";
//
//        CommonMethod.deleteAll(qnShowPath);
//        CommonMethod.deleteAll(qnResultPath);
//        File file = new File(qnImgPath);
//        file.mkdir();
//        file = new File(qnShowPath);
//        file.mkdir();
//        file = new File(qnVal1Path);
//        file.mkdir();

//        String testStr = "{\n" +
//                "    \"114\": \"{\\n    \\\"价格\\\": 47.15,\\n    \\\"周边环境\\\": 87.5,\\n    \\\"容积率\\\": 0.0,\\n    \\\"朝向\\\": 97.85,\\n    \\\"绿化率\\\": 0.0,\\n    \\\"面积\\\": 78.91\\n}\",\n" +
//                "    \"237\": \"{\\n    \\\"价格\\\": 91.25,\\n    \\\"周边环境\\\": 87.5,\\n    \\\"容积率\\\": 0,\\n    \\\"朝向\\\": 92.47,\\n    \\\"绿化率\\\": 73.33,\\n    \\\"面积\\\": 79.63\\n}\",\n" +
//                "    \"287\": \"{\\n    \\\"价格\\\": 60.0,\\n    \\\"周边环境\\\": 87.5,\\n    \\\"容积率\\\": 94.12,\\n    \\\"朝向\\\": 96.77,\\n    \\\"绿化率\\\": 100.0,\\n    \\\"面积\\\": 78.81\\n}\",\n" +
//                "    \"30\": \"{\\n    \\\"价格\\\": 98.47,\\n    \\\"周边环境\\\": 87.5,\\n    \\\"容积率\\\": 0.0,\\n    \\\"朝向\\\": 97.85,\\n    \\\"绿化率\\\": 0.0,\\n    \\\"面积\\\": 97.83\\n}\",\n" +
//                "    \"424\": \"{\\n    \\\"价格\\\": 66.25,\\n    \\\"周边环境\\\": 87.5,\\n    \\\"容积率\\\": 64.71,\\n    \\\"朝向\\\": 93.55,\\n    \\\"绿化率\\\": 93.33,\\n    \\\"面积\\\": 84.24\\n}\",\n" +
//                "    \"490\": \"{\\n    \\\"价格\\\": 85.0,\\n    \\\"周边环境\\\": 87.5,\\n    \\\"容积率\\\": 0,\\n    \\\"朝向\\\": 96.77,\\n    \\\"绿化率\\\": 96.67,\\n    \\\"面积\\\": 95.82\\n}\",\n" +
//                "    \"522\": \"{\\n    \\\"价格\\\": 98.75,\\n    \\\"周边环境\\\": 87.5,\\n    \\\"容积率\\\": 23.53,\\n    \\\"朝向\\\": 100.0,\\n    \\\"绿化率\\\": 66.67,\\n    \\\"面积\\\": 82.57\\n}\",\n" +
//                "    \"771\": \"{\\n    \\\"价格\\\": 96.25,\\n    \\\"周边环境\\\": 87.5,\\n    \\\"容积率\\\": 35.29,\\n    \\\"朝向\\\": 96.77,\\n    \\\"绿化率\\\": 90.0,\\n    \\\"面积\\\": 78.29\\n}\"\n" +
//                "}";
//
//        testStr = testStr.replace(" ", "");
//        testStr = testStr.replace("\n", "");
//        testStr = testStr.replace("\\n", "");
//        testStr = testStr.replace("\\","");
//        testStr = testStr.replace("\"{", "{");
//        testStr = testStr.replace("}\"", "}");
//        testStr = testStr.replace("价格", "price");
//        testStr = testStr.replace("周边环境", "vironment");
//        testStr = testStr.replace("容积率", "rate");
//        testStr = testStr.replace("朝向", "direct");
//        testStr = testStr.replace("绿化率", "greeningRate");
//        testStr = testStr.replace("面积", "area");
//
//        testStr = testStr.replace(".0", "");

//        String testStr = "{\"FAR\":\"\",\"price\":\"\",\"greeningrate\":\"\",\"school\":\"\",\"mall\":\"\",\"restaurant\":\"\",\"hospital\":\"\",\"subway\":\"\",\"bank\":\"\",\"park\":\"\",\"water\":\"\"}";
//
//        testStr = testStr.replace("\"\"", "0");
//
//        System.out.println(testStr);



//        String testStr = "{\n" +
//                "    \"30\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"价格\\\": 78774.0,\\n    \\\"地块名字\\\": \\\"手帕口北街\\\",\\n    \\\"地块所在地\\\": \\\"北京\\\",\\n    \\\"地块朝向\\\": 91,\\n    \\\"地块面积\\\": 54578.666970119535,\\n    \\\"容积率\\\": 0.0,\\n    \\\"建成日期\\\": \\\"nan\\\",\\n    \\\"绿化率\\\": 0.0,\\n    \\\"编号ID\\\": 30.0\\n}\",\n" +
//                "    \"114\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"价格\\\": 122281.0,\\n    \\\"地块名字\\\": \\\"朝阳门北小街\\\",\\n    \\\"地块所在地\\\": \\\"北京\\\",\\n    \\\"地块朝向\\\": 91,\\n    \\\"地块面积\\\": 64687.59232928281,\\n    \\\"容积率\\\": 0.0,\\n    \\\"建成日期\\\": \\\"nan\\\",\\n    \\\"绿化率\\\": 0.0,\\n    \\\"编号ID\\\": 114.0\\n}\",\n" +
//                "    \"237\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"价格\\\": 73000.0,\\n    \\\"地块名字\\\": \\\"京城仁合\\\",\\n    \\\"地块所在地\\\": \\\"北京\\\",\\n    \\\"地块朝向\\\": 86,\\n    \\\"地块面积\\\": 42538.32340448014,\\n    \\\"容积率\\\": 3.9,\\n    \\\"建成日期\\\": \\\"2004-01-01 00:00:00+08\\\",\\n    \\\"绿化率\\\": 22.0,\\n    \\\"编号ID\\\": 237.0\\n}\",\n" +
//                "    \"287\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"价格\\\": 48000.0,\\n    \\\"地块名字\\\": \\\"垂杨柳西里\\\",\\n    \\\"地块所在地\\\": \\\"北京\\\",\\n    \\\"地块朝向\\\": 90,\\n    \\\"地块面积\\\": 64742.24835764785,\\n    \\\"容积率\\\": 1.6,\\n    \\\"建成日期\\\": \\\"1984-01-01 00:00:00+08\\\",\\n    \\\"绿化率\\\": 30.0,\\n    \\\"编号ID\\\": 287.0\\n}\",\n" +
//                "    \"424\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"价格\\\": 107000.0,\\n    \\\"地块名字\\\": \\\"耕天下\\\",\\n    \\\"地块所在地\\\": \\\"北京\\\",\\n    \\\"地块朝向\\\": 87,\\n    \\\"地块面积\\\": 45003.303155363385,\\n    \\\"容积率\\\": 2.3,\\n    \\\"建成日期\\\": \\\"2004-01-01 00:00:00+08\\\",\\n    \\\"绿化率\\\": 32.0,\\n    \\\"编号ID\\\": 424.0\\n}\",\n" +
//                "    \"490\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"价格\\\": 68000.0,\\n    \\\"地块名字\\\": \\\"首开幸福广场\\\",\\n    \\\"地块所在地\\\": \\\"北京\\\",\\n    \\\"地块朝向\\\": 90,\\n    \\\"地块面积\\\": 51189.734812317394,\\n    \\\"容积率\\\": 4.1,\\n    \\\"建成日期\\\": \\\"2005-01-01 00:00:00+08\\\",\\n    \\\"绿化率\\\": 29.0,\\n    \\\"编号ID\\\": 490.0\\n}\",\n" +
//                "    \"522\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"价格\\\": 81000.0,\\n    \\\"地块名字\\\": \\\"东大桥斜街\\\",\\n    \\\"地块所在地\\\": \\\"北京\\\",\\n    \\\"地块朝向\\\": 93,\\n    \\\"地块面积\\\": 44109.665615814585,\\n    \\\"容积率\\\": 3.0,\\n    \\\"建成日期\\\": \\\"1991-01-01 00:00:00+08\\\",\\n    \\\"绿化率\\\": 20.0,\\n    \\\"编号ID\\\": 522.0\\n}\",\n" +
//                "    \"771\": \"{\\n    \\\"bank\\\": 0.0,\\n    \\\"hospital\\\": 0.0,\\n    \\\"mall\\\": 0.0,\\n    \\\"park\\\": 0.0,\\n    \\\"restaurant\\\": 1.0,\\n    \\\"school\\\": 0.0,\\n    \\\"subway\\\": 0.0,\\n    \\\"water\\\": 0.0,\\n    \\\"价格\\\": 77000.0,\\n    \\\"地块名字\\\": \\\"安德路社区\\\",\\n    \\\"地块所在地\\\": \\\"北京\\\",\\n    \\\"地块朝向\\\": 90,\\n    \\\"地块面积\\\": 65017.50087512024,\\n    \\\"容积率\\\": 2.8,\\n    \\\"建成日期\\\": \\\"1988-01-01 00:00:00+08\\\",\\n    \\\"绿化率\\\": 33.0,\\n    \\\"编号ID\\\": 771.0\\n}\"\n" +
//                "}";
//
//        testStr = testStr.replace(" ", "");
//        testStr = testStr.replace("\n", "");
//        testStr = testStr.replace("\\n", "");
//        testStr = testStr.replace("\\","");
//        testStr = testStr.replace("\"{", "{");
//        testStr = testStr.replace("}\"", "}");
//        testStr = testStr.replace("价格", "price");
//        testStr = testStr.replace("地块名字", "placeN");
//        testStr = testStr.replace("地块所在地", "placeL");
//        testStr = testStr.replace("地块朝向", "placeD");
//        testStr = testStr.replace("地块面积", "placeA");
//        testStr = testStr.replace("容积率", "rate");
//        testStr = testStr.replace("建成日期", "time");
//        testStr = testStr.replace("绿化率", "greeningRate");
//        testStr = testStr.replace("编号ID", "ID");
//        testStr = testStr.replace("nan", "");
//        System.out.println(testStr);
//
//
//        Map<String, PlaceModel> map = gson.fromJson(testStr, new TypeToken<Map<String, PlaceModel>>(){}.getType());
//        Set set = map.keySet();
//        for(Iterator iterator = set.iterator(); iterator.hasNext();){
//            String key = (String)iterator.next();
//            PlaceModel value = (PlaceModel)map.get(key);
//            System.out.println(key + " : " + gson.toJson(value));
//        }
//        System.out.println(map.size());



//        try {
//            String encoding="GBK";
//            File file=new File("E:\\wst\\dataset_tsmc2014\\dataset_tsmc2014\\dataset_TSMC2014_NYC.txt");
//            if(file.isFile() && file.exists()){ //判断文件是否存在
//                InputStreamReader read = new InputStreamReader(
//                        new FileInputStream(file),encoding);//考虑到编码格式
//                BufferedReader bufferedReader = new BufferedReader(read);
//                String lineTxt = null;
//                while((lineTxt = bufferedReader.readLine()) != null){
//                    System.out.println(lineTxt);
//                    String data[] = lineTxt.split("\t");
//                    if(data != null && data.length > 0){
//                        for(String str : data){
//                            System.out.println(str);
//                        }
//                    }else{
//                        System.out.println("error");
//                    }
//                    break;
//                }
//                read.close();
//            }else{
//                System.out.println("找不到指定的文件");
//            }
//        } catch (Exception e) {
//            System.out.println("读取文件内容出错");
//            e.printStackTrace();
//        }
//        String x = "Tue Apr 03 19:59:06 +0000 2012";
//        SimpleDateFormat sdf1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
//        try
//        {
//            Date date=sdf1.parse(x);
//            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(date);
//            cal.add(Calendar.HOUR, -8);
//            date = cal.getTime();
//            String sDate=sdf.format(date);
//            System.out.println(sDate);
//        }
//        catch (ParseException e)
//        {
//            e.printStackTrace();
//        }

//        String jdbcDriver = "com.mysql.cj.jdbc.Driver";
//        String dbAddress = "jdbc:mysql://localhost:3306/";
//        String dbName = "building";
//        String userName = "root";
//        String password = "ly10";
//
//        String tableName = "table1";
//
//        Connection con;
//
//        try {
//
//            Class.forName(jdbcDriver);
//            con = DriverManager.getConnection(dbAddress + dbName, userName, password);
//
//            String myTableName =
//                    "CREATE TABLE "+ tableName + " (\n" +
//                            "  `id` int(10) NOT NULL AUTO_INCREMENT,\n" +
//                            "  `userId` int(6) DEFAULT NULL,\n" +
//                            "  `year` int(4) DEFAULT NULL,\n" +
//                            "  `month` int(2) DEFAULT NULL,\n" +
//                            "  `season` int(2) DEFAULT NULL,\n" +
//                            "  `week` int(2) DEFAULT NULL,\n" +
//                            "  `workday` int(2) DEFAULT NULL,\n" +
//                            "  `hour1` int(2) DEFAULT NULL,\n" +
//                            "  `hour2` int(3) DEFAULT NULL,\n" +
//                            "  `lat` double(11,8) DEFAULT NULL,\n" +
//                            "  `lon` double(12,8) DEFAULT NULL,\n" +
//                            "  `placeName` varchar(100) DEFAULT NULL,\n" +
//                            "  `label` varchar(20) DEFAULT NULL,\n" +
//                            "  `weight` double(10,8) DEFAULT NULL,\n" +
//                            "  `cluster` int(2) DEFAULT NULL,\n" +
//                            "  `tag` int(10) DEFAULT NULL,\n" +
//                            "  `timeStr` varchar(20) DEFAULT NULL,\n" +
//                            "  PRIMARY KEY (`id`)\n" +
//                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
//
//            Statement statement = con.createStatement();
//            statement.execute(myTableName);
//            System.out.println("OK");
//        }catch(Exception e){
//            e.printStackTrace();
//        }


//        String dateS = "2020/11/11";
//        String timeS = "6:00:01";
//        String s = CommonMethod.getTheTime(dateS, timeS);
//        System.out.println(s);

//        final boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
//        boolean isLinux = System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0;
//
//        if(isWin){
//            System.out.println("windows");
//        }
//
//        if(isLinux){
//            System.out.println("linux");
//        }


    }

}

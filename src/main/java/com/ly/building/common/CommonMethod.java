package com.ly.building.common;

import com.ly.building.model.FormParam;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommonMethod {

    private static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * 字符型UTC时间转时间字符串
     *
     * @param utcStr UTC字符串（Tue Apr 03 19:59:06 +0000 2012）
     * @return 时间字符串(2012 - 04 - 03 19 : 59 : 06)
     */
    public static String UTCtoStr(String utcStr) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        String sDate = "";
        try {
            Date date = sdf1.parse(utcStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR, -8);
            date = cal.getTime();
            sDate = sdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            sDate = "error";

        } finally {
            return sDate;
        }
    }

    /**
     * 根据传入的字符串计算小时差
     *
     * @param startTime 开始时间字符串
     * @param endTime   结束时间字符串
     * @return 小时差
     */
    public static int diffHour(String startTime, String endTime) {
        try {
            Date fromDate2 = simpleFormat.parse(startTime);
            Date toDate2 = simpleFormat.parse(endTime);
            long from2 = fromDate2.getTime();
            long to2 = toDate2.getTime();
            int hours = (int) ((to2 - from2) / (1000 * 60 * 60));
            return hours;
        } catch (Exception e) {
            e.printStackTrace();
            return -100;
        }
    }

    /**
     * 创建散点相关信息
     *
     * @param dbAddress
     * @param dbName
     * @param userName
     * @param password
     * @param tableName
     * @return
     */
    public static boolean createMapTable(String dbAddress, String dbName, String userName, String password, String tableName) {
        Connection con;

        String jdbcDriver = "com.mysql.cj.jdbc.Driver";

        try {

            Class.forName(jdbcDriver);
            con = DriverManager.getConnection(dbAddress + dbName, userName, password);

            String myTableName = "CREATE TABLE " + tableName + " ("
                    + "id int(10) NOT NULL AUTO_INCREMENT,"
                    + "lng double(12,8) DEFAULT NULL,"
                    + "lat double(12,8) DEFAULT NULL,"
                    + "yearTime int(4) DEFAULT NULL," +
                    "  monthTime int(4) DEFAULT NULL," +
                    "  dayTime int(2) DEFAULT NULL," +
                    "  weekTime int(1) DEFAULT NULL," +
                    "  period double(10,3) DEFAULT NULL," +
                    "  actReal varchar(50) DEFAULT NULL," +
                    "  actPred varchar(50) DEFAULT NULL," +
                    "  timeStr varchar(20) DEFAULT NULL," +
                    "  tag int(4) DEFAULT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

            Statement statement = con.createStatement();
            statement.execute(myTableName);
            System.out.println("OK");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 创建热力图相关信息
     *
     * @param dbAddress
     * @param dbName
     * @param userName
     * @param password
     * @param tableName
     * @return
     */
    public static boolean createHotMapTable(String dbAddress, String dbName, String userName, String password, String tableName) {
        Connection con;

        String jdbcDriver = "com.mysql.cj.jdbc.Driver";

        try {

            Class.forName(jdbcDriver);
            con = DriverManager.getConnection(dbAddress + dbName, userName, password);

            String myTableName =
                    "CREATE TABLE " + tableName + " (\n" +
                            "  `id` int(10) NOT NULL AUTO_INCREMENT,\n" +
                            "  `userId` int(6) DEFAULT NULL,\n" +
                            "  `year` int(4) DEFAULT NULL,\n" +
                            "  `month` int(2) DEFAULT NULL,\n" +
                            "  `season` int(2) DEFAULT NULL,\n" +
                            "  `week` int(2) DEFAULT NULL,\n" +
                            "  `workday` int(2) DEFAULT NULL,\n" +
                            "  `hour1` int(2) DEFAULT NULL,\n" +
                            "  `hour2` int(3) DEFAULT NULL,\n" +
                            "  `lat` double(11,8) DEFAULT NULL,\n" +
                            "  `lon` double(12,8) DEFAULT NULL,\n" +
                            "  `placeName` varchar(100) DEFAULT NULL,\n" +
                            "  `labelStr` varchar(20) DEFAULT NULL,\n" +
                            "  `weight` double(10,8) DEFAULT NULL,\n" +
                            "  `cluster` int(2) DEFAULT NULL,\n" +
                            "  `tag` int(10) DEFAULT NULL,\n" +
                            "  `timeStr` varchar(20) DEFAULT NULL,\n" +
                            "  `day` int(2) DEFAULT NULL," +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

            Statement statement = con.createStatement();
            statement.execute(myTableName);
            System.out.println("OK");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getTheTime(String dateS, String timeS) {
        String dateStr = dateS + " " + timeS;

        try {
            Date date = dateFormat.parse(dateStr);
            return simpleFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 文件转base64字符串
     * @param filePath 文件路径
     * @return
     */
    public static String getImageStr(String filePath) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(filePath);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        return Base64.encodeBase64String(data);
    }

    public static String fileToBase64(File file) throws IOException {
        FileInputStream inputFile = null;
        byte[] buffer = null;
        try {
            inputFile = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != inputFile) {
                inputFile.close();
            }
        }
        byte[] bs = Base64.encodeBase64(buffer);
        return new String(bs);
    }

    public static String postFormJson(String url, List<FormParam> params) {
        try {
            URL mUrl = new URL(url);
            HttpURLConnection mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
            //设置链接超时时间
            mHttpURLConnection.setConnectTimeout(3600000);
            //设置读取超时时间
            mHttpURLConnection.setReadTimeout(3600000);
            //设置请求参数
            mHttpURLConnection.setRequestMethod("POST");
            //添加Header
            mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");

//            mHttpURLConnection.setRequestProperty("Content-type", "application/json; charset=utf-8");
            //接收输入流
            mHttpURLConnection.setDoInput(true);
            //传递参数时需要开启
            mHttpURLConnection.setDoOutput(true);
            //Post方式不能缓存,需手动设置为false
            mHttpURLConnection.setUseCaches(false);

            mHttpURLConnection.connect();

            if(!params.isEmpty()){

                DataOutputStream dos = new DataOutputStream(mHttpURLConnection.getOutputStream());

                StringBuffer sb = new StringBuffer();
                int count = 0;
                for(FormParam formParam : params){
                    if(count == 0){
                        sb.append(formParam.getKey()).append("=").append(formParam.getValue());
                        count += 1;
                    }else{
                        sb.append("&").append(formParam.getKey()).append("=").append(formParam.getValue());
                    }
                }

                String postContent = sb.toString();
                System.out.println("******** " + postContent);
                dos.write(postContent.getBytes("UTF-8"));
                dos.flush();
                // 执行完dos.close()后，POST请求结束
                dos.close();
            }

            // 获取代码返回值
            int respondCode = mHttpURLConnection.getResponseCode();

            if (respondCode == 200) {
                // 获取响应的输入流对象
                InputStream is = mHttpURLConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                String msg = new String(message.toByteArray(),"UTF-8");
                return msg;
            }
            return "fail";
        }catch(Exception e){
            return "error";
        }
    }

    public static void deleteAll(String path) {
        File filePar = new File(path);
        if (filePar.exists()) {
            File files[] = filePar.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    files[i].delete();
                } else if (files[i].isDirectory()) {
                    deleteAll(files[i].getAbsolutePath());
                    files[i].delete();
                }
            }
        }
    }


}

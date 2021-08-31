package com.ly.building.service;

import com.ly.building.common.CommonMethod;
import com.ly.building.model.BioActiveReq;
import com.ly.building.model.BioActiveRst;
import com.ly.building.model.GeneModel;
import com.ly.building.model.RstData;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
public class BioService {

    @Value("${bio.filePath}")
    private String filePath;

    @Value("${bio.resultPath}")
    private String resultPath;

    /**
     * @param request
     * @return
     */
    public RstData<String> processUpload(HttpServletRequest request) {

        RstData<String> rstData = new RstData<>();

        String md5 = "";

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

                        File file = new File(filePath + tmpFileName);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        os = new FileOutputStream(filePath + tmpFileName);
                        while ((len = ins.read(buffer)) > -1) {
                            os.write(buffer, 0, len);
                        }

                        InputStream insCopy = new FileInputStream(file);
                        md5 = DigestUtils.md5Hex(insCopy);
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

            rstData.setCode(1);
            rstData.setData(md5);
            return rstData;
        } catch (Exception e){
            e.printStackTrace();
            rstData.setCode(-2);
            return  rstData;
        }
    }

    /**
     *
     * @param data
     * @return
     */
    public RstData<BioActiveRst> processActive(BioActiveReq data) {

        RstData<BioActiveRst> rstData = new RstData<>();

        BioActiveRst bioActiveRst = new BioActiveRst();

        String total_cover = CommonMethod.getImageStr(resultPath + "feature importance：total_cover.jpg");
        String total_gain = CommonMethod.getImageStr(resultPath + "feature importance：total_gain.jpg");
        String gain = CommonMethod.getImageStr(resultPath + "feature importance：gain.jpg");
        String cover = CommonMethod.getImageStr(resultPath + "feature importance：cover.jpg");
        String weight = CommonMethod.getImageStr(resultPath + "feature importance：weight.jpg");

        String img2 = CommonMethod.getImageStr(resultPath + "GA-XGBoost_adpat-iter.jpg");

        try {
            File file = new File(resultPath + "GA-XGBoost candidate gene.csv");
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8);//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;

            List<GeneModel> geneList = new ArrayList<>();

            while ((lineTxt = bufferedReader.readLine()) != null) {
//                        System.out.println(lineTxt);
                String dataS[] = lineTxt.split(",");
                if ("candidate gene".equals(dataS[0])) {
                    continue;
                }
                GeneModel geneModel = new GeneModel();
                geneModel.setGeneStr(dataS[0]);
                geneList.add(geneModel);
            }

            bioActiveRst.setWeight(weight);
            bioActiveRst.setTotal_cover(total_cover);
            bioActiveRst.setTotal_gain(total_gain);
            bioActiveRst.setGain(gain);
            bioActiveRst.setCover(cover);
            bioActiveRst.setImg2(img2);
            bioActiveRst.setGeneList(geneList);

            rstData.setCode(1);
            rstData.setData(bioActiveRst);
            return rstData;

        }catch (Exception e){
            e.printStackTrace();
            rstData.setCode(-1);
            return rstData;
        }

    }
}

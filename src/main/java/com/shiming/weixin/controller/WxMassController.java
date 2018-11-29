package com.shiming.weixin.controller;

import com.shiming.weixin.dao.DmzjDAO;
import com.shiming.weixin.domain.Dmzj;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author shiming-kirino 2018/11/9 10:48
 */
@Controller
@RequestMapping("/mass")
public class WxMassController {

    @Autowired
    private DmzjDAO dmzjDAO;

    @RequestMapping(value="/mass.html",method= RequestMethod.GET)
    public String mass(){
        return "mass";
    }

    @RequestMapping(value="/sendMass.html",method = RequestMethod.POST)
    @ResponseBody
    public String massSuccess() throws MalformedURLException {
        return "success";
    }

    @RequestMapping(value="/fileWrite.html",method = RequestMethod.POST)
    @ResponseBody
    public String fileWrite(){
        try {
            URL url = new URL("https://imgs.gamersky.com/upimg/2018/201811131608153119.jpg");
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			//设置是否要从 URL 连接读取数据,默认为true
            uc.setDoInput(true);
            uc.connect();
            InputStream iputstream = uc.getInputStream();
			//打印文件长度
            System.out.println("file size is:" + uc.getContentLength());
            uc.connect();
            File tmpFile = new File("D:\\tmp\\1.jpg");
            FileUtils.copyToFile(iputstream, tmpFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @RequestMapping(value="/testDao.html",method = RequestMethod.POST)
    @ResponseBody
    public String testDao(){
        List<Dmzj> dmzjList = dmzjDAO.listFive();
        for (Dmzj dmzj:dmzjList) {
            System.out.println(dmzj.toString());
        }
        return "success";
    }
}

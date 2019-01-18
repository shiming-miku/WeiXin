package com.shiming.weixin.controller;

import com.shiming.weixin.config.WxMpConfiguration;
import com.shiming.weixin.config.WxMpProperties;
import com.shiming.weixin.dao.DmzjDAO;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpMassPreviewMessage;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
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
/**
 * @author shiming-kirino 2018/11/9 10:48
 */
@Controller
@RequestMapping("/mass")
public class WxMassController {

    @Autowired
    private DmzjDAO dmzjDAO;

    @Autowired
    private WxMpProperties properties;

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
        try {
            WxMpService wxMpService = WxMpConfiguration.getMpServices().get(properties.getConfigs().get(0).getAppId());
            WxMpMassPreviewMessage massPreviewMessage = new WxMpMassPreviewMessage();
            massPreviewMessage.setMsgType(WxConsts.MassMsgType.TEXT);
            massPreviewMessage.setContent("测试群发消息\n欢迎欢迎，热烈欢迎\n换行测试\n超链接:<a href=\"http://www.baidu.com\">Hello World</a>");
            massPreviewMessage.setToWxUserOpenid("oJ6Mw1U4RIp5F3LRO8DPL_AxTjn8");
            WxMpMassSendResult massResult = wxMpService.getMassMessageService().massMessagePreview(massPreviewMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return "success";
    }
}

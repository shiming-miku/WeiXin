package com.shiming.weixin.service;

import com.shiming.weixin.config.WxMpConfiguration;
import com.shiming.weixin.config.WxMpProperties;
import com.shiming.weixin.dao.ContentDAO;
import com.shiming.weixin.dao.DmzjDAO;
import com.shiming.weixin.domain.Content;
import com.shiming.weixin.domain.Dmzj;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpMassNews;
import me.chanjar.weixin.mp.bean.WxMpMassPreviewMessage;
import me.chanjar.weixin.mp.bean.material.WxMediaImgUploadResult;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import me.chanjar.weixin.mp.bean.result.WxMpMassUploadResult;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 定时任务实现
 *
 * @author shiming-kirino
 * @date 2018/11/26 20:19
 */
@Component
public class ScheduledService extends AbstractService {

	private SimpleDateFormat sdfmy = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private WxMpProperties properties;

	@Autowired
	private DmzjDAO dmzjDAO;

	@Autowired
	private ContentDAO contentDao;

	@Scheduled(cron = "0 0 17 * * ?")
	public void scheduled(){
		logger.info("使用cron自动发送群发信息{}", sdf.format(Calendar.getInstance().getTime()));
		try {
			WxMpService wxMpService = WxMpConfiguration.getMpServices().get(properties.getConfigs().get(0).getAppId());
			WxMpMassNews news = new WxMpMassNews();
			WxMediaUploadResult uploadMediaRes = null;
			List<Dmzj> dmzjList = dmzjDAO.listFive(sdfmy.format(Calendar.getInstance().getTime()));
			for(Dmzj dmzj:dmzjList){
				boolean tmpFlg = dmzj.getTitle().contains("美图推荐")||dmzj.getTitle().contains("【原创】")||dmzj
						.getTitle().contains("【每日一问】")||dmzj.getTitle().contains("【同人】")||dmzj.getTitle()
						.contains("【每周五送】");
				if(tmpFlg){
					continue;
				}
				URL url = new URL(dmzj.getCover());
				HttpURLConnection uc = (HttpURLConnection) url.openConnection();
				uc.setRequestProperty("Referer","https://news.dmzj.com");
				// 设置是否要从 URL 连接读取数据,默认为true
				uc.setDoInput(true);
				uc.connect();
				InputStream iputstream = uc.getInputStream();
				// 打印文件长度
				this.logger.info("file size is:{}", uc.getContentLength());
				// 上传图文消息的封面图片
				uploadMediaRes = wxMpService.getMaterialService().mediaUpload(WxConsts.MassMsgType.IMAGE, "jpg",
						iputstream);
				WxMpMassNews.WxMpMassNewsArticle article = new WxMpMassNews.WxMpMassNewsArticle();
				article.setTitle(DMZJ + dmzj.getTitle());
				article.setThumbMediaId(uploadMediaRes.getMediaId());
				article.setShowCoverPic(false);
				article.setAuthor("动漫之家");
				article.setContent(ZZZM + "<br>");
				article.setContentSourceUrl(dmzj.getUrl());
				article.setDigest("动漫之家");
				List<Content> contentList = contentDao.listByUrl(dmzj);
				int i = 0;
				for(Content content:contentList){
					HttpURLConnection ucContent = null;
					InputStream contentIputstream = null;
					try {
						URL urlcontent = new URL(content.getText());
						ucContent = (HttpURLConnection) urlcontent.openConnection();
						ucContent.setRequestProperty("Referer","https://news.dmzj.com");
						// 设置是否要从 URL 连接读取数据,默认为true
						ucContent.setDoInput(true);
						ucContent.connect();
						contentIputstream = ucContent.getInputStream();
						File tmpFile = new File("/weixin/tmp/" + (++i) +".jpg");
						// 打印文件长度
						this.logger.info("file size is2:{}", uc.getContentLength());
						FileUtils.copyToFile(contentIputstream, tmpFile);
						// 上传图文消息的正文图片(返回的url拼在正文的<img>标签中)
						try {
							WxMediaImgUploadResult imagedMediaRes = wxMpService.getMaterialService().
									mediaImgUpload(tmpFile);
							article.setContent(article.getContent() + "<img src=" + imagedMediaRes.getUrl() + "><br>");
						} catch (WxErrorException e) {
							this.logger.error("上传正文图片出错{}", e.getMessage());
						}
					} catch (IOException e) {
						this.logger.info("获取的是正文，不是图片，拼接正文");
						article.setContent(article.getContent() + P_STYLE_START + content.getText() + P_STYLE_END
							+ "<br>");
					}

				}
				article.setContent(article.getContent() + ZZZM);
				news.addArticle(article);
				if (news.getArticles().size() == 8) {
					break;
				}
			}
			// 预览群发
			WxMpMassUploadResult massUploadResult = null;
			if (news.getArticles().size() != 0) {
				for (int i = 0; i < SIZE; i++) {
					try {
						massUploadResult = wxMpService.getMassMessageService().massNewsUpload(news);
						break;
					} catch (WxErrorException e) {
						this.logger.warn("wxError捕获上传news出错再次上传:{}", sdf.format(Calendar.getInstance().getTime()));
					} catch (Exception e){
						this.logger.warn("catch捕获上传news出错再次上传:{}", sdf.format(Calendar.getInstance().getTime()));
					}
				}
			}
			WxMpMassPreviewMessage massPreviewMessage = new WxMpMassPreviewMessage();
			massPreviewMessage.setMsgType(WxConsts.MassMsgType.MPNEWS);
			massPreviewMessage.setMediaId(massUploadResult.getMediaId());
			massPreviewMessage.setToWxUserOpenid("oJ6Mw1U4RIp5F3LRO8DPL_AxTjn8");
			WxMpMassSendResult massResult = wxMpService.getMassMessageService().massMessagePreview(massPreviewMessage);
			this.logger.info("群发结果1:{}", massResult.getErrorMsg());
			massPreviewMessage.setToWxUserOpenid("oJ6Mw1b21hOfBVkb3MrNYasVItWw");
			WxMpMassSendResult massResultOther = wxMpService.getMassMessageService().massMessagePreview(massPreviewMessage);
			this.logger.info("群发结果2:{}", massResultOther.getErrorMsg());
		} catch (IOException e) {
			this.logger.error("群发错误IOException:{}", e.getMessage());
		} catch (WxErrorException e) {
			this.logger.error("群发出错:WxErrorException:{}", e.getMessage());
		}
	}
}

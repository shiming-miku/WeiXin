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
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
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
	public void scheduled() {
		logger.info("使用cron自动发送群发信息{}", sdf.format(Calendar.getInstance().getTime()));
		try {
			WxMpService wxMpService = WxMpConfiguration.getMpServices().get(properties.getConfigs().get(0).getAppId());
			WxMpMassNews news = new WxMpMassNews();
			WxMediaUploadResult uploadMediaRes = null;
			List<Dmzj> dmzjList = dmzjDAO.listFive(sdfmy.format(Calendar.getInstance().getTime()));
			for (Dmzj dmzj:dmzjList) {
				boolean tmpFlg = dmzj.getTitle().contains("美图推荐")||dmzj.getTitle().contains("【原创】")||dmzj
						.getTitle().contains("【每日一问】")||dmzj.getTitle().contains("【同人】")||dmzj.getTitle()
						.contains("【每周五送】");
				if (tmpFlg) {
					continue;
				}
				URL url = new URL(dmzj.getCover());
				HttpURLConnection uc = (HttpURLConnection) url.openConnection();
				uc.setRequestProperty("Referer","https://news.dmzj.com");
				// 设置是否要从 URL 连接读取数据,默认为true
				uc.setDoInput(true);
				uc.connect();
				InputStream inputStream = uc.getInputStream();
				// 打印文件长度
				this.logger.info("file size is:{}", uc.getContentLength());
				// 上传图文消息的封面图片
				uploadMediaRes = wxMpService.getMaterialService().mediaUpload(WxConsts.MassMsgType.IMAGE, "jpg"
					, inputStream);
				WxMpMassNews.WxMpMassNewsArticle article = new WxMpMassNews.WxMpMassNewsArticle();
				article.setTitle(dmzj.getTitle());
				article.setThumbMediaId(uploadMediaRes.getMediaId());
				article.setShowCoverPic(false);
				article.setAuthor(AUTHOR);
				article.setContent(ZZZM);
				article.setContentSourceUrl(dmzj.getUrl());
				article.setDigest(DIGEST);
				List<Content> contentList = contentDao.listByUrl(dmzj);
				int i = 0;
				for (Content content:contentList) {
					HttpURLConnection ucContent = null;
					InputStream contentInputStream = null;
					try {
						URL urlContent = new URL(content.getText());
						ucContent = (HttpURLConnection) urlContent.openConnection();
						ucContent.setRequestProperty("Referer","https://news.dmzj.com");
						// 设置是否要从 URL 连接读取数据,默认为true
						ucContent.setDoInput(true);
						ucContent.connect();
						contentInputStream = ucContent.getInputStream();
						File tmpFile = new File("/weixin/tmp/" + (++i) +".jpg");
						// 打印文件长度
						this.logger.info("file size is2:{}", uc.getContentLength());
						FileUtils.copyToFile(contentInputStream, tmpFile);
						// 上传图文消息的正文图片(返回的url拼在正文的<img>标签中)
						try {
							WxMediaImgUploadResult imagedMediaRes = wxMpService.getMaterialService().
								mediaImgUpload(tmpFile);
							article.setContent(article.getContent() + IMG_STYLE_START + "<img src="
								+ imagedMediaRes.getUrl() + ">" + IMG_STYLE_END);
						} catch (WxErrorException e) {
							this.logger.error("上传正文图片出错{}", e.getMessage());
						}
					} catch (IOException e) {
						this.logger.info("获取的是正文，不是图片，拼接正文");
						article.setContent(article.getContent() + P_STYLE_START + content.getText() + P_STYLE_END);
					}

				}
				article.setContent(article.getContent() + ZZZM);
				news.addArticle(article);
				if (news.getArticles().size() == 8) {
					break;
				}
			}
			sendNews(news);
		} catch (IOException e) {
			this.logger.error("群发错误IOException:{}", e.getMessage());
		} catch (WxErrorException e) {
			this.logger.error("群发出错:WxErrorException:{}", e.getMessage());
		}
	}

	private void sendNews(WxMpMassNews wxMpMassNews) throws WxErrorException {
		// 正式测试环境参数
		String isFormal = properties.getConfigs().get(0).getIsFormal();
		// 群发失败，重发次数
		Integer size = properties.getConfigs().get(0).getSize();
		logger.info("isFormal参数：{}，size参数：{}", isFormal, size);
		WxMpService wxMpService = WxMpConfiguration.getMpServices().get(properties.getConfigs().get(0).getAppId());
		// 上传群发消息
		WxMpMassUploadResult massUploadResult = null;
		if (wxMpMassNews.getArticles().size() != 0) {
			for (int i = 0; i < size; i++) {
				try {
					massUploadResult = wxMpService.getMassMessageService().massNewsUpload(wxMpMassNews);
					break;
				} catch (WxErrorException e) {
					this.logger.warn("wxError捕获上传news出错再次上传:{}", sdf.format(Calendar.getInstance().getTime()));
				} catch (Exception e){
					this.logger.warn("catch捕获上传news出错再次上传:{}", sdf.format(Calendar.getInstance().getTime()));
				}
			}
		}
		// 上传成功的情况，像用户发送消息
		if (null != massUploadResult) {
			if (FORMAL_TRUE.equals(isFormal)) {
				WxMpMassTagMessage massMessage = new WxMpMassTagMessage();
				massMessage.setMsgType(WxConsts.MassMsgType.MPNEWS);
				massMessage.setMediaId(massUploadResult.getMediaId());
				massMessage.setTagId(wxMpService.getUserTagService().tagGet().get(0).getId());
                // 标签群发
				WxMpMassSendResult cmassResult = wxMpService.getMassMessageService().massGroupMessageSend(massMessage);
				this.logger.info("标签群发结果:{}", cmassResult.getErrorMsg());
			} else {
				// 测试公众号，预览群发
				WxMpMassPreviewMessage massPreviewMessage = new WxMpMassPreviewMessage();
				massPreviewMessage.setMsgType(WxConsts.MassMsgType.MPNEWS);
				massPreviewMessage.setMediaId(massUploadResult.getMediaId());
				massPreviewMessage.setToWxUserOpenid("oJ6Mw1U4RIp5F3LRO8DPL_AxTjn8");
				WxMpMassSendResult massResult = wxMpService.getMassMessageService().massMessagePreview(massPreviewMessage);
				this.logger.info("群发结果1:{}", massResult.getErrorMsg());
				massPreviewMessage.setToWxUserOpenid("oJ6Mw1b21hOfBVkb3MrNYasVItWw");
				WxMpMassSendResult massResultOther = wxMpService.getMassMessageService().massMessagePreview(massPreviewMessage);
				this.logger.info("群发结果2:{}", massResultOther.getErrorMsg());
			}

		}
	}
}

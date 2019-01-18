package com.shiming.weixin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * service基类定义日志
 *
 * @author shiming-kirino
 * @date 2018/11/26 20:20
 */
public class AbstractService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final String P_STYLE_START = "<p style=\"line-height: 30px;font-size: 17px;color: rgb(37, 53, 69);margin-bottom: 25px;font-family:SourceHanSansCN, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Arial, Helvetica, DINAlternate-Bold,Serif;white-space: normal;background-color: rgb(255, 255, 255);text-indent: 2em;\">";
	final String DMZJ = "动漫之家-新闻 ";
 	final String ZZZM = "<section style=\"text-align: center;color: mediumblue;font-size: 17px;margin-bottom: 25px;font-family:SourceHanSansCN, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Arial, Helvetica, DINAlternate-Bold,Serif;white-space: normal;background-color: rgb(255, 255, 255);\"><span>本消息内容来源于《动漫之家 https://news.dmzj.com》</span></section>";
 	final String P_STYLE_END = "</p>";
 	final String IMG_STYLE_START = "<p style=\"text-align: center;margin-bottom: 25px;\">";
 	final String IMG_STYLE_END = "</p>";
 	final String AUTHOR = "搬运君";
 	final String DIGEST = "新闻资讯";
	/**
	 * 正式环境-true
	 */
	final String FORMAL_TRUE = "true";
}

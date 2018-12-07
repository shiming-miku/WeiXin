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
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected final String P_STYLE_START = "<p style=\"line-height: 30px;font-size: 17px;color: rgb(37, 53, 69);margin-bottom: 25px;font-family:SourceHanSansCN, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Arial, Helvetica, DINAlternate-Bold,Serif;white-space: normal;background-color: rgb(255, 255, 255);text-indent: 2em;\">";
	protected final String DMZJ = "动漫之家-新闻 ";
 	protected final String ZZZM = "<section style=\"text-align: center;color: mediumblue;font-size: 17px;margin-bottom: 25px;font-family:SourceHanSansCN, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Arial, Helvetica, DINAlternate-Bold,Serif;white-space: normal;background-color: rgb(255, 255, 255);\"><span>本消息内容来源于《动漫之家 https://news.dmzj.com》</span></section>";
    protected final String P_STYLE_END = "</p>";
	/**
	 *  微信群发消息news上传出错重新上传次数
	 */
    protected final Integer SIZE = 5;
}

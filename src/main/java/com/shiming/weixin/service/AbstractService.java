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
	protected final String DMZJ = "动漫之家-新闻 ";
	protected final String ZZZM = "<section style='text-align: center;color: mediumblue;'><span>本消息内容来源于《动漫之家 https://news.dmzj.com》</span></section>";
}

package com.shiming.weixin.config;

import com.shiming.weixin.utils.JsonUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * ConfigurationProperties注解起作用需开启 spring-boot-configuration-processor 处理器
 * wechat mp properties
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {
    private List<MpConfig> configs;

	@Data
	public static class MpConfig {
        /**
         * 设置微信公众号的appid
         */
        private String appId;

        /**
         * 设置微信公众号的app secret
         */
        private String secret;

        /**
         * 设置微信公众号的token
         */
        private String token;

        /**
         * 设置微信公众号的EncodingAESKey
         */
        private String aesKey;

        /**
         * 设置正式环境，测试环境
         * true-正式环境，false-测试环境
         */
        private String isFormal;

        /**
         * 群发失败重发次数
         */
        private Integer size;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}

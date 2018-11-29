package com.shiming.weixin.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 *
 * @author shiming-kirino
 * @date 2018/11/26 19:56
 */
@ToString
public class Content {
    @Setter
    @Getter
    private Long id;

    @Setter
    @Getter
    private String url;

    @Setter
    @Getter
    private String text;
}

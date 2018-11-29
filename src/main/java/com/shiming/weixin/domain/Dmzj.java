package com.shiming.weixin.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 *
 * @author shiming-kirino
 * @date  2018/11/26 19:56
 */
@ToString
public class Dmzj {

    @Setter
    @Getter
    private String time;

    @Setter
    @Getter
    private String title;

    @Setter
    @Getter
    private String cover;

    @Setter
    @Getter
    private String url;

    @Setter
    @Getter
    private String author;

    @Setter
    @Getter
    private String content;
}

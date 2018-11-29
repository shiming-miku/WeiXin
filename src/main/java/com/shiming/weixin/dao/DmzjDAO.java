package com.shiming.weixin.dao;


import com.shiming.weixin.domain.Dmzj;

import java.util.List;
/**
 * 获取首页信息
 *
 * @author shiming-kirino
 * @date 2018/11/26 17:17
 */
public interface DmzjDAO {

    /**
     * 获取5条当天的数据
     * @return list
     */
    List<Dmzj> listFive();
}

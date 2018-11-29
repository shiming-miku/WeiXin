package com.shiming.weixin.dao;



import com.shiming.weixin.domain.Content;
import com.shiming.weixin.domain.Dmzj;

import java.util.List;
/**
 * 获取正文信息
 *
 * @author shiming-kirino
 * @date 2018/11/26 17:18
 */
public interface ContentDAO {

    /**
     * 根据正文信息获取正文详细信息
     * @param dmzj
     * @return list
     */
    List<Content> listByUrl(Dmzj dmzj);
}

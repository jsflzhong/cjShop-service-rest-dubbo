package com.cj.core.service;

import com.cj.core.pojo.TbItem;
import com.cj.core.pojo.TbItemDesc;
import com.cj.core.pojo.TbItemParamItem;

/**
 * @author cj
 * @description 商品service
 * @date 2018/5/16
 */
public interface ItemService {

    /**
     * 根据商品id获取商品的基本数据(redis)
     *
     * @param itemId 商品id
     * @return 商品pojo
     * @author cj
     */
    TbItem getItemById(Long itemId);

    /**
     * 根据商品id获取商品的详情数据(redis)
     * @param itemId 商品id
     * @return 商品详情pojo
     * @author cj
     */
    TbItemDesc getItemDescById(Long itemId);

    /**
     * 根据商品id获取商品的规格参数数据(redis)
     * @param itemId 商品id
     * @return 商品规格参数pojo
     * @author cj
     */
    TbItemParamItem getItemParamById(Long itemId);
}

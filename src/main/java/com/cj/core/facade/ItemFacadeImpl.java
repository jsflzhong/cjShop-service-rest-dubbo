package com.cj.core.facade;

import com.cj.core.facade.service.ItemFacade;
import com.cj.core.pojo.TbItem;
import com.cj.core.pojo.TbItemDesc;
import com.cj.core.pojo.TbItemParamItem;
import com.cj.core.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * dubbo facade接口的实现.
 *
 * @author cj
 * @description 商品service
 * @date 2018/5/16
 */
@Service("itemFacade")
public class ItemFacadeImpl implements ItemFacade {

    @Autowired
    private ItemService itemService;

    /**
     * 根据商品id获取商品的基本数据(redis)
     *
     * @param itemId 商品id
     * @return 商品pojo
     * @author cj
     */
    public TbItem getItemById(Long itemId) {
        System.out.println("@@@@@@@@@ getItemById, itemId = " + itemId + " @@@@@@@@@" );
        return itemService.getItemById(itemId);
    }

    /**
     * 根据商品id获取商品的详情数据(redis)
     *
     * @param itemId 商品id
     * @return 商品详情pojo
     * @author cj
     */
    public TbItemDesc getItemDescById(Long itemId) {
        System.out.println("@@@@@@@@@ getItemDescById, itemId = " + itemId + " @@@@@@@@@" );
        return itemService.getItemDescById(itemId);
    }

    /**
     * 根据商品id获取商品的规格参数数据(redis)
     *
     * @param itemId 商品id
     * @return 商品规格参数pojo
     * @author cj
     */
    public TbItemParamItem getItemParamById(Long itemId) {
        System.out.println("@@@@@@@@@ getItemParamById, itemId = " + itemId + " @@@@@@@@@" );
        return itemService.getItemParamById(itemId);
    }
}

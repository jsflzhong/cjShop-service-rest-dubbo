package com.cj.core.facade;

import com.alibaba.dubbo.config.annotation.Service; // dubbo: 注解配置的方式
import com.alibaba.dubbo.config.annotation.Service;
import com.cj.core.facade.service.ItemCatFacade;
import com.cj.core.pojo.ItemCatResult;
import com.cj.core.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;  // dubbo: xml配置的方式

/**
 * dubbo facade接口的实现.
 *
 * @author cj
 */
//@Service("itemCatFacade") //dubbo: xml配置的方式
@Service(version="1.0.0")  // dubbo: 注解配置的方式
public class ItemCatFacadeImpl implements ItemCatFacade {

    @Autowired
    private ItemCatService itemCatService;

    /**
     * 查询商品分类列表
     * @author cj
     * @return
     */
    @Override
    public ItemCatResult getItemCatList() {
        return itemCatService.getItemCatList();
    }
}

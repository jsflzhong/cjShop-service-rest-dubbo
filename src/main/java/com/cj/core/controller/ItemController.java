package com.cj.core.controller;

import com.cj.common.pojo.TaotaoResult;
import com.cj.common.utils.ExceptionUtil;
import com.cj.core.pojo.TbItem;
import com.cj.core.pojo.TbItemDesc;
import com.cj.core.pojo.TbItemParamItem;
import com.cj.core.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author cj
 * @description 商品服务的接口Controller
 * @date 2018/5/16
 */
@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 根据商品id,查询商品基本信息
     * @param itemId 商品id
     * @return TaotaoResult json
     * @author cj
     */
    @RequestMapping("/base/{itemId}")
    @ResponseBody
    public TaotaoResult getItemById(@PathVariable Long itemId) {
        try {
            //调用service查询.
            TbItem item = itemService.getItemById(itemId);
            //用TaotaoResult包装一下TbItem对象.
            return TaotaoResult.ok(item);
        } catch (Exception e) {
            e.printStackTrace();
            //用TaotaoResult.build方法,返回错误和异常信息.
            //用ExceptionUtil工具类,取到异常e,返回给调用者.
            return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }
    }

    /**
     * 根据商品id,查询商品描述(商品详情)信息
     * @param itemId 商品id
     * @return TaotaoResult
     * @author cj
     */
    @RequestMapping("/desc/{itemId}")
    @ResponseBody
    public TaotaoResult getItemDescById(@PathVariable Long itemId) {
        try {
            TbItemDesc itemDesc = itemService.getItemDescById(itemId);
            //用TaotaoResult包装上面查到的TbItemDesc对象.
            return TaotaoResult.ok(itemDesc);
        } catch (Exception e) {
            e.printStackTrace();
            //如果出异常,则用异常工具类,给调用者返回异常的堆栈信息.
            return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }
    }

    /**
     * 发布"商品规格参数查询"服务
     * @param itemId 商品id
     * @return TaotaoResult
     * @author cj
     */
    @RequestMapping("/param/{itemId}")
    @ResponseBody
    public TaotaoResult getItemParamById(@PathVariable Long itemId) {
        try {
            //调用service,返回TbItemParamItem对象.
            TbItemParamItem itemParamItem = itemService.getItemParamById(itemId);
            //用TaotaoResult包装上TbItemParamItem对象.并返回.
            return TaotaoResult.ok(itemParamItem);
        } catch (Exception e) {
            e.printStackTrace();
            //如果出异常,用我的ExceptionUtil类,给调用者返回异常的堆栈信息.
            return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }
    }
}

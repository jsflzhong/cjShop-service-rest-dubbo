package com.cj.core.controller;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cj.core.pojo.ItemCatResult;
import com.cj.core.service.ItemCatService;

@Controller
@RequestMapping("/item/cat")
public class ItemCatController {

    @Resource
    private ItemCatService itemCatService;

    /**
     * 商品分类查询的Controller
     * 对应前台首页中左侧的商品分类拉伸页.
     * 注意:由于浏览器接收这个JSON数据时,会出现乱码.
     * 原因是,用Google看,header里面的content-type内容,为:text/html.
     * 但是我们要的应该是:application-json
     * 所以,我们要修改这个content-type.
     * 第二个属性的意思是: 把Content-type,改为:application-json(写的是个常量,实际上对应的就是这个意思),然后再把编码改成U8.
     *
     * @param callback jsonp标识
     * @return json
     * @author cj
     * 2016年10月4日下午7:10:33
     */
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @ResponseBody
    public Object getItemCatList(String callback) {
        //判断,如果参数为空,说明前端发送的是普通的AJAX请求. 那么正常吧java对象转为JSON,返回即可.
        ItemCatResult result = itemCatService.getItemCatList();
        if (StringUtils.isBlank(callback)) {
            //直接返回result对象. 因为用了注解@ResponseBody了.
            return result;
        }
        //如果字符串不为空，new一个:MappingJacksonValue
        //记得把上面的result对象塞进去.
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
        //调用MappingJacksonValue的setJsonpFunction(json)方法,可以直接把JSON数据外面包一层,转成js.
        //而外层的方法名是什么,已经在上面构造mappingJacksonValue对象时,由参数reslut传递过去了.
        mappingJacksonValue.setJsonpFunction(callback);
        //返回mappingJacksonValue对象.
        return mappingJacksonValue;
    }
}

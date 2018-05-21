package com.cj.core.controller;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cj.common.pojo.TaotaoResult;
import com.cj.common.utils.ExceptionUtil;
import com.cj.core.pojo.TbContent;
import com.cj.core.service.ContentService;

@Controller
public class ContentController {
	
	@Resource 
	private ContentService contentService;
	
	/**
	 * 根据"内容分类id",查询tb_content表，得到此分类下的内容列表
	 * @author cj
	 * @date 2016年10月7日下午10:40:25
	 * @param cid
	 * @return
	 */
	@RequestMapping("/content/{cid}")
	@ResponseBody
	//从路径上取出cid
	public TaotaoResult getContentList(@PathVariable Long cid) {
		//注意,我们这是在做服务. 所以,如果有异常,需要得处理一下. 给调用者反馈一个有用的信息. 比如异常信息.
		try {
			List<TbContent> list = contentService.getContentList(cid);
			TaotaoResult result = TaotaoResult.ok(list);
			return result;	
		} catch (Exception e) {
			//如果抛异常了,先在控制台打印一下异常.
			e.printStackTrace();
			//这里,得给调用者,反馈一个异常信息,才对他有用.
			//异常信息只能通过上面的e.printStackTrace();得到. 那么怎么在下面返回呢.
			//老师用了一个工具类.内部很简单,就是使用输出流,把传过去的异常e的信息,取到.
			//这里直接调用工具类的静态方法,把异常e传过去,返回e的堆栈信息.
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}	
	}
	
	/**
	 * 根据cid,调用service,同步缓存中的数据.
	 * @author cj
	 * @date 2016年10月16日下午10:25:31
	 * @param cid
	 * @return
	 */
	@RequestMapping("/sync/content/{cid}")//别忘记参数从路径中取值.
	@ResponseBody  
	public TaotaoResult sysncContent(@PathVariable Long cid) {
		try {
			TaotaoResult result = contentService.syncContent(cid);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			//调用ExceptionUtil,给调用者返回异常信息.
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}	
}

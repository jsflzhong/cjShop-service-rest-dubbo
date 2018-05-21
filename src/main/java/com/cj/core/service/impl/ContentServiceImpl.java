package com.cj.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cj.common.pojo.TaotaoResult;
import com.cj.common.utils.JsonUtils;
import com.cj.core.mapper.TbContentMapper;
import com.cj.core.pojo.TbContent;
import com.cj.core.pojo.TbContentExample;
import com.cj.core.pojo.TbContentExample.Criteria;
import com.cj.core.service.ContentService;
import com.cj.core.utils.JedisClient;

@Service
public class ContentServiceImpl implements ContentService {

	@Resource
	private TbContentMapper contentMapper;
	@Resource
	private JedisClient jedisClient;
	@Value("${REDIS_CONTENT_KEY}")
	private String REDIS_CONTENT_KEY;
	
	/**
	 * 根据内容分类id:cid,查询内容列表.
	 * @author cj
	 * @param cid
	 * @return
	 * 2016年10月7日
	 */
	@Override
	public List<TbContent> getContentList(Long cid) {
		
		//加入从缓存中查询...
		try {
			//从redis中取缓存数据
			//用debug调试时发现,第一次执行时,这个JSON是空的.因为还没在下面把查到的数据保存进缓存.
			//但是第二次首页刷新时,这个JSON就有数据了.因为在第一次时,下面就把查到的数据,存入Redis缓存了.
			String json = jedisClient.hget(REDIS_CONTENT_KEY, cid+"");
			//判断是否从缓存中取到了这个key.
			if (!StringUtils.isBlank(json)) {
				//如果取到了,则需要把json转换成List.
				//因为,看最下面保存的时候,是把查到的List,转换成JSON字符串,再保存进缓存的.
				//所以,当查出来给业务用时,业务需要的是个List,这就得转成List.用我们的jsonutils工具类.
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				//如果能从缓存中查到数据,那么根本不用走下面的逻辑了,直接返回这个List给controller.
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//查询条件.
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		//执行查询.
		//要用selectByExampleWithBLOBs方法,把所有列都查出来.因为你不知道别人调用你时,需要哪个列的数据.
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
		
		//写入缓存
		try {
			jedisClient.hset(REDIS_CONTENT_KEY, cid+"", JsonUtils.objectToJson(list));
			//缓存策略: 5分钟清除.注意:过期时间,只能在"key"上设置,不能在"hash里面的项上"设置.
			//jedisClient.expire(REDIS_CONTENT_KEY,300);
		} catch (Exception e) {
			//报错的话,就捕获这个异常,正常处理打印到控制台,然后下面可以正常返回数据.
			//这就是"添加缓存时不要影响业务逻辑"的道理.
			e.printStackTrace();
		}
		
		//返回这个list. 这次在controller那边封装成taotaoResult这个pojo.
		return list;

	}
	
	/**
	 * 同步缓存数据.
	 * @author cj
	 * @param cid
	 * @return
	 * 2016年10月16日
	 */
	@Override
	public TaotaoResult syncContent(Long cid) {
		//调用jedis客户端的删除方法,删除指定key的数据.
		//应该是,删除指定大key下的,小项,该项的key就是传入的cid.
		jedisClient.hdel(REDIS_CONTENT_KEY, cid + "");
		return TaotaoResult.ok();
	}

}

package com.cj.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cj.core.mapper.TbItemCatMapper;
import com.cj.core.pojo.CatNode;
import com.cj.core.pojo.ItemCatResult;
import com.cj.core.pojo.TbItemCat;
import com.cj.core.pojo.TbItemCatExample;
import com.cj.core.pojo.TbItemCatExample.Criteria;
import com.cj.core.service.ItemCatService;

/**
 * 查询商品分类的service.
 * @author cj
 */
@Service
public class ItemCatServiceImpl implements ItemCatService{
	
	@Resource
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 查询商品分类列表
	 * 调用下面的递归方法,封装好外层的pojo,并返回给controller.
	 * @author cj
	 * @return
	 * 2016年10月4日
	 */
	@Override
	public ItemCatResult getItemCatList() {
		//调用下面的递归方法查询商品分类列表
		List catList = getItemCatList(0l); //因为parentId是Long型,所以零后面得加个L.
		
		//创建外层的pojo.
		ItemCatResult result = new ItemCatResult();
		//封装字段.
		result.setData(catList);
		
		//返回这个外层的pojo.
		return result;
	}
	
	/**
	 * 根据parentId,递归查询tb_item_cat表的列表.
	 * @param parentId :tb_item_cat实体类的parentId字段.
	 * @return :返回的肯定是个数据集合.
	 * @author cj
	 */
	private List getItemCatList(Long parentId) {
		//创建查询条件.
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		//设父节点ID为0.
		criteria.andParentIdEqualTo(parentId);
		//执行查询. 查询父节点为0的tb_item_cat数据(即查询第一层节点的数据).
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		
		//自定义一个自己的集合.用来装拼接完的第二层pojo对象.
		//为什么?
		//因为,这个方法里,攒的都是第二个pojo的字段.
		//然后,我们要把这些pojo,放进集合,
		//因为,外层pojo,它的那唯一的字段data,就是list类型的.
		List resultList = new ArrayList<>();
		
		//弄个计数器. 因为首页左边的商品分类,只列出14个. 如果把数据库表中的全迭代出来,那么首页左边的分类,下面会多出几个.
		int count=0;
		//迭代查询到的集合.
		for (TbItemCat tbItemCat : list) {
			//不能超过14个.
			if(count >= 14) {
				break;
			}
			//如果当前是父节点(非最内层节点)
			if (tbItemCat.getIsParent()) {
				//创建内层的pojo.封装了内层的三个字段: url,name,items
				CatNode node = new CatNode();
				
				//开始封装内层pojo的字段们......
				
				//1>.封装url字段...
				//用软件看JSON的内层格式,url是这样的:
				// "u": "/products/2.html" 
				//前面的products是固定的, /后面的数字,是商品的id.所以拼接即可.
				node.setUrl("/products/"+tbItemCat.getId()+".html");
				
				//2>.封装name字段...
				//看JSON的第一层的name是这样的:
				//	n : "<a href='/products/1.html'>图书、音像、电子书刊</a>"
				//第二层的name是这样的:
				//	n : "电子书刊"
				//结论: 第一层与第二层的name值,是不同的.
				//所以,要判断一下,当前是不是第一层:			
				//如果当前节点为第一级节点
				if (tbItemCat.getParentId() == 0) { //看数据库表字段说明,parentid=0的就是第一层.
					//按第一层的内容格式封装name字段.
					node.setName("<a href='/products/"+tbItemCat.getId()+".html'>"+tbItemCat.getName()+"</a>");
					//如果是父节点且是第一级节点,那么计数器就+1
					//只有第一级节点不能超过14个元素.count为上面定义的计数器.
					count++;
				} else {
					//否则就按第二层的格式来封装name字段.
					node.setName(tbItemCat.getName());
				}
				
				//3>.封装items字段.
				//items的格式是个数组,里面的一下层内容,与上一层相同.还是一组u,n,i. 但是最后一层是不同的.即叶子节点的items内容是不同的. 在下面判断.
				//所以,就可以调用本方法自己了.也就是递归了. 
				//同时封装了node对象的items字段.
				//问题: 传的参数不应该是"parentId"吗? 怎么变成id了?
				//因为每个tb_item_cat的id,就是它下层叶子的父id.
				node.setItems(getItemCatList(tbItemCat.getId()));
				
				//最后,得把node对象添加到自己的集合列表
				resultList.add(node);
			} else {
				//如果是叶子节点(最内层节点)
				//那么items节点的内容就和上面不一样了.
				//内容是这样的: "/products/3.html|电子书"
				//所以直接拼接一个string出来,并不需要new一个pojo.
				String item = "/products/"+tbItemCat.getId()+".html|" + tbItemCat.getName();
				
				//最后,得把叶子节点的string添加到自己的集合列表
				resultList.add(item);
			}
			
			//到这里,想象一下这个List中的数据结构
			//先是非叶子节点.一个个的放进集合.
			//最后是叶子节点,string被放进了集合.
			//好像符合了JSON的数据格式?
		}
		return resultList;
	}
	
}

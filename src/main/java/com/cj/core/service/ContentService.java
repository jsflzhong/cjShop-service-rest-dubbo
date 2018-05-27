package com.cj.core.service;

import java.util.List;

import com.cj.core.pojo.TaotaoResult;
import com.cj.core.pojo.TbContent;

public interface ContentService {
	
	List<TbContent> getContentList(Long cid);
	//同步缓存数据.
	TaotaoResult syncContent(Long cid);
}	

package com.cj.core.facade;

import com.cj.common.pojo.TaotaoResult;
import com.cj.core.facade.service.ContentFacade;
import com.cj.core.pojo.TbContent;
import com.cj.core.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * dubbo facade接口的实现.
 *
 * @author cj
 */
@Service("contentFacade")
public class ContentFacadeImpl implements ContentFacade {

    @Autowired
    private ContentService contentService;

    @Override
    public List<TbContent> getContentList(Long cid) {
        return contentService.getContentList(cid);
    }

    //同步缓存数据.
    @Override
    public TaotaoResult syncContent(Long cid) {
        return contentService.syncContent(cid);
    }
}	

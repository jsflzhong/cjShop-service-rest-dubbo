package com.cj.core.service.impl;

import com.cj.core.utils.JsonUtils;
import com.cj.core.mapper.TbItemDescMapper;
import com.cj.core.mapper.TbItemMapper;
import com.cj.core.mapper.TbItemParamItemMapper;
import com.cj.core.pojo.TbItem;
import com.cj.core.pojo.TbItemDesc;
import com.cj.core.pojo.TbItemParamItem;
import com.cj.core.pojo.TbItemParamItemExample;
import com.cj.core.service.ItemService;
import com.cj.core.utils.JedisClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cj
 * @description 商品service
 * @date 2018/5/16
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Autowired
    private TbItemParamItemMapper itemParamItemMapper;

    //redis中商品信息基础key
    @Value("${REDIS_ITEM_KEY}")
    private String REDIS_ITEM_KEY;
    //redis中商品基本信息key
    @Value("${ITEM_BASE_INFO_KEY}")
    private String ITEM_BASE_INFO_KEY;
    //redis中商品数据缓存的过期时间
    @Value("${ITEM_EXPIRE_SECOND}")
    private Integer ITEM_EXPIRE_SECOND;
    //redis中商品描述(商品详情)key
    @Value("${ITEM_DESC_KEY}")
    private String ITEM_DESC_KEY;
    //redis中商品规格参数key
    @Value("${ITEM_PARAM_KEY}")
    private String ITEM_PARAM_KEY;


    /**
     * 根据商品id获取商品对象(redis)
     *
     * @param itemId 商品id
     * @return 商品pojo
     * @author cj
     */
    @Override
    public TbItem getItemById(Long itemId) {
        //1.先去查询Redis的缓存，如果缓存中有我需要的数据，则直接返回,不需要再走流程2去查数据库.
        //key是在下面3中,想Redis中添加缓存时,定义的key.
        try {
            String json = jedisClient.get(REDIS_ITEM_KEY + ":" + ITEM_BASE_INFO_KEY + ":" + itemId);
            //判断数据是否存在
            if (StringUtils.isNotBlank(json)) {
                //把json数据转换成java对象
                //注意,从Redis缓存中取出来的都是JSON对象.
                //但是实际需要使用的往往是java对象. 所以可以用我的JsonUtils工具类转一下.
                //最好像这里一样,添加一个json的工具中间层,便于维护!!!
                TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
                return item;
            }
        } catch (Exception e) {
            //log...
            e.printStackTrace();
        }

        //2.调用mapper,根据商品id查询商品基本信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        System.out.println("@@@@@@@@@ getItemById_afterRedis, item = " + item + " @@@@@@@@@" );

        //3.向redis中添加缓存。
        //如果上面走的是流程2,去查数据库了,则说明缓存中没有我要的数据.这里在返回前,应该把该数据,添加进Redis缓存.
        //"添加缓存原则是不能影响正常的业务逻辑".
        /**
         注意,与之前查询内容时添加缓存不同!
         商品信息的缓存的添加,是需要"设置过期时间的"!!
         因为,"内容"的变化不是很频繁.
         但是,"商品信息"不同,有的商品信息很快就作废了.有的冷门商品信息,某些用户查了一次后,很久后都没人用到,那么就不应该还放在缓存中!Redis缓存是放在内存中的!内存很珍贵!
         所以,当把"商品信息"添加进缓存时,需要设置过期时间.
         */
        try {
            //上次使用的是hash.
            //注意:过期时间,只能在"key"上设置,不能在"hash里面的项上"设置.
            //向redis中添加缓存. 需要调用"jedisClient工具类".
            //注意,key的定义,虽然是自己来定的,但是也不能太随意了. 为了维护,写进了配置文件.是:商品的信息和商品的基本信息.
            //key由三部分组成.前两部分在配置文件中定义.最后一个部分是商品id.
            //由于有商品id的存在,保证了key的唯一性.
            //value就是Redis缓存中要存的是什么.
            //肯定是上面,mapper根据id查出来的商品对象.但是java对象不能作为Redis中的value.得转成JSON.
            //下面这一行,就把上面查到的商品信息,保存进Redis缓存了.
            jedisClient.set(REDIS_ITEM_KEY + ":" + ITEM_BASE_INFO_KEY + ":" + itemId, JsonUtils.objectToJson(item));
            //设置key的过期时间
            //key是上面的key; value是时间,为了方便改动,也写进配置文件中.
            jedisClient.expire(REDIS_ITEM_KEY + ":" + ITEM_BASE_INFO_KEY + ":" + itemId, ITEM_EXPIRE_SECOND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 根据商品id获取商品的详情数据(redis)
     *
     * @param itemId 商品id
     * @return 商品详情pojo
     * @author cj
     */
    public TbItemDesc getItemDescById(Long itemId) {
        //1.查询缓存
        //查询缓存，如果有缓存，就直接返回,不需要走下面流程2.
        try {
            String json = jedisClient.get(REDIS_ITEM_KEY + ":" + itemId + ":" + ITEM_DESC_KEY);
            //判断数据是否存在
            if (StringUtils.isNotBlank(json)) {
                // 把json数据转换成java对象
                TbItemDesc itemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
                return itemDesc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //2.根据商品id查询商品详情(根据主键查询的方法,是没有withBlobs()这个方法的.它本身就会把所有字段都查出来)
        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);

        //3.添加缓存
        try {
            //向redis中添加缓存
            //key还是从配置文件中取.
            //value就是上面查到的TbItemDesc对象,得转成JSON数据,才能存入Redis.
            jedisClient.set(REDIS_ITEM_KEY + ":" + itemId + ":" + ITEM_DESC_KEY, JsonUtils.objectToJson(itemDesc));
            //设置key的过期时间
            jedisClient.expire(REDIS_ITEM_KEY + ":" + itemId + ":" + ITEM_DESC_KEY, ITEM_EXPIRE_SECOND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemDesc;
    }

    /**
     * 根据商品id获取商品的规格参数数据(redis)
     *
     * @param itemId 商品id
     * @return 商品规格参数pojo
     * @author cj
     */
    @Override
    public TbItemParamItem getItemParamById(Long itemId) {
        //添加缓存逻辑
        //1.查询缓存
        //查询缓存，如果有缓存，直接返回
        try {
            String json = jedisClient.get(REDIS_ITEM_KEY + ":" + itemId + ":" + ITEM_PARAM_KEY);
            //判断数据在缓存中是否存在.
            if (StringUtils.isNotBlank(json)) {
                //如果存在,就把查到的json数据转换成java对象
                TbItemParamItem itemParamitem = JsonUtils.jsonToPojo(json, TbItemParamItem.class);
                //返回java对象给controller层.
                return itemParamitem;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2.根据商品id查询规格参数
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        //用WithBLOBs取大文本.
        //这里返回的是集合,但是实际上只有一个.所以下面取list.get(0)即可.
        List<TbItemParamItem> list = itemParamItemMapper.selectByExampleWithBLOBs(example);
        //判断后,取规格参数
        if (list != null && list.size() > 0) {
            //取第一个元素即可.
            TbItemParamItem itemParamItem = list.get(0);

            //3.添加缓存(在返回之前).
            try {
                //向redis中添加缓存
                jedisClient.set(REDIS_ITEM_KEY + ":" + itemId + ":" + ITEM_PARAM_KEY, JsonUtils.objectToJson(itemParamItem));
                //设置key的过期时间
                jedisClient.expire(REDIS_ITEM_KEY + ":" + itemId + ":" + ITEM_PARAM_KEY, ITEM_EXPIRE_SECOND);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //返回查到的TbItemParamItem对象.
            return itemParamItem;
        }
        //如果没这个值,则返回null.
        return null;
    }
}

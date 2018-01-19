package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.jedis.JedisClientPool;
import cn.e3mall.common.pojo.EasyUIDataResult;
import cn.e3mall.common.pojo.EmallResult;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;
import redis.clients.jedis.JedisClusterInfoCache;
@Service
public class ContentServiceImpl implements ContentService{
@Value("${CONTENT_KEY}")
private String CONTENT_KEY;
	@Autowired
	private TbContentMapper tbContentMapper;
@Autowired
private JedisClient jedisClient;
	@Override
	public EasyUIDataResult getContentList(Long categoryId,int page,int rows) {
		//设置分页信息：
		PageHelper.startPage(page, rows);
		//执行查询：
   TbContentExample example=new TbContentExample();
 Criteria criteria = example.createCriteria();
   criteria.andCategoryIdEqualTo(categoryId);
   List<TbContent> list= tbContentMapper.selectByExample(example);
   //取分页信息：
   PageInfo<TbContent> pageinfo=new PageInfo<>(list);
   //创建返回对象
   EasyUIDataResult e=new EasyUIDataResult();
   e.setTotal(pageinfo.getTotal());
   e.setRows(list);
	return e;
	}
	@Override
	public EmallResult addContent(TbContent c) {
		
		c.setCreated(new Date());
		c.setUpdated(new Date());
		tbContentMapper.insert(c);
		EmallResult ok = EmallResult.ok();
		try {
			String hget = jedisClient.hget(CONTENT_KEY, c.getCategoryId().toString());
		if(StringUtils.isNotBlank(hget)){
			jedisClient.hdel(CONTENT_KEY, c.getCategoryId().toString());
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok;
	}
	@Override
	public List<TbContent> getListContentByCid(long cid) {
		//先从缓存中取
		try {
			String hget = jedisClient.hget(CONTENT_KEY, cid+"");
			System.out.println(hget);
		if(StringUtils.isNotBlank(hget)){
			List<TbContent> jsonToList = JsonUtils.jsonToList(hget, TbContent.class);
		return jsonToList;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> selectByExample = tbContentMapper.selectByExample(example);
		try {
			jedisClient.hset(CONTENT_KEY, cid+"", JsonUtils.objectToJson(selectByExample));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return  selectByExample;
	}

}

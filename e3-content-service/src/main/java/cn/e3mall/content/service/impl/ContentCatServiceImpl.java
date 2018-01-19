package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.EasyUINodeResult;
import cn.e3mall.common.pojo.EmallResult;
import cn.e3mall.content.service.ContentCatService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;
@Service
public class ContentCatServiceImpl implements ContentCatService{
@Autowired
private TbContentCategoryMapper tbContentCategoryMapper;
	@Override
	public List<EasyUINodeResult> getContentAsTree(long parentId) {
		TbContentCategoryExample example=new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId); 
		List<TbContentCategory> cc=tbContentCategoryMapper.selectByExample(example);
		
		List<EasyUINodeResult> result=new ArrayList<>();
for (TbContentCategory tbContentCategory : cc) {
			EasyUINodeResult e=new EasyUINodeResult();
			e.setId(tbContentCategory.getId());
			e.setText(tbContentCategory.getName());
			e.setState(tbContentCategory.getIsParent()?"closed":"open");
			result.add(e);
		}
return result;
	}
	@Override
	public EmallResult addContentCat(Long parentId, String name) {
TbContentCategory tbContentCategory = new TbContentCategory();
tbContentCategory.setCreated(new Date());
tbContentCategory.setUpdated(new Date());
tbContentCategory.setIsParent(false);
tbContentCategory.setParentId(parentId);
tbContentCategory.setSortOrder(1);
tbContentCategory.setStatus(1);
tbContentCategory.setName(name);
int insert = tbContentCategoryMapper.insert(tbContentCategory);
TbContentCategory parentNode= tbContentCategoryMapper.selectByPrimaryKey(parentId);
if(!parentNode.getIsParent()){
	parentNode.setIsParent(true);
	tbContentCategoryMapper.updateByPrimaryKey(parentNode);
}System.out.println(insert);
		return EmallResult.ok(tbContentCategory);
	}
	@Override
	public EmallResult updateContentCat(Long id, String name) {
		TbContentCategory tbContentCat = tbContentCategoryMapper.selectByPrimaryKey(id);
		tbContentCat.setName(name);
		tbContentCat.setUpdated(new Date());
		tbContentCategoryMapper.updateByPrimaryKey(tbContentCat);
		return EmallResult.ok();
	}
	@Override
	public EmallResult deleteContentCat(Long id) {
 TbContentCategory contentCat = tbContentCategoryMapper.selectByPrimaryKey(id);
		if(contentCat.getIsParent()){
			
			return EmallResult.ok();
		}else{
			Long parentId = contentCat.getParentId();
			TbContentCategoryExample example=new TbContentCategoryExample();
			Criteria createCriteria = example.createCriteria();
            createCriteria.andParentIdEqualTo(id);
			List<TbContentCategory> selectByExample = tbContentCategoryMapper.selectByExample(example);
			if(selectByExample.size()>1){
				
				tbContentCategoryMapper.deleteByPrimaryKey(id);
			}else{
			TbContentCategory contentCat02 = tbContentCategoryMapper.selectByPrimaryKey(parentId);
			contentCat02.setIsParent(false);
			
			tbContentCategoryMapper.updateByPrimaryKey(contentCat02);
			
			tbContentCategoryMapper.deleteByPrimaryKey(id);
			
			}
			
		return EmallResult.ok();
		}
	}

}



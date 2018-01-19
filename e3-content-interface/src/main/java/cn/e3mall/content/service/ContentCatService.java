package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.EasyUINodeResult;
import cn.e3mall.common.pojo.EmallResult;

public interface ContentCatService {
	//展示分类列表
public List<EasyUINodeResult>  getContentAsTree(long parentId);
//增加节点
public EmallResult addContentCat(Long parentId,String name);
//修改节点
public EmallResult updateContentCat(Long id ,String name);

//删除节点
public EmallResult deleteContentCat(Long id);
}

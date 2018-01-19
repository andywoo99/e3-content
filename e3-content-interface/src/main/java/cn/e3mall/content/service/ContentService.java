package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.EasyUIDataResult;
import cn.e3mall.common.pojo.EmallResult;
import cn.e3mall.pojo.TbContent;

public interface ContentService {
public EasyUIDataResult getContentList(Long categoryId,int page,int rows);
public EmallResult  addContent(TbContent c);
public List<TbContent> getListContentByCid(long cid);
}

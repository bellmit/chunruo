package com.chunruo.portal.tag;

import java.util.List;
import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.vo.TagModel;

/**
 * 商品分类-标签
 * @author Administrator
 *
 */
public class ProductCategoryListTag extends BaseTag {
	
	public TagModel<List<ProductCategory>> getData(){
		TagModel<List<ProductCategory>> tagModel = new TagModel<List<ProductCategory>> ();
		try{
			tagModel.setData(Constants.PRODUCT_CATEGORY_TREE_LIST);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}
}

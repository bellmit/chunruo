package com.chunruo.webapp.util;

import java.util.ArrayList;
import java.util.List;

import com.chunruo.core.Constants;
import com.chunruo.core.model.TagModel;
import com.chunruo.core.service.TagModelManager;
import com.chunruo.core.util.StringUtil;

public class TagUtil {
	
	/**
	 * 读取对应标签模版的名称
	 * @return
	 */
	public static String getTagNamesByIdAndType(Long objectId, Integer tagType) {
		String tagNames = null;
		try {
			TagModelManager tagModelManager = Constants.ctx.getBean(TagModelManager.class);
			List<TagModel> tagModelList = tagModelManager.getTagModelListByObjectId(objectId, tagType);
			List<String> tagNameList = new ArrayList<String>();
			if (tagModelList != null && !tagModelList.isEmpty()) {
				for (TagModel tagModel : tagModelList) {
					tagNameList.add(tagModel.getName());
				}
				tagNames = StringUtil.null2Str(StringUtil.strListToString(tagNameList));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return tagNames;
	}
	
}
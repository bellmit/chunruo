package com.chunruo.webapp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.StringUtil;

public class ImageUtil {
	/**
	 * 图片拷贝
	 * @param saveRecordList
	 * @param imageMap
	 * @throws Exception
	 */
	public static String copyImageFile( String imageUrl )throws Exception{
		if (StringUtil.isNull(imageUrl)) {
			return null;
		}
		try {
			File newFile = null;
			String filePath = CoreUtil.dateToPath("/images", imageUrl);
			String srcFilePath = Constants.DEPOSITORY_PATH + StringUtil.null2Str(imageUrl).replace("depository", "");
			String fullFilePath = Constants.EXTERNAL_IMAGE_PATH + "/upload" + filePath;
			boolean result = FileUploadUtil.moveFile(srcFilePath, fullFilePath);
			if (result == true 
					&& (newFile = new File(fullFilePath)) != null
					&& !newFile.exists()){
				throw new Exception("NotFound File[filePaht=" + fullFilePath + "]");
			}else if (!result) {
				throw new Exception("NotFound File[filePaht=" + fullFilePath + "]");
			}
			return filePath;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
}

package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.List;

import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.util.HtmlContParserUtil;
import com.chunruo.portal.vo.WebHtmlContVo;

/**
 * josn格式过滤特殊字符串
 * @author chunruo
 *
 */
public class ProductDescWebViewTag {
	public static final String UPLOAD_PATH = "/upload/";
	
	public List<WebHtmlContVo> get(Object content_1){
		String content = StringUtil.null2Str(content_1);
		content = content.replaceAll("(\r\n|\r|\n|\n\r)", "");
		content = content.replace("&nbsp;", "");
		List<WebHtmlContVo> webHtmlContList = new ArrayList<WebHtmlContVo>();
		webHtmlContList = HtmlContParserUtil.getHtmlContList(content, "product", null);
		if(webHtmlContList != null && webHtmlContList.size() > 0){
			for(WebHtmlContVo webHtmlContVo : webHtmlContList){
				if(webHtmlContVo != null){
					if(StringUtil.compareObject(webHtmlContVo.getContType(), WebHtmlContVo.CONT_TYPE_IMAGE)){
						// 图片
						String filePath = webHtmlContVo.getWebViewFileVo().getSrc();
//						if(StringUtil.null2Str(filePath).contains(ProductDescWebViewTag.UPLOAD_PATH)){
//							filePath = filePath.substring(filePath.indexOf(ProductDescWebViewTag.UPLOAD_PATH));
//							filePath = filePath.replace(ProductDescWebViewTag.UPLOAD_PATH, "");
//							webHtmlContVo.getWebViewFileVo().setSrc(filePath);
//						}else if(StringUtil.null2Str(filePath).contains("chunruo") && StringUtil.null2Str(filePath).contains("images")){
//							filePath = filePath.substring(filePath.indexOf("images"));
//							webHtmlContVo.getWebViewFileVo().setSrc(filePath);
//						}
					}
				}
			}
		}
		return webHtmlContList;
	}
}
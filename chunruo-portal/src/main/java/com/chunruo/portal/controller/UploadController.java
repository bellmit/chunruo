package com.chunruo.portal.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import com.chunruo.core.Constants;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.RequestUtil;

/**
 * @author chunruo
 *
 */
@Controller
@RequestMapping("/api/user/")
public class UploadController extends BaseController {

	/**
	 * web图片统一上传入口
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/webUploadImage")
	public @ResponseBody Map<String, Object> webUploadImage(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		try {
			String requestURL = RequestUtil.getRequestURL(request);
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<String> iter = multiRequest.getFileNames();
				String imagePath = "";
				while (iter.hasNext()) {
					// 由CommonsMultipartFile继承而来,拥有上面的方法.
					MultipartFile file = multiRequest.getFile(iter.next());
					if (!file.isEmpty()) {
						//普通图片
						imagePath = CoreUtil.dateToPath("/images", file.getOriginalFilename());
						String filePath = Constants.EXTERNAL_IMAGE_PATH + "/upload" + imagePath;
						FileUploadUtil.copyFile(file.getInputStream(), filePath);
					}
				}
				
				if (!StringUtil.isNull(imagePath)){
					System.out.println("userImageAbso============"+requestURL+  imagePath);
					System.out.println("userImage============"+imagePath);
					resultMap.put("userImageAbso", requestURL +"/upload"+ imagePath);
					resultMap.put("userImage", imagePath);
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
					resultMap.put(PortalConstants.MSG, "上传成功");
					resultMap.put(PortalConstants.SYSTEMTIME, StringUtil.null2Str(DateUtil.getCurrentTime()));
					return resultMap;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, this.getText("upload.headerImage.exception"));
		resultMap.put(PortalConstants.SYSTEMTIME, StringUtil.null2Str(DateUtil.getCurrentTime()));
		return resultMap;
	}
	
	
	
}

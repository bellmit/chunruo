package com.chunruo.webapp.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import com.chunruo.core.Constants;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.util.RequestUtil;
import com.chunruo.webapp.BaseController;

/**
 * 图片上传
 * @author chunruo
 *
 */
@Controller
@RequestMapping("/upload/")
public class UploadController extends BaseController {
	public final static Long UPLOAD_MAX_SIZE = 524288L; //上传文件0.5M
	@Autowired
	private MultipartResolver multipartResolver;

	@RequestMapping(value = "/fileMultiple")
    public @ResponseBody Map<String, Object> fileMultiple(final HttpServletRequest request){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String fileType = StringUtil.null2Str(request.getHeader("fileType"));
        resultMap.put("success", false);
        try{
        	if (!StringUtil.isNullStr(fileType)) {
    			try {
    				String tmpFileName = StringUtil.null2Str(UUID.randomUUID()) + fileType;
    				String filePath = String.format("/temp/%s", new Object[] { tmpFileName });
    				boolean result = FileUploadUtil.copyFile(request.getInputStream(), Constants.DEPOSITORY_PATH + filePath);
    				if(StringUtil.nullToBoolean(result)){
    					File file = new File(Constants.DEPOSITORY_PATH + filePath);
//    					if(UploadController.UPLOAD_MAX_SIZE.compareTo(file.length()) < 0){
//			        		resultMap.put("message", "上传文件不能超过0.5M,建议使用JPG格式");
//	        				resultMap.put("success", false);
//	        				return resultMap;
//			        	}
    				}
    				
    				resultMap.put("filePath", "depository" + filePath);
    				resultMap.put("userImage", "depository" + filePath);
    				resultMap.put("success", result);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
        }catch(Exception e){
        	e.printStackTrace();
        	log.debug(e.getMessage());
        }
        return resultMap;
    }
	
	/**
	 * form表单图片上传
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fileUpload")
	public @ResponseBody Map<String, Object> fileUpload(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			if (this.multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<String> iter = multiRequest.getFileNames();
			    while (iter.hasNext()) {
			    	// 由CommonsMultipartFile继承而来,拥有上面的方法.
			        MultipartFile file = multiRequest.getFile(iter.next());
			        if (!file.isEmpty()) {
			        	if(UploadController.UPLOAD_MAX_SIZE.compareTo(file.getSize()) < 0){
			        		resultMap.put("message", "上传文件不能超过0.5M,建议使用JPG格式");
			        		resultMap.put("error", true);
	        				resultMap.put("success", true);
	        				return resultMap;
			        	}
			        	
			        	String originFileName = file.getOriginalFilename();		
			        	String fileSuffix = FileUtil.getSuffixByFilename(originFileName);
			        	String tmpFileName = StringUtil.null2Str(UUID.randomUUID()) + fileSuffix;
	    				String filePath = String.format("/temp/%s", new Object[] { tmpFileName });
	    				boolean result = FileUploadUtil.copyFile(file.getInputStream(),  Constants.DEPOSITORY_PATH + filePath);
	    				if(result && (new File(Constants.DEPOSITORY_PATH + filePath)).exists()){
	    					resultMap.put("filePath", Constants.DEPOSITORY + filePath);
	    					resultMap.put("error", false);
	        				resultMap.put("success", true);
	        				return resultMap;
	    				}
			        }
			     }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
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
					System.out.println("userImageAbso============"+requestURL+ imagePath);
					System.out.println("userImage============"+imagePath);
					resultMap.put("userImageAbso", requestURL + imagePath);
					resultMap.put("userImage", imagePath);
					resultMap.put("success", true);
					return resultMap;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("msg", "上传失败");
		resultMap.put("success", false);
		return resultMap;
	}
}

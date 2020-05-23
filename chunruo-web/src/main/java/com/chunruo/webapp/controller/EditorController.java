package com.chunruo.webapp.controller;

import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import com.baidu.ueditor.define.BaseState;
import com.chunruo.core.Constants;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.FileIO;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/textarea")
public class EditorController extends BaseController {
	@Autowired
	private MultipartResolver multipartResolver;

	@RequestMapping(value="/textEditor")
	public @ResponseBody String ueditor(final HttpServletRequest request) {
		StringBuffer resultBuffer = new StringBuffer ();
		String action = StringUtil.null2Str(request.getParameter("action"));
		try{
			if(StringUtil.compareObject(action, "config")){
				InputStream inputStream = getClass().getResourceAsStream("/config.json");
				resultBuffer.append(FileIO.inputStream2String(inputStream));
			}else if(StringUtil.compareObject(action, "uploadimage")){
				BaseState baseState = new BaseState (false);
				try{
					if (multipartResolver.isMultipart(request)) {
						MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
						Iterator<String> iter = multiRequest.getFileNames();
						while (iter.hasNext()) {
							// 由CommonsMultipartFile继承而来,拥有上面的方法.
							MultipartFile file = multiRequest.getFile(iter.next());
							if (!file.isEmpty()) {
								String originFileName = file.getOriginalFilename();							// 获取文件扩展名
								String fileSuffix = FileUtil.getSuffixByFilename(originFileName);			// 文件后缀名
								String xfilePath = CoreUtil.dateToPath("images", originFileName);
								String filePath = Constants.EXTERNAL_IMAGE_PATH + "/upload/" + xfilePath;
								FileUploadUtil.copyFile(file.getInputStream(), filePath);
								
								baseState.setState(true);
								baseState.putInfo("url", xfilePath);
								baseState.putInfo("type", fileSuffix);
							}
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				resultBuffer.append(baseState.toJSONString());
			}
		}catch(Exception e){
			log.debug(e.getMessage());
		}
		return resultBuffer.toString();
	}
}

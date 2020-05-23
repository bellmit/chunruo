package com.chunruo.webapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import com.chunruo.core.Constants;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.XlsParserUtil;

@Controller  
@RequestMapping("/import")
public class ImportFileController extends BaseController{
	@Autowired
	private MultipartResolver multipartResolver;
	
	/**
	 * 手动导入XLS
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/baseImportFile")
	public @ResponseBody Map<String, Object> baseImportFile(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String filePath = this.getServletFileUploadPath(request);
		if(StringUtil.isNull(filePath) || !FileUploadUtil.existFile(filePath)){
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", this.getText("ajax.import.file.error"));
			return resultMap;
		}
		
		
		try {
			List<String> headerList =  XlsParserUtil.readHeader(filePath);
			List<String> storeStrList =  new ArrayList<String> ();
			List<String> strHeaderList =  new ArrayList<String> ();
			List<String> keyValueHeaderList =  new ArrayList<String> ();
			Map<String, String> headerMap = new HashMap<String, String> ();
			if(headerList != null && headerList.size() > 0){
				for(int i = 0; i < headerList.size(); i ++ ){
					String headerKey = headerList.get(i);
					String indexHeader = String.format("header_%s", i);
					headerMap.put(headerKey, indexHeader);
					keyValueHeaderList.add(String.format("{key: '%s', value: '%s'}", indexHeader, headerKey));
					strHeaderList.add(String.format("{text: '%s', dataIndex: '%s', width: 150, sortable : true}", headerKey, indexHeader));
				}
				
				List<Map<String, String>> objectMapList = XlsParserUtil.read(filePath, 0);
				if(objectMapList != null && objectMapList.size() > 0){
					for(Map<String, String> objectMap : objectMapList){
						Map<String, String> storeMap = new HashMap<String, String> ();
						for(Entry<String, String> entry : objectMap.entrySet()){
							if(headerMap.containsKey(entry.getKey())){
								storeMap.put(headerMap.get(entry.getKey()), entry.getValue());
							}
						}
						storeStrList.add(StringUtil.mapStrToJson(storeMap));
					}
				}
			}
			
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("strHeaderList", strHeaderList);
			resultMap.put("storeStrList", storeStrList);
			resultMap.put("keyValueHeaderList", keyValueHeaderList);
			resultMap.put("message", this.getText("ajax.import.success"));
			return resultMap;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", this.getText("ajax.import.failure"));
		return resultMap;
	}
	
	/**
	 * 导入文件数据转换
	 * @param dataMapList
	 * @param headerMapList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> importDataToMapList(List<Object> dataMapList, List<Object> headerMapList){
		List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>> ();
		if(headerMapList != null 
				&& headerMapList.size() > 0
				&& dataMapList != null 
				&& dataMapList.size() > 0){
			Map<String, String> headerMap = new HashMap<String, String> ();
			for(Object headerDataMap : headerMapList){
				try{
					Map<String, Object> dataMap = (Map<String, Object>) headerDataMap;
					if(dataMap.containsKey("key") && dataMap.containsKey("value")){
						headerMap.put(StringUtil.null2Str(dataMap.get("key")), StringUtil.null2Str(dataMap.get("value")));
					}
				}catch(Exception e){
					continue;
				}
			}
			
			if(headerMap != null && headerMap.size() > 0){
				for(Object objectDataMap : dataMapList){
					try{
						Map<String, Object> dataMap = (Map<String, Object>) objectDataMap;
						if(dataMap != null && dataMap.size() > 0){
							Map<String, String> realDataMap = new HashMap<String, String> ();
							for(Entry<String, Object> entry : dataMap.entrySet()){
								if(headerMap.containsKey(StringUtil.null2Str(entry.getKey()))){
									realDataMap.put(headerMap.get(StringUtil.null2Str(entry.getKey())), StringUtil.null2Str(entry.getValue()));
								}
							}
							objectMapList.add(realDataMap);
						}
					}catch(Exception e){
						continue;
					}
				}
			}
		}
		return objectMapList;
	}
	
	/**
	 * Multipart文件上传
	 * @param request
	 * @return
	 */
	protected String getServletFileUploadPath(HttpServletRequest request){
		String filePath = null;
		try{
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<String> iter = multiRequest.getFileNames();
			    while (iter.hasNext()) {
			    	// 由CommonsMultipartFile继承而来,拥有上面的方法.
			        MultipartFile file = multiRequest.getFile(iter.next());
			        if (!file.isEmpty()) {
			        	String originFileName = file.getOriginalFilename();		
			        	String fileSuffix = FileUtil.getSuffixByFilename(originFileName);
			        	String tmpFileName = StringUtil.null2Str(UUID.randomUUID()) + fileSuffix;
	    				String fuallFilePath = Constants.DEPOSITORY_PATH + String.format("/temp/%s", new Object[] { tmpFileName });
	    				boolean result = FileUploadUtil.copyFile(file.getInputStream(),  fuallFilePath);
	    				if(result && (new File(fuallFilePath)).exists()){
	    					filePath = fuallFilePath;
	    				}
			        }
			     }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return filePath;
	}
}

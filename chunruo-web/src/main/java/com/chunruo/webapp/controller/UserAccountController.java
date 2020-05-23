package com.chunruo.webapp.controller;
//package com.chunruo.webapp.controller;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import com.chunruo.core.Constants;
//import com.chunruo.core.model.UserAccountRecord;
//import com.chunruo.core.model.UserInfo;
//import com.chunruo.core.service.UserAccountRecordManager;
//import com.chunruo.core.service.UserInfoManager;
//import com.chunruo.core.util.DateUtil;
//import com.chunruo.core.util.FileUtil;
//import com.chunruo.core.util.StringUtil;
//import com.chunruo.core.util.XlsUtil;
//import com.chunruo.webapp.BaseController;
//import com.chunruo.webapp.interceptor.AuthorizeInterceptor;
//
//@Controller
//@RequestMapping("/userAccount/")
//public class UserAccountController extends BaseController {
//
//	@Autowired
//	private UserInfoManager userInfoManager;
//	@Autowired
//	private UserAccountRecordManager userAccountRecordManager;
//
//	/**
//	 * 集币充值记录列表
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(value = "/list")
//	@AuthorizeInterceptor(value="isExporter=true")
//	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		Map<String, Object> filtersMap = new HashMap<String, Object>();
//		List<UserAccountRecord> userAccountRecordList = new ArrayList<UserAccountRecord>();
//		Long count = 0L;
//		try {
//			String columns = StringUtil.nullToString(request.getParameter("columns"));
//			Boolean isExporter = StringUtil.nullToBoolean(request.getParameter("isExporter"));
//			int start = StringUtil.nullToInteger(request.getParameter("start"));
//			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
//			String sort = StringUtil.nullToString(request.getParameter("sort"));
//			String filters = StringUtil.nullToString(request.getParameter("filters"));
//			String beginTime = request.getParameter("beginTime");
//			String endTime = request.getParameter("endTime");
//			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
//			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), UserAccountRecord.class);
//
//			// filter过滤字段查询
//			if (filtersMap != null && filtersMap.size() > 0) {
//				for (Entry<String, Object> entry : filtersMap.entrySet()) {
//					paramMap.put(entry.getKey(), entry.getValue());
//				}
//
//			}
//			Map<String, String> columnMap = StringUtil.getColumnsMap(columns);
//            if(isExporter) {
//            	Date dateBeginTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, beginTime);
//				Date dateEndTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, endTime);
//				List<UserAccountRecord> recordList = this.userAccountRecordManager.getUserAccountRecordListByCreateTime(dateBeginTime,dateEndTime);
//
//				if (recordList != null && recordList.size() > 0) {
//					// 导出列表头信息
//					List<String> headerList = new ArrayList<String>();
//					for (Entry<String, String> entry : columnMap.entrySet()) {
//						headerList.add(entry.getValue());
//					}
//
//					List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>>();
//					this.setUserInfo(recordList);
//					for (int i = 0; i < recordList.size(); i++) {
//						try {
//							Map<String, String> objectMap = new HashMap<String, String>();
//							Map<String, Object> orderMap = StringUtil.objectToMap(recordList.get(i));
//							for (Entry<String, String> entry : columnMap.entrySet()) {
//								if (orderMap.containsKey(entry.getKey())) {
//									objectMap.put(entry.getValue(), StringUtil.null2Str(orderMap.get(entry.getKey())));
//								}
//							}
//							objectMapList.add(objectMap);
//						} catch (Exception e) {
//							e.printStackTrace();
//							continue;
//						}
//					}
//
//					// 导出文件地址
//					String filePath = StringUtil.getUniqueDateFilePath(OrderController.XLS_FILE_NAME);
//					File file = new File(Constants.DEPOSITORY_PATH + filePath);
//					FileUtil.createNewFile(file);
//					XlsUtil.writeFile(headerList, objectMapList, file.getPath());
//					resultMap.put("filePath", filePath);
//               }
//            }else {
//            	count = this.userAccountRecordManager.countHql(paramMap);
//    			if (count != null && count.longValue() > 0L) {
//    				userAccountRecordList = this.userAccountRecordManager.getHqlPages(paramMap, start, limit,
//    						sortMap.get("sort"), sortMap.get("dir"));
//    				this.setUserInfo(userAccountRecordList);
//    			}
//            }
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		resultMap.put("success", true);
//		resultMap.put("data", userAccountRecordList);
//		resultMap.put("totalCount", count);
//		resultMap.put("filters", filtersMap);
//		return resultMap;
//	}
//		
//	public void setUserInfo(List<UserAccountRecord> userAccountRecordList) {
//		if(userAccountRecordList != null && !userAccountRecordList.isEmpty()) {
//			List<Long> userIdList = new ArrayList<Long>();
//			for(UserAccountRecord record : userAccountRecordList) {
//				userIdList.add(StringUtil.nullToLong(record.getUserId()));
//			}
//			
//			List<UserInfo> userInfoList = this.userInfoManager.getByIdList(userIdList);
//			Map<Long,UserInfo> userInfoMap = new HashMap<Long,UserInfo>();
//			if(userInfoList != null && !userInfoList.isEmpty()) {
//				for(UserInfo userInfo : userInfoList) {
//					userInfoMap.put(StringUtil.nullToLong(userInfo.getUserId()), userInfo);
//				}
//			}
//			
//			for(UserAccountRecord record : userAccountRecordList) {
//				UserInfo userInfo = userInfoMap.get(StringUtil.nullToLong(record.getUserId()));
//			    if(userInfo != null && userInfo.getUserId() != null) {
//			    	record.setLevel(StringUtil.nullToInteger(userInfo.getLevel()));
//			    	record.setMobile(StringUtil.null2Str(userInfo.getMobile()));
//			        record.setNickName(StringUtil.null2Str(userInfo.getNickname()));
//			        record.setIdCardName(StringUtil.null2Str(userInfo.getIdCardName()));
//                    record.setIdCardNo(StringUtil.null2Str(userInfo.getIdCardNo()));
//			    }
//			}
//		}
//	}
//	
//	/**
//	 * 手动导出
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(value = "/downLoadExportFile")
//	public @ResponseBody void downLoadExportFile(final HttpServletRequest request, final HttpServletResponse response) {
//		try {
//			String filePath = StringUtil.null2Str(request.getParameter("filePath"));
//			if (FileUtil.checkFileExists(Constants.DEPOSITORY_PATH + filePath)) {
//				File file = new File(Constants.DEPOSITORY_PATH + filePath);
//				// 以流的形式下载文件。
//				InputStream fis = new BufferedInputStream(new FileInputStream(file));
//				byte[] buffer = new byte[fis.available()];
//				fis.read(buffer);
//				fis.close();
//
//				String filename = "纯若_" + DateUtil.formatDate("yyyy年MM月dd日", DateUtil.getCurrentDate())
//						+ OrderController.XLS_FILE_NAME;
//				// 清空response
//				response.reset();
//				// 设置response的Header
//
//				if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
//					filename = URLEncoder.encode(filename, "UTF-8");
//				} else {
//					filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
//				}
//
//				response.addHeader("Content-Disposition", "attachment;filename=" + filename);
//				response.addHeader("Content-Length", StringUtil.null2Str(file.length()));
//				OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
//				response.setContentType("application/vnd.ms-excel;charset=gb2312");
//				toClient.write(buffer);
//				toClient.flush();
//				toClient.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}

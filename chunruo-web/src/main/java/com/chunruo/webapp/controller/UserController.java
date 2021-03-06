package com.chunruo.webapp.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserInviteRecord;
import com.chunruo.core.model.UserSaleStandard;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserInviteRecordManager;
import com.chunruo.core.service.UserSaleStandardManager;
import com.chunruo.core.service.UserSocietyManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.ImportFileController;
import com.chunruo.webapp.config.RbacAuthorityService;
import com.chunruo.webapp.vo.ComboVo;

@Controller
@RequestMapping("/user/")
public class UserController extends BaseController {
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private UserSocietyManager userSocietyManager;
	@Autowired
	private UserSaleStandardManager userSaleStandardManager;
	@Autowired
	private UserInviteRecordManager userInviteRecordManager;

	@RequestMapping(value="/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Map<String, Object> paramMap = new HashMap<String, Object> ();
		Map<String, Object> filtersMap = new HashMap<String, Object> ();
		List<UserInfo> userList = new ArrayList<UserInfo>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), UserInfo.class);

			//?????????@??????????????????ID???#????????????
			String keyword = StringUtil.nullToString(request.getParameter("keyword"));
			if (!StringUtil.isNullStr(keyword)) {
				// ??????
				paramMap.put("title", "%" + keyword + "%");
				paramMap.put("content", "%" + keyword + "%");
			}

			// filter??????????????????
			if(filtersMap != null && filtersMap.size() > 0){
				for(Entry<String, Object> entry : filtersMap.entrySet()){
					String key = entry.getKey();
					if(key.equals("isAgent")){
						paramMap.put(key, StringUtil.nullToBoolean(entry.getValue()));
					}else if(key.equals("level")) {
						if(!StringUtil.compareObject(0, entry.getValue())) {
							paramMap.put("isAgent", true);
							paramMap.put(key, entry.getValue());
						}else {
							paramMap.put("isAgent", false);
						}
					}else{
						paramMap.put(key, entry.getValue());
					}
				}
			}

			count = this.userInfoManager.countHql(paramMap);
			if(count != null && count.longValue() > 0L){
				userList = this.userInfoManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(userList != null && userList.size() > 0){
					this.userInfoManager.detach(userList);

					for(UserInfo userInfo : userList){
						userInfo.setMobile(StringUtil.mobileFormat(userInfo.getMobile()));
						userInfo.setStoreMobile(StringUtil.mobileFormat(userInfo.getStoreMobile()));
						userInfo.setTopMobile(StringUtil.mobileFormat(userInfo.getTopMobile()));
					}
					userList = this.userInfoManager.addAddress(userList);
					userList = this.userInfoManager.addTopStoreInfo(userList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", userList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	@RequestMapping(value = "/getUserById")
	public @ResponseBody Map<String, Object> getOrderById(final HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long userId = StringUtil.nullToLong(request.getParameter("userId"));
		try{
			UserInfo userInfo = this.userInfoManager.get(userId);
			if(userInfo != null && userInfo.getUserId() != null){
				// ????????????????????????
				this.userInfoManager.addTopStoreInfo(Arrays.asList(new UserInfo[] {userInfo}));
				
				// ????????????????????????
				if(!RbacAuthorityService.isExistRbacAuthority(request,"/admin/userInfoMobile.msp")){
					//????????????entity????????????????????????Managed?????????????????????????????????????????????????????????entity???????????????????????????????????????????????????????????????
					//????????????entity????????????merge????????????????????????
					this.userInfoManager.detach(userInfo);
					userInfo.setMobile(StringUtil.mobileFormat(userInfo.getMobile()));
					userInfo.setStoreMobile(StringUtil.mobileFormat(userInfo.getStoreMobile()));
					userInfo.setTopMobile(StringUtil.mobileFormat(userInfo.getTopMobile()));
				}
				
				resultMap.put("user", userInfo);
			}
		}catch (Exception e){
			log.debug(e.getMessage());
		}

		resultMap.put("success", true);
		return resultMap;
	}


	/**
	 * ??????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/editUserMobile")
	public @ResponseBody Map<String, Object> editUserMobile(final HttpServletRequest request, final HttpServletResponse response)
			throws UnsupportedEncodingException {
		Map<String, Object> resultMap = new HashMap<String,Object>();
		String oldMobile = StringUtil.null2Str(request.getParameter("oldMobile"));
		String newMobile = StringUtil.null2Str(request.getParameter("newMobile"));
		try {
			if(StringUtil.isNull(oldMobile) || StringUtil.isNull(newMobile)) {
				resultMap.put("success", false);
				resultMap.put("message", getText("??????????????????"));
				return resultMap;
			}else if(!StringUtil.isValidateMobile(newMobile)) {
				resultMap.put("success", false);
				resultMap.put("message", getText("??????????????????"));
				return resultMap;
			}

			UserInfo oldUserInfo = this.userInfoManager.getUserInfoByMobile(oldMobile, UserInfo.DEFUALT_COUNTRY_CODE);
			UserInfo newUserInfo = this.userInfoManager.getUserInfoByMobile(newMobile, UserInfo.DEFUALT_COUNTRY_CODE);
			if(oldUserInfo == null || oldUserInfo.getUserId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", getText("???????????????"));
				return resultMap;
			}else if(newUserInfo != null && newUserInfo.getUserId() != null) {
				resultMap.put("success", false);
				resultMap.put("message", getText("?????????????????????"));
				return resultMap;
			}
			oldUserInfo.setMobile(newMobile);
			oldUserInfo.setUpdateTime(DateUtil.getCurrentDate());
			this.userInfoManager.update(oldUserInfo);
			resultMap.put("success", true);
			resultMap.put("message", getText("????????????"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}


	/**
	 * ????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getSystemUserInfo")
	public @ResponseBody Map<String, Object> getSystemUserInfo(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		List<ComboVo>  comboList = new ArrayList<ComboVo>(); 
		try {
			List<UserInfo> userInfoList = userInfoManager.getSystemUserInfo();
			if (userInfoList != null && userInfoList.size() > 0){
				for (UserInfo userInfo : userInfoList){
					ComboVo comboVo = new ComboVo();
					comboVo.setId(userInfo.getUserId());
					comboVo.setName(userInfo.getNickname());
					comboList.add(comboVo);
				}
			}
			resultMap.put("data", comboList);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return resultMap;
	}


	/**
	 * ?????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/setSystemUser")
	public @ResponseBody Map<String, Object> setSystemUser(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = StringUtil.null2Str(request.getParameter("idListGridJson"));

		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null == idList || idList.size() == 0){
			resultMap.put("success", false);
			resultMap.put("message", "??????????????????");
			return resultMap;
		}

		try {
			userInfoManager.setSystemUserInfo(idList);
			resultMap.put("success", true);
			resultMap.put("message", this.getText("??????????????????????????????"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("success", false);
		resultMap.put("message", this.getText("??????????????????????????????"));
		return resultMap;
	}


	/**
	 * ??????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/setCustomerManager")
	public @ResponseBody Map<String, Object> setCustomerManager(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = StringUtil.null2Str(request.getParameter("idListGridJson"));
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			List<UserInfo> userInfoList = this.userInfoManager.getByIdList(idList);
			if(userInfoList != null && userInfoList.size() > 0){
				for(UserInfo userInfo : userInfoList){
					userInfo.setUpdateTime(DateUtil.getCurrentDate());
				}
				this.userInfoManager.batchInsert(userInfoList, userInfoList.size());
			}

			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("message", "?????????????????????");
		return resultMap;
	}

	/**
	 * ????????????
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/cancelUser", method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> deleteProduct(@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
			
			List<UserInfo> userInfoList = this.userInfoManager.getByIdList(idList);
			List<String> openidList = new ArrayList<String>();
			if(userInfoList != null && userInfoList.size() > 0) {
				for(UserInfo userInfo : userInfoList) {
					openidList.add(StringUtil.null2Str(userInfo.getOpenId()));
				}
			}
			this.userSocietyManager.deleteUserSocietyByOpenId(openidList,idList);
			resultMap.put("success", true);
			resultMap.put("message", "????????????");
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
	}


	/**
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/userSaleStandard")
	public @ResponseBody Map<String, Object> userSaleStandard(final HttpServletRequest request, final HttpServletResponse response)
			throws UnsupportedEncodingException {
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Integer salesNum = StringUtil.nullToInteger(request.getParameter("salesNum"));
		Integer hours = StringUtil.nullToInteger(request.getParameter("hours"));
		try {
			if(salesNum <= 0 || hours <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("??????????????????????????????0"));
				return resultMap;
			}
			List<UserSaleStandard> userSaleStandardList = this.userSaleStandardManager.getAll();
			UserSaleStandard userSaleStandard = null;
			if(userSaleStandardList != null && userSaleStandardList.size() > 0) {
				userSaleStandard = userSaleStandardList.get(0);
			}else {
				userSaleStandard = new UserSaleStandard();
				userSaleStandard.setCreateTime(DateUtil.getCurrentDate());
			}
			userSaleStandard.setSalesNum(salesNum);
			userSaleStandard.setHours(hours);
			userSaleStandard.setUpdateTime(DateUtil.getCurrentDate());
			this.userSaleStandardManager.save(userSaleStandard);
			resultMap.put("success", true);
			resultMap.put("message", getText("????????????"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}


	@RequestMapping(value = "/getUserStandard")
	public @ResponseBody Map<String, Object> getUserStandard(final HttpServletRequest request, final HttpServletResponse response)
			throws UnsupportedEncodingException {
		Map<String, Object> resultMap = new HashMap<String,Object>();
		try {

			List<UserSaleStandard> standardList = this.userSaleStandardManager.getAll();
			if(standardList != null && standardList.size() > 0) {
				UserSaleStandard userSaleStandard = standardList.get(0);
				resultMap.put("data", userSaleStandard);
			}
			resultMap.put("success", true);
			resultMap.put("message", getText("????????????"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	
	/**
	 * ??????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/vipRecordList")
	public @ResponseBody Map<String, Object> vipRecordList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<UserInviteRecord> userInviteRecordList = new ArrayList<UserInviteRecord>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));// ??????
			String filters = StringUtil.nullToString(request.getParameter("filters"));// ??????
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), UserInviteRecord.class);

			// filter??????????????????
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.userInviteRecordManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				userInviteRecordList = this.userInviteRecordManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));

				List<Long> userIdList = new ArrayList<Long>();
				List<String> orderNoList = new ArrayList<String>();
				if(userInviteRecordList != null && userInviteRecordList.size() > 0) {
					for(UserInviteRecord userInviteRecord : userInviteRecordList) {
						userIdList.add(StringUtil.nullToLong(userInviteRecord.getUserId()));
						orderNoList.add(StringUtil.null2Str(userInviteRecord.getOrderNo()));
					}


					// ?????????????????????
					Map<Long, UserInfo> userInfoMap = new HashMap<Long, UserInfo>();
					List<UserInfo> userInfoList = this.userInfoManager.getByIdList(userIdList);			    	
					if(userInfoList != null && userInfoList.size() > 0) {
						for(UserInfo userInfo : userInfoList) {
							userInfoMap.put(userInfo.getUserId(), userInfo);
						}
					}

					for(UserInviteRecord userInviteRecord : userInviteRecordList) {
						UserInfo userInfo = userInfoMap.get(userInviteRecord.getUserId());
						if(userInfo != null && userInfo.getUserId() != null) {
							userInviteRecord.setMobile(StringUtil.mobileFormat(userInfo.getMobile()));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", userInviteRecordList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	
	
	
	@RequestMapping(value = "/updateUserLevel")
	public @ResponseBody Map<String, Object> updateUserLevel(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<>();
		String userId = StringUtil.nullToString(request.getParameter("userId"));
		String level = StringUtil.nullToString(request.getParameter("level"));
		try {
			if (StringUtil.isNull(userId) || StringUtil.isNull(level)) {
				resultMap.put("success", false);
				resultMap.put("message", this.getText("????????????????????????????????????"));
				return resultMap;
			}

			UserInfo user = this.userInfoManager.get(StringUtil.nullToLong(userId));
			if (StringUtil.isNumber(level)) {
				user.setLevel(StringUtil.nullToInteger(level));
			}
			
			user.setUpdateTime(DateUtil.getCurrentDate());
			this.userInfoManager.save(user);
			resultMap.put("success", true);
			resultMap.put("message", "????????????");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("message", "?????????????????????");
		return resultMap;
	}
	
}
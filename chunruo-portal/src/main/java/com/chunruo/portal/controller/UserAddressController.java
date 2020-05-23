package com.chunruo.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.cache.portal.impl.UserAddressListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.model.Area;
import com.chunruo.core.model.UserAddress;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.UserAddressManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.UserAddressUtil;

@Controller
@RequestMapping("/api/address/")
public class UserAddressController extends BaseController{
	@Autowired
	private UserAddressManager userAddressManager;
	@Autowired
	private UserAddressListByUserIdCacheManager userAddressListByUserIdCacheManager;
	
	/**
	 * 保存地址信息
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/saveAddress")
	public @ResponseBody Map<String, Object> saveAddress(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String addressId = StringUtil.null2Str(request.getParameter("addressId"));					//序号
	    String name = StringUtil.dotStrFormat(request.getParameter("name"));						//收货人
	    String mobile = StringUtil.null2Str(request.getParameter("mobile"));						//手机号码
	    Long provinceId = StringUtil.nullToLong(request.getParameter("provinceId"));				//省份ID
	    Long cityId = StringUtil.nullToLong(request.getParameter("cityId"));						//城市ID
	    Long areaId = StringUtil.nullToLong(request.getParameter("areaId"));						//区域ID
	    String address = StringUtil.null2Str(request.getParameter("address"));						//详细地址
	    String zipcode = StringUtil.null2Str(request.getParameter("zipcode"));						//邮编
	    String realName = StringUtil.dotStrFormat(request.getParameter("realName"));                //真实姓名
	    String identityNo = StringUtil.null2Str(request.getParameter("identityNo"));				//身份证号码
	    String identityFront = StringUtil.null2Str(request.getParameter("identityFrontRelative"));	//身份证正面
	    String identityBack = StringUtil.null2Str(request.getParameter("identityBackRelative"));	//身份证反面
	    Integer isDefault = StringUtil.nullToInteger(request.getParameter("isDefault"));	 		//是否默认
	    Integer productType = StringUtil.nullToInteger(request.getParameter("productType")); 		//商品类型
	    
		try{
			// 用户信息
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			Long userId = userInfo.getUserId();
			
			// 检查是否忽略身份证校验
			identityNo = StringUtil.null2Str(identityNo).toUpperCase();
			if(StringUtil.null2Str(realName).endsWith("#")){
				realName = realName.substring(0, realName.length() - 1);
			}
			
			//根据商品类型判断参数是否完整
			UserAddress userAddress = new UserAddress();
			userAddress.setAddressId(0L);
			userAddress.setMobile(mobile);
			userAddress.setRealName(realName);
			userAddress.setIdentityNo(identityNo);
			userAddress.setIdentityBack(identityBack);
			userAddress.setIdentityFront(identityFront);
			userAddress.setName(name);
			userAddress.setAddress(address);
			userAddress.setProvinceId(provinceId);
			userAddress.setAreaId(areaId);
			userAddress.setCityId(cityId);
			userAddress.setZipcode(zipcode);
			userAddress.setIsDefault(StringUtil.nullToBoolean(isDefault));
			
			// 检查修改地址是否已存在
			UserAddress tmpUserAddress = null;
			if(StringUtil.isNumber(addressId) && (tmpUserAddress = this.userAddressManager.get(StringUtil.nullToLong(addressId))) != null){
				// 查看身份证是否被时候处理
				if(StringUtil.null2Str(identityNo).compareTo(StringUtil.identityNoFormat(tmpUserAddress.getIdentityNo())) == 0) {
					identityNo = tmpUserAddress.getIdentityNo();
					userAddress.setIdentityNo(identityNo);
				}
			}
			
			// 检查地址是否有效
			MsgModel<UserAddress> msgModel = UserAddressUtil.checkIsValidUserAddress(userAddress);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, msgModel.getMessage());
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			//省份
			if(Constants.AREA_MAP.containsKey(provinceId)){
				userAddress.setProvinceId(provinceId);
				userAddress.setProvinceName(Constants.AREA_MAP.get(provinceId).getAreaName());
			}
			//城市
			if(Constants.AREA_MAP.containsKey(cityId)){
				userAddress.setCityId(cityId);
				userAddress.setCityName(Constants.AREA_MAP.get(cityId).getAreaName());
			}
			//区县
			if(Constants.AREA_MAP.containsKey(areaId)){
				userAddress.setAreaId(areaId);
				userAddress.setAreaName(Constants.AREA_MAP.get(areaId).getAreaName());
			}
			
			// 已存在的身份证信息,需要校验身份证有效比较(防止丢失记录)
			boolean isDefaultBak = false;     //地址之前默认状态
			if(tmpUserAddress != null && tmpUserAddress.getAddressId() != null){
				userAddress.setAddressId(tmpUserAddress.getAddressId());
				userAddress.setCreateTime(tmpUserAddress.getCreateTime());
				isDefaultBak = StringUtil.nullToBoolean(tmpUserAddress.getIsDefault());
				
				// 收货人姓名
				if(StringUtil.isChineseCharacters(name)){
					userAddress.setName(name);
				}
				
				if(!StringUtil.compareObject(productType, 0)) {
					// 订购人姓名
					if(StringUtil.isChineseCharacters(realName)){
						userAddress.setRealName(realName);
					}
					
					// 身份证号码
					if(!StringUtil.isValidIdentityCardNO(userAddress.getIdentityNo())){
						userAddress.setIdentityNo(StringUtil.null2Str(tmpUserAddress.getIdentityNo()).toUpperCase());
					}
					
					// 身份证背面
					if(StringUtil.isNull(userAddress.getIdentityBack())){
						userAddress.setIdentityBack(tmpUserAddress.getIdentityBack());
					}
					
					// 身份证正面
					if(StringUtil.isNull(userAddress.getIdentityFront())){
						userAddress.setIdentityFront(tmpUserAddress.getIdentityFront());
					}
				}
				
				// 邮编
				if(StringUtil.isNull(userAddress.getZipcode())){
					userAddress.setZipcode(tmpUserAddress.getZipcode());
				}
			}
			
			// 保存用户地址信息
			userAddress.setUserId(userId);
			userAddress.setUpdateTime(DateUtil.getCurrentDate());
			userAddress = this.userAddressManager.saveUserAddress(userAddress, isDefaultBak);
			
			try{
				// 删除用户缓存
				this.userAddressListByUserIdCacheManager.removeSession(userId);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			resultMap.put("addressId", userAddress.getAddressId());
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "保存成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "保存失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	
	/**
	 * 更新地址信息
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/updateAddress")
	public @ResponseBody Map<String, Object> updateAddress(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String addressId = StringUtil.null2Str(request.getParameter("addressId"));					//序号
		String identityName = StringUtil.dotStrFormat(request.getParameter("identityName"));		//身份证姓名
	    String identityNo = StringUtil.null2Str(request.getParameter("identityNo"));				//身份证号码
	    String identityFront = StringUtil.null2Str(request.getParameter("identityFrontRelative"));	//身份证正面
	    String identityBack = StringUtil.null2Str(request.getParameter("identityBackRelative"));	//身份证反面
	    Integer productType = StringUtil.nullToInteger(request.getParameter("productType")); 		//商品类型
	    
		try{
			// 用户信息
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			Long userId = userInfo.getUserId();
			
			// 检查是否忽略身份证校验
			identityNo = StringUtil.null2Str(identityNo).toUpperCase();
			if(StringUtil.null2Str(identityName).endsWith("#")){
				identityName = identityName.substring(0, identityName.length() - 1);
			}
			
			// 检查更新对象是否存在
			UserAddress userAddress = this.userAddressManager.get(StringUtil.nullToLong(addressId));
			if(userAddress == null || userAddress.getAddressId() == null){
				// 保存对象不存在
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "错误,对象不存在");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(!StringUtil.compareObject(userAddress.getUserId(), userInfo.getUserId())){
				// 非用户本人操作
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "错误,非用户本人操作");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			//检查格式化的身份证是否一致
			String dbIdentityNo = StringUtil.identityNoFormat(StringUtil.null2Str(userAddress.getIdentityNo()));
			if(StringUtil.compareObject(dbIdentityNo, identityNo)) {
				//使用已存在的地址信息中的身份证号
				identityNo = StringUtil.null2Str(userAddress.getIdentityNo()).toUpperCase();
			}
			
			// 需要身份证信息类型
			List<Integer> identityCardtatusList = new ArrayList<Integer> ();
			identityCardtatusList.add(GoodsType.GOODS_TYPE_CROSS);	//跨境
			identityCardtatusList.add(GoodsType.GOODS_TYPE_DIRECT);	//直邮
			identityCardtatusList.add(GoodsType.GOODS_TYPE_DIRECT_GO);//行邮
			
			List<Integer> directTypeList = new ArrayList<Integer>();
			directTypeList.add(GoodsType.GOODS_TYPE_DIRECT);    //直邮
			directTypeList.add(GoodsType.GOODS_TYPE_DIRECT_GO); //行邮
			
			if(identityCardtatusList.contains(productType)){
				// 检查身份证是否有效
				if(!StringUtil.isChineseCharacters(identityName)){
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "输入的姓名无效");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}else if(!StringUtil.isValidIdentityCardNO(identityNo)){
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "输入的身份证号码无效");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
				
				//检查中文名字是否包敏感词
				MsgModel<String> xmsgModel = UserAddressUtil.isContaintSensitiveWord(StringUtil.null2Str(identityName));
				if(StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, String.format("收货人包含\"%s\"特殊字符", StringUtil.null2Str(xmsgModel.getData())));
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
				
			}
			
			// 身份证姓名
			if(StringUtil.isChineseCharacters(identityName)){
				userAddress.setRealName(identityName);
			}
			// 身份证号码
			if(StringUtil.isValidIdentityCardNO(identityNo)){
				userAddress.setIdentityNo(StringUtil.null2Str(identityNo).toUpperCase());
			}
			// 身份证正面
			if(!StringUtil.isNull(identityFront)){
				userAddress.setIdentityFront(identityFront);
			}
			// 身份证背面
			if(!StringUtil.isNull(identityBack)){
				userAddress.setIdentityBack(identityBack);
			}
			
			// 保存用户地址信息
			userAddress.setUserId(userId);
			userAddress.setUpdateTime(DateUtil.getCurrentDate());
			userAddress = this.userAddressManager.saveUserAddress(userAddress, userAddress.getIsDefault());
			
			try{
				// 删除用户缓存
				this.userAddressListByUserIdCacheManager.removeSession(userId);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			resultMap.put("addressId", userAddress.getAddressId());
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "保存成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "保存失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	
	/**
	 * 设置默认地址
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/setDefaultAddress")
	public @ResponseBody Map<String, Object> setDefaultAddress(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String addressId = StringUtil.null2Str(request.getParameter("addressId"));					//序号
	    
		try{
			// 用户信息
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			Long userId = userInfo.getUserId();
			
			// 检查更新对象是否存在
			UserAddress userAddress = this.userAddressManager.get(StringUtil.nullToLong(addressId));
			if(userAddress == null || userAddress.getAddressId() == null){
				// 保存对象不存在
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "错误,对象不存在");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(!StringUtil.compareObject(userAddress.getUserId(), userInfo.getUserId())){
				// 非用户本人操作
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "错误,非用户本人操作");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 保存用户地址信息
			userAddress.setUserId(userId);
			userAddress.setUpdateTime(DateUtil.getCurrentDate());
			userAddress = this.userAddressManager.setDefaultAddress(userAddress);
			
			try{
				// 删除用户缓存
				this.userAddressListByUserIdCacheManager.removeSession(userId);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			resultMap.put("addressId", userAddress.getAddressId());
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "保存成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "保存失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	
	/**
	 * 删除地址
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/deleteAddress")
	public @ResponseBody Map<String, Object> deleteAddress(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long addressId = StringUtil.nullToLong(request.getParameter("addressId"));

		try {
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			UserAddress userAddress = this.userAddressManager.getUserAddressByAddressId(addressId,
					userInfo.getUserId());
			if (userAddress != null && userAddress.getAddressId() != null) {
				// 非本人删除
				if (!StringUtil.compareObject(userInfo.getUserId(), userAddress.getUserId())) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "删除失败");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
				this.userAddressManager.removeUserAddressById(userAddress);
			}

			try {
				// 清除缓存
				userAddressListByUserIdCacheManager.removeSession(userInfo.getUserId());
			} catch (Exception e) {
				e.printStackTrace();
			}

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "删除成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "删除失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * 智能录入
	 * 
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value = LoginInterceptor.LOGIN)
	@RequestMapping(value = "/intelligenInput")
	public @ResponseBody Map<String, Object> intelligenInput(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String address = StringUtil.null2Str(request.getParameter("address"));

		try {
			if (StringUtil.isNull(address)) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "请输入信息");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 去掉特殊符号
			address = StringUtil.getEscapeText(address); 

			String mobile = StringUtil.getMobileFromStr(address);
			if(StringUtil.isNull(mobile)) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "请输入手机号码");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			int mobileIndex = address.indexOf(mobile);
			String userName = "";
			if(mobileIndex >= 0) {
				userName = address.substring(0, mobileIndex > 4 ? 4 : address.indexOf(mobile));
			}
			
			//去掉手机号码
			address = address.replace(mobile, "");
			if(StringUtil.isNull(address)) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "地址信息不全");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 所有匹配的省市区
			List<Area> provinceList = new ArrayList<Area>();
			List<Area> cityList = new ArrayList<Area>();
			List<Area> regionList = new ArrayList<Area>();
			Map<Long, Area> provinceMap = new HashMap<Long, Area>();
			Map<Long, Area> cityMap = new HashMap<Long, Area>();
			Map<Long, Area> regionMap = new HashMap<Long, Area>();
			Map<Long, Area> areaMap = Constants.AREA_MAP;
			if (areaMap != null && areaMap.size() > 0) {
				for (Map.Entry<Long, Area> entry : areaMap.entrySet()) {
					Area area = entry.getValue();
					if (area != null && area.getAreaId() != null) {
						String areaName = StringUtil.null2Str(area.getAreaName());
						String shortName = StringUtil.null2Str(area.getShortName());
						if (address.contains(areaName) || address.contains(shortName)) {
							if (StringUtil.compareObject(area.getLevel(), 1)) {
								// 省
								provinceList.add(area);
								provinceMap.put(StringUtil.nullToLong(area.getAreaId()), area);
							} else if (StringUtil.compareObject(area.getLevel(), 2)) {
								// 市
								cityList.add(area);
								cityMap.put(StringUtil.nullToLong(area.getAreaId()), area);
							} else if (StringUtil.compareObject(area.getLevel(), 3)) {
								// 区
								regionList.add(area);
								regionMap.put(StringUtil.nullToLong(area.getAreaId()), area);
							}
						}
					}
				}

				Long provinceId = 0L;
				Long cityId = 0L;
				Long regionId = 0L;
				String provinceName = "";
				String cityName = "";
				String regionName = "";
				if (regionList != null && regionList.size() > 0) {
					for (Area region : regionList) {
						Area city = cityMap.get(StringUtil.nullToLong(region.getParentId()));
						if (city != null && city.getAreaId() != null) {
							// 最大化匹配
							Area province = areaMap.get(StringUtil.nullToLong(city.getParentId()));
							if (province != null && province.getAreaId() != null) {
								provinceId = StringUtil.nullToLong(province.getAreaId());
								cityId = StringUtil.nullToLong(city.getAreaId());
								regionId = StringUtil.nullToLong(region.getAreaId());
								provinceName = StringUtil.null2Str(province.getAreaName());
								cityName = StringUtil.null2Str(city.getAreaName());
								regionName = StringUtil.null2Str(region.getAreaName());
								String provinceShortName = StringUtil.null2Str(province.getShortName());
								String cityShortName = StringUtil.null2Str(province.getShortName());
								String regionShortName = StringUtil.null2Str(region.getShortName());
								if(address.contains(provinceName)) {
									address = address.replace(provinceName,"");
								}else if(address.contains(provinceShortName)) {
									address = address.replace(provinceShortName,"");
								}
								if(address.contains(cityName)) {
									address = address.replace(cityName,"");
								}else if(address.contains(cityShortName)) {
									address = address.replace(cityShortName,"");
								}
								if(address.contains(regionName)) {
									address = address.replace(regionName,"");
								}else if(address.contains(regionShortName)) {
									address = address.replace(regionShortName,"");
								}
								break;
							}
						}
					}
				}

				String addressInfo = "";
				if(!StringUtil.isNull(address)) {
					int length = address.length();
					if(StringUtil.isNull(userName)) {
						userName = address.substring(length - 2 < 0 ? 0 : length - 2,length);
					}
					addressInfo = address.replace(userName, "");
				}
				
				resultMap.put("provinceId", provinceId);
				resultMap.put("cityId", cityId);
				resultMap.put("regionId", regionId);
				resultMap.put("provinceName", provinceName);
				resultMap.put("cityName", cityName);
				resultMap.put("regionName", regionName);
				resultMap.put("userName", userName);
				resultMap.put("mobile", mobile);
				resultMap.put("addressInfo", addressInfo);
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
				resultMap.put(PortalConstants.MSG, "匹配成功");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "匹配失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
}

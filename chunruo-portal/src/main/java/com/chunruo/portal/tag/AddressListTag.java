package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chunruo.cache.portal.impl.UserAddressListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.model.UserAddress;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.ListPageUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.ListPageVo;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.UserAddressUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 用户地址列表
 * 按用户ID查询
 * @author chunruo
 *
 */
public class AddressListTag extends BaseTag {
	
	public TagModel<List<UserAddress>> getData(Object productType_1, Object pageidx_1, Object pagesize_1, Object lastId_1, Object keyword_1){
		Integer pageidx = StringUtil.nullToInteger(pageidx_1); 
		Integer pagesize = StringUtil.nullToInteger(pagesize_1);
		Integer productType = StringUtil.nullToInteger(productType_1);
		Long lastId = StringUtil.nullToLong(lastId_1);
		String keyword = StringUtil.null2Str(keyword_1).toLowerCase();
		keyword = StringUtil.decode(keyword);

		//设置分页
		if (pagesize == null || pagesize < 1)
			pagesize = PortalConstants.PAGE_LIST_SIZE;
		if (pageidx == null || pageidx <= 0)
			pageidx = 1;

		TagModel<List<UserAddress>> tagModel = new TagModel<List<UserAddress>> ();
		try{
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(userInfo == null || userInfo.getUserId() == null){
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}
			
			final Long userId = userInfo.getUserId();
			final UserAddressListByUserIdCacheManager userAddressListByUserIdCacheManager = Constants.ctx.getBean(UserAddressListByUserIdCacheManager.class);
			
			List<Long> userAddressIdList = new ArrayList<Long> ();
			final Map<Long, UserAddress> userAddressMap = new HashMap<Long, UserAddress> ();
			Map<String, UserAddress> userAddressIdMap = userAddressListByUserIdCacheManager.getSession(userId);
			if(userAddressIdMap != null && userAddressIdMap.size() > 0){
				// 找到默认地址设置最新时间
				for(UserAddress userAddress : userAddressIdMap.values()) {
					if(StringUtil.nullToBoolean(userAddress.getIsDefault())) {
						userAddress.setUpdateTime(DateUtil.getCurrentDate());
					}
				}
				
				//  排序
				List<Map.Entry<String, UserAddress>> mappingList = new ArrayList<Map.Entry<String, UserAddress>> (userAddressIdMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<String, UserAddress>>(){
					public int compare(Map.Entry<String, UserAddress> obj1, Map.Entry<String, UserAddress> obj2){
						UserAddress value1 = obj1.getValue();
						UserAddress value2 = obj2.getValue();
						if(value1.getUpdateTime() == null) {
							return 1;
						}else if(value2.getUpdateTime() == null){
							return -1;
						}
						return value2.getUpdateTime().compareTo(value1.getUpdateTime());
					}
				}); 
				
				for(Map.Entry<String, UserAddress> entry : mappingList){
					UserAddress userAddress = entry.getValue();
					if(!StringUtil.isNull(keyword)) {
						//检查收货人，号码是否匹配
						boolean isMathcKeyword = false;
						if(StringUtil.compareObject(userAddress.getName(), keyword)
								|| StringUtil.compareObject(userAddress.getMobile(), keyword)
								|| StringUtil.null2Str(userAddress.getAddress()).contains(keyword)) {
							isMathcKeyword = true;
						}
						
						// 关键字搜索不能存在
						if(!isMathcKeyword) {
							continue;
						}
					}
					userAddressIdList.add(userAddress.getAddressId());
					userAddressMap.put(userAddress.getAddressId(), userAddress);
				}
			}
			
			/**
			 * 自动List分页工具
			 */
			final int goodsType = productType;
			List<Integer> goodsTypeList = new ArrayList<Integer>();
			goodsTypeList.add(GoodsType.GOODS_TYPE_DIRECT);
			goodsTypeList.add(GoodsType.GOODS_TYPE_DIRECT_GO);
			
			ListPageUtil<UserAddress> pageUtil = new ListPageUtil<UserAddress> (){
				@Override
				public UserAddress addObject(Long objectId) {
					// 返回对象自定义
					if(userAddressMap != null && userAddressMap.containsKey(objectId)){
						UserAddress address = userAddressMap.get(objectId);
						address.setFullAddress(UserAddressUtil.getFullAddressInfo(address));
						
						//直邮、行邮检查身份证照片
						if(goodsTypeList.contains(goodsType)) {
							address.setIdentityFrontData(UserAddressUtil.getBase64ImageData(StringUtil.null2Str(address.getIdentityFront())));
							address.setIdentityBackData(UserAddressUtil.getBase64ImageData(StringUtil.null2Str(address.getIdentityBack())));
						}
						// 检查身份证信息是否有效
						MsgModel<UserAddress> msgModel = UserAddressUtil.checkIsValidUserAddress(address);
						address.setIsHavRealInfo(StringUtil.nullToBoolean(msgModel.getIsSucc()));
						return address;
					}
					return null;
				}
			};

			/**
			 * 返回自动分页结果
			 */
			ListPageVo<List<UserAddress>> listPageVo = pageUtil.getPageList(userAddressIdList, lastId, pageidx, pagesize);
			if (StringUtil.nullToBoolean(listPageVo.getIsNextPageURL())) {
				StringBuffer urls = new StringBuffer (this.getRequestURL(request) + "&");
				urls.append("keyword=" + keyword + "&");
				urls.append("pageidx=" + (++pageidx) + "&");
				urls.append("lastId=" + listPageVo.getLastId() + "&");
				urls.append("pagesize=" + pagesize);
				tagModel.setNextPageURL(urls.toString());
			}
			tagModel.setTotal(Long.parseLong(listPageVo.getCount().toString()));
			tagModel.setData(listPageVo.getDataList());
		}catch(Exception e){
			e.printStackTrace();
		}

		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}
}

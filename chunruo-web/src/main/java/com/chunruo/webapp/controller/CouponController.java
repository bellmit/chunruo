package com.chunruo.webapp.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.UserCouponStatus;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.CouponTask;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.UserCoupon;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserInviteRecord;
import com.chunruo.core.service.CouponManager;
import com.chunruo.core.service.CouponTaskManager;
import com.chunruo.core.service.ProductCategoryManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.UserCouponManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserInviteRecordManager;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.MyExcelExport;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.ImportFileController;
import com.chunruo.webapp.util.OrderUtil;
import com.chunruo.webapp.vo.CouponData;
import com.chunruo.webapp.vo.CouponProductCatrgory;

@Controller
@RequestMapping("/coupon/")
public class CouponController extends BaseController {
	private Logger log = LoggerFactory.getLogger(CouponController.class);
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private UserCouponManager userCouponManager;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private ProductCategoryManager productCategoryManager;
	@Autowired
	private ProductManager productManager;
	@Autowired
	private CouponTaskManager couponTaskManager;
	@Autowired
	private UserInviteRecordManager userInviteRecordManager;
	
	
	@RequestMapping(value = "/couponList")
	public @ResponseBody Map<String, Object> couponList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<Coupon> couponList = new ArrayList<Coupon>();
		List<CouponData> couponDataList = new ArrayList<CouponData>(); // 数据统计
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Coupon.class);
			String tag = StringUtil.nullToString(request.getParameter("tag"));

			// 内容、@用户名、用户ID、#手机号码
			String keyword = StringUtil.nullToString(request.getParameter("keyword"));
			if (!StringUtil.isNullStr(keyword)) {
				// 内容
				paramMap.put("title", "%" + keyword + "%");
				paramMap.put("content", "%" + keyword + "%");
			}

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			paramMap.put("isRechargeProductCoupon", false);
			count = this.couponManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				couponList = this.couponManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"),
						sortMap.get("dir"));
			}

			if (!StringUtil.isNull(tag)) {
				// 统计优惠券发送的数量
				List<UserCoupon> userCouponList = this.userCouponManager.getAll(); 
				Map<Long, Integer> countMap = getSendCount(userCouponList);

				if (couponList != null && couponList.size() > 0) {
					for (Coupon coupon : couponList) {
						CouponData couponData = new CouponData();
						couponData.setCouponId(coupon.getCouponId());
						couponData.setCouponName(coupon.getCouponName());
						couponData.setUsedCount(coupon.getUsedCount());
						couponData.setSendCount(countMap.get(coupon.getCouponId()));
						couponDataList.add(couponData);
					}
				}
				resultMap.put("data", couponDataList);
				resultMap.put("totalCount", count);
				resultMap.put("filters", filtersMap);
				return resultMap;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put("data", couponList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	/**
	 * 获取用户优惠券列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/userCouponList")
	public @ResponseBody Map<String, Object> userCouponList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<UserCoupon> userCouponList = new ArrayList<UserCoupon>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), UserCoupon.class);

			// 内容、@用户名、用户ID、#手机号码
			String keyword = StringUtil.nullToString(request.getParameter("keyword"));
			if (!StringUtil.isNullStr(keyword)) {
				// 内容
				paramMap.put("title", "%" + keyword + "%");
				paramMap.put("content", "%" + keyword + "%");
			}

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				//店铺名称过滤做特殊处理,转换成ID过滤
				if(filtersMap.containsKey("mobile")){
					List<Long> userIdList = new ArrayList<>();
					Map<String,Object> userInfoMap = new HashMap<>();
					userInfoMap.put("mobile", filtersMap.remove("mobile"));
					List<UserInfo> userInfoList = this.userInfoManager.getHqlPages(userInfoMap);
					if(userInfoList != null && userInfoList.size() > 0){
						for(UserInfo userInfo : userInfoList){
							userIdList.add(userInfo.getUserId());
						}
					}
					
					if(userIdList != null && userIdList.size() > 0){
						filtersMap.put("userId", userIdList);
					}else{
						resultMap.put("data", null);
						resultMap.put("totalCount", 0);
						resultMap.put("filters", filtersMap);
						return resultMap;
					}
				}
				
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
				
			}
			count = this.userCouponManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				userCouponList = this.userCouponManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(userCouponList != null && userCouponList.size() > 0) {
					Set<Long> userIdList = new HashSet<Long> ();
					Set<Long> couponIdList = new HashSet<Long> ();
					for(UserCoupon userCoupon : userCouponList) {
						userIdList.add(userCoupon.getUserId());
						couponIdList.add(userCoupon.getCouponId());
					}

					Map<Long, Coupon> couponByCouponIdMap = new HashMap<Long, Coupon> ();
					if(couponIdList != null && couponIdList.size() > 0){
						List<Coupon> couponList = this.couponManager.getByIdList(StringUtil.longSetToList(couponIdList));
						if(couponList != null && couponList.size() > 0){
							for(Coupon coupon : couponList){
								couponByCouponIdMap.put(coupon.getCouponId(), coupon);
							}
						}
					}

					Map<Long, UserInfo> userInfoByIdMap = new HashMap<Long, UserInfo> ();
					if(userIdList != null && userIdList.size() > 0){
						List<UserInfo> userInfoList = this.userInfoManager.getByIdList(StringUtil.longSetToList(userIdList));
						if(userInfoList != null && userInfoList.size() > 0){
							for(UserInfo userInfo : userInfoList){
								userInfoByIdMap.put(userInfo.getUserId(), userInfo);
							}
						}
					}

					for(UserCoupon userCoupon : userCouponList) {
						userCoupon.setCoupon(couponByCouponIdMap.get(userCoupon.getCouponId()));
						UserInfo userInfo = userInfoByIdMap.get(userCoupon.getUserId());
						if(userInfo != null &&userInfo.getUserId() != null) {
							userCoupon.setMobile(StringUtil.mobileFormat(userInfo.getMobile()));
							userCoupon.setNickName(userInfo.getNickname());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", userCouponList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	
	/**
	 * 任务优惠券列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/couponTaskList")
	public @ResponseBody Map<String, Object> couponTaskList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<CouponTask> couponTaskList = new ArrayList<CouponTask>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), CouponTask.class);

			// 内容、@用户名、用户ID、#手机号码
			String keyword = StringUtil.nullToString(request.getParameter("keyword"));
			if (!StringUtil.isNullStr(keyword)) {
				// 内容
				paramMap.put("title", "%" + keyword + "%");
				paramMap.put("content", "%" + keyword + "%");
			}

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}
			
			count = this.couponTaskManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				couponTaskList = this.couponTaskManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"),sortMap.get("dir"));
				if(couponTaskList != null && couponTaskList.size() > 0) {
					Set<Long> couponIdList = new HashSet<Long> ();
					for(CouponTask couponTask : couponTaskList) {
						couponIdList.add(couponTask.getCouponId());
					}

					Map<Long, Coupon> couponByCouponIdMap = new HashMap<Long, Coupon> ();
					if(couponIdList != null && couponIdList.size() > 0){
						List<Coupon> couponList = this.couponManager.getByIdList(StringUtil.longSetToList(couponIdList));
						if(couponList != null && couponList.size() > 0){
							for(Coupon coupon : couponList){
								couponByCouponIdMap.put(coupon.getCouponId(), coupon);
							}
						}
					}
					
					for(CouponTask couponTask: couponTaskList) {
						couponTask.setCoupon(couponByCouponIdMap.get(couponTask.getCouponId()));
					}
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", couponTaskList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	@RequestMapping(value = "/categoryTreeList")
	public @ResponseBody Map<String, Object> categoryTreeList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String categoryId = StringUtil.null2Str(request.getParameter("node"));
		List<ProductCategory> categoryTreeList = new ArrayList<ProductCategory> ();
		List<CouponProductCatrgory> couponCategoryTreeList = new ArrayList<CouponProductCatrgory> ();
		List<Product> productTreeList = new ArrayList<Product> ();
		try {
			ProductCategory productCategory = null;
			if(StringUtil.isNumber(categoryId) && (productCategory = this.productCategoryManager.get(StringUtil.nullToLong(categoryId))) != null){
				// 根据二级分类ID查询商品
				productTreeList = this.productManager.getProductListByCategoryId(productCategory.getCategoryId(), true);
				if(productTreeList != null && productTreeList.size() > 0){
					for(Product product : productTreeList){
						CouponProductCatrgory couponProductCatrgory = new CouponProductCatrgory();
						couponProductCatrgory.setId(product.getProductId());
						couponProductCatrgory.setName(product.getName());
						couponProductCatrgory.setLeaf(true);
						couponProductCatrgory.setDescription(null);
						couponProductCatrgory.setProfit(null);
						couponProductCatrgory.setStatus(1);
						couponProductCatrgory.setSelectType(2);
						couponCategoryTreeList.add(couponProductCatrgory);	
					}
				}
				resultMap.put("children", couponCategoryTreeList);
				resultMap.put("total_count", couponCategoryTreeList.size());
			}else{
				// 全部二级分类
				categoryTreeList = this.productCategoryManager.getProductCategoryByLevel(2, 1);
				if(categoryTreeList != null && categoryTreeList.size() > 0){
					for(ProductCategory category : categoryTreeList){
						CouponProductCatrgory couponProductCatrgory=new CouponProductCatrgory();
						couponProductCatrgory.setId(category.getCategoryId());
						couponProductCatrgory.setLeaf(false);
						couponProductCatrgory.setDescription(null);
						couponProductCatrgory.setProfit(null);
						couponProductCatrgory.setName(category.getName());
						couponProductCatrgory.setStatus(1);
						couponProductCatrgory.setSelectType(1);
						couponCategoryTreeList.add(couponProductCatrgory);
					}
				}
				resultMap.put("children", couponCategoryTreeList);
				resultMap.put("total_count", couponCategoryTreeList.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
	@RequestMapping(value = "/XProductCategoryList")
	public @ResponseBody Map<String, Object> XbrandList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ProductCategory> productCategoryList = new ArrayList<ProductCategory> ();
		try {
			productCategoryList= this.productCategoryManager.getProductCategoryByLevel(ProductCategory.PRODUCT_CATEGORY_LEVEL_SECOND, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", productCategoryList);
		resultMap.put("totalCount", productCategoryList.size());
		return resultMap;
	}

	/**
	 * 优惠券保存
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveCoupon")
	public @ResponseBody Map<String, Object> saveCoupon(@ModelAttribute("coupon") Coupon coupon,final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean isTask = StringUtil.isNull(request.getParameter("taskId"));
		Date receiveBeginTime = StringUtil.strToDate(request.getParameter("receiveBeginTime"), DateUtil.DATE_FORMAT);
		Date receiveEndTime = StringUtil.strToDate(request.getParameter("receiveEndTime"), DateUtil.DATE_FORMAT);
		String proudctIdListStr = StringUtil.null2Str(request.getParameter("productId"));
		String categoryIdListStr = StringUtil.null2Str(request.getParameter("categoryId"));
		try {
			//判断优惠券名称不得超过十个汉字
			byte[] bytes = coupon.getCouponName().getBytes("UTF-8");
			if (bytes.length > 30) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("优惠券名称过长!!!"));
				return resultMap;
			}
   
			if(StringUtil.compareObject(0, StringUtil.nullToInteger(coupon.getUseRangeType()))) {
				//使用范围
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("请选择优惠券使用范围"));
				return resultMap;
			}else if (receiveEndTime.getTime() < receiveBeginTime.getTime()) {
				//优惠券结束领取时间不得小于开始领取时间
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("优惠券结束领取时间不得小于开始领取时间"));
				return resultMap;
			} else if (StringUtil.isNull(coupon.getCouponName())) {
				//优惠券名称不能为空
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("优惠券名称不能为空"));
				return resultMap;
			} else if (StringUtil.nullToInteger(coupon.getEffectiveTime()).compareTo(0) <= 0) {
				//优惠券有效期必须大于0天且小于60天
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("优惠券有效期必须大于0"));
				return resultMap;
			}else if(StringUtil.nullToInteger(coupon.getTotalCount()).compareTo(0) <= 0) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("优惠券数量必须大于0"));
				return resultMap;
			}
             //判断优惠券详情是否为空
			if (coupon.getAttribute() == Coupon.COUPON_ATTRIBUTE_PRODUCT ) {
				List<Long> productIdList = StringUtil.stringToLongArray(proudctIdListStr);
				if(productIdList == null || productIdList.size() <=0) {
					//如果优惠券详情为空，用户优惠券属性选择了 ‘商品’
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("请选择商品"));
					return resultMap;
				}else if(StringUtil.nullToInteger(productIdList.size()) > 200) {
					//最多选择200个商品
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("最多选择20个商品"));
					return resultMap;
				}
				coupon.setAttributeContent(proudctIdListStr);
			} else if (coupon.getAttribute() == Coupon.COUPON_ATTRIBUTE_CATEGORY) {
				List<Long> categoryIdList = StringUtil.stringToLongArray(categoryIdListStr);
				if(categoryIdList == null || categoryIdList.size() <= 0) {
					//如果优惠券详情为空，用户优惠券属性选择了 ‘商品分类’
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("请选择分类"));
					return resultMap;
				}else if(StringUtil.nullToInteger(categoryIdList.size()) > 20) {
					//最多选择20个分类
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("最多选择20个二级分类"));
					return resultMap;
				}
				coupon.setAttributeContent(categoryIdListStr);
			}
			
			
			if (coupon.getCouponType() == Coupon.COUPON_TYPE_FULL) {
				//满减券
				if (StringUtil.isNull(coupon.getFullAmount())) {
					//满减券需要设置满多少金额
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("请设置满多少金额"));
					return resultMap;
				} else if (coupon.getFullAmount() <= 0 || !StringUtil.isNumber(coupon.getFullAmount())) {
					//满减金额不得为0
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("满减金额设置不正确"));
					return resultMap;
				}else if (coupon.getFullAmount() < coupon.getGiveAmount()) {
					//满额小于减额
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("满减券金额设置不正确，请重新设置！！！"));
					return resultMap;
				}
			}else if (StringUtil.compareObject(coupon.getCouponType(), Coupon.COUPON_TYPE_CASH)) {
				//代金券
				if (coupon.getGiveAmount() <= 0 || !StringUtil.isNumber(coupon.getGiveAmount())) {
					//减额不得小于0
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("满减金额设置不正确"));
					return resultMap;
				}
				coupon.setFullAmount(coupon.getGiveAmount());
			}
			
			coupon.setIsEnable(true);
			coupon.setUsedCount(0);
			coupon.setIsGiftCoupon(false);
			coupon.setIsRechargeProductCoupon(false);
			coupon.setRechargeTemplateId(0L);
			coupon.setCreateTime(new Date());
			coupon.setUpdateTime(new Date());
			coupon.setAdminUserName(this.getUserName(request));
			
			// 判断该券是否关联任务
			if (!isTask) {
				Long taskId = StringUtil.nullToLong(request.getParameter("taskId"));
				this.couponTaskManager.updateCouponTaskByBindCoupon(taskId, coupon);
			}else {
				this.couponManager.save(coupon);
			}

			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("保存失败"));
		return resultMap;
	}

	/**
	 * 设置优惠券状态
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/setSendStatus")
	public @ResponseBody Map<String, Object> setSendStatus(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long couponId = StringUtil.nullToLong(request.getParameter("couponId"));
		String record = request.getParameter("idListGridJson");
		try {
			// 检查ids对象是否有效
			List<Integer> levelList = new ArrayList<Integer> ();
			try{
				levelList = (List<Integer>) StringUtil.getIdIntegerList(record);
				if(levelList == null || levelList.size() <= 0){
					resultMap.put("success", false);
					resultMap.put("message", getText("请选择发送对象"));
					return resultMap;
				}
			}catch(Exception e){
				resultMap.put("success", false);
				resultMap.put("message", getText("请选择发送对象"));
				return resultMap;
			}
			
			Coupon coupon = this.couponManager.get(couponId);
			if (coupon != null && coupon.getCouponId() != null) {
				
				if (StringUtil.compareObject(coupon.getReceiveType(), Coupon.receive_Type_AUTO)) {
					MsgModel<List<UserCoupon>> msgModel = this.autoSendCoupon(coupon, levelList, null,1);
					if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
						resultMap.put("success", false);
						resultMap.put("message", getText("错误,优惠券派发失败"));
						return resultMap;
					}
					
					coupon.setSender(StringUtil.intArrayToString(levelList));
				}
				coupon.setIsEnable(true);
				coupon.setUpdateTime(new Date());
			}
			
			this.couponManager.update(coupon);
			if (coupon.getIsEnable()) {
				Constants.COUPON_MAP.put(coupon.getCouponId(), coupon);
			}
			
			resultMap.put("success", true);
			resultMap.put("message", getText("submit.success"));
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", true);
		resultMap.put("message", getText("submit.failure"));
		return resultMap;
	}

	@RequestMapping(value = "/setSendStatusByExcel")
	public @ResponseBody Map<String, Object> setSendStatusByExcel(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long couponId = StringUtil.nullToLong(request.getParameter("couponId"));
		Integer number = StringUtil.nullToInteger(request.getParameter("number"));
		String headerGridJson = StringUtil.null2Str(request.getParameter("headerGridJson"));
		String dataGridJson = StringUtil.null2Str(request.getParameter("dataGridJson"));
		List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>>();
		List<UserInfo> userInfoList=new ArrayList<UserInfo>();
		try {

			if(number <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("发送数量无效"));
				return resultMap;
			}
			// 检查导入数据是否有效
			List<Object> headerListMap = StringUtil.jsonDeserialize(headerGridJson);
			if (headerListMap == null || headerListMap.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("errors.select.object"));
				return resultMap;
			}

			// 检查导入数据是否有效
			List<Object> dataListMap = StringUtil.jsonDeserialize(dataGridJson);
			if (dataListMap == null || dataListMap.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("errors.select.object"));
				return resultMap;
			}

			objectMapList = ImportFileController.importDataToMapList(dataListMap, headerListMap);
			if (objectMapList == null || objectMapList.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("errors.select.object"));
				return resultMap;
			}

			//判断该手机号码是否有效
			List<Long> userIdList = new ArrayList<Long>();
			for(Map<String,String> map : objectMapList) {
				Long userId=StringUtil.nullToLong(map.get("用户序号"));
				userIdList.add(userId);
			}


			//判断该手机号码账户是否存在
			userInfoList = this.userInfoManager.getByIdList(userIdList);
		}catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}

		try {
			Coupon coupon = this.couponManager.get(couponId);
			if (coupon != null && coupon.getCouponId() != null) {
				if (StringUtil.compareObject(coupon.getReceiveType(), Coupon.receive_Type_AUTO)) {
					MsgModel<List<UserCoupon>> msgModel = this.autoSendCoupon(coupon,null,userInfoList,number);
					if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
						resultMap.put("error", true);
						resultMap.put("success", true);
						resultMap.put("message", getText("错误,优惠券派发失败"));
						return resultMap;
					}
				}
				
				coupon.setIsEnable(true); 
				coupon.setSender("excel user");
				coupon.setUpdateTime(new Date());
			}
			
			this.couponManager.update(coupon);
			if(coupon.getIsEnable()){
				Constants.COUPON_MAP.put(coupon.getCouponId(), coupon);
			}
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", this.getText("保存成功"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", this.getText("ajax.import.failure"));
			return resultMap;
		}
	}
	
	/**
	 * 自动派送优惠券
	 * @param coupon
	 * @param levelList
	 * @param userInfoList
	 * @return
	 */
	public MsgModel<List<UserCoupon>> autoSendCoupon(Coupon coupon, List<Integer> levelList, List<UserInfo> userInfoList,int number) {
		MsgModel<List<UserCoupon>> msgModel = new MsgModel<List<UserCoupon>> ();
		try{
			if (coupon != null && coupon.getCouponId() != null) {
				
				//优先按级别处理
				if (levelList != null && levelList.size() > 0) {
					userInfoList = new ArrayList<UserInfo> ();
					List<UserInfo> userList = this.userInfoManager.getUserInfoListByLevelList(levelList);
					if(userList != null && userList.size() > 0) {
						userInfoList.addAll(userList);
					}
				}
				
				if (userInfoList != null && userInfoList.size() > 0) {
					String receiveTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, new Date());
					String effectiveTime = DateUtil.getNearlyDate(StringUtil.nullToInteger(coupon.getEffectiveTime()));
					List<UserCoupon> userCouponList = new ArrayList<>();
					Set<Long> userIdSet = new HashSet<Long>();
					Date now = DateUtil.getCurrentDate();
					
					// 检查是否已发放优惠券用户信息
					final Long couponId = coupon.getCouponId();
					number = number * StringUtil.nullToInteger(coupon.getTotalCount());
					
					for (int i = 0; i < number; i++) {
						List<Long> userInfoIdList = new ArrayList<>();
						for (UserInfo userInfo : userInfoList) {

							UserCoupon userCoupon = new UserCoupon();
							userCoupon.setCouponId(couponId);
							userCoupon.setIsGiftCoupon(StringUtil.nullToBoolean(coupon.getIsGiftCoupon()));
							userCoupon.setCouponNo(CoreUtil.getUUID());
							userCoupon.setCouponStatus(UserCouponStatus.USER_COUPON_STATUS_NOT_USED);
							userCoupon.setCreateTime(now);
							userCoupon.setUpdateTime(now);
							userCoupon.setReceiveTime(receiveTime);
							userCoupon.setEffectiveTime(effectiveTime);
							userCoupon.setUserId(userInfo.getUserId());
							if (!userInfoIdList.contains(userInfo.getUserId())) {
								userInfoIdList.add(userInfo.getUserId());
								userIdSet.add(userInfo.getUserId());
								userCouponList.add(userCoupon);
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}

	
	/**
	 * 设置任务优惠券状态(0.禁用，1.启用)
	 * 
	 * @param request
	 * @return
	 */
	
	@RequestMapping(value = "/setTaskStatus")
	public @ResponseBody Map<String, Object> setTaskStatus(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long taskId = StringUtil.nullToLong(request.getParameter("taskId"));
		Integer status = StringUtil.nullToInteger(request.getParameter("status"));
		try {
			CouponTask couponTask = this.couponTaskManager.get(taskId);
		    if(couponTask != null && couponTask.getTaskId() != null) {
		    	couponTask.setTaskStatus(status);
		    	couponTask.setUpdateTime(new Date());  	
		    }

	    	this.couponTaskManager.update(couponTask);
		    resultMap.put("success", true);
			resultMap.put("message", getText("submit.success"));
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", true);
		resultMap.put("message", getText("submit.failure"));
		return resultMap;
	}

	/**
	 * 得到优惠券详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getCouponById")
	public @ResponseBody Map<String, Object> getCouponById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long couponId = StringUtil.nullToLong(request.getParameter("couponId"));
		try {
			Coupon coupon = this.couponManager.get(couponId);
            if(coupon != null &&  coupon.getCouponId() != null) {
            	resultMap.put("success", true);
    			resultMap.put("data", coupon);
            }
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		return resultMap;
	}
	
	@RequestMapping(value = "/setCouponTimeOut")
	public @ResponseBody Map<String, Object> setCouponTimeOut(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long couponId = StringUtil.nullToLong(request.getParameter("couponId"));
		try{
			Coupon coupon = this.couponManager.get(couponId);
			if(coupon != null && coupon.getCouponId() != null){		    
				this.couponManager.setCouponTimeout(coupon);
				
				resultMap.put("success", true);
				resultMap.put("message", getText("submit.success"));
				return resultMap;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("message", getText("submit.failure"));
		return resultMap;
	}
	
	@RequestMapping(value = "/setCouponEnabled")
	public @ResponseBody Map<String, Object> setCouponEnabled(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long couponId = StringUtil.nullToLong(request.getParameter("couponId"));
		try {
			Coupon coupon = this.couponManager.get(couponId);
			if (coupon != null && coupon.getCouponId() != null) {
				coupon.setIsEnable(true);
				coupon.setUpdateTime(new Date());
				this.couponManager.update(coupon);

				// 直接清除缓存
				Constants.COUPON_MAP.put(coupon.getCouponId(), coupon);

			} else {
				resultMap.put("success", false);
				resultMap.put("message", getText("submit.failure"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("success", true);
		resultMap.put("message", getText("submit.success"));
		return resultMap;
	}
	
	
	@RequestMapping(value = "/setGiftCoupon")
	public @ResponseBody Map<String, Object> setGiftCoupon(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long couponId = StringUtil.nullToLong(request.getParameter("couponId"));
		try {
			Coupon coupon = this.couponManager.get(couponId);
			if (coupon != null && coupon.getCouponId() != null) {
				coupon.setIsGiftCoupon(true);
				coupon.setUpdateTime(new Date());
				this.couponManager.save(coupon);

			} else {
				resultMap.put("success", false);
				resultMap.put("message", getText("submit.failure"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("success", true);
		resultMap.put("message", getText("submit.success"));
		return resultMap;
	}
	
	
	@RequestMapping(value = "/sendCouponToVIP1")
	public @ResponseBody Map<String, Object> sendCouponToVIP1(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long couponId = StringUtil.nullToLong(request.getParameter("couponId"));
		String taskName = StringUtil.null2Str(request.getParameter("taskName"));
		String beginTime = StringUtil.null2Str(request.getParameter("beginTime"));
		String endTime = StringUtil.null2Str(request.getParameter("endTime"));
		try{
			if(!DateUtil.isEffectiveTime(DateUtil.dateFormat, StringUtil.null2Str(beginTime))
					|| !DateUtil.isEffectiveTime(DateUtil.dateFormat, StringUtil.null2Str(endTime))
					|| beginTime.compareTo(endTime) > 0){
				resultMap.put("success", true);
				resultMap.put("message", getText("请选择有效时间"));
				return resultMap;
			}
			Date beginDate = DateUtil.parseDate(DateUtil.DATE_FORMAT, beginTime);
			Date endDate = DateUtil.parseDate(DateUtil.DATE_FORMAT, endTime);
			
			Coupon coupon = this.couponManager.get(couponId);
			if(coupon == null || coupon.getCouponId() == null) {
				resultMap.put("success", true);
				resultMap.put("message", getText("请输入正确的优惠券ID"));
				return resultMap;
			}	
			
			// 优惠券任务
			CouponTask couponTask = new CouponTask();
			couponTask.setTaskName(taskName);
			couponTask.setCouponId(couponId);
			couponTask.setTaskStatus(CouponTask.TASK_STATUS_ON);
			couponTask.setCreateTime(DateUtil.getCurrentDate());
			couponTask.setUpdateTime(couponTask.getCreateTime());
			
			couponTask = this.couponTaskManager.save(couponTask);
			//得到时间段内购买成功的vip记录
			List<UserInviteRecord> inviteRecordList = this.userInviteRecordManager.getUserInviteRecordListByCreateTime(beginDate, endDate);
			if(inviteRecordList == null || inviteRecordList.size() <=0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("此时间段内没有用户晋级为vip1"));
				return resultMap;
			}else {
				List<Long> userIdList = new ArrayList<Long>();
				for(UserInviteRecord record : inviteRecordList) {
					userIdList.add(StringUtil.nullToLong(record.getUserId()));
				}
				List<UserInfo> userInfoList = this.userInfoManager.getByIdList(userIdList);
				MsgModel<List<UserCoupon>> msgModel = this.autoSendCoupon(coupon, null, userInfoList,1);
				if(!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", msgModel.getMessage());
					return resultMap;
				}
				resultMap.put("success", true);
				resultMap.put("error", false);
				resultMap.put("message", getText("submit.success"));
				return resultMap;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("发送失败"));
		return resultMap;
	}
	
	/**
	 * 使用优惠券相关订单
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/exportCouponOrderExcel")
	public @ResponseBody void exportAbnormalOrderExcel(final HttpServletRequest request, final HttpServletResponse response)
			throws UnsupportedEncodingException {
		try {
			Long couponId = StringUtil.nullToLong(request.getParameter("couponId"));
			String status = request.getParameter("status");
			Coupon coupon = this.couponManager.get(couponId);
			if(coupon != null && coupon.getCouponId() != null) {
				List<Object[]> dataList = OrderUtil.orderReport(null, null, null, status,coupon.getCouponId());
				MyExcelExport myExcel = new MyExcelExport("OrderReport", Constants.ORDER_REPORT_COLUMN_NAME, dataList, response);	
			    myExcel.export();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 批量删除
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteUserCoupon")
	public @ResponseBody Map<String, Object> deleteUserCoupon(@RequestParam(value = "idListGridJson") String record,
			final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			List<Long> idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			this.userCouponManager.updateUserCouponStatus(UserCoupon.USER_COUPON_STATUC_DELETE,idList);
			resultMap.put("success", true);
			resultMap.put("message", "禁用成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("message", "禁用失败");
		return resultMap;
	}
	

	/**
	 * 统计发送数量
	 * @param userCouponList
	 * @return
	 */
	private Map<Long,Integer> getSendCount(List<UserCoupon> userCouponList){
		Map<Long, Integer> countMap = new HashMap<Long, Integer>();
		if (!CollectionUtils.isEmpty(userCouponList)) {
			for (UserCoupon userCoupon : userCouponList) {
				if (!countMap.containsKey(userCoupon.getCouponId())) {
					countMap.put(userCoupon.getCouponId(), 1);
				} else {
					countMap.put(userCoupon.getCouponId(), countMap.get(userCoupon.getCouponId()) + 1);
				}
			}
		}
		return countMap;
	}
	
	
	@RequestMapping(value = "/exportCoupon")
	public @ResponseBody void exportCoupon(final HttpServletRequest request, final HttpServletResponse response)
			throws UnsupportedEncodingException {
		String beginTime = request.getParameter("beginTime");
		String endTime = request.getParameter("endTime");
		Long couponId  = StringUtil.nullToLong(request.getParameter("couponId"));
		try {
			
			StringBuilder sqlBul = new StringBuilder();
			sqlBul.append("SELECT juc.`coupon_id` '优惠券id',jc.coupon_name '优惠券名称', ");
			sqlBul.append("juc.`user_id` '用户id', ");
			sqlBul.append("(SELECT jui.mobile FROM jkd_user_info jui WHERE jui.user_id = juc.`user_id`) '手机号码', ");
			sqlBul.append("CASE WHEN juc.coupon_status = -1 THEN '被占用' WHEN juc.coupon_status = 0 THEN '未使用' WHEN ");
			sqlBul.append("juc.coupon_status = 1 THEN '已使用' WHEN juc.coupon_status = 2 THEN '已过期' END AS '优惠券状态', ");
			sqlBul.append("juc.receive_time '领取时间',case when juc.is_gift_coupon = true then '是' else '否' end as '是否赠品' ");
			sqlBul.append("FROM jkd_user_coupon juc inner join jkd_coupon jc on juc.coupon_id = jc.coupon_id WHERE juc.create_time between '%s' and '%s' ");
			if(couponId != 0) {
				sqlBul.append("and juc.coupon_id  ="+couponId);
			}
			
			List<Object[]> objectList = this.couponManager.querySql(String.format(sqlBul.toString(), beginTime,endTime));
			
			MyExcelExport myExcel = new MyExcelExport("CouponReport", Constants.COUPON_REPORT_COLUMN_NAME, objectList, response);
			myExcel.export();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

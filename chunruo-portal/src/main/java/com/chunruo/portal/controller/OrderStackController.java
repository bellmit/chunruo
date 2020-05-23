package com.chunruo.portal.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.model.Area;
import com.chunruo.core.model.OrderStack;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.model.UserAddress;
import com.chunruo.core.model.UserCart;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.OrderStackManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.ProductSpecManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.XlsParserUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.ProductCheckUtil;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.util.UserAddressUtil;

/**
 * 批量导单堆栈
 * @author chunruo
 *
 */
@Controller
@RequestMapping("/api/orderStack/")
public class OrderStackController extends BaseController{
	/** Excel 每次最大导单数量 */
	public static final Integer BATCH_IMPORT_ORDER_SIZE = 50;
	/** 防止用户多次提交的令牌 */
	public static final String TOKEN = "ORDER_IMPORT_TOKEN";
	@Autowired
	private ProductManager productManager;
	@Autowired
	private ProductSpecManager productSpecManager;
	@Autowired
	private OrderStackManager orderStackManager;

	/**
	 * 更新批量导单地址信息
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/updateOrderStack")
	public @ResponseBody Map<String, Object> updateOrderStack(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long orderStackId = StringUtil.nullToLong(request.getParameter("orderStackId"));
		Long provinceId = StringUtil.nullToLong(request.getParameter("provinceId"));
		Long cityId = StringUtil.nullToLong(request.getParameter("cityId"));
		Long areaId = StringUtil.nullToLong(request.getParameter("areaId"));
		String address = StringUtil.null2Str(request.getParameter("address"));
		String consignee = StringUtil.null2Str(request.getParameter("name"));
		String consigneePhone = StringUtil.null2Str(request.getParameter("mobile"));
		String identityName = StringUtil.null2Str(request.getParameter("realName"));
		String identityNo = StringUtil.null2Str(request.getParameter("identityNo"));

		UserInfo userInfo = PortalUtil.getCurrentUserInfo(request, UserInfo.LOGIN_TYPE_PC);
		try{
			OrderStack orderStack = this.orderStackManager.get(orderStackId);
			if(orderStack == null || orderStack.getOrderStackId() == null){
				//订单不存在
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单不存在");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(!StringUtil.compareObject(orderStack.getUserId(), userInfo.getUserId())){
				//订单无权限操作
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单无权限操作");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			orderStack = this.orderStackManager.detach(orderStack);
			orderStack.setProvinceId(provinceId);
			orderStack.setCityId(cityId);
			orderStack.setAreaId(areaId);
			orderStack.setAddress(address);
			orderStack.setConsignee(consignee);
			orderStack.setConsigneePhone(consigneePhone);
			orderStack.setIdentityName(identityName);

			// 身份证格式化特殊处理
			if(StringUtil.isValidIdentityCardNO(orderStack.getIdentityNo())) {
				if(!StringUtil.compareObject(StringUtil.identityNoFormat(orderStack.getIdentityNo()), identityNo)) {
					orderStack.setIdentityNo(identityNo);
				}
			}else {
				orderStack.setIdentityNo(identityNo);
			}

			// 通过商品信息找出productType
			int productType = GoodsType.GOODS_TYPE_COMMON;
			List<OrderStack> orderStackList = new ArrayList<OrderStack> ();
			orderStackList.add(orderStack);
			List<UserCart> list = OrderStackController.getMergeUserCartList(orderStackList, userInfo);
			if(list != null && list.size() > 0) {
				for(UserCart userCart : list) {
					if(!StringUtil.compareObject(userCart.getProductType(), GoodsType.GOODS_TYPE_COMMON)) {
						productType = GoodsType.GOODS_TYPE_CROSS;
					}
				}
			}

			//检查用户地址信息是否有效
			UserAddress userAddress = this.getUserAddress(orderStack);
			MsgModel<UserAddress> xsgModel = UserAddressUtil.checkIsValidUserAddress( userAddress);
			if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(xsgModel.getMessage()));
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 更新批量订单地址信息
			if(StringUtil.isNull(orderStack.getGroupKey())) {
				orderStack.setUpdateTime(DateUtil.getCurrentDate());
				this.orderStackManager.save(orderStack);
			}else {
				// 通过订单组合ID批量修改
				List<OrderStack> stackList = this.orderStackManager.getOrderStackListByGroupKey(orderStack.getGroupKey());
				if(stackList != null && stackList.size() > 0) {
					for(OrderStack stack : stackList) {
						stack.setProvinceId(provinceId);
						stack.setCityId(cityId);
						stack.setAreaId(areaId);
						stack.setAddress(address);
						stack.setConsignee(consignee);
						stack.setConsigneePhone(consigneePhone);
						stack.setIdentityName(identityName);
						stack.setIdentityNo(orderStack.getIdentityNo());
						stack.setUpdateTime(DateUtil.getCurrentDate());
					}
					this.orderStackManager.batchInsert(stackList, stackList.size());
				}
			}

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "保存成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "请求失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * 删除记录
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/deleteOrderStack")
	public @ResponseBody Map<String, Object> deleteOrderStack(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long orderStackId = StringUtil.nullToLong(request.getParameter("orderStackId"));
		UserInfo userInfo = PortalUtil.getCurrentUserInfo(request, UserInfo.LOGIN_TYPE_PC);
		try{
			OrderStack orderStack = this.orderStackManager.get(orderStackId);
			if(orderStack == null || orderStack.getOrderStackId() == null){
				//订单不存在
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单不存在");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(!StringUtil.compareObject(orderStack.getUserId(), userInfo.getUserId())){
				//订单无权限操作
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单无权限操作");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			this.orderStackManager.deleteAllByOrderStackId(orderStack.getOrderStackId());
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "删除成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "请求失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * 订单批量导入校验页
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@PostMapping(value = "/orderImport")
	public @ResponseBody Map<String, Object> orderImport(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<OrderStack> orderStackList = new ArrayList<OrderStack> ();
		UserInfo userInfo = PortalUtil.getCurrentUserInfo(request, UserInfo.LOGIN_TYPE_PC);
		String filePath = "";
		try {
			// 1.将Excel文件写入本地临时路径
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<String> iter = multiRequest.getFileNames();
				if (iter.hasNext()) {
					MultipartFile file = multiRequest.getFile(iter.next());
					if (!file.isEmpty()) {
						String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1); // 文件扩展名
						String fileName = StringUtil.null2Str(DateUtil.getCurrentTime()) + "." + fileExt; // 重命名文件
						filePath = String.format("%s/depository/tempFile/%s", Constants.EXTERNAL_IMAGE_PATH, fileName);
						FileUtil.checkDirExists(new File(filePath).getParent());
						FileUploadUtil.copyFile(file.getInputStream(), filePath);
					}
				}
			}

			// 检查上传文件是否存在
			if(!FileUtil.checkFileExists(filePath)) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "文件上传失败,请求联系客服");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 检查xls头标题信息配置
			int startRowNum = 0;
			MsgModel<Integer> msgModel = this.checkXlsHeader(filePath, startRowNum);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
				startRowNum = 1;
				msgModel = this.checkXlsHeader(filePath, startRowNum);
				if(!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, msgModel.getMessage());
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}

			// xls数据解析
			List<Map<String, String>> list = XlsParserUtil.read(filePath, startRowNum);
			if(list != null && list.size() > 0) {
				for(Map<String, String> map : list) {
					OrderStack orderStack = new OrderStack ();
					orderStack.setGroupKey(StringUtil.null2Str(map.get("订单号")));
					orderStack.setConsignee(StringUtil.null2Str(map.get("收货人姓名")).replaceAll("\\s+", ""));
					orderStack.setConsigneePhone(StringUtil.null2Str(map.get("收货人手机号")));
					orderStack.setProvinceName(StringUtil.null2Str(map.get("省")));
					orderStack.setCityName(StringUtil.null2Str(map.get("市")));
					orderStack.setAreaName(StringUtil.null2Str(map.get("区")));
					orderStack.setAddress(StringUtil.null2Str(map.get("详细地址")));
					orderStack.setIdentityNo(StringUtil.null2Str(map.get("收货人身份证号")).replaceAll("\\s+", ""));
					orderStack.setIdentityName(StringUtil.null2Str(map.get("身份证姓名")).replaceAll("\\s+", ""));
					orderStack.setProductCode(StringUtil.null2Str(map.get("商品货号")));
					orderStack.setQuantity(StringUtil.nullToInteger(map.get("商品数量")));
					
					// 检查数据是否为空
					if(StringUtil.isNull(orderStack.getConsignee()) 
							&& StringUtil.isNull(orderStack.getConsigneePhone())
							&& StringUtil.isNull(orderStack.getProvinceName())
							&& StringUtil.isNull(orderStack.getCityName())
							&& StringUtil.isNull(orderStack.getAreaName())
							&& StringUtil.isNull(orderStack.getProductCode())) {
						continue;
					}
					orderStackList.add(orderStack);
				}
			}

			// 检查导入文件数量
			if(orderStackList != null && orderStackList.size() > OrderStackController.BATCH_IMPORT_ORDER_SIZE) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "每次最多只能导入\""+ OrderStackController.BATCH_IMPORT_ORDER_SIZE +"\"个订单,请修Excel改后再次上传");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 校验导入订单的每一项是否有效
			MsgModel<List<OrderStack>> xsgModel = this.checkXlsData(orderStackList, userInfo);
			resultMap.put("list", xsgModel.getData());
			resultMap.put("token", this.getToken(request));
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "请求成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e) {
			e.printStackTrace();

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
			resultMap.put(PortalConstants.MSG, "上传文件解析异常," + e.getMessage());
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}
	}

	/**
	 * 订单批量导入
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@PostMapping(value = "/orderBatch")
	public @ResponseBody Map<String, Object> orderBatch(@RequestBody List<OrderStack> orderStackList, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		UserInfo userInfo = PortalUtil.getCurrentUserInfo(request, UserInfo.LOGIN_TYPE_PC);
		String token = StringUtil.null2Str(request.getParameter("token"));

		// 分布式锁
		RedissonClient redissonClient = Constants.ctx.getBean(RedissonClient.class);
		RLock lock = redissonClient.getLock("lock_order_batch");
		lock.lock();
		try {
			// 检查请求参数是否为空
			if(orderStackList == null || orderStackList.isEmpty()) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "请上传有效订单数据");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 校验令牌,防止多次提交
			MsgModel<Void> tokenModel = this.checkToken(token, request);
			if(!StringUtil.nullToBoolean(tokenModel.getIsSucc())) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, tokenModel.getMessage());
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 检查批量导单数据是否有效
			MsgModel<List<OrderStack>> xsgModel = this.checkXlsData(orderStackList, userInfo);
			if(StringUtil.nullToBoolean(xsgModel.getIsSucc())) {
				int index = 0;
				orderStackList = xsgModel.getData();
				for(OrderStack orderStack : orderStackList) {
					++index;
					if(StringUtil.nullToBoolean(orderStack.getIsError())) {
						resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
						resultMap.put(PortalConstants.MSG, String.format("第%s行,%s", index, StringUtil.null2Str(orderStack.getMessage())));
						resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
						return resultMap;
					}
				}
			}

			// 检查订单是否同订单多商品合并
			Map<String, String> sameBatchMap = new HashMap<String, String> ();
			for(OrderStack orderStack : orderStackList) {
				// 检查是否同订单设置唯一标识
				if(!StringUtil.isNull(orderStack.getGroupKey())) {
					if(sameBatchMap.containsKey(orderStack.getGroupKey())) {
						orderStack.setGroupKey(sameBatchMap.get(orderStack.getGroupKey()));
					}else {
						String randomNo = CoreInitUtil.getRandomNo();
						sameBatchMap.put(orderStack.getGroupKey(), randomNo);
						orderStack.setGroupKey(randomNo);
					}
				}

				orderStack.setOrderNo(CoreInitUtil.getRandomStackNo());
				orderStack.setUserId(userInfo.getUserId());
				orderStack.setCreateTime(DateUtil.getCurrentDate());
				orderStack.setUpdateTime(orderStack.getCreateTime());
			}
			this.orderStackManager.batchInsert(orderStackList, orderStackList.size());

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "请求成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			// 释放锁
			lock.unlock();     
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "请求服务器失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * 默认文件下载
	 * @param path
	 * @param response
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/download")
	public void download(HttpServletRequest request, HttpServletResponse response) {
		InputStream inStream = null;
		try {
			String filename = StringUtil.null2Str("导入数据格式模板.xlsx");	// 文件名
			if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
				filename = URLEncoder.encode(filename, "UTF-8");
			} else {
				filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
			}

			// 以流的形式下载文件。
			String filePath = StringUtil.null2Str(Constants.conf.getProperty("jkd.order.stack.tempalt.path"));
			inStream = new FileInputStream(new File(filePath));
			response.setHeader("Content-Length", String.valueOf(inStream.available()));
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-disposition", "attachment;filename=" + filename);
			OutputStream ouputStream = response.getOutputStream();
			// 循环取出流中的数据
			byte[] b = new byte[1024];
			int len;
			while ((len = inStream.read(b)) > 0) {
				ouputStream.write(b, 0, len);
			}
			inStream.close();
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 检查导入订单参数是否有效
	 * @param orderStackList
	 * @return
	 */
	private MsgModel<List<OrderStack>> checkXlsData(List<OrderStack> orderStackList, UserInfo userInfo){
		MsgModel<List<OrderStack>> msgModel = new MsgModel<List<OrderStack>> ();
		boolean isAllExistError = false;
		try {
			if(orderStackList != null && orderStackList.size() > 0) {
				for(OrderStack orderStack : orderStackList) {
					boolean isExistError = false;
					StringBuffer errorBuffer = new StringBuffer ();
					
					// 检查收货人姓名
					if(StringUtil.isNull(orderStack.getConsignee())) {
						isExistError = true;
						errorBuffer.append("收货人姓名|");
					}else {
						//检查收货人姓名是否包敏感词
						MsgModel<String> xmsgModel = UserAddressUtil.isContaintSensitiveWord(StringUtil.null2Str(orderStack.getConsignee()));
						if(StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
							isExistError = true;
							errorBuffer.append("收货人姓名|");
						}
					}

					// 检查收货人手机号
					if(!StringUtil.isValidateMobile(orderStack.getConsigneePhone())) {
						isExistError = true;
						errorBuffer.append("收货人手机号|");
					}

					// 检查省
					if(StringUtil.isNull(orderStack.getProvinceName())) {
						isExistError = true;
						errorBuffer.append("省|");
					}else {
						boolean isExistProvince = false;
						if(Constants.PROVINCE_AREA_LIST != null && Constants.PROVINCE_AREA_LIST.size() > 0) {
							for(Area province : Constants.PROVINCE_AREA_LIST) {
								if(StringUtil.compareObject(orderStack.getProvinceName(), province.getAreaName())) {
									orderStack.setProvinceId(province.getAreaId());
									isExistProvince = true;
								}
							}
						}

						// 检查是否存在省
						if(!StringUtil.nullToBoolean(isExistProvince)) {
							isExistError = true;
							errorBuffer.append("省|");
						}
					}

					// 检查市
					if(orderStack.getProvinceId() != null) {
						if(StringUtil.isNull(orderStack.getCityName())) {
							isExistError = true;
							errorBuffer.append("市|");
						}else {
							boolean isExistCity = false;
							if(Constants.CITY_ARE_AMAP!= null 
									&& Constants.CITY_ARE_AMAP.size() > 0
									&& Constants.CITY_ARE_AMAP.containsKey(orderStack.getProvinceId())) {
								for(Area city : Constants.CITY_ARE_AMAP.get(orderStack.getProvinceId())) {
									if(StringUtil.compareObject(orderStack.getCityName(), city.getAreaName())) {
										orderStack.setCityId(city.getAreaId());
										isExistCity = true;
									}
								}
							}

							// 检查是否存在省
							if(!StringUtil.nullToBoolean(isExistCity)) {
								isExistError = true;
								errorBuffer.append("市|");
							}
						}
					}

					// 检查区
					if(orderStack.getCityId() != null) {
						if(StringUtil.isNull(orderStack.getAreaName())) {
							isExistError = true;
							errorBuffer.append("区|");
						}else {
							boolean isExistArea = false;
							if(Constants.COUNTRY_AREA_MAP!= null 
									&& Constants.COUNTRY_AREA_MAP.size() > 0
									&& Constants.COUNTRY_AREA_MAP.containsKey(orderStack.getCityId())) {
								for(Area area : Constants.COUNTRY_AREA_MAP.get(orderStack.getCityId())) {
									if(StringUtil.compareObject(orderStack.getAreaName(), area.getAreaName())) {
										orderStack.setAreaId(area.getAreaId());
										isExistArea = true;
									}
								}
							}

							// 检查是否存在省
							if(!StringUtil.nullToBoolean(isExistArea)) {
								isExistError = true;
								errorBuffer.append("区|");
							}
						}
					}

					// 检查详细地址
					if(StringUtil.isNull(orderStack.getAddress())) {
						isExistError = true;
						errorBuffer.append("详细地址|");
					}

					// 检查商品货号
					if(StringUtil.isNull(orderStack.getProductCode())) {
						isExistError = true;
						errorBuffer.append("商品货号|");
					}else {
						Product product = null;
						if(StringUtil.null2Str(orderStack.getProductCode()).startsWith("JKD_PP_")) {
							// 检查商品是否存在
							Long productId = StringUtil.nullToLong(StringUtil.null2Str(orderStack.getProductCode()).replaceFirst("JKD_PP_", ""));
							product = this.productManager.get(productId);
							if(product == null 
									|| product.getProductId() == null
									|| StringUtil.nullToBoolean(product.getIsSpceProduct())) {
								isExistError = true;
								errorBuffer.append("商品货号不存在|");
							}else {
								orderStack.setProductId(product.getProductId());
							}
						}else if(StringUtil.null2Str(orderStack.getProductCode()).startsWith("JKD_PGN_")) {
							// 检查组合商品是否存在
							boolean isCheckSucc = false;
							String groupInfo = StringUtil.null2Str(orderStack.getProductCode()).replaceFirst("JKD_PGN_", "");
							String[] groupArrays = groupInfo.split("_");
							if(groupArrays != null && groupArrays.length > 1) {
								Long productId = StringUtil.nullToLong(groupArrays[0]);
								String groupProductInfo = StringUtil.null2Str(groupArrays[1]);

								MsgModel<Product> psgModel = ProductUtil.getProductByUserLevel(productId, null, userInfo, false);
								if(!StringUtil.nullToBoolean(psgModel.getIsSucc())) {
									isCheckSucc = true;
									isExistError = true;
									errorBuffer.append("组合商品" + psgModel.getMessage() + "|");
								}else {
									product = psgModel.getData();
									if(product != null 
											&& product.getProductId() != null
											&& StringUtil.nullToBoolean(product.getIsGroupProduct())) {
										isCheckSucc = true;
										MsgModel<Product> xmsgModel = ProductCheckUtil.checkGroupProductByUserLevel(product, groupProductInfo, 1, false, userInfo);
										if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
											isExistError = true;
											errorBuffer.append("组合商品" + xmsgModel.getMessage() + "|");
										}

										orderStack.setProductId(product.getProductId());
										orderStack.setGroupProductInfo(groupProductInfo);
									}
								}
							}

							// 检查组合商品是否有效
							if(!isCheckSucc) {
								isExistError = true;
								errorBuffer.append("组合商品已下架或不存在|");
							}
						}else if(StringUtil.null2Str(orderStack.getProductCode()).startsWith("JKD_PSC_")) {
							// 检查商品是否存在
							Long productSpecId = StringUtil.nullToLong(StringUtil.null2Str(orderStack.getProductCode()).replaceFirst("JKD_PSC_", ""));
							ProductSpec productSpec = this.productSpecManager.get(productSpecId);
							if(productSpec == null 
									|| productSpec.getProductId() == null
									|| productSpec.getProductSpecId() == null) {
								isExistError = true;
								errorBuffer.append("商品货号不存在|");
							}else {
								// 检查商品是否存在
								product = this.productManager.get(productSpec.getProductId());
								if(product == null 
										|| product.getProductId() == null
										|| !StringUtil.nullToBoolean(product.getIsSpceProduct())) {
									isExistError = true;
									errorBuffer.append("商品货号不存在|");
								}

								orderStack.setProductId(productSpec.getProductId());
								orderStack.setProductSpecId(productSpec.getProductSpecId());
							}
						}else{
							isExistError = true;
							errorBuffer.append("商品货号|");
						}

						// 检查商品是否身份证信息
						if(product != null && product.getProductId() != null) {
							ProductWarehouse warehouse = ProductUtil.getProductWarehouse(product.getWareHouseId());
							if(warehouse == null || warehouse.getWarehouseId() == null) {
								isExistError = true;
								errorBuffer.append("商品库存配置错误|");
							}else {
								// 检查是否需要身份证信息
								List<Integer> productTypeList = new ArrayList<Integer> ();
								productTypeList.add(GoodsType.GOODS_TYPE_DIRECT);  //直邮
								productTypeList.add(GoodsType.GOODS_TYPE_CROSS);   //跨境
								orderStack.setIsNeedIdentity(false);
								if(productTypeList.contains(warehouse.getProductType())) {
									orderStack.setIsNeedIdentity(true);
								}

								//检查用户地址信息是否有效
								UserAddress userAddress = this.getUserAddress(orderStack);
								MsgModel<UserAddress> xsgModel = UserAddressUtil.checkIsValidUserAddress( userAddress);
								if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
									isExistError = true;
									errorBuffer.append(xsgModel.getMessage() + "|");
								}
							}
						}
					}

					// 检查商品数量
					if(StringUtil.nullToInteger(orderStack.getQuantity()) <= 0) {
						isExistError = true;
						errorBuffer.append("商品数量|");
					}

					// 有错误信息
					if(StringUtil.nullToBoolean(isExistError)) {
						isAllExistError = true;
						orderStack.setIsError(true);
						orderStack.setMessage(errorBuffer.toString() + "以上参数错误");
					}
				}
			}

			// 检查订单商品库存限购信息(及多商品订单合并校验)
			this.checkOrderStackProduct(orderStackList, userInfo);
		}catch(Exception e) {
			e.printStackTrace();
		}

		msgModel.setData(orderStackList);
		msgModel.setIsSucc(isAllExistError);
		return msgModel;
	}

	/**
	 * 换算成UserAddress对象
	 * @param orderStack
	 * @return
	 */
	private UserAddress getUserAddress(OrderStack orderStack) {
		// 检查地址信息
		UserAddress userAddress = new UserAddress ();
		userAddress.setAddressId(-1L);
		userAddress.setProvinceId(orderStack.getProvinceId());
		userAddress.setProvinceName(orderStack.getProvinceName());
		userAddress.setCityId(orderStack.getCityId());
		userAddress.setCityName(orderStack.getCityName());
		userAddress.setAreaId(orderStack.getAreaId());
		userAddress.setAreaName(orderStack.getAreaName());
		userAddress.setAddress(orderStack.getAddress());
		userAddress.setName(orderStack.getConsignee());
		userAddress.setMobile(orderStack.getConsigneePhone());
		userAddress.setIdentityNo(orderStack.getIdentityNo());
		userAddress.setRealName(orderStack.getIdentityName());
		return userAddress;
	}

	/**
	 * 检查订单商品库存限购信息
	 * 多商品订单合并校验
	 * @param orderStackList
	 * @return
	 */
	private void checkOrderStackProduct(List<OrderStack> orderStackList, UserInfo userInfo){
		try {
			if(orderStackList != null && orderStackList.size() > 0) {
				List<OrderStack> mergeOrderList = OrderStackController.getMergeOrderList(orderStackList, userInfo);
				if(mergeOrderList != null && mergeOrderList.size() > 0) {
					for(OrderStack orderStack : mergeOrderList) {
						MsgModel<List<Product>> msgModel = ProductCheckUtil.checkProductList(orderStack.getUserCartList(), userInfo, true);
						if(!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
							orderStack.setIsError(true);
							if(StringUtil.isNull(orderStack.getMessage())) {
								orderStack.setMessage(StringUtil.null2Str(msgModel.getMessage()));
							}else {
								orderStack.setMessage(String.format("%s|%s", StringUtil.null2Str(orderStack.getMessage()), StringUtil.null2Str(msgModel.getMessage())));
							}
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查Xls头文件信息配置
	 * @param filePath
	 * @param startRowNum
	 * @return
	 */
	private MsgModel<Integer> checkXlsHeader(String filePath, int startRowNum){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		try {
			List<String> list = XlsParserUtil.readHeader(filePath, startRowNum);
			if(list != null 
					&& list.size() > 0
					&& list.contains("收货人姓名")
					&& list.contains("收货人手机号")
					&& list.contains("省")
					&& list.contains("市")
					&& list.contains("区")
					&& list.contains("详细地址")
					&& list.contains("商品货号")
					&& list.contains("商品数量")) {
				msgModel.setData(startRowNum);
				msgModel.setIsSucc(true);
				return msgModel;
			}else {
				msgModel.setMessage("xls头标题信息配置错误");
				msgModel.setIsSucc(false);
				return msgModel;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		msgModel.setMessage("xls文件解析错误");
		msgModel.setIsSucc(false);
		return msgModel;
	}

	/**
	 * 获取token
	 * @return
	 */
	private String getToken(HttpServletRequest request) {
		String token = CoreUtil.getUUID();
		request.getSession().setAttribute(OrderStackController.TOKEN, token);
		return token;
	}

	/**
	 * 校验token
	 * @param requestToken
	 * @param request
	 * @return
	 */
	private MsgModel<Void> checkToken(String requestToken, HttpServletRequest request) {
		MsgModel<Void> msgModel = new MsgModel<Void>();
		try {
			String seesionToken = StringUtil.null2Str(request.getSession().getAttribute(OrderStackController.TOKEN));
			request.getSession().removeAttribute(OrderStackController.TOKEN);
			if(StringUtil.isNull(requestToken)) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("token不存在,请重新上传excel");
				return msgModel;
			}
			if(!StringUtil.compareObject(requestToken, seesionToken)) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("请不要重复提交");
				return msgModel;
			}else {
				msgModel.setIsSucc(true);
				return msgModel;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("校验token失败");
		return msgModel;
	}

	/**
	 * 订单合并
	 * @param orderStackList
	 * @param userInfo
	 * @return
	 */
	public static List<OrderStack> getMergeOrderList(List<OrderStack> orderStackList, UserInfo userInfo){
		List<OrderStack> mergeOrderList = new ArrayList<OrderStack> ();
		try {
			if(orderStackList != null && orderStackList.size() > 0) {
				// 订单多商品合并
				List<Long> orderStackIdList = new ArrayList<Long> ();
				Map<String, List<OrderStack>> orderStackMap = new HashMap<String, List<OrderStack>> ();
				for(OrderStack orderStack : orderStackList){
					String groupKey = StringUtil.null2Str(orderStack.getGroupKey());
					if(!StringUtil.isNull(groupKey)) {
						if(orderStackMap.containsKey(groupKey)) {
							orderStackMap.get(groupKey).add(orderStack);
						}else {
							List<OrderStack> list = new ArrayList<OrderStack> ();
							list.add(orderStack);
							orderStackMap.put(groupKey, list);
							orderStackIdList.add(orderStack.getOrderStackId());
						}
					}else {
						orderStackIdList.add(orderStack.getOrderStackId());
					}
				}

				for(OrderStack orderStack : orderStackList){
					//  检查组合多商品是否存在
					if(!orderStackIdList.contains(orderStack.getOrderStackId())) {
						continue;
					}

					// 判断是否多商品订单
					List<OrderStack> list = new ArrayList<OrderStack> ();
					String groupKey = StringUtil.null2Str(orderStack.getGroupKey());
					if(!StringUtil.isNull(groupKey)) {
						list.addAll(orderStackMap.get(groupKey));
					}else {
						list.add(orderStack);
					}

					// 批量导入订单数据合并成购物车商品
					List<UserCart> userCartList = OrderStackController.getMergeUserCartList(list, userInfo);
					orderStack.setUserCartList(userCartList);
					mergeOrderList.add(orderStack);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return mergeOrderList;
	}

	/**
	 * 批量导单明细
	 * 批量导入订单数据合并成购物车商品
	 * @param orderStackList
	 * @param userInfo
	 * @return
	 */
	public static List<UserCart> getMergeUserCartList(List<OrderStack> orderStackList, UserInfo userInfo){
		List<UserCart> userCartList = new ArrayList<UserCart> ();
		try {
			if(orderStackList != null && orderStackList.size() > 0) {
				for(OrderStack orderStack : orderStackList) {
					UserCart userCart = new UserCart();
					userCart.setProductId(orderStack.getProductId());
					userCart.setProductSpecId(orderStack.getProductSpecId());
					userCart.setGroupProductInfo(orderStack.getGroupProductInfo());
					userCart.setQuantity(orderStack.getQuantity());

					// 检查商品是否有效
					MsgModel<Product> msgModel = ProductUtil.getProductByUserLevel(userCart.getProductId(), userCart.getProductSpecId(), userInfo, false);
					if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
						Product product = msgModel.getData();
						// 检查秒杀商品即将开始状态
						ProductCheckUtil.checkSeckillProductStatusReadStatus(product);
						// 组合商品合并
						if(StringUtil.nullToBoolean(product.getIsGroupProduct())){
							MsgModel<Product> xmsgModel = ProductCheckUtil.checkGroupProductByUserLevel(product, userCart.getGroupProductInfo(), userCart.getQuantity(), false, userInfo);
							if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
								// 组合商品解析错误
								continue;
							}
						}

						// 商品税率计算
//						if(!StringUtil.nullToBoolean(product.getIsFreeTax()) && StringUtil.compareObject(product.getProductType(), GoodsType.GOODS_TYPE_CROSS)) {
//							Double taxAmount = StringUtil.nullToDoubleFormat(product.getPaymentPrice() * Product.TAXRATE  ); 
//							userCart.setTax(taxAmount);
//						}
						MsgModel<Double> tsgModel = ProductUtil.getProductTax(product.getPaymentPrice(), product.getProductType(), product.getIsFreeTax());
					    if(StringUtil.nullToBoolean(tsgModel.getIsSucc())) {
					    	userCart.setTax(StringUtil.nullToDoubleFormat(tsgModel.getData()));
					    }
						

						userCart.setPaymentPrice(StringUtil.nullToDoubleFormat(product.getPaymentPrice()));
						userCart.setProductTags(StringUtil.null2Str(product.getProductTags()));
						userCart.setProductName(StringUtil.null2Str(product.getName()));
						userCart.setProductType(StringUtil.nullToInteger(product.getProductType()));
						userCart.setWarehouseTemplateId(StringUtil.nullToLong(product.getWareHouseTemplateId()));
						userCart.setImagePath(product.getImage());
						userCart.setProductId(product.getProductId());
						userCart.setIsSoldout(product.getIsPaymentSoldout());
						userCart.setStockNumber(product.getPaymentStockNumber());
						userCart.setIsTaskProduct(StringUtil.nullToBoolean(product.getIsTaskProduct()));
						userCart.setTaskProductTag(StringUtil.null2Str(product.getTaskProductTag()));
						userCart.setIsSeckillProduct(StringUtil.nullToBoolean(product.getIsSeckillProduct()));
						userCart.setIsRechargeGiftProduct(product.getIsRechargeGiftProduct());
						userCartList.add(userCart);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return userCartList;
	}
}

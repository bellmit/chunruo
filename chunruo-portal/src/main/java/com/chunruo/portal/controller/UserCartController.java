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

import com.chunruo.cache.portal.impl.UserCartListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.UserCart;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.UserCartManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.tag.ProductDetailTag;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.ProductCheckUtil;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.util.UserCartUtil;

/**
 * 购物车
 * @author chunruo
 */
@Controller
@RequestMapping("/api/cart/")
public class UserCartController extends BaseController{
	@Autowired
	private UserCartManager userCartManager;
	@Autowired
	private UserCartListByUserIdCacheManager userCartListByUserIdCacheManager;
	
	/**
	 * 增加商品数量
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/addNumCart")
	public @ResponseBody Map<String, Object> add(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long productId = StringUtil.nullToLong(request.getParameter("productId"));
		Long productSpecId = StringUtil.nullToLong(request.getParameter("productSpecId"));
		Integer quantity = StringUtil.nullToInteger(request.getParameter("num")); //购物车传参
		Integer addNum = StringUtil.nullToInteger(request.getParameter("addNum"));//商品详情传参
		String groupProductInfo = StringUtil.nullToString(request.getParameter("groupProductInfo")).replace("\\s+", ""); //组合商品数据
		
		try{
			// 检查当前店铺信息是否有效
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			//购物车商品数量不能为空
			if(StringUtil.compareObject(quantity, 0) && StringUtil.compareObject(addNum, 0)){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "购物车商品数量不能为空");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			//判断商品是否已经下架
			MsgModel<Product> msgModel = ProductUtil.getProductByUserLevel(productId, productSpecId, userInfo, true);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, msgModel.getMessage());
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 商品信息
			Product product = msgModel.getData();
			boolean isSpceProduct = StringUtil.nullToBoolean(product.getIsSpceProduct());
			
			// 检查购物车是否已存在商品
			UserCart userCart = null;
			MsgModel<UserCart> cmsgModel = ProductCheckUtil.checkExistUserCartByProduct(product, productSpecId, groupProductInfo, userInfo);
			if(StringUtil.nullToBoolean(cmsgModel.getIsSucc())){
				// 已存在的购物车记录
				userCart = cmsgModel.getData();
			}else{
				// 新建购物车记录
				userCart = new UserCart();
				userCart.setProductId(product.getProductId());
				userCart.setUserId(userInfo.getUserId());
				userCart.setIsSpceProduct(isSpceProduct);
				userCart.setProductSpecId(productSpecId);
				userCart.setGroupProductInfo(groupProductInfo);
				userCart.setCreateTime(DateUtil.getCurrentDate());
			}
			
			// 商品数量累计
			Integer number = 0;
			if(quantity.compareTo(0) == 1){
				number = quantity;
				userCart.setQuantity(number);
			}else{
				Integer quantityBak = StringUtil.nullToInteger(userCart.getQuantity());
				number = quantityBak + addNum;
				userCart.setQuantity(number);
			}
			
			// 检查商品是否能加入购物车
			MsgModel<Product> productCheckModel = ProductCheckUtil.checkProduct(product.getProductId(), productSpecId, groupProductInfo, number, userInfo,false);
			if(!StringUtil.nullToBoolean(productCheckModel.getIsSucc())){
				// 没有选择有效商品
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(productCheckModel.getMessage()));
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 保存数据库
			userCart.setUpdateTime(DateUtil.getCurrentDate());
			this.userCartManager.save(userCart);
			
			try{
				// 更新缓存
				this.userCartListByUserIdCacheManager.updateSession(userInfo.getUserId(), userCart);
			}catch(Exception e){
				e.getMessage();
			}
			
			String tax = "0";
			String goTax = "0";
			Double totalPaymentPrice = DoubleUtil.mul(product.getPaymentPrice(), StringUtil.nullToDouble(number));
		    MsgModel<Double> tsgModel = ProductUtil.getProductTax(totalPaymentPrice, product.getProductType(), product.getIsFreeTax());
			if(!StringUtil.nullToBoolean(tsgModel.getIsSucc())) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "税费计算错误");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			Double taxPrice = StringUtil.nullToDoubleFormat(tsgModel.getData());
			tax = StringUtil.nullToDoubleFormatStr(taxPrice);
//			if(StringUtil.compareObject(product.getProductType(), GoodsType.GOODS_TYPE_DIRECT)
//					&& taxPrice.compareTo(0D) <= 0) {
//				goTax = "订单商品总金额约384元以内免税";
//			}
			//获取购物车商品数量
			Integer cartProductNumbers = UserCartUtil.getUserCartProductNumbers(userInfo);
			resultMap.put("tax", tax);
			resultMap.put("goTax", goTax);
			resultMap.put("cartId", userCart.getCartId());
			resultMap.put("cartProductNumbers", cartProductNumbers);
			resultMap.put("quantity", number);
			resultMap.put("productId", product.getProductId());
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "加入购物车成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "加入购物车失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	
	
	/**
	 * 删除商品
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/deleteCart")
	public @ResponseBody Map<String, Object> deleteCart(final HttpServletRequest request ,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String cartIds = StringUtil.null2Str(request.getParameter("cartIds"));
		
		try{
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			List<Long> cartIdList = StringUtil.stringToLongArray(cartIds);
			if(cartIdList == null || cartIdList.size() <= 0){
				// 没有选择记录
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, this.getText("错误，删除失败"));
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 删除已选择的购物车数据
			List<UserCart> userCartList = this.userCartManager.getByIdList(cartIdList);
			if(userCartList != null && userCartList.size() > 0){
				List<Long> idList = new ArrayList<Long> ();
				for(UserCart userCart : userCartList){
					if(!StringUtil.compareObject(userCart.getUserId(), userInfo.getUserId())){
						// 用户信息不匹配
						continue;
					}
					idList.add(userCart.getCartId());
				}
				
				// 更新数据库
				this.userCartManager.deleteByIdList(idList);
				
				try{
					// 更新缓存
					this.userCartListByUserIdCacheManager.deleteSession(userInfo.getUserId(), idList);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "删除购物车成功");			
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "删除失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	
	/**
	 * 检查购物车内是否有有效商品
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/checkIsHaveProduct")
	public @ResponseBody Map<String,Object> checkIsHaveProduct(final HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		
		try {
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			List<UserCart> userCartList = new ArrayList<UserCart>();
			Map<String,UserCart> userCartMap = this.userCartListByUserIdCacheManager.getSession(StringUtil.nullToLong(userInfo.getUserId()));
			if(userCartMap != null && userCartMap.size() > 0) {
				for(Map.Entry<String, UserCart> entry : userCartMap.entrySet()) {
					userCartList.add(entry.getValue());
				}
			}
			
			resultMap.put("isValidProduct", 0);
			if(userCartList != null && userCartList.size() > 0) {
				for(UserCart userCart : userCartList ) {
					//检查商品是否有效
					MsgModel<Product> productCheckModel = ProductUtil.getProductByUserLevel(userCart.getProductId(), userCart.getProductSpecId(), userInfo, true);
					if(StringUtil.nullToBoolean(productCheckModel.getIsSucc())){
						resultMap.put("isValidProduct", 1);
						break;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
}


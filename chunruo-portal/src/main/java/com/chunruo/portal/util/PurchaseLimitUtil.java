package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.chunruo.cache.portal.impl.OrderLockStockByProductIdCacheManager;
import com.chunruo.cache.portal.impl.PurchaseLimitListCacheManager;
import com.chunruo.cache.portal.impl.RefundByOrderItemIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.OrderLockStock;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.PurchaseLimit;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.controller.OrderPayController;

/**
 * 限购
 * @author Administrator
 */
public class PurchaseLimitUtil {
	protected static final Log log = LogFactory.getLog(PurchaseLimitUtil.class);
	public static ThreadLocal<Map<Long,Integer>> USER_LIMIT_THREAD_LOCAL = new ThreadLocal<Map<Long,Integer>>();

	//**  此类中返回msgModel.setIsSucc(false)代表限购无效，可继续购买；只有返回true时，才说明被限购了，不能购买 。 **//
	
	/**
	 * 按身份证限购
	 * 检查身份证号码一天内下单数量
	 * @return
	 */
	public static MsgModel<Integer> checkIdentityOrderNumToday(String identityNo){
		MsgModel<Integer> msgModel = new MsgModel<Integer>();
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	public static MsgModel<Integer> checkUserLimitByProduct(Product product, UserInfo userInfo, int number){
		MsgModel<Integer> msgModel = new MsgModel<Integer>();
		try {
			MsgModel<PurchaseLimit> limitModel = PurchaseLimitUtil.getPurchaseLimitByType(PurchaseLimit.PURCHASE_LLIMIT_USER, StringUtil.nullToLong(product.getProductId()));
		    if(!StringUtil.nullToBoolean(limitModel.getIsSucc())) {
		    	msgModel.setData(-1);
		    	msgModel.setIsSucc(false);
		    	return msgModel;
		    }
		    
		    PurchaseLimit purchaseLimit = limitModel.getData();
		    if(purchaseLimit != null 
		    		&& purchaseLimit.getLimitId() != null
		    		&& purchaseLimit.getCreateTime() != null
		    		&& StringUtil.compareObject(product.getProductId(), purchaseLimit.getProductId())) {
		    	Integer limitHours = StringUtil.nullToInteger(purchaseLimit.getHours());
		    	Integer limitNumber = StringUtil.nullToInteger(purchaseLimit.getLimitNumber());
		    	if(limitHours <= 0 || limitNumber <= 0) {
		    		msgModel.setData(-1);
		    		msgModel.setIsSucc(false);
		    		return msgModel;
		    	}
				
				List<Integer> refundTypeList = new ArrayList<Integer>();
				refundTypeList.add(Refund.REFUND_TYPE_MONEY);   
				refundTypeList.add(Refund.REFUND_TYPE_GOODS);   
				refundTypeList.add(Refund.REFUND_TYPE_CANCEL);  
				
				int remainNumber = limitNumber;  
				Date lastBuyTime = null;
				boolean isUserLimit = false;
				
				OrderItemsManager orderItemsManager = Constants.ctx.getBean(OrderItemsManager.class);
				RefundByOrderItemIdCacheManager refundByOrderItemIdCacheManager = Constants.ctx.getBean(RefundByOrderItemIdCacheManager.class);
				
				String startPayTime = DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, purchaseLimit.getCreateTime());
				List<OrderItems> orderItemsList = orderItemsManager.getListByPurchaseLimit(userInfo.getUserId(), startPayTime, purchaseLimit.getProductId());
				if(orderItemsList != null && orderItemsList.size() > 0) {
		        	int totalBuyProductQuantity = 0;  
		        	Date limitDate = DateUtil.getDateMinuteBefore(DateUtil.getCurrentDate(), limitHours * 60);
    				for(OrderItems orderItems : orderItemsList) {
    					Long productId = StringUtil.nullToLong(orderItems.getProductId());
    					if(StringUtil.nullToBoolean(orderItems.getIsGroupProduct())) {
    						if(!StringUtil.nullToBoolean(orderItems.getIsMainGroupItem())) {
    							continue;
    						}
    						productId = StringUtil.nullToLong(orderItems.getGroupProductId());
    					}
    					if(!StringUtil.compareObject(productId, purchaseLimit.getProductId())) {
    						continue;
    					}
    					
    					//退款完成不计算在内
    					Refund refund = refundByOrderItemIdCacheManager.getSession(StringUtil.nullToLong(orderItems.getItemId()));
    					if (refund != null 
    							&& refund.getRefundId() != null
    							&& StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_COMPLETED)
    							&& refundTypeList.contains(StringUtil.nullToInteger(refund.getRefundType()))) {
							continue;
						}
    					
    					//获取最近的限购购买时间
    					if(StringUtil.nullToBoolean(orderItems.getIsLastProduct())
    							&& !isUserLimit 
    							&& lastBuyTime == null) {
					    	Date lastLimitDate = DateUtil.getDateMinuteBefore(DateUtil.getCurrentDate(), 48 * 60);
							if(lastLimitDate.getTime() <= orderItems.getCreateTime().getTime()) {
								//触发用户限购的时间
    							lastBuyTime = orderItems.getCreateTime();
								isUserLimit = true;
							}
    					}
    					
    					//非限制时间段内的订单忽略
	        			if(limitDate.getTime() <= orderItems.getCreateTime().getTime()) {
        					totalBuyProductQuantity += StringUtil.nullToInteger(orderItems.getQuantity());
	        			}
    				}
    				
    				//剩下可购数量
			        remainNumber = limitNumber - totalBuyProductQuantity;
			        if(isUserLimit) {
			        	msgModel.setIsSucc(true);
			        	msgModel.setLastTime(lastBuyTime);
			        	msgModel.setIsExpire(true);        //标识开启48小时限购    订单加个48小时字段
			        	msgModel.setMessage(String.format("\"%s\"不可继续购买", product.getName()));
			        	return msgModel;
			        }else if(totalBuyProductQuantity >= limitNumber) {
			        	//超出限购数量
			        	msgModel.setIsSucc(true);
			        	msgModel.setLastTime(lastBuyTime);
			        	msgModel.setIsExpire(true);        //标识开启48小时限购    订单加个48小时字段
			        	msgModel.setMessage(String.format("\"%s\"不可继续购买", product.getName()));
			        	return msgModel;
			        }else if(number > remainNumber) {
			        	msgModel.setIsSucc(true);
			        	msgModel.setData(remainNumber);
                    	msgModel.setMessage(String.format("您当前最多可购买商品\"%s\"%s件", product.getName(), remainNumber));
	                    return msgModel;
			        }else if(number == remainNumber) {
			        	//开启48小时内限购此商品
			        	msgModel.setIsSucc(false);
			        	msgModel.setLastTime(lastBuyTime);
			        	msgModel.setIsExpire(true);        //标识开启48小时限购    订单加个48小时字段
			        	msgModel.setData(remainNumber);    //可购数量
			        	return msgModel;
			        }
				}
		        
		        msgModel.setIsSucc(false);
		        msgModel.setData(remainNumber);
		        return msgModel;
		    } 
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		msgModel.setData(-1);
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 按等级限购
	 * @param product
	 * @param userInfo
	 * @return
	 */
	public static MsgModel<Integer> checkUserLevelProductLimit(Product product, UserInfo userInfo){
		MsgModel<Integer> msgModel = new MsgModel<Integer>();
		try {
			//获取等级限购策略
			MsgModel<PurchaseLimit> limitModel = PurchaseLimitUtil.getPurchaseLimitByType(PurchaseLimit.PURCHASE_LLIMIT_USERLEVEL, StringUtil.nullToLong(product.getProductId()));
			if(!StringUtil.nullToBoolean(limitModel.getIsSucc())) {
				msgModel.setIsSucc(false);
				msgModel.setData(-1);
				msgModel.setMessage("未设置等级限购");
				return msgModel;
			}
			
			PurchaseLimit purchaseLimit = limitModel.getData();
			int v1Number = StringUtil.nullToInteger(purchaseLimit.getV1Number());  //v1预留库存
		    int v2Number = StringUtil.nullToInteger(purchaseLimit.getV2Number());  //v2预留库存
			if(v1Number <= 0 || v2Number <= 0) {
				msgModel.setIsSucc(false);
				msgModel.setData(-1);
				msgModel.setMessage("预留库存设置错误");
				return msgModel;
			}
					
			
			List<Integer> typeList = new ArrayList<Integer>();
			typeList.add(OrderLockStock.ORDER_LOCK_STOCK_LEVEL);   //等级限购
			typeList.add(OrderLockStock.ORDER_LOCK_STOCK_SECLEVEL);//秒杀&限购
			
			Map<Long,ProductSpec> productSpecMap = new HashMap<Long,ProductSpec>();
			if(StringUtil.nullToBoolean(product.getIsSpceProduct())) {
				List<ProductSpec> productSpecList = product.getProductSpecList();
				if(productSpecList != null && productSpecList.size() > 0) {
					for(ProductSpec productSpec : productSpecList) {
						productSpecMap.put(StringUtil.nullToLong(productSpec.getProductSpecId()), productSpec);
					}
				}
			}
			
			//当前已锁库存数
			int lockStockNumber = 0; 
			OrderLockStockByProductIdCacheManager orderLockStockByProductIdCacheManager = Constants.ctx.getBean(OrderLockStockByProductIdCacheManager.class);
			List<OrderLockStock> orderLockStockList = orderLockStockByProductIdCacheManager.getSession(StringUtil.nullToLong(product.getProductId()));
			if(orderLockStockList != null && orderLockStockList.size() > 0) {
				for(OrderLockStock orderLockStock : orderLockStockList) {
					if(typeList.contains(StringUtil.nullToInteger(orderLockStock.getType()))) {
						if(StringUtil.compareObject(StringUtil.nullToLong(OrderPayController.ORDER_PAY_LOCAL.get()), StringUtil.nullToLong(orderLockStock.getOrderId()))) {
					        //当前付款订单忽略
							log.info("2当前付款orderId:"+OrderPayController.ORDER_PAY_LOCAL.get());
							continue;
						}
						
						lockStockNumber += StringUtil.nullToInteger(orderLockStock.getQuantity());
						if(StringUtil.nullToBoolean(orderLockStock.getIsSpceProduct())) {
							//规格库存减去锁库
							ProductSpec productSpec = productSpecMap.get(StringUtil.nullToLong(orderLockStock.getProductSpecId()));
							if(productSpec != null && productSpec.getProductSpecId() != null) {
								int paymentStockNumber = StringUtil.nullToInteger(productSpec.getPaymentStockNumber()) - StringUtil.nullToInteger(orderLockStock.getQuantity());
							    if(paymentStockNumber <= 0) {
							    	productSpec.setIsPaymentSoldout(true);
									productSpec.setPaymentStockNumber(0);
							    }else {
									productSpec.setPaymentStockNumber(paymentStockNumber);
							    }
							}
						}
					}
				}
			}
			
			List<Integer> userLevelList = new ArrayList<Integer>();
			userLevelList.add(UserLevel.USER_LEVEL_V2);  //v2
			userLevelList.add(UserLevel.USER_LEVEL_V3);  //v3
			userInfo.setPaymentUserLevel(StringUtil.nullToInteger(userInfo.getLevel()));
			
			//商品剩余可购库存(总的可销售商品数量)
			int remainNumber = StringUtil.nullToInteger(product.getPaymentStockNumber()) - lockStockNumber;
			log.info(String.format("[商品id:%s,总库存:%s,锁库:%s]", product.getProductId(), product.getPaymentStockNumber(), lockStockNumber));
			log.info("2-0.当前可购库存：" + remainNumber );
			if(remainNumber <= v1Number) {
				//仅剩v1预留库存，商品售价为v1
				if(userLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))) {
					//临时修改用户等级
					userInfo.setPaymentUserLevel(UserLevel.USER_LEVEL_DEALER);
				}
			}else if(remainNumber <= (v1Number + v2Number)) {
				log.info("2-1.当前可购库存：" + remainNumber);
				if(userLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))) {
					remainNumber = remainNumber - v1Number;
				}
				//v2预留库存
				if(StringUtil.compareObject(StringUtil.nullToInteger(userInfo.getLevel()), UserLevel.USER_LEVEL_V3)) {
					userInfo.setPaymentUserLevel(UserLevel.USER_LEVEL_V2);
				}
			}else {
				if(StringUtil.compareObject(StringUtil.nullToInteger(userInfo.getLevel()), UserLevel.USER_LEVEL_V3)) {
					remainNumber = remainNumber - (v1Number +v2Number);
				}
				log.info("2-2.当前可购库存："+remainNumber);
			}
			
			msgModel.setIsSucc(false);
			msgModel.setData(remainNumber);
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		msgModel.setData(-1);
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 获取限购策略
	 * @param type
	 * @return
	 */
	public static MsgModel<PurchaseLimit> getPurchaseLimitByType(Integer type, Long productId){
		MsgModel<PurchaseLimit> msgModel = new MsgModel<PurchaseLimit>();
		try {
			PurchaseLimitListCacheManager purchaseLimitListCacheManager = Constants.ctx.getBean(PurchaseLimitListCacheManager.class);
			Map<String, List<PurchaseLimit>> purchaseLimitMap = purchaseLimitListCacheManager.getSession();
			if(purchaseLimitMap != null
					&& purchaseLimitMap.size() > 0
					&& purchaseLimitMap.containsKey(StringUtil.null2Str(type))) {
				PurchaseLimit purchaseLimit = null;
				List<PurchaseLimit> purchaseLimitList = purchaseLimitMap.get(StringUtil.null2Str(type));
				if(purchaseLimitList != null && purchaseLimitList.size() > 0) {
					if(StringUtil.compareObject(type, PurchaseLimit.PURCHASE_LLIMIT_SUBSCRIBER)) {
						purchaseLimit = purchaseLimitList.get(0);
					}else {
						for(PurchaseLimit limit : purchaseLimitList) {
							if(StringUtil.compareObject(StringUtil.nullToLong(limit.getProductId()), productId)) {
								purchaseLimit = limit;
								break;
							}
						}
					}
				}
				if(purchaseLimit != null && purchaseLimit.getLimitId() != null) {
					msgModel.setIsSucc(true);
					msgModel.setData(purchaseLimit);
					return msgModel;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		return msgModel;
	}
}
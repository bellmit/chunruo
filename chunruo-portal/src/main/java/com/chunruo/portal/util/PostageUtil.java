package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chunruo.cache.portal.impl.PostageTemplateCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.model.PostageTemplate;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.vo.PostageVo;

public class PostageUtil {
	
	public static Map<String, Object>  getProductPostageRegion(Product product) {
		Map<String, Object> objectMap = new HashMap<String,Object>();
		try {
			
			if(StringUtil.nullToBoolean(product.getIsAggrProduct())) {
				//聚合商品
				List<Product> aggrProductList = product.getAggrProductList();
				if(aggrProductList != null && !aggrProductList.isEmpty()) {
					for(Product aggrProduct : aggrProductList) {
						//获取快递运送地点
						Map<String, Object> dataMap =  getProductPostageRegion(aggrProduct.getTemplateId(), aggrProduct.getIsFreePostage(),aggrProduct.getProductType());
						//获取运费区间
						checkProductPostageRegion(aggrProduct);
						
						List<Integer> productTypeList = new ArrayList<Integer>();
						productTypeList.add(GoodsType.GOODS_TYPE_DIRECT); // 直邮
						productTypeList.add(GoodsType.GOODS_TYPE_DIRECT_GO);// 行邮
						if(productTypeList.contains(aggrProduct.getProductType())) {
							aggrProduct.setPostageNotice(String.format("运费预计%s，需7~10个工作日送达", aggrProduct.getPostage()));
						}else {
							aggrProduct.setPostageNotice(String.format("运费预计%s，支付后1~2个工作日发货", aggrProduct.getPostage()));
						}
						if(StringUtil.compareObject(aggrProduct.getProductId(), product.getProductId())) {
							objectMap.put("postage", aggrProduct.getPostage());
					    	objectMap.putAll(dataMap);
					    }
					}
				}
			}else {
				objectMap.putAll(getProductPostageRegion(product.getTemplateId(), product.getIsFreePostage(),product.getProductType()));
				//获取运费区间
				checkProductPostageRegion(product);
				objectMap.put("postage", product.getPostage());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return objectMap;
	}
	
	/**
	 * 获取商品运费区间
	 * @param product
	 */
	public static void checkProductPostageRegion(Product product) {
		try {
			if(StringUtil.nullToBoolean(product.getIsSpceProduct())) {
				//规格商品
				List<ProductSpec> productSpecList = product.getProductSpecList();
				if(productSpecList != null && !productSpecList.isEmpty()) {
					List<Double> postageList = new ArrayList<Double>();
					for(ProductSpec productSpec : productSpecList) {
						Map<String,Double> postageMap = PostageUtil.getPostage(product.getTemplateId(), productSpec.getWeigth());
					    if(postageMap != null && !postageMap.isEmpty()) {
					    	postageList.add(StringUtil.nullToDoubleFormat(postageMap.get("minPostage")));
					    	postageList.add(StringUtil.nullToDoubleFormat(postageMap.get("maxPostage")));
					    }
					}
					
					if(postageList != null && !postageList.isEmpty()) {
						Collections.sort(postageList);
						Double minPostage = postageList.get(0);
				    	product.setPostage(String.format("%s起", StringUtil.nullToDoubleFormatStr(minPostage)));
					}
				}
			}else {
				Map<String,Double> postageMap = PostageUtil.getPostage(product.getTemplateId(), product.getWeigth());
			    if(postageMap != null && !postageMap.isEmpty()) {
			    	Double minPostage = StringUtil.nullToDouble(postageMap.get("minPostage"));
			    	product.setPostage(String.format("%s起", StringUtil.nullToDoubleFormatStr(minPostage)));
			    }
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 得到该商品首重运费区间
	 * @param templateId
	 * @param isFreePostageProduct
	 * @return
	 */
	public static Map<String, Object>  getProductPostageRegion(Long templateId, Boolean isFreePostageProduct,int productType) {
		Map<String, Object> objectMap = new HashMap<String,Object>();
		try {
			PostageTemplateCacheManager postageTemplateCacheManager = Constants.ctx.getBean(PostageTemplateCacheManager.class);
			if (templateId != null) {
				// 所有的运费区间
				List<PostageTemplate> postageTemplateList = postageTemplateCacheManager.getSession();
				PostageTemplate productTemplate = null; // 该商品运费模板
				PostageTemplate freePostageTemplate = null; // 包邮运费模板
				if (postageTemplateList != null && postageTemplateList.size() > 0) {
					for (PostageTemplate postageTemplate : postageTemplateList) {
						if (StringUtil.compareObject(postageTemplate.getTemplateId(), templateId)) {
							productTemplate = postageTemplate;
						} else if (StringUtil.nullToBoolean(postageTemplate.getIsFreeTemplate())) {
							freePostageTemplate = postageTemplate;
						}
					}

					if (productTemplate != null && productTemplate.getTemplateId() != null) {
//						List<Double> postagePriceList = new ArrayList<Double>();
						List<PostageVo> postageVoList = new ArrayList<PostageVo>();
						List<String> strPostageList = StringUtil.strToStrList(productTemplate.getTplArea(), ";");
						String priceNoticeStr = "%sg以内%s元 ，之后每增加%sg,运费增加%s元";
						if (strPostageList != null && strPostageList.size() > 0) {
							int i = 1;
							for (String strPostage : strPostageList) {
								PostageVo postageVo = PostageVo.getIntance(strPostage);
								if (postageVo != null && postageVo.getAreaIdList() != null
										&& postageVo.getAreaIdList().size() > 0) {
									Double postage = postageVo.getFirstPrice(); // 首重邮费
//									postagePriceList.add(postageVo.getFirstPrice());
									String priceNotice = String.format(priceNoticeStr, postageVo.getFirstWeigth(),
											StringUtil.nullToDoubleFormatStr(postage), postageVo.getAfterWeigth(),
											StringUtil.nullToDoubleFormatStr(postageVo.getAfterPrice()));
									postageVo.setPriceNotice(priceNotice);
									if(StringUtil.compareObject(i, 1)) {
										postageVo.setAreaNames("全国（指定地区除外）");
									}else {
										StringBuffer areaNameStrBuf = new StringBuffer();
										for (Long areaId : postageVo.getAreaIdList()) {
											String areaName = Constants.AREA_MAP.get(areaId).getAreaName();
											areaNameStrBuf.append(areaName);
											areaNameStrBuf.append(",");
										}
										postageVo.setAreaNames(areaNameStrBuf.deleteCharAt(areaNameStrBuf.length() - 1).toString());
									}
									i++;
									postageVoList.add(postageVo);
								}
							}
						}

						Collections.reverse(postageVoList);  // 反序
						// 包邮商品
						if (StringUtil.nullToBoolean(isFreePostageProduct) && postageVoList != null && postageVoList.size() > 0
								&& freePostageTemplate != null && freePostageTemplate.getTemplateId() != null
								&& freePostageTemplate.getFreePostageAmount().compareTo(0.0d) > 0) {
							PostageVo postageVo = new PostageVo();
							postageVo.setAreaNames("包邮活动");
							postageVo.setPriceNotice(String.format("此商品参与满%s元包邮",
									StringUtil.nullToDoubleFormatStr(freePostageTemplate.getFreePostageAmount())));
							postageVoList.add(postageVo);
						}

//						if (postagePriceList != null && postagePriceList.size() > 0) {
//							Collections.sort(postagePriceList);
//							Double minPricePostage = StringUtil.nullToDoubleFormat(postagePriceList.get(0));
//							Double maxPricePostage = StringUtil.nullToDoubleFormat(postagePriceList.get(postagePriceList.size() - 1));
//							objectMap.put("postage", "0");
//							if (minPricePostage.compareTo(maxPricePostage) == 0) {
//								objectMap.put("postage", String.format("%s", maxPricePostage.intValue()));
//							} else {
//								objectMap.put("postage", String.format("%s~%s", minPricePostage.intValue(),maxPricePostage.intValue()));
//							}
//						}

						objectMap.put("postageVoList", postageVoList);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectMap;
	}	

	/**
	 * 查找当前活动满包邮模板
	 * @return
	 */
	public static PostageTemplate getFreePostageTemplate(boolean isCheckArea, Long provinceId){
		PostageTemplate freeTemplate = null;
		try{
			// 查找当前活动满包邮模板
			PostageTemplateCacheManager postageTemplateCacheManager = Constants.ctx.getBean(PostageTemplateCacheManager.class);
			List<PostageTemplate> postageTemplateList = postageTemplateCacheManager.getSession();
			if(postageTemplateList != null && postageTemplateList.size() > 0){
				for(PostageTemplate postageTemplate : postageTemplateList){
					if(StringUtil.nullToBoolean(postageTemplate.getIsFreeTemplate())){
						if(StringUtil.nullToBoolean(isCheckArea)){
							// 校验按地区查询
							List<String> postageList = StringUtil.strToStrList(postageTemplate.getTplArea(), ",");
							if(postageList != null && postageList.size() > 0){
								List<String> freePostageAreaList = StringUtil.strToStrList(postageList.get(0), "&");
								if(freePostageAreaList != null 
										&& freePostageAreaList.size() > 0
										&& freePostageAreaList.contains(StringUtil.null2Str(provinceId))){
									freeTemplate = postageTemplate;
									break;
								}
							}
						}else{
							freeTemplate = postageTemplate;
							break;
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return freeTemplate;
	}
		
	/**
	 * 根据邮费模板
	 * 按地区和重量计算邮费
	 * @param templateId
	 * @param areaCode
	 * @param totalWeights
	 * @return
	 */
	public static PostageVo getPostage(Long templateId, Long areaCode, Double totalWeights){
		PostageTemplateCacheManager postageTemplateCacheManager = Constants.ctx.getBean(PostageTemplateCacheManager.class);
		if(templateId != null){
			List<PostageTemplate> postageTemplateList = postageTemplateCacheManager.getSession();
			if(postageTemplateList != null && postageTemplateList.size() > 0){
				Map<Long, PostageTemplate> postageTemplateMap = new HashMap<Long, PostageTemplate> ();
				for(PostageTemplate postageTemplate : postageTemplateList){
					postageTemplateMap.put(postageTemplate.getTemplateId(), postageTemplate);
				}

				if(postageTemplateMap.containsKey(templateId)){
					PostageTemplate postageTemplate = postageTemplateMap.get(templateId);
					List<String> strPostageList = StringUtil.strToStrList(postageTemplate.getTplArea(), ";");
					if(strPostageList != null && strPostageList.size() > 0){
						for(String strPostage : strPostageList){
							PostageVo postageVo = PostageVo.getIntance(strPostage);
							if(postageVo != null 
									&& postageVo.getAreaIdList() != null
									&& postageVo.getAreaIdList().contains(areaCode)){
								Double postage = new Double(0);
								Double totalWeight = StringUtil.nullToDouble(totalWeights);
								if(postageVo.getAfterWeigth() > 0 && postageVo.getAfterPrice().intValue() > 0){
									if(totalWeight >= postageVo.getFirstWeigth()){
										Double totalAfterWeights = DoubleUtil.sub(totalWeight, postageVo.getFirstWeigth());
										//超过部分取整
										Double afterWeigthTime = StringUtil.nullToDouble(Math.ceil(DoubleUtil.divide(totalAfterWeights, postageVo.getAfterWeigth())));
										postage = DoubleUtil.add(postageVo.getFirstPrice(),DoubleUtil.mul(afterWeigthTime, postageVo.getAfterPrice()));
									}else{
										postage = postageVo.getFirstPrice();
									}
								}else{
									postage = postageVo.getFirstPrice();
								}
								postageVo.setPostage(postage);
								return postageVo;
							}
						}
					}
				}
			}
		}

		return null;
	}
	
	
	/**
	 *
	 * 获取商品最低运费
	 * @param templateId
	 * @param totalWeights
	 * @return
	 */
	public static Map<String,Double> getPostage(Long templateId, Double totalWeights){
		Map<String,Double> map = new HashMap<String,Double>();
		try {
			if(templateId != null){
				PostageTemplateCacheManager postageTemplateCacheManager = Constants.ctx.getBean(PostageTemplateCacheManager.class);
				List<PostageTemplate> postageTemplateList = postageTemplateCacheManager.getSession();
				if(postageTemplateList != null && postageTemplateList.size() > 0){
					Map<Long, PostageTemplate> postageTemplateMap = new HashMap<Long, PostageTemplate> ();
					for(PostageTemplate postageTemplate : postageTemplateList){
						postageTemplateMap.put(postageTemplate.getTemplateId(), postageTemplate);
					}

					if(postageTemplateMap.containsKey(templateId)){
						PostageTemplate postageTemplate = postageTemplateMap.get(templateId);
						List<String> strPostageList = StringUtil.strToStrList(postageTemplate.getTplArea(), ";");
						if(strPostageList != null && strPostageList.size() > 0){
							List<Double> postagePriceList = new ArrayList<Double>();
							for(String strPostage : strPostageList){
								PostageVo postageVo = PostageVo.getIntance(strPostage);
								if(postageVo != null 
										&& postageVo.getAreaIdList() != null){
									if(!StringUtil.nullToBoolean(postageTemplate.getIsFreeTemplate())) {
										Double postage = new Double(0);
										Double totalWeight = DoubleUtil.add(StringUtil.nullToDouble(totalWeights), postageVo.getPackageWeigth());  //包材
										//非包邮模板
										if(postageVo.getAfterWeigth() > 0 && postageVo.getAfterPrice().intValue() > 0){
											if(totalWeight >= postageVo.getFirstWeigth()){
												Double totalAfterWeights = DoubleUtil.sub(totalWeight, postageVo.getFirstWeigth());
												//超过部分取整
												Double afterWeigthTime = StringUtil.nullToDouble(Math.ceil(DoubleUtil.divide(totalAfterWeights, postageVo.getAfterWeigth())));
												postage = DoubleUtil.add(postageVo.getFirstPrice(),DoubleUtil.mul(afterWeigthTime, postageVo.getAfterPrice()));
											}else{
												postage = postageVo.getFirstPrice();
											}
										}else{
											postage = postageVo.getFirstPrice();
										}
										postagePriceList.add(postage);
									}
								}
							}
							if(postagePriceList != null && !postagePriceList.isEmpty()) {
								//排序
								Collections.sort(postagePriceList);
								map.put("minPostage", StringUtil.nullToDoubleFormat(postagePriceList.get(0)));
								map.put("maxPostage", StringUtil.nullToDouble(postagePriceList.get(postagePriceList.size() - 1)));
							}
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 集合所有参与包邮活动且在包邮区的商品，
	 * 若满足金额，模版暂改为所在仓库包邮模版
	 * @param buyProductList
	 * @return MsgModel<List<Product>>
	 */
	public static MsgModel<List<Product>> checkFreePostageProduct(List<Product> buyProductList, Long provinceId){
		MsgModel<List<Product>> productCheckModel = new MsgModel<List<Product>> ();
		try{
			// 检查参与包邮的商品
			List<Product> freePostageProductList = new ArrayList<Product>();
			for(Product product : buyProductList){
				if(StringUtil.nullToBoolean(product.getIsFreePostage())){
					freePostageProductList.add(product);
				}
			}

			// 统计包邮商品的总金额
			Double productAmount = new Double(0);
			if(freePostageProductList != null && freePostageProductList.size() > 0){
				for(Product product : freePostageProductList){
					//商品数量
					int productNumber = StringUtil.nullToInteger(product.getPaymentBuyNumber());
					//商品单价
					Double price = StringUtil.nullToDoubleFormat(product.getPaymentPrice());
					//商品总结
					Double amount = StringUtil.nullToDoubleFormat(price * productNumber);
					productAmount += amount;
				}

				//包邮商品总金额
				productAmount = StringUtil.nullToDoubleFormat(productAmount);

				// 检查满包邮金额是否满足
				PostageTemplate freeTemplate = PostageUtil.getFreePostageTemplate(true, provinceId);
				if(freeTemplate != null && freeTemplate.getTemplateId() != null){
					Double freePostagePrice = StringUtil.nullToDouble(freeTemplate.getFreePostageAmount());
					if(productAmount.compareTo(freePostagePrice) >= 0){
						for(Product product : freePostageProductList){
							product.setPaymentTemplateId(freeTemplate.getTemplateId());
						}
					}
				}
			}

			productCheckModel.setData(buyProductList);
			productCheckModel.setIsSucc(true);
			return productCheckModel;
		}catch(Exception e){
			e.printStackTrace();

			productCheckModel.setIsSucc(false);
			productCheckModel.setMessage("商品满包邮模板异常");
			return productCheckModel;
		}
	}
}

package com.chunruo.webapp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.chunruo.core.Constants;
import com.chunruo.core.model.FxChildren;
import com.chunruo.core.model.FxPage;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.WebUrlConfig;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductBrand;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.service.FxPageManager;
import com.chunruo.core.service.ProductBrandManager;
import com.chunruo.core.service.ProductCategoryManager;
import com.chunruo.core.service.ProductSpecManager;
import com.chunruo.core.service.WebUrlConfigManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.util.JsonParseUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.vo.FxChildrenFieldVo;

/**
 * 分销模板页面解析
 * @author chunruo
 */
public class ChannelUtil {
	
	/**
	 * 解析自定义频道页面
	 * @param customString
	 * @param page
	 * @return
	 */
	public static List<FxChildren> parseChannel(String customString, FxPage page){
		List<FxChildren> childrenList = new ArrayList<FxChildren>();
		try {
			JSONArray array = new JSONArray(customString);
			if (array == null || array.length() <= 0) {
				return childrenList;
			}

			for (int i = 0; i < array.length(); i++) {
				FxChildren children = new FxChildren();
				children.setPageId(page.getPageId());
				children.setChildrenId(page.getChannelId());
				children.setCreateTime(new Date());
				children.setSort(i);
				children.setUpdateTime(children.getUpdateTime());
				JSONObject obj = array.getJSONObject(i);
				List<Map<String, Object>> contentMapList = new ArrayList<Map<String, Object>>();

				switch (obj.getString("type")) {
				// 图片导航
				case Constants.FiledType.IMAGE_NAV_TYPE:
					children.setAttribute(0);
					children.setType(1);
					Iterator iterator1 = obj.keys();
					while (iterator1.hasNext()) {
						String key = (String) iterator1.next();
						if (!StringUtil.compareObject("type", key)) {
							try {
								JSONObject childObj = obj.getJSONObject(key);
								Map<String, Object> contentMap = new HashMap<String, Object>();
								contentMap.put("picture", StringUtil.null2Str(childObj.getString("image")));
								setTargetType(contentMap, childObj.getString("prefix"));
								
								//配置h5跳转地址
								contentMap.put("webUrl", "");
								if(StringUtil.nullToBoolean(childObj.has("webUrl"))) {
									String webUrl = StringUtil.null2Str(childObj.getString("webUrl"));
									if(StringUtil.isHttpUrl(webUrl)) {
										contentMap.put("webUrl", webUrl);
									}
								}
								
								contentMap.put("navigation_name", childObj.getString("title"));
								// url获取到的是对应的id
								contentMap.put("content", childObj.getString("url"));
								contentMapList.add(contentMap);
							} catch (Exception e) {
								continue;
							}

						}
					}
					break;
				// 图片广告
				case Constants.FiledType.IMAGE_AD_TYPE:
					Integer attribute=StringUtil.nullToInteger(obj.getString("image_type"));
					children.setAttribute(StringUtil.nullToInteger(obj.getString("image_type")));
					children.setType(0);
					if (obj.has("nav_list")) {
						JSONObject navListObj = obj.getJSONObject("nav_list");
						if(StringUtil.compareObject(attribute, 2) && !StringUtil.compareObject(navListObj.length(), 2)) {
							//判断双图banner
							return checkData(childrenList,"双图banner必须为两张");
						}
						Iterator iteratorNav = navListObj.keys();
						while (iteratorNav.hasNext()) {
							String key = (String) iteratorNav.next();
							if (!StringUtil.compareObject("type", key)) {
								try {
									JSONObject childObj = navListObj.getJSONObject(key);
									Map<String, Object> contentMap = new HashMap<String, Object>();
									if (StringUtil.compareObject(children.getAttribute(), 1)) {
										// 单张的图片
										contentMap.put("picture", "");
										children.setPicture(StringUtil.null2Str(childObj.getString("image")));
									} else {
										// 多图轮播、双排显示
										contentMap.put("picture", StringUtil.null2Str(childObj.getString("image")));
									}
									
									setTargetType(contentMap, childObj.getString("prefix"));
									//配置h5跳转地址
									contentMap.put("webUrl", "");
									if(StringUtil.nullToBoolean(childObj.has("webUrl"))) {
										String webUrl = StringUtil.null2Str(childObj.getString("webUrl"));
										if(StringUtil.isHttpUrl(webUrl)) {
											contentMap.put("webUrl", webUrl);
										}
									}
									// 色值
									contentMap.put("navigation_name", childObj.getString("title"));
//									contentMap.put("navigation_name", "");
									// url获取到的是对应的id
									contentMap.put("content", childObj.getString("url"));
									contentMapList.add(contentMap);
								} catch (Exception e) {
									continue;
								}
							}
						}
					}
					break;
				// 图片专题
				case Constants.FiledType.IMAGE_THEME_TYPE:
					children.setAttribute(StringUtil.nullToInteger(obj.getString("image_type")));
					children.setType(2);
					if (obj.has("nav_list")) {
						JSONObject navListObj = obj.getJSONObject("nav_list");
						Iterator iteratorNav = navListObj.keys();
						while (iteratorNav.hasNext()) {
							String key = (String) iteratorNav.next();
							if (!StringUtil.compareObject("type", key)) {
								try {
									JSONObject childObj = navListObj.getJSONObject(key);
									Map<String, Object> contentMap = new HashMap<String, Object>();

									if (StringUtil.compareObject(children.getAttribute(), 1)) {
										// 单张的图片
										contentMap.put("picture", "");
										children.setPicture(StringUtil.null2Str(childObj.getString("image")));
									} else {
										// 多图轮播、双排显示
										contentMap.put("picture", StringUtil.null2Str(childObj.getString("image")));
									}
									setTargetType(contentMap, childObj.getString("prefix"));
									// 广告名称
									contentMap.put("navigation_name", childObj.getString("title"));
									// url获取到的是对应的id
									contentMap.put("content", childObj.getString("url"));
									contentMapList.add(contentMap);
								} catch (Exception e) {
									continue;
								}
							}
						}
					}
					break;
					// 图片专题
				case Constants.FiledType.IMAGE_SECKILL_TYPE:
					children.setAttribute(StringUtil.nullToInteger(obj.getString("image_type")));
					children.setType(7);
					Map<String, Object> contentMaps = new HashMap<String, Object>();
					contentMaps.put("target_type", "");
					if (obj.has("nav_list")) {
						JSONObject navListObj = obj.getJSONObject("nav_list");
						Iterator iteratorNav = navListObj.keys();
						while (iteratorNav.hasNext()) {
							String key = (String) iteratorNav.next();
							
							if (!StringUtil.compareObject("type", key)) {
								try {
									JSONObject childObj = navListObj.getJSONObject(key);
									if (StringUtil.compareObject(children.getAttribute(), 1)) {
										// 单张的图片
										contentMaps.put("picture", "");
										children.setPicture(StringUtil.null2Str(childObj.getString("image")));
									} else {
										// 多图轮播、双排显示
										contentMaps.put("picture", StringUtil.null2Str(childObj.getString("image")));
									}

									setTargetType(contentMaps, childObj.getString("prefix"));
									// 广告名称
									contentMaps.put("navigation_name", childObj.getString("title"));
									// url获取到的是对应的id
									contentMaps.put("content", childObj.getString("url"));
								} catch (Exception e) {
									continue;
								}
							}
						}
					}
					contentMapList.add(contentMaps);
					break;
				// 图片商品
				case Constants.FiledType.GOODS_TYPE:
					children.setAttribute(StringUtil.nullToInteger(obj.getString("size")));					
					children.setType(3);
					Map<String, Object> contentMap = new HashMap<String, Object>();
					contentMap.put("target_type", "");
					contentMap.put("navigation_name", "");
					contentMap.put("picture", "");
					String googsString = "";
					if (obj.has("goods")) {
						JSONObject goodsObj = obj.getJSONObject("goods");
						Iterator iteratorGoods = goodsObj.keys();
						Set<String> goodsIdSet = new HashSet<String>();
						while (iteratorGoods.hasNext()) {
							String key = (String) iteratorGoods.next();
							JSONObject childObj = goodsObj.getJSONObject(key);
							goodsIdSet.add(StringUtil.null2Str(childObj.get("id")));
						}
						googsString = StringUtil.strSetToString(goodsIdSet);
					}
					contentMap.put("content", googsString);
					contentMapList.add(contentMap);
					break;
					// 图片商品
				case Constants.FiledType.IMAGE_MODULE_TYPE:
					children.setAttribute(StringUtil.nullToInteger(obj.getString("size")));
					//类型为模块时新增以下两项数据
					children.setSpecialName(StringUtil.nullToString(obj.getString("title")));
                    children.setPicture(StringUtil.nullToString(obj.getString("picture")));					
					children.setType(4);
					Map<String, Object> moduleContentMap = new HashMap<String, Object>();
					moduleContentMap.put("target_type", "");
					moduleContentMap.put("navigation_name", "");
					moduleContentMap.put("picture", "");
					String moduleGoodsString = "";
					if (obj.has("goods")) {
						JSONObject goodsObj = obj.getJSONObject("goods");
						Iterator iteratorGoods = goodsObj.keys();
						Set<String> goodsIdSet = new HashSet<String>();
						while (iteratorGoods.hasNext()) {
							String key = (String) iteratorGoods.next();
							JSONObject childObj = goodsObj.getJSONObject(key);
							goodsIdSet.add(StringUtil.null2Str(childObj.get("id")));
						}
						moduleGoodsString = StringUtil.strSetToString(goodsIdSet);
					}
					moduleContentMap.put("content", moduleGoodsString);
					contentMapList.add(moduleContentMap);
					break;
				}
				children.setUpdateTime(new Date());
				children.setContents(StringUtil.objectToJSON(contentMapList));
				childrenList.add(children);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return childrenList;
	}
	
	private static void setTargetType(Map<String, Object> contentMap, String prefix) {
		try {
			// target_type 0-页面跳转 1-专题（目前没有） 2-商品
			if (StringUtil.compareObject(prefix, "页面")) {
				contentMap.put("target_type", 0);
			} else if (StringUtil.compareObject(prefix, "专题")) {
				contentMap.put("target_type", 3);
			}else if(StringUtil.compareObject(prefix, "礼包")){
				contentMap.put("target_type", 4);
				contentMap.put("giftId", StringUtil.nullToString(Constants.conf.getProperty("jkd.invite.product.id")));
			}else if(StringUtil.compareObject(prefix, "分类")){
				contentMap.put("target_type", 5);
			} else if(StringUtil.compareObject(prefix, "奖励中心")){
				contentMap.put("target_type", 6);
			}else if(StringUtil.compareObject(prefix, "小程序")) {
				contentMap.put("target_type", 7);
				contentMap.put("originId", StringUtil.null2Str(Constants.conf.getProperty("mini.program.answer.originId")));
				contentMap.put("appid", StringUtil.null2Str(Constants.conf.getProperty("mini.program.answer.appId")));
			} else if(StringUtil.compareObject(prefix, "H5")){
				contentMap.put("target_type", 8);
			} else if(StringUtil.compareObject(prefix, "发现详情")){
				contentMap.put("target_type", 9);
			} else if(StringUtil.compareObject(prefix, "发现话题标签")){
				contentMap.put("target_type", 10);
			} else if(StringUtil.compareObject(prefix, "发现主体")){
				contentMap.put("target_type", 11);
			} else if(StringUtil.compareObject(prefix, "品牌详情")){
				contentMap.put("target_type", 13);
			}else if(StringUtil.compareObject(prefix, "邀请有礼")) {
				contentMap.put("target_type", 14);
			} else {
				contentMap.put("target_type", 2);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 解析自定义频道页面
	 * @param customString
	 * @param page
	 * @return
	 */
	public static List<FxChildrenFieldVo> parseChannel(List<FxChildren>fxChildrenList){
		List<FxChildrenFieldVo> fieldVoList = new ArrayList<FxChildrenFieldVo>();
		if (fxChildrenList == null || fxChildrenList.size() == 0){
			return fieldVoList;
		}
		try{
			for (FxChildren child : fxChildrenList){
				FxChildrenFieldVo fieldVo = new FxChildrenFieldVo();
				String content = child.getContents();
				try {
					JSONArray jsonArr = new JSONArray(content);
					if (jsonArr != null ){
						//给前端的map
						Map<String,Object> contentMap = new HashMap<String, Object>();
						for (int i = 0 ; i < jsonArr.length(); i++){
							JSONObject json2 = jsonArr.getJSONObject(i);
							Map<String,Object> childMap = JsonParseUtil.json2Map(json2.toString());
							getFieldMap(childMap, child, contentMap);
						}
						
						fieldVo.setContent(contentMap);
						if (StringUtil.compareObject(child.getType(), 1)){
							fieldVo.setField_type(Constants.FiledType.IMAGE_NAV_TYPE);
						}else if (StringUtil.compareObject(child.getType(), 0)){
							fieldVo.setField_type(Constants.FiledType.IMAGE_AD_TYPE);
							Map <String,Object> imageMap = new HashMap<String, Object>();
							imageMap.put("nav_list", contentMap);
							imageMap.put("image_type", StringUtil.nullToInteger(child.getAttribute()));
							fieldVo.setContent(imageMap);
						}else if(StringUtil.compareObject(child.getType(), 2)) {
							fieldVo.setField_type(Constants.FiledType.IMAGE_THEME_TYPE);
							Map <String,Object> imageMap = new HashMap<String, Object>();
							imageMap.put("nav_list", contentMap);
							imageMap.put("image_type", StringUtil.nullToInteger(child.getAttribute()));
							fieldVo.setContent(imageMap);
						}else if(StringUtil.compareObject(child.getType(), 7)) {
							fieldVo.setField_type(Constants.FiledType.IMAGE_SECKILL_TYPE);
							Map <String,Object> imageMap = new HashMap<String, Object>();
							imageMap.put("nav_list", contentMap);
							imageMap.put("image_type", StringUtil.nullToInteger(child.getAttribute()));
							fieldVo.setContent(imageMap);
						}else if (StringUtil.compareObject(child.getType(), 3)){
							fieldVo.setField_type(Constants.FiledType.GOODS_TYPE);
						}else if(StringUtil.compareObject(child.getType(), 4)) {
							fieldVo.setField_type(Constants.FiledType.IMAGE_MODULE_TYPE);
						}
					}
				} catch (Exception e) {
					continue;
				}
				if (!StringUtil.isNull(fieldVo.getField_type())){
					fieldVoList.add(fieldVo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fieldVoList;
	}
	
	
	/**
	 * 解析fx页面数据中的没个元素
	 * @param childMap
	 * @param child
	 * @param resultMap
	 */
	public static void getFieldMap(Map<String,Object> childMap , FxChildren child,Map<String,Object> resultMap ){
		Map<String,Object> contentMap = new HashMap<String, Object>();
		if(childMap == null || childMap.size() == 0){
			childMap = new HashMap<String,Object>();
		}
		
		try {
			if (StringUtil.compareObject(child.getType(), 1)){
				getImageTypeMap(childMap, contentMap);
				resultMap.put(StringUtil.null2Str(resultMap.size()), contentMap);
			}else if (StringUtil.compareObject(child.getType(), 0)){
				getImageTypeMap(childMap, contentMap);
				
				//单图
				if (StringUtil.compareObject(child.getAttribute(), 1)){
					contentMap.put("image", StringUtil.null2Str(child.getPicture()));
				}
				resultMap.put(StringUtil.null2Str(resultMap.size()), contentMap);
			}else if (StringUtil.compareObject(child.getType(), 2) || StringUtil.compareObject(child.getType(), 5)){
				getImageTypeMap(childMap, contentMap);
				
				//单图
				if (StringUtil.compareObject(child.getAttribute(), 1)){
					contentMap.put("image", StringUtil.null2Str(child.getPicture()));
				}
				resultMap.put(StringUtil.null2Str(resultMap.size()), contentMap);
			}else if (StringUtil.compareObject(child.getType(), 3) || StringUtil.compareObject(child.getType(), 4)){
				//商品类型
				ProductManager productManager = Constants.ctx.getBean(ProductManager.class);
				ProductSpecManager productSpecManager = Constants.ctx.getBean(ProductSpecManager.class);
				
				contentMap.put("size", StringUtil.nullToInteger(child.getAttribute()));
				//类型为模块时新增以下两项数据
				if(StringUtil.compareObject(child.getType(), 4)) {
					contentMap.put("title", StringUtil.null2Str(child.getSpecialName()));
					contentMap.put("picture", StringUtil.null2Str(child.getPicture()));
				}
				
				List<Long> idList = StringUtil.stringToLongArray(StringUtil.null2Str(childMap.get("content")));
				if(idList != null && idList.size() > 0){
					String idStr = StringUtil.longArrayToString(idList);
					List<Product> productList = new ArrayList<Product>();
					
					//按输入的id顺序得到商品
					String sql = String.format("select product_id,name,image,is_spce_product,is_soldout from jkd_product where product_id in(%s) order by field(product_id, %s)", idStr, idStr);
					List<Object[]> objectList = productManager.querySql(sql);
					if(!CollectionUtils.isEmpty(objectList)) {
						for(Object[] object : objectList){
							Product product = new Product ();
							product.setProductId(StringUtil.nullToLong(object[0]));
							product.setName(StringUtil.null2Str(object[1]));
							product.setImage(StringUtil.null2Str(object[2]));
							product.setIsSpceProduct(StringUtil.nullToBoolean((object[3])));
							product.setIsSoldout(StringUtil.nullToBoolean(object[4]));
							productList.add(product);
						}
					}
					
					List<Map <String,Object>> mapList = new LinkedList<Map<String,Object>>();
					for (Product product : productList){
						Double price = StringUtil.nullToDouble(product.getPriceRecommend());
						if(StringUtil.nullToBoolean(product.getIsSpceProduct())) {
							List<ProductSpec> productSpecList = productSpecManager.getProductSpecListByProductId(product.getProductId());
							if(productSpecList != null && productSpecList.size() > 0) {
								Collections.sort(productSpecList, new Comparator<ProductSpec>(){
									public int compare(ProductSpec o1, ProductSpec o2) {
										// 按价格升序排序
										Double priceWholesale1 = StringUtil.nullToDouble(o1.getPriceWholesale());
										Double priceWholesale2 = StringUtil.nullToDouble(o2.getPriceWholesale());
										return priceWholesale1.compareTo(priceWholesale2);
									}
								});
								price = StringUtil.nullToDouble(productSpecList.get(0).getPriceWholesale());
							}
						}
						
						//判断是否有效的商品
						if (product != null ){
							Map <String,Object> productMap = new HashMap<String, Object>();
							productMap.put("id", product.getProductId());
							productMap.put("title", product.getName());
							productMap.put("price", price);
							productMap.put("image", StringUtil.null2Str(product.getImage()));
							mapList.add(productMap);
						}
					}
					if(StringUtil.compareObject(child.getType(), 4)) {
						contentMap.put(Constants.FiledType.IMAGE_MODULE_TYPE, mapList);
					}else {
						contentMap.put(Constants.FiledType.GOODS_TYPE, mapList);
					}
					resultMap.putAll(contentMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导航图片解析
	 * @param childMap
	 * @param contentMap
	 */
	private static void getImageTypeMap(Map<String,Object> childMap , Map<String,Object> contentMap){
		if(childMap == null ){
			childMap = new HashMap<String,Object>();
		}
		
		try{
			for (Entry<String, Object> entry : childMap.entrySet()){
				if (StringUtil.compareObject(entry.getKey(), "navigation_name")){
					//图片标题
					contentMap.put("title", entry.getValue());
				}
				if(StringUtil.compareObject(entry.getKey(), "webUrl")) {
					//h5地址
					contentMap.put("webUrl", StringUtil.null2Str(entry.getValue()));
				}
				if (StringUtil.compareObject(entry.getKey(), "picture")){
					//图片地址
					contentMap.put("image", StringUtil.null2Str(entry.getValue()));
				}
				if (StringUtil.compareObject(entry.getKey(), "content")){
					//图片链接
					contentMap.put("url", entry.getValue());
				}
				
				if (StringUtil.compareObject(entry.getKey(), "target_type")){
					try {
						if (StringUtil.compareObject(entry.getValue(), 0)){
							contentMap.put("prefix", "页面");
							if (childMap.containsKey("content") && !StringUtil.isNull(childMap.get("content"))){
								FxPageManager fxPageManager = Constants.ctx.getBean(FxPageManager.class);
								FxPage page = fxPageManager.get(StringUtil.nullToLong(childMap.get("content")));
								contentMap.put("name", page.getPageName());
							}
						}else if(StringUtil.compareObject(entry.getValue(), 3)) {
							contentMap.put("prefix", "专题");
							if (childMap.containsKey("content") && !StringUtil.isNull(childMap.get("content"))){
								FxPageManager fxPageManager = Constants.ctx.getBean(FxPageManager.class);
								FxPage page = fxPageManager.get(StringUtil.nullToLong(childMap.get("content")));
								contentMap.put("name", page.getPageName());
							}
						}else if(StringUtil.compareObject(entry.getValue(), 4)){
							contentMap.put("prefix", "礼包");
							if (childMap.containsKey("content") && !StringUtil.isNull(childMap.get("content"))){
								ProductManager productWholesaleManager = Constants.ctx.getBean(ProductManager.class);
								Product wholesale = productWholesaleManager.get(StringUtil.nullToLong(childMap.get("content")));
								contentMap.put("name", wholesale.getName());
							}
						}else if(StringUtil.compareObject(entry.getValue(), 5)) {
							contentMap.put("prefix", "分类");
							if (childMap.containsKey("content") && !StringUtil.isNull(childMap.get("content"))){
								ProductCategoryManager productCategoryManager = Constants.ctx.getBean(ProductCategoryManager.class);
								ProductCategory productCategory= productCategoryManager.get(StringUtil.nullToLong(childMap.get("content")));
								contentMap.put("name", productCategory.getName());
							}
						}else if(StringUtil.compareObject(entry.getValue(), 6)) {
							contentMap.put("prefix", "奖励中心");
							contentMap.put("name", " ");
						}else if(StringUtil.compareObject(entry.getValue(), 7)){
							contentMap.put("prefix", "小程序");
							contentMap.put("name", " ");
						}else if(StringUtil.compareObject(entry.getValue(), 8)){
							contentMap.put("prefix", "H5");
							if (childMap.containsKey("content") && !StringUtil.isNull(childMap.get("content"))){
								WebUrlConfigManager webUrlConfigManager = Constants.ctx.getBean(WebUrlConfigManager.class);
								WebUrlConfig webUrlConfig= webUrlConfigManager.get(StringUtil.nullToLong(childMap.get("content")));
								contentMap.put("name", webUrlConfig.getName());
							}
						}else if(StringUtil.compareObject(entry.getValue(), 13)){
							contentMap.put("prefix", "品牌详情");
							if (childMap.containsKey("content") && !StringUtil.isNull(childMap.get("content"))){
						        ProductBrandManager productBrandManager = Constants.ctx.getBean(ProductBrandManager.class);
						        ProductBrand productBrand= productBrandManager.get(StringUtil.nullToLong(childMap.get("content")));
								contentMap.put("name", productBrand.getName());
							}
						}else if(StringUtil.compareObject(entry.getValue(), 14)){
							contentMap.put("prefix", "邀请有礼");
							contentMap.put("name", " ");
						}else {
							contentMap.put("prefix", "商品");
							if (childMap.containsKey("content") && !StringUtil.isNull(childMap.get("content"))){
								ProductManager productWholesaleManager = Constants.ctx.getBean(ProductManager.class);
								Product wholesale = productWholesaleManager.get(StringUtil.nullToLong(childMap.get("content")));
								contentMap.put("name", wholesale.getName());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 检查图片、链接是否为空
	 * @param childrenList
	 * @param data
	 */
	private static List<FxChildren> checkData(List<FxChildren> childrenList,String data){
		    FxChildren errorChildren = new FxChildren();
			errorChildren.setErrorCode(0);
			errorChildren.setReason(data);
			childrenList.clear();
			childrenList.add(errorChildren);
			return childrenList;
	}
	
}

//package com.didispace;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import com.chunruo.core.util.FileIO;
//import com.chunruo.core.util.StringUtil;
//import com.chunruo.core.util.pherialize.MixedArray;
//import com.chunruo.core.util.pherialize.Pherialize;
//
//public class Test {
//
//	public static void main(String[] args) {
//		
//		String test = FileIO.readFile("D://test.txt");
//		System.out.println(test);
//		JSONArray array = new JSONArray(test);
//		//JSONObject json = new JSONObject(test);
//		if (array != null && array.length() > 0){
//			for (int i = 0; i < array.length(); i++ ){
//				JSONObject obj = array.getJSONObject(i);
//				Map<String,Object> mixedArray = new HashMap<String,Object>();
//				switch(obj.getString("type")){
//					case "title": 
//						mixedArray.put("title", obj.getString("title"));
//						mixedArray.put("sub_title", obj.getString("sub_title"));
//						mixedArray.put("sub_title", obj.getString("sub_title"));
//						mixedArray.put("show_method", obj.getString("show_method"));
//						mixedArray.put("bgcolor", obj.getString("bgcolor"));
//						//标题
//						//htmlBuffer.append(HtmlParseUtil.getTitle(mixedArray));
//						break;
//					case "rich_text":
//						mixedArray.put("screen", obj.getString("screen"));
//						mixedArray.put("content", obj.getString("content"));
//						mixedArray.put("bgcolor", obj.getString("bgcolor"));
//						//富文本内容区域
//						//htmlBuffer.append(HtmlParseUtil.getRichText(mixedArray));
//						break;
//					case "notice":
//						mixedArray.put("content", obj.getString("content"));
//						System.out.println(StringUtil.objectToJSON(mixedArray));
//						//店铺公告
//						//htmlBuffer.append(HtmlParseUtil.getNotice(mixedArray));
//						break;
//					case "line":
//						//辅助线
//						//htmlBuffer.append(HtmlParseUtil.getLine());
//						break;
//					case "white":
//						mixedArray.put("left", obj.get("left"));
//						mixedArray.put("height", obj.get("height"));
//						//辅助空白
//						//htmlBuffer.append(HtmlParseUtil.getWhite(mixedArray));
//						break;
//					case "search":
//						break;
//					case "attention_collect":
//						break;
//					case "store":
//						break;
//					case "text_nav":
//						Iterator iterator = obj.keys();
//						while(iterator.hasNext()){
//				            String key = (String) iterator.next();
//				            if (!StringUtil.compareObject("type", key)){
//				            	try {
//				            		JSONObject childObj = obj.getJSONObject(key);
//				            		Map<String,Object> childArray = new HashMap<String,Object>();
//				            		childArray.put("title", childObj.getString("title"));
//				            		childArray.put("prefix", childObj.getString("prefix"));
//				            		childArray.put("name", childObj.getString("name"));
//				            		childArray.put("url", childObj.getString("url"));
//				            		mixedArray.put(key, childArray);
//								} catch (Exception e) {
//									continue;
//								}
//				            	
//				            }
//						}
//						break;
//					case "image_nav":
//						Iterator iterator1 = obj.keys();
//						while(iterator1.hasNext()){
//				            String key = (String) iterator1.next();
//				            if (!StringUtil.compareObject("type", key)){
//				            	try {
//				            		JSONObject childObj = obj.getJSONObject(key);
//				            		Map<String,Object> childArray = new HashMap<String,Object>();
//				            		childArray.put("title", childObj.getString("title"));
//				            		childArray.put("prefix", childObj.getString("prefix"));
//				            		childArray.put("name", childObj.getString("name"));
//				            		childArray.put("image", childObj.getString("image"));
//				            		mixedArray.put(key, childArray);
//								} catch (Exception e) {
//									continue;
//								}
//				            	
//				            }
//						}
//						//图片导航
//						//htmlBuffer.append(HtmlParseUtil.getImageNav(mixedArray, storeCustomField));
//						break;
//					case "component":
//						//自定义模块
//						//htmlBuffer.append(HtmlParseUtil.getComponent(mixedArray, storeCustomField));
//						break;
//					case "link":
//						//关联链接
//						//htmlBuffer.append(HtmlParseUtil.getLink(mixedArray, storeCustomField));
//						break;
//					case "image_ad":
//						mixedArray.put("max_width", obj.get("max_width"));
//						mixedArray.put("image_size", obj.getString("image_size"));
//						mixedArray.put("image_type", obj.getString("image_type"));
//						mixedArray.put("max_height", obj.get("max_height"));
//						if (obj.has("nav_list")){
//							JSONObject navListObj = obj.getJSONObject("nav_list");
//							Iterator iteratorNav = navListObj.keys();
//							while(iteratorNav.hasNext()){
//					            String key = (String) iteratorNav.next();
//					            if (!StringUtil.compareObject("type", key)){
//					            	try {
//					            		JSONObject childObj = navListObj.getJSONObject(key);
//					            		Map<String,Object> childArray = new HashMap<String,Object>();
//					            		childArray.put("title", childObj.getString("title"));
//					            		childArray.put("prefix", childObj.getString("prefix"));
//					            		childArray.put("name", childObj.getString("name"));
//					            		childArray.put("image", childObj.getString("image"));
//					            		mixedArray.put(key, childArray);
//									} catch (Exception e) {
//										continue;
//									}
//					            }
//							}
//							//System.out.println(StringUtil.objectToJSON());
//						}
//						//图片广告
//						//htmlBuffer.append(HtmlParseUtil.getImageAd(mixedArray, storeCustomField));
//						break;
//					case "goods":
//						mixedArray.put("size", obj.getString("size"));
//						mixedArray.put("size_type", obj.getString("size_type"));
//						mixedArray.put("buy_btn", obj.getString("buy_btn"));
//						mixedArray.put("buy_btn_type", obj.getString("buy_btn_type"));
//						mixedArray.put("show_title", obj.getString("show_title"));
//						mixedArray.put("price", obj.getString("price"));
//						String googsString = "";
//						if (obj.has("goods")){
//							JSONObject goodsObj = obj.getJSONObject("goods");
//							Iterator iteratorGoods = goodsObj.keys();
//							while(iteratorGoods.hasNext()){
//								 String key = (String) iteratorGoods.next();
//								 JSONObject childObj = goodsObj.getJSONObject(key);
//								 googsString += "," + childObj.get("id");
//							}
//						}
//						if (googsString.length() > 1)
//							googsString = googsString.substring(1);//去除开头的‘,’
//						mixedArray.put("goods", googsString);
//						
//						//商品
//						//htmlBuffer.append(HtmlParseUtil.getGoods(mixedArray));
//						break;
//					case "tpl_shop":
//						//logo抬头
//						//htmlBuffer.append(HtmlParseUtil.getTplShop_1(mixedArray));
//						break;
//					case "tpl_shop1":
//						//logo抬头
//						//htmlBuffer.append(HtmlParseUtil.getTplShop_2(mixedArray));
//						break;
//					case "goods_group1":
//						//商品分组1
//						//htmlBuffer.append(HtmlParseUtil.getGoodsGroup_1(mixedArray));
//						break;
//					case "goods_group2":
//						//商品分组2
//						//htmlBuffer.append(HtmlParseUtil.getGoodsGroup_2(mixedArray));
//						break;
//				}
//				System.out.println(obj.getString("type"));
//				System.out.println(StringUtil.objectToJSON(mixedArray));
//				System.out.println(Pherialize.serialize(mixedArray));;
//			}
//		}
//	}
//}

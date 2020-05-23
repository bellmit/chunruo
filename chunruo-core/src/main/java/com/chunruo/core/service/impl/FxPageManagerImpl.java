package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.FxChildren;
import com.chunruo.core.model.FxPage;
import com.chunruo.core.model.Product;
import com.chunruo.core.repository.FxChildrenRepository;
import com.chunruo.core.repository.FxPageRepository;
import com.chunruo.core.repository.ProductRepository;
import com.chunruo.core.service.FxChannelManager;
import com.chunruo.core.service.FxPageManager;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("fxPageManager")
public class FxPageManagerImpl extends GenericManagerImpl<FxPage, Long> implements FxPageManager {
	private FxPageRepository fxPageRepository;
	@Autowired
	private FxChildrenRepository fxChildrenRepository;
	@Autowired
	private FxChannelManager fxChannelManager;
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	public FxPageManagerImpl(FxPageRepository fxPageRepository) {
		super(fxPageRepository);
		this.fxPageRepository = fxPageRepository;
	}

	@Override
	public List<FxPage> getFxPageListByChannelId(Long channelId) {
		return this.fxPageRepository.getFxPageListByChannelId(channelId);
	}

	@Override
	public List<FxPage> getFxPageListByChannelId(Long channelId, Integer category) {
		return this.fxPageRepository.getFxPageListByChannelId(channelId, category);
	}
	
	@Override
	public FxPage saveFxPage(FxPage fxPage){
		fxPage = this.save(fxPage);
		this.fxChannelManager.updateFxChannelUpdateTimeByChannelId(fxPage.getChannelId());
		return fxPage;
	}

	@Override
	public String getImages(Long pageId) {
		List<FxChildren> list = fxChildrenRepository.getFxChildrenListByPageId(pageId);
		String table = "<table ><tr> <td colspan=\"2\"  style=\"width:200px; height: 80px;text-align: center\"> "
				+ "新增模块: <select id=\"select_val\"> <option value=\"1\">Banner-多张轮播</option> "
				+ "<option value=\"2\">Banner-单张宽</option> <option value=\"3\">Banner-单张窄</option> "
				+ "<option value=\"4\">Navigation-导航一行</option> <option value=\"5\">Navigation-导航两行</option>"
				+ "<option value=\"8\">Product-商品一行一个</option> <option value=\"9\">Product-商品一行两个</option>"
				+ " </select><br>  专题名称: <input type=\"text\" name=\"sName\" id=\"sName\" /><br> "
				+ "<input type=\"button\" value=\"确认\" onclick=\"creatModular(" + pageId + ")\"> "
				+ "<input type=\"button\" value=\"取消\" onclick=\"clean()\">  </tr>";
		if (null != list && list.size() > 0) {
			for (FxChildren fxChildren : list) {
				List<Map<String, String>> mapList = new ArrayList<>();
				if (!StringUtil.isNullStr(fxChildren.getContents())) {
					mapList = StringUtil.jsonToHashMapList(fxChildren.getContents());
				}
				String path = "http://www.jikeduo.com.cn/depository/";
				if(fxChildren.getType() == 3){
					path = "http://www.jikeduo.com.cn/upload/";
				}
				// banner
				if (fxChildren.getType().equals(0)) {
					/// banner多图轮播
					if (fxChildren.getAttribute().equals(0)) {
						if (mapList.size() > 0) {
							String imgList = "";

							for (int i = 0; i < mapList.size(); i++) {
								imgList = imgList + "<img src=\"" + path + mapList.get(i).get("picture")
										+ "\" width=\"200\" height=\"100\" ondblclick=\"deleteImageFromModular("
										+ fxChildren.getChildrenId() + "," + i + ")\"/>";
							}
							String banner = "<tr><td width=\"200\" height=\"" + 100 * mapList.size()
									+ "\"/>[IMAGElIST]</td><td  width=\"150\" height=\"100\" style=\"text-align: center;\"><a onclick=\"up("
									+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\">向上</a> "
									+ "<a onclick=\"down(" + fxChildren.getChildrenId()
									+ ")\" style=\"cursor:pointer\">向下</a> <br> <a style=\"cursor:pointer\" onclick=\"getImageListByType(1,"
									+ fxChildren.getChildrenId() + ")\">编辑</a> "
									+ " <a style=\"cursor:pointer\" onclick=\"jumpSetting("+ fxChildren.getChildrenId() +")\">跳转</a>"
											+ " <a onclick=\"deleteModular(" + fxChildren.getChildrenId()
									+ ")\" style=\"cursor:pointer\">删除</a></td> </tr>";
							table = table + banner.replace("[IMAGElIST]", imgList);
						} else {
							String banner = "<tr><td width=\"200\" height=\"100\"></td><td  width=\"150\" height=\"100\" style=\"text-align: center;\"> "
									+ "<a style=\"cursor:pointer\" onclick=\"getImageListByType(1,"
									+ fxChildren.getChildrenId() + ")\">编辑</a> " + " <a onclick=\"deleteModular("
									+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\"> 删除</a></td> </tr>";
							table = table + banner;
						}

					}
					// banner单张宽、窄
					if (fxChildren.getAttribute().equals(1) || fxChildren.getAttribute().equals(2)) {
						int type =  2;
						if(fxChildren.getAttribute() == 2){
							type =  3;
						}
						
						if (mapList.size() > 0) {
							String banner = "<tr><td width=\"200\" height=\"100\"><img src=\"" + path
									+ fxChildren.getPicture()
									+ "\" width=\"200\" height=\"100\" ondblclick=\"deleteImageFromModular("
									+ fxChildren.getChildrenId() + ",0)\"/></td>"
									+ "<td  width=\"150\" height=\"100\" style=\"text-align: center;\"><a onclick=\"up("
									+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\">向上</a> "
									+ "<a onclick=\"down(" + fxChildren.getChildrenId()
									+ ")\" style=\"cursor:pointer\">向下</a><br><a style=\"cursor:pointer\" onclick=\"getImageListByType(" + type + ","
									+ fxChildren.getChildrenId() + ")\">编辑</a> "
									+ " <a style=\"cursor:pointer\" onclick=\"jumpSetting("+ fxChildren.getChildrenId() +")\">跳转</a> <a onclick=\"deleteModular(" + fxChildren.getChildrenId()
									+ ")\" style=\"cursor:pointer\"> 删除</a></td> </tr>";
							table = table + banner;
						} else {
							String banner = "<tr><td width=\"200\" height=\"100\"></td><td  width=\"150\" height=\"100\" style=\"text-align: center;\"> "
									+ "<a style=\"cursor:pointer\" onclick=\"getImageListByType(" + type + ","
									+ fxChildren.getChildrenId() + ")\">编辑</a> " + " <a onclick=\"deleteModular("
									+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\"> 删除</a></td> </tr>";
							table = table + banner;
						}

					}
				}
				// 导航
				if (fxChildren.getType().equals(1)) {
					int type =  4;
					if(fxChildren.getAttribute() == 1){
						type =  5;
					}
					String navStr = "<tr>";
					if (mapList.size() > 4) {
						navStr = navStr + "<td width=\"200\" height=\"160\">";
					} else {
						navStr = navStr + "<td width=\"200\" height=\"80\">";
					}
					String img = "";
					if (mapList.size() > 0) {
						for (int i = 0; i < mapList.size(); i++) {

							img = img + "<div  style=\"float: left;width: 50px;height: 80px\">"
									+ "<div style=\"margin-top:0px ; margin-left:0px;width: 50px;height: 50px\">"
									+ "<img src=\"" + path + mapList.get(i).get("picture")
									+ "\" width=\"50\" height=\"50\" ondblclick=\"deleteImageFromModular("
									+ fxChildren.getChildrenId() + "," + i + ")\"/></div>"
									+ "<div style=\"margin-top:0px ; margin-left:0px;width: 50px;height: 50px; text-align:center\">"
									+ mapList.get(i).get("navigation_name") + "</div></div>";

						}
						table = table + navStr + img
								+ "</td><td  width=\"150\" height=\"100\" style=\"text-align: center;\"><a onclick=\"up("
								+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\">向上</a>"
								+ " <a onclick=\"down(" + fxChildren.getChildrenId()
								+ ")\" style=\"cursor:pointer\">向下</a> <br> <a style=\"cursor:pointer\"  onclick=\"getImageListByType(" + type + ","
								+ fxChildren.getChildrenId() + ")\">编辑</a> <a style=\"cursor:pointer\" onclick=\"jumpSetting("+ fxChildren.getChildrenId() +")\">跳转</a> " + " <a onclick=\"deleteModular("
								+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\"> 删除</a></td></tr>";

					} else {
						table = table
								+ "<tr><td width=\"200\" height=\"80\"></td><td style=\"text-align: center;\"> <a style=\"cursor:pointer\"  onclick=\"getImageListByType(" + type + ","
								+ fxChildren.getChildrenId() + ")\">编辑</a> " + " <a onclick=\"deleteModular("
								+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\"> 删除</a></td></tr>";
					}

				}
				// 专题
				// if (fxChildren.getType().equals(2)) {
				// if(mapList.size() > 0){
				// if (fxChildren.getAttribute().equals(0)) {
				// String subject = " <img src=\"" + path +
				// mapList.get(0).get("picture")
				// + "\" width=\"195\" height=\"100\"/>";
				// table = table + subject;
				// }
				// if (fxChildren.getAttribute().equals(1)) {
				// String subject = " <img src=\"" + path +
				// mapList.get(0).get("picture")
				// + "\" width=\"95\" height=\"100\"/><img src=\"" +
				// mapList.get(1).get("picture")
				// + "\" width=\"95\" height=\"100\"/>";
				// table = table + subject;
				// }
				// }else{
				//
				// }
				//
				// }
				// 商品
				if (fxChildren.getType().equals(3)) {
					if (mapList.size() > 0) {
						List<Long> productIds = new ArrayList<>();
						for (Map<String, String> map : mapList) {
							productIds.addAll(StringUtil.stringToLongArray(map.get("content")));
						}
						//商品为空，返回空白行
						if (null == productIds || productIds.size() == 0) {
							table = table
									+ "<tr> <td width=\"200\" height=\"100\"></td><td style=\"text-align: center;\"> <a style=\"cursor:pointer\" onclick=\"getImageListByType(6,"
									+ fxChildren.getChildrenId() + ")\">编辑</a><a onclick=\"deleteModular("
									+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\"> 删除</a>  </td></tr>";
							
						}else{
							List<Product> plist = productRepository.getByIdList(productIds);
							List<Product> productList = new ArrayList<>();
							for (Product product : plist) {
								if (!product.getIsSoldout()) {
									productList.add(product);
								}
							}
							String pro = "";
							// 一行一个
							if (fxChildren.getAttribute().equals(0)) {
								for (int i = 0; i < productList.size(); i++) {
									// for (ProductWholesale product : productList)
									// {

									String pInfo = " <tr> <td width=\"200\" height=\"100\"><img src=\"" + path
											+ productList.get(i).getImage()
											+ "\" width=\"100\" height=\"100\" ondblclick=\"deleteImageFromModular("
											+ fxChildren.getChildrenId() + "," + productList.get(i).getProductId()
											+ ")\"/>"
											+ "<div style=\" float:right;width:95px;word-break:break-all;border:0px\"> "
											+ productList.get(i).getName() + "</div></td>";
									if (i == 0) {
										pInfo = pInfo
												+ "<td  width=\"150\" height=\"100\" style=\"text-align: center;\"><a onclick=\"up("
												+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\">向上</a>"
												+ " <a onclick=\"down(" + fxChildren.getChildrenId()
												+ ")\" style=\"cursor:pointer\">向下</a> <br> <a style=\"cursor:pointer\"  onclick=\"getImageListByType(6,"
												+ fxChildren.getChildrenId() + ")\">编辑</a>" + " <a onclick=\"deleteModular("
												+ fxChildren.getChildrenId()
												+ ")\" style=\"cursor:pointer\"> 删除</a></td></tr>";
									} else {
										pInfo = pInfo + "<td  width=\"150\" height=\"100\"></td></tr>";
									}
									pro = pro + pInfo;
								}
								table = table + pro;
							}
							// 一行两个
							if (fxChildren.getAttribute().equals(1)) {

								int remainder = productList.size() % 2;

								for (int i = 0; i < productList.size(); i = i + 2) {

									if (remainder == 0) {
										table = table + "<tr><td width=\"200\" height=\"150\"><img src=\"" + path
												+ productList.get(i).getImage()
												+ "\" width=\"95\" height=\"100\" ondblclick=\"deleteImageFromModular("
												+ fxChildren.getChildrenId() + "," + productList.get(i).getProductId()
												+ ")\"/><img src=\"" + path + productList.get(i + 1).getImage()
												+ "\" width=\"95\" height=\"100\" ondblclick=\"deleteImageFromModular("
												+ fxChildren.getChildrenId() + "," + productList.get(i + 1).getProductId()
												+ ")\"/><div style=\" float:left;width:95px;word-break:break-all;border:0px\"> "
												+ productList.get(i).getName()
												+ "</div><div style=\" float:right;width:95px;word-break:break-all;border:0px\">"
												+ productList.get(i + 1).getName() + "</div></td>";
										if (i == 0) {
											table = table
													+ "<td  width=\"150\" height=\"100\" style=\"text-align: center;\"><a onclick=\"up("
													+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\">向上</a>"
													+ " <a onclick=\"down(" + fxChildren.getChildrenId()
													+ ")\" style=\"cursor:pointer\">向下</a><br> <a style=\"cursor:pointer\" onclick=\"getImageListByType(6,"
													+ fxChildren.getChildrenId() + ")\">编辑</a>"
													+ "  <a onclick=\"deleteModular(" + fxChildren.getChildrenId()
													+ ")\" style=\"cursor:pointer\"> 删除</a></td></tr>";
										} else {
											table = table + "<td  width=\"150\" height=\"100\"></td></tr>";
										}
									} else {
										if (i < productList.size() - 1) {
											table = table + "<tr><td width=\"200\" height=\"150\"><img src=\"" + path
													+ productList.get(i).getImage()
													+ "\" width=\"95\" height=\"100\" ondblclick=\"deleteImageFromModular("
													+ fxChildren.getChildrenId() + "," + productList.get(i).getProductId()
													+ ")\"/><img src=\"" + path + productList.get(i + 1).getImage()
													+ "\" width=\"95\" height=\"100\" ondblclick=\"deleteImageFromModular("
													+ fxChildren.getChildrenId() + ","
													+ productList.get(i + 1).getProductId()
													+ ")\"/><div style=\" float:left;width:95px;word-break:break-all;border:0px\"> "
													+ productList.get(i).getName()
													+ "</div><div style=\" float:right;width:95px;word-break:break-all;border:0px\">"
													+ productList.get(i).getName() + "</div></td>";
											if (i == 0) {
												table = table
														+ "<td  width=\"150\" height=\"100\" style=\"text-align: center;\"><a onclick=\"up("
														+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\">向上</a>"
														+ " <a onclick=\"down(" + fxChildren.getChildrenId()
														+ ")\" style=\"cursor:pointer\">向下</a> <br>"
														+ "<a style=\"cursor:pointer\" onclick=\"getImageListByType(6,"
														+ fxChildren.getChildrenId()
														+ ")\">编辑</a>  <a onclick=\"deleteModular("
														+ fxChildren.getChildrenId()
														+ ")\" style=\"cursor:pointer\"> 删除</a></td></tr>";
											} else {
												table = table + "<td  width=\"150\" height=\"100\"></td></tr>";
											}
										} else {
											table = table + "<tr><td width=\"200\" height=\"150\"><img src=\"" + path
													+ productList.get(i).getImage()
													+ "\"  style=\"margin-top:0px ; margin-right:50%;width: 95px;height: 95px\"  "
													+ "ondblclick=\"deleteImageFromModular(" + fxChildren.getChildrenId()
													+ "," + productList.get(i).getProductId() + ")\"/>"
													+ " <div style=\"margin-top:0px ; margin-left:0px; float:left;width:95px;word-break:break-all;border:0px\">"
													+ productList.get(i).getName()
													+ "</div></td> <td  width=\"150\" height=\"100\"></td></tr>";
										}

									}

								}

							}
						}
						
					} else {
						table = table
								+ "<tr> <td width=\"200\" height=\"100\"></td><td style=\"text-align: center;\"> <a style=\"cursor:pointer\" onclick=\"getImageListByType(6,"
								+ fxChildren.getChildrenId() + ")\">编辑</a><a onclick=\"deleteModular("
								+ fxChildren.getChildrenId() + ")\" style=\"cursor:pointer\"> 删除</a>  </td></tr>";
					}

				}
			}
		}

		table = table + "</table>";
		return table;
	}

	@Override
	public void deletePageByPageIdList(List<Long> pageIdList) {
		if(pageIdList != null && pageIdList.size() > 0){
			this.fxPageRepository.deleteByIdList(pageIdList);
			this.fxChildrenRepository.deleteFxChildrenByPageIdList(pageIdList);
		}
	}

	@Override
	public List<FxPage> getFxPageListByChannelIdList(List<Long> channelIdList,Integer category) {
		return this.fxPageRepository.getFxPageListByChannelIdList(channelIdList,category);
	}

	@Override
	public List<FxPage> getInnerFxPageList() {
		return this.fxPageRepository.getInnerFxPageList();
	}

}

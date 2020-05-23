package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.chunruo.core.Constants;
import com.chunruo.core.model.FxChildren;
import com.chunruo.core.model.FxPage;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductBrand;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.ProductTask;
import com.chunruo.core.model.WebUrlConfig;
import com.chunruo.core.service.FxChildrenManager;
import com.chunruo.core.service.FxPageManager;
import com.chunruo.core.service.ProductBrandManager;
import com.chunruo.core.service.ProductCategoryManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.ProductSpecManager;
import com.chunruo.core.service.ProductTaskManager;
import com.chunruo.core.service.WebUrlConfigManager;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.SplitPageUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.util.ChannelUtil;
import com.chunruo.webapp.vo.FxChildrenFieldVo;
import com.chunruo.webapp.vo.FxPageVo;
import com.chunruo.webapp.vo.PageObjcet;
import com.chunruo.webapp.vo.ProductWholesaleVo;

/**
 * 模板数据加载
 * @param request
 * @return
 */

@Controller
@RequestMapping("/widget/")
public class WidgetController {
	@Autowired
	private ProductManager productManager;
	@Autowired
	private FxPageManager fxPageManager;
	@Autowired
	private FxChildrenManager fxChildrenManager;
	@Autowired
	private ProductBrandManager productBrandManager;
	@Autowired
	private ProductSpecManager productSpecManager;
	@Autowired
	private ProductTaskManager productTaskManager;
	@Autowired
	private WebUrlConfigManager webUrlConfigManager;
	@Autowired
	private ProductCategoryManager productCategoryManager;

	/**
	 * 产品列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getProductList")
	public @ResponseBody Map<String, Object> getProductList(final HttpServletRequest request ,final HttpServletResponse response) {
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		String keyword = request.getParameter("keyword");//关键字
		Integer currentPage = StringUtil.nullToInteger(request.getParameter("currentPage"));//当前页
		Integer currentLimit = StringUtil.nullToInteger(request.getParameter("limit"));//每页数据条数
    	
    	
    	//返回分页对象
		int maxPageNumber = 0;
    	PageObjcet<ProductWholesaleVo> page = null;
    	//市场商品返回列表  用户返回给前端解析
    	List<ProductWholesaleVo> productVoList = new ArrayList<ProductWholesaleVo>();
    	
    	try {
    		page = new PageObjcet<ProductWholesaleVo> ();
    		StringBuffer sqlBuffer = new StringBuffer ();
    		sqlBuffer.append("select distinct(w.product_id) from jkd_product w left join jkd_product_spec p on w.product_id = p.product_id ");
    		sqlBuffer.append("where w.status = 1 and w.is_soldout = 0 ");
    		if(StringUtil.isNumber(keyword)){
    			sqlBuffer.append(String.format("and product_id = %s ", StringUtil.nullToLong(keyword)));
    		}else if(!StringUtil.isNull(keyword)) {
    			sqlBuffer.append("and upper(w.name) like '%" + StringUtil.null2Str(keyword).toUpperCase() + "%' ");
    		}
    		sqlBuffer.append("and ((w.is_spce_product = 0 and w.is_group_product = 0 and w.stock_number > 0) or (w.is_spce_product = 1 and p.stock_number > 0) or (w.is_group_product = 1)) "); 
    		sqlBuffer.append("order by w.update_time desc");
    		
    		List<Long> idList = new ArrayList<Long> ();
    		List<Object[]> objectList = this.productManager.querySql(sqlBuffer.toString());
    		if(objectList != null && objectList.size() > 0) {
    			List<Long> allWholsaleIdList = new ArrayList<Long> ();
    			for(int i=0;i<objectList.size();i++) {
    				allWholsaleIdList.add(StringUtil.nullToLong(objectList.get(i)));
    			}
    			
    			maxPageNumber = SplitPageUtil.getMaxPage(allWholsaleIdList.size(), currentLimit);
        		long start = SplitPageUtil.getStart(currentPage, currentLimit);
    			long limit = SplitPageUtil.getEnd(currentPage, currentLimit);
    		
    			page.setLimit(StringUtil.nullToLong(currentLimit));
        		//设置当前页
            	page.setCurrentPage(currentPage);
            	page.getStart();
            	//总数
        		page.setTotal(StringUtil.nullToLong(allWholsaleIdList.size()));
    			for (int i = 0; i < allWholsaleIdList.size(); i++) {
    				if (i >= start && i <= limit) {
    					idList.add(allWholsaleIdList.get(i));
    				} else if (i > limit) {
    					break;
    				}
    			}
    		}
    		
    		List<Product> productList = this.productManager.getByIdList(idList);
    		if (productList != null && productList.size() >0){
    			for (Product product : productList){
					Double price = StringUtil.nullToDouble(product.getPriceRecommend());
					//判断是否是多规格商品
    				if(StringUtil.nullToBoolean(product.getIsSpceProduct())) {
						List<ProductSpec> productSpecList = this.productSpecManager.getProductSpecListByProductId(product.getProductId());
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
    				
    				ProductWholesaleVo productVo = new ProductWholesaleVo();
    				productVo.setCreateTime(DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, product.getCreateTime()));
    				productVo.setImage(product.getImage());
    				productVo.setName(product.getName());
    				productVo.setPrice(price);
    				productVo.setPriceCost(product.getPriceCost());
    				productVo.setPriceRecommend(product.getPriceRecommend());
    				productVo.setProductCode(product.getProductCode());
//    				productVo.setProfit(product.getProfit());
    				productVo.setQuantity(product.getStockNumber());
    				productVo.setProductId(product.getProductId());
    				productVoList.add(productVo);
    			}
    		}
    		
    	
    		//分页结果列表
    		page.setPageList(productVoList);
    		//分页 页面列表
    		page.setPageIndexList();
    		//查询条件
      //  	page.setQueryString(queryString);
        	resultMap.put("code", 1);
			resultMap.put("page", page);
			return resultMap;
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	resultMap.put("msg", "没有查询到对应的数据");
		return resultMap;
    }
	
	
	/**
	 * 大礼包
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getProductPackage")
	public @ResponseBody Map<String, Object> getProductPackage(final HttpServletRequest request ,final HttpServletResponse response) {
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		
    	
    	try {
    		Long packageId = StringUtil.nullToLong(Constants.conf.getProperty("jkd.invite.product.id"));
        	Product productPackage = this.productManager.get(packageId);
        	resultMap.put("code", 1);
			resultMap.put("page", productPackage);
			return resultMap;
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	resultMap.put("msg", "没有查询到对应的数据");
		return resultMap;
    }
	
	
	/**
	 * 商品分类
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getProductCategoryList")
	public @ResponseBody Map<String, Object> getProductCategoryList(final HttpServletRequest request ,final HttpServletResponse response) {
		
		String keyword = request.getParameter("keyword");//关键字
		Integer currentPage = StringUtil.nullToInteger(request.getParameter("currentPage"));//当前页
		Integer currentLimit = StringUtil.nullToInteger(request.getParameter("limit"));//每页数据条数
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		
    	
    	try {
    		//返回分页对象
    		int maxPageNumber = 0;
    		int start = SplitPageUtil.getStart(currentPage, currentLimit);
			int limit = SplitPageUtil.getEnd(currentPage, currentLimit);
    		PageObjcet<ProductCategory> page = new PageObjcet<ProductCategory> ();
    		Map<String, Object> paramMap = new HashMap<String, Object>();
    		paramMap.put("level", 2);
    		paramMap.put("status", 1);
    		Long count = this.productCategoryManager.countHql(paramMap);
    		List<ProductCategory> productCategoryList = this.productCategoryManager.getHqlPages(paramMap, start, limit,"createTime","desc");
            if(productCategoryList != null && productCategoryList.size() > 0) {
            	maxPageNumber = SplitPageUtil.getMaxPage(productCategoryList.size(), currentLimit);
            }
		
			page.setLimit(StringUtil.nullToLong(currentLimit));
    		//设置当前页
        	page.setCurrentPage(currentPage);
        	page.getStart();
        	//总数
    		page.setTotal(StringUtil.nullToLong(count));
    	
    		//分页结果列表
    		page.setPageList(productCategoryList);
    		//分页 页面列表
    		page.setPageIndexList();
    		resultMap.put("code", 1);
    		resultMap.put("page", page);
			return resultMap;
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	resultMap.put("msg", "没有查询到对应的数据");
		return resultMap;
    }
	
	/**
	 * h5列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getWebUrlList")
	public @ResponseBody Map<String, Object> getWebUrlList(final HttpServletRequest request ,final HttpServletResponse response) {
		
		String keyword = request.getParameter("keyword");//关键字
		Integer currentPage = StringUtil.nullToInteger(request.getParameter("currentPage"));//当前页
		Integer currentLimit = StringUtil.nullToInteger(request.getParameter("limit"));//每页数据条数
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		
    	
    	try {
    		//返回分页对象
    		int maxPageNumber = 0;
    		int start = SplitPageUtil.getStart(currentPage, currentLimit);
			int limit = SplitPageUtil.getEnd(currentPage, currentLimit);
    		PageObjcet<WebUrlConfig> page = new PageObjcet<WebUrlConfig> ();
    		Map<String, Object> paramMap = new HashMap<String, Object>();
    		paramMap.put("isEnable", true);
    		Long count = this.webUrlConfigManager.countHql(paramMap);
    		List<WebUrlConfig> webUrlConfigList = this.webUrlConfigManager.getHqlPages(paramMap, start, limit,"createTime","desc");
            if(webUrlConfigList != null && webUrlConfigList.size() > 0) {
            	maxPageNumber = SplitPageUtil.getMaxPage(webUrlConfigList.size(), currentLimit);
            }
		
			page.setLimit(StringUtil.nullToLong(currentLimit));
    		//设置当前页
        	page.setCurrentPage(currentPage);
        	page.getStart();
        	//总数
    		page.setTotal(StringUtil.nullToLong(count));
    	
    		//分页结果列表
    		page.setPageList(webUrlConfigList);
    		//分页 页面列表
    		page.setPageIndexList();
    		resultMap.put("code", 1);
    		resultMap.put("page", page);
			return resultMap;
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	resultMap.put("msg", "没有查询到对应的数据");
		return resultMap;
    }
	
	
	/**
	 * 发现列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getDiscoveryList")
	public @ResponseBody Map<String, Object> getDiscoveryList(final HttpServletRequest request ,final HttpServletResponse response) {
		
		String keyword = request.getParameter("keyword");//关键字
		Integer currentPage = StringUtil.nullToInteger(request.getParameter("currentPage"));//当前页
		Integer currentLimit = StringUtil.nullToInteger(request.getParameter("limit"));//每页数据条数
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		
    	
    	try {
    		
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	resultMap.put("msg", "没有查询到对应的数据");
		return resultMap;
    }
	
	/**
	 * 发现话题标签列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getDiscoveryModuleList")
	public @ResponseBody Map<String, Object> getDiscoveryModuleList(final HttpServletRequest request ,final HttpServletResponse response) {
		
		String keyword = request.getParameter("keyword");//关键字
		Integer currentPage = StringUtil.nullToInteger(request.getParameter("currentPage"));//当前页
		Integer currentLimit = StringUtil.nullToInteger(request.getParameter("limit"));//每页数据条数
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		
    	
    	try {
    		
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	resultMap.put("msg", "没有查询到对应的数据");
		return resultMap;
    }
	
	/**
	 * 发现主体列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getDiscoveryCreaterList")
	public @ResponseBody Map<String, Object> getDiscoveryCreaterList(final HttpServletRequest request ,final HttpServletResponse response) {
		
		String keyword = request.getParameter("keyword");//关键字
		Integer currentPage = StringUtil.nullToInteger(request.getParameter("currentPage"));//当前页
		Integer currentLimit = StringUtil.nullToInteger(request.getParameter("limit"));//每页数据条数
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		
    	
    	try {
    		
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	resultMap.put("msg", "没有查询到对应的数据");
		return resultMap;
    }
	
	
	/**
	 * 商品品牌列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getProductBrandList")
	public @ResponseBody Map<String, Object> getProductBrandList(final HttpServletRequest request ,final HttpServletResponse response) {
		
		String keyword = request.getParameter("keyword");//关键字
		Integer currentPage = StringUtil.nullToInteger(request.getParameter("currentPage"));//当前页
		Integer currentLimit = StringUtil.nullToInteger(request.getParameter("limit"));//每页数据条数
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		
    	
    	try {
    		//返回分页对象
    		int maxPageNumber = 0;
    		int start = SplitPageUtil.getStart(currentPage, currentLimit);
			int limit = SplitPageUtil.getEnd(currentPage, currentLimit);
    		PageObjcet<ProductBrand> page = new PageObjcet<ProductBrand> ();
    		Map<String, Object> paramMap = new HashMap<String, Object>();
    		Long count = this.productBrandManager.countHql(paramMap);
    		List<ProductBrand> productBrandList = this.productBrandManager.getHqlPages(paramMap, start, limit,"createTime","desc");
            if(productBrandList != null && productBrandList.size() > 0) {
            	maxPageNumber = SplitPageUtil.getMaxPage(productBrandList.size(), currentLimit);
            }
		
			page.setLimit(StringUtil.nullToLong(currentLimit));
    		//设置当前页
        	page.setCurrentPage(currentPage);
        	page.getStart();
        	//总数
    		page.setTotal(StringUtil.nullToLong(count));
    	
    		//分页结果列表
    		page.setPageList(productBrandList);
    		//分页 页面列表
    		page.setPageIndexList();
    		resultMap.put("code", 1);
    		resultMap.put("page", page);
			return resultMap;
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	resultMap.put("msg", "没有查询到对应的数据");
		return resultMap;
    }
	
	/**
	 * 大礼包
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getAward")
	public @ResponseBody Map<String, Object> getAward(final HttpServletRequest request ,final HttpServletResponse response) {
		
		String keyword = request.getParameter("keyword");//关键字
		Integer currentPage = StringUtil.nullToInteger(request.getParameter("currentPage"));//当前页
		Integer currentLimit = StringUtil.nullToInteger(request.getParameter("limit"));//每页数据条数
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		
    	
    	try {
    		//返回分页对象
    		int maxPageNumber = 0;
    		int start = SplitPageUtil.getStart(currentPage, currentLimit);
			int limit = SplitPageUtil.getEnd(currentPage, currentLimit);
    		PageObjcet<ProductTask> page = new PageObjcet<ProductTask> ();
    		Map<String, Object> paramMap = new HashMap<String, Object>();
//    		paramMap.put("level", 2);
    		paramMap.put("isEnable", true);
    		Long count = this.productTaskManager.countHql(paramMap);
    		List<ProductTask> productCategoryList = this.productTaskManager.getHqlPages(paramMap, start, limit,"createTime","desc");
            if(productCategoryList != null && productCategoryList.size() > 0) {
            	maxPageNumber = 1;
            }
    		
    		
		
			page.setLimit(StringUtil.nullToLong(currentLimit));
    		//设置当前页
        	page.setCurrentPage(currentPage);
        	page.getStart();
        	//总数
    		page.setTotal(StringUtil.nullToLong(count));
    	
    		//分页结果列表
    		page.setPageList(productCategoryList);
    		//分页 页面列表
    		page.setPageIndexList();
    		resultMap.put("code", 1);
    		resultMap.put("page", page);
			return resultMap;
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	resultMap.put("msg", "没有查询到对应的数据");
		return resultMap;
    }
	
	
	
	
	/**
	 * 获取内页列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getPageList")
	public @ResponseBody Map<String, Object> getPageList(final HttpServletRequest request ,final HttpServletResponse response) {
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		Long channelId = StringUtil.nullToLong(request.getParameter("channelId"));//频道id
		Long pageId = StringUtil.nullToLong(request.getParameter("pageId"));//当前pageId
		Integer currentPage = StringUtil.nullToInteger(request.getParameter("currentPage"));//当前页
		Integer limit = StringUtil.nullToInteger(request.getParameter("limit"));//每页数据条数
		Map<String,Object> paramMap = new HashMap<String,Object>();
		if (StringUtil.isNull(channelId) || StringUtil.compareObject(channelId, 0)){
			resultMap.put("msg", "频道id不能为空");
			return resultMap;
		}
		if (StringUtil.isNull(pageId) || StringUtil.compareObject(pageId, 0)){
			resultMap.put("msg", "页面id不能为空");
			return resultMap;
		}
		FxPage fxPage = fxPageManager.get(pageId);
		if (fxPage == null || !StringUtil.compareObject(fxPage.getCategoryType(), FxPage.CATEGORY_TYPE_HOME)){
			resultMap.put("msg", "当前页不是首页不能加载内页");
			return resultMap;
		}
		try {
			Long discoveryChannelId = StringUtil.nullToLong(Constants.conf.getProperty("jkd.discovery.banner.id"));
			if(!StringUtil.compareObject(discoveryChannelId, channelId)) {
				paramMap.put("channelId",channelId);
			}
			paramMap.put("categoryType", FxPage.CATEGORY_TYPE_PAGE);
			paramMap.put("isDelete", false);
			//返回分页对象
	    	PageObjcet<FxPageVo> pages = null;
	    	List<FxPage> fxPageList = new ArrayList<FxPage>();
	    	//页面返回列表  用户返回给前端解析
	    	List<FxPageVo> fxPageVoList = new ArrayList<FxPageVo>();
			
	    	//商品总数
    		Long total = fxPageManager.countHql(paramMap);
    		pages = new PageObjcet<FxPageVo> ();
    		//每页条数
    		pages.setLimit(StringUtil.nullToLong(limit));
    		//设置当前页
        	pages.setCurrentPage(currentPage);
			
        	fxPageList = fxPageManager.getHqlPages(paramMap,pages.getStart(), pages.getLimit().intValue(), "createTime", PageObjcet.SORE_DESC);
    		if (fxPageList != null && fxPageList.size() >0){
    			for (FxPage fxPages : fxPageList){
    				FxPageVo fxPageVo = new FxPageVo(fxPages);	
    				fxPageVoList.add(fxPageVo);
    			}
    		}
        	
    		//总数
    		pages.setTotal(total);
    		//分页结果列表
    		pages.setPageList(fxPageVoList);
    		//分页 页面列表
    		pages.setPageIndexList();
    		//查询条件
        	//pages.setQueryString(queryString);
        	resultMap.put("code", 1);
			resultMap.put("page", pages);
			return resultMap;
//        	  	
//			List<FxPageVo> dataList = new ArrayList<FxPageVo>();
//			List<FxPage> pageList = fxPageManager.getFxPageListByChannelId(channelId);
//			for (FxPage page : pageList){
//				//排除当前页面
//				if(!StringUtil.compareObject(page.getPageId(), pageId)){
//					FxPageVo paveVo = new FxPageVo(page);
//					dataList.add(paveVo);
//				}
//			}
//			resultMap.put("code", 1);
//			resultMap.put("dataList", dataList);
//			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("msg", "没有对应的数据");
		return resultMap;
	}
	
	
	
	/**
	 * 获取专题列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getThemePageList")
	public @ResponseBody Map<String, Object> getThemePageList(final HttpServletRequest request ,final HttpServletResponse response) {
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		Long channelId = StringUtil.nullToLong(request.getParameter("channelId"));//频道id
		Long pageId = StringUtil.nullToLong(request.getParameter("pageId"));//当前pageId
		Integer currentPage = StringUtil.nullToInteger(request.getParameter("currentPage"));//当前页
		Integer limit = StringUtil.nullToInteger(request.getParameter("limit"));//每页数据条数
		Map<String,Object> paramMap = new HashMap<String,Object>();
		if (StringUtil.isNull(channelId) || StringUtil.compareObject(channelId, 0)){
			resultMap.put("msg", "频道id不能为空");
			return resultMap;
		}
		if (StringUtil.isNull(pageId) || StringUtil.compareObject(pageId, 0)){
			resultMap.put("msg", "专题id不能为空");
			return resultMap;
		}
		FxPage fxPage = fxPageManager.get(pageId);
		if (fxPage == null || !StringUtil.compareObject(fxPage.getCategoryType(), FxPage.CATEGORY_TYPE_HOME)){
			resultMap.put("msg", "当前页不是首页不能加载专题");
			return resultMap;
		}
		try {
			Long discoveryChannelId = StringUtil.nullToLong(Constants.conf.getProperty("jkd.discovery.banner.id"));
			if(!StringUtil.compareObject(discoveryChannelId, channelId)) {
				paramMap.put("channelId",channelId);
			}
			paramMap.put("categoryType", FxPage.CATEGORY_TYPE_THEME);
			paramMap.put("isDelete", false);
			//返回分页对象
	    	PageObjcet<FxPageVo> pages = null;
	    	List<FxPage> fxPageList = new ArrayList<FxPage>();
	    	//页面返回列表  用户返回给前端解析
	    	List<FxPageVo> fxPageVoList = new ArrayList<FxPageVo>();
			
	    	//商品总数
    		Long total = fxPageManager.countHql(paramMap);
    		pages = new PageObjcet<FxPageVo> ();
    		//每页条数
    		pages.setLimit(StringUtil.nullToLong(limit));
    		//设置当前页
        	pages.setCurrentPage(currentPage);
			
        	fxPageList = fxPageManager.getHqlPages(paramMap,pages.getStart(), pages.getLimit().intValue(), "createTime", PageObjcet.SORE_DESC);
    		if (fxPageList != null && fxPageList.size() >0){
    			for (FxPage fxPages : fxPageList){
    				FxPageVo fxPageVo = new FxPageVo(fxPages);	
    				List<FxChildren> fxChildrenList = this.fxChildrenManager.getFxChildrenListByPageId(fxPages.getPageId());
    				if(fxChildrenList != null && fxChildrenList.size() > 0) {
    					for(FxChildren fxChildren : fxChildrenList) {
    						if(StringUtil.compareObject(fxChildren.getType(), FxChildren.FXCHILDREN_TYP_SPECIAL)) {
    							fxPageVo.setImage(fxChildren.getPicture());
    						}
    					}
    				}
    				fxPageVoList.add(fxPageVo);
    			}
    		}
        	
    		//总数
    		pages.setTotal(total);
    		//分页结果列表
    		pages.setPageList(fxPageVoList);
    		//分页 页面列表
    		pages.setPageIndexList();
    		//查询条件
        	//pages.setQueryString(queryString);
        	resultMap.put("code", 1);
			resultMap.put("page", pages);
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("msg", "没有对应的数据");
		return resultMap;
	}
	
	
	
	/**
	 * 保存页面信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value="savePage")
	public @ResponseBody Map<String, Object> savePage(final HttpServletRequest request ,final HttpServletResponse response) {
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long pageId = StringUtil.nullToLong(request.getParameter("pageId"));				//页面id
		String custom = StringUtil.null2Str(request.getParameter("custom"));				//自定义分组
		if (StringUtil.isNull(pageId) || StringUtil.compareObject(pageId, 0)){
			resultMap.put("code", 0);
			resultMap.put("msg", "页面不存在");
			return resultMap;
		}else if (StringUtil.isNull(custom)){
			resultMap.put("code", 0);
			resultMap.put("msg", "页面内容不能为空");
			return resultMap;
		}
		
		try {
			FxPage page = this.fxPageManager.get(pageId);
			if(page == null || page.getPageId() == null){
				resultMap.put("code", 0);
				resultMap.put("msg", "页面内容不能为空");
				return resultMap;
			}
			
			List<FxChildren> childrens = ChannelUtil.parseChannel(custom, page);
			if(childrens != null && childrens.size() > 0) {
				if(childrens.get(0).getErrorCode() != null) {
					resultMap.put("code", 0);
					resultMap.put("msg", childrens.get(0).getReason());
					return resultMap;
				}
			}
			this.fxChildrenManager.saveNewChildList(childrens, pageId);
			resultMap.put("code", 1);
			resultMap.put("msg", "保存成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("code", 0);
		resultMap.put("msg", "保存失败");
		return resultMap;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getPageById")
	public @ResponseBody Map<String, Object> getPageById(final HttpServletRequest request ,final HttpServletResponse response) {
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("code", 0);
		Long pageId = StringUtil.nullToLong(request.getParameter("pageId"));				//页面id
		if (StringUtil.isNull(pageId) || StringUtil.compareObject(pageId, 0)){
			resultMap.put("msg", "页面不存在");
			return resultMap;
		}
		
		List<FxChildren> childrenList = fxChildrenManager.getFxChildrenListByPageId(pageId);
		try {
			List<FxChildrenFieldVo> fieldVoList  = ChannelUtil.parseChannel(childrenList);
			System.out.println(StringUtil.objectToJSON(fieldVoList));
			resultMap.put("code", 1);
			resultMap.put("data", fieldVoList);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("msg", "没有对应的数据");
		return resultMap;
	}
	
	
	/**
	 * web单图上传
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/uploadImage")
	public @ResponseBody Map<String, Object> webUploadImage(final HttpServletRequest request ,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		
		try {

			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<String> iter = multiRequest.getFileNames();
				while (iter.hasNext()) {
					// 由CommonsMultipartFile继承而来,拥有上面的方法.
					MultipartFile file = multiRequest.getFile(iter.next());
					if (!file.isEmpty()) {
						String originFileName = file.getOriginalFilename();							// 获取文件扩展名
						String xfilePath = CoreUtil.dateToPath("upload/images/banner", originFileName);
						String filePath = Constants.EXTERNAL_IMAGE_PATH + "/" + xfilePath;
						FileUploadUtil.copyFile(file.getInputStream(), filePath);

						resultMap.put("url", xfilePath);
						resultMap.put("code", 1);
						resultMap.put("msg", "图片上传成功");
						return resultMap;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("code", "0");
		resultMap.put("msg", "图片上传失败");
		return resultMap;
	}
}


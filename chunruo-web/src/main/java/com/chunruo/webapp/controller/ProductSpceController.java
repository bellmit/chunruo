package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.model.ProductSpecModel;
import com.chunruo.core.service.ProductSpecModelManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/productSpce/")
public class ProductSpceController extends BaseController {
	@Autowired
	private ProductSpecModelManager productSpecModelManager;
	
	/**
	 * 规格类型参数列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/specModelList")
	public @ResponseBody Map<String, Object> specModelList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ProductSpecModel> productSpecModelList = new ArrayList<ProductSpecModel> ();
		Long count = 0L;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));

			// 内容、@用户名、用户ID、#手机号码
			String keyword = StringUtil.nullToString(request.getParameter("query"));
			if(!StringUtil.isNullStr(keyword)) {
				// 内容
				paramMap.put("name", "%" + keyword + "%");
			}

			count = this.productSpecModelManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				productSpecModelList = this.productSpecModelManager.getHqlPages(paramMap, sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", productSpecModelList);
		resultMap.put("totalCount", count);
		return resultMap;
	}
	
	/**
	 * 增加规格类型参数
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addSpecModel")
	public @ResponseBody Map<String, Object> addSpecModel(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String name = StringUtil.nullToString(request.getParameter("name"));
			if(StringUtil.isNull(name)){
				resultMap.put("success", false);
				resultMap.put("message", "规格类型不能为空");
				return resultMap;
			}
			
			int sort = 0;
			List<ProductSpecModel> list = this.productSpecModelManager.getAll();
			if(list != null && list.size() > 0){
				sort = list.size();
				for(ProductSpecModel psm : list){
					if(StringUtil.compareObject(StringUtil.null2Str(name).toLowerCase(), StringUtil.null2Str(psm.getName()).toLowerCase())){
						resultMap.put("success", false);
						resultMap.put("message", "错误,规格类型已存在");
						return resultMap;
					}
				}
			}
			
			// 保存数据
			ProductSpecModel productSpecModel = new ProductSpecModel ();
			productSpecModel.setName(name);
			productSpecModel.setSort(sort);
			productSpecModel.setCreateTime(DateUtil.getCurrentDate());
			productSpecModel.setUpdateTime(productSpecModel.getCreateTime());
			this.productSpecModelManager.save(productSpecModel);
			
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
}

package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductIntro;
import com.chunruo.core.service.ProductIntroManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/productIntro/")
public class ProductIntroController extends BaseController {

	@Autowired
	private ProductIntroManager productIntroManager;
	
	@RequestMapping("/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<ProductIntro> productIntroList = new ArrayList<ProductIntro>();
		Long count = 0L;
		try {

			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));// 排序
			String filters = StringUtil.nullToString(request.getParameter("filters"));// 过滤
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), ProductIntro.class);

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

			count = this.productIntroManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				productIntroList = this.productIntroManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"),
						sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", productIntroList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	
	/**
	 * 新建、修改商品说明
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveOrUpdateIntro")
	public @ResponseBody Map<String, Object> saveOrUpdateIntro(@ModelAttribute("productIntro") ProductIntro productIntro,final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {

			// 判断输入是否为空
			if (StringUtil.isNull(productIntro.getTitle())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("请输入标题"));
				return resultMap;
			} else if (StringUtil.isNull(productIntro.getIntroduction())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("请输入简介"));
				return resultMap;
			} else if (StringUtil.isNull(productIntro.getDescription())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("请输入描述"));
				return resultMap;
			}

			// 判断输入的文字是否超过指定长度
			byte[] titleBytes = productIntro.getTitle().getBytes("UTF-8");
			byte[] introductionBytes = productIntro.getIntroduction().getBytes("UTF-8");
			byte[] descriptionBytes = productIntro.getDescription().getBytes("UTF-8");
			if (titleBytes.length > 24) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("标题不得超过8个文字"));
				return resultMap;
			} else if (descriptionBytes.length > 24) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("描述不得超过8个文字"));
				return resultMap;
			} 

			if (productIntro.getIntroId() == null) {
				// 新增
				productIntro.setCreateTime(DateUtil.getCurrentDate());
				productIntro.setUpdateTime(DateUtil.getCurrentDate());
				this.productIntroManager.save(productIntro);
				//更新缓存
				Constants.PRODUCT_INTRO_MAP.put(productIntro.getIntroId(), productIntro);
			} else {
				// 更新
				ProductIntro dbProductIntro = this.productIntroManager.get(productIntro.getIntroId());
				if (dbProductIntro == null || dbProductIntro.getIntroId() == null) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("错误,没有找到商品说明"));
					return resultMap;
				}
				dbProductIntro.setSort(productIntro.getSort());
				dbProductIntro.setIntroduction(productIntro.getIntroduction());
				dbProductIntro.setTitle(productIntro.getTitle());
				dbProductIntro.setDescription(productIntro.getDescription());
				dbProductIntro.setUpdateTime(DateUtil.getCurrentDate());
				this.productIntroManager.update(dbProductIntro);
				//更新缓存
				Constants.PRODUCT_INTRO_MAP.put(dbProductIntro.getIntroId(), dbProductIntro);
			}

			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("submit.success"));
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("操作失败"));
		return resultMap;
	}

	
	/**
	 * 删除商品说明
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteIntro")
	public @ResponseBody Map<String, Object> deleteIntro(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = request.getParameter("idListGridJson");
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
			this.productIntroManager.deleteByIdList(idList);
			for(Long introId:idList) {
				//从缓存中删除掉
				Constants.PRODUCT_INTRO_MAP.remove(introId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		resultMap.put("success", true);
		resultMap.put("message", getText("submit.success"));
		return resultMap;

	}
}

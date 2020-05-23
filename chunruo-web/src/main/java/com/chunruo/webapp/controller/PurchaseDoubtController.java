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
import com.chunruo.core.model.PurchaseDoubt;
import com.chunruo.core.service.PurchaseDoubtManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.SpringServletContext;

@Controller
@RequestMapping("/purchaseDoubt/")
public class PurchaseDoubtController extends BaseController {

	@Autowired
	private PurchaseDoubtManager purchaseDoubtManager;

	@RequestMapping("/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<PurchaseDoubt> productIntroList = new ArrayList<PurchaseDoubt>();
		Long count = 0L;
		try {

			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));// 排序
			String filters = StringUtil.nullToString(request.getParameter("filters"));// 过滤
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), PurchaseDoubt.class);

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

			count = this.purchaseDoubtManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				productIntroList = this.purchaseDoubtManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"),
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
	 * 新建、修改购买答疑
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveOrUpdateDoubt")
	public @ResponseBody Map<String, Object> saveOrUpdateDoubt(@ModelAttribute("purchaseDoubt") PurchaseDoubt purchaseDoubt, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {

			// 判断输入是否为空
			if (StringUtil.isNull(purchaseDoubt.getTitle())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("请输入标题"));
				return resultMap;
			} else if (StringUtil.isNull(purchaseDoubt.getDescription())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("请输入描述"));
				return resultMap;
			}

			// 判断输入的文字是否超过指定长度
			byte[] titleBytes = purchaseDoubt.getTitle().getBytes("UTF-8");
			byte[] descriptionBytes = purchaseDoubt.getDescription().getBytes("UTF-8");
			if (titleBytes.length > 60) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("标题不得超过20个文字"));
				return resultMap;
			} else if (descriptionBytes.length > 300) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("描述不得超过100个文字"));
				return resultMap;
			}

			if (purchaseDoubt.getDoubtId() == null) {
				// 新增
				purchaseDoubt.setCreateTime(DateUtil.getCurrentDate());
				purchaseDoubt.setUpdateTime(DateUtil.getCurrentDate());
				this.purchaseDoubtManager.save(purchaseDoubt);
				//更新缓存
				Constants.PURCHASE_DOUBT_MAP.put(purchaseDoubt.getDoubtId(), purchaseDoubt);
			} else {
				// 更新
				PurchaseDoubt dbPurchaseDoubt = this.purchaseDoubtManager.get(purchaseDoubt.getDoubtId());
				if (dbPurchaseDoubt == null || dbPurchaseDoubt.getDoubtId() == null) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("错误,没有找到商品答疑"));
					return resultMap;
				}
				dbPurchaseDoubt.setSort(purchaseDoubt.getSort());
				dbPurchaseDoubt.setTitle(purchaseDoubt.getTitle());
				dbPurchaseDoubt.setDescription(purchaseDoubt.getDescription());
				dbPurchaseDoubt.setUpdateTime(DateUtil.getCurrentDate());
				this.purchaseDoubtManager.update(dbPurchaseDoubt);
				//更新缓存
				Constants.PURCHASE_DOUBT_MAP.put(dbPurchaseDoubt.getDoubtId(), dbPurchaseDoubt);
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
	 * 删除商品答疑
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteDoubt")
	public @ResponseBody Map<String, Object> deleteDoubt(final HttpServletRequest request) {
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
			this.purchaseDoubtManager.deleteByIdList(idList);
			for(Long doubtId:idList) {
				Constants.PURCHASE_DOUBT_MAP.remove(doubtId);
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

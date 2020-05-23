package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.model.ProductAnswer;
import com.chunruo.core.model.ProductQuestion;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.ProductAnswerManager;
import com.chunruo.core.service.ProductQuestionManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.vo.ProductAnswerVo;
import com.chunruo.webapp.vo.ProductQuestionVo;

@Controller
@RequestMapping("/questionAndAnswer/")
public class ProductQuestionController  extends BaseController{
	@Autowired
	private ProductQuestionManager productQuestionManager;
	@Autowired
	private ProductAnswerManager productAnswerManager;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private ProductManager productManager;
	
	
	@RequestMapping(value = "/questionList")
	public @ResponseBody Map<String, Object> questionList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<ProductQuestion> questionList = new ArrayList<>();
		List<ProductQuestionVo> questionVoList = new ArrayList<>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), ProductQuestion.class);
			paramMap.put("isDelete", false);
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.productQuestionManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				questionList = this.productQuestionManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if (questionList != null && questionList.size() > 0){
					List<Long> userIdList = new ArrayList<Long>();
					List<Long> productIdList = new ArrayList<Long>();
					Map<Long, UserInfo> userInfoMap = new HashMap<Long, UserInfo>();
					Map<Long, Product> productMap = new HashMap<Long, Product>();
					for (ProductQuestion question : questionList){
						try {
							ProductQuestionVo questionVo = new ProductQuestionVo(question);
							userIdList.add(question.getUserId());
							productIdList.add(question.getProductId());
							questionVoList.add(questionVo);
						} catch (Exception e) {
							continue;
						}
					}
					
					//用户信息赋值
					if (userIdList != null && userIdList.size() > 0){
						List<UserInfo> userInfoList = userInfoManager.getByIdList(userIdList);
						if (userInfoList != null && userInfoList.size() > 0){
							for (UserInfo userInfo : userInfoList){
								userInfoMap.put(userInfo.getUserId(), userInfo);
							}
						}
					}
					
					//商品信息
					if (productIdList != null && productIdList.size() > 0){
						List<Product> productList = productManager.getByIdList(productIdList);
						if (productList != null && productList.size() > 0){
							for (Product product : productList){
								productMap.put(product.getProductId(), product);
								
							}
						}
					}
					
					for (ProductQuestionVo questionVo : questionVoList){
						if (productMap != null && productMap.size() > 0){
							Product product = productMap.get(questionVo.getProductId());
							if (product != null){
								questionVo.setProductName(product.getName());
								questionVo.setImage(product.getImage());
							}
						}
						
						if(userInfoMap != null && userInfoMap.size() > 0){
							UserInfo userInfo = userInfoMap.get(questionVo.getUserId());
							if (userInfo != null){
								questionVo.setUserName(userInfo.getNickname());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("data", questionVoList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	@RequestMapping(value = "/answerList")
	public @ResponseBody Map<String, Object> answerList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		
		List<ProductAnswer> answerList = new ArrayList<>();
		List<ProductAnswerVo> answerVoList = new ArrayList<>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), ProductAnswer.class);
			paramMap.put("isDelete", false);
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					if (StringUtil.compareObject(entry.getKey(), "content")){
						paramMap.put(entry.getKey(),"%" + entry.getValue() + "%");
					}else{
						paramMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
			
			count = this.productAnswerManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				answerList = this.productAnswerManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if (answerList != null && answerList.size() > 0){
					List<Long> userIdList = new ArrayList<Long>();
					List<Long> productIdList = new ArrayList<Long>();
					Map<Long, UserInfo> userInfoMap = new HashMap<Long, UserInfo>();
					Map<Long, ProductQuestionVo> questionVoMap = new HashMap<Long, ProductQuestionVo>();
					Map<Long, Product> productMap = new HashMap<Long, Product>();
					List<ProductQuestion> questionList = new ArrayList<>();
					Set<Long> questionIdSet = new HashSet<Long>();
					for (ProductAnswer answer : answerList){
						try {
							ProductAnswerVo answerVo = new ProductAnswerVo(answer);
							answerVoList.add(answerVo);
							questionIdSet.add(answer.getQuestionId());
							userIdList.add(answer.getUserId());
						} catch (Exception e) {
							continue;
						}
					}
					//问题信息
					if(questionIdSet != null && questionIdSet.size() > 0){
						questionList = this.productQuestionManager.getByIdList(StringUtil.longSetToList(questionIdSet));
					}
					if (questionList != null && questionList.size() > 0){
						for (ProductQuestion question : questionList){
							ProductQuestionVo questionVo = new ProductQuestionVo(question);
							questionVoMap.put(question.getQuestionId(), questionVo);
							productIdList.add(question.getProductId());
						}
					}
					
					//用户信息赋值 
					if (userIdList != null && userIdList.size() > 0){
						List<UserInfo> userInfoList = this.userInfoManager.getByIdList(userIdList);
						if (userInfoList != null && userInfoList.size() > 0){
							for (UserInfo userInfo : userInfoList){
								userInfoMap.put(userInfo.getUserId(), userInfo);
							}
						}
					}
					
					//商品信息
					if (productIdList != null && productIdList.size() > 0){
						List<Product> productList = this.productManager.getByIdList(productIdList);
						if (productIdList != null && productIdList.size() > 0){
							for (Product product : productList){
								productMap.put(product.getProductId(), product);
							}
						}
					}
					
					// 回答信息
					if (answerVoList != null && answerVoList.size() > 0){
						for (ProductAnswerVo answerVo : answerVoList){
							if (questionVoMap != null && questionVoMap.size() > 0){
								ProductQuestionVo questionVo = questionVoMap.get(answerVo.getQuestionId());
								if (questionVo != null)	{
									Product product = productMap.get(questionVo.getProductId());
									answerVo.setQuestionContent(questionVo.getContent());
									if (product != null){
										answerVo.setProductName(product.getName());
									}
								}	
							}
							
							
							if(userInfoMap != null && userInfoMap.size() > 0){
								UserInfo userInfo = userInfoMap.get(answerVo.getUserId());
								if (userInfo != null){
									answerVo.setUserName(userInfo.getNickname());
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("data", answerVoList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	
	/**
	 * 根据问题ID获取问题答案
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getAnswerListByQuestionId")
	public @ResponseBody Map<String, Object> getAnswerListByQuestionId(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long questionId = StringUtil.nullToLong(request.getParameter("questionId"));
		List<ProductAnswerVo> answerVoList = new ArrayList<ProductAnswerVo>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("isDelete", false);
		paramMap.put("questionId", questionId);
		Long count = 0L;
		try {
			count = productAnswerManager.countHql(paramMap);
			List<ProductAnswer> answerList = new ArrayList<>();
			if (count != null && count.longValue() > 0L) {
				answerList = productAnswerManager.getHqlPages(paramMap , "createTime", "desc");
				if (answerList != null && answerList.size() > 0){
					List<Long> userIdList = new ArrayList<Long>();
					Map<Long, UserInfo> userInfoMap = new HashMap<Long, UserInfo>();
					for (ProductAnswer answer : answerList){
						try {
							ProductAnswerVo answerVo = new ProductAnswerVo(answer);
							userIdList.add(answer.getUserId());
							answerVoList.add(answerVo);
						} catch (Exception e) {
							continue;
						}
						
					}
					if (userIdList != null && userIdList.size() > 0){
						List<UserInfo> userInfoList = userInfoManager.getByIdList(userIdList);
						if (userInfoList != null && userInfoList.size() > 0){
							for (UserInfo userInfo : userInfoList){
								userInfoMap.put(userInfo.getUserId(), userInfo);
							}
						}
					}
					if (userInfoMap != null && userInfoMap.size() > 0){
						for (ProductAnswerVo answer : answerVoList){
							UserInfo userInfo = userInfoMap.get(answer.getUserId());
							answer.setUserName(userInfo.getNickname());
						}
					}
				}
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("data", answerVoList);
		resultMap.put("totalCount", count);
		return resultMap;
	}
	
	/**
	 * 审核商品问题
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/checkQuestion", method=RequestMethod.POST)
   	public @ResponseBody Map<String, Object> updateQuestionStatus(@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
		
		try {
			this.productQuestionManager.updateQuestionStatusByIdList(idList, true);
			resultMap.put("success", true);
			resultMap.put("message", "审核成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("success", false);
		resultMap.put("message", "审核失败");
		return resultMap;
	}

	/**
	 * 删除商品问题
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/deleteQuestion", method=RequestMethod.POST)
   	public @ResponseBody Map<String, Object> deleteQuestion(@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
		
		try {
			//批量删除问题
			this.productQuestionManager.deleteQuestionByIdList(idList);
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
	
	/**
	 * 审核商品问题答案
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/checkAnswer", method=RequestMethod.POST)
   	public @ResponseBody Map<String, Object> updateAnswerStatus(@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object> ();
    	List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
		
		try {
			//批量审核回答
			this.productAnswerManager.updateAnswerStatusByIdList(idList, true);
			resultMap.put("success", true);
			resultMap.put("message", "审核成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("success", false);
		resultMap.put("message", "审核失败");
		return resultMap;
	}
	
	/**
	 * 删除商品问题答案
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/deleteAnswer", method=RequestMethod.POST)
   	public @ResponseBody Map<String, Object> deleteAnswer(@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				//idList为空
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
		
		try {
			//批量删除回答
			this.productAnswerManager.deleteAnswerByIdList(idList);
			resultMap.put("success", true);
			resultMap.put("message", "删除成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("success", false);
		resultMap.put("message", "删除失败");
		return resultMap;
	}
	
	/**
	 * 添加问题回答
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveAnswer", method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveAnswer( final HttpServletRequest request) {
		Long questionId = StringUtil.nullToLong(request.getParameter("questionId"));
		Long userId = StringUtil.nullToLong(request.getParameter("userId"));
		String content = StringUtil.null2Str(request.getParameter("content"));
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("success", false);
		resultMap.put("error", true);
		ProductQuestion productQuestion = null;
		if (StringUtil.isNull(questionId) 
				|| StringUtil.compareObject(questionId, 0)
				|| (productQuestion = this.productQuestionManager.get(questionId)) == null){
			resultMap.put("success", false);
			resultMap.put("message", "问题不存在");
			return resultMap;
		}else if (StringUtil.isNull(userId) || StringUtil.compareObject(userId, 0)){
			resultMap.put("success", false);
			resultMap.put("message", "请选择用户");
			return resultMap;
		}else if (StringUtil.isNull(content) ){
			resultMap.put("success", false);
			resultMap.put("message", "回答内容不能为空");
			return resultMap;
		}
		
		Product product = this.productManager.get(StringUtil.nullToLong(productQuestion.getProductId()));
		if(product == null || product.getProductId() == null) {
			resultMap.put("success", false);
			resultMap.put("message", "商品不存在");
			return resultMap;
		}
		
		
		try {
			ProductAnswer answer = new ProductAnswer();
			answer.setContent(content);
			answer.setUserId(userId);
			answer.setQuestionId(questionId);
			answer.setIsDelete(false);
			answer.setStatus(true);
			answer.setCreateTime(new Date());
			answer.setUpdateTime(answer.getCreateTime());
			answer = this.productAnswerManager.save(answer);
			answer.setProductName(StringUtil.null2Str(product.getName()));
			
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
		resultMap.put("success", true);
		resultMap.put("message", getText("save.success"));
		return resultMap;
	}
}

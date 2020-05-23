package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductSeckill;
import com.chunruo.core.model.ProductTask;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserProductTaskItem;
import com.chunruo.core.model.UserProductTaskRecord;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.ProductSeckillManager;
import com.chunruo.core.service.ProductTaskManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserProductTaskItemManager;
import com.chunruo.core.service.UserProductTaskRecordManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/productTask/")
public class ProductTaskController extends BaseController {
	private Logger log = LoggerFactory.getLogger(ProductTaskController.class);
	@Autowired
	private ProductManager productManager;

	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private ProductTaskManager productTaskManager;
	@Autowired
	private ProductSeckillManager productSeckillManager;
	@Autowired
	private UserProductTaskItemManager userProductTaskItemManager;
	@Autowired
	private UserProductTaskRecordManager userProductTaskRecordManager;
	/**
	 * 列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<ProductTask> productTaskList = new ArrayList<ProductTask>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), ProductTask.class);

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

			paramMap.put("isDelete", false);
			count = this.productTaskManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				productTaskList = this.productTaskManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"),
						sortMap.get("dir"));
				if (productTaskList != null && productTaskList.size() > 0) {
					List<Long> productIdList = new ArrayList<Long>();
					for (ProductTask productTask : productTaskList) {
						productIdList.add(StringUtil.nullToLong(productTask.getProductId()));
					}

					Map<Long, Product> productMap = new HashMap<Long, Product>();
					List<Product> productList = this.productManager.getByIdList(productIdList);
					if (productList != null && productList.size() > 0) {
						for (Product product : productList) {
							productMap.put(StringUtil.nullToLong(product.getProductId()), product);
						}
					}

					if (productMap != null && productMap.size() > 0) {
						for (ProductTask productTask : productTaskList) {
							Product product = productMap.get(productTask.getProductId());
							if (product != null && product.getProductId() != null) {
								productTask.setProductName(StringUtil.null2Str(product.getName()));
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("data", productTaskList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	

	
	/**
	 * 发布信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveProductTask")
	public @ResponseBody Map<String, Object> saveProductTask(@ModelAttribute("productTask") ProductTask productTask,
			final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {

			String taskName = StringUtil.null2Str(productTask.getTaskName());
			byte[] taskNameBytes = taskName.getBytes("UTF-8");
			if (taskNameBytes.length > 45) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("任务名称不得多于15个汉字"));
				return resultMap;
			}

			Integer taskNumber = StringUtil.nullToInteger(productTask.getTaskNumber());
			if(taskNumber <= 0 || taskNumber > 5) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("任务数量必须为0~5个"));
				return resultMap;
			}
			
			Integer reward = StringUtil.nullToInteger(productTask.getReward());
			if(reward.compareTo(0) <= 0) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("任务奖励配置错误"));
				return resultMap;
			}
			
			Integer maxGroupNumber = StringUtil.nullToInteger(productTask.getMaxGroupNumber());
			if(maxGroupNumber <= 0) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("任务上限周期配置错误"));
				return resultMap;
			}
			if(productTask.getBeginTime() == null
					|| productTask.getEndTime() == null) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("任务开始、结束时间配置错误"));
				return resultMap;
			}
			Date currentDate = DateUtil.getFormatDate(DateUtil.getCurrentDate(), DateUtil.DATE_TIME_PATTERN);
			if(productTask.getBeginTime().after(productTask.getEndTime())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("开始时间不能晚于结束时间"));
				return resultMap;
			}else if(currentDate.after(productTask.getEndTime())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("结束时间不能小于当前时间"));
				return resultMap;
			}
			
			Long productId = StringUtil.nullToLong(productTask.getProductId());
			Product product = this.productManager.get(productId);
			if(product == null || product.getProductId() == null
					|| StringUtil.nullToBoolean(product.getIsSoldout())
					|| !StringUtil.nullToBoolean(product.getStatus())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("该商品已售罄或已下架或选择了多个商品"));
				return resultMap;
			}
			
			//检查该商品是否为秒杀商品
			if(!StringUtil.isNull(product.getSeckillId())) {
				String currentTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_HOUR, DateUtil.getCurrentDate());
				ProductSeckill productSeckill = this.productSeckillManager.get(StringUtil.nullToLong(product.getSeckillId()));
			    if(productSeckill != null && productSeckill.getSeckillId() != null
			    		&& !StringUtil.nullToBoolean(productSeckill.getIsDelete())
			    		&& productSeckill.getEndTime().compareTo(currentTime) > 0) {
			    	resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("该商品为秒杀商品，暂不支持配置任务商品"));
					return resultMap;
			    }
			}

			
			
			List<ProductTask> productTaskList = this.productTaskManager.getProductTaskListByProductId(productId);
			if(productTaskList != null && productTaskList.size() > 0 ) {
				for(ProductTask task : productTaskList) {
					Date beginTime = task.getBeginTime();
					Date endTime = task.getEndTime();
				    if(beginTime != null && endTime != null
				    		&& endTime.getTime() >= DateUtil.getCurrentTime()
				    		&& StringUtil.nullToBoolean(task.getIsEnable())) {
				    	resultMap.put("error", true);
						resultMap.put("success", true);
						resultMap.put("message", getText("此商品所属任务尚未结束"));
						return resultMap;
				    }
				}
			}
			
			// 检查是否新建批发商品
			boolean isNews = (productTask.getTaskId() == null);
			if (isNews) {
				productTask.setCreateTime(DateUtil.getCurrentDate());
			} else {
				ProductTask dbProductTask = this.productTaskManager.get(productTask.getTaskId());
				if (dbProductTask == null || dbProductTask.getTaskId() == null) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("errors.nuKnow"));
					return resultMap;
				} else if (dbProductTask.getCreateTime() == null) {
					dbProductTask.setCreateTime(DateUtil.getCurrentDate());
				}
				productTask.setCreateTime(dbProductTask.getCreateTime());
			}

			productTask.setIsEnable(true);
			productTask.setIsDelete(false);
			productTask.setProductName(StringUtil.null2Str(product.getName()));
			productTask.setImagePath(StringUtil.null2Str(product.getImage()));
			productTask.setUpdateTime(DateUtil.getCurrentDate());
			this.productTaskManager.save(productTask);
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("保存失败"));
		return resultMap;
	}

	/**
	 * 得到任务详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getProductTaskById")
	public @ResponseBody Map<String, Object> getProductTaskById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String taskId = StringUtil.null2Str(request.getParameter("taskId"));
		ProductTask productTask = null;
		try {
			if (StringUtil.isNumber(taskId)
					&& (productTask = this.productTaskManager.get(StringUtil.nullToLong(taskId))) != null) {

				if(productTask.getProductId() != null) {
					Product product = this.productManager.get(StringUtil.nullToLong(productTask.getProductId()));
					if (product != null && product.getProductId() != null) {
						productTask.setProductName(StringUtil.null2Str(product.getName()));
					}
				}
				resultMap.put("success", true);
				resultMap.put("data", productTask);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		return resultMap;
	}

	/**
	 * 批量删除
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteProductTask")
	public @ResponseBody Map<String, Object> deleteProductTask(@RequestParam(value = "idListGridJson") String record,
			final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			List<Long> idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
			List<ProductTask> productTaskList = this.productTaskManager.getByIdList(idList);
            if(productTaskList != null && productTaskList.size() > 0) {
            	for(ProductTask task : productTaskList) {
            		if(StringUtil.nullToBoolean(task.getIsEnable())) {
            			resultMap.put("success", false);
        				resultMap.put("message", String.format("请先禁用\"%s\"任务",task.getTaskName()));
        				return resultMap;
            		}
            		task.setIsEnable(false);
            		task.setIsDelete(true);
            		task.setUpdateTime(DateUtil.getCurrentDate());
            	}
            	this.productTaskManager.batchInsert(productTaskList, productTaskList.size());
            }
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
	 * 结束任务
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateIsEnable")
	public @ResponseBody Map<String, Object> updateIsEnable(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long taskId = StringUtil.nullToLong(request.getParameter("taskId"));
		Boolean isEnable = StringUtil.nullToBoolean(request.getParameter("isEnable"));
		try{
		    ProductTask productTask = this.productTaskManager.get(taskId);
		    if(productTask == null || productTask.getTaskId() == null) {
		    	resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("errors.nuKnow"));
				return resultMap;
		    }
		    
		    if(isEnable) {
		    	List<ProductTask> productTaskList = this.productTaskManager.getProductTaskListByProductId(StringUtil.nullToLong(productTask.getProductId()));
		        if(productTaskList != null && productTaskList.size() > 0) {
		        	for(ProductTask task : productTaskList) {
		        		if(StringUtil.nullToBoolean(task.getTaskStatus())
		        				&& !StringUtil.compareObject(productTask.getTaskId(), task.getTaskId())) {
		        			resultMap.put("error", true);
		    				resultMap.put("success", true);
		    				resultMap.put("message", getText("该商品已有开启中得任务"));
		    				return resultMap;
		        		}
		        	}
		        }
		    }

		    productTask.setIsEnable(isEnable);
		    productTask.setUpdateTime(DateUtil.getCurrentDate());
		    this.productTaskManager.save(productTask);
		    resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("errors.nuKnow"));
		return resultMap;
	}
	
	
	
	/**
	 * 列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/userProductRecordlist")
	public @ResponseBody Map<String, Object> userProductRecordlist(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<UserProductTaskRecord> recordList = new ArrayList<UserProductTaskRecord>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), UserProductTaskRecord.class);

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

			count = this.userProductTaskRecordManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				recordList = this.userProductTaskRecordManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"),
						sortMap.get("dir"));
				if (recordList != null && recordList.size() > 0) {
					List<Long> userIdList = new ArrayList<Long>();
					List<Long> taskIdList = new ArrayList<Long>();
					for (UserProductTaskRecord record : recordList) {
						taskIdList.add(StringUtil.nullToLong(record.getTaskId()));
						userIdList.add(StringUtil.nullToLong(record.getUserId()));
					}

					Map<Long, ProductTask> productTaskMap = new HashMap<Long, ProductTask>();
					List<ProductTask> productTaskList = this.productTaskManager.getByIdList(taskIdList);
					if (productTaskList != null && productTaskList.size() > 0) {
						for (ProductTask productTask : productTaskList) {
							productTaskMap.put(StringUtil.nullToLong(productTask.getTaskId()), productTask);
						}
					}
					
					Map<Long,UserInfo> userInfoMap = new HashMap<Long,UserInfo>();
					List<UserInfo> userInfoList = this.userInfoManager.getByIdList(userIdList);
                    if(userInfoList != null && userInfoList.size() > 0) {
                    	for(UserInfo userInfo : userInfoList) {
                    		userInfoMap.put(StringUtil.nullToLong(userInfo.getUserId()), userInfo);
                    	}
                    }
					
                    for (UserProductTaskRecord record : recordList) {
                    	UserInfo userInfo = userInfoMap.get(StringUtil.nullToLong(record.getUserId()));
                    	if(userInfo != null && userInfo.getUserId() != null) {
                    		record.setUserName(StringUtil.null2Str(userInfo.getNickname()));
                    	}
                    	ProductTask productTask = productTaskMap.get(StringUtil.nullToLong(record.getTaskId()));
                    	if(productTask != null && productTask.getTaskId() != null) {
                    		record.setTaskName(StringUtil.null2Str(productTask.getTaskName()));
                    	}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("data", recordList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	
	/**
	 * 得到任务详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getUserProductTaskById")
	public @ResponseBody Map<String, Object> getUserProductTaskById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String recordId = StringUtil.null2Str(request.getParameter("recordId"));
		UserProductTaskRecord record = null;
		try {
			if (StringUtil.isNumber(recordId)
					&& (record = this.userProductTaskRecordManager.get(StringUtil.nullToLong(recordId))) != null) {

				if(record.getRecordId()!= null) {
					List<UserProductTaskItem> userProductTaskItemList = this.userProductTaskItemManager.getUserProductTaskItemListById(StringUtil.nullToLong(record.getTaskId()), StringUtil.nullToLong(record.getUserId()));
				    
					ProductTask productTask = this.productTaskManager.get(StringUtil.nullToLong(record.getTaskId()));
					
					resultMap.put("item", userProductTaskItemList);
					resultMap.put("productTask", productTask);
				}
				
				
				
				resultMap.put("success", true);
				resultMap.put("data", record);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		return resultMap;
	}
	
}

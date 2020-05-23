package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.InviteTask;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.service.CouponManager;
import com.chunruo.core.service.InviteTaskManager;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.vo.ImageVo;

@Controller
@RequestMapping("/inviteTask/")
public class InviteTaskController extends BaseController {
	private Logger log = LoggerFactory.getLogger(InviteTaskController.class);
	
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private InviteTaskManager inviteTaskManager;


	/**
	 * 模块列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<InviteTask> inviateTaskList = new ArrayList<InviteTask>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), InviteTask.class);

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

			count = this.inviteTaskManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				inviateTaskList = this.inviteTaskManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"),
						sortMap.get("dir"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("data", inviateTaskList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	/**
	 * 保存模块
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveInviteTask")
	public @ResponseBody Map<String, Object> saveInviteTask(
			@ModelAttribute("inviteTask") InviteTask inviteTask, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String recodeGridJson = StringUtil.null2Str(request.getParameter("recodeGridJson"));
		try {

			if(StringUtil.nullToInteger(inviteTask.getNumber()).compareTo(0) <= 0) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("邀请人数必须大于0"));
				return resultMap;
			}else if(StringUtil.isNull(inviteTask.getInviteDesc())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("奖励描述不能为空"));
				return resultMap;
			}
			
			List<Integer> typeList = new ArrayList<Integer>();
			typeList.add(InviteTask.INVITE_TASK_COUPON);
			typeList.add(InviteTask.INVITE_TASK_JIBI);
			typeList.add(InviteTask.INVITE_TASK_COUPON_JIBI);
			if(!typeList.contains(inviteTask.getType())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("奖励类型错误"));
				return resultMap;
			}
			
			if(StringUtil.compareObject(inviteTask.getType(), InviteTask.INVITE_TASK_COUPON)
					|| StringUtil.compareObject(inviteTask.getType(), InviteTask.INVITE_TASK_COUPON_JIBI)) {
				List<Long> couponIdList = StringUtil.stringToLongArray(inviteTask.getCouponIds());
				if(couponIdList == null || couponIdList.isEmpty()) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("优惠券id不能为空"));
					return resultMap;
				}
				
				for (Long id : couponIdList) {
					Coupon coupon = this.couponManager.get(id);
					if (coupon == null || coupon.getCouponId() == null
							|| !StringUtil.nullToBoolean(coupon.getIsEnable())) {
						resultMap.put("message", String.format("\"%s\"此id所对应的优惠券未找到或未启用", id));
						resultMap.put("success", true);
						resultMap.put("error", true);
						return resultMap;
					}
				}
			}
			
			if(StringUtil.compareObject(inviteTask.getType(), InviteTask.INVITE_TASK_JIBI)
					|| StringUtil.compareObject(inviteTask.getType(), InviteTask.INVITE_TASK_COUPON_JIBI)) {
				if(StringUtil.nullToDouble(inviteTask.getAmount()).compareTo(0D) <= 0) {
					resultMap.put("message", "集币金额必须大于0");
					resultMap.put("success", true);
					resultMap.put("error", true);
					return resultMap;	
				}
			}
			
			
			// 图片
			Set<String> filePathMap = new HashSet<String>();
			List<Object> saveMap = StringUtil.jsonDeserialize(recodeGridJson);
			List<ProductImage> imageList = new ArrayList<ProductImage>();
			if (saveMap != null && saveMap.size() > 0) {
				for (int i = 0; i < saveMap.size(); i++) {
					@SuppressWarnings("unchecked")
					ProductImage productImage = createImage((Map<String, Object>) saveMap.get(i),
							ProductImage.IMAGE_TYPE_MATERIAL);
					if (StringUtil.isNull(productImage.getImageType())) {
						resultMap.put("error", true);
						resultMap.put("success", true);
						resultMap.put("message", getText("image.saveAndDel.dataEexption"));
						return resultMap;
					} else if (productImage.getImageId() == null
							&& !StringUtil.isNullStr(productImage.getImagePath())) {
						if (filePathMap.contains(productImage.getImagePath())) {
							continue;
						}
						filePathMap.add(productImage.getImagePath());
					}
					imageList.add(productImage);
				}
			} else {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("头像不能为空"));
				return resultMap;
			}

		
			
			if (imageList != null && imageList.size() > 0) {
				ProductImage image = imageList.get(0);
				inviteTask.setImagePath(image.getImagePath());
			}

			if (!StringUtil.isNull(inviteTask.getTaskId())) {
				InviteTask dbInviteTask = this.inviteTaskManager.get(inviteTask.getTaskId());
				if (dbInviteTask == null || dbInviteTask.getTaskId() == null) {
					inviteTask.setCreateTime(DateUtil.getCurrentDate());
				} else {
					inviteTask.setCreateTime(dbInviteTask.getCreateTime());
				}
			} else {
				inviteTask.setCreateTime(DateUtil.getCurrentDate());
			}
			inviteTask.setUpdateTime(DateUtil.getCurrentDate());

			this.inviteTaskManager.save(inviteTask);
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
	 * 得到模块详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getInviteTaskById")
	public @ResponseBody Map<String, Object> getModuleById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String taskId = StringUtil.null2Str(request.getParameter("taskId"));
		InviteTask inviteTask = null;
		List<ImageVo> imageList = new ArrayList<ImageVo>();

		try {
			if (StringUtil.isNumber(taskId)
					&& (inviteTask = this.inviteTaskManager.get(StringUtil.nullToLong(taskId))) != null) {
			
				String backgroundImagePath = inviteTask.getImagePath();
				ImageVo backgroundImage = new ImageVo();
				if (!StringUtil.isNull(backgroundImagePath)) {
					if (backgroundImagePath.startsWith("/")) {
						backgroundImagePath = backgroundImagePath.substring(1);
					}
					backgroundImage.setFileName(backgroundImagePath.substring(backgroundImagePath.lastIndexOf("/")));
					backgroundImage.setFileType(FileUtil.getSuffixByFilename(backgroundImagePath));
					backgroundImage.setFilePath("upload/" + backgroundImagePath);
				}
				imageList.add(backgroundImage);
				resultMap.put("success", true);
				resultMap.put("data", inviteTask);
				resultMap.put("imageList", imageList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		return resultMap;
	}

	/**
	 * 删除邀请任务
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteInviteTask")
	public @ResponseBody Map<String, Object> deleteInviteTask(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long taskId = StringUtil.nullToLong(request.getParameter("taskId"));
		try {
			this.inviteTaskManager.remove(taskId);
			resultMap.put("success", true);
			resultMap.put("message", getText("删除成功"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("success", false);
		resultMap.put("message", getText("操作失败"));
		return resultMap;
	}


	/**
	 * 图片转换工具
	 * @param record imageType
	 * @return
	 */
	private ProductImage createImage(Map<String, Object> record, Integer imageType){
		
		
		ProductImage productImage = new ProductImage();
		if ((record.get("fileId") != null) && (StringUtil.isNumber(StringUtil.null2Str(record.get("fileId"))))) {
			productImage.setImageId(StringUtil.nullToLong(record.get("fileId")));
		}

		// 文件类型
		String filePath=StringUtil.nullToString(record.get("filePath")).replace("upload/", "");
		String fileType = FileUtil.getSuffixByFilename(filePath);
		if (StringUtil.isNull(fileType)) {
			fileType = StringUtil.nullToString(record.get("fileType"));
		}

		if(StringUtil.null2Str(filePath).contains(Constants.DEPOSITORY)){
			String srcFilePath = Constants.DEPOSITORY_PATH + StringUtil.null2Str(filePath).replace(Constants.DEPOSITORY, "");
			if(FileUtil.checkFileExists(srcFilePath)){
				String realFilePath = CoreUtil.dateToPath("/images", filePath);
				if(StringUtil.compareObject(imageType, 3)) {
					realFilePath = CoreUtil.dateToPath("/videos", filePath);
				}
				String fullFilePath = Constants.EXTERNAL_IMAGE_PATH + "/upload" + realFilePath;
				boolean result = FileUploadUtil.moveFile(srcFilePath, fullFilePath);
				if (result == true && FileUtil.checkFileExists(fullFilePath)){
					filePath = realFilePath;
				}
			}
		}
		productImage.setImageType(imageType);
		productImage.setImageName(StringUtil.nullToString(record.get("fileName")));
		productImage.setImagePath(filePath);
		return productImage;
	}
}

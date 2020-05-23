package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.model.FxChannel;
import com.chunruo.core.model.FxPage;
import com.chunruo.core.service.FxChannelManager;
import com.chunruo.core.service.FxPageManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.vo.ComboVo;

/**
 * 分销市场
 * 
 * @author chunruo
 */
@Controller
@RequestMapping("/channel/")
public class FxChannelController extends BaseController {
	private Logger log = LoggerFactory.getLogger(FxChannelController.class);
	@Autowired
	private FxChannelManager fxChannelManager;
	@Autowired
	private FxPageManager fxPageManager;

	/**
	 * 分销市场列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list")
	public @ResponseBody Map<String, Object> channelList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<FxChannel> channelList = new ArrayList<>();
		int count = 0;
		try {
			// 启用和停用状态的频道数据
			List<Integer> statusList = new ArrayList<Integer>();
			statusList.add(FxChannel.FX_CHANNEL_STATUS_STOP);
			statusList.add(FxChannel.FX_CHANNEL_STATUS_ENABLE);

			channelList = this.fxChannelManager.getFxChannelListByStatusList(statusList);
			if (channelList != null && channelList.size() > 0) {
				count = channelList.size();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", channelList);
		resultMap.put("totalCount", count);
		return resultMap;
	}

	/**
	 * 分销分类页面列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/pageListByChannelId")
	public @ResponseBody Map<String, Object> pageListByChannelId(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long channelId = StringUtil.nullToLong(request.getParameter("channelId"));
		List<FxPage> pageList = new ArrayList<FxPage>();
		int count = 0;
		try {
			FxChannel fxChannel = this.fxChannelManager.get(channelId);
			if (fxChannel != null && fxChannel.getChannelId() != null) {
				pageList = this.fxPageManager.getFxPageListByChannelId(fxChannel.getChannelId());
				if (pageList != null && pageList.size() > 0) {
					count = pageList.size();
					for (FxPage fxPage : pageList) {
						fxPage.setChannelName(fxChannel.getChannelName());
						fxPage.setCategoryName("<span style=\"color:green;\"><b>内页</b></span>");
						if (StringUtil.compareObject(fxPage.getCategoryType(), 0)) {
							fxPage.setCategoryName("<span style=\"color:red;\"><b>频道首页</b></span>");
						}else if(StringUtil.compareObject(fxPage.getCategoryType(), 2)) {
							fxPage.setCategoryName("<span style=\"color:red;\"><b>专题</b></span>");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", pageList);
		resultMap.put("totalCount", count);
		return resultMap;
	}

	/**
	 * 更改频道状态（启用，删除）
	 * 
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateChannelStatus", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> updateProductSoldoutStatus(
			@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Integer status = StringUtil.nullToInteger(request.getParameter("status"));
		try {
			List<Long> idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			this.fxChannelManager.updateFxChannelStatus(idList, status);
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
	 * 频道详情
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getChannelById")
	public @ResponseBody Map<String, Object> getChannelById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long channelId = StringUtil.nullToLong(request.getParameter("channelId"));

		FxChannel fxChannel = null;
		List<FxPage> pageList = new ArrayList<FxPage>();
		try {
			fxChannel = this.fxChannelManager.get(channelId);
			if (fxChannel != null && fxChannel.getChannelId() != null) {
				pageList = this.fxPageManager.getFxPageListByChannelId(fxChannel.getChannelId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put("success", true);
		resultMap.put("channel", fxChannel);
		resultMap.put("channelPageList", pageList);

		return resultMap;
	}

	/**
	 * 渠道编辑保存
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveChannel")
	public @ResponseBody Map<String, Object> saveChannel(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String channelId = request.getParameter("channelId");
		String channelName = request.getParameter("channelName");
		String sort = request.getParameter("sort");

		try {
			// 检查频道名称是否为空
			if (StringUtil.isNull(channelName)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "错误,频道名称不能为空");
				return resultMap;
			}

			// 检查渠道是否新建
			FxChannel fxChannel = this.fxChannelManager.get(StringUtil.nullToLong(channelId));
			if (fxChannel != null && fxChannel.getChannelId() != null) {
				
				fxChannel.setChannelName(channelName);
				if (!StringUtil.isNull(sort)) {
					fxChannel.setSort(StringUtil.nullToInteger(sort));
				}
				fxChannel.setUpdateTime(DateUtil.getCurrentDate());

			} else {
				fxChannel = new FxChannel();
				fxChannel.setStatus(0);// 默认禁用
				fxChannel.setChannelName(channelName);
				fxChannel.setSort(StringUtil.nullToInteger(sort));
				fxChannel.setCreateTime(DateUtil.getCurrentDate());
				fxChannel.setUpdateTime(fxChannel.getCreateTime());
			}

			this.fxChannelManager.save(fxChannel);
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", "保存成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", "错误,保存失败");
		return resultMap;
	}

	/**
	 * 预览编辑页面
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getimageListByPageId")
	public @ResponseBody Map<String, Object> getimageListByPageId(final HttpServletRequest request) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long pageId = StringUtil.nullToLong(request.getParameter("pageId"));
		try {
			String table = this.fxPageManager.getImages(pageId);
			resultMap.put("success", true);
			resultMap.put("table", table);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("success", false);
		return resultMap;
	}

	/**
	 * 新建页面
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/savePage")
	public @ResponseBody Map<String, Object> savePage(final HttpServletRequest request) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String pageName = request.getParameter("pageName");
		Long channelId = StringUtil.nullToLong(request.getParameter("channelId"));
		Integer typeId = StringUtil.nullToInteger(request.getParameter("typeId"));
		byte[] bytes=pageName.getBytes("UTF-8");
		if (StringUtil.isNull(pageName)) {
			resultMap.put("success", false);
			resultMap.put("message", "页面名称不能为空");
			return resultMap;
		}else if(bytes.length > 30) {
			resultMap.put("success", false);
			resultMap.put("message", "页面名称过长");
			return resultMap;
		}

		
		
		try {
			FxChannel fx = null;
			if (StringUtil.isNull(channelId) || StringUtil.compareObject(channelId, 0)
					|| (fx = fxChannelManager.get(channelId)) == null || fx.getChannelId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", "频道不存在");
				return resultMap;
			}

			// 频道首页面
			if (StringUtil.compareObject(FxPage.CATEGORY_TYPE_HOME, typeId)) {
				List<FxPage> pageList = fxPageManager.getFxPageListByChannelId(channelId);
				if (null != pageList && pageList.size() > 0) {
					for (FxPage p : pageList) {
						if (StringUtil.compareObject(FxPage.CATEGORY_TYPE_HOME, p.getCategoryType())) {
							p.setCategoryType(FxPage.CATEGORY_TYPE_PAGE);
							p.setUpdateTime(new Date());
							fxPageManager.update(p);
						}
					}
				}
			}
			
				FxPage page = new FxPage();
				page.setChannelId(channelId);
				page.setCategoryType(typeId);
				page.setPageName(pageName);
				page.setCreateTime(new Date());
				page.setIsDelete(false);
				page.setUpdateTime(page.getCreateTime());
				this.fxPageManager.saveFxPage(page);
			
			
			resultMap.put("success", true);
			resultMap.put("message", "保存成功");
			return resultMap;
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("message", "保存失败");
		return resultMap;
	}

	/**
	 * 编辑页面
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/editPage")
	public @ResponseBody Map<String, Object> editPage(final HttpServletRequest request) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String pageId = request.getParameter("pageId");
		String pageName = request.getParameter("pageName");
		Long channelId = StringUtil.nullToLong(request.getParameter("channelId"));
		Integer typeId = StringUtil.nullToInteger(request.getParameter("typeId"));
		byte[] bytes=pageName.getBytes("UTF-8");
		if (StringUtil.isNull(pageName)) {
			resultMap.put("success", false);
			resultMap.put("message", "页面名称不能为空");
			return resultMap;
		}else if(bytes.length > 30) {
			resultMap.put("success", false);
			resultMap.put("message", "页面名称过长");
			return resultMap;
		}

		try {
			FxChannel fx = null;
			if (StringUtil.isNull(channelId) || StringUtil.compareObject(channelId, 0)
					|| (fx = fxChannelManager.get(channelId)) == null || fx.getChannelId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", "频道不存在");
				return resultMap;
			}

			// 频道首页面
			if (StringUtil.compareObject(FxPage.CATEGORY_TYPE_HOME, typeId)) {
				List<FxPage> pageList = fxPageManager.getFxPageListByChannelId(channelId);
				if (null != pageList && pageList.size() > 0) {
					for (FxPage p : pageList) {
						if (StringUtil.compareObject(FxPage.CATEGORY_TYPE_HOME, p.getCategoryType())) {
							p.setCategoryType(FxPage.CATEGORY_TYPE_PAGE);
							p.setUpdateTime(new Date());
							fxPageManager.update(p);
						}
					}
				}
			}

			FxPage page = fxPageManager.get(StringUtil.stringToLong(pageId));
			if (null != page) {
				page.setPageName(pageName);
				page.setCategoryType(typeId);
				page.setUpdateTime(new Date());
				this.fxPageManager.update(page);
			}else{
				resultMap.put("success", false);
				resultMap.put("message", "页面不存在");
				return resultMap;
			}

			resultMap.put("success", true);
			resultMap.put("message", "保存成功");
			return resultMap;
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("message", "保存失败");
		return resultMap;
	}

	/**
	 * 设置首页
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/setHomePage")
	public @ResponseBody Map<String, Object> setHomePage(final HttpServletRequest request) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long pageId = StringUtil.nullToLong(request.getParameter("pageId"));

		try {
			FxPage page = this.fxPageManager.get(pageId);
			if (page == null || page.getPageId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", "页面不存在");
				return resultMap;
			} else if (StringUtil.compareObject(page.getCategoryType(), FxPage.CATEGORY_TYPE_HOME)) {
				resultMap.put("success", false);
				resultMap.put("message", "当前页面已经是首页");
				return resultMap;
			}else if(StringUtil.compareObject(page.getCategoryType(),FxPage.CATEGORY_TYPE_THEME )) {
				resultMap.put("success", false);
				resultMap.put("message", "专题页面不可设置为首页");
				return resultMap;
			}

			Long channelId = page.getChannelId();
			FxChannel fx = fxChannelManager.get(channelId);
			if (fx == null || fx.getChannelId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", "频道不存在");
				return resultMap;
			}

			List<FxPage> pageList = this.fxPageManager.getFxPageListByChannelId(channelId);
			if (pageList != null && pageList.size() > 0) {
				for (FxPage p : pageList) {
					if (StringUtil.compareObject(p.getCategoryType(), FxPage.CATEGORY_TYPE_HOME)) {
						p.setCategoryType(FxPage.CATEGORY_TYPE_PAGE);
						p.setUpdateTime(new Date());
						fxPageManager.update(p);
					}
				}
			}

			page.setCategoryType(FxPage.CATEGORY_TYPE_HOME);
			page.setUpdateTime(new Date());
			this.fxPageManager.saveFxPage(page);

			resultMap.put("success", true);
			resultMap.put("message", "设置成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("message", "设置失败");
		return resultMap;
	}

	/**
	 * 删除页面
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/deletePage")
	public @ResponseBody Map<String, Object> deletePage(final HttpServletRequest request) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String pageIds = request.getParameter("pageIds");
		if (StringUtil.isNull(pageIds)) {
			resultMap.put("success", false);
			resultMap.put("message", "请选择要删除的页面");
			return resultMap;
		}

		try {
			List<Long> pageIdList = (List<Long>) StringUtil.getIdLongList(pageIds);
			if (pageIdList == null || pageIdList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", "请选择要删除的页面");
				return resultMap;
			}

			List<FxPage> fxPageList = this.fxPageManager.getByIdList(pageIdList);
			if (fxPageList == null || fxPageList.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", "请选择要删除的页面");
				return resultMap;
			}

			// 检查删除是否包含频道删除
			if (fxPageList != null && fxPageList.size() > 0) {
				for (FxPage fxPage : fxPageList) {
					fxPage.setIsDelete(true);
					fxPage.setUpdateTime(new Date());
					if (StringUtil.compareObject(fxPage.getCategoryType(), FxPage.CATEGORY_TYPE_HOME)) {
						resultMap.put("success", false);
						resultMap.put("message", String.format("错误,『%s』为首页不能直接删除", fxPage.getPageName()));
						return resultMap;
					}
				}
				this.fxChannelManager.updateFxChannelUpdateTimeByChannelId(fxPageList.get(0).getChannelId());
			}

			this.fxPageManager.batchInsert(fxPageList, fxPageList.size());
			resultMap.put("success", true);
			resultMap.put("message", "删除成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("message", "错误,删除失败");
		return resultMap;
	}
	
	/**
	 * 获取所有正在使用的频道供ext ComboBox使用
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getComboChannelList")
	public @ResponseBody Map<String, Object> getComboChannelList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		List<ComboVo>  comboList = new ArrayList<ComboVo>(); 
		try {
			List<FxChannel> channelList = fxChannelManager.getFxChannelListByStatus(FxChannel.FX_CHANNEL_STATUS_ENABLE);
			if (channelList != null && channelList.size() > 0){
				for (FxChannel fxChannel : channelList){
					ComboVo comboVo = new ComboVo();
					comboVo.setId(fxChannel.getChannelId());
					comboVo.setName(fxChannel.getChannelName());
					comboList.add(comboVo);
				}
			}
			resultMap.put("data", comboList);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 获取频道下所有正在使用的内页除首页外供ext ComboBox使用
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getComboPageListByChannelId")
	public @ResponseBody Map<String, Object> getComboPageListByChannelId(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Long channelId = StringUtil.nullToLong(request.getParameter("channelId"));
		List<ComboVo>  comboList = new ArrayList<ComboVo>(); 
		try {
			List<FxPage> pageList = fxPageManager.getFxPageListByChannelId(channelId);
			if (pageList != null && pageList.size() > 0){
				for (FxPage page : pageList){
					if (StringUtil.compareObject(page.getCategoryType(), FxPage.CATEGORY_TYPE_PAGE)){
						ComboVo comboVo = new ComboVo();
						comboVo.setId(page.getPageId());
						comboVo.setName(page.getPageName());
						comboList.add(comboVo);
					}
				}
			}
			resultMap.put("data", comboList);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return resultMap;
	}
}

//package com.didispace;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.chunruo.core.Application;
//import com.chunruo.core.model.FxChannel;
//import com.chunruo.core.model.FxChildren;
//import com.chunruo.core.model.FxChildrenGroup;
//import com.chunruo.core.model.FxPage;
//import com.chunruo.core.model.PigcmsFxChannel;
//import com.chunruo.core.model.PigcmsFxChildren;
//import com.chunruo.core.model.PigcmsFxPage;
//import com.chunruo.core.repository.FxChannelRepository;
//import com.chunruo.core.repository.FxChildrenGroupRepository;
//import com.chunruo.core.repository.FxChildrenRepository;
//import com.chunruo.core.repository.FxPageRepository;
//import com.chunruo.core.repository.PigcmsFxChannelRepository;
//import com.chunruo.core.repository.PigcmsFxChildrenRepository;
//import com.chunruo.core.repository.PigcmsFxPageRepository;
//import com.chunruo.core.util.DateUtil;
//import com.chunruo.core.util.JsonParseUtil;
//import com.chunruo.core.util.StringUtil;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//public class ApplicationTests {
//
//	@Autowired
//	private PigcmsFxChannelRepository pigcmsFxChannelRepository;
//	
//	@Autowired
//	private PigcmsFxChildrenRepository pigcmsFxChildrenRepository;
//	
//	@Autowired
//	private PigcmsFxPageRepository pigcmsFxPageRepository;
//	
//	@Autowired
//	private FxChannelRepository fxChannelRepository;
//	
//	@Autowired
//	private FxPageRepository fxPageRepository;
//	
//	@Autowired
//	private FxChildrenRepository fxChildrenRepository;
//	
//	@Autowired
//	private FxChildrenGroupRepository fxChildrenGroupRepository;
//	
//	@Test
//	public void test() throws Exception {
//		List<PigcmsFxPage> pageList = this.pigcmsFxPageRepository.findAll();
//		
//		List<PigcmsFxChannel> channelList = pigcmsFxChannelRepository.findAll();
//		
//		List<PigcmsFxChildren> childrenList = pigcmsFxChildrenRepository.findAll();
//		
//		List<FxChannel> resutltChannelList = new ArrayList<FxChannel>();
//		for(PigcmsFxChannel each : channelList){
//			FxChannel bean = new FxChannel();
//			Date parseDate = getParseDate(each.getAddTime());
//			bean.setCreateTime(parseDate);
//			bean.setUpdateTime(parseDate);
//			bean.setName(each.getName());
//			bean.setSort(each.getSort());
//			bean.setStatus(each.getStatus());
//			bean.setId(Long.parseLong(each.getId().toString()));
//			resutltChannelList.add(bean);
//			fxChannelRepository.save(bean);
//		}
//		
//		List<FxPage> resutltPageList = new ArrayList<FxPage>();
//		for(PigcmsFxPage each : pageList){
//			FxPage bean = new FxPage();
//			Date parseDate = getParseDate(each.getAddTime());
//			bean.setPageId(Long.parseLong(each.getPageId().toString()));
//			bean.setCreateTime(parseDate);
//			bean.setUpdateTime(parseDate);
//			bean.setCategory(each.getCategory());
//			bean.setChannelId(each.getChannelId());
//			bean.setPageName(each.getPageName());
//			bean.setPageId(Long.parseLong(each.getPageId().toString()));
//			resutltPageList.add(bean);
//			fxPageRepository.save(bean);
//		}
//		
//		List<FxChildren> resutlt1List = new ArrayList<FxChildren>();
//		List<FxChildrenGroup> resutlt2List = new ArrayList<FxChildrenGroup>();
//		for(PigcmsFxChildren each : childrenList){
//			FxChildren bean1 = new FxChildren();
//			Date parseDate = getParseDate(each.getAddTime());
//			
//			long childrenId = Long.parseLong(each.getChildrenId().toString());
//			bean1.setChildrenId(childrenId);
//			bean1.setAttribute(each.getAttribute());
//			bean1.setCreateTime(parseDate);
//			bean1.setPageId(each.getPageId());
//			bean1.setPicture(each.getPicture());
//			bean1.setSort(each.getSort());
//			bean1.setSpecialName(each.getSpecialName());
//			bean1.setType(each.getType());
//			bean1.setUpdateTime(parseDate);
//			resutlt1List.add(bean1);
//			fxChildrenRepository.save(bean1);
//			
//			String contents = each.getContents();
//			List<Map<String, Object>> parseJSON2List = JsonParseUtil.parseJSON2List(contents);
//			for(Map<String, Object> map : parseJSON2List){
//				FxChildrenGroup bean2 = new FxChildrenGroup();
//				String picture = map.get("picture").toString();
//				String target_type = map.get("target_type").toString();
//				String content = map.get("content").toString();
//				String navigation_name = map.get("navigation_name").toString();
//				
//				bean2.setContent(content);
//				bean2.setNavigationName(navigation_name);
//				bean2.setParentId(childrenId);
//				bean2.setPicture(picture);
//				bean2.setTargetType(StringUtil.isNull(target_type) ? null : Integer.parseInt(target_type));
//				resutlt2List.add(bean2);
//				fxChildrenGroupRepository.save(bean2);
//			}
//		}
////		fxChannelRepository.batchInsert(resutltChannelList, resutltChannelList.size());
////		fxPageRepository.batchInsert(resutltPageList, resutltPageList.size());
////		fxChildrenRepository.batchInsert(resutlt1List, resutlt1List.size());
////		fxChildrenGroupRepository.batchInsert(resutlt2List, resutlt2List.size());
//		
//	}
//
//	private Date getParseDate(int addTime) {
//		Date date = new Date(addTime * 1000l);
//		String formatDate = DateUtil.yyyyMMddHHmmss_formate.format(date);
//		Date parseDate = DateUtil.parseDate(DateUtil.yyyyMMddHHmmss_formate, formatDate);
//		return parseDate;
//	}
//}

//package com.didispace;
//
//import java.util.List;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.chunruo.core.model.UserAddress;
//import com.chunruo.core.service.UserAddressManager;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = ApplicationTests.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//public class ApplicationManagerTests {
//	
//	@Autowired
//	UserAddressManager userAddressManager;
//	
////	@Autowired
////	ProductCategoryManager productCategoryManager;
////	
////	@Autowired
////	ProductWholesaleManager productWholesaleManager;
////	
////	@Autowired
////	CountryMobilePrefixManager countryMobilePrefixManager;
////	
////	@Autowired
////	ProductManager productManager;
////	
////	@Autowired
////	OrderManager orderManager;
////	
////	@Test
////	public void testFxChannel() throws Exception {
////		fxChannelManager.getFxChannelDetail(null, null, 15L, null);
////	}
////	
////	
////	@Test
////	public void testCategory() throws Exception {
////		productCategoryManager.getProductCategoryList();
////	}
////	
////	@Test
////	public void testProduct() throws Exception {
////		productWholesaleManager.getWholesaleProductList(null, null, 144L, "Cellucor", "price", "asc", 1, 20);
////	}
////	
////	@Test
////	public void testProductDetail() throws Exception {
////		ProductVo productDetail = productWholesaleManager.getProductDetail(1L, 171L);
////		String bean2json = JsonUtil.bean2json(productDetail);
////		System.out.println(bean2json);
////	}
////	
////	@Test
////	public void testCountryMobilePrefix() throws Exception {
////		List<Long> countryIdList = new ArrayList<Long>();
////		countryIdList.add(214L);
////		countryIdList.add(396L);
////		countryIdList.add(397L);
////		countryIdList.add(413L);
////		countryIdList.add(414L);
////		TreeMap<String, List<CountryMobilePrefix>> countryMobilePrefixList = countryMobilePrefixManager.getCountryMobilePrefixList(countryIdList);
////		String bean2json = JsonUtil.bean2json(countryMobilePrefixList);
////		System.out.println(bean2json);
////	}
////	
////	@Test
////	public void testProductList() throws Exception {
////		Map<String,Object> paramMap = new HashMap<String,Object>();
////    	paramMap.put("userId",2);
////    	paramMap.put("status", 1);
////    	paramMap.put("isWholesale", 1);
////		productManager.getPageProductList(1113L, 0, 10, paramMap);
////	}
//	
//	@Test
//	public void testOrder() throws Exception {
//		List<UserAddress> list = this.userAddressManager.getUserAddressListByUserId(7L);
//		System.out.println(list.size());
////		TModel<List<OrderVO>> orderList = orderManager.getOrderList(8L, 10645L, 1, 1, 20);
////		String bean2json = JsonUtil.bean2json(orderList.gettModel());
//		//System.out.println(bean2json);
//	}
//}

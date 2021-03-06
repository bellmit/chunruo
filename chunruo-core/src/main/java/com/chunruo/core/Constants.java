package com.chunruo.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import com.chunruo.core.model.Area;
import com.chunruo.core.model.Bank;
import com.chunruo.core.model.Country;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.CouponTask;
import com.chunruo.core.model.ExpressCode;
import com.chunruo.core.model.Keywords;
import com.chunruo.core.model.PostageTemplate;
import com.chunruo.core.model.ProductBrand;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.ProductIntro;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.model.PurchaseDoubt;
import com.chunruo.core.model.RefundReason;
import com.chunruo.core.model.RollingNotice;
import com.chunruo.core.model.SignImageText;
import com.chunruo.core.model.UserLevelExplain;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.util.Configuration;

public class Constants {
	public static volatile String DEPOSITORY = "depository";
	public static volatile String DEPOSITORY_PATH = "";
	public final static String CHANNEL_ALL = "ALL";
	public final static String PUB_DIR_ORDER = "order";
	public final static String PUB_DIR_RETURN = "return";
	public final static String PUB_DIR_WMS_GROME_ORDER = "wmsGromeOrder";
	public final static String PUB_DIR_WMS_NEW_QUICK_ORDER = "wmsNewQuickOrder";
	public final static String CROSS_PRODUCT_LIMIT_MSG = "超出限额";
	public static String SERVER_REAL_PATH = null;
	
	
	
	public static final Long ADMINSTARTOR_ID = 1L;
	public static final int ERP_PAGE_SIZE = 20;
	public static final String ERP_NAME_XHTD = "xhtd";
	public static final String ERP_XMP_XHTD = "xml";
	public static final String ERP_XMP_XHTD_JSON = "json";
	public static final String RER_SUCCESS = "isSuccess";
	public static final String RER_ERRORCODE = "errorCode";
	public static final String RER_ERRORMSG = "errorMsg";
	
	public static ApplicationContext ctx = null;
	public static Configuration conf = null;
	public static Long APP_CLIENT_WECHAT_CONFIG_ID = null;
	public static Long PUBLIC_ACCOUNT_WECHAT_CONFIG_ID = null;
	public static Long MINI_PROGRAM_WECHAT_CONFIG_ID = null;
	public static Long JKD_PROGRAM_WECHAT_CONFIG_ID = null;
	public static Long PUBLIC_INVITE_ACCOUNT_CONFIG_ID = null;
	public static String EXTERNAL_IMAGE_PATH = null;
	public static String EXTERNAL_IMAGE_OSS_PATH = null;
	public static String IMAGE_MAGICK_CONVERT_PATH = null;
	public static final Integer PAGE_SIZE = 10;
	public static final Long TOP_NODE_ID = new Long(0);
	public static final Long TOP_DEFUALT_ID = new Long(1);
	public static final Integer NO = new Integer(0);
	public static final Integer YES = new Integer(1);
	public static final Double MAX_PRODUCT_AMOUNT = new Double (2600);
	public static final Double MAX_PRODUCT_GO_AMOUNT = new Double (1000);
	public static final Double MONTH_SALE_WEIGHT = new Double(0.2);
	public static final Double ACTIVITY_SALE_WEIGHT = new Double(0.3);
	public static final Double CUSTOM_SALE_WEIGHT = new Double(0.5);
	public static final Long PRODUCT_RECOMMEND_TAG_ID = 2L;
	public static final Double GAP_AMOUNT = new Double(0.03);
	public static final Double MIN_PAY_AMOUNT = new Double(0.1);
	public static Double REAL_RATE = new Double(0);
	public static final String MENU_TREE_MAPS = "allMenuTreeMaps";
	public static final String POSTAGE_TPL_MAPS = "allPostageTplMaps";
	public static final String PRODUCT_INTRO_MAPS = "allProductIntroMaps";
	public static final String PURCHASE_DOUBT_MAPS = "allPurchaseDoubtMaps";
	public static final String PRODUCT_COUNTRY_MAPS = "allProductCountryMaps";
	public static final String PRODUCT_CATEGORY_LISTS = "allProductCategoryLists";
	public static final String PRODUCT_WAREHOUSE_MAPS = "allProductWarehouseLists";
	public static final String HANDER_WAREHOUSE_MAPS = "allHanderWarehouseLists";
	public static final String NOFree_POSTAGE_TEMPLATE_MAPS="allNoFreePostageTemplateMaps";
	public static final String EXPRESS_CODE_MAPS = "allExpressCodeMaps";
	public static final String WEIXIN_TOKEN_MAP_KEY = "token";
	//系统缓存对象
	public static Map<Long, ProductWarehouse> PRODUCT_WAREHOUSE_MAP = new HashMap<Long, ProductWarehouse> ();
	public static Map<Long, ProductWarehouse> HANDER_WAREHOUSE_MAP = new HashMap<Long, ProductWarehouse> ();
	public static Map<String,String> WEIXIN_TOKEN_MAP = new HashMap<String, String>();
	public static Map<String,String> TX_IM_USER_SIG_MAP = new HashMap<String,String>();
	public static List<ProductCategory> PRODUCT_CATEGORY_TREE_LIST = new ArrayList<ProductCategory> ();
	public static Map<Long, ProductCategory> PRODUCT_CATEGORY_MAP = new HashMap<Long, ProductCategory> ();
	
	public static List<Long> AUTO_OPERATOR_USERID_LIST = new ArrayList<Long> ();
	public static Map<Long, Area> AREA_MAP = new HashMap<Long, Area> ();
	public static List<Area> PROVINCE_AREA_LIST = new ArrayList<Area> ();
	public static Map<Long, List<Area>> CITY_ARE_AMAP = new HashMap<Long, List<Area>> ();
	public static Map<Long, List<Area>> COUNTRY_AREA_MAP = new HashMap<Long, List<Area>> ();
	public static Map<Long, List<Area>> AREA_LIST_MAP = new HashMap<Long, List<Area>> ();
	public static List<Area> AREA_TREE_LIST = new ArrayList<Area> ();
	public static Map<Long, WeChatAppConfig> WECHAT_CONFIG_ID_MAP = new HashMap<Long, WeChatAppConfig> ();
	public static Map<String, WeChatAppConfig> WECHAT_APP_ID_MAP = new HashMap<String, WeChatAppConfig> ();
	public static Map<Long, ProductIntro> PRODUCT_INTRO_MAP = new HashMap<Long, ProductIntro> ();
	public static Map<Long, Country> PRODUCT_COUNTRY_MAP = new HashMap<Long, Country> ();
	public static Map<String, ExpressCode> EXPRESS_CODE_MAP = new HashMap<String, ExpressCode> ();
	public static List<Bank> BANK_LIST = new ArrayList<Bank> ();
	public static List<Bank> BANK_QUICK_LIST = new ArrayList<Bank> ();
	public static Map<Long, Bank> BANK_MAP = new HashMap<Long, Bank> ();
	public static Map<Long, RefundReason> REFUND_REASON_MAP = new HashMap<Long, RefundReason> ();
	public static Map<Integer, List<UserLevelExplain>> USER_LEVEL_EXPLAIN_LIST_MAP = new HashMap<Integer, List<UserLevelExplain>> ();
	public static Map<Long, CouponTask> COUPON_TASK_MAP = new HashMap<Long, CouponTask> ();
	public static Map<Long, Coupon> COUPON_MAP = new HashMap<Long, Coupon> ();
	public static Map<Integer, RollingNotice> ROLLING_NOTICE_MAP = new HashMap<Integer, RollingNotice> ();
	public static Map<Long,PostageTemplate> NOFree_POSTAGE_TEMPLATE_MAP=new HashMap<Long,PostageTemplate>();
	public static Map<Long, PurchaseDoubt> PURCHASE_DOUBT_MAP = new HashMap<Long, PurchaseDoubt> ();
	public static Keywords  DEFAULT_KEYWORDS = new Keywords();
	public static List<Keywords> KEYWORDS_ALL_LIST = new ArrayList<Keywords> ();
	public static Map<Long, ProductBrand> PRODUCT_BRAND_MAP = new HashMap<Long, ProductBrand> ();
	public static List<SignImageText> SIGN_IMAGE_TEXT_LIST = new ArrayList<SignImageText>();
	public static List<Object[]> CATEGORY_BRAND_LIST = new ArrayList<Object[]> ();
	
	public static class WechatOautType{
		public final static int WECHAT_OAUTH_TYPE_LOGIN = 0;	//微信授权登陆成功
		public final static int WECHAT_OAUTH_TYPE_NEW = 1;		//微信授权全新用户注册
		public final static int WECHAT_OAUTH_TYPE_AGENT = 2;	//微信授权普通用户转换成代理商
		public final static int WECHAT_OAUTH_TYPE_MOBILE = 3;	//微信授权代理商补手机号码
	}
	
	public static class BuyPostType{
		public static final Integer POST_BUY_QUICK_TYPE = 0;		//立即购买
		public static final Integer POST_BUY_CART_TYPE = 1;			//购物车结算
		public static final Integer POST_BUY_STACK_TYPE = 2;		//批量导入下单
	}
	
	public static class PayWay{
		public static final Integer PAY_WAY_CASH = 0;		//现金支付
		public static final Integer PAY_WAY_ACCOUNT = 1;    //账户余额支付
	}
	
	public static class PaymentType{
		public static final Integer PAYMENT_TYPE_FRIEND = -1;		//代付请求
		public static final Integer PAYMENT_TYPE_WECHAT = 0;		//微信支付
		public static final Integer PAYMENT_TYPE_ALIPAY = 1;		//支付宝支付
		public static final Integer PAYMENT_TYPE_HUIFU = 2;			//上海汇付
		public static final Integer PAYMENT_TYPE_EASYPAY = 3;		//易生支付
	}
	
	public static class UserCouponStatus{
		public static final Integer USER_COUPON_STATUS_NOT_USED = 0;	//未使用
		public static final Integer USER_COUPON_STATUS__USED = 1;		//已使用
		public static final Integer USER_COUPON_STATUS_EXPIRED = 2;		//已过期
	}
	
	public static Map<Integer,String> paymentTypeMap = new HashMap<Integer,String>();
	static {
		paymentTypeMap.put(PaymentType.PAYMENT_TYPE_WECHAT, "微信支付");
		paymentTypeMap.put(PaymentType.PAYMENT_TYPE_ALIPAY, "支付宝支付");
	}
	
	public static class GoodsType{
		public final static Integer GOODS_TYPE_COMMON = 1;		//国内
		public final static Integer GOODS_TYPE_CROSS = 2;		//跨境,身份证号码和真实姓名
		public final static Integer GOODS_TYPE_DIRECT = 3;		//BC直邮,身份证号码和真实姓名，身份证照片正反面
		public final static Integer GOODS_TYPE_DIRECT_GO = 4;	//行邮,身份证号码和真实姓名，身份证照片正反面
	}
	
	public static class NoticeType{
		public final static Integer NOTICE_TYPE_NO = 1;		//设置到货通知
		public final static Integer NOTICE_TYPE_YES = 2;	//已设置到货通知
	}
	
	public static List<Integer> PRODUCT_TYPE_ALL_LIST = new ArrayList<Integer>();
	public static List<Integer> PRODUCT_TYPE_CROSS_LIST = new ArrayList<Integer>();
	static {
		PRODUCT_TYPE_ALL_LIST.add(GoodsType.GOODS_TYPE_COMMON );		//国内
		PRODUCT_TYPE_ALL_LIST.add(GoodsType.GOODS_TYPE_CROSS );			//跨境,身份证号码和真实姓名
		PRODUCT_TYPE_ALL_LIST.add(GoodsType.GOODS_TYPE_DIRECT );		//BC直邮,身份证号码和真实姓名，身份证照片正反面
		PRODUCT_TYPE_ALL_LIST.add(GoodsType.GOODS_TYPE_DIRECT_GO );		//行邮,身份证号码和真实姓名，身份证照片正反面
		
		PRODUCT_TYPE_CROSS_LIST.add(GoodsType.GOODS_TYPE_CROSS );		//跨境,身份证号码和真实姓名
		PRODUCT_TYPE_CROSS_LIST.add(GoodsType.GOODS_TYPE_DIRECT );		//BC直邮,身份证号码和真实姓名，身份证照片正反面
		PRODUCT_TYPE_CROSS_LIST.add(GoodsType.GOODS_TYPE_DIRECT_GO );	//行邮,身份证号码和真实姓名，身份证照片正反面
	}	
	
	// 用户等级
	public static class UserLevel{
		public final static Integer USER_LEVEL_COMMON = 0;		//普通用户
		public final static Integer USER_LEVEL_BUYERS= 1;		//店长
		public final static Integer USER_LEVEL_DEALER = 2;		//经销商
		public final static Integer USER_LEVEL_AGENT = 3;		//总代
		public final static Integer USER_LEVEL_V2 = 4;		    //V2
		public final static Integer USER_LEVEL_V3 = 5;		    //V3
	
		public static List<Integer> ALL_USER_LEVEL = new ArrayList<Integer> ();
		static{
			UserLevel.ALL_USER_LEVEL.add(UserLevel.USER_LEVEL_COMMON);//普通用户
			UserLevel.ALL_USER_LEVEL.add(UserLevel.USER_LEVEL_BUYERS);//店长
			UserLevel.ALL_USER_LEVEL.add(UserLevel.USER_LEVEL_DEALER);//经销商
			UserLevel.ALL_USER_LEVEL.add(UserLevel.USER_LEVEL_AGENT);//总代
			UserLevel.ALL_USER_LEVEL.add(UserLevel.USER_LEVEL_V2);//V2
			UserLevel.ALL_USER_LEVEL.add(UserLevel.USER_LEVEL_V3);//V3
		}
	}	
	
	// 推手等级
	public static class PushUserLevel{
		public final static Integer PUSH_USER_LEVEL_COMMOM = 0;		//普通用户
		public final static Integer PUSH_USER_LEVEL_PRACTICE = 1;   //实习推手
		public final static Integer PUSH_USER_LEVEL_PARTNER = 2;    //合作推手
		public final static Integer PUSH_USER_LEVEL_MANAGER = 3;    //推广经理
	}	
	
	//'订单状态  1未支付 2未发货 3已发货 4已完成 5已取消 6退款中 ',
	public static class OrderStatus{
		public static final Integer NEW_ORDER_STATUS = 1;     			//未支付
		public static final Integer UN_DELIVER_ORDER_STATUS = 2;		//未发货
		public static final Integer DELIVER_ORDER_STATUS = 3;			//已发货
		public static final Integer OVER_ORDER_STATUS = 4;				//已完成 
		public static final Integer CANCEL_ORDER_STATUS = 5;			//已取消
		public static final Integer INTERCEPT_ORDER_STATUS = 7;			//拦截状态(虚拟值)
	}
	
	//会员礼包类型
	public static class MemberGiftType{
		public static final Integer NO_GIFT_MEMBER_TYPE = 1;      //无赠品
		public static final Integer COUPON_GIFT_MEMBER_TYPE = 2;  //优惠券
		public static final Integer PRODUCT_GIFT_MEMBER_TYPE = 3; //商品
		public static final Integer ALL_GIFT_MEMBER_TYPE = 4;     //商品和优惠券
	}
	
	//未收货订单状态
	public static class UnDeliverStatus{
		public static final Integer UN_DELIVER_STATUS = 0;           //代发货状态(一小时内订单)
		public static final Integer UN_DELIVER_CANCELING = 1;        //取消受理中
		public static final Integer UN_DELIVER_INTERCEPT_FAIL = 2;   //拦截失败
		public static final Integer UN_DELIVER_RESTORE = 3;          //继续发货
	}
	public static class PushCustomStatus{
		public final static Integer PUSH_CUSTOM_STAUTS_NO = 0; 			//未推送
		public final static Integer PUSH_CUSTOM_STAUTS_WAIT = 1; 		//已推送
	    public final static Integer PUSH_CUSTOM_STAUTS_SUC = 2; 		//推送成功
	    public final static Integer PUSH_CUSTOM_STAUTS_FAIL = 3; 		//推送失败
	}
	
	//物流状态
	public static class LogisticsStatus{
		public static final Integer LOGISTICS_STATUS_SUCC = 1;          //下单成功
		public static final Integer LOGISTICS_STATUS_SENT = 2;          //已发货
		public static final Integer LOGISTICS_STATUS_CANCEL = 3;        //已取消
		public static final Integer LOGISTICS_STATUS_FINISH = 4;        //已完成
		public static final Integer LOGISTICS_STATUS_CANCELING = 5;     //订单取消受理中
		public static final Integer LOGISTICS_STATUS_CANCEL_REFUSE = 6; //订单取消失败（后台人员操作继续发货）
		public static final Integer LOGISTICS_STATUS_CANCEL_SENT = 7;   //订单取消失败（已推送至海关）
	}
	
	public final static Map<Integer,String> orderStatusMap = new HashMap<Integer,String>();
	static {
		orderStatusMap.put(OrderStatus.NEW_ORDER_STATUS, "未支付");
		orderStatusMap.put(OrderStatus.UN_DELIVER_ORDER_STATUS, "未发货");
		orderStatusMap.put(OrderStatus.DELIVER_ORDER_STATUS, "已发货");
		orderStatusMap.put(OrderStatus.OVER_ORDER_STATUS, "已完成");
		orderStatusMap.put(OrderStatus.CANCEL_ORDER_STATUS, "已取消");
	}
		
	public final static Map<String,String> packageComMap = new HashMap<String, String>();
	static {
		packageComMap.put("德邦物流", "deppon");
		packageComMap.put("ems快递", "ems");
		packageComMap.put("汇通速递", "huitongkuaidi");
		packageComMap.put("汇通快递", "huitongkuaidi");
		packageComMap.put("快捷速递", "kuaijiesudi");
		packageComMap.put("快捷快递", "kuaijiesudi");
		packageComMap.put("其他", "OTHER");
		packageComMap.put("申通快递", "shentong");
		packageComMap.put("顺丰速运", "shunfeng");
		packageComMap.put("顺丰快递", "shunfeng");
		packageComMap.put("圆通速递", "yuantong");
		packageComMap.put("圆通快递", "yuantong");
		packageComMap.put("韵达快递", "yunda");
		packageComMap.put("中通速递", "zhongtong");
		packageComMap.put("中通快递", "zhongtong");
	}
	
	//1:进行中;2:退款;3:成功;4:失败
	public static class FinancialRecordStatus{
		public static final Integer NEW_STATUS = 1;			//进行中
		public static final Integer REFUND_STATUS = 2;		//退款
		public static final Integer SUCCESS_STATUS = 3;		//成功
		public static final Integer FAILED_STATUS = 4;		//失败
	}

	//提现状态(1:申请中;3:提现成功;4:提现失败)
	public static class WithdrawalStatus{
		public static final Integer NEW_STATUS = 1; 		// 申请中
		public static final Integer SUCCESS_STATUS = 3;		//提现成功
		public static final Integer FAILED_STATUS = 4;		//提现失败
	}
	
	public static class CustomType{
		public static final String PRODUCT_GTOUP_TYPE = "good_cat"; //自定义分组
		public static final String WEI_PAGE_TYPE = "page";		  //自定义微页面模板
	}
	
	//自定义字段类型
	public static class FiledType{
		public static final String GOODS_TYPE = "goods"; //产品
		public static final String TITLE_TYPE = "title"; //标题
		public static final String TPL_SHOP_TYPE = "tpl_shop"; //
		public static final String SEARCH_TYPE = "search"; //搜索
		public static final String RICH_TEXT_TYPE = "rich_text"; ////富文本内容区域
		public static final String TEXT_NAV_TYPE = "text_nav"; //
		public static final String IMAGE_AD_TYPE = "image_ad"; //图片广告
		public static final String IMAGE_THEME_TYPE = "image_theme"; //图片专题
		public static final String IMAGE_MODULE_TYPE = "image_module"; //图片模块
		public static final String IMAGE_TEXT_TYPE = "text_module"; //图片模块
		public static final String IMAGE_SECKILL_TYPE = "image_seckill"; //图片秒杀
		public static final String LINE_TYPE = "line"; //辅助线
		public static final String LINK_TYPE = "link"; //
		public static final String STORE_TYPE = "store"; //
		public static final String IMAGE_NAV_TYPE = "image_nav"; //图片导航
		public static final String TPL_SHOP_1_TYPE = "tpl_shop1"; //logo抬头
		public static final String ATTENTION_COLLECT_TYPE = "attention_collect"; //
		public static final String NOTICE_TYPE = "notice"; //店铺公告
		public static final String WHITE_TYPE = "white"; ////辅助空白
		public static final String SPECIAL_SUBJECT_TYPE = "special"; //产品
	}
  	
}

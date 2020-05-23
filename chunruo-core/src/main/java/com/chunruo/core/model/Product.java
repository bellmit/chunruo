package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.chunruo.core.vo.BarrageVo;
import com.chunruo.core.vo.CommodityPriceVo;
import com.chunruo.core.util.StringUtil;

/**
 * 商品
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_product")
public class Product implements Cloneable{
	public static final Double TAXRATE = 0.091d;
	public static final Double CROSS_PRODUCT_LIMIT = 0.5d;
	public static final Double DEFAULT_B_TO_G = 454.6;
	private Long productId;					//序号ID
    private Long userId;					//用户ID
    private String name;					//商品名称
    private Integer stockNumber;			//商品库数量
    private Double v2Price;                 //v2价
    private Double v3Price;                 //v3价
    private Double priceRecommend;			//售卖价格
    private Double priceCost;				//成本价格(供货商成本价格)
    private String categoryFids;			//商品分类父类ID 
    private String categoryIds;				//商品分类ID
    private Double weigth;					//产品重量(单位:克)
    private String productCode;				//商品编码
    private String productSku;				//规格编码
    private Long wareHouseId;				//所属仓库ID
    private Long templateId;           		//邮费模板ID
    private String image;					//商品主图
    private Integer salesNumber;			//商品销量
    private Integer maxLimitNumber;         //单次最大购买量(预警)
    private Boolean status;					//状态(0:取消分销，1-设置分销)
    private Boolean isSoldout;				//是否售完(0:未售完;1:已售完)
    private Date lastRepTime;               //上一次补货时间
    private Date soldoutTime;				//售完时间
    private String productDesc;				//商品描述
    private String productCopywriter;		//商品文案
    private String purchaseNotes;			//购买须知
    private String productIntros;			//商品说明id多个以“,”隔开
    private Boolean isTest;					//是否测试商品
    private Boolean isPackage;				//是否是邀请好友的购买礼包
    private Boolean isShowPrice;            //店长是否显示成本价
    private Boolean isFreeTax;              //是否免税
    private Boolean isFresh;                //是否生鲜商品
    private Boolean isOpenV2Price;          //是否显示v2价
    private Boolean isOpenV3Price;          //是否开启v3价
    private Boolean isShowLevelPrice;       //是否显示等级价
    private Boolean isShowV2Price;          //是否显示v2价
    private Boolean isShowV3Price;          //是否显示v3价
    private Boolean isFreePostage;          //商品是否参与包邮
    private Boolean isSpceProduct;			//是否规格商品
    private Boolean isMoreSpecProduct;		//是否多规格商品
    private Boolean isGroupProduct;			//是否组合商品
    private String doubtIds;                //商品答疑ID多个以"，"隔开
    private Long brandId;					//品牌id
    private String productEffectIntro;      //商品作用简介
    private Boolean isDelete;               //是否删除了
    private Boolean isGuideProduct;         //是否引导商品
    private String usageMethod;             //使用方法
    private String tagIds;                  //自定义标签id
    private Boolean isShow;                 //是否显示再app内
    private String aggrProductIds;          //聚合商品id
	private String adminUserName;           // 操作员
    private Date createTime;				//创建时间
    private Date updateTime;				//更新时间
    
    // 秒杀
    private Long seckillId;					//秒杀场次ID
    private Integer seckillSort;			//秒杀场次内容排序
    private Double seckillPrice;			//秒杀价格
    private Double seckillProfit;			//秒杀利润
    private Double seckillMinSellPrice;     //秒杀最低售价
    private Integer seckillTotalStock;		//秒杀库存数量
    private Integer seckillSalesNumber;		//秒杀商品销量
    private Integer seckillLimitNumber;		//秒杀商品限购数量
    
    @Transient
    private Double price;                   //商品显示价格
    private String priceDiscount;			//优惠信息
    private String categoryPathName;		//分类总路径
    private String categoryFidName;			//商品分类父类名称
    private String categoryIdName;			//商品分类名称
    private String wareHouseName;			//仓库名称
    private String userName;				//用户名称
    private Integer productProfit;			//商品利润，售卖价格 - 实际价格
    private Integer recommendProfit;        //建议利润
    private Double proxyPrice;				//店铺代理价格
    private Integer isProxy;			    //是否收藏
    private Integer isCommonUserLevel;    	//h5是否普通用户 
    private Integer isBuyPackage;           //h5是否显示开通经销商
    private String nickName;                //用户昵称
    private String tagName;					//标签名
    private Double tax;                     //税费
    private String paymentTaxRegion;        //税费区间
    private Double realSellPrice;           //用户等级真正售价
    private String commodityPriceTag;       //专享价标签
    private Integer productLevelType;        //商品等级类型
    private Double sortWeight;              //排序权重
    private Double minPaymentPrice;         //最低价格
    private Double maxPaymentPrice;         //最高价格
    private Long wareHouseTemplateId;       //仓库模板id
    private String brandName;				//品牌名称
	private Integer productType;            //商品类型
	private Boolean isTeamPackage;			//是否团队代理商品
	private String  postageTemplateName;    //模板名称
	private String productTags;				//商品规格信息
	private Long inviteProductId;			//邀请商品Id
	private Long productSpecId;             //最低价格的规格id
	private Integer warehouseType;          //仓库类型
	private String postage;                 //邮费
	
	//任务商品
	private Boolean isTaskProduct;              //是否任务商品
	private String taskProductTag;              //任务商品标签
	private String rewardNotes;                 //报价弹框提示语
	private ProductTask currentProductTask;     //当前商品所属任务
	
    // 相关联商品对象
    private Double paymentPrice;				//商品实际支付价格
    private Double paymentOriginalPrice;		//商品非秒杀价格
    private String paymentOriginalPriceRegion;  //商品非秒杀价格区间
    private String paymentPriceRegion;		    //规格商品价格区间
    private String seckillPriceRegion;          //秒杀价格区间
    private String sellPriceRegion;             //售价区间
    private String profitRegion;                //利润区间
    private Integer paymentBuyNumber;			//购买数量
    private Integer paymentCartNumbers;			//购物车商品数量
    private Double paymentWeigth;				//购买商品单价重量
    private Long paymentTemplateId;				//邮费模板
	private Integer paymentStockNumber;			//统一库存数量
	private Integer paymentSeckillTotalStock;	//秒杀库存数量
	private Boolean isPaymentSoldout;			//统一状态是否售完(0:未售完;1:已售完)
	private Boolean isPaymentSeckillLimit;		//是否秒杀限制购买数量
	private Integer paymentSeckillBuyNum;		//秒杀限制已购买数量
	private Integer originSeckillStockNumber;   //秒杀场次商品真实库存
	
	// 组合商品信息
	private String paymentGroupPriceRegion;		//组合商品价格区间
	private String groupPriceRecommend;			//组合商品推荐价格区间
	private String groupPriceWholesale;			//组合商品市场价格区间
	private String groupTaxRecommend;           //组合商品推荐价税费区间
	private String groupTaxWholesale;            //组合商品市场价税费区间
	private String groupProductInfo;			//组合商品信息
	private String groupUniqueBatch;			//组合商品拆单唯一批次号
	private Boolean isMainGroupItem;			//是否主退款组合商品
	private Long groupProductId;
	private Integer saleTimes;
	private ProductGroup productGroup;
	private Integer groupSingleTotalNumber;
	private Long groupId;
	
	// 相关联描述对象
	private Long seckillStartTime;			//秒杀开始时间
    private Long seckillEndTime;			//秒杀结束时间
    private Integer seckillLockNumber;		//秒杀锁定库存数量
    private Boolean isSeckillProduct;		//是否秒杀商品(验证秒杀场次是否有效)
    private Integer seckillStatus;			//秒杀状态
    private Boolean isSeckillLimit;			//是否秒杀限购
    private Boolean isSeckillStarted;       //是否秒杀已开始
    private Integer seckillExistBuyNum;		//秒杀已购买总数量
	private Integer seckillWaitBuyNum;		//秒杀待支付总数量
    private Boolean isSeckillUpdateLevel;	//是否秒杀用户等级升级
    private Boolean isSeckillReadStatus;	//是否秒杀即将开始状态
    private String seckillReadDateTips;		//秒杀即将开抢提现
    private String seckillName;
    private Boolean isOpenPriceRecommend = false;    //是否开启推荐价
    private Boolean isHaveProductMaterial = false; //是否配置了商品素材
    private Boolean isBanPurchase = false;         //禁止48小时内购买
	private Date banPurchaseEndTime;               //禁止购买结束时间
	private Boolean isLevelLimitProduct = false;   //是否等级限购商品
	private Integer levelLimitNumber;              //等级限购数量
	private Long lockOrderId;                      //orderPay时锁库不检查
	private Boolean isRechargeGiftProduct;         //是否充值赠送商品
	private String rechargeDesc;                   //充值信息
	private String rechargeNotes;                  //充值备注
	private Boolean isAggrProduct;                 //是否聚合商品
	private String estimatedTime;                  //预计送达
    private String postageNotice;                  //邮寄说明
    private String payIntro;                       //支付说明
    private String couponIntro;                    //优惠券说明
	private Integer soldoutNoticeType;             //售罄通知类型
	private String soldoutNotice;                  //售罄通知说明
	private Integer seckillNoticeType;             //是否已设置秒杀提醒
	
    private ProductSpec currentProductSpec;	//当前选择的规格商品
    private List<Product> aggrProductList = new ArrayList<Product>();  //聚合商品
    private List<Long> categoryFidList = new ArrayList<Long>();   //一级分类id集合
    private List<Long> categoryIdList = new ArrayList<Long>();    //二级分类id集合
    private List<ProductCollectionProfit> collectionProfitList = new ArrayList<ProductCollectionProfit>();
	private List<String> infoList = new ArrayList<String>();
	private List<BarrageVo> barrageVoList = new ArrayList<BarrageVo>();
	private List<ProductSpec> productSpecList = new ArrayList<ProductSpec> ();
    private List<ProductIntro> productIntroList = new ArrayList<ProductIntro> ();
    private List<CommodityPriceVo> commodityPriceVoList = new ArrayList<CommodityPriceVo> ();
    private List<CommodityPriceVo> newCommodityPriceVoList = new ArrayList<CommodityPriceVo> ();
    private List<CommodityPriceVo> levelPromoteList = new ArrayList<CommodityPriceVo> ();
    private List<PurchaseDoubt> purchaseDoubtList = new ArrayList<PurchaseDoubt> ();
    private Map<Long, List<ProductGroup>> productGroupListMap = new HashMap<Long, List<ProductGroup>> ();
    private List<Product> productGroupList = new ArrayList<Product>();
    private List<Integer> tagPriceList = new ArrayList<Integer>();
    private Map<String,String> tagMap = new HashMap<String,String>();
    private String wechat;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	@Column(name="seckill_id")
    public Long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(Long seckillId) {
		this.seckillId = seckillId;
	}

	@Column(name="seckill_sort")
	public Integer getSeckillSort() {
		return seckillSort;
	}

	public void setSeckillSort(Integer seckillSort) {
		this.seckillSort = seckillSort;
	}
	
	@Column(name="seckill_price")
	public Double getSeckillPrice() {
		return seckillPrice;
	}

	public void setSeckillPrice(Double seckillPrice) {
		this.seckillPrice = seckillPrice;
	}

	@Column(name="seckill_profit")
	public Double getSeckillProfit() {
		return seckillProfit;
	}

	public void setSeckillProfit(Double seckillProfit) {
		this.seckillProfit = seckillProfit;
	}
	
	@Column(name="seckill_min_sell_price")
	public Double getSeckillMinSellPrice() {
		return seckillMinSellPrice;
	}

	public void setSeckillMinSellPrice(Double seckillMinSellPrice) {
		this.seckillMinSellPrice = seckillMinSellPrice;
	}

	@Column(name="seckill_total_stock")
	public Integer getSeckillTotalStock() {
		return seckillTotalStock;
	}

	public void setSeckillTotalStock(Integer seckillTotalStock) {
		this.seckillTotalStock = seckillTotalStock;
	}

	@Column(name="seckill_sales_number")
	public Integer getSeckillSalesNumber() {
		return seckillSalesNumber;
	}

	public void setSeckillSalesNumber(Integer seckillSalesNumber) {
		this.seckillSalesNumber = seckillSalesNumber;
	}
	
	@Column(name="seckill_limit_number")
	public Integer getSeckillLimitNumber() {
		return seckillLimitNumber;
	}

	public void setSeckillLimitNumber(Integer seckillLimitNumber) {
		this.seckillLimitNumber = seckillLimitNumber;
	}

	@Column(name="user_id")
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

    @Column(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    @Column(name="stock_number")
    public Integer getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}

	@Column(name="max_limit_number")
	public Integer getMaxLimitNumber() {
		return maxLimitNumber;
	}

	public void setMaxLimitNumber(Integer maxLimitNumber) {
		this.maxLimitNumber = maxLimitNumber;
	}

	@Column(name="v2_price")
    public Double getV2Price() {
		return v2Price;
	}

	public void setV2Price(Double v2Price) {
		this.v2Price = v2Price;
	}

	 @Column(name="v3_price")
	public Double getV3Price() {
		return v3Price;
	}

	public void setV3Price(Double v3Price) {
		this.v3Price = v3Price;
	}

    @Column(name="price_recommend")
    public Double getPriceRecommend() {
		return priceRecommend;
	}


	public void setPriceRecommend(Double priceRecommend) {
		this.priceRecommend = priceRecommend;
	}

    @Column(name="price_cost")
    public Double getPriceCost() {
        return priceCost;
    }

	public void setPriceCost(Double priceCost) {
        this.priceCost = priceCost;
    }

	@Column(name="tag_ids")
	public String getTagIds() {
		return tagIds;
	}

	public void setTagIds(String tagIds) {
		this.tagIds = tagIds;
	}

    @Column(name="category_fids")
    public String getCategoryFids() {
		return categoryFids;
	}

	public void setCategoryFids(String categoryFids) {
		this.categoryFids = categoryFids;
	}

	@Column(name="category_ids")
	public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	@Column(name="last_rep_time")
	public Date getLastRepTime() {
		return lastRepTime;
	}

	public void setLastRepTime(Date lastRepTime) {
		this.lastRepTime = lastRepTime;
	}

	@Column(name="weigth")
    public Double getWeigth() {
		return weigth;
	}

	public void setWeigth(Double weigth) {
		this.weigth = weigth;
	}
    
    @Column(name="product_code")
    public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode == null ? null : productCode.trim();
	}

    @Column(name="product_sku")
    public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}
	
	@Column(name="ware_house_id"  ,nullable=false)
	public Long getWareHouseId() {
		return wareHouseId;
	}

	public void setWareHouseId(Long wareHouseId) {
		this.wareHouseId = wareHouseId;
	}
	
    @Column(name="image")
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image == null ? null : image.trim();
    }

    @Column(name="sales_number")
    public Integer getSalesNumber() {
		return salesNumber;
	}

	public void setSalesNumber(Integer salesNumber) {
		this.salesNumber = salesNumber;
	}

	 @Column(name="aggr_product_ids")
    public String getAggrProductIds() {
		return aggrProductIds;
	}

	public void setAggrProductIds(String aggrProductIds) {
		this.aggrProductIds = aggrProductIds;
	}

	@Column(name="status")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Column(name="is_soldout")
    public Boolean getIsSoldout() {
		return isSoldout;
	}

	public void setIsSoldout(Boolean isSoldout) {
		this.isSoldout = isSoldout;
	}
	
	@Column(name="soldout_time")
	public Date getSoldoutTime() {
		return soldoutTime;
	}

	public void setSoldoutTime(Date soldoutTime) {
		this.soldoutTime = soldoutTime;
	}

	@Column(name="is_show")
	public Boolean getIsShow() {
		return isShow;
	}

	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}

	@Column(name="is_free_tax")
	public Boolean getIsFreeTax() {
		return isFreeTax;
	}

	public void setIsFreeTax(Boolean isFreeTax) {
		this.isFreeTax = isFreeTax;
	}

	@Column(name="is_spce_product")
    public Boolean getIsSpceProduct() {
		return isSpceProduct;
	}

	public void setIsSpceProduct(Boolean isSpceProduct) {
		this.isSpceProduct = isSpceProduct;
	}
	
	@Column(name="is_more_spec_product")
	public Boolean getIsMoreSpecProduct() {
		return isMoreSpecProduct;
	}

	public void setIsMoreSpecProduct(Boolean isMoreSpecProduct) {
		this.isMoreSpecProduct = isMoreSpecProduct;
	}
	
	@Column(name="is_group_product")
	public Boolean getIsGroupProduct() {
		return isGroupProduct;
	}

	public void setIsGroupProduct(Boolean isGroupProduct) {
		this.isGroupProduct = isGroupProduct;
	}
    @Lob
    @Column(name="product_desc")
	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name="product_intros")
	public String getProductIntros() {
		return productIntros;
	}

	public void setProductIntros(String productIntros) {
		this.productIntros = productIntros;
	}
	
	@Column(name="purchase_notes")
	public String getPurchaseNotes() {
		return purchaseNotes;
	}

	public void setPurchaseNotes(String purchaseNotes) {
		this.purchaseNotes = purchaseNotes;
	}
	
	@Column(name="product_copywriter")
	public String getProductCopywriter() {
		return productCopywriter;
	}

	public void setProductCopywriter(String productCopywriter) {
		this.productCopywriter = productCopywriter;
	}

	@Column(name="is_test")
	public Boolean getIsTest() {
		return isTest;
	}

	public void setIsTest(Boolean isTest) {
		this.isTest = isTest;
	}

	@Column(name="is_package")
	public Boolean getIsPackage() {
		return isPackage;
	}

	public void setIsPackage(Boolean isPackage) {
		this.isPackage = isPackage;
	}
	
	@Column(name="is_show_price")
	public Boolean getIsShowPrice() {
		return isShowPrice;
	}

	public void setIsShowPrice(Boolean isShowPrice) {
		this.isShowPrice = isShowPrice;
	}
		
	@Column(name="is_open_v2_price")
	public Boolean getIsOpenV2Price() {
		return isOpenV2Price;
	}

	public void setIsOpenV2Price(Boolean isOpenV2Price) {
		this.isOpenV2Price = isOpenV2Price;
	}

	@Column(name="is_open_v3_price")
	public Boolean getIsOpenV3Price() {
		return isOpenV3Price;
	}

	public void setIsOpenV3Price(Boolean isOpenV3Price) {
		this.isOpenV3Price = isOpenV3Price;
	}

	@Column(name="is_show_level_price")
	public Boolean getIsShowLevelPrice() {
		return isShowLevelPrice;
	}

	public void setIsShowLevelPrice(Boolean isShowLevelPrice) {
		this.isShowLevelPrice = isShowLevelPrice;
	}

	@Column(name="is_show_v2_price")
	public Boolean getIsShowV2Price() {
		return isShowV2Price;
	}

	public void setIsShowV2Price(Boolean isShowV2Price) {
		this.isShowV2Price = isShowV2Price;
	}

	@Column(name="is_show_v3_price")
	public Boolean getIsShowV3Price() {
		return isShowV3Price;
	}

	public void setIsShowV3Price(Boolean isShowV3Price) {
		this.isShowV3Price = isShowV3Price;
	}

	@Column(name="is_fresh")
	public Boolean getIsFresh() {
		return isFresh;
	}

	public void setIsFresh(Boolean isFresh) {
		this.isFresh = isFresh;
	}

	@Column(name="is_free_postage")
	 public Boolean getIsFreePostage() {
		return isFreePostage;
	}

	public void setIsFreePostage(Boolean isFreePostage) {
		this.isFreePostage = isFreePostage;
	}

	@Column(name="is_guide_product")
	public Boolean getIsGuideProduct() {
		return isGuideProduct;
	}

	public void setIsGuideProduct(Boolean isGuideProduct) {
		this.isGuideProduct = isGuideProduct;
	}

	@Column(name="template_id")
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	
	@Column(name="usage_method")
	public String getUsageMethod() {
		return usageMethod;
	}

	public void setUsageMethod(String usageMethod) {
		this.usageMethod = usageMethod;
	}

	@Column(name="doubt_ids")
	public String getDoubtIds() {
		return doubtIds;
	}

	public void setDoubtIds(String doubtIds) {
		this.doubtIds = doubtIds;
	}
	
	@Column(name="brand_id")
	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	@Column(name="product_effect_intro")
	public String getProductEffectIntro() {
		return productEffectIntro;
	}

	public void setProductEffectIntro(String productEffectIntro) {
		this.productEffectIntro = productEffectIntro;
	}

	@Column(name="is_delete")
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Transient
	public Long getPaymentTemplateId() {
		return paymentTemplateId;
	}

	public void setPaymentTemplateId(Long paymentTemplateId) {
		this.paymentTemplateId = paymentTemplateId;
	}

	@Transient
	public Integer getProductProfit() {
		return productProfit;
	}

	public void setProductProfit(Integer productProfit) {
		this.productProfit = productProfit;
	}

	@Transient
	public Integer getRecommendProfit() {
		return recommendProfit;
	}

	public void setRecommendProfit(Integer recommendProfit) {
		this.recommendProfit = recommendProfit;
	}

	@Transient
	public Double getMinPaymentPrice() {
		return minPaymentPrice;
	}

	public void setMinPaymentPrice(Double minPaymentPrice) {
		this.minPaymentPrice = minPaymentPrice;
	}

	@Transient
	public Double getMaxPaymentPrice() {
		return maxPaymentPrice;
	}

	public void setMaxPaymentPrice(Double maxPaymentPrice) {
		this.maxPaymentPrice = maxPaymentPrice;
	}

	@Transient
	public List<ProductIntro> getProductIntroList() {
		return productIntroList;
	}

	public void setProductIntroList(List<ProductIntro> productIntroList) {
		this.productIntroList = productIntroList;
	}
	
	@Transient
	public List<PurchaseDoubt> getPurchaseDoubtList() {
		return purchaseDoubtList;
	}

	public void setPurchaseDoubtList(List<PurchaseDoubt> purchaseDoubtList) {
		this.purchaseDoubtList = purchaseDoubtList;
	}
	
	@Transient
	public Double getProxyPrice() {
		return proxyPrice;
	}

	public void setProxyPrice(Double proxyPrice) {
		this.proxyPrice = proxyPrice;
	}
	
	@Transient
	public Integer getIsProxy() {
		return isProxy;
	}

	public void setIsProxy(Integer isProxy) {
		this.isProxy = isProxy;
	}

	@Transient
	public Integer getIsCommonUserLevel() {
		return isCommonUserLevel;
	}

	public void setIsCommonUserLevel(Integer isCommonUserLevel) {
		this.isCommonUserLevel = isCommonUserLevel;
	}
	
	@Transient
	public Integer getIsBuyPackage() {
		return isBuyPackage;
	}

	public void setIsBuyPackage(Integer isBuyPackage) {
		this.isBuyPackage = isBuyPackage;
	}
	
	@Transient
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Transient
	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	@Transient
	public String getPaymentTaxRegion() {
		return paymentTaxRegion;
	}

	public void setPaymentTaxRegion(String paymentTaxRegion) {
		this.paymentTaxRegion = paymentTaxRegion;
	}

	@Transient
	public Double getRealSellPrice() {
		return realSellPrice;
	}

	public void setRealSellPrice(Double realSellPrice) {
		this.realSellPrice = realSellPrice;
	}
	
	@Transient
	public String getCommodityPriceTag() {
		return commodityPriceTag;
	}

	public void setCommodityPriceTag(String commodityPriceTag) {
		this.commodityPriceTag = commodityPriceTag;
	}

	@Transient
	public Integer getProductLevelType() {
		return productLevelType;
	}

	public void setProductLevelType(Integer productLevelType) {
		this.productLevelType = productLevelType;
	}

	@Transient
	public List<String> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<String> infoList) {
		this.infoList = infoList;
	}

	@Transient
	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	@Transient
	public Integer getPaymentStockNumber() {
		return paymentStockNumber;
	}

	public void setPaymentStockNumber(Integer paymentStockNumber) {
		this.paymentStockNumber = paymentStockNumber;
	}
	
	@Transient
	public Integer getPaymentSeckillTotalStock() {
		return paymentSeckillTotalStock;
	}

	public void setPaymentSeckillTotalStock(Integer paymentSeckillTotalStock) {
		this.paymentSeckillTotalStock = paymentSeckillTotalStock;
	}

	@Transient
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Transient
	public String getWareHouseName() {
		return wareHouseName;
	}

	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}

	@Transient
	public String getCategoryFidName() {
		return categoryFidName;
	}

	public void setCategoryFidName(String categoryFidName) {
		this.categoryFidName = categoryFidName;
	}

	@Transient
	public String getCategoryIdName() {
		return categoryIdName;
	}

	public void setCategoryIdName(String categoryIdName) {
		this.categoryIdName = categoryIdName;
	}

	@Transient
	public String getCategoryPathName() {
		return categoryPathName;
	}

	public void setCategoryPathName(String categoryPathName) {
		this.categoryPathName = categoryPathName;
	}

	@Transient
	public Boolean getIsTeamPackage() {
		return isTeamPackage;
	}

	public void setIsTeamPackage(Boolean isTeamPackage) {
		this.isTeamPackage = isTeamPackage;
	}

	@Transient
	public String getSellPriceRegion() {
		return sellPriceRegion;
	}

	public void setSellPriceRegion(String sellPriceRegion) {
		this.sellPriceRegion = sellPriceRegion;
	}

	@Transient
	public String getProfitRegion() {
		return profitRegion;
	}

	public void setProfitRegion(String profitRegion) {
		this.profitRegion = profitRegion;
	}

	@Transient
	public String getPostageTemplateName() {
		return postageTemplateName;
	}

	public void setPostageTemplateName(String postageTemplateName) {
		this.postageTemplateName = postageTemplateName;
	}

	@Transient
	public List<ProductSpec> getProductSpecList() {
		return productSpecList;
	}

	public void setProductSpecList(List<ProductSpec> productSpecList) {
		this.productSpecList = productSpecList;
	}

	@Transient
	public String getPriceDiscount() {
		return priceDiscount;
	}

	public void setPriceDiscount(String priceDiscount) {
		this.priceDiscount = priceDiscount;
	}
	
	@Transient
	public String getPaymentPriceRegion() {
		return paymentPriceRegion;
	}

	public void setPaymentPriceRegion(String paymentPriceRegion) {
		this.paymentPriceRegion = paymentPriceRegion;
	}

	@Transient
	public String getSeckillPriceRegion() {
		return seckillPriceRegion;
	}

	public void setSeckillPriceRegion(String seckillPriceRegion) {
		this.seckillPriceRegion = seckillPriceRegion;
	}

	@Transient
	public Double getPaymentPrice() {
		return paymentPrice;
	}

	public void setPaymentPrice(Double paymentPrice) {
		this.paymentPrice = paymentPrice;
	}
	
	@Transient
	public Double getPaymentOriginalPrice() {
		return paymentOriginalPrice;
	}

	public void setPaymentOriginalPrice(Double paymentOriginalPrice) {
		this.paymentOriginalPrice = paymentOriginalPrice;
	}

	@Transient
	public String getPaymentOriginalPriceRegion() {
		return paymentOriginalPriceRegion;
	}

	public void setPaymentOriginalPriceRegion(String paymentOriginalPriceRegion) {
		this.paymentOriginalPriceRegion = paymentOriginalPriceRegion;
	}

	@Transient
	public Integer getOriginSeckillStockNumber() {
		return originSeckillStockNumber;
	}

	public void setOriginSeckillStockNumber(Integer originSeckillStockNumber) {
		this.originSeckillStockNumber = originSeckillStockNumber;
	}

	@Transient
	public ProductSpec getCurrentProductSpec() {
		return currentProductSpec;
	}

	public void setCurrentProductSpec(ProductSpec currentProductSpec) {
		this.currentProductSpec = currentProductSpec;
	}
	
	@Transient
	public Integer getSeckillLockNumber() {
		return seckillLockNumber;
	}

	public void setSeckillLockNumber(Integer seckillLockNumber) {
		this.seckillLockNumber = seckillLockNumber;
	}

	@Transient
	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	@Transient
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
	
	@Transient
	public Long getSeckillStartTime() {
		return seckillStartTime;
	}

	public void setSeckillStartTime(Long seckillStartTime) {
		this.seckillStartTime = seckillStartTime;
	}
	
	@Transient
	public Long getSeckillEndTime() {
		return seckillEndTime;
	}

	public void setSeckillEndTime(Long seckillEndTime) {
		this.seckillEndTime = seckillEndTime;
	}

	@Transient
	public Boolean getIsSeckillProduct() {
		return isSeckillProduct;
	}

	public void setIsSeckillProduct(Boolean isSeckillProduct) {
		this.isSeckillProduct = isSeckillProduct;
	}

	@Transient
	public Integer getPaymentBuyNumber() {
		return paymentBuyNumber;
	}

	public void setPaymentBuyNumber(Integer paymentBuyNumber) {
		this.paymentBuyNumber = paymentBuyNumber;
	}

	@Transient
	public Integer getPaymentCartNumbers() {
		return paymentCartNumbers;
	}

	public void setPaymentCartNumbers(Integer paymentCartNumbers) {
		this.paymentCartNumbers = paymentCartNumbers;
	}

	@Transient
	public Boolean getIsPaymentSoldout() {
		return isPaymentSoldout;
	}

	public void setIsPaymentSoldout(Boolean isPaymentSoldout) {
		this.isPaymentSoldout = isPaymentSoldout;
	}
	
	@Transient
	public String getProductTags() {
		return productTags;
	}

	public void setProductTags(String productTags) {
		this.productTags = productTags;
	}
	
	@Transient
	public Integer getWarehouseType() {
		return warehouseType;
	}

	public void setWarehouseType(Integer warehouseType) {
		this.warehouseType = warehouseType;
	}

	@Transient
	public Double getPaymentWeigth() {
		return paymentWeigth;
	}

	public void setPaymentWeigth(Double paymentWeigth) {
		this.paymentWeigth = paymentWeigth;
	}
	
	@Transient
	public Long getWareHouseTemplateId() {
		return wareHouseTemplateId;
	}

	public void setWareHouseTemplateId(Long wareHouseTemplateId) {
		this.wareHouseTemplateId = wareHouseTemplateId;
	}
	
	@Transient
	public Integer getSeckillStatus() {
		return seckillStatus;
	}

	public void setSeckillStatus(Integer seckillStatus) {
		this.seckillStatus = seckillStatus;
	}
	
	@Transient
	public Boolean getIsSeckillUpdateLevel() {
		return isSeckillUpdateLevel;
	}

	public void setIsSeckillUpdateLevel(Boolean isSeckillUpdateLevel) {
		this.isSeckillUpdateLevel = isSeckillUpdateLevel;
	}
	
	@Transient
	public Boolean getIsSeckillReadStatus() {
		return isSeckillReadStatus;
	}

	public void setIsSeckillReadStatus(Boolean isSeckillReadStatus) {
		this.isSeckillReadStatus = isSeckillReadStatus;
	}
	
	@Transient
	public String getSeckillName() {
		return seckillName;
	}

	public void setSeckillName(String seckillName) {
		this.seckillName = seckillName;
	}
	
	@Transient
	public String getSeckillReadDateTips() {
		return seckillReadDateTips;
	}

	public void setSeckillReadDateTips(String seckillReadDateTips) {
		this.seckillReadDateTips = seckillReadDateTips;
	}
	
	@Transient
	public Long getInviteProductId() {
		return inviteProductId;
	}

	public void setInviteProductId(Long inviteProductId) {
		this.inviteProductId = inviteProductId;
	}
	
	@Transient
	public Long getProductSpecId() {
		return productSpecId;
	}

	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}

	@Transient
	public String getTagName() {
		return tagName;
	}
	
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	@Transient
	public Map<String, String> getTagMap() {
		return tagMap;
	}

	public void setTagMap(Map<String, String> tagMap) {
		this.tagMap = tagMap;
	}
	
	@Transient
	public Map<Long, List<ProductGroup>> getProductGroupListMap() {
		return productGroupListMap;
	}

	public void setProductGroupListMap(Map<Long, List<ProductGroup>> productGroupListMap) {
		this.productGroupListMap = productGroupListMap;
	}
	
	@Transient
	public List<BarrageVo> getBarrageVoList() {
		return barrageVoList;
	}

	public void setBarrageVoList(List<BarrageVo> barrageVoList) {
		this.barrageVoList = barrageVoList;
	}

	@Transient
	public List<CommodityPriceVo> getCommodityPriceVoList() {
		return commodityPriceVoList;
	}

	public void setCommodityPriceVoList(List<CommodityPriceVo> commodityPriceVoList) {
		this.commodityPriceVoList = commodityPriceVoList;
	}

	@Transient
	public List<CommodityPriceVo> getNewCommodityPriceVoList() {
		return newCommodityPriceVoList;
	}

	public void setNewCommodityPriceVoList(List<CommodityPriceVo> newCommodityPriceVoList) {
		this.newCommodityPriceVoList = newCommodityPriceVoList;
	}

	@Transient
	public List<CommodityPriceVo> getLevelPromoteList() {
		return levelPromoteList;
	}

	public void setLevelPromoteList(List<CommodityPriceVo> levelPromoteList) {
		this.levelPromoteList = levelPromoteList;
	}

	@Transient
	public String getPaymentGroupPriceRegion() {
		return paymentGroupPriceRegion;
	}

	public void setPaymentGroupPriceRegion(String paymentGroupPriceRegion) {
		this.paymentGroupPriceRegion = paymentGroupPriceRegion;
	}
	
	@Transient
	public String getGroupPriceRecommend() {
		return groupPriceRecommend;
	}

	public void setGroupPriceRecommend(String groupPriceRecommend) {
		this.groupPriceRecommend = groupPriceRecommend;
	}

	@Transient
	public String getGroupPriceWholesale() {
		return groupPriceWholesale;
	}

	public void setGroupPriceWholesale(String groupPriceWholesale) {
		this.groupPriceWholesale = groupPriceWholesale;
	}

	@Transient
	public String getGroupTaxRecommend() {
		return groupTaxRecommend;
	}

	public void setGroupTaxRecommend(String groupTaxRecommend) {
		this.groupTaxRecommend = groupTaxRecommend;
	}

	@Transient
	public String getGroupTaxWholesale() {
		return groupTaxWholesale;
	}

	public void setGroupTaxWholesale(String groupTaxWholesale) {
		this.groupTaxWholesale = groupTaxWholesale;
	}

	@Transient
	public Long getGroupProductId() {
		return groupProductId;
	}

	public void setGroupProductId(Long groupProductId) {
		this.groupProductId = groupProductId;
	}
	
	@Transient
	public String getGroupProductInfo() {
		return groupProductInfo;
	}

	public void setGroupProductInfo(String groupProductInfo) {
		this.groupProductInfo = groupProductInfo;
	}

	@Transient
	public List<Product> getProductGroupList() {
		return productGroupList;
	}

	public void setProductGroupList(List<Product> productGroupList) {
		this.productGroupList = productGroupList;
	}
	
	@Transient
	public Integer getSaleTimes() {
		return saleTimes;
	}

	public void setSaleTimes(Integer saleTimes) {
		this.saleTimes = saleTimes;
	}

	@Transient
	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	@Transient
	public Integer getGroupSingleTotalNumber() {
		return groupSingleTotalNumber;
	}

	public void setGroupSingleTotalNumber(Integer groupSingleTotalNumber) {
		this.groupSingleTotalNumber = groupSingleTotalNumber;
	}

	@Transient
	public String getGroupUniqueBatch() {
		return groupUniqueBatch;
	}

	public void setGroupUniqueBatch(String groupUniqueBatch) {
		this.groupUniqueBatch = groupUniqueBatch;
	}
	
	@Transient
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	@Transient
	public Boolean getIsBanPurchase() {
		return isBanPurchase;
	}

	public void setIsBanPurchase(Boolean isBanPurchase) {
		this.isBanPurchase = isBanPurchase;
	}

	@Transient
	public Date getBanPurchaseEndTime() {
		return banPurchaseEndTime;
	}

	public void setBanPurchaseEndTime(Date banPurchaseEndTime) {
		this.banPurchaseEndTime = banPurchaseEndTime;
	}

	@Transient
	public Boolean getIsLevelLimitProduct() {
		return isLevelLimitProduct;
	}

	public void setIsLevelLimitProduct(Boolean isLevelLimitProduct) {
		this.isLevelLimitProduct = isLevelLimitProduct;
	}

	@Transient
	public String getCouponIntro() {
		return couponIntro;
	}

	public void setCouponIntro(String couponIntro) {
		this.couponIntro = couponIntro;
	}

	@Transient
	public Integer getSoldoutNoticeType() {
		return soldoutNoticeType;
	}

	public void setSoldoutNoticeType(Integer soldoutNoticeType) {
		this.soldoutNoticeType = soldoutNoticeType;
	}
	
	@Transient
	public String getSoldoutNotice() {
		return soldoutNotice;
	}

	public void setSoldoutNotice(String soldoutNotice) {
		this.soldoutNotice = soldoutNotice;
	}

	@Transient
	public Integer getSeckillNoticeType() {
		return seckillNoticeType;
	}

	public void setSeckillNoticeType(Integer seckillNoticeType) {
		this.seckillNoticeType = seckillNoticeType;
	}

	@Transient
	public Integer getLevelLimitNumber() {
		return levelLimitNumber;
	}

	public void setLevelLimitNumber(Integer levelLimitNumber) {
		this.levelLimitNumber = levelLimitNumber;
	}

	@Transient
	public Boolean getIsMainGroupItem() {
		return isMainGroupItem;
	}

	public void setIsMainGroupItem(Boolean isMainGroupItem) {
		this.isMainGroupItem = isMainGroupItem;
	}
	
	@Transient
	public Boolean getIsHaveProductMaterial() {
		return isHaveProductMaterial;
	}

	public void setIsHaveProductMaterial(Boolean isHaveProductMaterial) {
		this.isHaveProductMaterial = isHaveProductMaterial;
	}

	@Transient
	public Boolean getIsSeckillLimit() {
		return isSeckillLimit;
	}

	public void setIsSeckillLimit(Boolean isSeckillLimit) {
		this.isSeckillLimit = isSeckillLimit;
	}
	
	@Transient
	public Boolean getIsSeckillStarted() {
		return isSeckillStarted;
	}

	public void setIsSeckillStarted(Boolean isSeckillStarted) {
		this.isSeckillStarted = isSeckillStarted;
	}

	@Transient
	public Integer getSeckillExistBuyNum() {
		return seckillExistBuyNum;
	}

	public void setSeckillExistBuyNum(Integer seckillExistBuyNum) {
		this.seckillExistBuyNum = seckillExistBuyNum;
	}

	@Transient
	public Integer getSeckillWaitBuyNum() {
		return seckillWaitBuyNum;
	}

	public void setSeckillWaitBuyNum(Integer seckillWaitBuyNum) {
		this.seckillWaitBuyNum = seckillWaitBuyNum;
	}

	@Transient
	public Boolean getIsPaymentSeckillLimit() {
		return isPaymentSeckillLimit;
	}

	public void setIsPaymentSeckillLimit(Boolean isPaymentSeckillLimit) {
		this.isPaymentSeckillLimit = isPaymentSeckillLimit;
	}

	@Transient
	public Integer getPaymentSeckillBuyNum() {
		return paymentSeckillBuyNum;
	}

	public void setPaymentSeckillBuyNum(Integer paymentSeckillBuyNum) {
		this.paymentSeckillBuyNum = paymentSeckillBuyNum;
	}

	@Transient
	public Boolean getIsRechargeGiftProduct() {
		return isRechargeGiftProduct;
	}

	public void setIsRechargeGiftProduct(Boolean isRechargeGiftProduct) {
		this.isRechargeGiftProduct = isRechargeGiftProduct;
	}

	@Transient
	public String getRechargeDesc() {
		return rechargeDesc;
	}

	public void setRechargeDesc(String rechargeDesc) {
		this.rechargeDesc = rechargeDesc;
	}

	@Transient
	public String getRechargeNotes() {
		return rechargeNotes;
	}

	public void setRechargeNotes(String rechargeNotes) {
		this.rechargeNotes = rechargeNotes;
	}

	@Transient
	public List<Integer> getTagPriceList() {
		return tagPriceList;
	}

	public void setTagPriceList(List<Integer> tagPriceList) {
		this.tagPriceList = tagPriceList;
	}

	@Transient
	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}


	@Transient
	public Double getSortWeight() {
		return sortWeight;
	}

	public void setSortWeight(Double sortWeight) {
		this.sortWeight = sortWeight;
	}
	
	@Transient
	public Boolean getIsOpenPriceRecommend() {
		return isOpenPriceRecommend;
	}

	public void setIsOpenPriceRecommend(Boolean isOpenPriceRecommend) {
		this.isOpenPriceRecommend = isOpenPriceRecommend;
	}

	@Transient
	public Boolean getIsTaskProduct() {
		return isTaskProduct;
	}

	public void setIsTaskProduct(Boolean isTaskProduct) {
		this.isTaskProduct = isTaskProduct;
	}

	@Transient
	public Long getLockOrderId() {
		return lockOrderId;
	}

	public void setLockOrderId(Long lockOrderId) {
		this.lockOrderId = lockOrderId;
	}

	@Transient
	public String getTaskProductTag() {
		return taskProductTag;
	}

	public void setTaskProductTag(String taskProductTag) {
		this.taskProductTag = taskProductTag;
	}

	@Transient
	public String getRewardNotes() {
		return rewardNotes;
	}

	public void setRewardNotes(String rewardNotes) {
		this.rewardNotes = rewardNotes;
	}

	@Transient
	public Boolean getIsAggrProduct() {
		return isAggrProduct;
	}

	public void setIsAggrProduct(Boolean isAggrProduct) {
		this.isAggrProduct = isAggrProduct;
	}

	@Transient
	public String getPostage() {
		return postage;
	}

	public void setPostage(String postage) {
		this.postage = postage;
	}

	@Transient
	public List<Product> getAggrProductList() {
		return aggrProductList;
	}

	public void setAggrProductList(List<Product> aggrProductList) {
		this.aggrProductList = aggrProductList;
	}

	@Transient
	public ProductTask getCurrentProductTask() {
		return currentProductTask;
	}

	public void setCurrentProductTask(ProductTask currentProductTask) {
		this.currentProductTask = currentProductTask;
	}

	@Transient
	public String getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(String estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	@Transient
	public String getPayIntro() {
		return payIntro;
	}

	public void setPayIntro(String payIntro) {
		this.payIntro = payIntro;
	}

	@Transient
	public String getPostageNotice() {
		return postageNotice;
	}

	public void setPostageNotice(String postageNotice) {
		this.postageNotice = postageNotice;
	}

	@Transient
	public List<ProductCollectionProfit> getCollectionProfitList() {
		return collectionProfitList;
	}

	public void setCollectionProfitList(List<ProductCollectionProfit> collectionProfitList) {
		this.collectionProfitList = collectionProfitList;
	}

	@Transient
	public List<Long> getCategoryFidList() {
		return categoryFidList;
	}

	public void setCategoryFidList(List<Long> categoryFidList) {
		this.categoryFidList = categoryFidList;
	}

	@Transient
	public List<Long> getCategoryIdList() {
		return categoryIdList;
	}

	public void setCategoryIdList(List<Long> categoryIdList) {
		this.categoryIdList = categoryIdList;
	}

	@Transient
	public String getAdminUserName() {
		return adminUserName;
	}

	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
	}

	@Override
	public Product clone(){
		//浅拷贝
		try {
			// 直接调用父类的clone()方法
			Product product = (Product) super.clone();
			if(StringUtil.nullToBoolean(product.getIsGroupProduct())){
				if(this.getProductGroupListMap() != null && this.getProductGroupListMap().size() > 0){
					Map<Long, List<ProductGroup>> productGroupListMap = new HashMap<Long, List<ProductGroup>> ();
					for(Entry<Long, List<ProductGroup>> entry : this.getProductGroupListMap().entrySet()){
						try{
							if(entry.getValue() != null && entry.getValue().size() > 0){
								List<ProductGroup> productGroupList = new ArrayList<ProductGroup> ();
								for(ProductGroup productGroup : entry.getValue()){
									productGroupList.add(productGroup.clone());
								}
								productGroupListMap.put(entry.getKey(), productGroupList);
							}
						}catch(Exception e){
							continue;
						}
					}
					product.setProductGroupListMap(productGroupListMap);
				}
			}else if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
				// 规格商品
				if(this.getProductSpecList() != null && this.getProductSpecList().size() > 0){
					List<ProductSpec> productSpecList = new ArrayList<ProductSpec> ();
					for(ProductSpec productSpec : this.getProductSpecList()){
						try{
							productSpecList.add(productSpec.clone());
						}catch(Exception e){
							continue;
						}
					}
					product.setProductSpecList(productSpecList);
				}
			}
			return product;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
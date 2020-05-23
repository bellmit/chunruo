package com.chunruo.core.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.WeiNiProduct;
import com.chunruo.core.repository.WeiNiProductRepository;
import com.chunruo.core.service.WeiNiProductManager;
import com.chunruo.core.util.DateUtil;

@Component("weiNiProductManager")
public class WeiNiProductManagerImpl extends GenericManagerImpl<WeiNiProduct, Long> implements WeiNiProductManager{
	private WeiNiProductRepository weiNiProductRepository;

	@Autowired
	public WeiNiProductManagerImpl(WeiNiProductRepository weiNiProductRepository) {
		super(weiNiProductRepository);
		this.weiNiProductRepository = weiNiProductRepository;
	}

	@Override
	public boolean batchInsertWeiNiProduct(List<WeiNiProduct> modelList, int commitPerCount) {
		Long begin = new Date().getTime();  // 开时时间  
		boolean result = true;
		Connection conn = null;
		int commitCount = (commitPerCount / 5000); //5000条默认提交一次,需提交次数

		try { 
			StringBuffer sqlPrefixBuffer = new StringBuffer();
			sqlPrefixBuffer.append("insert into jkd_wei_ni_product(");
			sqlPrefixBuffer.append("bar_code,");
			sqlPrefixBuffer.append("brand,"); 
			sqlPrefixBuffer.append("category,"); 
			sqlPrefixBuffer.append("country,"); 
			sqlPrefixBuffer.append("delivery_city,");
			sqlPrefixBuffer.append("delivery_code,"); 
			sqlPrefixBuffer.append("detail_img_urls,"); 
			sqlPrefixBuffer.append("details,"); 
			sqlPrefixBuffer.append("display_img_urls,");
			sqlPrefixBuffer.append("goods_no,"); 
			sqlPrefixBuffer.append("if_invoice,"); 
			sqlPrefixBuffer.append("is_limit_price,"); 
			sqlPrefixBuffer.append("rate,"); 
			sqlPrefixBuffer.append("retail_price,"); 
			sqlPrefixBuffer.append("sale_type,"); 
			sqlPrefixBuffer.append("settle_price,"); 
			sqlPrefixBuffer.append("sku_name,"); 
			sqlPrefixBuffer.append("sku_no,"); 
			sqlPrefixBuffer.append("spec,"); 
			sqlPrefixBuffer.append("three_category,"); 
			sqlPrefixBuffer.append("two_category,"); 
			sqlPrefixBuffer.append("valid_day,"); 
			sqlPrefixBuffer.append("weight");  
			sqlPrefixBuffer.append(") values ");
			
			conn = this.getJdbcTemplate().getDataSource().getConnection();
			StringBuffer suffix = new StringBuffer();  
			conn.setAutoCommit(false);   // 设置事务为非自动提交  
			// 比起st，pst会更好些  
			PreparedStatement pst = conn.prepareStatement("");  
			// 外层循环，总提交事务次数  
			for (int i = 0; i <= commitCount; i++) {  
				// 第N次提交步长  
				List<WeiNiProduct> subUserList = modelList.subList(5000 * i, 5000 * (i + 1) > commitPerCount ? commitPerCount : 5000 * (i + 1));
				for (int j = 0; j < subUserList.size(); j++) { 
					Date currentDate = DateUtil.getCurrentDate();
					WeiNiProduct model = subUserList.get(j);
					// 构建sql
					suffix.append("(");
					suffix.append(String.format("'%s',", model.getBarCode()));
					suffix.append(String.format("'%s',", model.getBrand()));
					suffix.append(String.format("'%s',", model.getCategory()));
					suffix.append(String.format("'%s',", model.getCountry()));
					suffix.append(String.format("'%s',", model.getDeliveryCity()));
					suffix.append(model.getDeliveryCode()+",");
					suffix.append(String.format("'%s',", model.getDetailImgUrls()));
					suffix.append(String.format("'%s',", model.getDetails()));
					suffix.append(String.format("'%s',", model.getDisplayImgUrls()));
					suffix.append(String.format("'%s',", model.getGoodsNo()));
					suffix.append(String.format("'%s',", model.getDetailImgUrls()));
					suffix.append(model.getIfInvoice()+",");
					suffix.append(model.getIsLimitPrice()+",");
					suffix.append(model.getLimitNumber()+",");
					suffix.append(model.getRate()+",");
					suffix.append(String.format("'%s',", model.getRetailPrice()));
					suffix.append(model.getSaleType()+",");
					suffix.append(model.getSettlePrice()+",");
					suffix.append(String.format("'%s',", model.getSkuName()));
					suffix.append(String.format("'%s',", model.getSkuNo()));
					suffix.append(String.format("'%s',", model.getSpec()));
					suffix.append(String.format("'%s',", model.getThreeCategory()));
					suffix.append(String.format("'%s',", model.getTwoCategory()));
					suffix.append(String.format("'%s',", model.getValidDay()));
					suffix.append(model.getWeight());
					suffix.append("),");
				}  

				String sql = sqlPrefixBuffer.toString() + suffix.substring(0, suffix.length() - 1);  
				pst.addBatch(sql);   // 添加执行sql  
				pst.executeBatch();  // 执行操作  
				conn.commit();   // 提交事务  
				suffix = new StringBuffer();   // 清空上一次添加的数据  
			}  
			pst.close();  
		} catch (SQLException e) {  
			result = false;
			try {  
				conn.rollback(); //进行事务回滚  
			} catch (SQLException ex) {   
			}
			e.printStackTrace();  
		} finally{ 
			try {
				conn.close();  //关闭连接
			} catch (SQLException e) {
				e.printStackTrace();
				log.error("!!!!!!!!!!!close onnection fail!!!!!!!!!!!!!!!!");
			}
		}

		Long end = new Date().getTime();  
		// 耗时  
		log.debug(String.format("batchInsertWeiNiProduct==>>[rows: %s, time: %sms]", commitPerCount, end - begin));
		return result;
	}

	
}

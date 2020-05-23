package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.UserSaleRecord;
import com.chunruo.core.repository.UserSaleRecordRepository;
import com.chunruo.core.service.UserSaleRecordManager;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userSaleRecordManager")
public class UserSaleRecordManagerImpl extends GenericManagerImpl<UserSaleRecord, Long> implements UserSaleRecordManager {
	private UserSaleRecordRepository userSaleRecordRepository;
	
	@Autowired
	public UserSaleRecordManagerImpl(UserSaleRecordRepository userSaleRecordRepository) {
		super(userSaleRecordRepository);
		this.userSaleRecordRepository = userSaleRecordRepository;
	}

	@Override
	public List<UserSaleRecord> getUserSaleRecordListByUserId(Long userId) {
		return this.userSaleRecordRepository.getUserSaleRecordListByUserId(userId);
	}
	
	@Override
	public UserSaleRecord getUserSaleRecordByOrderId(Long orderId) {
		return this.userSaleRecordRepository.getUserSaleRecordByOrderId(orderId);
	}

	@Override
	public List<UserSaleRecord> getUserSaleRecordListByUpdateTime(Date updateTime) {
		return this.userSaleRecordRepository.getUserSaleRecordListByUpdateTime(updateTime);
	}

	@Override
	public void saveUserSaleRecordByOrder(Order order) {
	}

	@Override
	public void updateUserSaleRecord(Order order,Boolean isReduce,Boolean isHaveRefund) {
	}

	@Override
	public void updateUserSaleRecordByStatus(List<Long> orderIdList, Integer status) {
		if(orderIdList != null && orderIdList.size() > 0) {
		    this.userSaleRecordRepository.updateUserSaleRecordByStatus(orderIdList, status);
		}
	}

	@Override
	public List<UserSaleRecord> getUserSaleRecordListByOrderIdList(List<Long> orderIdList) {
		return this.userSaleRecordRepository.getUserSaleRecordListByOrderIdList(orderIdList);
	}

	@Override
	public List<Object[]> getUserSaleRecordListByUserIdList(List<Long> userIdList) {
		try {
			StringBuffer sqlBuf = new StringBuffer();
			sqlBuf.append("SELECT jui.user_id,IFNULL(aa.cur_day_sale_amount,0),IFNULL(bb.cur_month_sale_amount,0),IFNULL(cc.last_month_sale_amount,0),IFNULL(dd.last_month_refund_amount,0) FROM jkd_user_info jui ");
			sqlBuf.append("LEFT JOIN ");
			sqlBuf.append("( SELECT jusr.`user_id`,SUM(jusr.`sale_amount`) cur_day_sale_amount FROM jkd_user_sale_record jusr WHERE DATE_FORMAT(jusr.order_pay_time,'%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d') ");
			sqlBuf.append("AND order_status IN(2,3,4) GROUP BY jusr.`user_id` ");
			sqlBuf.append(") aa ON aa.user_id = jui.user_id ");
			sqlBuf.append("LEFT JOIN ");
			sqlBuf.append("( SELECT jusr.`user_id`,SUM(jusr.`sale_amount`) cur_month_sale_amount FROM jkd_user_sale_record jusr WHERE order_sent_time IS NOT NULL AND DATE_FORMAT(jusr.`order_sent_time`,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') ");
			sqlBuf.append("AND order_status IN(3,4,5) GROUP BY jusr.`user_id` ");
			sqlBuf.append(") bb ON bb.user_id = jui.user_id ");
			sqlBuf.append("LEFT JOIN ");
			sqlBuf.append("( SELECT jusr.`user_id`,SUM(jusr.`sale_amount`) last_month_sale_amount FROM jkd_user_sale_record jusr WHERE order_sent_time IS NOT NULL AND DATE_FORMAT(jusr.`order_sent_time`,'%Y-%m') = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH),'%Y-%m') ");
			sqlBuf.append("AND order_status IN(3,4,5) GROUP BY jusr.`user_id` ");
			sqlBuf.append(") cc ON cc.user_id = jui.user_id ");
			sqlBuf.append("LEFT JOIN ");
			sqlBuf.append("( SELECT jusr.`user_id`,SUM(jusr.`sale_amount`),SUM(jusr.`refund_amount`) last_month_refund_amount FROM jkd_user_sale_record jusr WHERE order_sent_time IS NOT NULL AND DATE_FORMAT(jusr.`order_sent_time`,'%Y-%m') = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH),'%Y-%m') ");
			sqlBuf.append("AND order_status = 5  GROUP BY jusr.`user_id` ");
			sqlBuf.append(") dd ON dd.user_id = jui.user_id ");
			sqlBuf.append(" and jui.user_id in("+StringUtil.longListToStr(userIdList)+")");
			
			return this.querySql(sqlBuf.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void updateUserLevelByFunction() {
		
	}

	@Override
	public void updateUserSaleRecord(Order order, Boolean isReduce, OrderItems orderItems) {
	}

	@Override
	public List<Object[]> countMonthSaleAmountByUserId(Long userId) {
		String dateFormat = "%Y-%m";
		StringBuilder strBulSql = new StringBuilder();
		strBulSql.append("select avg(amount),max(amount),user_id from ");
		strBulSql.append("(");
		strBulSql.append("select sum(sale_amount) amount,user_id from jkd_user_sale_record where user_id = %s group by user_id,date_format(order_pay_time,'%s')");
		strBulSql.append(") aa ");
		return this.querySql(String.format(strBulSql.toString(), userId,dateFormat));
	}
}

package com.chunruo.portal.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

/**
 * 
 * 每晚2点：已发货订单-> 已完成订单
 */
@Component
public class AutoOrderCompletedScheduler {
	protected final transient Log log = LogFactory.getLog(getClass());
	private Lock lock = new ReentrantLock();
	@Autowired
	private OrderManager orderManager;

//	@Scheduled(cron = "0 0 2 * * ?")
	@Scheduled(cron = "0 */1 * * * ?")
	protected void autoCompletedOrder() {
		
		int size = 0;
		log.info("AutoOrderCompletedScheduler ........");
		// 加锁
		lock.lock();
		try{
			
			
			StringBuffer sqlBuffer = new StringBuffer ();
			sqlBuffer.append("select order_id from jkd_order where status = 3  and TIMESTAMPDIFF(DAY, sent_time, now()) >= 7");
			String sql = String.format(sqlBuffer.toString());
			log.debug(sql);
			
			
			List<Object[]> objectList = orderManager.querySql(sql);
			if(objectList == null || objectList.size() == 0){
				return;
			}
			
			size = objectList.size();
			List<Long> orderList = new ArrayList<Long>();
			for(int i = 0; i < objectList.size(); i ++){
				orderList.add(StringUtil.nullToLong(objectList.get(i)));
			}
			
			for(final Long orderId : orderList){
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try {
							orderManager.updateOrderCompleteStatus(OrderStatus.OVER_ORDER_STATUS, orderId);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			// 释放锁
			lock.unlock();     
		}
		log.info("AutoOrderCompletedScheduler === " + String.format("[size=%s]", size));
	}
}

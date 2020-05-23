package com.chunruo.portal.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.chunruo.core.util.CoreInitUtil;

/**
 * 刷新系统配置
 * 每5分钟一次
 * @author 
 */
@Component
public class AutoRefreshScheduler {
	protected final transient Log log = LogFactory.getLog(getClass());
	
	@Scheduled(cron="0 0/5 * * * *")
	public void execute(){
		try{
			log.info("AutoRefreshScheduler===========");
        	CoreInitUtil.init();
        	log.info("AutoRefreshScheduler===========end=");
        }catch(Exception e){
        	e.printStackTrace();
        }
       
	}

}

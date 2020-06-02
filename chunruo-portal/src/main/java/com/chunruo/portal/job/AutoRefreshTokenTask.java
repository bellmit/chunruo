package com.chunruo.portal.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.chunruo.core.Constants;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.portal.util.WeiXinPayUtil;

@Component
public class AutoRefreshTokenTask {
	private static Log log = LogFactory.getLog(AutoRefreshTokenTask.class);
	
	@Scheduled(cron="0 0 0/2 * * *")
	public void execute(){
		try{
			Constants.WEIXIN_TOKEN_MAP.clear();
			WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(Constants.PUBLIC_ACCOUNT_WECHAT_CONFIG_ID);
			WeiXinPayUtil.getWeiXinToken(weChatAppConfig.getAppId(), weChatAppConfig.getAppSecret());
		}catch(Exception e){
			e.printStackTrace();
			log.debug(e.getMessage());
		}
	}
}

package com.chunruo.core.util;

import java.util.Map;

import com.chunruo.core.Constants;
import com.sensorsdata.analytics.javasdk.SensorsAnalytics;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

public class SensorsAnalyticUtil {

	/**
	 * 统计用户行为
	 * 
	 * @param map
	 * @param eventName
	 * @param distinctId
	 */
	public static void pushMegToSensorsAnalytics(final Map<String, Object> map, final Map<String, Object> profiles,
			final String eventName, final String distinctId, final String anonymousId) {
		BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
			@Override
			public void run() {
				SensorsAnalytics sa = (SensorsAnalytics) Constants.ctx.getBean("sa");
				try {
					if (!StringUtil.isNull(anonymousId)) {
						// 将用户id与神策id想关联
						sa.trackSignUp(distinctId, anonymousId);
					}
					if (profiles != null && profiles.size() > 0) {
						// 用户属性
						sa.profileSet(distinctId, true, profiles); // 此时传入的是注册ID了
					}
					if (map != null && map.size() > 0) {
						// 事件属性
						sa.track(distinctId, true, eventName, map);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						sa.shutdown();
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}

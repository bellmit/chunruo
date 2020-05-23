package com.chunruo.core.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 基础线程池
 * @author chunruo
 *
 */
public class BaseThreadPool {
	private static final Log log = LogFactory.getLog(BaseThreadPool.class);
	public static int corePoolSize = 4;					    //最小线程池
	public static int maximumPoolSize = corePoolSize * 2;	//最大线程池
	public static int keepAliveTime = 25;					//线程有效时间
	public static long waitTime = 1500;						//线程等待时间
	
	static ThreadPoolExecutor workers;

	public static void init() {
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		RejectedExecutionHandler handler = new RejectedExecutionHandler() {
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				try{
					Thread.sleep(BaseThreadPool.waitTime);
				}catch (InterruptedException e) {
					e.printStackTrace();
					log.debug(e.getMessage());
				}
				executor.execute(r);
			}
		};

		if(workers == null){
			workers = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue, handler);
		}
	}
	
	/**
	 * 外部使用线程池
	 * @return
	 */
	public static ThreadPoolExecutor getThreadPoolExecutor(){
		if (workers == null)
			init();
		return workers;
	}
	
//	public static void main(String args[]){
//		BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
//			@Override
//			public void run() {
//				try{
//					String solrUrl="http://localhost:8090/solr/userInfo";
//					
//				}catch(Exception e){
//					log.debug(e.getMessage());
//				}
//			}
//		});
//		
//	}
}
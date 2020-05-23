package com.chunruo.core.util;

import org.apache.log4j.Logger;

import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.LogUtil;


public class LogUtil {
	public static Logger log = Logger.getLogger(LogUtil.class);
	
	/**
	 * 创建log日志(初始化时以时间开头)
	 * @param path	日志文件全路径
	 * @param fields	日志参数
	 */
	public static void log(Logger logger, String[] fields){
		StringBuffer buffer=new StringBuffer();
		buffer.append(DateUtil.getDateTime("yyyyMMddHHmmss", DateUtil.getCurrentDate()));

		for(String field:fields){
			if(field==null|| field.trim().length()==0)field="";
			buffer.append("|").append(field);
		}

		logger.debug(buffer.toString());
	}
	
	/**
	 * 创建log日志(初始化时不以时间开头)
	 * @param logger		日志文件全路径
	 * @param fields		日志参数
	 */
	public static void logNoDate(Logger logger, String[] fields){
		StringBuffer buffer=null;
		for(String field:fields){
			if(field==null|| field.trim().length()==0)field="";
			if(buffer==null)
				buffer=new StringBuffer(field);
			else
				buffer.append("|").append(field);
		}
		logger.debug(buffer.toString());
	}
	
}

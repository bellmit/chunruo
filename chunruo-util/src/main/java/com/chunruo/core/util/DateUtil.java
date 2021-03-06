package com.chunruo.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtil {
    private static Log log = LogFactory.getLog(DateUtil.class);
    private static Lock lock = new ReentrantLock();
    public static Map<String, ThreadLocal<SimpleDateFormat>> dataFormatMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String TIME_PATTERN_STR = "HHmmss";
    public static final String DATE_TIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_YYYY_MM_PATTERN = "yyyy-MM";
    public static final String DATE_YYYYMMDD_PATTERN = "yyyyMMdd";
    public static final String DATE_YYYY_PATTERN = "yyyy";
    public static final String TIME_HHMM_PATTERN = "HH:mm";
    public static final String TIME_HHMM_PATTERN2 = "HHmm";
    public static final String DATE_TIME_NO_HORI_PATTERN = "yyyyMMdd HH:mm:ss";
    public static final String DATE_TIME_NO_SPACE_PATTERN = "yyyyMMddHHmmss";
    public static final String DATE_TIME_PLAYBILL_PATTERN = "yyyyMMdd HH:mm";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_ENGLISH_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String DATE_TIME_HORI_PATTERN = "yyyyMMddHHmm";
    public static final String DATE_FORMAT_HOUR = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_YEAR = "yyyy-MM-dd";
    public static final String DATE_FORMAT_MONTH_DAY = "MM???dd???";
    public static final String DATE_FORMAT_MONTH_TIME_PATTERN= "MM???dd??? HH:mm";
    public static final String DATE_FORMAT_MONTH_DAY_MINUTE = "MM???dd??? HH:mm";
    public static final String DATE_FORMAT_YEAR_MONTH_DAY = "yyyy???MM???dd???";
    public static final String DATE_FORMAT_DAY_HOUR_MINUTE = "X???Y???Z???";
    public static final String DATE_FORMAT_YEAR_COUPON = "yyyy.MM.dd";
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy/MM/dd";
    
    public static final String yyyyMMddHHmm = DateUtil.DATE_TIME_HORI_PATTERN;
    public static final String timeFormat = DateUtil.DATE_TIME_MS_PATTERN;
    public static final String dateFormat = DateUtil.DATE_TIME_PATTERN;
    public static final String yyyyMMdd = DateUtil.DATE_YYYYMMDD_PATTERN;
    public static final String DATE_YYYY = DateUtil.DATE_YYYY_PATTERN;
    public static final String DATE_HOUR = DateUtil.DATE_FORMAT_HOUR;
    public static final String YYYY_MM_DD = DateUtil.DATE_FORMAT_YEAR;
    public static final String HHmm = DateUtil.TIME_HHMM_PATTERN;
    public static final String HHmm2 = DateUtil.TIME_HHMM_PATTERN2;
    public static final String yyyyMMddHHmmss = DateUtil.DATE_TIME_NO_HORI_PATTERN;
    public static final String yyyyMMddHHmmssFile = DateUtil.DATE_TIME_NO_SPACE_PATTERN;
    public static final String PLAYBILL_TIME_PATTERN = DateUtil.DATE_TIME_PLAYBILL_PATTERN;
    public static final String yyyyMMddHHmmss_formate = DateUtil.DATE_FORMAT;
    public static final String yyyyMM_formate = DateUtil.DATE_YYYY_MM_PATTERN;
    
	/**
     * ????????????ThreadLocal???sdf,??????????????????new??????sdf
     * @param pattern
     * @return
     */
    public static SimpleDateFormat getDateFormat(final String pattern) {
    	ThreadLocal<SimpleDateFormat> tl = dataFormatMap.get(pattern);
    	if (tl == null) {
    		lock.lock();
    		try{
    			// ?????????????????????????????????????????????sdfMap?????????????????????put?????????sdf
    			tl = dataFormatMap.get(pattern);
    			if (tl == null) {
    				// ???????????????,??????ThreadLocal<SimpleDateFormat>??????????????????new SimpleDateFormat
    				tl = new ThreadLocal<SimpleDateFormat>() {
    					@Override
    					protected SimpleDateFormat initialValue() {
    						return new SimpleDateFormat(pattern);
    					}
    				};
    				dataFormatMap.put(pattern, tl);
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			lock.unlock();
    		}
    	}
    	return tl.get();
    }
       
    /**
     * ????????????????????????
     * @param pattern
     * @param dateStr
     * @return
     */
    public static boolean isEffectiveTime(String pattern, String dateStr){
    	try {
    		SimpleDateFormat sdf = DateUtil.getDateFormat(pattern);
    		if (sdf != null && !StringUtil.isNull(dateStr)) {
    			sdf.parse(dateStr);
    			return true;
        	}
		} catch (ParseException e) {
		}
    	return false;
    }
   
    /**
     * ??????????????????????????????
     * @param pattern
     * @param dateStr
     * @return
     */
    public static Date parseDate(SimpleDateFormat sdf, String dateStr){
    	if (sdf == null) {
    		return null;
    	}
    	
    	try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static String formatDate(String pattern, Date adate){
    	SimpleDateFormat sdf = DateUtil.getDateFormat(pattern);
    	try{
    		if(sdf != null){
    			return sdf.format(adate);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    } 
    
    public static String formatDate(String pattern){
    	SimpleDateFormat sdf = DateUtil.getDateFormat(pattern);
    	try{
    		if(sdf != null){
    			return sdf.format(DateUtil.getCurrentDate());
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    } 
    
    public static String formatDate(String pattern, String adate){
    	SimpleDateFormat sdf = DateUtil.getDateFormat(pattern);
    	try{
    		if(sdf != null
    				&& !StringUtil.isNull(adate) 
    				&& StringUtil.isNumber(adate) 
    				&& StringUtil.nullToLong(adate).longValue() > 0L){
    			Date date = new Date(StringUtil.nullToLong(adate));
    			return sdf.format(date);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    } 

    /**
     * ????????????????????????
     * @param pattern
     * @param dateStr
     * @return
     */
    public static Date parseDate(String pattern, String dateStr){
    	try {
    		SimpleDateFormat sdf = DateUtil.getDateFormat(pattern);
    		if (sdf != null) {
    			return sdf.parse(dateStr);
        	}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
    } 
    
    /**
     * ??????????????????yyyy-MM-dd HH:mm:ss?????????HH:mm??????
     */
    public static String strToString(String date){
    	if(date == null || "".equals(date)){
    		return date;
    	}
    	
    	String temp = "";
    	try{
    		Date dateStr = DateUtil.getDateFormat(dateFormat).parse(date);
    		temp = DateUtil.getDateFormat(HHmm).format(dateStr);
    	}catch(Exception ex){
    		log.debug(ex.getStackTrace());
    	}
    	return temp;
    }
    
    public static String dateToString(Date date){
    	try {
    		SimpleDateFormat sdf = DateUtil.getDateFormat(DATE_FORMAT);
    		if (sdf != null && date != null) {
    			return sdf.format(date);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }

    /**
     * Return default datePattern (MM/dd/yyyy)
     * @return a string representing the date pattern on the UI
     */
    public static String getDatePattern() {
    	String defaultDatePattern = DATE_FORMAT_YEAR;
        return defaultDatePattern;
    }

    public static String getDateTimePattern() {
        return DateUtil.getDatePattern() + " HH:mm:ss.S";
    }

    /**
     * This method attempts to convert an Oracle-formatted date
     * in the form dd-MMM-yyyy to mm/dd/yyyy.
     *
     * @param aDate date from database as a string
     * @return formatted string for the ui
     */
    public static String getDate(Date aDate) {
        SimpleDateFormat df;
        String returnValue = "";

        if (aDate != null) {
            df = DateUtil.getDateFormat(getDatePattern());
            returnValue = df.format(aDate);
        }
        return (returnValue);
    }

    /**
     * This method generates a string representation of a date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param strDate a string representation of a date
     * @return a converted Date object
     * @see java.text.SimpleDateFormat
     * @throws ParseException when String doesn't match the expected format
     */
    public static Date convertStringToDate(String aMask, String strDate) throws ParseException {
    	if (StringUtil.isNull(strDate)) {
    		return null;
    	}
        
        Date date;
        SimpleDateFormat df = DateUtil.getDateFormat(aMask);
        if (log.isDebugEnabled()) {
//            log.debug("converting '" + strDate + "' to date with mask '" + aMask + "'");
        }

        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            //log.error("ParseException: " + pe);
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }

        return (date);
    }

    /**
     * This method returns the current date time in the format:
     * MM/dd/yyyy HH:MM a
     *
     * @param theTime the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime) {
        return getDateTime(TIME_PATTERN, theTime);
    }

    /**
     * This method returns the current date in the format: MM/dd/yyyy
     * @return the current date
     * @throws ParseException when String doesn't match the expected format
     */
    public static Calendar getToday() throws ParseException {
        Date today = DateUtil.getCurrentDate();
        SimpleDateFormat df = DateUtil.getDateFormat(getDatePattern());
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));

        return cal;
    }
    
    /**
     * ???????????????????????????
     * @param days ??????
     * @return
     */
	public static String getNearlyDate(int days) {
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.DATE, days);
		SimpleDateFormat sdf = DateUtil.getDateFormat(DATE_FORMAT_YEAR);
		return sdf.format(cal1.getTime());
	}
	
	/**
     * ???????????????????????????
     * @param months ??????
     * @return
     */
	public static String getNearlyMonth(int months) {
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.MONTH, -months);
		cal1.add(Calendar.DATE, -1);
		SimpleDateFormat sdf = DateUtil.getDateFormat(DATE_FORMAT_YEAR);
		return sdf.format(cal1.getTime());
	}

	/**
     * ???????????????????????????
     * @param months ??????
     * @return
     */
	public static String getNearlyYear(int years) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(new Date());   
		cal1.add(Calendar.YEAR, -years); 
		cal1.add(Calendar.MONTH, -1); 
		SimpleDateFormat sdf = DateUtil.getDateFormat(DATE_FORMAT_YEAR);
		return sdf.format(cal1.getTime());
	}
	
	
    /**
     * This method generates a string representation of a date's date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     * 
     * @see java.text.SimpleDateFormat
     */
    public static String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            log.error("aDate is null!");
        } else {
            df = DateUtil.getDateFormat(aMask);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    } 
    
    public static Date getFormatDate(Date date, String pattern) {
    	try {
    		return convertStringToDate(pattern, getDateTime(pattern, date));
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }

    /**
     * This method generates a string representation of a date based
     * on the System Property 'dateFormat'
     * in the format you specify on input
     * 
     * @param aDate A date to convert
     * @return a string representation of the date
     */
    public static String convertDateToString(Date aDate) {
        return getDateTime(getDatePattern(), aDate);
    }

    /**
     * This method converts a String to a date using the datePattern
     * 
     * @param strDate the date to convert (in format MM/dd/yyyy)
     * @return a date object
     * @throws ParseException when String doesn't match the expected format
     */
    public static Date convertStringToDate(String strDate){
        Date aDate = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug("converting date with pattern: " + getDatePattern());
            }

            aDate = convertStringToDate(getDatePattern(), strDate);
        } catch (ParseException pe) {
            log.error("Could not convert '" + strDate + "' to a date, throwing exception");
            pe.printStackTrace();
        }
        return aDate;
    }
    
    public static java.sql.Date convertDateToSqlDate(Date date){
    	return new java.sql.Date(date.getTime());
	}
    
    public static java.sql.Timestamp convertDateToTimestamp(Date date){
    	return new java.sql.Timestamp(date.getTime());
	}
    
    public static String getNowTime(Date date) {
    	if (date==null){
    		return "";
    	}
		return DateUtil.getDateFormat(timeFormat).format(date);
	}
    
    public static String getDateTime(String sdate) {
    	try{
    	java.sql.Timestamp date = stringToTimestamp(sdate);
    		return DateUtil.getDateFormat(dateFormat).format(date);
    	}catch(Exception e){
    		return sdate;
    	}
	}
    
    public static java.sql.Timestamp stringToTimestamp(String timestampStr) {  
    	if (timestampStr == null || timestampStr.length() < 1)  
    	return null;  
    	return java.sql.Timestamp.valueOf(timestampStr);  
    }  
    /**
     *????????????????????????????????????????????????????????????7????????? 
     * @param date
     * @return
     */
    public static String[] getWholeWeekByDate(Date date){
		String[] ss = new String[7];
		Calendar calendar = Calendar.getInstance();
		for (int i = 0, j = 2; i < 6 && j < 8; i++, j++){
		     calendar.setTime(date);
		     calendar.setFirstDayOfWeek(Calendar.MONDAY); 
		     calendar.set(Calendar.DAY_OF_WEEK, j);
		     ss[i] =  getFormatDate(calendar.getTime());
		}
	    calendar.setTime(date);
	    calendar.setFirstDayOfWeek(Calendar.MONDAY); 
	    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6); 
		ss[6]= getFormatDate(calendar.getTime());
		return ss;
	}
    
    /**
     * ???????????? yyyy???????????????
     * @param d
     * @return
     */
    public static String getFormatYear(Long timeMillis) {
    	try{
    		Date date = new Date(timeMillis);
    		return DateUtil.getDateFormat(DATE_YYYY).format(date);
    	}catch(Exception e){
    		log.debug(e.getMessage());
    	}
    	return null;
    }
    
    /**
     * ???????????? yyyyMMdd???????????????
     * @param d
     * @return
     */
    public static String getFormatDate(Long timeMillis) {
    	try{
    		Date date = new Date(timeMillis);
    		return DateUtil.getDateFormat(yyyyMMdd).format(date);
    	}catch(Exception e){
    		log.debug(e.getMessage());
    	}
    	return null;
    }
    
    /**
     * ???????????? yyyyMMdd???????????????
     * @param d
     * @return
     */
    public static String getFormatDate(Date d) {
    	return DateUtil.getDateFormat(yyyyMMdd).format(d);
    }
    
    public static String getHHmm2(Date d) {
    	return DateUtil.getDateFormat(HHmm2).format(d);
    }
    
    public static Date getDateByString(String pattern) throws ParseException {
    	return DateUtil.getDateFormat(yyyyMMdd).parse(pattern);
    }
   
    public static Date getPlayBillTimeByPattern(String date) throws ParseException {
    	return DateUtil.getDateFormat(PLAYBILL_TIME_PATTERN).parse(date);
    }
    
	public static String getNowTime() {
		SimpleDateFormat df = DateUtil.getDateFormat("yyyyMMddHHmmss");
		Date date = DateUtil.getCurrentDate();
		String nowTime = df.format(date);
		return nowTime;
	}
	
	/**
	 * @return ??????????????????yyyyMMddHHmmss
	 */
	public static String getNowTimeNumber() {
		SimpleDateFormat df = DateUtil.getDateFormat("yyyyMMddHHmmss");
		Date date = DateUtil.getCurrentDate();
		String nowTime = df.format(date);
		return nowTime;
	}
	
	/**
	 * @return ??????????????????????????????
	 */
	public static String getNowTime(String pattern) {
		SimpleDateFormat df = DateUtil.getDateFormat(pattern);
		Date date = DateUtil.getCurrentDate();
		String nowTime = df.format(date);
		return nowTime;
	}
	
	/**
	 * ?????????????????????
	 * @param ??????
	 * @return long
	 */
	public static Long getSeconds(Date date) {
		if (date == null) {
			return 0L;
		}
		
		long secLong = date.getTime() / 1000L;
		return secLong;
	}

	/**
	 * ?????????2000???1???1??? 00:00:00??????????????????????????????
	 * 
	 * @param ??????
	 * @param ????????????
	 *            ?????????yyyy-MM-dd HH:mm:ss
	 * @return long
	 */
	public static Long getSeconds(String dateStr, String df) {
		if (dateStr == null || "".equals(dateStr)) {
			return null;
		}
		if (df == null || "".equals(df)) {
			df = DATE_FORMAT;
		}
		SimpleDateFormat formatter = DateUtil.getDateFormat(df);
		Date date = formatter.parse(dateStr, new ParsePosition(0));
		return getSeconds(date);
	}
	
	public static Long getMillSeconds(Date date) {
		try {
			if (date == null) {
				return null;
			}

			long secLong = date.getTime();
			return secLong;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}
	
	public static Long getMillSeconds(Date date, String df) {
		if (date == null) {
			return null;
		}
		long secLong = date.getTime();
		return secLong;
	}
	
	public static Long getMillSeconds(String dateStr, String df) {
		if (dateStr == null || "".equals(dateStr)) {
			return null;
		}
		if (df == null || "".equals(df)) {
			df = DATE_FORMAT;
		}
		SimpleDateFormat formatter = DateUtil.getDateFormat(df);
		Date date = formatter.parse(dateStr, new ParsePosition(0));
		return getMillSeconds(date);
	}
	
	   /**
     * ???????????? yyyyMMdd???????????????
     * @param d
     * @return
     */

    public static Date getDateByStringyyyyMMddHHmmss(String pattern) throws ParseException {
    	return DateUtil.getDateFormat(yyyyMMddHHmmssFile).parse(pattern);
    }
    
    public static String getFormatDateByyyyyMMddHHmmssFile(Date d) {
    	return DateUtil.getDateFormat(yyyyMMddHHmmssFile).format(d);
    }
    public static String formateStrDate(String d) {
    		Date formateDate = null;
			try {
				formateDate = DateUtil.getDateFormat(dateFormat).parse(d);
				String dateStr = getFormatDateByyyyyMMddHHmmssFile(formateDate);
				return dateStr;
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		return null;
    }
    
    public static String formatLongToTimeStr(Long msl){
		String str = "";
	    Integer hour = 0;
	    Integer minute = 0;
	    Integer second = 0;
	    Integer ms = 0;

        second = msl.intValue() / 1000;
        ms = msl.intValue() % 1000;

        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        
        if(hour > 0)
        	str = hour.toString() + "??????";
        if(minute > 0)
        	str += minute.toString()  + "??????";
        if(second > 0)
        	str += second.toString() + "???";
        if(ms > 0)
            	str += ms.toString() + "??????";
        
        return str;
	}
    
    /**
     * unix??????????????????java????????? 
     * ???????????????yyyy-MM-dd HH:mm:ss
     * @param unixTime unix??????
     * @return java?????????
     */
    public static String seconds2Date(String unixTime) {
    	return seconds2Date(unixTime, DATE_FORMAT);
    }
    
    /**
     * unix??????????????????java?????????
     * @param unixTime unix??????
     * @param pattern ??????
     * @return ???????????????java?????????
     */
    public static String seconds2Date(String unixTime, String pattern) {
		Long timestamp = Long.parseLong(unixTime) * 1000;
		String date = DateUtil.getDateFormat(pattern).format(new java.util.Date(timestamp));
		return date;
	}
    
    /**
     * ???????????????java??????
     * @param time
     * @param pattern
     * @return
     */
    public static String millisecond2DateStr(String time) {
    	Long timestamp = Long.parseLong(time);
		String date = null;
		try {
			date = DateUtil.getDateFormat(DATE_TIME_MS_PATTERN).format(new java.util.Date(timestamp));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
    
    /**
     * ???????????????java??????
     * @param time
     * @return
     */
    public static Date millisecond2Date(String time) {
		Long timestamp = Long.parseLong(time);
		Date date = null;
		try {
			String strDate = DateUtil.getDateFormat(DATE_TIME_MS_PATTERN).format(new java.util.Date(timestamp));
			date = convertStringToDate(DATE_TIME_MS_PATTERN, strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
    
    /**
     * ???????????????java??????
     * @param time
     * @param pattern
     * @return
     */
    public static Date millisecond2Date(String time, String pattern) {
    	if (StringUtil.isNull(time)) {
    		return null;
    	}
    	
		Date date = null;
		try {
			Long timestamp = Long.parseLong(time);
			String strDate = DateUtil.getDateFormat(pattern).format(new java.util.Date(timestamp));
			date = convertStringToDate(pattern, strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
    
    /**
     * ??????????????????Unix?????????
     * @return ????????????Unix?????????
     */
    public static int getUnixCurrentTime() {
    	Long currentTime = DateUtil.getCurrentTime() / 1000L;
    	return currentTime.intValue();
    }
    
    /**
     * ?????????????????????????????????
     * @return ?????????????????????
     */
    public static String getCurrentMillisecond() {
    	Long currentTime = DateUtil.getCurrentTime();
    	return currentTime.toString();
    }
    
    public static Date convertSqlToDate(Object obj) {
		Date date = null;
		String strDate = StringUtil.null2Str(obj);
		if (strDate.endsWith(".0")) {// ??????".0"
			strDate = strDate.substring(0, strDate.indexOf("."));
		}
		try {
			date = DateUtil.convertStringToDate(DateUtil.DATE_TIME_PATTERN, strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
    
    /**
     * ???????????????????????????
     * @param d
     * @param day
     * @return
     */
    public static Date getZeroWholeDateByDate(Date d, String pattern){
		String zeroWholeDate = DateUtil.formatDate(pattern, d);
		return DateUtil.parseDate(pattern, zeroWholeDate);  
	}
    
    /**
     * ??????????????????
     * @param d
     * @param day
     * @return
     */
    public static Date getMonthBeforeByDay(Date d, int month){  
		 Calendar now = Calendar.getInstance();  
		 now.setTime(d);  
		 now.set(Calendar.DAY_OF_MONTH, 1);  
		 now.set(Calendar.MONTH, now.get(Calendar.MONTH) - month);  
		 return now.getTime();  
	}
    
    /**
     * ??????????????????
     * @param d
     * @param day
     * @return
     */
    public static Date getMonthAfterByDay(Date d, int month){  
		 Calendar now = Calendar.getInstance();  
		 now.setTime(d);  
		 now.set(Calendar.MONTH, now.get(Calendar.MONTH) + month);  
		 return now.getTime();  
	}
    
    /**
     * ??????????????????
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBeforeByDay(Date d, int day){  
		 Calendar now = Calendar.getInstance();  
		 now.setTime(d);  
		 now.set(Calendar.DATE, now.get(Calendar.DATE) - day);  
		 now.set(Calendar.HOUR_OF_DAY, 0); 
		 now.set(Calendar.MINUTE, 0);  
		 now.set(Calendar.SECOND, 0);
		 return now.getTime();  
	}
    
    /**
     * ??????????????????
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfterByDay(Date d, int day){  
		 Calendar now = Calendar.getInstance();  
		 now.setTime(d);  
		 now.set(Calendar.DATE, now.get(Calendar.DATE) + day);  
		 return now.getTime();  
	}
    
    /**
     * ?????????????????????
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int minute){  
    	Calendar now = Calendar.getInstance();  
    	now.setTime(d);  
    	now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + minute);  
    	return now.getTime();  
    }
    
    /**
     * ??????????????????
     * @param d
     * @param day
     * @return
     */
    public static Date getYearAfter(Date d, int year){  
    	Calendar now = Calendar.getInstance();  
    	now.setTime(d);  
    	now.add(Calendar.YEAR,year);  
    	return now.getTime();  
    }
    
    /**
     * ??????????????????
     * @param d
     * @param day
     * @return
     */
    public static Date getYearAfterDay(Date d, int day){  
    	Calendar now = Calendar.getInstance();  
    	now.setTime(d);  
    	now.add(Calendar.DAY_OF_YEAR,day);  
    	return now.getTime();  
    }
    
    /**
     * ?????????????????????
     * @param d
     * @param day
     * @return
     */
    public static Date compareTime(Date d){
    	Date date = new Date();
    	if (d == null){
    		return date;
    	}
    	if (d.getTime() > date.getTime()){
    		return d;
    	}
    	return date;
    }
    
    public static String getMondayOfDayWeek(int beforeWeek){
    	Calendar cal = Calendar.getInstance();
    	if(beforeWeek > 1){
    		beforeWeek = beforeWeek - 1;
    		cal.add(Calendar.DATE, -beforeWeek * 7);
    	}
    	 
    	cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    	return DateUtil.getDateFormat(YYYY_MM_DD).format(cal.getTime());
    }
    
    public static Date getCurrentDateEnd(int day){
    	Long time = DateUtil.getCurrentTime() + day * 24 * 3600 * 1000;
    	return new Date(time);
	 }
    
    public static Long getCurrentMinEnd(int min){
    	return DateUtil.getCurrentTime() + min * 60 * 1000;
	 }
    
    public static Date getCurrentDate() {
    	return new Date();
    }
    
    public static long getCurrentTime() {
    	return getCurrentDate().getTime();
    }
    
    /** ?????????????????? */
    public static int getDateYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
    
    /** ?????????????????? */
    public static int getDateMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}
    
    /** ?????????????????? */
    public static int getDateDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DATE);
	}
    
    /**
     * ????????????????????????
     * @param Date ??????
     * @return
     */
	public static int getDaysFromCurTime(Date date) {
		int days = (int) ((System.currentTimeMillis() - date.getTime())/1000/60/60/24);
		return days;
	}
	
	/**
	 * ????????????????????????
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getDaysFromCurTime(Date startDate, Date endDate) {
		int days = (int) ((startDate.getTime() - endDate.getTime())/1000/60/60/24);
		return days;
	}
	
	/**
	 * ????????????????????????
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getDaysBetweenHours(Date startDate, Date endDate) {
		int hours = (int) ((endDate.getTime() - startDate.getTime())/1000/60/60);
		return hours;
	}
	
	public static int getDaysBetweenMintues(Date startDate, Date endDate) {
		int hours = (int) ((endDate.getTime() - startDate.getTime())/1000/60);
		return hours;
	}
	
	public static int getPeriodMonth(Date startDate, Date endDate) {
		return getDaysFromCurTime(startDate, endDate) / 30;
	}
	
	public static long getCurrentDateBefore(int day){
    	return DateUtil.getCurrentTime() - day * 24 * 3600 * 1000;
	}
	
	/**  
     * ???????????????????????????????????????  
     * @param smdate ??????????????? 
     * @param bdate  ??????????????? 
     * @return ???????????? 
     * @throws ParseException  
     */    
    public static int daysBetween(Date smdate, Date bdate) {    
    	try {
    		SimpleDateFormat sdf = DateUtil.getDateFormat("yyyy-MM-dd");  
            smdate = sdf.parse(sdf.format(smdate));  
            bdate = sdf.parse(sdf.format(bdate));  
            Calendar cal = Calendar.getInstance();    
            cal.setTime(smdate);    
            long time1 = cal.getTimeInMillis();                 
            cal.setTime(bdate);    
            long time2 = cal.getTimeInMillis();         
            long between_days=(time2-time1)/(1000*3600*24);  
            return Integer.parseInt(String.valueOf(between_days));     
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return 0;
    }    
    
    
    /**  
     * ???????????????????????????????????????
     * @param smdate ??????????????? 
     * @param bdate  ??????????????? 
     * @return ???????????? 
     * @throws ParseException  
     */    
    public static int mintuesBetween(Date smdate, Date bdate) {    
    	try {
    		SimpleDateFormat sdf = DateUtil.getDateFormat(DATE_TIME_PATTERN);  
            smdate = sdf.parse(sdf.format(smdate));  
            bdate = sdf.parse(sdf.format(bdate));  
            Calendar cal = Calendar.getInstance();    
            cal.setTime(smdate);    
            long time1 = cal.getTimeInMillis();                 
            cal.setTime(bdate);    
            long time2 = cal.getTimeInMillis();         
            long between_days=(time2-time1)/(1000*60);  
            return Integer.parseInt(String.valueOf(between_days));  
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
            return 0;   
    } 
      
    /**
     * ????????????????????????????????? 
     * @param smdate
     * @param bdate
     * @return
     * @throws ParseException
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException{  
        SimpleDateFormat sdf = DateUtil.getDateFormat("yyyy-MM-dd");  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(sdf.parse(smdate));    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(sdf.parse(bdate));    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));     
    }  
    
    
    
    /**
     * ????????????????????????
     * @param d
     * @param minute
     * @return
     */
    public static Date getDateHourBefore(Date d, int hour){  
    	try{
    		Long longtime = new Long(hour * 60 * 60 * 1000);
        	Date date = new Date(d.getTime() - longtime);
        	return date; 
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return d;  
	}
    
    /**
     * ????????????????????????
     * @param d
     * @param minute
     * @return
     */
    public static Date getDateMinuteBefore(Date d, int minute){  
    	try{
    		Long longtime = new Long(minute * 60 * 1000);
        	Date date = new Date(d.getTime() - longtime);
        	return date; 
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return d;  
	}
    
    /**
     * ????????????????????????
     * @param d
     * @param minute
     * @return
     */
    public static Date getDateMinuteAfter(Date d, int minute){  
    	try{
	    	Long longtime = new Long(minute * 60 * 1000);
	    	Date date = new Date(d.getTime() + longtime);
	    	return date;  
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return d;
    }
    
    /**
     * ?????????????????????
     * @param d
     * @param minute
     * @return
     */
    public static Date getDateSecondBefore(Date d, int second){  
    	Long longtime = new Long(second * 1000);
    	Date date = new Date(d.getTime() - longtime);
		return date;  
	}
    
    public static Date truncate(Date date, int field){
		return DateUtils.truncate(date, field);
	}
    
    @SuppressWarnings("deprecation")
    public static String formatDayHourMinute(int CountDownDays,Date beginTime){
    	if (CountDownDays == 0){
    		return DATE_FORMAT_DAY_HOUR_MINUTE;
    	}
    	
		long minute = new Date().getMinutes() - beginTime.getMinutes();
    	long now = CountDownDays*24*60 - minute;
    	long	day = now/(24*60);
    	long	hour = (now%(24*60))/60;
    	long	min = now%60;
    	return DATE_FORMAT_DAY_HOUR_MINUTE.replace("X", String.valueOf(day)).replace("Y", String.valueOf(hour)).replace("Z", String.valueOf(min));
    }
    /**
     * ???????????????????????????
     * @return
     */
    public static Date getMonthFirstDay(){
    	Calendar cal = Calendar.getInstance();  
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);  
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));  
        return  cal.getTime(); 
    }
    
    /**
     * ??????????????????????????????
     * @return
     */
    public static Date getMonthDay(int day){
    	try {
    		Calendar cal = Calendar.getInstance();  
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);  
            cal.set(Calendar.DAY_OF_MONTH, day); 
            return  cal.getTime(); 
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	 return null;
    }
    /**
     * ????????????????????????0?????????
     * @return
     */
    public static Date getLastMonthFirstDay(){
    	Calendar calendar=Calendar.getInstance();
  	    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);  
  	    calendar.add(Calendar.MONTH, -1);
  	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}
    /**
     * ??????????????????????????????23???59???59?????????
     * @return
     */
    public static Date getLastMonthEndDay(){
    	Calendar calendar=Calendar.getInstance();
    	calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
    	calendar.add(Calendar.MONTH, -1);
    	calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    	return calendar.getTime();
    }
    
    /** ???????????????0?????????  
     * @return
     */
    public static Date getWeekFirstDay() {  
        Calendar cal = Calendar.getInstance();  
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);  
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
        return cal.getTime();  
    } 
    
    /** ???????????????23???59???59?????????  
     * @return
     */
    public  static Date getWeekEndDay() {  
        Calendar cal = Calendar.getInstance();  
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);  
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);   
        cal.add(Calendar.DAY_OF_WEEK, 6);  
        return cal.getTime();  
    } 
    
    /**
     * ?????????????????????
     * @param timeMillis
     * @return
     */
    public static Date getTimeMillisToDate(Long timeMillis){
    	try{
    		Date d = new Date(timeMillis);
        	return d;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    	
    }
    /**
     * ?????????????????????
     * @param timeMillis
     * @return
     */
    public static boolean timeIsHour(Date date){
    	try{
    		GregorianCalendar gc = new GregorianCalendar();
        	gc.setTime(date);
        	if((gc.get(Calendar.MINUTE)==0) && (gc.get(Calendar.SECOND)==0)) {
        		return true;
        	} 
        	else{
        		return false;
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return false;
    }
    
    /**
     * ?????????????????????
     * @return
     */
    public static String getMinMonthDate() {  
        Calendar calendar = Calendar.getInstance();  
        try {  
            calendar.setTime(new Date(System.currentTimeMillis()));  
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            return DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, calendar.getTime());
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }
    
    /**
     * ?????????????????????????????????
     * @param minDate
     * @param maxDate
     * @return
     */
    public static List<String> getMonthBetween(String minDate, String maxDate) {  
		try {
			ArrayList<String> result = new ArrayList<String>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// ??????????????????

			Calendar min = Calendar.getInstance();
			Calendar max = Calendar.getInstance();

			min.setTime(sdf.parse(minDate));
			min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

			max.setTime(sdf.parse(maxDate));
			max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

			Calendar curr = min;
			while (curr.before(max)) {
				result.add(sdf.format(curr.getTime()));
				curr.add(Calendar.MONTH, 1);
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }  

    /**
	 * ????????????????????????????????????
	 * @param date
	 * @param format
	 * @return
	 */
	public static String strToString(String date,String format) {
		try {
			Date adate = DateUtil.parseDate(DATE_FORMAT_YEAR, date);
			DateFormat df = new SimpleDateFormat(format);
            return df.format(adate);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
//	public static void main(String[] a) throws ParseException {
////    	System.out.println(getNowTime());
////    	System.out.println(getNearlyDate(0));
////    	Long time = 1488692553L * 1000;
////    	System.out.println(formatDate(DateUtil.DATE_FORMAT_YEAR,new Date(time)));
////    	System.out.println(parseDate(DATE_FORMAT_YEAR,new Date(1488692553 * 1000));
////    	System.out.println(DateUtil.getDateMinuteBefore(DateUtil.getCurrentDate(), 60));
////    	Long createTime = DateUtil.getCurrentDate().getTime();
////		Long startTime = DateUtil.getMillSeconds("2018-11-01", DateUtil.DATE_FORMAT_YEAR);
////		if(createTime < startTime) {
////			System.out.println(true);
////		}
////		System.out.println(createTime);
////		System.out.println(startTime);
////		System.out.println(DateUtil.formatDate(DateUtil.DATE_YYYY_MM_PATTERN, DateUtil.getMonthBeforeByDay(DateUtil.getCurrentDate(), 1)));
////   
////        List<String> monthList = DateUtil.getMonthBetween("2019-02-22","2019-02-11");
////        for(String month : monthList) {
////        	System.out.println(month);
////        }
//
////		String c = "2020-11-29";
//		String b = "2020-11-29";
////		System.out.println(c.compareTo(b));
////		DateUtil.parseDate(dateFormat, b);
////		Date date1 = DateUtil.parseDate(DateUtil.DATE_FORMAT, c);
////		Date date2 = DateUtil.parseDate(DateUtil.DATE_FORMAT, b);
////		int days = DateUtil.daysBetween(date1, date2);
////		int mintues = DateUtil.mintuesBetween(date1, date2);
////		System.out.println(days);
////		System.out.println(mintues);
////		
////		if(DateUtil.isEffectiveTime(DateUtil.DATE_FORMAT_YEAR, c)) {  //??????
////			Date parseDate = DateUtil.parseDate(DateUtil.DATE_FORMAT_YEAR, c);  //??????
////			String aaa = DateUtil.formatDate(dateFormat, parseDate);          //??????
////			System.out.println("aaa"+aaa);
////		}
//		System.out.println("?????????????????????"+strToString("2020-10-20",DateUtil.DATE_FORMAT_YEAR_MONTH_DAY));
//		System.out.println(DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR_MONTH_DAY, new Date()));
//    }
	
}

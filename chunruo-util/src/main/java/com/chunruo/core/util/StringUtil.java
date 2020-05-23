package com.chunruo.core.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@SuppressWarnings("unchecked")
public class StringUtil {
	public static ResourceBundle resourceBundle = null;
	public static final String BUNDLE_KEY = "ApplicationResources";
	
	
	public static void main(String[] args) {
       List<Integer> list = new ArrayList<Integer>();
       list.add(2);
       list.add(3);
       list.add(4);
       list.add(5);
       list.add(6);
       list.add(7);
       List<Integer> subList = list.subList(0, 2);
       for(Integer i : subList) {
    	   System.out.println(i);
       }
       
       System.out.println(isHttpUrl("http://192.168.1.10:8080"));
	}
	
	/**
	 * json转换成对象
	 * @param:传入对象，json字符串
	 * @return:Object
	 */
	public static <T> T jsonToObj(String jsonStr, Class<T> clazz){
		try{
			ObjectMapper mapper = new ObjectMapper();
			// 如果json中有新增的字段并且是实体类类中不存在的，不报错
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return  (T) mapper.readValue(jsonStr, clazz.newInstance().getClass());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 对象转换成json
	 * @param:传入对象
	 * @return:json字符串
	 */
	public static String objToJson(Object obj){
		try{
			ObjectMapper mapper = new ObjectMapper();
			// 如果json中有新增的字段并且是实体类类中不存在的，不报错
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.writeValueAsString(obj);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * double比较在两个double之间
	 * @param oneDouble
	 * @param towDouble
	 * @param differDouble
	 * @return
	 */
	public static boolean doubleTowValueBetween(Double oneDouble, Double towDouble, Double differDouble){
		try{
			oneDouble = StringUtil.nullToDouble(oneDouble);
			towDouble = StringUtil.nullToDouble(towDouble);
			differDouble = StringUtil.nullToDouble(differDouble);
			if(oneDouble.compareTo(0.0D) > 0
					&& towDouble.compareTo(0.0D) > 0
					&& differDouble.compareTo(0.0D) > 0){
				if(oneDouble.compareTo(towDouble) == 0){
					// 比较两值相对
					return true;
				}else if(oneDouble.compareTo(towDouble) > 0){
					Double differValue = DoubleUtil.sub(oneDouble, towDouble);
					if(differDouble.compareTo(differValue) >= 0){
						// 比较范围值要小
						return true;
					}
				}else{
					Double differValue = DoubleUtil.sub(towDouble, oneDouble);
					if(differDouble.compareTo(differValue) >= 0){
						// 比较范围值要小
						return true;
					}
				}	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 字符串年月日
	 * 转换成时间长整形
	 * SimpleDateFormat是线程不安全的
	 * @return
	 */
	public synchronized static Long strDateYMD2Long(String expireEndDate){
		// 有效到期时间
		Long longExpireTime = 0L;
		if(!StringUtil.isNull(expireEndDate)
				&& DateUtil.isEffectiveTime(DateUtil.YYYY_MM_DD, expireEndDate)){
			Date lastRenewDate = DateUtil.parseDate(DateUtil.YYYY_MM_DD, expireEndDate);
			longExpireTime = lastRenewDate.getTime();
		}
		return longExpireTime;
	}
	
	/**
	 * 自动唯一值
	 * @return
	 */
	public static String getRandomUUID(){
		String randomUUID = UUID.randomUUID().toString();
		return randomUUID.replace("-", "");
	}
	
	/**
	 * 自动唯一的日志文件路径
	 * @return
	 */
	public static String getUniqueDateFilePath(String fileSuffix){
		String strDate = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.getCurrentDate());
		return String.format("/%s/%s%s", strDate, StringUtil.getRandomUUID(), fileSuffix);
	}

	/**
	 * 对象转换Map
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> objectToMap(Object obj) throws Exception {    
        if(obj == null)  
            return null;      
  
        Map<String, Object> map = new HashMap<String, Object>();   
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());    
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();    
        for (PropertyDescriptor property : propertyDescriptors) {    
            String key = property.getName();    
            if (key.compareToIgnoreCase("class") == 0) {   
                continue;  
            }
            
            Method getter = property.getReadMethod();  
            Object value = getter!=null ? getter.invoke(obj) : null;  
            map.put(key, value);  
        }    
        return map;  
    }   
	
	/**
	 * 比较两个值是否相等
	 * @param objOne
	 * @param objTow
	 * @return
	 */
	public static boolean compareObject(Object objOne, Object objTow){
		return StringUtil.null2Str(objOne).equals(StringUtil.null2Str(objTow));
	}

	/**
	 * 比较两个值是否相等
	 * @param objOne
	 * @param objTow
	 * @return int;符合为1,不符合为0
	 */
	public static int compareObjectToInt(Object objOne, Object objTow){
		return StringUtil.compareObject(objOne, objTow) ? Integer.valueOf(1) : Integer.valueOf(0);
	}
	
	/**
	 * 判断是否为合法的日期时间字符串
	 * 
	 * @param str_input
	 * @return boolean;符合为true,不符合为false
	 */
	public static boolean isDate(String str_input, String rDateFormat) {
		if (!isNull(str_input)) {
			SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
			formatter.setLenient(false);
			try {
				formatter.format(formatter.parse(str_input));
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	public static boolean isNull(String str) {
		if (str == null)
			return true;
		else if("".equals(str.trim()))
			return true;
		else if("null".equals(str.trim()))
			return true;
		else if(str.trim().length() == 0)
			return true;
		else
			return false;
	}
	
	public static boolean isEachNull(List<String> list) {
		for(String each : list){
			if(isNull(each)){
				return true;
			}
		}
		return false;
	}

	public static boolean isNull(Object str) {
		if (str == null)
			return true;
		else if("".equals(str.toString()))
			return true;
		else if("null".equals(str.toString()))
			return true;
		else
			return false;
	}

	public static boolean isNullStr(String str) {
		if (str == null||str.equals(""))
			return true;
		else
			return false;
	}

	// 将NULL转换成空字符串
	public static String null2Str(Object value) {
		return value == null || "null".equals(value.toString()) ? "" : value.toString().trim();
	}

	public static String null2Str(String value) {
		return value == null || "null".equals(value) ? "" : value.trim();
	}

	public static String nullToString(String value) {
		return value == null || "null".equals(value) ? "" : value.trim();
	}

	public static String nullToString(Object value) {
		return value == null ? "" : value.toString().trim();
	}
	
	/**
     * 判断字符串是否为URL
     * @param urls 需要判断的String类型url
     * @return true:是URL；false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
    	if(StringUtil.isNull(urls)) {
    		return false;
    	}
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
            + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
 
        Pattern pat = Pattern.compile(regex.trim());//对比
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }
    
    /**
     * 获取指定url中的某个参数
     * @param url
     * @param name
     * @return
     */
    public static String getParamByUrl(String url, String name) {
    	if(StringUtil.isNull(url) || StringUtil.isNull(name)) {
    		return null;
    	}
        url += "&";
        String pattern = "(\\?|&){1}#{0,1}" + name + "=[a-zA-Z0-9]*(&{1})";

        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(url);
        if (m.find( )) {
            return m.group(0).split("=")[1].replace("&", "");
        } else {
            return null;
        }
    }

	/**
	 * 将姓名中包含点的替换成正确格式的点
	 * @param value
	 * @return
	 */
	public static String dotStrFormat(String value) {
		String valueStr = StringUtil.null2Str(value);
		List<String> strList = new ArrayList<String>();
    	strList.add("·");
    	strList.add("•");
    	strList.add(".");
    	for(String str : strList) {
    		if(valueStr.contains(str)) {
    			valueStr = valueStr.replace(str, "·");
    		}
    	}
		return valueStr;
	}
	
	public static String strJonsFormat(Object value){
		String htmlValue = StringUtil.null2Str(value);
		if (!StringUtil.isNull(htmlValue)) {
			htmlValue = htmlValue.replace("&amp;", "&");
			htmlValue = htmlValue.replace("&quot;", "\"");
			htmlValue = htmlValue.replace("&lt;", "<");
			htmlValue = htmlValue.replace("&gt;", ">");
			Gson gson = new Gson();  
			htmlValue =  gson.toJson(htmlValue);
			if (!StringUtil.isNull(htmlValue) 
					&& htmlValue.startsWith("\"") 
					&& htmlValue.endsWith("\"")
					&& htmlValue.length() > 2) {
				htmlValue = htmlValue.substring(1, htmlValue.length() - 1);
			}
		}
		return htmlValue;
	}
	
	public static Long nullToLong(Object value){
		return value == null || "null".equals(value.toString()) ? 0L: stringToLong(value.toString());
	}

	public static Integer nullToInteger(Object value){
		return value == null || "null".equals(value.toString()) ? 0: stringToInteger(value.toString());
	}
	
	public static Byte nullToByte(Object value){
		return value == null || "null".equals(value.toString()) ? Byte.valueOf("0"): stringToByte(value.toString());
	}
	
	public static List<String> nullArrayToList(String[] strArrays){
		List<String> arrayList = new ArrayList<String> ();
		if(strArrays != null && strArrays.length > 0){
			for(int i = 0; i < strArrays.length; i ++){
				arrayList.add(StringUtil.null2Str(strArrays[i]));
			}
		}
		return arrayList;
	}
	
	public static Double addDouble(Double value1, Double value2) { 
		BigDecimal b1 = new BigDecimal(nullToString(value1)); 
		BigDecimal b2 = new BigDecimal(nullToString(value2)); 
		return StringUtil.nullToDoubleFormat(String.valueOf(b1.add(b2))); 
	} 

	/**
	 * 向上取整，1=向下取整
	 * @param value
	 * @param type
	 * @return
	 */
	public static String getIntegerNumberStr(Object value,int type) {
		Double val = StringUtil.nullToDoubleFormat(value);
		try {
			if(type == 1) {
				val = Math.floor(val);
			}else {
				val = Math.ceil(val);
			}
			return StringUtil.null2Str(StringUtil.nullToDouble(val).intValue());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return StringUtil.nullToDoubleFormatStr(val);
	}
	
	public static Double nullToDouble(Object value){
		if(!isNull(value)){
			if(value instanceof Double){
				return (Double)value;
			}else{
				try {
					Double d = Double.parseDouble(nullToString(value));
					return d;
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		return new Double(0);
	}
	
	/**
	 * 保留两位小数
	 * @param value
	 * @return
	 */
	public static String nullToDoubleFormatDecimal(Object value){
		try {
			DecimalFormat df = new DecimalFormat("0.00");
			return StringUtil.null2Str(df.format(StringUtil.nullToDoubleFormat(value)));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return StringUtil.null2Str(StringUtil.nullToDoubleFormat(value));
	}
	
	public static String nullToDoubleFormatStr(Object value){
		return StringUtil.null2Str(StringUtil.nullToDoubleFormat(value));
	}
	
	public static Double nullToDoubleFormat(Object value){
		try{
			double d = StringUtil.nullToDouble(value);
			//保留两位小数，如果不需要四舍五入，可以使用RoundingMode.DOWN
			//使用double作为构造函数， 不够精确，需使用string
			BigDecimal bigDecimal = BigDecimal.valueOf(d).setScale(2, RoundingMode.UP);
			return bigDecimal.doubleValue();
		}catch(Exception e){
			e.printStackTrace();
		}
		return new Double(0);
	} 

	/**
	 * 超过万元以上的以 2.34万格式显示
	 * @param price
	 * @return
	 */
	public static String formatAmount(String price,String unit) {
        BigDecimal bigDecimal1 = new BigDecimal(price);
        BigDecimal bigDecimal2 = new BigDecimal(unit);
        if(bigDecimal1.compareTo(bigDecimal2) < 0) {
        	return StringUtil.null2Str(price);
        }
        // 转换为万元（除以10000）
        BigDecimal decimal = bigDecimal1.divide(new BigDecimal("10000"));
        String value = decimal.toString();
        //刚好整除
        if(value.indexOf(".") == -1) {
        	return StringUtil.null2Str(value+"w");
        }
        
        // 保留两位小数
        DecimalFormat formater = new DecimalFormat("0.00");
        // 四舍五入
        formater.setRoundingMode(RoundingMode.FLOOR);
        // 格式化完成之后得出结果
        String formatNum = formater.format(decimal)+"w";
        return StringUtil.null2Str(formatNum);
	}
	
	public static Float nullToFloat(Object value){
		if(!isNull(value)){
			if(value instanceof Float){
				return (Float)value;
			}else{
				try {
					Float f = Float.parseFloat(nullToString(value));
					return f;
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		return new Float(0);
	}

	public static Boolean nullToBoolean(Object value){
		if(value == null 
				|| "null".equals(value.toString()))
			return false;
		if("1".equals(value.toString().trim()) 
				|| "true".equalsIgnoreCase(value.toString().trim())
				|| "是".equalsIgnoreCase(value.toString().trim()))
			return true;
		return false;
	}
	
	public static Integer booleanToInt(Boolean value){
		if(StringUtil.nullToBoolean(value)){
			return 1;
		}
		return 0;
	}

	public static Long stringToLong(String value) {
		Long l;
		value = nullToString(value);
		if ("".equals(value)) {
			l = 0L;
		} else {
			try {
				l = Long.valueOf(value);
			} catch (Exception e) {
				l = 0L;
			}
		}
		return l;
	}

	public static Integer stringToInteger(String value) {
		Integer l;
		value = nullToString(value);
		if ("".equals(value)) {
			l = 0;
		} else {
			try {
				l = Integer.valueOf(value);
			} catch (Exception e) {
				l = 0;
			}
		}
		return l;
	}
	
	public static Byte stringToByte(String value) {
		Byte l;
		value = nullToString(value);
		if ("".equals(value)) {
			l = 0;
		} else {
			try {
				l = Byte.valueOf(value);

			} catch (Exception e) {
				l = 0;
			}
		}
		return l;
	}
	
	public static String integerToString(Integer value) {
		if(value == null){
			return "0";
		}
		
		return value.toString();
	}

	@SuppressWarnings("unlikely-arg-type")
	public static List<Long> stringToLongArray(String value) {
		List<Long> ls = new ArrayList<Long> ();
		try{
			value = StringUtil.null2Str(value);
			if(StringUtil.isNull(value)){
				return ls;
			}

			String[] ids = value.split(",");
			for(int i = 0; i < ids.length; i ++ ){
				String number = StringUtil.null2Str(ids[i]);
				if(!ls.contains(number) && isNumber(number)){
					ls.add(Long.parseLong(number));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ls;
	}
	
	public static List<Integer> stringToIntegerArray(String value) {
		List<Integer> ls = new ArrayList<Integer> ();
		try{
			value = StringUtil.null2Str(value);
			if(StringUtil.isNull(value)){
				return ls;
			}

			String[] ids = value.split(",");
			for(int i = 0; i < ids.length; i ++ ){
				String number = StringUtil.null2Str(ids[i]);
				if(isNumber(number)){
					ls.add(Integer.parseInt(number));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ls;
	}
	public static Set<Long> stringToIntegerSet(String value) {
		Set<Long> set = new HashSet<Long>();
		try{
			value = StringUtil.null2Str(value);
			if(StringUtil.isNull(value)){
				return set;
			}
			
			String[] ids = value.split(",");
			for(int i = 0; i < ids.length; i ++ ){
				String number = StringUtil.null2Str(ids[i]);
				if(isNumber(number)){
					set.add(Long.parseLong(ids[i]));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return set;
	}

	public static List<Integer> getIdIntegerList(String recordJson)throws Exception{
		List<Integer> idList = new ArrayList<Integer> ();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Object> list = objectMapper.readValue(recordJson, List.class);
		if(list != null && list.size() > 0){
			for(Object object : list){
				idList.add(StringUtil.nullToInteger(object));
			}
		}
		return idList;
	}

	public static List<Long> getIdLongList(String recordJson)throws Exception{
		List<Long> idList = new ArrayList<Long> ();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			List<Object> list = objectMapper.readValue(recordJson, List.class);
			if(list != null && list.size() > 0){
				for(Object object : list){
					idList.add(StringUtil.nullToLong(object));
				}
			}
		}catch(Exception e) {
			//e.printStackTrace();
		}
		return idList;
	}

	public static List<String> getIdStringList(String recordJson) throws Exception{
		List<String> idList = new ArrayList<String> ();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Object> list = objectMapper.readValue(recordJson, List.class);
		if(list != null && list.size() > 0){
			for(Object object : list){
				idList.add(StringUtil.nullToString(object));
			}
		}
		return idList;
	}

	public static List<Object> jsonDeserialize(String recordJson)throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(recordJson, List.class);
	}

	public static String longArrayToString(List<Long> value){
		StringBuffer sb = new StringBuffer ();
		if(value == null || value.size() == 0)
			return null;

		for(Iterator<Long> it = value.iterator(); it.hasNext(); ){
			sb.append(it.next());
			if(it.hasNext())
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static String stringArrayToString(List<String> existMobileList){
		StringBuffer sb = new StringBuffer ();
		if(existMobileList == null || existMobileList.size() == 0)
			return null;

		for(Iterator<String> it = existMobileList.iterator(); it.hasNext(); ){
			sb.append(it.next());
			if(it.hasNext())
				sb.append(",");
		}
		return sb.toString();
	}

	public static String longArray2String(List<Long> value){
		StringBuffer sb = new StringBuffer ();
		if(value == null || value.size() == 0)
			return null;

		for(Iterator<Long> it = value.iterator(); it.hasNext(); ){
			sb.append("'" + it.next() + "'");
			if(it.hasNext())
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static String intArrayToString(List<Integer> value){
		StringBuffer sb = new StringBuffer ();
		if(value == null || value.size() == 0)
			return null;
		
		for(Iterator<Integer> it = value.iterator(); it.hasNext(); ){
			sb.append(null2Str(it.next()));
			if(it.hasNext())
				sb.append(",");
		}
		return sb.toString();
	}

	public static Long stringToLong(Object value) {
		Long l;
		value = nullToString(value);
		if ("".equals(value)) {
			l = 0L;
		} else {
			try {
				l = Long.valueOf(value.toString());
			} catch (Exception e) {
				l = 0L;
			}
		}
		return l;
	}

	public static List<Long> longSetToList(Set<Long> value){
		List<Long> list = new ArrayList<Long> ();
		if(value == null || value.size() == 0)
			return list;

		for(Iterator<Long> it = value.iterator(); it.hasNext(); ){
			list.add(it.next());
		}
		return list;
	}

	public static String longSetToStr(Set<Long> value){
		StringBuffer sb = new StringBuffer();
		if(value == null || value.size() == 0)
			return "";

		for(Iterator<Long> it = value.iterator(); it.hasNext(); ){
			sb.append(null2Str(it.next()));
			if(it.hasNext())
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static String longListToStr(List<Long> value){
		StringBuffer sb = new StringBuffer();
		if(value == null || value.size() == 0)
			return "";
		
		for(Iterator<Long> it = value.iterator(); it.hasNext(); ){
			sb.append(null2Str(it.next()));
			if(it.hasNext())
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static String longListToString(List<Long> value){
		StringBuffer sb = new StringBuffer();
		if(value == null || value.size() == 0)
			return "";
		
		for(Iterator<Long> it = value.iterator(); it.hasNext(); ){
			sb.append("'");
			sb.append(null2Str(it.next()));
			sb.append("'");
			if(it.hasNext())
				sb.append(",");
		}
		return sb.toString();
	}

	public static List<Integer> integerSetToList(Set<Integer> value){
		List<Integer> list = new ArrayList<Integer> ();
		if(value == null || value.size() == 0)
			return list;

		for(Iterator<Integer> it = value.iterator(); it.hasNext(); ){
			list.add(it.next());
		}
		return list;
	}

	public static String strSetToString(Set<String> value){
		StringBuffer sb = new StringBuffer();
		if(value == null || value.size() == 0)
			return sb.toString();

		for(Iterator<String> it = value.iterator(); it.hasNext(); ){
			sb.append(it.next());
			if(it.hasNext())
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static String strListToString(List<String> value){
		StringBuffer sb = new StringBuffer();
		if(value == null || value.size() == 0)
			return sb.toString();

		for(Iterator<String> it = value.iterator(); it.hasNext(); ){
			sb.append(it.next());
			if(it.hasNext())
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static String strListToString(List<String> value, String splitChar){
		StringBuffer sb = new StringBuffer();
		if(value == null || value.size() == 0)
			return sb.toString();

		for(Iterator<String> it = value.iterator(); it.hasNext(); ){
			sb.append(it.next());
			if(it.hasNext())
				sb.append(splitChar);
		}
		return sb.toString();
	}

	public static List<String> strSetToList(Set<String> value){
		List<String> list = new ArrayList<String> ();
		if(value == null || value.size() == 0)
			return list;

		for(Iterator<String> it = value.iterator(); it.hasNext(); ){
			list.add(it.next());
		}
		return list;
	}

	public static List<String> strToStrList(String value, String splitChar){
		List<String> list = new ArrayList<String> ();
		if(StringUtil.isNullStr(value)) return list;

		String[] strArray = value.split(splitChar);
		for(int i = 0; i < strArray.length; i ++){
			if(!StringUtil.isNullStr(strArray[i]))
				list.add(strArray[i].trim());
		}
		return list;
	}

	/** 
	 * 判断double是否是整数 
	 * @param obj 
	 * @return 
	 */  
	public static boolean isIntegerForDouble(Double obj) {  
		try {
			double eps = 1e-10;  // 精度范围  
			return obj-Math.floor(obj) < eps;  
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	} 
	
	/**
	 * 判断字符串是否是整数
	 */
	public static String getRealNumber(Double value) {
		try {
			if(StringUtil.isIntegerForDouble(value)) {
				return StringUtil.null2Str((int)Math.floor(value));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value.toString();
	}
	
	/**
	 * 判断字符串是否是整数
	 */
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static int parseInteger(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 判断字符串是否是浮点数
	 */
	public static boolean isDouble(String value) {
		try {
			Double d=Double.parseDouble(value);
			String tempD=d.toString();
			if (tempD.contains("."))
				return true;
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumber(String value) {
		if(isNullStr(value)) return false;
		return isInteger(value) || isDouble(value);
	}

	public static boolean isNumber(Object value) {
		if(isNull(value)) return false;
		return isInteger(nullToString(value)) || isDouble(nullToString(value));
	}

	/** 判断是否为时间 * */
	public static boolean isDate(String value) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sdf.parse(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String nullToDateString(String value) {
		try {
			if(!StringUtil.isNullStr(value) && StringUtil.isDate(value)){
				Date date = DateUtil.parseDate(DateUtil.DATE_FORMAT_YEAR, value);
				return DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * 中文转换--文章换行的转换
	 * 
	 * @param str
	 * 
	 * @return
	 */
	public static String getChinseText(String str) {
		if (str == null)
			return ("");
		if (str.equals(""))
			return ("");
		// 建立一个StringBuffer来处理输入数据
		StringBuffer buf = new StringBuffer(str.length() + 6);
		char ch = '\n';
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch == '\r') {
				buf.append(" ");
			} else if (ch == '\n') {
				buf.append(" ");
			} else if (ch == '\t') {
				buf.append("    ");
			} else if (ch == ' ') {
				buf.append(" ");
			} else if (ch == '\'') {
				buf.append("\\'");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	//清除特殊字符
	public static String getescapeText(String str) {
		if (str == null)
			return ("");
		if (str.equals(""))
			return ("");
		// 建立一个StringBuffer来处理输入数据
		StringBuffer buf = new StringBuffer(str.length() + 6);
		char ch = '\n';
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch == '\r') {
				buf.append("");
			} else if (ch == '\n') {
				buf.append("");
			} else if (ch == '\t') {
				buf.append("");
			} else if (ch == ' ') {
				buf.append("");
			} else if (ch == '\'') {
				buf.append("");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * 清除所有特殊字符，只保留中英文字符和数字
	 * @param str
	 * @return
	 */
	public static String getEscapeText(String str){
		try{
			String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";     
			Pattern p = Pattern.compile(regEx);        
			Matcher m = p.matcher(str);  
			return m.replaceAll("");
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 清除所有特殊字符，只保留中英文字符和数字
	 * @param str
	 * @return
	 */
	public static boolean isEscapeText(String str){
		boolean flag=false;
		try{
			String regEx = "[`~!@#$%^&*()+=|{}':;',…\\[\\].<>/?~！@#￥%…&*（）——+|{}【】‘；：”“’。，、？]";     
			Pattern p = Pattern.compile(regEx);        
			Matcher m = p.matcher(str);  
			if(m.find())flag=true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 判断字符串中是否包含除中英文字符和数字外的特殊字符，包含返回true
	 * @param str
	 * @return
	 */
	public static boolean haveEscapeText(String str){
		if(str.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length()==0){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * java判断是否为全汉字
	 * @param str
	 * @return
	 */
	public static boolean isChineseCharacters(String str){
		String pointReg = "[\\u4e00-\\u9fa5]+[·•][\\u4e00-\\u9fa5]+";
		String reg = "[\\u4e00-\\u9fa5]+";
		if (str.contains("·") || str.contains("•")) {
			if (str.matches(pointReg)) {
				return true;
			} else {
				return false;
			}
		} else {
			if (str.matches(reg)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 判断价格
	 * @param str
	 * @return
	 */
	public static boolean isPrice(String str){
		String reg = "[\\d]{1,3}|[\\d]{1,3}\\.[\\d]{1,2}";
		if(!StringUtil.isNull(str) && StringUtil.null2Str(str).matches(reg)){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断重量
	 * @param str
	 * @return
	 * 
	 */
	public static boolean isWeigth(String str){
		String reg = "[\\d]{1,4}|[\\d]{1,4}\\.[\\d]{1}";
		if(!StringUtil.isNull(str) && StringUtil.null2Str(str).matches(reg)){
			return true;
		}
		return false;
	}
	
	/**
	 * java判断是否是只有一个汉字
	 * @param str
	 * @return
	 */
	public static boolean isOneChineseCharacters(String str){
		String reg = "[\\u4e00-\\u9fa5]{1}";
		if(!StringUtil.isNull(str) && StringUtil.null2Str(str).matches(reg)){
			return true;
		}
		return false;
	}
	
	/**
	 * java判断是否是只有一个字母
	 * @param str
	 * @return
	 */
	public static boolean isOneEnglishCharacters(String str){
		String reg = "[a-zA-Z]{1}";
		if(!StringUtil.isNull(str) && StringUtil.null2Str(str).matches(reg)){
			return true;
		}
		return false;
	}
	
	/**
	 * java判断是否为11位手机数字
	 * @param str
	 * @return
	 */
	public static boolean isMobileNumber(String str){
		String reg = "[\\d]{11}";
		if(!StringUtil.isNull(str) && StringUtil.null2Str(str).matches(reg)){
			return true;
		}
		return false;
	}
	
	/**
	 * 根据转义列表对字符串进行转义(escape)。
	 * @param source 待转义的字符串
	 * @param escapeCharMap 转义列表
	 * @return 转义后的字符串
	 */
	public static String escapeCharacter(String source, @SuppressWarnings("rawtypes") HashMap escapeCharMap) {
		if (source == null || source.length() == 0) {
			return source;
		}

		if (escapeCharMap.size() == 0) {
			return source;
		}

		StringBuffer sb = new StringBuffer(source.length() + 100);
		StringCharacterIterator sci = new StringCharacterIterator(source);
		for (char c = sci.first(); c != StringCharacterIterator.DONE; c = sci.next()) {
			String character = String.valueOf(c);
			if (escapeCharMap.containsKey(character)) {
				character = (String) escapeCharMap.get(character);
			}
			sb.append(character);
		}
		return sb.toString();
	}

	/**
	 * 
	 * 中文转换--文章换行的转换
	 * 
	 * @param str
	 * 
	 * @return
	 */

	public static String changeEnter(String str) {
		if (str == null)
			return ("");
		if (str.equals(""))
			return ("");
		// 建立一个StringBuffer来处理输入数据
		StringBuffer buf = new StringBuffer(str.length() + 6);
		char ch = '\n';
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch == '\r') {
				buf.append("|");
			} else if (ch == '\n') {
				buf.append("|");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	// 截掉url左边的一级目录名,如/wap/news/index.xml -> /news/index.xml
	public static String trimLeftNode(String str) {
		if (str == null)
			return "";

		if (str.startsWith("/")) {
			int ind = str.indexOf("/", 1);
			if (ind > 0)
				return str.substring(ind);
		}
		return str;
	}

	public static String generatedUrl(int pageType, List<String> sourceList, String nodestr, int maxint) {
		List<String> nodeList = new ArrayList<String>();
		Random rmd = new Random();
		String rstr = "";
		Set<String> cpSet = new HashSet<String>();
		Set<Integer> distNum = new HashSet<Integer>();
		Set<String> distCp = new HashSet<String>();
		for (int i = 0; i < sourceList.size(); i++) {
			String tmpstr = sourceList.get(i);
			if (getSpstr(tmpstr, 1).equals(nodestr)) {
				nodeList.add(tmpstr);
				cpSet.add(getSpstr(tmpstr, 3));
			}
		}
		if (nodeList.size() > maxint) {
			for (int i = 0; i < maxint;) {
				int tmpint = rmd.nextInt(nodeList.size());
				String tmpstr = nodeList.get(tmpint);
				if ((distCp.add(getSpstr(tmpstr, 3)) || distCp.size() >= cpSet
						.size())
						&& distNum.add(tmpint)) {
					rstr += "<a href='" + getSpstr(tmpstr, 4) + "'>"
							+ getSpstr(tmpstr, 2) + "</a><br/>";
					i++;
				}
			}
		} else {
			for (int i = 0; i < nodeList.size(); i++) {
				String tmpstr = nodeList.get(i);
				rstr += "<a href='" + getSpstr(tmpstr, 4) + "'>"
						+ getSpstr(tmpstr, 2) + "</a><br/>";
			}
		}
		return rstr;
	}

	public static String getSpstr(String spstr, int level) {
		String rstr = "";
		for (int i = 0; i < level; i++) {
			if (spstr.indexOf("|*") == -1) {
				rstr = spstr;
				return rstr;
			} else {
				rstr = spstr.substring(0, spstr.indexOf("|*"));
			}
			spstr = spstr.substring(spstr.indexOf("|*") + 2, spstr.length());
		}
		return rstr;
	}

	public static String toString(Object obj) {
		try {
			return obj.toString();
		} catch (Exception e) {
			return "";
		}
	}

	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
		'9', 'a', 'b', 'c', 'd', 'e', 'f' }; 

	/**
	 * 把byte[]数组转换成十六进制字符串表示形式
	 * @param tmp    要转换的byte[]
	 * @return 十六进制字符串表示形式
	 */
	public static String byteToHexString(byte[] tmp) {
		if(tmp == null){
			throw new NullPointerException();
		}
		int len = tmp.length;
		char str[] = new char[len * 2];
		int i = 0;
		for(byte b:tmp){
			str[i*2] = hexDigits[b >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, 
			str[i*2+1] = hexDigits[b & 0xf]; // 取字节中低 4 位的数字转换
			i++;
		}
		return new String(str);
	}

	/**
	 * 得到一个String值的指定长度的字符串形式
	 * NOTE:	不足的前面添'0'
	 * 			
	 * @param s
	 * @param len
	 * @param cutHead
	 * 		当s的长度大于len时，截取方式：true,截掉头部；否则从截掉尾部
	 * 		例如getStringByAppointLen("12345",3,true) ---> "345"
	 * @return
	 */
	public static String getStringByAppointLen(String s,int len){
		if(s == null || len <=0){
			s = "";
		}
		
		if(len >= s.length()){
			return s;
		}else{
			return s.substring(0,len);
		}
	}

	public static Map<String, String> getSortMap(String sortStr) {
		Map<String, String> map = new HashMap<String, String> ();
		try {
			if (!StringUtil.isNullStr(sortStr)) {
				JSONArray jSONArray = new JSONArray(sortStr);
				for (int i = 0; i < jSONArray.length(); i++) {
					JSONObject jsonObj = jSONArray.getJSONObject(i);
					map.put("sort", StringUtil.null2Str(jsonObj.get("property")));
					map.put("dir", StringUtil.null2Str(jsonObj.get("direction")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * json转换成map对象
	 * @param strJSON
	 * @return
	 */
	public static Map<String, String> jsonToHashMap(String strJSON)  {  
		Map<String, String> dataMap = new HashMap<String, String>();  
		try {
			if (!StringUtil.isNullStr(strJSON)) {
				JSONObject jsonObject = new JSONObject(strJSON);
				Iterator<?> it = jsonObject.keys();
				while (it.hasNext()) {
					String key = StringUtil.null2Str(it.next());
					dataMap.put(key, StringUtil.null2Str(jsonObject.getString(key)));
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataMap;  
	}  
	
	/**
	 * json转换成mapList对象
	 * @param strJSON
	 * @return
	 */
	public static List<Map<String, String>> jsonToHashMapList(String strJSON)  {  
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>> ();
		if (!StringUtil.isNullStr(strJSON)) {
			try { 
				JSONArray jsonArray = new JSONArray(strJSON);
				if(jsonArray != null && jsonArray.length() > 0){
					for(int i = 0; i < jsonArray.length(); i++){
						try{
							Map<String, String> dataMap = new HashMap<String, String>(); 
							JSONObject jsonObject = (JSONObject) jsonArray.get(i);
							Iterator<?> it = jsonObject.keys();
							while (it.hasNext()) {
								String key = StringUtil.null2Str(it.next());
								dataMap.put(key, StringUtil.null2Str(jsonObject.getString(key)));
							}
							
							// map对象大小零加入List对象
							if(dataMap != null && dataMap.size() > 0){
								mapList.add(dataMap);
							}
						}catch(Exception e){
							continue;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mapList;  
	}  
	
	// 判断是否数字类型
	public static boolean isNumeric(String str){ 
		try{
			Pattern pattern = Pattern.compile("[0-9]*"); 
			Matcher isNum = pattern.matcher(str);
			if(!isNum.matches()){
				return false; 
			} 
		}catch(Exception e){
			e.printStackTrace();
		}
		return true; 
	}
	
	public static Map<String, String> getColumnsMap(String columns){
		Map<String, String> columnMap = new LinkedHashMap<String, String>();
		if (!StringUtil.isNullStr(columns)) {
			try {
				JSONArray jSONArray = new JSONArray(columns);
				for (int i = 0; i < jSONArray.length(); i++) {
					JSONObject jsonObj = jSONArray.getJSONObject(i);
					if(!StringUtil.isNull(jsonObj.get("key")) && !StringUtil.isNull(jsonObj.get("value"))){
						String key = StringUtil.null2Str(jsonObj.get("key"));
						String value = StringUtil.null2Str(jsonObj.get("value"));
						columnMap.put(key, value);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return columnMap;
	}
	
	public static Map<String, Object> getFiltersMap(String filters, Class<?> clazz) {
		Map<String, Object> map = new HashMap<String, Object> ();
		try {
			if (!StringUtil.isNullStr(filters)) {
				Field[] fields = clazz.getDeclaredFields();  
				String[] types = {
						"java.lang.Integer",  
				        "java.lang.Double",  
				        "java.lang.Float",  
				        "java.lang.Long",  
				        "java.lang.Short",  
				        "java.lang.Byte",  
				        "java.lang.Boolean",  
				        "java.lang.Character",  
				        "java.lang.String", 
				        "java.util.Date", 
				        "int","double","long","short","byte","boolean","char","float"
				};
				
				List<String> operatorList = new ArrayList<String> ();
				operatorList.add("<>");
				operatorList.add("<=");
				operatorList.add(">=");
				operatorList.add("<");
				operatorList.add(">");
				operatorList.add("=");
				
				if(fields != null && fields.length > 0){
					Map<String, String> fieldTypeMap = new HashMap<String, String> ();
					for(Field f : fields){  
						f.setAccessible(true);  
						try {  
							for(String str : types) {  
								if(f.getType().getName().equals(str)){
									fieldTypeMap.put(f.getName(), f.getType().getName());
								}
							}
						}catch(Exception e){
							continue;
						}
					}
					
					if(fieldTypeMap != null && fieldTypeMap.size() > 0){
						JSONArray jSONArray = new JSONArray(filters);
						for (int i = 0; i < jSONArray.length(); i++) {
							JSONObject jsonObj = jSONArray.getJSONObject(i);
							if(!StringUtil.isNull(jsonObj.get("key")) && !StringUtil.isNull(jsonObj.get("value"))){
								String key = StringUtil.null2Str(jsonObj.get("key"));
								String value = StringUtil.null2Str(jsonObj.get("value"));
								if(fieldTypeMap.containsKey(key)){
									String fieldType = fieldTypeMap.get(key);
									if(StringUtil.compareObject(fieldType, "java.lang.Integer") || StringUtil.compareObject(fieldType, "int")){
										map.put(key, StringUtil.nullToInteger(value));
									}else if(StringUtil.compareObject(fieldType, "java.lang.Double") || StringUtil.compareObject(fieldType, "double")){
										map.put(key, StringUtil.nullToDouble(value));
									}else if(StringUtil.compareObject(fieldType, "java.lang.Float") || StringUtil.compareObject(fieldType, "float")){
										map.put(key, StringUtil.nullToFloat(value));
									}else if(StringUtil.compareObject(fieldType, "java.lang.Long") || StringUtil.compareObject(fieldType, "long")){
										map.put(key, StringUtil.nullToLong(value));
									}else if(StringUtil.compareObject(fieldType, "java.lang.Short") || StringUtil.compareObject(fieldType, "short")){
										//map.put(key, StringUtil.nullToShort(value));
									}else if(StringUtil.compareObject(fieldType, "java.lang.Byte") || StringUtil.compareObject(fieldType, "byte")){
										//map.put(key, StringUtil.nullToByte(value));
									}else if(StringUtil.compareObject(fieldType, "java.lang.Boolean") || StringUtil.compareObject(fieldType, "boolean")){
										map.put(key, StringUtil.nullToBoolean(value));
									}else if(StringUtil.compareObject(fieldType, "java.lang.Character") || StringUtil.compareObject(fieldType, "char")){
										//map.put(key, StringUtil.nullToCharacter(value));
									}else if(StringUtil.compareObject(fieldType, "java.util.Date")){
										String strDate = StringUtil.null2Str(value);
										if(strDate.contains("|")){
											String[] dateArray = strDate.split(","); 
											if(dateArray != null && dateArray.length > 0){
												for(int j = 0; j < dateArray.length; j ++){
													String[] operatorArray = dateArray[j].split("\\|"); 
													if(operatorArray != null 
															&& operatorArray.length == 2
															&& operatorList.contains(StringUtil.null2Str(operatorArray[0]))
															&& StringUtil.isDate(StringUtil.null2Str(operatorArray[1]))){
														StringBuffer mapKey = new StringBuffer ();
														mapKey.append(key);
														mapKey.append(StringUtil.null2Str(operatorArray[0]));
														SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
														map.put(mapKey.toString(), DateUtil.parseDate(sdf, StringUtil.null2Str(operatorArray[1])));
													}
												}
											}
										}else if(StringUtil.isDate(value)){
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
											map.put(key, DateUtil.parseDate(sdf, StringUtil.null2Str(value)));
										}
									}else if(StringUtil.compareObject(fieldType, "java.lang.String")){
										map.put(key, "%" + value + "%");
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public static String strMapToJSON(Map<String, Object> jsonMap){
		ObjectMapper om = new ObjectMapper();  
		Writer w = new StringWriter();  
		String json = null;  
		try {  
			om.writeValue(w, jsonMap);  
			json = w.toString();  
			w.close();  
		} catch (IOException e) {  
			e.printStackTrace();
		}  
		return json;
	}
	
	public static String objectToJSON(Object object){
		ObjectMapper om = new ObjectMapper();  
		Writer w = new StringWriter();  
		String json = null;  
		try {  
			om.writeValue(w, object);  
			json = w.toString();  
			w.close();  
		} catch (IOException e) {  
			e.printStackTrace();
		}  
		return json;
	}

	public static Boolean string2Boolean(String str){
		try{
			if("0".equals(str))
				return Boolean.FALSE;
			else if("1".equals(str))
				return Boolean.TRUE;
			else if("false".equalsIgnoreCase(str))
				return Boolean.FALSE;
			else if("true".equalsIgnoreCase(str))
				return Boolean.TRUE;
		}catch(Exception e){
			return Boolean.FALSE;
		}
		return Boolean.FALSE;
	}

	/**
	 * 比较版本大小
	 * @param s1
	 * @param s2
	 * @return s1>s2,return=1; s1==s2,return=0;s1<s2,return=-1;
	 */
	public static int compare(String s1, String s2) {
		if (s1 == null && s2 == null)
			return 0;
		else if (s1 == null)
			return -1;
		else if (s2 == null)
			return 1;

		String[] arr1 = s1.split("[^a-zA-Z0-9]+");
		String[] arr2 = s2.split("[^a-zA-Z0-9]+");

		int i1, i2, i3;
		for (int j = 0, max = Math.min(arr1.length, arr2.length); j <= max; j++) {
			if (j == arr1.length)
				return j == arr2.length ? 0 : -1;
			else if (j == arr2.length)
				return 1;

			try {
				i1 = Integer.parseInt(arr1[j]);
			} catch (Exception x) {
				i1 = Integer.MAX_VALUE;
			}

			try {
				i2 = Integer.parseInt(arr2[j]);
			} catch (Exception x) {
				i2 = Integer.MAX_VALUE;
			}

			if (i1 != i2) {
				return i1 - i2;
			}

			i3 = arr1[j].compareTo(arr2[j]);

			if (i3 != 0)
				return i3;
		}

		return 0;
	}
	
	public static String base64Decode(String str) {
		if (StringUtil.isNull(str)) {
			return "";
		}

		try {
			return new String(Base64.decodeBase64(str), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String decode(String str) {
		if (StringUtil.isNull(str)) {
			return "";
		}

		try {
			return StringUtil.nullToString(URLDecoder.decode(str, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String encode(String str) {
		if (StringUtil.isNull(str)) {
			return "";
		}

		try {
			return StringUtil.nullToString(URLEncoder.encode(str, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Date strToDate(String str_input, String rDateFormat) {
		Date updateTime = null;
		if(!StringUtil.isNull(str_input)){
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
				formatter.setLenient(false);
				updateTime = formatter.parse(str_input);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return updateTime;
	}

	public static String mapToJson(Map<String, Object> resultMap){
		try{
			Gson gson = new Gson(); 
			return gson.toJson(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String mapStrToJson(Map<String, String> resultMap){
		try{
			Gson gson = new Gson(); 
			return gson.toJson(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isValiJosn(String jsonData){
		boolean isValiJosn = false;
		try{
			if(!isNull(jsonData)){
				JSONObject resultJSON = new JSONObject(jsonData);
				if(resultJSON != null && resultJSON.length() > 0){
					return true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return isValiJosn;
	}

	public static boolean isValidateMobile(String mobile) {
		if(StringUtil.isNull(mobile)) {
			return false;
		}
		Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}
	
	public static String getMobileFromStr(String str) {
		try {
			if(StringUtil.isNull(str)) {
				return null;
			}
			
			Pattern p = Pattern.compile("1[3456789]\\d{9}");
			Matcher m = p.matcher(str);
			while(m.find()) {
				return StringUtil.null2Str(m.group());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean checkSMSCodeValidTime(int validTime, Date createTime){
		try{
			Long tmpTime = System.currentTimeMillis() - validTime * 60 * 1000;
			if(createTime.getTime() >= tmpTime){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static String bankCardFormat(String bankCardNumber){
		String strBankCardNumber = StringUtil.nullToString(bankCardNumber);
		try{
			if(strBankCardNumber != null && strBankCardNumber.length() > 8){
				strBankCardNumber = strBankCardNumber.substring(0, 4) + "****" + strBankCardNumber.substring(strBankCardNumber.length() - 4);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return strBankCardNumber;
	}

	public static String mobileFormat(String mobile){
		String strMobile = StringUtil.nullToString(mobile);
		try{
			if(!StringUtil.isNull(strMobile)){
				if(strMobile.length() == 11){
					strMobile = strMobile.substring(0, 3) + "****" + strMobile.substring(7);
				}else {
					StringBuffer sb = new StringBuffer();
					for(int i = 0; i < strMobile.length(); i ++){
						sb.append("*");
					}
					strMobile = sb.toString();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return strMobile;
	}
	
	public static String identityNoFormat(String identityNo){
		try{
			if(StringUtil.isValidIdentityCardNO(identityNo)){
				return identityNo.substring(0, 6) + "********" + identityNo.substring(14).toUpperCase();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String shortMobileFormat(String mobile){
		String strMobile = StringUtil.nullToString(mobile);
		try{
			if(strMobile != null && strMobile.length() == 11){
				strMobile = strMobile.substring(0, 2) + "**" + strMobile.substring(9);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return strMobile;
	}
	public static String shortBankCardFormat(String bankCard){
		String strbankCard = StringUtil.nullToString(bankCard);
		try{
			if(strbankCard != null && strbankCard.length() >= 11){
				strbankCard =  "**" + strbankCard.substring(strbankCard.length() - 4);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return strbankCard;
	}

	public static String getText(String key, Object[] args) {
		if (StringUtil.resourceBundle == null)
			StringUtil.resourceBundle = ResourceBundle.getBundle(StringUtil.BUNDLE_KEY, Locale.CHINA);
		if (StringUtil.resourceBundle != null) {
			try {
				String result = StringUtil.resourceBundle.getString(key);
				if (result != null)
					return MessageFormat.format(result, args);
				else
					return result;
			} catch (Exception e) {
				return key;
			}
		}
		return "";
	}

	public static String getText(String key) {
		return getText(key, null);
	}

	/**
	 * 按字节数截取字符串，一个中文长度为2
	 * @param str
	 * @param subSLength
	 * @return
	 */
	public static String subStr(String str, int subSLength){
		if(!StringUtil.isNull(str)){
			try{
				byte[] bytes = str.getBytes("Unicode");   
				int n = 0;
				int i = 2;  
				for (; i < bytes.length && n < subSLength; i++){    
					if (i % 2 == 1){   
						n++; 
					}else{ 
						if (bytes[i] != 0){   
							n++;   
						}   
					}   
				} 

				if (i % 2 == 1){   
					i = i + 1;   
				}   
				
				String result = new String(bytes, 0, i, "Unicode"); 
				if( result.length() < str.length()){
					return result + "...";
				}else{
					return result;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return "";
	}
	
	/**
	 * 服务器上取不到urlencode后的中文参数的问题，需要自己从queryString中解析
	 * @param queryString
	 * @param paramKey
	 * @return
	 */
	public static String getQueryStringByKey(String queryString, String paramKey){
		String paramValue = null;
		if (!StringUtil.isNull(queryString)) {
			Map<String, String> paramMap = new HashMap<String, String> ();
			StringTokenizer st = new StringTokenizer(queryString, "&");
		    while (st.hasMoreTokens()) {
		        String pairs = st.nextToken();
		        String key = StringUtil.null2Str(pairs.substring(0, pairs.indexOf('=')));
		        String value = StringUtil.null2Str(pairs.substring(pairs.indexOf('=') + 1));
		        paramMap.put(key, value);
		    }
		    
		    if(paramMap != null 
		    		&& paramMap.size() > 0
		    		&& paramMap.containsKey(StringUtil.null2Str(paramKey))){
		    	paramValue = paramMap.get(paramKey);
		    }
	    }
	    return paramValue;
	}
	
	/**
	 * unicode转成汉字
	 * @param ascii
	 * @return
	 */
	public static String decodeUnicode(Object unicode){ 
		String asciiStr = null2Str(unicode);
		if(!isNull(asciiStr)){
			int i = -1;  
			int pos = 0;  
			try{
				boolean isNative = false;
				StringBuilder sb = new StringBuilder();  
				String tmpString = asciiStr.replace("\\U", "\\u");
				while((i = tmpString.indexOf("\\u", pos)) != -1){  
					isNative = true; 
		            sb.append(tmpString.substring(pos, i));  
		            if(i+5 < tmpString.length()){  
		                pos = i+6;  
		                sb.append((char)Integer.parseInt(tmpString.substring(i+2, i+6), 16));  
		            }  
		        } 
				
				if(isNative){
					 return sb.toString();  
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return asciiStr;
	}
	
	/**
	 * 是否有效的身份证
	 * @param identityCard
	 * @return
	 */
	public static boolean isValidIdentityCardNO(String identityCard){
		identityCard = StringUtil.null2Str(identityCard);
		if(!StringUtil.isNullStr(identityCard) && IdCardValidatorUtil.isValidatedAllIdcard(identityCard)
				&& identityCard.length() == 18){
			return true;
		}
		return false;
	}
	
	/**
	 * 得到一个String值的指定长度的字符串形式
	 * NOTE:	不足的前面添'0'
	 * 			
	 * @param s
	 * @param len
	 * @param cutHead
	 * 		当s的长度大于len时，截取方式：true,截掉头部；否则从截掉尾部
	 * 		例如getStringByAppointLen("12345",3,true) ---> "345"
	 * @return
	 */
	public static String getStringByAppointLen(String s,int len,boolean cutHead){
		if(s == null || len <=0){
			s = "";
		}
		if(len > s.length()){
			int size = len - s.length();
			StringBuffer sb = new StringBuffer();
			while(size -- > 0){
				sb.append("0");
			}
			sb.append(s);
			return sb.toString();
		}else if(len == s.length()){
			return s;
		}else{
			if(cutHead){
				return s.substring(s.length() - len, s.length());
			}else{
				return s.substring(0,len);
			}
		}
	}
	/*卡号截取后部，前面以*隐藏
	 * cordNo 卡号
	 * len 截取长度
	 * 
	 * 
	 * */
	public static String getIndetiryCordNoFomat(String cordNo,int len){
		if (len > 0 && cordNo.length() > len){
			for(int i =0;i<cordNo.length()-len;i++){
				cordNo = cordNo.replaceFirst(nullToString(cordNo.charAt(i)), "*");
			}
		}
		return cordNo;
	}
	
	/*名字超过len长度后显示...
	 * cordNo 卡号
	 * len 截取长度
	 * 
	 * 
	 * */
	public static String getStringFomat(String str,int len){
		if (len > 0 && str.length() > len){
			str = str.substring(0, len) + "...";
		}
		return str;
	}
	
	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	
	/**
     * 过滤昵称特殊表情
     */
    public static String filterName(String name) {
        if(name==null){
            return null;

        }
        if("".equals(name.trim())){
            return "";
        }

        Pattern patter = Pattern.compile("[a-zA-Z0-9\u4e00-\u9fa5]");
        Matcher match = patter.matcher(name);

        StringBuffer buffer = new StringBuffer();

        while (match.find()) {
            buffer.append(match.group());
        }

        return buffer.toString();
    }
    
    //生成随机数字和字母,  
    public static String getStringRandom(int length) {  
        String val = "";  
        Random random = new Random();  
        //参数length，表示生成几位随机数  
        for(int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";  
            //输出字母还是数字  
            if( "char".equalsIgnoreCase(charOrNum) ) {  
                //输出是大写字母还是小写字母  
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;  
                val += (char)(random.nextInt(26) + temp);  
            } else if( "num".equalsIgnoreCase(charOrNum) ) {  
                val += String.valueOf(random.nextInt(10));  
            }  
        }
        return val;  
    }
    
  //生成随机数字  
    public static String getNumberRandom(int length) {  
        String val = "";  
        Random random = new Random();  
        //参数length，表示生成几位随机数  
        for(int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));  
        }
        return val;  
    }
    
    //处理身份证姓名
    public static String replaceIdCardName(String value) {
    	String strValue = StringUtil.null2Str(value);
    	try {
    		if(!StringUtil.isNull(strValue) && strValue.length() > 1) {
    			StringBuilder strBul = new StringBuilder();
    			int length = strValue.length();
    			for(int i = 0; i < length - 1; i++) {
    				strBul.append("*");
    			}
    			return strValue.replace(strValue.subSequence(0, length - 1), strBul.toString());
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return strValue;
    }
    
    //处理身份证号码
    public static String replaceIdCardNo(String value) {
    	String strValue = StringUtil.null2Str(value);
    	try {
    		if(!StringUtil.isNull(strValue) && strValue.length() == 18) {
    			return strValue.substring(0, 3) + "*************" +strValue.substring(16,18);
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return strValue;
    }
}

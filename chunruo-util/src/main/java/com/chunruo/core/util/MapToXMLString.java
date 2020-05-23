package com.chunruo.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Class MapToXMLString.
 *
 * @author sword.cai (c) Totyustar 2008.
 */
public class MapToXMLString {

	/**
	 * Converter Map<Object, Object> instance to xml string. Note: currently,
	 * we aren't consider more about some collection types, such as array,list,
	 *
	 * @param dataMap  the data map
	 *
	 * @return the string
	 */
	public static String converter(Map<String, Object> dataMap)
	{
		synchronized (MapToXMLString.class)
		{
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("<root>");
			Set<String> objSet = dataMap.keySet();
			for (Object key : objSet)
			{
				if (key == null)
				{
					continue;
				}
				strBuilder.append("<").append(key.toString()).append(">");
				Object value = dataMap.get(key);
				strBuilder.append(coverter(value));
				strBuilder.append("</").append(key.toString()).append(">");
			}
			strBuilder.append("</root>");
			return strBuilder.toString();
		}
	}

	public static String coverter(Object[] objects) {
		StringBuilder strBuilder = new StringBuilder();
		for(Object obj:objects) {
			String objectName = StringUtil.null2Str(obj.getClass().getName());
			if(objectName.contains(".")){
				objectName = objectName.substring(objectName.lastIndexOf(".") + 1);
			}
			if(objectName.length() >= 1){
				objectName = objectName.substring(0,1).toLowerCase() + objectName.substring(1);
			}
			if(objectName.length() >= 3 && objectName.substring(objectName.length() - 2).equalsIgnoreCase("vo")){
				objectName = objectName.substring(0, objectName.length() - 2);
			}
			strBuilder.append(String.format("<%s>", objectName));
			strBuilder.append(coverter(obj));
			strBuilder.append(String.format("</%s>", objectName));
		}
		return strBuilder.toString();
	}

	public static String coverter(Collection<?> objects){
		StringBuilder strBuilder = new StringBuilder();
		for(Object obj:objects) {
			String objectName = StringUtil.null2Str(obj.getClass().getName());
			if(objectName.contains(".")){
				objectName = objectName.substring(objectName.lastIndexOf(".") + 1);
			}
			if(objectName.length() >= 1){
				objectName = objectName.substring(0,1).toLowerCase() + objectName.substring(1);
			}
			if(objectName.length() >= 3 && objectName.substring(objectName.length() - 2).equalsIgnoreCase("vo")){
				objectName = objectName.substring(0, objectName.length() - 2);
			}
			strBuilder.append(String.format("<%s>", objectName));
			strBuilder.append(coverter(obj));
			strBuilder.append(String.format("</%s>", objectName));
		}
		return strBuilder.toString();
	}

	/**
	 * Coverter.
	 *
	 * @param object the object
	 * @return the string
	 */
	@SuppressWarnings("unused")
	public static String coverter(Object object){
		if (object instanceof Object[]){
			return coverter((Object[]) object);
		}
		if (object instanceof Collection){
			return coverter((Collection<?>) object);
		}
		
		StringBuilder strBuilder = new StringBuilder();
		if (isObject(object)){
			Class<? extends Object> clz = object.getClass();
			Field[] fields = clz.getDeclaredFields();

			for (Field field : fields){
				if(field == null 
						|| !Modifier.isPrivate(field.getModifiers())
						|| Modifier.isStatic(field.getModifiers())
						|| Modifier.isFinal(field.getModifiers())){
					continue;
				}
				
				field.setAccessible(true);
				String fieldName = field.getName();
				Object value = null;
				try{
					value = field.get(object);
				}catch (Exception e){
					continue;
				}
				
				if (value instanceof Object[] || value instanceof Collection){
					strBuilder.append("<").append(fieldName).append(">");
					strBuilder.append(coverter(value));
					strBuilder.append("</").append(fieldName).append(">");
				}else{
					strBuilder.append("<").append(fieldName).append(">");
					strBuilder.append(StringUtil.null2Str(value));
					strBuilder.append("</").append(fieldName).append(">");
				}
			}
		}else if (object == null){
			strBuilder.append("");
		}else if (object instanceof Date){
			strBuilder.append(DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, (Date)object));
		}else{
			strBuilder.append(object.toString());
		}
		return strBuilder.toString();
	}

	/**
	 * Checks if is object.
	 *
	 * @param obj the obj
	 *
	 * @return true, if is object
	 */
	private static boolean isObject(Object obj){
		if (obj == null){
			return false;
		}
		if (obj instanceof String){
			return false;
		}
		if (obj instanceof Integer){
			return false;
		}
		if (obj instanceof Double){
			return false;
		}
		if (obj instanceof Float){
			return false;
		}
		if (obj instanceof Byte){
			return false;
		}
		if (obj instanceof Long){
			return false;
		}
		if (obj instanceof Character){
			return false;
		}
		if (obj instanceof Short){
			return false;
		}
		if (obj instanceof Boolean){
			return false;
		}
		return true;
	}

	public static void main(String[] args){
    	// method方法没有定义
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("success", false);
		resultMap.put("message", "sss");
    	System.out.println(MapToXMLString.converter(resultMap));
    }
} 
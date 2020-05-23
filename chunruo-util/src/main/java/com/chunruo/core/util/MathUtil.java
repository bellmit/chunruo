package com.chunruo.core.util;

import java.math.BigDecimal;

public class MathUtil { 

/** 
* 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精 确的浮点数运算，包括加减乘除和四舍五入。 
*/ 
private static final int DEF_DIV_SCALE = 10; // 这个类不能实例化 

	private MathUtil() {
		
	} 
	
	/** 
	* 提供精确的加法运算。 
	* 
	* @param v1 
	*            被加数 
	* @param v2 
	*            加数 
	* @return 两个参数的和 
	*/ 
	public static String add(String v1, String v2) { 
		BigDecimal b1 = new BigDecimal(v1); 
		BigDecimal b2 = new BigDecimal(v2); 
		return String.valueOf(b1.add(b2)); 
	} 
	
	
	/** 
	* 提供精确的加法运算。 
	* 
	* @param v1 
	*            被加数 
	* @param v2 
	*            加数 
	* @return 两个参数的和 
	*/ 
	public static Double add(Double v1, Double v2) { 
		try {
			BigDecimal b1 = new BigDecimal(String.valueOf(v1)); 
			BigDecimal b2 = new BigDecimal(String.valueOf(v2)); 
			return StringUtil.nullToDouble(String.valueOf(b1.add(b2))); 
		}catch(Exception e) {
			e.printStackTrace();
		}
		return StringUtil.nullToDoubleFormat(v1 + v2);
	} 
	
	/** 
	* 提供精确的减法运算。 
	* 
	* @param v1 
	*            被减数 
	* @param v2 
	*            减数 
	* @return 两个参数的差 
	*/ 
	public static String sub(String v1, String v2) { 
		BigDecimal b1 = new BigDecimal(v1); 
		BigDecimal b2 = new BigDecimal(v2); 
		return String.valueOf(b1.subtract(b2)); 
	} 
	
	public static Double sub(Double v1, Double v2) { 
		try {
			BigDecimal b1 = new BigDecimal(String.valueOf(v1)); 
			BigDecimal b2 = new BigDecimal(String.valueOf(v2)); 
			return StringUtil.nullToDoubleFormat(String.valueOf(b1.subtract(b2))); 
		}catch(Exception e) {
			e.printStackTrace();
		}
		return StringUtil.nullToDoubleFormat(v1 - v2);
	}
	
	/** 
	* 提供精确的乘法运算。 
	* 
	* @param v1 
	*            被乘数 
	* @param v2 
	*            乘数 
	* @return 两个参数的积 
	*/ 
	public static String mul(String v1, String v2) { 
		BigDecimal b1 = new BigDecimal(v1); 
		BigDecimal b2 = new BigDecimal(v2); 
		return String.valueOf(b1.multiply(b2)); 
	} 
	
	/** 
	* 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。 
	* 
	* @param v1 
	*            被除数 
	* @param v2 
	*            除数 
	* @return 两个参数的商 
	*/ 
	public static String div(String v1, String v2) { 
		return div(v1, v2, DEF_DIV_SCALE); 
	} 
	public static String divs(String v1, String v2) { 
		return div(v1, v2, 1); 
	}
	/** 
	* 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。 
	* 
	* @param v1 
	*            被除数 
	* @param v2 
	*            除数 
	* @param scale 
	*            表示表示需要精确到小数点以后几位。 
	* @return 两个参数的商 
	*/ 
	public static String div(String v1, String v2, int scale) {
		if (scale < 0) { 
			throw new IllegalArgumentException("The scale must be a positive integer or zero"); 
		} 
		BigDecimal b1 = new BigDecimal(v1); 
		BigDecimal b2 = new BigDecimal(v2); 
		return String.valueOf(b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP));
	} 
	
	/** 
	* 提供精确的小数位四舍五入处理。 
	* 
	* @param v 
	*            需要四舍五入的数字 
	* @param scale 
	*            小数点后保留几位 
	* @return 四舍五入后的结果 
	*/ 
	public static Double round(Double value, int scale) { 
		if (value == null || scale < 0) { 
			return 0.0D;
		} 
		BigDecimal b = new BigDecimal(value); 
		return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/** 
	* 提供精确的小数位四舍五入处理。 
	* 
	* @param v 
	*            需要四舍五入的数字 
	* @param scale 
	*            小数点后保留几位 
	* @return 四舍五入后的结果 
	*/ 
	public static String getMathStr(Double value, int scale) {
		if (value == null || scale < 0) { 
			return "0.0";
		} 
		BigDecimal b = new BigDecimal(value);
		double doubleValue = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		return doubleValue+"";
	}
	
	public static Integer getPageNumber(Long totalNumber, Integer pagesize) {
		Long pageNum = Long.parseLong(pagesize.toString());
		Long page = totalNumber / pageNum;
		
		if(totalNumber % pageNum > 0){
			page = page + 1;
		}
		
		return page.intValue();
	}
} 
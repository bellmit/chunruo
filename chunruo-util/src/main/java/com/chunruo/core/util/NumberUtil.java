package com.chunruo.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtil {

	private static final Double MILLION = 10000.0;
    private static final Double MILLIONS = 1000000.0;
    private static final Double BILLION = 100000000.0;
    private static final String MILLION_UNIT = "万";
    private static final String BILLION_UNIT = "亿";
    
    /**
     * 将数字转换成以万为单位或者以亿为单位，因为在前端数字太大显示有问题
     *
     * @author
     * @version 1.00.00
     *
     * @date 2018年1月18日
     * @param amount 报销金额
     * @return
     */
    public static String amountConversion(Object value1){
		Double amount = StringUtil.nullToDouble(value1);
		// 最终返回的结果值
		String result = String.valueOf(amount);
		try {
			// 四舍五入后的值
			double value = 0;
			// 转换后的值
			double tempValue = 0;
			// 余数
			double remainder = 0;

			// 金额大于1百万小于1亿
			if (amount > MILLIONS && amount < BILLION) {
				tempValue = amount / MILLION;
				remainder = amount % MILLION;

				// 余数小于5000则不进行四舍五入
				if (remainder < (MILLION / 2)) {
					value = formatNumber(tempValue, 2, false);
				} else {
					value = formatNumber(tempValue, 2, true);
				}
				// 如果值刚好是10000万，则要变成1亿
				if (value == MILLION) {
					result = zeroFill(value / MILLION) + BILLION_UNIT;
				} else {
					result = zeroFill(value) + MILLION_UNIT;
				}
			}
			// 金额大于1亿
			else if (amount > BILLION) {
				tempValue = amount / BILLION;
				remainder = amount % BILLION;

				// 余数小于50000000则不进行四舍五入
				if (remainder < (BILLION / 2)) {
					value = formatNumber(tempValue, 2, false);
				} else {
					value = formatNumber(tempValue, 2, true);
				}
				result = zeroFill(value) + BILLION_UNIT;
			} else {
				result = zeroFill(amount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    }
    
    
    /**
     * 对数字进行四舍五入，保留2位小数
     *
     * @author
     * @version 1.00.00
     *
     * @date 2018年1月18日
     * @param number 要四舍五入的数字
     * @param decimal 保留的小数点数
     * @param rounding 是否四舍五入
     * @return
     */
    public static Double formatNumber(double number, int decimal, boolean rounding){
        BigDecimal bigDecimal = new BigDecimal(number);
        
        if(rounding){
            return bigDecimal.setScale(decimal,RoundingMode.HALF_UP).doubleValue();
        }else{
            return bigDecimal.setScale(decimal,RoundingMode.DOWN).doubleValue();
        }
    }
    
    /**
     * 对四舍五入的数据进行补0显示，即显示.00
     *
     * @author
     * @version 1.00.00
     *
     * @date 2018年1月23日
     * @return
     */
    public static String zeroFill(double number){
        String value = String.valueOf(number);
        
        if(value.indexOf(".")<0){
            value = value + ".00";
        }else{
            String decimalValue = value.substring(value.indexOf(".")+1);
            
            if(decimalValue.length()<2){
                value = value + "0";
            }
        }
        return value;
    }
    
    /** 
     * <pre> 
     * 数字格式化显示  
     * 小于万默认显示 大于万以1.7万方式显示最大是9999.9万  
     * 大于亿以1.1亿方式显示最大没有限制都是亿单位  
     * make by dongxh 2017年12月28日上午10:05:22 
     * </pre> 
     * @param num 
     *            格式化的数字 
     * @return 
     */  
	 public static String formatNumber(Object num) {  
	        StringBuffer sb = new StringBuffer();  
	        if (!StringUtil.isNumber(num))  
	            return "0";  
	      
	  
	        DecimalFormat df = new DecimalFormat("###.##");
	        BigDecimal b1 = new BigDecimal("10000");  
	        BigDecimal b2 = new BigDecimal("100000000");  
	        BigDecimal b3 = new BigDecimal(StringUtil.null2Str(num));  
	  
	        String formatNumStr = "";  
	        String nuit = "";  
	  
	        // 以万为单位处理  
	        if (b3.compareTo(b1) == -1) {  
	            sb.append(b3.toString());  
	        } else if ((b3.compareTo(b1) == 0 && b3.compareTo(b1) == 1)  
	                || b3.compareTo(b2) == -1) {  
	        	formatNumStr = df.format(b3.divide(b1)).toString(); 
	        	nuit = "万"; 
	        	if(formatNumStr.compareTo("10000") == 0) {
	        		BigDecimal a = new BigDecimal(formatNumStr);
	        		formatNumStr = df.format(a.divide(b1));
	        		nuit = "亿";  
	        	}
	        } else if (b3.compareTo(b2) == 0 || b3.compareTo(b2) == 1) {  
	            formatNumStr = df.format(b3.divide(b2)).toString();  
	            nuit = "亿";  
	        }  
	        sb.append(formatNumStr).append(nuit);  
//	        if (!"".equals(formatNumStr)) {  
//	            int i = formatNumStr.indexOf(".");  
//	            if (i == -1) {  
//	                sb.append(formatNumStr).append(nuit);  
//	            } else {  
//	                i = i + 1;  
//	                String v = formatNumStr.substring(i, i + 1);  
//	                if (!v.equals("0")) {  
//	                    sb.append(formatNumStr.substring(0, i + 1)).append(nuit);  
//	                } else {  
//	                    sb.append(formatNumStr.substring(0, i - 1)).append(nuit);  
//	                }  
//	            }  
//	        }  
	        if (sb.length() == 0)  
	            return "0";  
	        return sb.toString();
	 }
    
    public static void main(String[] args) {
//    	System.out.println(amountConversion("1.1"));
//    	System.out.println(amountConversion("11.22"));
//    	System.out.println(amountConversion("112.21"));
//		System.out.println(amountConversion("10000000000000"));
//		System.out.println(amountConversion("112"));
//		System.out.println(amountConversion("100000000.00"));
		System.out.println(amountConversion("9"));
	}
}

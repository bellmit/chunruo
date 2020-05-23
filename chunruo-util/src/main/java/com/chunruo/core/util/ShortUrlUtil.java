package com.chunruo.core.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;  
  
public class ShortUrlUtil {  
    private static final String[] l = {   
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",   
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",  
        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",  
        "u", "v", "w", "x", "y", "z",   
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",   
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",   
        "U", "V", "W", "X", "Y", "Z"};  
      
    private static int count = 100;  
  
    @SuppressWarnings("unused")
	private static int getCount(){  
        if(count>999)count = 100;  
        return count++;   
    }  
      
    //TentoN(这里是你想转换的数 ,这里是你想转换为多少进制 2-62之间）  
    public static String TentoN(long value, int number) {  
        if (number <= 1 || number > l.length) {  
            throw new RuntimeException("Faild");  
        }  
        //负数处理  
        if (value < 0) {  
            return "-" + TentoN(0 - value, number);  
        }  
        if (value < number) {  
            return l[(int)value];  
        } else {  
            long n = value % (long)number;  
            return (TentoN(value / number, number) + l[(int)n]);  
        }  
    }  
  
    /** 
     * 返回4位随机数 
     * @return 
     */  
    public static Integer getRandom2(){  
        Integer i = new Random().nextInt(9999);  
        while(i<1000)    i=i<<1;  
        return i;  
    }  
      
    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
 
 
public static String generateShortUuid() {
	StringBuffer shortBuffer = new StringBuffer();
	String uuid = UUID.randomUUID().toString().replace("-", "");
	for (int i = 0; i < 8; i++) {
		String str = uuid.substring(i * 4, i * 4 + 4);
		int x = Integer.parseInt(str, 16);
		shortBuffer.append(chars[x % 0x3E]);
	}
	return shortBuffer.toString();
 
}


    public static void main(String[] args) throws InterruptedException {  
        long a = System.currentTimeMillis();  
          
        Set<String> set = new HashSet<String>();
        for(int i=0;i<1000000;i++) {
        	String s = generateShortUuid();
        	set.add(s);
        	System.out.println(s);
        }
        long b = System.currentTimeMillis();  
        System.out.println("毫秒："+(b-a));  
        System.out.println(set.size());
    }  
} 
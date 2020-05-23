package com.chunruo.easypay;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

public class EasyMD5Utils {

    /** 
     * 根据文件计算出文件的MD5 
     * @param file 
     * @return 
     */  
	public static String getFileMD5(File file) {
    	if (!file.isFile()) {  
            return null;  
        } 
    	
    	try {
    		return DigestUtils.md5Hex(FileUtils.readFileToByteArray(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    /** 
     * 获取文件夹中的文件的MD5值 
     * @param file 
     * @param listChild 
     * @return 
     */  
    public static Map<String,String> getDirMD5(File file, boolean listChild){
        if(! file.isDirectory()){  
            return null;  
        }  
          
        Map<String, String> map = new HashMap<String, String>();
        String md5;
          
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {  
            File file2 = files[i];
            if(file2.isDirectory() && listChild){  
                map.putAll(getDirMD5(file2, listChild));  
            }else{  
                md5 = getFileMD5(file2);  
                if(md5 != null){  
                    map.put(file2.getPath(), md5);  
                }  
            }  
        }  
        return map;  
    }  
    
    /**
     * 对文件生成MD5摘要
     * 
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMd5Digest(byte[] data)
        throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        byte[] b = md.digest();
        return byte2hex(b);
    }
    
    public static String byte2hex(byte[] b) {
        return byte2hex(b, false, false);
    }
    
    /**
     * 2进制转16进制
     * 
     * @param b
     * @param hexUpperCase 是否大写
     * @param hexInsBlank 是8位一空格
     * @return
     */
    public static String byte2hex(byte[] b, boolean hexUpperCase, boolean hexInsBlank) // 二进制转字符串
    {
        String hs = "";
        for (int n = 0; n < b.length; n++) {
            if (hexInsBlank && n > 0 && n % 4 == 0)
                hs = hs + " ";
            if (hexUpperCase)
                hs = hs + Integer.toString((b[n] & 0xFF) + 0x100, 16).substring(1).toUpperCase();
            else
                hs = hs + Integer.toString((b[n] & 0xFF) + 0x100, 16).substring(1);
        }
        return hs;
    }
    
    public static byte[] digestMD5(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        return md.digest();
    }
    
}

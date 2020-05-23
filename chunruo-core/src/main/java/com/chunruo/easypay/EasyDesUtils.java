package com.chunruo.easypay;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.security.Key;

/**
 * <p>
 * DES加密解密工具包
 * </p>
 * 
 */
public class EasyDesUtils {
    private static final String ALGORITHM = "DES";
    private static final int CACHE_SIZE = 1024;

    /**
     * 加密
     * 
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        return cipher.doFinal(data);
    }
    
    /**
     * 文件加密
     * 
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
     */
    public static void encryptFile(byte [] key, String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destFile);
            Key k = toKey(key);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, k);
            CipherInputStream cin = new CipherInputStream(in, cipher);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = cin.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            cin.close();
            in.close();
        }
    }
    
    /**
     * 解密
     * 
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        return cipher.doFinal(data);
    }
    
    /**
     * 文件解密
     * 
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
     */
    public static void decryptFile(byte[] key, String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destFile);
            Key k = toKey(key);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, k);
            CipherOutputStream cout = new CipherOutputStream(out, cipher);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                cout.write(cache, 0, nRead);
                cout.flush();
            }
            cout.close();
            out.close();
            in.close();
        }
    }

    /**
     * 转换密钥
     * 
     * @param key
     * @return
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        return secretKey;
    }
/*
    public static String getEncrypedPasswordBase64(String password) throws Exception {
    	return Base64Utils.encode(DESUtils.encrypt(password.getBytes(), APIConstant.MAIN_KEY.getBytes())) ;
    }

    public static String getDecrypedPasswordBase64(String b64password) throws Exception {
    	return new String(DESUtils.decrypt(Base64Utils.decode(b64password), APIConstant.MAIN_KEY.getBytes()));
    }
    
    public static void main(String[] args) throws Exception {
//		System.out.println("tt 1 = " + JSONObject.toJSONString(System.getProperty("line.separator")));
//		System.setProperty("line.separator", "") ;
//		System.out.println("tt 2 = " + JSONObject.toJSONString(System.getProperty("line.separator")));
    	String b64 = DESUtils.getEncrypedPasswordBase64("111111");
    	System.out.println("b64 = " + b64) ;
    	String d64 = DESUtils.getDecrypedPasswordBase64(b64);
    	System.out.println("d64 = " + d64 + ", len = " + d64.length()) ;
	}*/
}
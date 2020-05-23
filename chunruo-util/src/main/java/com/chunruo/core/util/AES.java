package com.chunruo.core.util;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static final String iv = "jikeduoweidianxx";//偏移量字符串必须是16位 当模式是CBC的时候必须设置偏移量
    private static final String Algorithm = "AES";
    private static final String AlgorithmProvider = "AES/CBC/PKCS5Padding"; //算法/模式/补码方式

    public static byte[] generatorKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(Algorithm);
        keyGenerator.init(256);//默认128，获得无政策权限后可为192或256
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    public static IvParameterSpec getIv() throws UnsupportedEncodingException {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("utf-8"));
        System.out.println("偏移量："+byteToHexString(ivParameterSpec.getIV()));
        return ivParameterSpec;
    }

    public static String encrypt(String src) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
    	byte key[] = "jikeduoshopweidi".getBytes("utf-8");
    	SecretKey secretKey = new SecretKeySpec(key, Algorithm);
        IvParameterSpec ivParameterSpec = getIv();
        Cipher cipher = Cipher.getInstance(AlgorithmProvider);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] cipherBytes = cipher.doFinal(src.getBytes(Charset.forName("utf-8")));
        return byteToHexString(cipherBytes);
    }

    public static String decrypt(String src) throws Exception {
    	byte key[] = "jikeduoshopweidi".getBytes("utf-8");
        SecretKey secretKey = new SecretKeySpec(key, Algorithm);

        IvParameterSpec ivParameterSpec = getIv();
        Cipher cipher = Cipher.getInstance(AlgorithmProvider);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] hexBytes = hexStringToBytes(src);
        byte[] plainBytes = cipher.doFinal(hexBytes);
        return byteToHexString(plainBytes);
    }

    /**
     * 将byte转换为16进制字符串
     * @param src
     * @return
     */
    public static String byteToHexString(byte[] src) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xff;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append("0");
            }
            sb.append(hv);
        }
        return sb.toString();
    }

    /**
     * 将16进制字符串装换为byte数组
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            b[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return b;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

//    public static void main(String[] args) {
//        try {
//            // byte key[] = generatorKey();
//            // 密钥必须是16的倍数
////            byte key[] = "1111111111111111".getBytes("utf-8");//hexStringToBytes("0123456789ABCDEF");
//            String src = "zhangyueniubi";
////            System.out.println("密钥:"+byteToHexString(key));
//            System.out.println("原字符串:"+src);
//
//            String enc = encrypt(src);
//            System.out.println("加密："+enc);
//            System.out.println("解密："+new String(decrypt("f9945a738f1c219ce54dfabe53337ed99a38eeed0f20217af9178569368fa8bb6f340eec04ee4811ba34fb90bb8fbcb49db40d13ba275b2bfd5ca53fa95d240882867a86adf101803987ac67e05d669a552d658fd27ba52c236bdcf19dd05bb2eba53eb49be76079fcf2d16bebdb0871c9797ad77ecac45a41b212c26899917941108aea4a502572da2ac19b91c80d40a2f3a21d45f8ab6bff45c5bfb69793cd560e08ef6722b82c3e9f0e0c72787ec54bf1de8537b94bd81c58bd7d65455e381acf93448524a68b71b474ce52eeec6a6bb531e54b53c87d1d3893bbb9c769d343f95261f3bf6a1811c86ccdd2699d4b497fe9a6b8a13803349d456c0974c26616b4c515935d5c62b33a6baad33ca7d4"), "utf-8"));
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
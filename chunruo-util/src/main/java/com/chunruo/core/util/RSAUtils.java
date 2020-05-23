package com.chunruo.core.util;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RSAUtils {
	
	public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";
    
	/** 
     * 生成公钥和私钥 
     * @throws NoSuchAlgorithmException  
     * 
     */  
    public static HashMap<String, Object> getKeys() throws NoSuchAlgorithmException{
    	Security.addProvider(new BouncyCastleProvider());
        HashMap<String, Object> map = new HashMap<String, Object>();  
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_ALGORITHM, new BouncyCastleProvider());  
        keyPairGen.initialize(1024);  
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        map.put("public", publicKey);  
        map.put("private", privateKey);  
        return map;  
    }  
    /** 
     * 使用模和指数生成RSA公钥 
     * @param modulus  模 
     * @param exponent  指数 
     * @return 
     */  
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {  
    	Security.addProvider(new BouncyCastleProvider());
        try {  
            BigInteger b1 = new BigInteger(modulus);  
            BigInteger b2 = new BigInteger(exponent);  
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM, new BouncyCastleProvider());  
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);  
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
    /** 
     * 使用模和指数生成RSA私钥 
     * /None/NoPadding
     * @param modulus     模 
     * @param exponent   指数 
     * @return 
     */  
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {  
        try {  
        	Security.addProvider(new BouncyCastleProvider());
            BigInteger b1 = new BigInteger(modulus);  
            BigInteger b2 = new BigInteger(exponent);  
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM, new BouncyCastleProvider());  
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);  
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
    
    
    /** 
     * 私钥解密 
     *  
     * @param data 
     * @param privateKey 
     * @return 
     * @throws Exception 
     */  
    public static String decryptByPrivateKeyStr(String data, String privateKeyStr)  
            throws Exception { 
    	try {
    		RSAPrivateKey priKey = RSAUtils.loadPrivateKeyByStr(privateKeyStr);
			return RSAUtils.privateDecrypt(data, priKey);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    } 
  
    /** 
     * 公钥加密 
     *  
     * @param data 
     * @param publicKey 
     * @return 
     * @throws Exception 
     */  
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey)  
            throws Exception {  
    	Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM, new BouncyCastleProvider());  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
        // 模长  
        int key_len = publicKey.getModulus().bitLength() / 8;  
        // 加密数据长度 <= 模长-11  
        String[] datas = splitString(data, key_len - 11);  
        String mi = "";  
        //如果明文长度大于模长-11则要分组加密  
        for (String s : datas) {  
            mi += bcd2Str(cipher.doFinal(s.getBytes()));  
        }  
        return mi;  
    }  
  
    /** 
     * 私钥解密 
     *  
     * @param data 
     * @param privateKey 
     * @return 
     * @throws Exception 
     */  
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey)  
            throws Exception { 
    	Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM, new BouncyCastleProvider());  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
        //模长  
        int key_len = privateKey.getModulus().bitLength() / 8;  
        byte[] bytes = data.getBytes();  
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);  
        //System.err.println(bcd.length);  
        //如果密文长度大于模长则要分组解密  
        String ming = "";  
        byte[][] arrays = splitArray(bcd, key_len);  
        for(byte[] arr : arrays){  
            ming += new String(cipher.doFinal(arr));  
        }  
        return ming;  
    }  
    
    /** 
     * 从字符串中加载公钥 
     *  
     * @param publicKeyStr 
     *            公钥数据字符串 
     * @throws Exception 
     *             加载公钥时产生的异常 
     */  
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)  
            throws Exception {  
        try {  
            byte[] buffer = Base64.decodeBase64(publicKeyStr);  
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);  
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);  
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("公钥非法");  
        } catch (NullPointerException e) {  
            throw new Exception("公钥数据为空");  
        }  
    }  
    
    /** 
     * 从字符串中加载私钥 
     *  
     * @param privateKeyStr 
     *            
     * @return RSAPrivateKey
     * @throws Exception 
     */ 
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)  
            throws Exception {  
        try {  
            byte[] buffer = Base64.decodeBase64(privateKeyStr);  
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("私钥非法");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥数据为空");  
        }  
    } 
    
    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }
    
    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }
    
    /** 
     * ASCII码转BCD码 
     *  
     */  
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {  
        byte[] bcd = new byte[asc_len / 2];  
        int j = 0;  
        for (int i = 0; i < (asc_len + 1) / 2; i++) {  
            bcd[i] = asc_to_bcd(ascii[j++]);  
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));  
        }  
        return bcd;  
    }  
    public static byte asc_to_bcd(byte asc) {  
        byte bcd;  
  
        if ((asc >= '0') && (asc <= '9'))  
            bcd = (byte) (asc - '0');  
        else if ((asc >= 'A') && (asc <= 'F'))  
            bcd = (byte) (asc - 'A' + 10);  
        else if ((asc >= 'a') && (asc <= 'f'))  
            bcd = (byte) (asc - 'a' + 10);  
        else  
            bcd = (byte) (asc - 48);  
        return bcd;  
    }  
    /** 
     * BCD转字符串 
     */  
    public static String bcd2Str(byte[] bytes) {  
        char temp[] = new char[bytes.length * 2], val;  
  
        for (int i = 0; i < bytes.length; i++) {  
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);  
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
  
            val = (char) (bytes[i] & 0x0f);  
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
        }  
        return new String(temp);  
    }  
    /** 
     * 拆分字符串 
     */  
    public static String[] splitString(String string, int len) {  
        int x = string.length() / len;  
        int y = string.length() % len;  
        int z = 0;  
        if (y != 0) {  
            z = 1;  
        }  
        String[] strings = new String[x + z];  
        String str = "";  
        for (int i=0; i<x+z; i++) {  
            if (i==x+z-1 && y!=0) {  
                str = string.substring(i*len, i*len+y);  
            }else{  
                str = string.substring(i*len, i*len+len);  
            }  
            strings[i] = str;  
        }  
        return strings;  
    }  
    /** 
     *拆分数组  
     */  
    public static byte[][] splitArray(byte[] data,int len){  
        int x = data.length / len;  
        int y = data.length % len;  
        int z = 0;  
        if(y!=0){  
            z = 1;  
        }  
        byte[][] arrays = new byte[x+z][];  
        byte[] arr;  
        for(int i=0; i<x+z; i++){  
            arr = new byte[len];  
            if(i==x+z-1 && y!=0){  
                System.arraycopy(data, i*len, arr, 0, y);  
            }else{  
                System.arraycopy(data, i*len, arr, 0, len);  
            }  
            arrays[i] = arr;  
        }  
        return arrays;  
    }
    
    public static void main(String[] args) throws Exception{
//    	HashMap<String, Object> map = getKeys();  
//        //生成公钥和私钥  
//        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");  
//        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");  
//        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));  
//        System.out.println("公钥："+publicKeyString);
//		// 得到私钥字符串  
//		String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded()))); 
//		System.out.println("私钥："+privateKeyString);
//        //模  
//        String modulus = publicKey.getModulus().toString();  
//        System.out.println("pubkey modulus="+modulus);
//        //公钥指数  
//        String public_exponent = publicKey.getPublicExponent().toString();
//        System.out.println("pubkey exponent="+public_exponent);
//        //私钥指数  
//        String private_exponent = privateKey.getPrivateExponent().toString();  
//        System.out.println("private exponent="+private_exponent);
//        //明文  
          String ming = "111111|1575958665965";  
//        //使用模和指数生成公钥和私钥  
//        RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);  
//        RSAPrivateKey priKey = RSAUtils.getPrivateKey(modulus, private_exponent);  
        
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClY/fwIldccWpLeRjE2rPi3FSsqj/64UBm0XHjmVs/opsjliKNFuwXxoj4R+5DI23sNGOIQIvIidJLeK75EccPTRQgkzXHjGvxXpWM9AKhZVi228DPVUYuzji2S1HxUkmJYFVh0uCnLOnPHV00iP99bLuUFDZUiJz8D1C/6nj3fwIDAQAB";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKVj9/AiV1xxakt5GMTas+LcVKyqP/rhQGbRceOZWz+imyOWIo0W7BfGiPhH7kMjbew0Y4hAi8iJ0kt4rvkRxw9NFCCTNceMa/FelYz0AqFlWLbbwM9VRi7OOLZLUfFSSYlgVWHS4Kcs6c8dXTSI/31su5QUNlSInPwPUL/qePd/AgMBAAECgYEAllUB3wI4Ck0E40lVXEm2zPSc+r1dnnr0gQDL1qdkq7jv7y7ehQRa4VmS0qr/RYOEvPj5p7WT2Vp9DiDKWpbwKeA8iUJscJ8G8Jdn831UmOy3e4taRjE5MGN0RsGfY9NOX5ItQ2QXsX0XEjJu7BXRIdhYzPQuthOguBlb655PzeECQQDP+2qFB8Pt8/ImtD2fZS2sb2u6b98S3xeaOAzvoPWCdLP/VT6CgF1EHqbr8fn5AJG8jnzBvrdzL4b6DBd7I+npAkEAy5M3hEZ/IWWuy0Wx396m7xDzCYAZ0WqZWnh7if3ZuKrM77LWRZOEK8eyQWr1yF1c+u6b5Qvvab+/E5VgmPONJwJAYjxjfMBFOCCQUN2zeZVFtHvQS3hJylUbhdlRquHA9MSAvGqcIvHUm7dKqolZw9YUABbhMHYPmlVpkLOjZ6N3+QJAE7DKpuW2uEFAlfLI0LGVjfwiteGJHcNkz+ZldKR0IbOLnQe8SACLwTxQlplE6rt1GeRoedAcuSrNzZQeQs0T8wJBAIsFBpqfeLDqNhSCTvARqzoovM/lwXMd5350oIrFO6iHWvz0S9ysAWHb0Nw1XEhCJQCz31lA4xJm32Am636IGvo=";
       
        String aa = "NNZdrgvum6_iX-xtsU3FYNS1tNQYp8qwbvtJ0hpo6hpCFwCOGSThdEBu6GCFMWvh9hIYYe_Wh07wb0XoSmtFtu6OA7ouJL222B7UrMNyyT2vbHygVzQtUuKhBg3jS4YT0qOf1lDWb96ogsjy_RpvbFdC7nfqkuvGl9ic0tArjhQ";
        RSAPublicKey pubKey = getPublicKey(publicKey);
        RSAPrivateKey priKey = getPrivateKey(privateKey);
         //加密后的密文  
        String mi = RSAUtils.publicEncrypt(ming, pubKey);
        System.err.println("mi="+mi);  
        //解密后的明文  
        String ming2 = RSAUtils.privateDecrypt(aa, priKey);
        System.err.println("ming2="+ming2); 
        
    }
}

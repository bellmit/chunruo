package com.chunruo.core.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AESUtil {
	private SecretKey _KEY;
	private IvParameterSpec _IV;
	private Cipher cipher;
	public static final String transformation = "AES/CBC/PKCS5PADDING";
	public static final String IV = "mengdong.com";
	public static boolean initialized = false; 
	
	
	public AESUtil(String transformation) {
		try {
			if (transformation.equals("AES/ECB/PKCS5PADDING")
					|| transformation.equals("AES/CBC/PKCS5PADDING")) {
				cipher = Cipher.getInstance(transformation);
			} else {
				throw new Exception(
						"transformation = AES/ECB/PKCS5PADDING or AES/CBC/PKCS5PADDING");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public byte[] doAES(byte[] byteContent, int opmode) {
		try {
			if (cipher.getAlgorithm().indexOf("ECB") != -1) {
				cipher.init(opmode, getKEY());
			}
			if (cipher.getAlgorithm().indexOf("CBC") != -1) {
				cipher.init(opmode, getKEY(), getIV());
			}
			byte[] resultByteContent = cipher.doFinal(byteContent);
			return resultByteContent;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String encrypt(byte[] byteContent) {
		try {
			return base64_Encode(doAES(byteContent, Cipher.ENCRYPT_MODE));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public byte[] decrypt(String strContent) {
		try {
			return doAES(base64_Decode(strContent), Cipher.DECRYPT_MODE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private String base64_Encode(byte[] byteContent) {
		try {
			return new String(new Base64().encode(byteContent), "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private byte[] base64_Decode(String base64Str) {
		try {
			return new Base64().decode(base64Str.getBytes("UTF-8"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void setKEY(String keyStr, Boolean isMD5) {
		_KEY = AESUtil.generateKey(keyStr, isMD5);
	}

	public SecretKey getKEY() {
		if (_KEY == null) {
			_KEY = AESUtil.generateKey("", false);
		}
		return _KEY;
	}

	public static SecretKey generateKey(String seedStr, Boolean isMD5) {
		try {
			if (seedStr.equals("")) {
				SecureRandom random = new SecureRandom();
				seedStr = AESUtil.bytesToHexString(random.generateSeed(16));
			}
			// ----------------------
			SecretKey secretKey;
			if (isMD5) {
				secretKey = new SecretKeySpec(md5Hex(seedStr).substring(0, 16).getBytes("UTF-8"), "AES");
			} else {
				KeyGenerator kgen = KeyGenerator.getInstance("AES");
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				random.setSeed(seedStr.getBytes()); // 生成基于种子的字节
				kgen.init(128, random);
				secretKey = kgen.generateKey();
			}
//			System.out.println("Key:"
//					+ new String(secretKey.getEncoded(), "UTF-8"));
			return secretKey;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void setIV(String ivStr, Boolean isMD5) {
		_IV = AESUtil.generateIV(ivStr, isMD5);
	}

	public IvParameterSpec getIV() {
		if (_IV == null) {
			_IV = AESUtil.generateIV("", false);
		}
		return _IV;
	}

	public static IvParameterSpec generateIV(String seedStr, Boolean isMD5) {
		try {

			IvParameterSpec IV;
			if (seedStr.equals("")) {
				SecureRandom random = new SecureRandom();
				seedStr = AESUtil.bytesToHexString(random.generateSeed(16));
			}
			if (isMD5) {
				seedStr = md5Hex(seedStr).substring(0, 16);
			} else {
				seedStr = (seedStr + "0000000000000000").substring(0, 16);
			}
			IV = new IvParameterSpec(seedStr.getBytes("UTF-8"));
//			System.out.println("IV(hex):" + AESUtil.bytesToHexString(IV.getIV()));
			return IV;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static File generateKeyFile(SecretKey key, String keyFilePath) {
		try {
			byte[] enCodeFormat = key.getEncoded();
			File keyFile = new File(keyFilePath);
			FileUtils.writeByteArrayToFile(keyFile, enCodeFormat);
			return keyFile;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	public static String md5Hex(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}
	
	public static final void decryptFile(String transformation, String iv, String key,
			String from, String to) throws IOException {
		AESUtil hepaes = new AESUtil(transformation);
		hepaes.setKEY(key, true);
		hepaes.setIV(iv, true);
		FileUtils.writeByteArrayToFile(new File(to), hepaes.decrypt(new String(
				FileUtils.readFileToByteArray(new File(from)))));
	}
	
	public static final void encryptFile(String transformation, String iv, String key,
			String from, String to) throws UnsupportedEncodingException,
			IOException {
		AESUtil hepaes = new AESUtil(transformation);
		hepaes.setKEY(key, true);
		hepaes.setIV(iv, true);
		FileUtils.writeByteArrayToFile(new File(to),
				hepaes.encrypt(FileUtils.readFileToByteArray(new File(from))).getBytes("UTF-8"));
	}
	
	public static final String encryptString(String transformation, String iv, String key, String json) throws UnsupportedEncodingException {
		AESUtil hepaes = new AESUtil(transformation);
		hepaes.setKEY(key, true);
		hepaes.setIV(iv, true);
		return hepaes.encrypt(json.getBytes("UTF-8"));
	}
	
	public static final String decryptString(String transformation, String iv, String key, String json) {
		try {
			AESUtil hepaes = new AESUtil(transformation);
			hepaes.setKEY(key, true);
			hepaes.setIV(iv, true);
			byte[] byteEncrypt = hepaes.decrypt(json);
			return new String(byteEncrypt);
		}catch(Exception e) {
			//e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * AES解密
	 * @param content 密文
	 * @return
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchProviderException 
	 */
	public static String decrypt(byte[] content, byte[] keyByte, byte[] ivByte) throws InvalidAlgorithmParameterException {
		initialize();
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			Key sKeySpec = new SecretKeySpec(keyByte, "AES");
			
			cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化 
			byte[] resultByte = cipher.doFinal(content);
			if(null != resultByte && resultByte.length > 0){
				String result = new String(resultByte, "UTF-8");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();  
		}
		return "";
	}  
	
	public static void initialize(){  
        if (initialized) return;  
        Security.addProvider(new BouncyCastleProvider());  
        initialized = true;  
    }
	
	//生成iv  
    public static AlgorithmParameters generateIV(byte[] iv) throws Exception{  
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");  
        params.init(new IvParameterSpec(iv));  
        return params;  
    }  
	
//	public static void main(String[] args) {
//		String key = "icourses" + "ff808081-362dba52-0136-2dba5226-0000";
//		String from = "D:\\encrypt\\ff808081-362dba52-0136-2dba5226-0000.m3u8.ics";
//		String to = "D:\\encrypt\\test1.m3u8";
//
//		try {
////			String iv = "vstudying.com";
////			System.out.println("decryptFile start!");
////			decryptFile(AESUtil.transformation, AESUtil.IV, key, from, to);
////			System.out.println("decryptFile end!");
////
////			String m3u8 = "D:\\encrypt\\ff808081-362dba52-0136-2dba5226-0000-longlong.m3u8";
////			String ics = "D:\\encrypt\\test1.m3u8.ics";
////			
////			System.out.println("encryptFile start!");
////			encryptFile(AESUtil.transformation, AESUtil.IV, key, to, ics);
////			System.out.println("encryptFile end!");
//
//			// System.out.println("decryptFile start!");
//			// String icsto = ics+"2.m3u8";
//			// decryptFile(PWD, ics, to);
//
//			String pwd = "88888-0001-2101USER_LOGIN#portal@vstudying.css!";
//			String userPwd = "111111";
//			String passwd = AESUtil.encryptString(AESUtil.transformation, AESUtil.IV, pwd, userPwd);
//			
//			System.out.println("decrypt passwd -> " + passwd);
////			String key2 = AESUtil.md5Hex(pwd.toString()).substring(0,16);
//			
////			System.out.println("key2 string -> " + key2);
////			String json = "{'result':'1','desc':'成功','list':[]}";
////			System.out.println("json string -> " + json);
////			String encrypt;
//
////			encrypt = encryptString(AESUtil.transformation, AESUtil.IV, key, json);
//			
//			
//			String decrypt = decryptString(AESUtil.transformation, AESUtil.IV, pwd, passwd);
//			
//			System.out.println("decrypt string -> " + decrypt);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}

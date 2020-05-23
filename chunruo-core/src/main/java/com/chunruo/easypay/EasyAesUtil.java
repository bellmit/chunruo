package com.chunruo.easypay;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public abstract class EasyAesUtil {

	private static Logger logger = LoggerFactory.getLogger(EasyAesUtil.class);
	public static final String algorithm = "AES";
	public static final String transformation = "AES/ECB/PKCS5Padding";
	
	/**
	 * 解密
	 * 
	 * @param str
	 * @return
	 */
	public static String doDecrypt(String str, String aesKey) {
		try {

			byte[] bytesKey = Base64.decodeBase64(aesKey.getBytes("utf-8"));

			SecretKey dataKey = new SecretKeySpec(bytesKey, algorithm);

			Cipher cipher = Cipher.getInstance(transformation);

			cipher.init(Cipher.DECRYPT_MODE, dataKey);

			byte[] result = cipher.doFinal(Base64.decodeBase64(str));

			return new String(result);
		} catch (Exception e) {
			logger.error("AES解密失败", e);
		}
		return null;
	}

	/**
	 * 加密
	 * 
	 * @param str
	 * @return
	 */
	public static String doEncrypt(String str, String aesKey) {
		try {
			byte[] bytesKey = Base64.decodeBase64(aesKey.getBytes("utf-8"));
			SecretKey dataKey = new SecretKeySpec(bytesKey, algorithm);

			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.ENCRYPT_MODE, dataKey);
			byte[] result = cipher.doFinal(str.getBytes());

			return Base64.encodeBase64String(result);
		} catch (Exception e) {
			logger.error("AES加密失败", e);
		}
		return null;
	}

	/**
	 * AES加密
	 * 
	 * @param data
	 *            需要被加密的字符串
	 * @param key
	 * 
	 * @return 密文
	 */
	public static byte[] encrypt(byte[] content, byte[] key) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, algorithm);

			Cipher cipher = Cipher.getInstance(transformation);// 创建密码器

			cipher.init(Cipher.ENCRYPT_MODE, secretKey);// 初始化为加密模式的密码器

			byte[] result = cipher.doFinal(content);// 加密
			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 加密
	 * 
	 * @param content
	 * @param secretKey
	 * @return
	 */
	public static byte[] encrypt(byte[] content, SecretKey secretKey) {
		try {
			Cipher cipher = Cipher.getInstance(transformation);// 创建密码器
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);// 初始化为加密模式的密码器
			byte[] result = cipher.doFinal(content);// 加密
			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 解密AES加密过的字符串
	 * 
	 * @param content
	 *            AES加密过过的内容
	 * @param key
	 * 
	 * @return 明文
	 */
	public static byte[] decrypt(byte[] content, byte[] key) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, algorithm);
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] result = cipher.doFinal(content);
			return result; // 明文

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 解密
	 * 
	 * @param content
	 * @param secretKey
	 * @return
	 */
	public static byte[] decrypt(byte[] content, SecretKey secretKey) {
		try {
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] result = cipher.doFinal(content);
			return result; // 明文

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 生成秘钥
	 * 
	 * @param keySize
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] initKey(int keySize) throws NoSuchAlgorithmException {

		KeyGenerator kg = KeyGenerator.getInstance(algorithm);
		kg.init(keySize, new SecureRandom());
		// 生成秘密密钥
		SecretKey secretKey = kg.generateKey();
		// 获得密钥的二进制编码形式
		return secretKey.getEncoded();
	}

	/**
	 * 解密文件
	 * 
	 * @param is
	 * @return
	 */
	public static byte[] decryptFile(InputStream is, String aesKey) {

		CipherInputStream cis = null;
		ByteArrayOutputStream out = null;
		byte[] rst;
		try {
			byte[] bytesKey = Base64.decodeBase64(aesKey.getBytes("utf-8"));

			SecretKey secretKey = new SecretKeySpec(bytesKey, algorithm);

			Cipher cipher = Cipher.getInstance(transformation);

			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			cis = new CipherInputStream(is, cipher);
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int r;
			while ((r = cis.read(buffer)) > 0) {
				out.write(buffer, 0, r);
			}
			rst = out.toByteArray();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				cis.close();
			} catch (Throwable th) {
			}
			try {
				out.close();
			} catch (Throwable th) {
			}
		}
		return rst;
	}

	/**
	 * 加密文件
	 * 
	 * @param filePath
	 * @param destFilePath
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static void encryptFile(String filePath, String destFilePath, String aesKey) throws Exception {
		File filein = new File(filePath);
		File fileout = new File(destFilePath);
		
		byte[] bytesKey = Base64.decodeBase64(aesKey.getBytes("utf-8"));
		SecretKey secretKey = new SecretKeySpec(bytesKey, algorithm);
		Cipher cipher = Cipher.getInstance(transformation);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		InputStream is = new FileInputStream(filein);
		OutputStream out = new FileOutputStream(fileout);
		CipherInputStream cis = new CipherInputStream(is, cipher);
		try {
			byte[] buffer = new byte[1024];
			int r;
			while ((r = cis.read(buffer)) > 0) {
				out.write(buffer, 0, r);
			}
		} finally {
			cis.close();
			is.close();
			out.close();
		}
	}


}

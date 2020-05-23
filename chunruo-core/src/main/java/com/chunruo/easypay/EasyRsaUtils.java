package com.chunruo.easypay;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Enumeration;

/**
 * <p>
 * 数字签名/加密解密工具包
 * </p>
 *
 * @author IceWee
 * @date 2012-4-26
 * @version 1.0
 */
public class EasyRsaUtils {

	private static Logger logger = LoggerFactory.getLogger(EasyRsaUtils.class);

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Java密钥库(Java 密钥库，JKS)KEY_STORE
	 */
	public static final String KEY_STORE = "JKS";

	public static final String X509 = "X.509";

	/**
	 * 文件读取缓冲区大小
	 */
	private static final int CACHE_SIZE = 2048;

	/**
	 * 最大文件加密块
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/**
	 * 最大文件解密块
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;

	/**
	 * <p>
	 * 根据密钥库获得私钥
	 * </p>
	 *
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	private static PrivateKey getPrivateKey(String keyStorePath, String alias, String password) throws Exception {
		KeyStore keyStore = getKeyStore(keyStorePath, password);
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
		return privateKey;
	}

	/**
	 * <p>
	 * 获得密钥库
	 * </p>
	 *
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	private static KeyStore getKeyStore(String keyStorePath, String password) throws Exception {
		FileInputStream in = new FileInputStream(keyStorePath);
		KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
		keyStore.load(in, password.toCharArray());
		in.close();
		return keyStore;
	}

	/**
	 * <p>
	 * 根据证书获得公钥
	 * </p>
	 *
	 * @param certificatePath
	 *            证书存储路径
	 * @return
	 * @throws Exception
	 */
	private static PublicKey getPublicKey(String certificatePath) throws Exception {
		Certificate certificate = getCertificate(certificatePath);
		PublicKey publicKey = certificate.getPublicKey();
		return publicKey;
	}

	/**
	 * <p>
	 * 获得证书
	 * </p>
	 *
	 * @param certificatePath
	 *            证书存储路径
	 * @return
	 * @throws Exception
	 */
	private static Certificate getCertificate(String certificatePath) throws Exception {
		CertificateFactory certificateFactory = CertificateFactory.getInstance(X509);
		FileInputStream in = new FileInputStream(certificatePath);
		Certificate certificate = certificateFactory.generateCertificate(in);
		in.close();
		return certificate;
	}

	/**
	 * <p>
	 * 根据密钥库获得证书
	 * </p>
	 *
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	private static Certificate getCertificate(String keyStorePath, String alias, String password) throws Exception {
		KeyStore keyStore = getKeyStore(keyStorePath, password);
		Certificate certificate = keyStore.getCertificate(alias);
		return certificate;
	}

	/**
	 * <p>
	 * 私钥加密
	 * </p>
	 *
	 * @param data
	 *            源数据
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String keyStorePath, String alias, String password)
			throws Exception {
		// 取得私钥
		PrivateKey privateKey = getPrivateKey(keyStorePath, alias, password);
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/**
	 * <p>
	 * 文件私钥加密
	 * </p>
	 * <p>
	 * 过大的文件可能会导致内存溢出 </>
	 *
	 * @param filePath
	 *            文件路径
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptFileByPrivateKey(String filePath, String keyStorePath, String alias, String password)
			throws Exception {
		byte[] data = fileToByte(filePath);
		return encryptByPrivateKey(data, keyStorePath, alias, password);
	}

	/**
	 * <p>
	 * 文件加密
	 * </p>
	 *
	 * @param srcFilePath
	 *            源文件
	 * @param destFilePath
	 *            加密后文件
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @throws Exception
	 */
	public static void encryptFileByPrivateKey(String srcFilePath, String destFilePath, String keyStorePath,
											   String alias, String password) throws Exception {
		// 取得私钥
		PrivateKey privateKey = getPrivateKey(keyStorePath, alias, password);
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		File srcFile = new File(srcFilePath);
		FileInputStream in = new FileInputStream(srcFile);
		File destFile = new File(destFilePath);
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		destFile.createNewFile();
		OutputStream out = new FileOutputStream(destFile);
		byte[] data = new byte[MAX_ENCRYPT_BLOCK];
		byte[] encryptedData; // 加密块
		while (in.read(data) != -1) {
			encryptedData = cipher.doFinal(data);
			out.write(encryptedData, 0, encryptedData.length);
			out.flush();
		}
		out.close();
		in.close();
	}

	/**
	 * <p>
	 * 文件加密成BASE64编码的字符串
	 * </p>
	 *
	 * @param filePath
	 *            文件路径
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	public static String encryptFileToBase64ByPrivateKey(String filePath, String keyStorePath, String alias,
														 String password) throws Exception {
		byte[] encryptedData = encryptFileByPrivateKey(filePath, keyStorePath, alias, password);
		return Base64Utils.encode(encryptedData);
	}

	/**
	 * <p>
	 * 私钥解密
	 * </p>
	 *
	 * @param encryptedData
	 *            已加密数据
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] encryptedData, String keyStorePath, String alias, String password)
			throws Exception {
		// 取得私钥
		PrivateKey privateKey = getPrivateKey(keyStorePath, alias, password);
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 解密byte数组最大长度限制: 128
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * <p>
	 * 公钥加密
	 * </p>
	 *
	 * @param data
	 *            源数据
	 * @param certificatePath
	 *            证书存储路径
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String certificatePath) throws Exception {
		// 取得公钥
		PublicKey publicKey = getPublicKey(certificatePath);
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/**
	 * <p>
	 * 公钥解密
	 * </p>
	 *
	 * @param encryptedData
	 *            已加密数据
	 * @param certificatePath
	 *            证书存储路径
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] encryptedData, String certificatePath) throws Exception {
		PublicKey publicKey = getPublicKey(certificatePath);
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * <p>
	 * 文件解密
	 * </p>
	 *
	 * @param srcFilePath
	 *            源文件
	 * @param destFilePath
	 *            目标文件
	 * @param certificatePath
	 *            证书存储路径
	 * @throws Exception
	 */
	public static void decryptFileByPublicKey(String srcFilePath, String destFilePath, String certificatePath)
			throws Exception {
		PublicKey publicKey = getPublicKey(certificatePath);
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		File srcFile = new File(srcFilePath);
		FileInputStream in = new FileInputStream(srcFile);
		File destFile = new File(destFilePath);
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		destFile.createNewFile();
		OutputStream out = new FileOutputStream(destFile);
		byte[] data = new byte[MAX_DECRYPT_BLOCK];
		byte[] decryptedData; // 解密块
		while (in.read(data) != -1) {
			decryptedData = cipher.doFinal(data);
			out.write(decryptedData, 0, decryptedData.length);
			out.flush();
		}
		out.close();
		in.close();
	}

	/**
	 * <p>
	 * 生成数据签名
	 * </p>
	 *
	 * @param data
	 *            源数据
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	public static byte[] sign(byte[] data, String keyStorePath, String alias, String password) throws Exception {
		// 获得证书
		X509Certificate x509Certificate = (X509Certificate) getCertificate(keyStorePath, alias, password);
		// 获取私钥
		KeyStore keyStore = getKeyStore(keyStorePath, password);
		// 取得私钥
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
		// 构建签名
		Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}

	/**
	 * <p>
	 * 生成数据签名并以BASE64编码
	 * </p>
	 *
	 * @param data
	 *            源数据
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	public static String signToBase64(byte[] data, String keyStorePath, String alias, String password)
			throws Exception {
		return Base64Utils.encode(sign(data, keyStorePath, alias, password));
	}

	/**
	 * <p>
	 * 生成文件数据签名(BASE64)
	 * </p>
	 * <p>
	 * 需要先将文件私钥加密，再根据加密后的数据生成签名(BASE64)，适用于小文件
	 * </p>
	 *
	 * @param filePath
	 *            源文件
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	public static String signFileToBase64WithEncrypt(String filePath, String keyStorePath, String alias,
													 String password) throws Exception {
		byte[] encryptedData = encryptFileByPrivateKey(filePath, keyStorePath, alias, password);
		return signToBase64(encryptedData, keyStorePath, alias, password);
	}

	/**
	 * <p>
	 * 生成文件签名
	 * </p>
	 * <p>
	 * 注意：<br>
	 * 方法中使用了FileChannel，其巨大Bug就是不会释放文件句柄，导致签名的文件无法操作(移动或删除等)<br>
	 * 该方法已被generateFileSign取代
	 * </p>
	 *
	 * @param filePath
	 *            文件路径
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static byte[] signFile(String filePath, String keyStorePath, String alias, String password)
			throws Exception {
		byte[] sign = new byte[0];
		// 获得证书
		X509Certificate x509Certificate = (X509Certificate) getCertificate(keyStorePath, alias, password);
		// 获取私钥
		KeyStore keyStore = getKeyStore(keyStorePath, password);
		// 取得私钥
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
		// 构建签名
		Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
		signature.initSign(privateKey);
		File file = new File(filePath);
		if (file.exists()) {
			FileInputStream in = new FileInputStream(file);
			FileChannel fileChannel = in.getChannel();
			MappedByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			signature.update(byteBuffer);
			fileChannel.close();
			in.close();
			sign = signature.sign();
		}
		return sign;
	}

	/**
	 * <p>
	 * 生成文件数字签名
	 * </p>
	 *
	 * <p>
	 * <b>注意：</b><br>
	 * 生成签名时update的byte数组大小和验证签名时的大小应相同，否则验证无法通过
	 * </p>
	 *
	 * @param filePath
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static byte[] generateFileSign(String filePath, String keyStorePath, String alias, String password)
			throws Exception {
		byte[] sign = new byte[0];
		// 获得证书
		X509Certificate x509Certificate = (X509Certificate) getCertificate(keyStorePath, alias, password);
		// 获取私钥
		KeyStore keyStore = getKeyStore(keyStorePath, password);
		// 取得私钥
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
		// 构建签名
		Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
		signature.initSign(privateKey);
		File file = new File(filePath);
		if (file.exists()) {
			FileInputStream in = new FileInputStream(file);
			byte[] cache = new byte[CACHE_SIZE];
			int nRead = 0;
			while ((nRead = in.read(cache)) != -1) {
				signature.update(cache, 0, nRead);
			}
			in.close();
			sign = signature.sign();
		}
		return sign;
	}

	/**
	 * <p>
	 * 文件签名成BASE64编码字符串
	 * </p>
	 *
	 * @param filePath
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static String signFileToBase64(String filePath, String keyStorePath, String alias, String password)
			throws Exception {
		return Base64Utils.encode(generateFileSign(filePath, keyStorePath, alias, password));
	}

	/**
	 * <p>
	 * 验证签名
	 * </p>
	 *
	 * @param data
	 *            已加密数据
	 * @param sign
	 *            数据签名[BASE64]
	 * @param certificatePath
	 *            证书存储路径
	 * @return
	 * @throws Exception
	 */
	public static boolean verifySign(byte[] data, String sign, String certificatePath) throws Exception {
		// 获得证书
		X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);
		// 获得公钥
		PublicKey publicKey = x509Certificate.getPublicKey();
		// 构建签名
		Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
		signature.initVerify(publicKey);
		signature.update(data);
		return signature.verify(Base64Utils.decode(sign));
	}

	/**
	 * <p>
	 * 校验文件完整性
	 * </p>
	 * <p>
	 * 鉴于FileChannel存在的巨大Bug，该方法已停用，被validateFileSign取代
	 * </p>
	 *
	 * @param filePath
	 *            文件路径
	 * @param sign
	 *            数据签名[BASE64]
	 * @param certificatePath
	 *            证书存储路径
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static boolean verifyFileSign(String filePath, String sign, String certificatePath) throws Exception {
		boolean result = false;
		// 获得证书
		X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);
		// 获得公钥
		PublicKey publicKey = x509Certificate.getPublicKey();
		// 构建签名
		Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
		signature.initVerify(publicKey);
		File file = new File(filePath);
		if (file.exists()) {
			byte[] decodedSign = Base64Utils.decode(sign);
			FileInputStream in = new FileInputStream(file);
			FileChannel fileChannel = in.getChannel();
			MappedByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			signature.update(byteBuffer);
			in.close();
			result = signature.verify(decodedSign);
		}
		return result;
	}

	/**
	 * <p>
	 * 校验文件签名
	 * </p>
	 *
	 * @param filePath
	 * @param sign
	 * @param certificatePath
	 * @return
	 * @throws Exception
	 */
	public static boolean validateFileSign(String filePath, String sign, String certificatePath) throws Exception {
		boolean result = false;
		// 获得证书
		X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);
		// 获得公钥
		PublicKey publicKey = x509Certificate.getPublicKey();
		// 构建签名
		Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
		signature.initVerify(publicKey);
		File file = new File(filePath);
		if (file.exists()) {
			byte[] decodedSign = Base64Utils.decode(sign);
			FileInputStream in = new FileInputStream(file);
			byte[] cache = new byte[CACHE_SIZE];
			int nRead = 0;
			while ((nRead = in.read(cache)) != -1) {
				signature.update(cache, 0, nRead);
			}
			in.close();
			result = signature.verify(decodedSign);
		}
		return result;
	}

	/**
	 * <p>
	 * BASE64解码->签名校验
	 * </p>
	 *
	 * @param base64String
	 *            BASE64编码字符串
	 * @param sign
	 *            数据签名[BASE64]
	 * @param certificatePath
	 *            证书存储路径
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyBase64Sign(String base64String, String sign, String certificatePath) throws Exception {
		byte[] data = Base64Utils.decode(base64String);
		return verifySign(data, sign, certificatePath);
	}

	/**
	 * <p>
	 * BASE64解码->公钥解密-签名校验
	 * </p>
	 *
	 *
	 * @param base64String
	 *            BASE64编码字符串
	 * @param sign
	 *            数据签名[BASE64]
	 * @param certificatePath
	 *            证书存储路径
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyBase64SignWithDecrypt(String base64String, String sign, String certificatePath)
			throws Exception {
		byte[] encryptedData = Base64Utils.decode(base64String);
		byte[] data = decryptByPublicKey(encryptedData, certificatePath);
		return verifySign(data, sign, certificatePath);
	}

	/**
	 * <p>
	 * 文件公钥解密->签名校验
	 * </p>
	 *
	 * @param encryptedFilePath
	 *            加密文件路径
	 * @param sign
	 *            数字证书[BASE64]
	 * @param certificatePath
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyFileSignWithDecrypt(String encryptedFilePath, String sign, String certificatePath)
			throws Exception {
		byte[] encryptedData = fileToByte(encryptedFilePath);
		byte[] data = decryptByPublicKey(encryptedData, certificatePath);
		return verifySign(data, sign, certificatePath);
	}

	/**
	 * <p>
	 * 校验证书当前是否有效
	 * </p>
	 *
	 * @param certificate
	 *            证书
	 * @return
	 */
	public static boolean verifyCertificate(Certificate certificate) {
		return verifyCertificate(new Date(), certificate);
	}

	/**
	 * <p>
	 * 验证证书是否过期或无效
	 * </p>
	 *
	 * @param date
	 *            日期
	 * @param certificate
	 *            证书
	 * @return
	 */
	public static boolean verifyCertificate(Date date, Certificate certificate) {
		boolean isValid = true;
		try {
			X509Certificate x509Certificate = (X509Certificate) certificate;
			x509Certificate.checkValidity(date);
		} catch (Exception e) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * <p>
	 * 验证数字证书是在给定的日期是否有效
	 * </p>
	 *
	 * @param date
	 *            日期
	 * @param certificatePath
	 *            证书存储路径
	 * @return
	 */
	public static boolean verifyCertificate(Date date, String certificatePath) {
		Certificate certificate;
		try {
			certificate = getCertificate(certificatePath);
			return verifyCertificate(certificate);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <p>
	 * 验证数字证书是在给定的日期是否有效
	 * </p>
	 *
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 */
	public static boolean verifyCertificate(Date date, String keyStorePath, String alias, String password) {
		Certificate certificate;
		try {
			certificate = getCertificate(keyStorePath, alias, password);
			return verifyCertificate(certificate);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <p>
	 * 验证数字证书当前是否有效
	 * </p>
	 *
	 * @param keyStorePath
	 *            密钥库存储路径
	 * @param alias
	 *            密钥库别名
	 * @param password
	 *            密钥库密码
	 * @return
	 */
	public static boolean verifyCertificate(String keyStorePath, String alias, String password) {
		return verifyCertificate(new Date(), keyStorePath, alias, password);
	}

	/**
	 * <p>
	 * 验证数字证书当前是否有效
	 * </p>
	 *
	 * @param certificatePath
	 *            证书存储路径
	 * @return
	 */
	public static boolean verifyCertificate(String certificatePath) {
		return verifyCertificate(new Date(), certificatePath);
	}

	/**
	 * <p>
	 * 文件转换为byte数组
	 * </p>
	 *
	 * @param filePath
	 *            文件路径
	 * @return
	 * @throws Exception
	 */
	public static byte[] fileToByte(String filePath) throws Exception {
		byte[] data = new byte[0];
		File file = new File(filePath);
		if (file.exists()) {
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
			byte[] cache = new byte[CACHE_SIZE];
			int nRead = 0;
			while ((nRead = in.read(cache)) != -1) {
				out.write(cache, 0, nRead);
				out.flush();
			}
			out.close();
			in.close();
			data = out.toByteArray();
		}
		return data;
	}

	/**
	 * <p>
	 * 二进制数据写文件
	 * </p>
	 *
	 * @param bytes
	 *            二进制数据
	 * @param filePath
	 *            文件生成目录
	 */
	public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
		InputStream in = new ByteArrayInputStream(bytes);
		File destFile = new File(filePath);
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		destFile.createNewFile();
		OutputStream out = new FileOutputStream(destFile);
		byte[] cache = new byte[CACHE_SIZE];
		int nRead = 0;
		while ((nRead = in.read(cache)) != -1) {
			out.write(cache, 0, nRead);
			out.flush();
		}
		out.close();
		in.close();
	}

	/**
	 * 公钥验证签名
	 *
	 * @param publicKeyStr
	 * @param content
	 * @param sign
	 * @return
	 */
	public static boolean doVerifyWithSHA1(String publicKeyStr, String content, String sign) {
		try {
			byte[] publicKeyByte = Base64.decodeBase64(publicKeyStr.getBytes("utf-8"));

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyByte);

			PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

			Signature signature = Signature.getInstance("SHA1withRSA");

			signature.initVerify(publicKey);

			signature.update(content.getBytes("utf-8"));

			return signature.verify(Base64.decodeBase64(sign));
		} catch (Exception e) {
			logger.error("RSA验签异常", e);
		}
		return false;
	}

	/**
	 * 私钥签名
	 *
	 * @param privateKeyStr
	 * @param str
	 * @return
	 */
	public static String doSign(String privateKeyStr, String str) {
		try {

			byte[] privateKeyByte = Base64.decodeBase64(privateKeyStr.getBytes("utf-8"));

			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyByte);

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

			Signature signature = Signature.getInstance("SHA1withRSA");

			signature.initSign(privateKey);

			signature.update(str.getBytes("utf-8"));

			byte[] result = signature.sign();

			return new String(Base64.encodeBase64(result), "utf-8");
		} catch (Exception e) {
			logger.error("RSA签名异常", e);
		}
		return null;
	}

	/**
	 * 解密读取RSA Private Key
	 * @param path
	 * @param aesKey
	 * @return
	 */
	public static String readRSAPrivateKey(String path, String aesKey) {
		try {
			// 加载加密的私钥文件
			InputStream pvtfile = EasyRsaUtils.class.getClassLoader().getResourceAsStream(path.substring(10));

			// 解密文件
			byte[] pvtPemBytes = EasyAesUtil.decryptFile(pvtfile, aesKey);

			// 读取文件内容
			InputStream pvtPemBytesInput = new ByteArrayInputStream(pvtPemBytes);
			byte[] pvt = readPemFile(new InputStreamReader(pvtPemBytesInput));

			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pvt);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

			return Base64.encodeBase64String(privateKey.getEncoded());
		} catch (GeneralSecurityException e) {
			logger.error("生成私钥失败", e);
			throw new RuntimeException("生成私钥失败");
		}
	}

	/**
	 * 读取RSA Public Key
	 *
	 * @param path
	 * @return
	 */
	public static String readRSAPublicKey(String path) {
		try {
			InputStream pubfile = EasyRsaUtils.class.getClassLoader().getResourceAsStream(path.substring(10));
			byte[] pubKey = readPemFile(new InputStreamReader(pubfile));

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(keySpec);

			return Base64.encodeBase64String(publicKey.getEncoded());
		} catch (Exception e) {
			logger.error("生成RSA公钥失败", e);
			throw new RuntimeException("生成RSA公钥失败");
		}
	}

	/**
	 * 读取文件
	 * @param streamReader
	 * @return
	 */
	private static byte[] readPemFile(Reader streamReader) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(streamReader);
			String s = br.readLine();
			StringBuilder sb = new StringBuilder();
			s = br.readLine();
			while (s.charAt(0) != '-') {
				sb.append(s);
				s = br.readLine();
			}
			// 编码转换，进行BASE64解码
			return Base64.decodeBase64(sb.toString());
		} catch (Exception e) {
			logger.error("生成RSA公钥失败", e);
			throw new RuntimeException("读取文件失败");
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {

			}
		}
	}

	public static String sign(PrivateKey privateKey, byte[] data) throws Exception {

		Signature signature = Signature.getInstance("SHA256withRSA", "BC");
		signature.initSign(privateKey);
		signature.update(data);
		byte[] sign = signature.sign();
		return Base64.encodeBase64String(sign);
	}

	/**
	 * 获取证书公私钥
	 *
	 * @param strPfxPath
	 * @param strPassword
	 * @return
	 */
	public static PrivateKey getPfxInfo(String strPfxPath, String strPassword) {

		// 加载加密的私钥文件
		InputStream pvtfile = EasyRsaUtils.class.getClassLoader().getResourceAsStream(strPfxPath.substring(10));

		try {
			// 实例化KeyStore对象
			KeyStore ks = KeyStore.getInstance("PKCS12");
			// If the keystore password is empty(""), then we have to set
			// to null, otherwise it won't work!!!
			char[] nPassword = null;
			if ((strPassword == null) || strPassword.trim().equals("")) {
				nPassword = null;
			} else {
				nPassword = strPassword.toCharArray();
			}
			// 加载密钥库,使用密码"password"
			ks.load(pvtfile, nPassword);
			// System.out.println("keystore type=" + ks.getType());
			// Now we loop all the aliases, we need the alias to get keys.
			// It seems that this value is the "Friendly name" field in the
			// detals tab <-- Certificate window <-- view <-- Certificate
			// Button <-- Content tab <-- Internet Options <-- Tools menu
			// In MS IE 6.
			// 列出此密钥库的所有别名
			Enumeration<String> enumas = ks.aliases();
			String keyAlias = null;
			if (enumas.hasMoreElements())// we are readin just one certificate.
			{
				keyAlias = (String) enumas.nextElement();
			}
			// 获得别名为xxx所对应的私钥
			PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
			return prikey;
		} catch (Exception e) {
			logger.error("根据路径和密码获取证书信息出错,pfxPath:[" + strPfxPath + "]", e);
			return null;
		} finally {
			try {
				pvtfile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 私钥加密
	 *
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPrivateKey(byte[] data, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] bytes = cipher.doFinal(data);
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * 根据证书地址，获得公钥
     */
	@SuppressWarnings("restriction")
	public static String getPublicKeyStr(String certPath) throws Exception {
		// 获得证书
		X509Certificate x509Certificate = (X509Certificate) getCertificate(certPath);
		// 获得公钥
		PublicKey publicKey = x509Certificate.getPublicKey();

		BASE64Encoder base64Encoder=new BASE64Encoder();
		String publicKeyString = base64Encoder.encode(publicKey.getEncoded());
		return publicKeyString;
	}

	public static void main(String[] args) {
		// System.out.println(readRSAPrivateKey("classpath:cert/custom_hz_pvt_pkcs8_cr.data").equals(postPrivateKey));
		// System.out.println(readRSAPublicKey("classpath:cert/custom_hz_pub_key.pem").equals(publicKey));
	}


}
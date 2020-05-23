package com.chunruo.portal.util;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 自定义des对称加密
 * 加密后只有数字和字母
 * @author chunruo
 *
 */
public class DesUtil {
	
	public static void main(String[] args){
		String oauthInfo="B8ADBB0E1D921C2A9DD1CD68ED2E607D00A4F62C43959E685A26402582831B39FCF1DBF8D4E2E60F1B58C24A5BC6961C4467B50636CC731620F7BB87926067876371A845E26CE4684AF87437925198478444246F8AD28E62ACF3424734FD903FC8E36C2A401223116EB4CAC56057801405A63C9D3810BAE8AA41BCBD3CC80A8DE65DC5FFC22492455F0408D0778CBD76162692CDC0643E405C57D2C44B39D68F4452402A7A3B692F90CD609F39F809558D34CBBDBCE4E685CFEB0EE5D9FD2880ABAE84FC4F6B20DD2EE6141558C82F04FAD99AC47EB0F569DF5304A7CF804139E06FA6D42D47E938F8E416794B7BB27F06DDFEBC1A2DF8EFFFEADE9D27FD75FBCE5916F8243223698704CE69D5F22F310647C7C4BF180587763F69B00FB0A372DECF80C70913CD028B320D0A85A201E074A03DD80ABEF552D366D5E2D6772DB2C51895B513CD602108FB88C65441BB6C1F49771F9990553300CB0E5F8EE4C389F055F056EC9DD4D8AAC983E505E7370DB3644261654E75B6A6749005C0FE04A7BE197A73A2AB31258661398CB750E6F690BB5F068613D01869F385E8FAD3898EDAD293F8CFCDC94F";
		String strDecrypt;
		try {
			strDecrypt = DesUtil.decrypt(oauthInfo, WeiXinPayUtil.DES_ENCRYPT_CRYPT_KEY);System.out.println(strDecrypt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

    /** 加密算法,可用 DES,DESede,Blowfish. */
    private final static String ALGORITHM = "DES";

    /**
     * DES解密算法
     * @param data
     * @param cryptKey  密钥 要是偶数
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, String cryptKey) throws Exception {
        return new String(decrypt(hex2byte(data.getBytes()),
                cryptKey.getBytes()));
    }

    /**
     * DES加密算法
     * @param data
     * @param cryptKey
     * @return
     * @throws Exception
     */
    public final static String encrypt(String data, String cryptKey)
            throws Exception {
        return byte2hex(encrypt(data.getBytes(), cryptKey.getBytes()));
    }

    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(data);
    }

    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        // 现在，获取数据并解密
        // 正式执行解密操作
        return cipher.doFinal(data);
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }
}
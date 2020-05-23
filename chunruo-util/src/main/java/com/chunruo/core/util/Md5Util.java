package com.chunruo.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.chunruo.core.util.MD5;

public class Md5Util {
	
	public static String md5Code(String str) {
		try {
			byte[] res = str.getBytes();
			MessageDigest md = MessageDigest.getInstance("MD5".toUpperCase());
			byte[] result = md.digest(res);
			for (int i = 0; i < result.length; i++) {
				md.update(result[i]);
			}
			byte[] hash = md.digest();
			StringBuffer d = new StringBuffer("");
			for (int i = 0; i < hash.length; i++) {
				int v = hash[i] & 0xFF;
				if (v < 16)
					d.append("0");
				d.append(Integer.toString(v, 16).toUpperCase() + "");
			}
			return d.toString();
		} catch (Exception e) {
			return "";
		}
	}
	
	public static String md5String(String str) {
		try {
			byte[] res = str.getBytes();
			return MD5.md5Str(res, 0, res.length);
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 生成md5校验码
	 * @param srcContent 需要加密的数据
	 * @return 加密后的md5校验码。出错则返回null。
	 */
	public static String makeMd5Sum(byte[] srcContent)
	{
		if (srcContent == null)
		{
			return null;
		}

		String strDes = null;

		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(srcContent);
			strDes = bytes2Hex(md5.digest()); // to HexString
		}
		catch (NoSuchAlgorithmException e)
		{
			return null;
		}
		return strDes;
	}

	private static String bytes2Hex(byte[] byteArray)
	{
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++)
		{
			if(byteArray[i] >= 0 && byteArray[i] < 16)
			{
				strBuf.append("0");
			}
			strBuf.append(Integer.toHexString(byteArray[i] & 0xFF));
		}
		return strBuf.toString();
	}
}

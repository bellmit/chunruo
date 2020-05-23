package com.chunruo.easypay;

import com.alibaba.fastjson.JSONObject;
import com.chunruo.core.Constants;
import com.chunruo.core.vo.EasyRequestVo;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: Will
 * @Date: 2018/12/11 17:40
 * @Description: 加签工具类
 */
public class EasySignUtils {
	private static Logger logger = LoggerFactory.getLogger(EasySignUtils.class); 
	public static String localFile = "";
	public static String merchantKeyUrl = "";
	public static String certFileIUrlStr = "";    
	public static String orderImportHeader = "";
	public static String customPassword = "";
	public static String customSupplierNo = "";
	public static String customSubSupplierNo = "";
    
    static {
    	localFile = "/Users/snail/易生支付/crossborder-demo/src/txt";
    	merchantKeyUrl = "/Users/snail/易生支付/crossborder-demo/src/cert/20190496.keystore";
    	certFileIUrlStr = "/Users/snail/易生支付/crossborder-demo/src/cert/crossborder.cer";
    	orderImportHeader = "/Users/snail/易生支付/crossborder-demo/src/cert/custom-header.csv";
    	customPassword = "222222";
    	customSupplierNo = "20190496";
    	customSubSupplierNo = "";
    }
    
    /**
     * json请求加签
     *
     * @param map
     * @return
     * @throws Exception
     */
    public static Map<String, String> sign(Map<String, String> map) throws Exception {
        String supplierNo = map.get("supplierNo");
        String randomKeyStr = map.get("randomKey");
        String signSource = EasySignUtils.generateSignSource(map) + "randomkey=" + randomKeyStr;
        String signMd5 = EasyMD5Utils.getMd5Digest(signSource.getBytes());
        
        String alias = supplierNo;
        String sign = EasyRsaUtils.signToBase64(signMd5.getBytes(), merchantKeyUrl, alias, customPassword);
        map.put("sign", sign);

        //跨境的公钥加密randomKey
        String randomKey = Base64Utils.encode(EasyRsaUtils.encryptByPublicKey(randomKeyStr.getBytes(), certFileIUrlStr));
        map.put("randomKey", randomKey);
        logger.debug("easy sign=" + StringUtil.objToJson(randomKey));
        return map;
    }

    /**
     * 文件加签
     * @param requestVO
     */
    public static boolean fileSign(EasyRequestVo requestVo) throws Exception {
    	// 加密randomKey
    	byte[] randomByte = requestVo.getRandomKey().getBytes();
        String encryptRandomKey = Base64Utils.encode(EasyRsaUtils.encryptByPublicKey(randomByte, certFileIUrlStr));
        requestVo.setRandomKey(encryptRandomKey);
    	
        // 给订单文件加密
    	String encryptFilePath = String.format("%s/%s-%s.txt", localFile, requestVo.getSupplierNo(), System.currentTimeMillis());
    	FileUtil.createNewFile(new File(encryptFilePath));
        EasyDesUtils.encryptFile(randomByte, requestVo.getFiles().getPath(), encryptFilePath);

        // 获取文件的MD5值
        File file = new File(encryptFilePath);
        requestVo.setFiles(file);
        String md5 = EasyMD5Utils.getFileMD5(file);
     
        // 给MD5加签并传输
        File keyFile = new File(merchantKeyUrl);
        if(!keyFile.exists()){  
        	//判断签名秘钥是否存在
        	logger.debug("sign签名秘钥不存在： " + merchantKeyUrl);
        	return false;
        }
        
        String alias = requestVo.getSupplierNo();
        String sign = EasyRsaUtils.signToBase64(md5.getBytes(), merchantKeyUrl, alias, customPassword);
        logger.debug("easy fileSign=" + sign);
        requestVo.setSign(sign);
        return true;
    }

    /**
     * json请求的参数排序
     * @param map
     * @return
     */
    public static String generateSignSource(Map<String, String> map) {
        Map<String, Object> newMap = new HashMap<String, Object>();
        for (String key : map.keySet()) {
            if (map.get(key) != null
                    && !map.get(key).equals("")
                    && !key.equals("sign")
                    && !key.equals("randomKey")) {
                newMap.put(key.toLowerCase(), map.get(key));
            }
        }
        
        Map<String, Object> sort = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        
        sort.putAll(newMap);
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : sort.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        return sb.toString();
    }

    /**
     * json数据返回解密
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public static String decodeResponse(JSONObject jsonObject, String supplierNo, String origin) throws Exception {
        String alias = supplierNo;
        if (!new File(merchantKeyUrl).exists()) {
        	logger.debug("私钥证书不存在： " + merchantKeyUrl);
        }

        byte[] randomKeyB = new byte[0];
        randomKeyB = EasyRsaUtils.decryptByPrivateKey(Base64Utils.decode((String) jsonObject.get("randomKey")),
        		merchantKeyUrl, alias, customPassword);

        //默认编码为utf-8
        String data = new String(EasyDesUtils.decrypt(Base64Utils.decode((String) jsonObject.get("data")), randomKeyB)); 
        return data;
    }
    
    /**
     * json数据返回解密zip数据
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public static String decodeZipResponse(JSONObject jsonObject, String supplierNo, String origin, boolean zipFlag) throws Exception {
    	String alias = supplierNo;
    	if (!new File(merchantKeyUrl).exists()) {
    		logger.debug("私钥证书不存在： " + merchantKeyUrl);
    	}
    	
    	byte[] randomKeyB = new byte[0];
    	randomKeyB = EasyRsaUtils.decryptByPrivateKey(Base64Utils.decode((String) jsonObject.get("randomKey")),
    			merchantKeyUrl, alias, customPassword);
    	
    	byte[] zipData = EasyDesUtils.decrypt(Base64Utils.decode((String) jsonObject.get("data")), randomKeyB);
    	if (zipFlag) {
			return new String(zipData, "ISO-8859-1");
		} else {
			//注意此处解压后转换编码为ISO-8859-1
			return new String(EasyZipUtils.decompressZip(zipData), "ISO-8859-1");  
		}
    }
}

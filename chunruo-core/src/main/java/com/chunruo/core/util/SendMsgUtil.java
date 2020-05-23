package com.chunruo.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;

import com.chunruo.core.Constants;
import com.chunruo.core.model.SmsSendRecord;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.SmsSendRecordManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.Md5Util;
import com.chunruo.core.util.SSLClient;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.XmlParseUtil;
import com.chunruo.core.util.XmlUtil;

public class SendMsgUtil {
	public static Log log = LogFactory.getLog(SendMsgUtil.class);
	private static HttpClient client;
	
	/**
	 *  华信短信
	 * @param mobile
	 * @param message
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean hxSendMessage(String mobile, String countryCode, String message){
		try{
			boolean isI18NMobileCode = false;
			String sendURL = Constants.conf.getProperty("SMS_URL");
			String account = Constants.conf.getProperty("SMS_ACCOUNT");
			String password = Constants.conf.getProperty("SMS_PASSWD");
			if(!StringUtil.compareObject(UserInfo.DEFUALT_COUNTRY_CODE, countryCode)){
				// 国际短信
				isI18NMobileCode = true;
				mobile = countryCode + mobile;
				message = encodeHexStr(8, message).toUpperCase();
				sendURL = Constants.conf.getProperty("SMS_I18N_URL");
				account = Constants.conf.getProperty("SMS_I18N_ACCOUNT");
				password = Md5Util.md5String(Constants.conf.getProperty("SMS_I18N_PASSWD")).toUpperCase();
			}
			
			HttpPost post = new HttpPost(sendURL);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("action","send"));
			nvps.add(new BasicNameValuePair("userid", ""));
			nvps.add(new BasicNameValuePair("account", account)); 	
			nvps.add(new BasicNameValuePair("password", password));		
			nvps.add(new BasicNameValuePair("mobile", mobile));		//多个手机号用逗号分隔
			nvps.add(new BasicNameValuePair("content", message));
			nvps.add(new BasicNameValuePair("sendTime", DateUtil.formatDate( DateUtil.DATE_TIME_PATTERN, new Date())));
			nvps.add(new BasicNameValuePair("extno", ""));
			
			// 短信发送记录
			SmsSendRecordManager smsSendRecordManager = Constants.ctx.getBean(SmsSendRecordManager.class);
			SmsSendRecord smsSendRecord = new SmsSendRecord ();
			smsSendRecord.setMobile(mobile);
			smsSendRecord.setContent(message);
			smsSendRecord.setStatus(false);
			smsSendRecord.setCreateTime(DateUtil.getCurrentDate());
			smsSendRecord.setUpdateTime(smsSendRecord.getCreateTime());
			smsSendRecord = smsSendRecordManager.save(smsSendRecord);
			
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			client = new SSLClient();
			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if(statusCode == 200){
				HttpEntity entity = response.getEntity();
				// 将字符转化为XML
				String result = EntityUtils.toString(entity, "UTF-8");
				log.info("SendMsg[mobile=" + mobile + ", result=" + result + "]");
				if(!StringUtil.isNull(result)){
					if(isI18NMobileCode){
						if(XmlUtil.isStrAvalidXML(result)){
							Map<String, Object> resultToMap = XmlParseUtil.xmlCont2Map(result);
							if(resultToMap.containsKey("returnstatus") 
									&& StringUtil.compareObject("Success".toLowerCase(), StringUtil.null2Str(resultToMap.get("returnstatus")).toLowerCase())){
								smsSendRecord.setStatus(true);
								smsSendRecord.setUpdateTime(DateUtil.getCurrentDate());
								smsSendRecordManager.save(smsSendRecord);
								return true;
							}
						}
					}else{
						JSONObject jsonObject = new JSONObject(result);
						if(jsonObject != null
								&& jsonObject.has("returnstatus")
								&& StringUtil.null2Str(jsonObject.get("returnstatus")).equalsIgnoreCase("Success")
								&& jsonObject.has("successCounts")
								&& StringUtil.compareObject(jsonObject.get("successCounts"), 1)){
							smsSendRecord.setStatus(true);
							smsSendRecord.setUpdateTime(DateUtil.getCurrentDate());
							smsSendRecordManager.save(smsSendRecord);
							return true;
						}
					}
				}
				post.releaseConnection();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	//字符编码成HEX
    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return "0x" + str;//0x表示十六进制
    }
	
    //转换十六进制编码为字符串
    public static String toStringHex(String s) {
        if ("0x".equals(s.substring(0, 2))) {
            s = s.substring(2);
        }
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            s = new String(baKeyword, "GBK");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
    
     /** *//**
     * 把字节数组转换成16进制字符串
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
         sTemp = Integer.toHexString(0xFF & bArray[i]);
         if (sTemp.length() < 2)
          sb.append("0");
         sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }  


    //字符编码成HEX
    public static String encodeHexStr(int dataCoding, String realStr) {
        String strhex = "";
        try {
             byte[] bytSource = null;
            if (dataCoding == 15) {
                bytSource = realStr.getBytes("GBK");
            } else if (dataCoding == 3) {
                bytSource = realStr.getBytes("ISO-8859-1");
            } else if (dataCoding == 8) {
                 bytSource = realStr.getBytes("UTF-16BE");
            } else {
                 bytSource = realStr.getBytes("ASCII");
            }
            strhex = bytesToHexString(bytSource);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strhex;
    }
    
    //hex编码还原成字符
    public static String decodeHexStr(int dataCoding, String hexStr) {
        String strReturn = "";
        try {
            int len = hexStr.length() / 2;
            byte[] bytSrc = new byte[len];
            for (int i = 0; i < len; i++) {
                String s = hexStr.substring(i * 2, 2);
                bytSrc[i] = Byte.parseByte(s, 512);
                Byte.parseByte(s, i);
                //bytSrc[i] = Byte.valueOf(s);
                //bytSrc[i] = Byte.Parse(s, System.Globalization.NumberStyles.AllowHexSpecifier);
            }

            if (dataCoding == 15) {
                strReturn = new String(bytSrc, "GBK");
            } else if (dataCoding == 3) {
                strReturn = new String(bytSrc, "ISO-8859-1");
            } else if (dataCoding == 8) {
                strReturn = new String(bytSrc, "UTF-16BE");
                //strReturn = Encoding.BigEndianUnicode.GetString(bytSrc);
            } else {
                strReturn = new String(bytSrc, "ASCII");
                //strReturn = System.Text.ASCIIEncoding.ASCII.GetString(bytSrc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strReturn;
    }
}

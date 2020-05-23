package com.chunruo.core.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CoreUtil {
	public static ResourceBundle resourceBundle = null;
	public static final String BUNDLE_KEY = "ApplicationResources";
	
	public static final int PUB_DIRNUM_MAX = 1000; // 发布文件夹数量最大值
	public static final String PATH_SEPARATOR = "/";
	public static final String DEPOSITORY = "depository";
	public static String DEPOSITORY_PATH = "";
	public final static String PUB_DIR_CONT = "cont";// 内容目录
	public final static String PUB_DIR_IMAGE = "image";
	public static ResourceBundle errorCodeResourceBundle = null;

	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 按日期生成文件路径
	 * @param fileName
	 * @return
	 */
	public static synchronized String dateToPath(String fileName) {
		Random rand = new Random();  
		Date currentDate = DateUtil.getCurrentDate();
		StringBuffer filePathBuffer = new StringBuffer ();
		filePathBuffer.append("/" + DateUtil.formatDate("yyyy/MM/dd", currentDate));
		filePathBuffer.append("/" + Long.toHexString(currentDate.getTime()));
		filePathBuffer.append(rand.nextInt(89) + 10);
		filePathBuffer.append(FileUtil.getSuffixByFilename(fileName));
		return filePathBuffer.toString();
	}
	
	/**
	 * 按日期生成文件路径
	 * @param fileName
	 * @return
	 */
	public static synchronized String dateToPath(String filePrefix, String fileName) {
		Random rand = new Random();  
		Date currentDate = DateUtil.getCurrentDate();
		StringBuffer filePathBuffer = new StringBuffer ();
		filePathBuffer.append(filePrefix);
		filePathBuffer.append("/" + DateUtil.formatDate("yyyy/MM/dd", currentDate));
		filePathBuffer.append("/" + Long.toHexString(currentDate.getTime()));
		filePathBuffer.append(rand.nextInt(89) + 10);
		filePathBuffer.append(FileUtil.getSuffixByFilename(fileName));
		return filePathBuffer.toString();
	}
	
	public static String idToPath(Long cont_Id) {
		int k = PUB_DIRNUM_MAX;// 1000
		return cont_Id / k / k / k % k + PATH_SEPARATOR + cont_Id / k / k % k + PATH_SEPARATOR + cont_Id / k % k;
	}

	public static String idToNamePath(Long cont_Id) {
		int k = PUB_DIRNUM_MAX;// 1000
		return cont_Id / k / k / k % k + PATH_SEPARATOR + cont_Id / k / k % k + PATH_SEPARATOR + cont_Id / k % k
				+ PATH_SEPARATOR + cont_Id % k;
	}

	/**
	 * 根据华为内容ID，转换成图片存放路径，如6100000226L，转换后为'610/0/226'
	 * 
	 * @param cont_Id
	 * @return 路径
	 */
	public static String idToImagePath(Long cont_Id) {
		return cont_Id / 10000000 + PATH_SEPARATOR + cont_Id / 10000
				% PUB_DIRNUM_MAX + PATH_SEPARATOR + cont_Id % 10000;
	}

	/**
	 * 根据华为内容ID，转换成图片存放路径，如6100000226L，转换后为'610/0/226'
	 * 
	 * @param cont_Id
	 * @return 路径
	 */
	public static String idToFullImagePath(Long cont_Id, String fileName) {
		return cont_Id / 10000000 + PATH_SEPARATOR + cont_Id / 10000
				% PUB_DIRNUM_MAX + PATH_SEPARATOR + cont_Id % 10000
				+ PATH_SEPARATOR + fileName + PATH_SEPARATOR;
	}

	public static String idToShortName(Long id) {
		int k = PUB_DIRNUM_MAX;// 1000
		return id % k + "";
	}

	public static String idToFullPathImage(Long id, String type) {
		return DEPOSITORY_PATH + PATH_SEPARATOR + PUB_DIR_IMAGE
				+ PATH_SEPARATOR + idToNamePath(id) + "." + type;
	}

	public static String idToFullPathContent(Long id, String type) {
		return DEPOSITORY_PATH + PATH_SEPARATOR + PUB_DIR_CONT + PATH_SEPARATOR
				+ idToNamePath(id) + "." + type;
	}

	public static Long nameToId(String name) {
		String nm = replaceSeparator(name);
		if (nm.startsWith("/"))
			nm = nm.substring(1);
		String[] ids = nm.split("/");
		if (ids == null || ids.length != 3)
			return null;
		int k = PUB_DIRNUM_MAX;
		return Long.valueOf(ids[0]) * k * k + Long.valueOf(ids[1]) * k
				+ Long.valueOf(ids[2]);
	}

	public static String replaceSeparator(String path) {
		return (path == null) ? null : path.replaceAll("\\\\", "/").replaceAll(
				"//", "/");
	}
	
	public static boolean isAvalidIdPath(String idPath){
		if(idPath == null || "".equals(idPath)){
			return false;
		}
		
		if (idPath.startsWith("/")) 
			idPath = idPath.substring(1);
		
		String[] arryIds = idPath.split("/");
		for(int i = 0; i < arryIds.length; i ++) {
			try{
				Long.parseLong(arryIds[i]);
			}catch(Exception e){
				return false;
			}
		}
		return true;
	}

	public static String getErrorText(String key, Object[] args) {
		if (errorCodeResourceBundle == null)
			errorCodeResourceBundle = ResourceBundle.getBundle("errorCode",
					Locale.CHINA);

		if (errorCodeResourceBundle != null) {
			try {
				String result = errorCodeResourceBundle.getString(key);
				if (result != null)
					return MessageFormat.format(result, args);
				else
					return result;
			} catch (Exception e) {
				return key;
			}
		} else {

		}
		return "";
	}

	public static String getErrorText(String key) {
		return getErrorText(key, null);
	}

	public static Map<Object, Object> transObjectPropertiesToFieldsMap(Object obj) {
		Map<Object, Object> newFields = new HashMap<Object, Object>();
		Field[] contentfields = obj.getClass().getDeclaredFields();
		AccessibleObject.setAccessible(contentfields, true);
		for (Field contentfield : contentfields) {
			try {
				if (!"serialVersionUID".equals(contentfield.getName()))
					newFields.put(contentfield.getName().toUpperCase(),
							BeanUtils.getProperty(obj, contentfield.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newFields;
	}

	public static String getContentUrl(String contUrl, Long nodeId,
			Long dataObjectId, String nodeUrlPath) {
		return nodeUrlPath + "/n" + nodeId + "d" + dataObjectId + "c" + contUrl;
	}

	/**
	 * 对字符串数组中的数据按HashCode进行冒泡排序，并返回排过序的数组
	 * 
	 * @param array
	 * @return
	 */
	public static String[] sort(String[] array) {
		String temp;
		for (int i = 0; i < array.length; ++i) {
			for (int j = 0; j < array.length - i - 1; ++j) {
				if (array[j].hashCode() > array[j + 1].hashCode()) {
					temp = array[j];
					array[j] = array[j + 1];
					array[j + 1] = temp;
				}
			}
		}
		return array;
	}

	/**
	 * 格式化数据
	 * 
	 * @param number
	 *            如：0.122323233
	 * @param pattern
	 *            如：“#0.0000”，
	 * @return 如：0.1223
	 */
	public static String formatNumber(float number, String pattern) {
		DecimalFormat df = new DecimalFormat("#0.0000");
		return df.format(number);
	}
	
	/**
	 * 格式化数据 保留小数点后2位
	 * @param price
	 * @return
	 */
	public static String formatPrice(long price) {
		DecimalFormat decimal = new DecimalFormat("#0.00");
		return decimal.format(price);
	}
	
	/**
	 * 对一个list集合随机取出size个进行随机不重复排序
	 * 
	 * @param ls
	 * @param size
	 * @return
	 */
	public static List random(List ls, int size) {
		if(ls == null ||ls.size() == 0) return null;
		int total = ls.size();
		if (total < size) {
			return ls;
		}
		List res = new ArrayList();
		Random rd = new Random();
		for (int i = 0; i < size; i++) {
			// 得到一个位置
			int r = rd.nextInt(total - i);
			// 得到那个位置的数值
			Object obj = ls.get(r);
			res.add(obj);
			// 将该位置的数字移除
			ls.remove(r);

		}
		return res;
	}
	
	//版本规范是0~999.0~999.0~999.0~999
	public static boolean checkVersion(String version) {
		if (version == null || "".equals(version) || (version.split("\\.").length != 3 && version.split("\\.").length != 4)){
			return false;
		}
		try{
			//之前的 版本都是两个点。现在改为3个点。
			if(version.split("\\.").length == 3){
				version = version+".0";
			}
			String[] numbers = version.split("\\.");
			for (int i = 0; i < numbers.length; i++) {
				Long num = Long.parseLong(numbers[i]);
				if(num > 999L || num < 0L)
					return false;
			}
		}catch(Exception e) {
			return false;
		}
		return true;
	}
	
	public static Long versionToLong(String version) {
		if(!checkVersion(version))
			return 0L;
		String[] numbers = version.split("\\.");
		return Long.parseLong(numbers[0]) * 1000000 + Long.parseLong(numbers[1]) * 1000 + Long.parseLong(numbers[2]);
	}
	
	
	/**
	 * 校验日期类型栏目
	 * yyyyMMdd
	 * @param strData
	 * @return
	 */
	public static boolean isAvalidStringData(String strData){
		if(strData == null || strData.length() != 8)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.parse(strData);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
	
	public static boolean isContains(List<Long> idList, Long value){
		if(idList == null || value == null || idList.size() == 0){
			return false;
		}
		for(Long id : idList){
			if(id.longValue() == value.longValue()){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isContains(List<String> strList, String value){
		if(strList == null || value == null || strList.size() == 0){
			return false;
		}
		for(String str : strList){
			if(StringUtil.null2Str(str).equalsIgnoreCase(StringUtil.null2Str(value))){
				return true;
			}
		}
		return false;
	}
	
	public static String getFileType(String fileName){
		String type = "";
		if ((!StringUtil.isNullStr(fileName)) 
				&& (!StringUtil.null2Str(fileName).endsWith(".")) 
				&& (StringUtil.null2Str(fileName).lastIndexOf(".") > 0)){
			type = fileName.substring(fileName.lastIndexOf("."));
		}
		return type;
	}
	
	public static String getStrToFiftyLength(String value){
		if(!StringUtil.isNullStr(value) && value.length() > 50){
			value = value.substring(0, 50);
		}
		return value;
	}
	
	public static String getText(String key, Object[] args) {
		if (CoreUtil.resourceBundle == null)
			CoreUtil.resourceBundle = ResourceBundle.getBundle(
					CoreUtil.BUNDLE_KEY, Locale.CHINA);
		if (CoreUtil.resourceBundle != null) {
			try {
				String message = StringUtil.null2Str(CoreUtil.resourceBundle.getString(key));
				if(args != null && args.length > 0){
					for(int i = 0; i < args.length; i ++){
						try{
							message = message.replace("{" + i + "}", StringUtil.null2Str(args[i]));
						}catch(Exception e){
							continue;
						}
					}
				}
				return message;
			} catch (Exception e) {
				return key;
			}
		}
		return "";
	}

	public static String getText(String key) {
		return getText(key, null);
	}
}

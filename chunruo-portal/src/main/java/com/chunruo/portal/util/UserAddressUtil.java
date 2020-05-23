package com.chunruo.portal.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import com.chunruo.cache.portal.impl.UserAddressListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.UserAddress;
import com.chunruo.core.util.Base64Util;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;

/**
 * 地址信息工具类
 * @author chunruo
 */
public class UserAddressUtil {
	
	/**
	 * 获取用户地址
	 * 并判断地址是有有效
	 * @param userId
	 * @param addressId
	 * @return
	 */
	public static MsgModel<UserAddress> checkIsValidUserAddress(UserAddress userAddress){
		MsgModel<UserAddress> msgModel = new MsgModel<UserAddress> ();
		try{
			if(userAddress == null || userAddress.getAddressId() == null){
				msgModel.setMessage("请选择有效收货地址");
				msgModel.setIsSucc(false);
				return msgModel;
			}

			// 是否无效的省市区街道
			if(userAddress.getProvinceId() == null || !Constants.AREA_MAP.containsKey(userAddress.getProvinceId())){
				msgModel.setMessage("地址-省份不存在");
				msgModel.setData(userAddress);
				msgModel.setIsSucc(false);
				return msgModel;
			}else if(userAddress.getCityId() == null || !Constants.AREA_MAP.containsKey(userAddress.getCityId())){
				msgModel.setMessage("地址-市不存在");
				msgModel.setData(userAddress);
				msgModel.setIsSucc(false);
				return msgModel;
			}else if(userAddress.getAreaId() == null || !Constants.AREA_MAP.containsKey(userAddress.getAreaId())){
				msgModel.setMessage("地址-区县不存在");
				msgModel.setData(userAddress);
				msgModel.setIsSucc(false);
				return msgModel;
			}else if(StringUtil.isNull(userAddress.getAddress())){
				msgModel.setMessage("收货地址不能为空");
				msgModel.setData(userAddress);
				msgModel.setIsSucc(false);
				return msgModel;
			}else if(StringUtil.isNull(userAddress.getMobile()) || !StringUtil.isValidateMobile(userAddress.getMobile())){
				msgModel.setMessage("收货手机号无效");
				msgModel.setData(userAddress);
				msgModel.setIsSucc(false);
				return msgModel;
			}else if(StringUtil.isNull(userAddress.getName())){
				msgModel.setMessage("收货人不能为空");
				msgModel.setData(userAddress);
				msgModel.setIsSucc(false);
				return msgModel;
			}
			
			msgModel.setData(userAddress);
			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setMessage("请选择有效收货地址");
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 获取用户的默认地址
	 * @param userId
	 * @param addressId
	 * @return
	 */
	public static UserAddress getDefualtAddress(Long userId, Long addressId){
		try{
			UserAddressListByUserIdCacheManager userAddressListByUserIdCacheManager = Constants.ctx.getBean(UserAddressListByUserIdCacheManager.class);
			Map<String, UserAddress> mapList = userAddressListByUserIdCacheManager.getSession(userId);
			if(mapList != null && mapList.size() > 0){
				if(mapList.containsKey(StringUtil.null2Str(addressId))){
					// 指定默认地址
					return mapList.get(StringUtil.null2Str(addressId));
				}else{
					UserAddress defualtUserAddress = null;
					for(Entry<String, UserAddress> entry : mapList.entrySet()){
						defualtUserAddress = entry.getValue();
						if(StringUtil.nullToBoolean(entry.getValue().getIsDefault())){
							// 默认地址
							return entry.getValue();
						}
					}
					
					// 以上条件都不满足情况下
					return defualtUserAddress;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取用户的默认地址
	 * @param userId
	 * @param addressId
	 * @return
	 */
	public static MsgModel<UserAddress> getAddressByAddressId(Long userId, Long addressId){
		MsgModel<UserAddress> msgModel = new MsgModel<UserAddress> ();
		try{
			UserAddressListByUserIdCacheManager userAddressListByUserIdCacheManager = Constants.ctx.getBean(UserAddressListByUserIdCacheManager.class);
			Map<String, UserAddress> mapList = userAddressListByUserIdCacheManager.getSession(userId);
			if(mapList != null
					&& mapList.size() > 0
					&& mapList.containsKey(StringUtil.null2Str(addressId))){
				msgModel.setIsSucc(true);
				msgModel.setMessage("请求成功");
				msgModel.setData(mapList.get(StringUtil.null2Str(addressId)));
				return msgModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		msgModel.setMessage("收货地址未找到错误");
		return msgModel;
	}
	
	/**
	 * 根据用户对象获取全用户信息
	 * @param userAddress
	 * @return
	 */
	public static String getFullAddressInfo(UserAddress userAddress){
		StringBuffer addressBuffer = new StringBuffer ();
		try{
			if(Constants.AREA_MAP.containsKey(userAddress.getProvinceId())){
				addressBuffer.append(Constants.AREA_MAP.get(userAddress.getProvinceId()).getAreaName());
				if(Constants.AREA_MAP.containsKey(userAddress.getCityId())){
					addressBuffer.append(Constants.AREA_MAP.get(userAddress.getCityId()).getAreaName());
					if(Constants.AREA_MAP.containsKey(userAddress.getAreaId())){
						addressBuffer.append(Constants.AREA_MAP.get(userAddress.getAreaId()).getAreaName());
						addressBuffer.append(StringUtil.null2Str(userAddress.getAddress()));
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return addressBuffer.toString();
	}
	
	/**
	 * 根据用户对象获取全用户信息
	 * @param userAddress
	 * @return
	 */
	public static String getFullAddressInfo(Order order){
		StringBuffer addressBuffer = new StringBuffer ();
		try{
			if(Constants.AREA_MAP.containsKey(order.getProvinceId())){
				addressBuffer.append(Constants.AREA_MAP.get(order.getProvinceId()).getAreaName());
				if(Constants.AREA_MAP.containsKey(order.getCityId())){
					addressBuffer.append(Constants.AREA_MAP.get(order.getCityId()).getAreaName());
					if(Constants.AREA_MAP.containsKey(order.getAreaId())){
						addressBuffer.append(Constants.AREA_MAP.get(order.getAreaId()).getAreaName());
						addressBuffer.append(StringUtil.null2Str(order.getAddress()));
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return addressBuffer.toString();
	}
	
	/**
	 * 检查中文名字是否包敏感词
	 * @param realName
	 * @return
	 */
	public static MsgModel<String> isContaintSensitiveWord(String realName){
		MsgModel<String> msgModel = new MsgModel<String> ();
		try{
			String sensitiveWords = StringUtil.null2Str(Constants.conf.getProperty("jkd.order.consignee.filter.words"));
			String[] wordArray = sensitiveWords.split(",");
			if(wordArray != null && wordArray.length > 0){
				for(int i = 0; i < wordArray.length; i ++){
					if(!StringUtil.isNull(wordArray[i])
							&& StringUtil.null2Str(realName).contains(StringUtil.null2Str(wordArray[i]))){
						msgModel.setIsSucc(true);
						msgModel.setData(StringUtil.null2Str(wordArray[i]));
						return msgModel;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	
	
	/**
	 * 获取身份证路径
	 * @param imagePath
	 * @return
	 */
	public static String getImagePath(String imagePath){
		if(StringUtil.isNull(imagePath)) {
			return "";
		}
		return Constants.EXTERNAL_IMAGE_PATH + imagePath;
	}
	
	/**
	 * 图片使用base64加水印
	 * @param imagePath
	 * @return
	 */
	public static String getBase64ImageData(String imagePath) {
	    ByteArrayOutputStream outputStream = null;
		try {   
			String path = UserAddressUtil.getImagePath(imagePath);
            Image srcImg = ImageIO.read(new File(path));
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);   
  
            // 得到画笔对象   
            Graphics2D g = buffImg.createGraphics();   
  
            // 设置对线段的锯齿状边缘处理   
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);   
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);   
  
            // 设置水印旋转   
            double xwidth = (double) buffImg.getWidth() / 2;
        	double xheight = (double) buffImg.getHeight() / 2;
            g.rotate(Math.toRadians(-45), xwidth, xheight);     
  
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度   
            //String iconPath = Constants.conf.getProperty("");
            String iconPath = Constants.EXTERNAL_IMAGE_PATH + "/idCard/customs.png";
            ImageIcon imgIcon = new ImageIcon(iconPath);   
            // 得到Image对象。   
            Image img = imgIcon.getImage();   
  
            float alpha = 0.5f; // 透明度   
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));   
  
            // 表示水印图片的位置   
            g.drawImage(img, 250, 200, null);   
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));   
            g.dispose();   
  
            // 生成图片  
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(buffImg, "JPG", outputStream);
            return Base64Util.encode(outputStream.toByteArray());
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            try {   
                if (outputStream != null) {   
                	outputStream.close();   
                }
            } catch (Exception e) {   
                e.printStackTrace();   
            }   
        }   
		return null;
	}
}

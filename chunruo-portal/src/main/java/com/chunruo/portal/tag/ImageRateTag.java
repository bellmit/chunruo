package com.chunruo.portal.tag;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chunruo.cache.portal.impl.ImageRateCacheManager;
import com.chunruo.cache.portal.vo.ImageVo;
import com.chunruo.core.Constants;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.Md5Util;
import com.chunruo.core.util.StringUtil;

/**
 * 图片转码
 * @author chunruo
 *
 */
public class ImageRateTag {
	private static final Log log = LogFactory.getLog(ImageRateTag.class);
	public static final String fileType = ".jpg";
	public static List<String> fileTypeList = new ArrayList<String> ();
	static{
		fileTypeList.add(".jpg");
		fileTypeList.add(".png");
		fileTypeList.add(".jpeg");
		fileTypeList.add(".gif");
	}
	
	/**
	 * 图片转码
	 * @param filePath
	 * @param rate
	 * @return
	 */
	public static String getData(String filePath, final String rate){
		try{
			ImageVo imageVo = ImageRateTag.getImageRate(filePath, rate);
			if(imageVo != null && !StringUtil.isNull(imageVo.getFilePath())){
				return StringUtil.null2Str(imageVo.getFilePath());
			}
		}catch(Exception e){
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		return "";
	}
	
	/**
	 * 图片转码
	 * @param filePath
	 * @param rate
	 * @return
	 */
	public static ImageVo getImageRate( String filePath, final String rate){
		ImageRateCacheManager imageRateCacheManager = Constants.ctx.getBean(ImageRateCacheManager.class);
		try{
			if (!StringUtil.isNull(filePath)
					&& (filePath.startsWith("http://") || filePath.startsWith("https://"))) {
				//已有完整路径图片处理
                ImageVo imageVo = new ImageVo();
                imageVo.setFilePath(filePath);
                imageVo.setHeight(200);
                imageVo.setWidth(200);
                return imageVo;
			}
			
			filePath = CoreUtil.replaceSeparator("/" + StringUtil.null2Str(filePath));
			String md5RealFilePath = Md5Util.md5Code(filePath);
			// 缓存已存在图片信息直接返回
			ImageVo imageVo = imageRateCacheManager.getSession(md5RealFilePath);
			if(imageVo != null && !StringUtil.isNull(imageVo.getFilePath())){
				return imageVo;
			}
			
			final String fullFilePath = CoreUtil.replaceSeparator(Constants.EXTERNAL_IMAGE_PATH + filePath);
			if(StringUtil.isNumber(rate) 
					&& !StringUtil.isNullStr(filePath)
					&& filePath.indexOf(".") > 0) {
				String fileType = filePath.substring(filePath.lastIndexOf("."));
				if(fileTypeList.contains(StringUtil.null2Str(fileType).toLowerCase())){ 
					// 本地文件磁盘
					if(!StringUtil.isNullStr(fullFilePath) && FileUtil.checkFileExists(fullFilePath)){
						// 更新分辨率图片缓存
						BufferedImage sourceImg = ImageIO.read(new FileInputStream(new File(fullFilePath)));
						imageVo = new ImageVo();
						imageVo.setFilePath(Constants.conf.getProperty("request.http.url") + filePath);
						imageVo.setHeight(sourceImg.getHeight());
						imageVo.setWidth(sourceImg.getWidth());
						imageRateCacheManager.updateSession(md5RealFilePath, imageVo);
						return imageVo;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		
		// 全部异常图片返回图片地址
		ImageVo imageVo = new ImageVo();
		imageVo.setFilePath(filePath);
		return imageVo;
	}
	
	/**
	 * 图片转码
	 * @param filePath
	 * @param rate
	 * @param prefix
	 * @return
	 */
	public static String getData(String filePath, final String rate, String prefix){
		if(StringUtil.isNull(filePath)){
			return "";
		}
		if(!StringUtil.isNull(prefix) 
				&& !(filePath.startsWith("http://") || filePath.startsWith("https://"))){
			if(!filePath.startsWith(prefix)){
				filePath = prefix + filePath;
			}
		}
		return ImageRateTag.getData(filePath, rate);
	}
	
	/**
	 * 图片转码对象
	 * @param filePath
	 * @param rate
	 * @param prefix
	 * @return
	 */
	public static ImageVo getImageRate(String filePath, final String rate, String prefix){
		if(StringUtil.isNull(filePath)){
			return new ImageVo();
		}
		
		if(!StringUtil.isNull(prefix)){
			if(!filePath.startsWith(prefix)
					&& !(filePath.startsWith("http://") || filePath.startsWith("https://"))){
				filePath = prefix + filePath;
			}
		}
		return ImageRateTag.getImageRate(filePath, rate);
	}
}
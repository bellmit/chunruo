package com.chunruo.core.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.SystemUtil;


public class ImageUtil {

	/**
	 * 将原图片sourceImageFile 按照encoder格式转化为despImage目标图片
	 * 
	 * @param sourceImage
	 * @param despImage
	 * @param encoder
	 */
	public static void changeImgeEncode(File sourceImage, File despImage,
			String encoder)throws Exception {

		BufferedImage input = null;
			input = ImageIO.read(sourceImage);
			ImageIO.write(input, encoder, despImage);
			input.flush();
	}

	/**
	 *将该方法由private改为public 比例缩放图片
	 * @param sourcePath
	 * @param despPath
	 * @param height
	 * @param width
	 * @throws Exception
	 */
	private static void chageImageSize(String sourcePath, String despPath,
			String imgType, int height, int width) throws Exception {
		sourcePath = CoreUtil.replaceSeparator(sourcePath);
		double Ratio = 0.0;

		File sourceFile = new File(sourcePath);
		File despFile = new File(despPath);

		if (!despFile.isFile()){
			throw new Exception(despFile + " is not image file error in getFixedBoundIcon!====");
		}

		BufferedImage Bi = ImageIO.read(sourceFile);
		if ((Bi.getHeight() > height) || (Bi.getWidth() > width)) {
			if (Bi.getHeight() > height) {
				Ratio = (new Integer(height)).doubleValue() / Bi.getHeight();
			} else {
				Ratio = (new Integer(width)).doubleValue() / Bi.getWidth();
			}

			File ThF = new File(despPath);
			Image Itemp = Bi.getScaledInstance(width, height, Bi.SCALE_SMOOTH);
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(Ratio, Ratio), null);
			Itemp = op.filter(Bi, null);
			try {
				ImageIO.write((BufferedImage) Itemp, imgType, ThF);
			} catch (Exception ex) {
			}
		}
		return;
	}
	
	/**
	 *将该方法由private改为public 比例缩放图片
	 * @param sourcePath
	 * @param despPath
	 * @param height
	 * @param width
	 * @throws Exception
	 */
	public static String chageImageSize(InputStream inputStream, String despPath, String imgType, int height, int width) throws Exception {
		double Ratio = 0.0;
		BufferedImage Bi = ImageIO.read(inputStream);
		if ((Bi.getHeight() > height) || (Bi.getWidth() > width)) {
			if (Bi.getHeight() > height) {
				Ratio = (new Integer(height)).doubleValue() / Bi.getHeight();
			} else {
				Ratio = (new Integer(width)).doubleValue() / Bi.getWidth();
			}

			File filePath = new File(despPath);
			Image Itemp = Bi.getScaledInstance(width, height, Bi.SCALE_SMOOTH);
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(Ratio, Ratio), null);
			Itemp = op.filter(Bi, null);
			try {
				ImageIO.write((BufferedImage) Itemp, imgType, filePath);
			} catch (Exception ex) {
			}
		}else{
			// 拷贝文件
			FileUploadUtil.copyFile(inputStream, despPath);
		}
		return despPath;
	}

	/**
	 * 对图片裁剪，并把裁剪完蛋新图片保存 。
	 */
	public static void operateImage(int x, int y, int width, int height,
			String srcpath, String despPath, String imgType) {
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			is = new FileInputStream(srcpath);

			// 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader
			// 声称能够解码指定格式。 参数：formatName - 包含非正式格式名称 .
			// （例如 "jpeg" 或 "tiff"）等 。
			Iterator<ImageReader> it = ImageIO
					.getImageReadersByFormatName(imgType);
			ImageReader reader = it.next();
			iis = ImageIO.createImageInputStream(is);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			Rectangle rect = new Rectangle(x, y, width, height);
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			ImageIO.write(bi, imgType, new File(despPath));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (iis != null)
				try {
					iis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 注：2010.02.26 duguoc将该方法由private改为public 比例缩放图片
	 * 
	 * @param sourcePath
	 * @param outPutFolder
	 *            目标文件夹
	 * @param despPath
	 *            目标路径
	 * @param height
	 * @param width
	 * @throws Exception
	 */
	public static void chageImageSize(String sourcePath,
			String outputFolderPath, String despPath, String imgType,
			int height, int width) throws Exception {
		sourcePath = CoreUtil.replaceSeparator(sourcePath);
		double Ratio = 0.0;

		File sourceFile = new File(sourcePath);

		Icon ret = new ImageIcon(sourcePath);
		BufferedImage Bi = ImageIO.read(sourceFile);
		if ((Bi.getHeight() > height) || (Bi.getWidth() > width)) {
			if (Bi.getHeight() > height) {
				Ratio = (new Integer(height)).doubleValue() / Bi.getHeight();
			} else {
				Ratio = (new Integer(width)).doubleValue() / Bi.getWidth();
			}

			File ThF = new File(despPath);
			Image Itemp = Bi.getScaledInstance(width, height, Bi.SCALE_SMOOTH);
			AffineTransformOp op = new AffineTransformOp(AffineTransform
					.getScaleInstance(Ratio, Ratio), null);
			Itemp = op.filter(Bi, null);
			try {
				FileUtil.checkDirExists(outputFolderPath);
				ImageIO.write((BufferedImage) Itemp, imgType, ThF);
			} catch (Exception ex) {

			}
		}
		return;
	}

	/**
	 * 注：2010.02.26 duguoc将该方法由private改为public 比例缩放图片 如果原图片的高比目标图片的高要大，则以高来缩小
	 * 如果原图片的高比目标图片的高要小，则以宽来放大
	 * 
	 * @param sourcePath
	 * @param outPutFolder
	 *            目标文件夹
	 * @param despPath
	 *            目标路径
	 * @param height
	 * @param width
	 * @throws Exception
	 *             0:默认情况返回 1：成功 2：太小了 3：转换失败
	 */
	@SuppressWarnings("static-access")
	public static boolean changeImageSize(String sourcePath,
			String despPath, String imgType,
			int height, int width) throws Exception {
		try {
			sourcePath = CoreUtil.replaceSeparator(sourcePath);
			double Ratio = 0.0;

			File sourceFile = new File(sourcePath);
			BufferedImage Bi = ImageIO.read(sourceFile);
			if (Bi.getHeight() > height) {
				Ratio = (new Integer(height)).doubleValue() / Bi.getHeight();
			} else {
				Ratio = (new Integer(width)).doubleValue() / Bi.getWidth();
			}

			File despFile = new File(despPath);
			Image Itemp = Bi.getScaledInstance(width, height, Bi.SCALE_REPLICATE);
			AffineTransformOp op = new AffineTransformOp(AffineTransform
					.getScaleInstance(Ratio, Ratio), null);
			Itemp = op.filter(Bi, null);
			
			FileUtil.checkDirExists(despFile.getParent());
			boolean isSuc = ImageIO.write((BufferedImage) Itemp, imgType, despFile);
			return isSuc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public static BufferedImage gray(BufferedImage bi) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		bi = op.filter(bi, null);
		return bi;
	}
	
	/**
	 * 使用ImageMagick本地工具等比缩放图片、如果默认图片比实际小则不处理
	 *
	 * @param magickPath ImageMagick安装路基 如:/usr/bin/convert
	 * @param sourcePat
	 * @param despPath 目标路径
	 * @param height
	 * @param width
	 * @throws Exception  0:默认情况返回 1：成功 2：太小了 3：转换失败
	 */
	public static boolean changeImageSizeByImageMagick(String imageMagick, String sourcePath, String despPath, String rate){
		String[] paretm = new String[]{imageMagick, sourcePath, despPath, rate};
		int exitVal = 0;
		try {
			//convert source.jpg -resize x60 result_60.jpg
			exitVal = SystemUtil.exec(paretm);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (exitVal == 200) ? true : false;
	}
	
	/**  
     * 给图片添加水印、可设置水印图片旋转角度  
     * @param iconPath 水印图片路径  
     * @param srcImgPath 源图片路径  
     * @param targerPath 目标图片路径  
     * @param degree 水印图片旋转角度  
     */  
    public static void markImageByIcon(String iconPath, String srcImgPath,   
            String targerPath, Integer degree) {   
        OutputStream os = null;   
        try {   
            Image srcImg = ImageIO.read(new File(srcImgPath));
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),   
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);   
  
            // 得到画笔对象   
            Graphics2D g = buffImg.createGraphics();   
  
            // 设置对线段的锯齿状边缘处理   
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);   
  
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg   
                    .getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);   
  
            if (null != degree) {   
                // 设置水印旋转   
            	double xwidth = (double) buffImg.getWidth() / 2;
            	double xheight = (double) buffImg.getHeight() / 2;
                g.rotate(Math.toRadians(degree), xwidth, xheight);   
            }   
  
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度   
            ImageIcon imgIcon = new ImageIcon(iconPath);   
            // 得到Image对象。   
            Image img = imgIcon.getImage();   
  
            float alpha = 0.5f; // 透明度   
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));   
  
            // 表示水印图片的位置   
            g.drawImage(img, 250, 200, null);   
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));   
            g.dispose();   
  
            os = new FileOutputStream(targerPath);   
            // 生成图片   
            ImageIO.write(buffImg, "JPG", os);
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            try {   
                if (null != os)   
                    os.close();   
            } catch (Exception e) {   
                e.printStackTrace();   
            }   
        }   
    }   
}

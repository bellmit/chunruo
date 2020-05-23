package com.chunruo.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chunruo.core.util.FileUtil;

/**
 * 类说明: 文件操作类
 *
 */
public class FileUtil {
	private final static Log log = LogFactory.getLog(FileUtil.class);
	private final static byte[] UTF_8_BOM ={(byte) 0xEF,(byte) 0xBB,(byte) 0xBF};
	
	/**
	 * 检查文件是否存在
	 * @param filePath
	 * @return
	 */
	public static boolean isExistFile(String filePath) {
		try {
			File file = new File(filePath);
			if(file != null && file.exists()) {
				return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 文件后缀名
	 * @param filename
	 * @return
	 */
	public static String getSuffixByFilename(String filename){
		try{
			filename = StringUtil.null2Str(filename);
			return filename.substring(filename.lastIndexOf(".")).toLowerCase();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	 
	/**
	 * 删除指定文件或目录
	 * 
	 * @param file
	 * 		需要被删除的文件
	 * @return
	 * 		0成功,非0失败
	 */
	public static int deleteFile(File file) {
		if(file == null) {
			return -1;
		}
		if(file.exists()) {
			if(file.isFile()) {
				file.delete();
			}
			if(file.isDirectory()) {
				File[] subFiles = file.listFiles();
				if(subFiles != null) {
					for(int i=0;i<subFiles.length;i++) {
						deleteFile(subFiles[i]);
					}
				}
				file.delete();
			}
			return 0;
		}else {
			return -1;
		}
	}

	/**
	 * 文件拷贝
	 * @param src
	 * @param tar
	 */
	public static void fileCopy(File srcFile, File tarFile) throws Exception{
		FileInputStream is = null;
		FileOutputStream os = null;
		try {
			is = new FileInputStream(srcFile);
			os = new FileOutputStream(tarFile);
			// write the file to the file specified
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (FileNotFoundException fnfe) {
			throw fnfe;
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				throw e;
			}
			try {
				if (os != null)
					os.close();
			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * 将字符串写入到文件中，
	 * @param filePath      文件路径+文件名
	 * @param fileContent   文件内容
	 * @param encoding      字符串编码格式 默认系统编码
	 * @throws Exception
	 */
	public static void writeFileByString(String filePath, String fileContent,
			String encoding) {
		FileUtil.writeFileByString(filePath, fileContent, encoding, false);
	}


	/**
	 * 将字符串写入到文件中，
	 * 
	 * @param filePath
	 *            文件路径+文件名
	 * @param fileContent
	 *            文件内容
	 * @param encoding
	 *            字符串编码格式 默认系统编码
	 * @param append
	 *            如果为true表示将fileContent中的内容添加到文件file末尾处
	 * @throws Exception
	 * 
	 * @author duguocheng
	 */
	public static void writeFileByString(String filePath, String fileContent,
			String encoding, boolean append) {
		PrintWriter out = null;
		try {
			if (filePath == null || fileContent == null
					|| fileContent.length() <= 0) {
				log.error("into writeFileByString [filePath=" + filePath
						+ ",filePath=" + fileContent + "] is null return!!!");
				return;
			}

			if (append) {
				File tempFile = new File(filePath);
				if (!tempFile.exists()) {
					tempFile.getParentFile().mkdirs();
					tempFile.createNewFile();
				}
			} else
				createNewFile(new File(filePath));

			if (encoding == null || encoding.trim().length() <= 0) {
				out = new PrintWriter(new FileWriter(filePath));
			} else {
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,append), encoding)), true);
			}
			out.print(fileContent);
			out.close();
		} catch (Exception e) {
			log.error("writeFileByString Exception:" + e, e);
		} finally {
			if (out != null)
				out.close();
		}
	}



	/**
	 * 将字符串写入到文件中，
	 * 
	 * @param filePath
	 *            文件路径+文件名
	 * @param fileContent
	 *            文件内容
	 * @param encoding
	 *            字符串编码格式 默认系统编码
	 * @param append
	 *            如果为true表示将fileContent中的内容添加到文件file末尾处
	 * @throws Exception
	 * 
	 * @author duguocheng
	 */
	public static void writeFileByString(String filePath, String fileContent, String encoding, boolean append,boolean isNewLine) {
		PrintWriter out = null;
		try {
			if (filePath == null || fileContent == null
					|| fileContent.length() <= 0) {
				log.error("into writeFileByString [filePath=" + filePath
						+ ",filePath=" + fileContent + "] is null return!!!");
				return;
			}

			if (append) {
				File tempFile = new File(filePath);
				if (!tempFile.exists()) {
					tempFile.getParentFile().mkdirs();
					tempFile.createNewFile();
				}
			} else
				createNewFile(new File(filePath));

			if (encoding == null || encoding.trim().length() <= 0) {
				out = new PrintWriter(new FileWriter(filePath));
			} else {
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,append), encoding)), true);
			}
			if(isNewLine){
				out.println();	
			}
			out.print(fileContent);
			out.close();
		} catch (Exception e) {
			log.error("writeFileByString Exception:" + e, e);
		} finally {
			if (out != null)
				out.close();
		}
	}


	public static String loadAFileToString(File f){     
		InputStream is = null;   
		String ret = null;   
		try {   
			is = new BufferedInputStream( new FileInputStream(f) );   
			long contentLength = f.length();   
			ByteArrayOutputStream outstream = new ByteArrayOutputStream( contentLength > 0 ? (int) contentLength : 1024);   
			byte[] buffer = new byte[4096];   
			int len;   
			while ((len = is.read(buffer)) > 0) {   
				outstream.write(buffer, 0, len);   
			}               
			outstream.close();   
			ret = new String(outstream.toByteArray(), "utf-8"); 
		}catch(IOException e){
			ret="";
		} finally {   
			if(is!=null) {try{is.close();} catch(Exception e){} }   
		}   
		return ret;           
	}  

	public static String loadAFileToString2(File f) {
		InputStream is = null;
		String ret = "";
		if (f.exists()) {
			BufferedReader br = null;
			try {
				String line;
				is = new FileInputStream(f);
				InputStreamReader read = new InputStreamReader(is, "utf-8");
				br = new BufferedReader(read);
				while ((line = br.readLine()) != null) {
					ret += line + "\r\n";
				}
			} catch (Exception e) {
				ret = "";
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return ret;
	}
	/**
	 * 创建文件，如果存在，删除后，新建
	 * @param f
	 * @throws IOException
	 */
	public static void createNewFile(File f)throws IOException{
		if(f.exists()){
			f.delete();
		}
		f.getParentFile().mkdirs();
		f.createNewFile();
	}	

	public static boolean checkFileExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}


	public static void checkDirExists(String dirPath) {
		File dirFile = new File(dirPath);
		if (!dirFile.isDirectory()) {
			dirFile.mkdirs();
		}
	}


	/**
	 * 取得文件大小单位M
	 * @param f
	 * @throws IOException
	 */
	public static double getFileSize(File f)throws IOException{
		FileInputStream fis =null;
		try {
			fis = new FileInputStream(f);
			BigDecimal b = new BigDecimal(Integer.toString(fis.available()));
			BigDecimal s = new BigDecimal("1048576");
			return b.divide(s).doubleValue();
		}catch(Exception e) {

		}finally{
			if(fis != null)
				fis.close();
		}
		return 0;
	}

	/**
	 * 判断文件是否为UTF-8格式
	 * @param f
	 * @return
	 */
	public static boolean isUTF8(File f){
		boolean isUtf8 =false;
		try {
			FileInputStream fis = new FileInputStream(f);			
			byte[] bom = new byte[3];
			fis.read(bom);
			fis.close();
			if(null != bom && bom.length>2
					&&bom[0]==UTF_8_BOM[0]
							&&bom[1]==UTF_8_BOM[1]
									&&bom[2]==UTF_8_BOM[2]){
				isUtf8= true;				
			}
		} catch (Exception e) {			
			return false;
		}	
		return isUtf8;
	}

	/**
	 * 把一个文件A追加到另一个文件B中，
	 * @param sorceFile 文件A
	 * @param despFile  文件B
	 */
	public static void writeFileByFileWithUtf(File sorceFile, File despFile) {
		long sourceLength = sorceFile.length();
		if (sourceLength == 0) {
			return;
		}

		InputStream is = null;
		FileOutputStream fos = null;

		byte[] bytes = new byte[1024 * 4];

		int numRead = 0;
		try {
			double fileSize = FileUtil.getFileSize(despFile);

			is = new FileInputStream(sorceFile);
			fos = new FileOutputStream(despFile, true);

			if (fileSize == 0) {
				byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
				fos.write(bom);
			}

			while ((numRead = is.read(bytes, 0, 1024 * 4)) >= 0) {
				if (numRead < 1024 * 4) {
					String str = new String(bytes);
					String pattern = "\\x00+$";
					Pattern p = Pattern.compile(pattern);  
					Matcher m = p.matcher(str);
					str = m.replaceAll("");
					bytes = str.getBytes();
				}

				fos.write(bytes, 0, bytes.length);
				bytes = new byte[1024 * 4];
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将字符串content的内容追加到文件filePath的最后，如果文件不存在，先创建新文件
	 * 追加文件：在构造FileOutputStream时，把第二个参数设为true
	 * 编码：UTF-8
	 * 
	 * @param filePath	文件路径+文件名
	 * @param content	内容
	 */
	public static boolean writeFileAppend(String filePath, String content) {
		boolean res = true;
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath, true), "UTF-8"));
			out.write(content);
		} catch (Exception e) {
			log.error("", e);
			res = false;
		} 
		try {
			out.close();
		} catch (IOException e) {
			log.error("", e);
		}
		return res;
	}

	/**
	 * 判断参数是不是文件头标识
	 * @param content
	 * @return
	 */
	public static boolean isFileHead(String content){
		byte[] b = content.trim().getBytes();
		if(b.length!=3) return false;
		if(b[0]!=-17) return false;
		if(b[1]!=-69) return false;
		if(b[2]!=-65) return false;
		return true;
	}


	/**
	 * 把一个文件A追加到另一个文件B中，
	 * 
	 * @param sorceFile
	 *            文件A
	 * @param despFile
	 *            文件B
	 * @throws IOException
	 */
	public static void appendTextToFile(String fileName, String text)
			throws IOException {
		if (text.length() == 0) {
			return;
		}
		File despFile = new File(fileName);
		if (!checkFileExists(fileName)) {
			File f = new File(fileName);
			createNewFile(f);
		}
		InputStream is = null;
		FileOutputStream fos = null;

		byte[] bytes = new byte[1024 * 4];

		int numRead = 0;
		try {
			double fileSize = FileUtil.getFileSize(despFile);

			is = new ByteArrayInputStream(text.getBytes());
			fos = new FileOutputStream(despFile, true);

			if (fileSize == 0) {
				byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
				fos.write(bom);
			}

			while ((numRead = is.read(bytes, 0, 1024 * 4)) >= 0) {
				if (numRead < 1024 * 4) {
					String str = new String(bytes);
					String pattern = "\\x00+$";
					Pattern p = Pattern.compile(pattern);
					Matcher m = p.matcher(str);
					str = m.replaceAll("");
					bytes = str.getBytes();
				}

				fos.write(bytes, 0, bytes.length);
				bytes = new byte[1024 * 4];
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static String loadFileToStr(File f) throws FileNotFoundException {
		InputStream is = null;
		String ret = "";
		if (f.exists()) {
			BufferedReader br = null;
			try {
				String line;
				is = new FileInputStream(f);
				InputStreamReader read = new InputStreamReader(is, "utf-8");
				br = new BufferedReader(read);
				while ((line = br.readLine()) != null) {
					ret += line + "\r\n";
					log.debug(ret);
				}
			} catch (Exception e) {
				ret = "";
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Java文件操作 获取文件扩展名
	 * @param fileName
	 * @return
	 */
	public static String getExtensionName(String fileName) {
		String suffix = "";
		if ((fileName != null) && (fileName.length() > 0)) {
			int dot = fileName.lastIndexOf('.');
			if ((dot > -1) && (dot < (fileName.length() - 1))) {
				suffix = fileName.substring(dot + 1);
			}
		}
		return suffix;
	}

	/**
	 * Java文件操作 获取不带扩展名的文件名
	 * @param fileName
	 * @return
	 */
	public static String getFileNameNoEx(String fileName) {
		String prefix = "";
		if ((fileName != null) && (fileName.length() > 0)) {
			int dot = fileName.lastIndexOf('.');
			if ((dot > -1) && (dot < (fileName.length()))) {
				prefix = fileName.substring(0, dot); 
			}
		}
		return prefix;
	}

	/**
	 * 读取文件类型
	 * @param strURL
	 * @return
	 */
	public static String getReaderImageType(String filePath){
		ImageInputStream iis = null;
		try{
			File file = new File(filePath);
			iis = ImageIO.createImageInputStream(file);
	        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
	        if (!iter.hasNext()) {
	           return null;
	        }
	        
	        ImageReader reader = iter.next();
	        return StringUtil.null2Str(reader.getFormatName()).toLowerCase();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
		return null;
	}
	
	/**
	 * 下载网络图片
	 * @param url
	 * @param rootFilePath
	 * @param savePath
	 * @return
	 * @throws Exception
	 */
	public static boolean downloadImage(String strURL, String rootFilePath, String filePath) throws Exception { 
		InputStream is = null; 
		OutputStream os = null;
		byte[] bs = new byte[1024]; 
		int len;  
		try{
			URL url = new URL(strURL);  
			URLConnection con = url.openConnection();
			con.setConnectTimeout(30*1000); 
			 
			is = con.getInputStream(); 
			File file = new File(rootFilePath + filePath);  
			if(!file.getParentFile().exists()){  
				file.getParentFile().mkdirs();  
			} 

			os = new FileOutputStream(file.getPath()); 
			while ((len = is.read(bs)) != -1) {  
				os.write(bs, 0, len);  
			}
			os.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(os != null){
				try{
					os.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(is != null){
				try{
					is.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return false;  
	}   
	
	 /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

                bos.close();
            }
        }
    }
}

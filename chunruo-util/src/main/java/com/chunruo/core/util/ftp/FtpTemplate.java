package com.chunruo.core.util.ftp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.ftp.FtpConnection;
import com.chunruo.core.util.ftp.FtpException;
import com.chunruo.core.util.ftp.FtpPoolManager;
import com.chunruo.core.util.ftp.FtpServer;
import com.chunruo.core.util.ftp.FtpTemplate;
import com.chunruo.core.util.ftp.PagingFileFilter;

public class FtpTemplate {

	public static final Log log = LogFactory.getLog(FtpTemplate.class);
	
	private static FtpConnection getConnection(FtpServer pubServer)throws FtpException {
		return FtpPoolManager.getConnection(pubServer);
	}

	/**
	 * 上传或者删除ftp服务器上的文件
	 * 
	 * @param pub	 *            ftp服务器对象
	 * @param localHomePath	 *            本地文件路径根目录
	 * @param filePaths	 *            上传文件路径集合
	 * @param queueType	 *            上传or删除 true 上传 false 删除
	 * @return
	 */
	public static String upLoadOrDelFile(FtpServer pub, String localHomePath,
			List<String[]> filePaths, boolean queueType) {
		String result = "";		
		for (String[] filePath : filePaths) {
			try {
				FtpTemplate.upLoadOrDelFile(pub, localHomePath, filePath, queueType);
			} catch (FtpException e) {
				result = result + "|" + e.getErrorCode() + ":" + e.getMessage();
			} catch (IOException e1) {
				result = result + "|" + e1.getMessage();
			}
		}
		return result;
	}
	/**
	 * 
	 * @param localHomePath
	 *            本地根路径
	 * @param filePath
	 *            文件路径(包括文件名) *
	 * @param queueType
	 *            true: uploadfile false : delfile
	 * @throws CmsException
	 */
	public static void upLoadOrDelFile(FtpServer pubServer, String localHomePath,
			String[] filePath, boolean queueType) throws FtpException,
			IOException {
		String userHomePath = pubServer.getFtpurl();
		userHomePath = CoreUtil.replaceSeparator(userHomePath);
		if (!userHomePath.startsWith("/"))
			userHomePath = "/" + userHomePath;
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = getConnection(pubServer);
			if (filePath == null || "".equals(filePath))
				throw new FtpException(FtpException.FTP_PATH_NOT_FOUND, "path is empty!");
			
			// 本地文件全路径
			String localFullPath ;
			if(filePath[0]==null||filePath.equals("")){
				localFullPath = CoreUtil.replaceSeparator(localHomePath);
			}else{
				localFullPath = CoreUtil.replaceSeparator(localHomePath + "/" + filePath[0]);
			}
		
			// 相对路径拆分
			String[] filePathArray = filePath[1].split("[/\\\\]");
			// 相对路徑
			String dir = "";
			if (filePathArray.length > 1){
				dir = CoreUtil.replaceSeparator(filePath[1].substring(0, filePath[1].length() - filePathArray[filePathArray.length - 1].length() - 1));
			}
			
			// 文件名
			String fname = filePathArray[filePathArray.length - 1];
			// ftp 当前目录
			String ftpCurPath = ftpConnection.getWorkingDirectory();
			// ftp 默认目录
			String ftpDefaultHomePath = ftpConnection.getDefaultHomePath();
			if (!ftpCurPath.equals((ftpDefaultHomePath.equals("/") ? "" : ftpDefaultHomePath) + userHomePath + dir)) {
				// 先把工作目录设定为用户目录
				if (!ftpConnection.setWorkingDirectory((ftpDefaultHomePath.equals("/") ? "" : ftpDefaultHomePath) + userHomePath)){ 
					throw new FtpException(FtpException.FTP_FTPURL_NOT_FOUND, "");
				}
				
				for (int i = 0; i < filePathArray.length - 1; i++) {
					if (!filePathArray[i].equals("")){
						ftpConnection.makeDirectory(filePathArray[i]);
					}
				}
			}
			
			if (localFullPath.contains("*")) {
				// 本地文件全路径的目录
				String fullDirPath = localFullPath.substring(0, localFullPath.length() - fname.length() - 1);
				File fullDir = new File(fullDirPath);
				File[] allFile = fullDir.listFiles(new PagingFileFilter(fname));
				if (allFile == null || allFile.length < 1) {
					if(queueType == true)
						throw new FtpException(FtpException.FTP_FILE_NOT_FOUND, localFullPath);
				} else {
					for (File file : allFile) {
						if (queueType) {
							ftpConnection.upLoadFile(file);
						} else {
							ftpConnection.deleteFile(file.getName());
						}
					}
				}
			} else {
				File file = new File(localFullPath);
				if (queueType) {
					ftpConnection.upLoadFile(file);
				} else {
					ftpConnection.deleteFile(file.getName());
				}
			}
		}catch(FtpException ex){
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		}catch(IOException ex){
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} finally {
			FtpPoolManager.freeConnection(pubServer, ftpConnection);
		}
	}
	
	/**
	 * 上传或者删除ftp服务器上的文件
	 * 
	 * @param pub	 *            ftp服务器对象
	 * @param localHomePath	 *            本地文件路径根目录
	 * @param filePaths	 *            上传文件路径集合
	 * @param queueType	 *            上传or删除 true 上传 false 删除
	 * @return
	 */
	public static String downLoadFile(FtpServer pub, List<String> filePaths, String localHomePath, List<String> reDownLoadFiles) {
		String result = "";		
		for (String filePath : filePaths) {
			try {
				FtpTemplate.downLoadFilesByPath(pub, filePath, localHomePath);
			} catch (FtpException e) {
				reDownLoadFiles.add(filePath);
				result = result + "|" + e.getErrorCode() + ":" + e.getMessage();
			} catch (IOException e1) {
				result = result + "|" + e1.getMessage();
			}
		}
		return result;
	}
	
	/**
	 * 下载服务器相对路径下的全部文件到本地路径下, 返回下载的文件路径列表(包括文件名)
	 * 
	 * @param pubServer
	 *            下载服务器
	 * @param path
	 *            下载的相对路径
	 * @param localHomePath
	 *            本地路径
	 */
	public static void downLoadFilesByPath(FtpServer pubServer, String ftpFilePath, String localHomePath) throws FtpException, IOException {
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = getConnection(pubServer);
			ftpConnection.setWorkingDirectory(ftpConnection.getDefaultHomePath());
			String userHomePath = pubServer.getFtpurl();
			if(!"/".equals(userHomePath)) {
				userHomePath = CoreUtil.replaceSeparator(userHomePath);
				if (userHomePath.startsWith("/"))
					userHomePath = userHomePath.substring(1);				
				ftpConnection.setWorkingDirectory(userHomePath);
			}
			
			// FTP文件全路径
			String ftpFullPath ;
			if(ftpFilePath == null || ftpFilePath.equals("")){
				ftpFullPath = CoreUtil.replaceSeparator(ftpConnection.getWorkingDirectory());
			}else{
				ftpFullPath = CoreUtil.replaceSeparator(ftpConnection.getWorkingDirectory() + "/" + ftpFilePath);
			}
			
			// ftp相对路径拆分
			String[] filePathArray = ftpFullPath.split("[/\\\\]");
			String fname = filePathArray[filePathArray.length - 1];
			String fullDirPath = ftpFullPath.substring(0, ftpFullPath.length() - fname.length() - 1);
			String localfullDirPath = CoreUtil.replaceSeparator(localHomePath + "/" + ftpFilePath.substring(0, ftpFilePath.length() - fname.length() - 1));
			
			if (ftpFullPath.contains("*")){
				String[] fileNames = ftpConnection.listFTPFiles(fullDirPath, new PagingFileFilter(fname));
				if (fileNames == null || fileNames.length < 1) {
					log.error("downLoadFilesByPath[fileNames == null || fileNames.length < 1]");
					throw new FtpException(FtpException.FTP_PATH_NOT_FOUND, "path is empty!");
				}
				
				// 匹配文件FTP下载
				for (String fileName : fileNames) {
					try {
						ftpConnection.downLoadFile(fullDirPath, fileName, localfullDirPath);
					} catch (FtpException ex){
						ex.printStackTrace();
						log.error(ex.getMessage());
						throw ex;
					}
				}
			}else{
				try {
					ftpConnection.downLoadFile(fullDirPath, fname, localfullDirPath);
				} catch (FtpException ex){
					ex.printStackTrace();
					log.error(ex.getMessage());
					throw ex;
				}
			}
		} catch(FtpException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		}finally {
			FtpPoolManager.freeConnection(pubServer, ftpConnection);
		}
	}
	
	/**
	 * 下载到传入localHomePath地址下面
	 * 
	 * @param pubServer
	 * @param path
	 *            路径
	 * @param name
	 *            文件名
	 * @param localPath
	 *            本地路径
	 * @param isSourceDel 
	 *            true   删除ftp上的源文件
	 *            false  不删除
	 * @throws Exception 
	 */
	public static void downloadFile(FtpServer pubServer, String path, String name, String localPath, boolean isSourceDel) throws FtpException,IOException,Exception {
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = getConnection(pubServer);
			ftpConnection.setWorkingDirectory(ftpConnection
					.getDefaultHomePath());
			String userHomePath = pubServer.getFtpurl();
			log.debug("[userHomePath="+userHomePath+"]");
			if(!"/".equals(userHomePath)){
				userHomePath = CoreUtil.replaceSeparator(userHomePath);
				if (userHomePath.startsWith("/"))
					userHomePath = userHomePath.substring(1);				
				ftpConnection.setWorkingDirectory(userHomePath);
			}
			log.debug("[WorkingDirectory="+ftpConnection.getWorkingDirectory()+"]");
			
			ftpConnection.downLoadFile(path, name, localPath);
			if(isSourceDel){
				String remoteFileName = CoreUtil.replaceSeparator(path + "/" + name);
				if (remoteFileName.startsWith("/")){
					remoteFileName = remoteFileName.substring(1);
				}
				log.debug("ftp deleteFile[remoteFileName="+remoteFileName+"] doing!");
				ftpConnection.deleteFile(remoteFileName);
				log.debug("ftp deleteFile[remoteFileName="+remoteFileName+"] success!");
			}
		}catch(FtpException ce){
			throw ce;
		}catch(IOException ie){
			throw ie;
		}catch(Exception e){
			throw e;
		}finally {
			FtpPoolManager.freeConnection(pubServer, ftpConnection);
		}
	}

	public void delFile() {

	}
	
	public static void upLoadOrDelFile(FtpServer pubServer, String localHomePath, String filePath, boolean queueType) throws FtpException,
			IOException {
		String userHomePath = pubServer.getFtpurl();
		log.debug("===ftp userHomePath : "+userHomePath);
		userHomePath = CoreUtil.replaceSeparator(userHomePath);
		if (!userHomePath.startsWith("/")){
			userHomePath = "/" + userHomePath;
		}
		
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = getConnection(pubServer);
			if (filePath == null || "".equals(filePath)){
				throw new FtpException(FtpException.FTP_PATH_NOT_FOUND, "path is empty!");
			}
			
			// 本地文件全路径
			String localFullPath ;
			if(filePath==null||filePath.equals("")){
				localFullPath = CoreUtil.replaceSeparator(localHomePath);
			}else{
				localFullPath = CoreUtil.replaceSeparator(localHomePath + "/" + filePath);
			}
			log.debug("===ftp localFullPath : "+localFullPath);
			// 相对路径拆分
			String[] filePathArray = filePath.split("[/\\\\]");
			// 相对路徑
			String dir = "";
			if (filePathArray.length > 1){
				dir = CoreUtil.replaceSeparator(filePath.substring(0, filePath.length() - filePathArray[filePathArray.length - 1].length() - 1));
			}
			
			// 文件名
			String fname = filePathArray[filePathArray.length - 1];
			// ftp 当前目录
			String ftpCurPath = ftpConnection.getWorkingDirectory();
			log.debug("===ftp ftpCurPath : "+ftpCurPath);
			// ftp 默认目录
			String ftpDefaultHomePath = ftpConnection.getDefaultHomePath();
			log.debug("===ftp ftpDefaultHomePath : "+ftpDefaultHomePath);
			if (!ftpCurPath.equals((ftpDefaultHomePath.equals("/") ? "" : ftpDefaultHomePath) + userHomePath + dir)) {
				
				// 先把工作目录设定为用户目录
				String workingDir = (ftpDefaultHomePath.equals("/") ? "" : ftpDefaultHomePath) + userHomePath;
				log.debug("=ftp=workingDir="+workingDir);
				if (!ftpConnection.setWorkingDirectory(workingDir)){ 
					throw new FtpException(FtpException.FTP_FTPURL_NOT_FOUND, "");
				}
				
				for (int i = 0; i < filePathArray.length - 1; i++) {
					if (!filePathArray[i].equals(""))
						ftpConnection.makeDirectory(filePathArray[i]);
				}
			}
			
			if (localFullPath != null) {
				File file = new File(localFullPath);
				if (queueType) {
					ftpConnection.upLoadFile(file);
				} else {
					ftpConnection.deleteFile(file.getName());
				}
			}
		}catch(FtpException ex){
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		}catch(IOException ex){
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} finally {
			FtpPoolManager.freeConnection(pubServer, ftpConnection);
		}
	}
	
	/**
	 * 列举ftp服务器指定path下的所有文件/文件夹名称
	 * @param pubServer
	 * @param path
	 * @return
	 * @throws CmsException
	 * @throws IOException
	 * @throws Exception
	 */
	public static String[] listNames(FtpServer pubServer, String path) throws FtpException,IOException,Exception {
		String[] listNames = null;
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = getConnection(pubServer);
			ftpConnection.setWorkingDirectory(ftpConnection.getDefaultHomePath());
			String userHomePath = pubServer.getFtpurl();
			log.debug("[userHomePath="+userHomePath+"]");
			if(!"/".equals(userHomePath)){
				userHomePath = CoreUtil.replaceSeparator(userHomePath);
				if (userHomePath.startsWith("/")){
					userHomePath = userHomePath.substring(1);
				}
				ftpConnection.setWorkingDirectory(userHomePath);
			}
			
			log.debug("[WorkingDirectory="+ftpConnection.getWorkingDirectory()+"]");
			if(!"/".equals(path)){
				path = CoreUtil.replaceSeparator(path);
				if (path.startsWith("/")){
					path = path.substring(1);
				}
				ftpConnection.setWorkingDirectory(path);
			}
			log.debug("[WorkingDirectory="+ftpConnection.getWorkingDirectory()+"]");
			
			listNames = ftpConnection.listNames();
		}catch(FtpException ce){
			throw ce;
		}catch(IOException ie){
			throw ie;
		}catch(Exception e){
			throw e;
		}finally {
			FtpPoolManager.freeConnection(pubServer, ftpConnection);
		}
		return listNames;
	}
	
}
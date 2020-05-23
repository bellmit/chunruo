package com.chunruo.core.util.ftp;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;

import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.ftp.FtpException;

@SuppressWarnings("unchecked")
public class FtpConnection {
	private final transient Log log = LogFactory.getLog(getClass());
	public static final int FTP_CONNECT_TIMEOUT = 20000; // FTP 连接等待超时时间(毫秒)
	public static final Long FTP_TRY_DELAY = 30000L; // FTP 连接重试延时(毫秒)
	public static final int FTP_RETRY_TIMES = 5; // FTP 连接重试次数
	public static int FTP_MAX_CONNECTION = 300;
	public static int FTP_IDLE_CONNECTION = 100;
	
	private FTPClient ftpClient = new FTPClient();
	private String ftpHomePath = "";// 记下ftp默认根目录

	public void setDefaultHomePath() {
		try {
			ftpHomePath = getWorkingDirectory();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ftpConnect error!", e);
		}
	}

	public String getDefaultHomePath() {
		return ftpHomePath;
	}

	public void connectServer(String server, int port) throws SocketException, IOException {
		ftpClient.setConnectTimeout(FTP_CONNECT_TIMEOUT);
		ftpClient.connect(server, port);
	}

	public boolean loginServer(String user, String password) throws IOException {
		return ftpClient.login(user, password);
	}

	public void close() throws IOException {
		ftpClient.disconnect();
		try {
			ftpClient.sendCommand("bye");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean setWorkingDirectory(String path) throws IOException {
		return ftpClient.changeWorkingDirectory(path);
	}

	public String getWorkingDirectory() throws IOException {
		return ftpClient.printWorkingDirectory();
	}

	public boolean makeDirectory(String dirName) throws IOException, FtpException {
		if (setWorkingDirectory(dirName)) {
			return true;
		} else {
			if (ftpClient.makeDirectory(dirName) && setWorkingDirectory(dirName))
				return true;
			else
				throw new FtpException(FtpException.FTP_NO_PERMISSION, "no permission");
		}
	}

	/**
	 * 列出path目录下的所有文件名数组
	 * 
	 * @param path目录名
	 *            ,不包括/
	 * @return 文件名数组
	 * @throws IOException
	 */
	public String[] listNames(String ftpPath) throws IOException {
		return ftpClient.listNames(ftpPath);
	}

	/**
	 * 列出当前目录下的所有文件名数组
	 * 
	 * @return 文件名数组
	 * @throws IOException
	 */
	public String[] listNames() throws IOException {
		return ftpClient.listNames();
	}

	public boolean deleteFile(String filename) throws IOException {
		boolean result = ftpClient.deleteFile(filename);
		log.debug("ftpconnection_deleteFile_success.....fileName: "
				+ ftpClient.printWorkingDirectory() + "/"
				+ filename);
		return result;
	}
	
	public String[] listFTPFiles(String ftpPath, FileFilter filter) throws IOException {
		String[] ss = ftpClient.listNames(ftpPath);
		if (ss == null) return null;
		List<String> v = new ArrayList<String> ();
		for (int i = 0 ; i < ss.length ; i++) {
		    File f = new File(ss[i]);
		    if ((filter == null) || filter.accept(f)) {
		    	v.add(f.getName());
		    }
		}
		return (String[])(v.toArray(new String[v.size()]));
	}

	// 从FTP服务器下载文件
	public void downLoadFile(String filePath, String fileName, String localPath)throws IOException, FtpException {
		String remoteFileName = CoreUtil.replaceSeparator(filePath + "/" + fileName);
		FileOutputStream fos = null;
		File file = null;
		try {
			file = new File(CoreUtil.replaceSeparator(localPath + "/" + fileName));
			FileUtil.createNewFile(file);
			fos = new FileOutputStream(file);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setBufferSize(1024);
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (ftpClient.retrieveFile(remoteFileName, fos)) {
				log.info("downloadFile is success:  " + remoteFileName);
			} else {
				fos.close();
				fos = null;
				if (file != null){
					FileUtil.deleteFile(file);
				}
				log.error("downloadFile is error! " + remoteFileName + "  is not found!");
				throw new FtpException(FtpException.FTP_FILE_NOT_FOUND, remoteFileName);
			}
		} catch(Exception e){
			e.printStackTrace();
			throw new FtpException(e.getMessage());
		} finally {
			if (fos != null)
				fos.close();
		}
	}

	// 要求工作目录已准备
	public void upLoadFile(File file) throws FtpException, IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			String destFileName = file.getName();
			String tempFileName = "temp_" + destFileName;
			// 上传本地文件到服务器上(文件名以'temp_'开头，当上传完毕后，名字改为正式名)
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (ftpClient.storeFile(tempFileName, fis)) {
				// 上传完毕后，名字改为正式名(该方法在远程有效，本地不用此方法，而用renameTo方法)
				ftpClient.rename(tempFileName, destFileName);
				log.debug("ftpconnection_uploadFile_success.....fileName: "
						+ ftpClient.printWorkingDirectory() + "/"
						+ destFileName);
			} else {
				throw new FtpException(FtpException.FTP_NO_PERMISSION, "no write permission");
			}
		} finally {
			if (fis != null)
				fis.close();
		}

	}
}


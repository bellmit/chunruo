package com.chunruo.core.util.ftp;

public class FtpException extends Exception{
	
	public static final String FTP_LOGIN_ERROR = "FTP_LOGIN_ERROR";
	public static final String FTP_CONNECT_ERROR = "FTP_CONNECT_ERROR";
	public static final String FTP_ALL_CONNECTIONS_BUSY = "FTP_ALL_CONNECTIONS_BUSY";
	public static final String FTP_NO_PERMISSION = "FTP_NO_PERMISSION";
	public static final String FTP_PATH_NOT_FOUND = "FTP_PATH_NOT_FOUND";
	public static final String FTP_FILE_NOT_FOUND = "FTP_FILE_NOT_FOUND";
	public static final String FTP_FTPURL_NOT_FOUND = "FTP_FTPURL_NOT_FOUND";
	public static final String FTP_UNDIFINE_ERROR = "FTP_UNDIFINE_ERROR";
	
	public static final String SYNC_DOWNLOADFILE_ERROR = "SYNC_DOWNLOADFILE_ERROR";
	public static final String SYNC_PARSEFILE_ERROR = "SYNC_PARSEFILE_ERROR";

	private static final long serialVersionUID = 1L;
	private String errorCode;

	
	public FtpException(String msg) {
		super(msg);
	}

	public FtpException(String errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
	}

	public FtpException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public FtpException(String errorCode, String msg, Throwable cause) {
		super(msg, cause);
		this.errorCode = errorCode;
	}


	public String getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


}

package com.chunruo.core.util.ftp;

import com.chunruo.core.util.ftp.FtpServer;

//用于存储FTPSERVER信息，本意是不入库的，可以放到缓存。
public class FtpServer {
	private String ftpurl;
	private String ftpuser;
	private String ftppasswd;
	private String ip;
	private Long port;

	public Long getPort() {
		return port;
	}

	public void setPort(Long port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getFtpurl() {
		return ftpurl;
	}

	public void setFtpurl(String ftpurl) {
		this.ftpurl = ftpurl;
	}

	public String getFtpuser() {
		return ftpuser;
	}

	public void setFtpuser(String ftpuser) {
		this.ftpuser = ftpuser;
	}

	public String getFtppasswd() {
		return ftppasswd;
	}

	public void setFtppasswd(String ftppasswd) {
		this.ftppasswd = ftppasswd;
	}
	
	@Override
	public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
    	FtpServer pojo = (FtpServer)o;
        if (ftpurl != null ? !ftpurl.equals(pojo.ftpurl) : pojo.ftpurl != null) return false;
        if (ftpuser != null ? !ftpuser.equals(pojo.ftpuser) : pojo.ftpuser != null) return false;
        if (ftppasswd != null ? !ftppasswd.equals(pojo.ftppasswd) : pojo.ftppasswd != null) return false;
        if (ip != null ? !ip.equals(pojo.ip) : pojo.ip != null) return false;
        if (port != null ? !port.equals(pojo.port) : pojo.port != null) return false;

        return true;
    }
	
	@Override
	public int hashCode() {
        int result = 0;
        result = (ftpurl != null ? ftpurl.hashCode() : 0);
        result = 31 * result + (ftpuser != null ? ftpuser.hashCode() : 0);
        result = 31 * result + (ftppasswd != null ? ftppasswd.hashCode() : 0);
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);

        return result;
    }
}

package com.chunruo.core.util.ftp;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chunruo.core.util.ftp.FtpConnection;
import com.chunruo.core.util.ftp.FtpException;


public class FtpConnectionPool {
	protected final Log log = LogFactory.getLog(getClass());
	private CopyOnWriteArrayList<FtpConnection> freeCons = new CopyOnWriteArrayList<FtpConnection>();
	private CopyOnWriteArrayList<FtpConnection> usingCons = new CopyOnWriteArrayList<FtpConnection>();

	private String host = "localhost";// FTP ip
	private int port = 21;// FTP port
	private String user = "anonymous";// FTP帐号
	private String password = "";// FTP密码

	public void freeAllConnection() {
		while (freeCons.size() > 0) {
			closeConnection(freeCons.get(0));
			freeCons.remove(0);
		}
		while (usingCons.size() > 0) {
			closeConnection(usingCons.get(0));
			usingCons.remove(0);
		}
	}

	public int freeCount() {
		return freeCons.size();
	}

	public FtpConnection getConnection() throws FtpException,IOException {
		FtpConnection con = null;
		log.info("before free using Conns size is :"+usingCons.size()+"\t free Conns size is :"+ freeCons.size());
		while (freeCons.size() > 0) {
			con = freeCons.remove(0);
			try {
				if (con.getWorkingDirectory() != null)
					break;
				else
					closeConnection(con);
			} catch (Exception e) {
				closeConnection(con);
			}
			con = null;
			continue;
		}
		if (con == null && usingCons.size() < FtpConnection.FTP_MAX_CONNECTION) {
			con = openConnection();
		}
		if (con != null) {
			usingCons.add(con);
		}

		log.info("FtpConnectionPool: ......host: " + host + " port: " + port
				+ "\tusingCons :" + usingCons.size()
				+ "\tfreeCons : " + freeCons.size());
		return con;
	}

	public synchronized void freeConnection(FtpConnection con) {
		log.info("before free using Conns size is :"+usingCons.size()+"\t free Conns size is :"+ freeCons.size());
		usingCons.remove(con);
		if (freeCons.size() < FtpConnection.FTP_IDLE_CONNECTION && con != null) {
			freeCons.add(con);
		} else {
			closeConnection(con);
		} 
		log.info("after free using Conns size is :"+usingCons.size()+"\t free Conns size is :"+ freeCons.size());
		
	}

	private FtpConnection openConnection() throws FtpException, IOException {
		log.debug("openConnection  -----------------------------" + host);
		FtpConnection con = new FtpConnection();
		con.connectServer(host, port);
		if (con.loginServer(user, password)) {
			con.setDefaultHomePath();
			return con;
		} else {
			closeConnection(con);
			throw new FtpException(FtpException.FTP_LOGIN_ERROR);
		}

	}

	private void closeConnection(FtpConnection con) {
		try {
			con.close();
			con = null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

package com.chunruo.core.util.ftp;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chunruo.core.util.ftp.FtpConnection;
import com.chunruo.core.util.ftp.FtpConnectionPool;
import com.chunruo.core.util.ftp.FtpException;
import com.chunruo.core.util.ftp.FtpPoolManager;
import com.chunruo.core.util.ftp.FtpServer;


public class FtpPoolManager {
	protected final Log log = LogFactory.getLog(FtpPoolManager.class);
	public static ConcurrentHashMap<String, FtpConnectionPool> pools = new ConcurrentHashMap<String, FtpConnectionPool>();

	public static FtpConnection getConnection(FtpServer pubServer)throws FtpException {
		FtpConnectionPool pool = getFtpConnectionPool(pubServer);
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = pool.getConnection();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FtpException(FtpException.FTP_CONNECT_ERROR);
		}
		return ftpConnection;
	}

	private static synchronized FtpConnectionPool getFtpConnectionPool(FtpServer pubServer) throws FtpException {
		FtpConnectionPool pool = null;
		pool = pools.get(pubServer.getIp());
		if (pool == null) {
			if (pubServer.getIp() != null 
					&& pubServer.getFtpuser() != null
					&& pubServer.getFtppasswd() != null) {
				pool = new FtpConnectionPool();
				pool.setHost(pubServer.getIp());
				if (pubServer.getPort() != null)
					pool.setPort(pubServer.getPort().intValue());
				pool.setUser(pubServer.getFtpuser());
				pool.setPassword(pubServer.getFtppasswd());
				pools.put(pubServer.getIp(), pool);
			} else {
				throw new FtpException(pubServer.getIp() + ":" + pubServer.getPort()+" : "+pubServer.getFtpurl() + "setting is error!");
			}
		}
		return pool;
	}

	public static void freeConnection(FtpServer pubServer,
			FtpConnection con) {
		FtpConnectionPool pool = pools.get(pubServer.getIp());
		pool.freeConnection(con);
	}

	public static void freeAllConnection() {
		for (Iterator<Map.Entry<String, FtpConnectionPool>> it = pools
				.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, FtpConnectionPool> entry = it.next();
			FtpConnectionPool pool = entry.getValue();
			pool.freeAllConnection();
			pools.remove(entry.getKey());
		}
	}

}

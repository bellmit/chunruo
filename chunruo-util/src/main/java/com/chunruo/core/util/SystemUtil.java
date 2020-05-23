package com.chunruo.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.chunruo.core.util.StreamGobbler;
import com.chunruo.core.util.SystemUtil;

public class SystemUtil {
	
	private static Logger log = Logger.getLogger(SystemUtil.class);
	
	/**
	 * 执行系统命令
	 * 
	 * @param cmds
	 * 		一个执行的命令和参数的字符串数组，数组的第一个元素是要执行的命令往后依次都是命令的参数
	 * @return     the exit value of the process. By convention, 
     *             <code>0</code> indicates normal termination.
	 * @throws IOException
	 */
	public static int exec(String[] cmds) throws IOException {
		StringBuffer sbuffer = new StringBuffer();
		if(cmds != null && cmds.length > 0){
			for(int i = 0; i < cmds.length; i ++){
				sbuffer.append(cmds[i] + " ");
			}
			System.out.println(sbuffer.toString());
		}
		
		Process process = Runtime.getRuntime().exec(cmds);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR"); 
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");

		errorGobbler.start();
		outputGobbler.start();
		
		int exitVal = -1;
		try {
			exitVal = process.waitFor();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		log.error("exitVal="+exitVal);
		return exitVal;
	}
	
	/**
	 * 执行系统命令
	 * 
	 * @param cmds
	 * 		一个执行的命令和参数的字符串数组，数组的第一个元素是要执行的命令往后依次都是命令的参数
	 * @param error
	 * 		错误输出流信息记录List
	 * @param input
	 * 		标准输出流信息记录List
	 * @return     the exit value of the process. By convention, 
     *             <code>0</code> indicates normal termination.
	 * @throws IOException
	 */
	public static int exec(String[] cmds,List<String> error,List<String> input) throws IOException {

		Process process = Runtime.getRuntime().exec(cmds);
		
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR",error); 

		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT",input);

		errorGobbler.start();
		outputGobbler.start();
		
		int exitVal = -1;
		try {
			exitVal = process.waitFor();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		log.info("exitVal="+exitVal);
		return exitVal;
	}
	
	public static long[] getJvmMemoryInfo() {
		long[] mem = new long[4];
		 Runtime rt = Runtime.getRuntime();
		 mem[0]= rt.totalMemory();
		 mem[1] = rt.freeMemory();		 
		 mem[2] = rt.maxMemory();
		 mem[3] = mem[1]-mem[0];
		 return mem;
	}
	
	public static boolean isLinux(){
		return "Linux".equalsIgnoreCase(System.getProperty("os.name")) ;
	}
	
	
	
	/**
	   * get memory by used info
	   *
	   * @return int[] result
	   * result.length==4;int[0]=MemTotal;int[1]=MemFree;int[2]=SwapTotal;int[3]=SwapFree;
	   * @throws IOException
	   * @throws InterruptedException
	   */

	public static long[] getLinuxMemInfo() throws IOException, InterruptedException {
		if(!isLinux()) return null;
		File file = new File("/proc/meminfo");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		long[] result = new long[4];
		String str = null;
		StringTokenizer token = null;
		while ((str = br.readLine()) != null) {
			token = new StringTokenizer(str);
			if (!token.hasMoreTokens())
				continue;

			str = token.nextToken();
			if (!token.hasMoreTokens())
				continue;

			if (str.equalsIgnoreCase("MemTotal:"))
				result[0] = Long.parseLong((token.nextToken()));
			else if (str.equalsIgnoreCase("MemFree:"))
				result[1] = Integer.parseInt(token.nextToken());
			else if (str.equalsIgnoreCase("SwapTotal:"))
				result[2] = Integer.parseInt(token.nextToken());
			else if (str.equalsIgnoreCase("SwapFree:"))
				result[3] = Integer.parseInt(token.nextToken());
		}

		return result;
	}

	/**
	 * get memory by used info
	 * 
	 * @return float efficiency
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static long getLinuxCpuInfo() throws IOException, InterruptedException {
		if(!isLinux()) return 0;
		File file = new File("/proc/stat");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		StringTokenizer token = new StringTokenizer(br.readLine());
		token.nextToken();
		long user1 = Long.parseLong(token.nextToken());
		long nice1 =Long.parseLong(token.nextToken());
		long sys1 = Long.parseLong(token.nextToken());
		long idle1 = Long.parseLong(token.nextToken());

		Thread.sleep(5000);

		br = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		token = new StringTokenizer(br.readLine());
		token.nextToken();
		long user2 = Long.parseLong(token.nextToken());
		long nice2 = Long.parseLong(token.nextToken());
		long sys2 = Long.parseLong(token.nextToken());
		long idle2 = Long.parseLong(token.nextToken());

		return 100*((user2 + sys2 + nice2) - (user1 + sys1 + nice1))
				/  ((user2 + nice2 + sys2 + idle2) - (user1 + nice1
						+ sys1 + idle1));
	}
	
	/**
	 * get disk info
	 * 
	 * @return float efficiency
	 * @throws IOException 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String[] getLinuxDiskInfo() throws IOException {
		//if(!isLinux()) return null;
		String[] result = new String[6];
		List<String> lines = new ArrayList<String>();
		long re = exec(new String[]{"df","-kP"},null,lines);
		for(String line :lines){
			String[] dfs = line.split("\\s+");
			if(dfs.length ==6 && "/".equals(dfs[5])){
				result[0] = dfs[0];
				result[1] = dfs[1];
				result[2] = dfs[2];
				result[3] = dfs[3];
				result[4] = dfs[4].substring(0,dfs[4].indexOf("%"));//trim %
				result[5] = dfs[5];
				break;
			}
		}
		return result;
	}
	
	/**
	 * 取得主机的ip地址列表
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String[] getLinuxHostInfo() throws IOException, InterruptedException {
		List<String> result = new ArrayList<String>();
		List<String> lines = new ArrayList<String>();
		int re = exec(new String[]{"ifconfig"},null,lines);
		for(int i=0;i<lines.size(); i++){
			String curLine = lines.get(i)==null ? "":lines.get(i).trim();
			String[] temp = null;
			//找出"inet addr:"开头的行,此行包含ip地址
			if(curLine.startsWith("inet addr:")){
				temp = curLine.split("\\s+");
				for(String ip : temp) {
					if(ip.startsWith("addr:")){
						ip = ip.substring(5).trim();
						//保存除127.0.0.1以外的所有ip
						if(ip!=null && !ip.equals("127.0.0.1")) {
							result.add(ip);
						}
					}
				}
			}
		}
		return result.toArray(new String[result.size()]);
	}
	
	/**
	 * linux shell调用
	 * @param shell 
	 * @param parms
	 * @return
	 * @throws IOException
	 */
	public static int execLinuxShell(String shell, String[] parms) throws IOException {
		StringBuffer sb = new StringBuffer("");
		sb.append(shell);
		for (int i = 0; i < parms.length; i++) {
			sb.append(" " + parms[i]);
		}
		String[] paretm = new String[]{"/bin/sh","-c",sb.toString()};
		int exitVal = -1;
		exitVal = SystemUtil.exec(paretm);
		return exitVal;
	}
	
	/**
	 * 获取cmd命令行流数据
	 * @param cmd
	 * @return
	 */
	public static String getCmdInputStream(String cmd){
		StringBuffer sb = new StringBuffer ();
		System.out.println(StringUtil.null2Str(cmd));
		if(StringUtil.isNull(cmd)){
			return null;
		}
		
		try {  
			List<String> cmds = StringUtil.strToStrList(cmd, "\\s+");
            ProcessBuilder builder = new ProcessBuilder();    
            builder.command(cmds);  
            builder.redirectErrorStream(true);  
            Process proc = builder.start();  
            BufferedReader stdout = new BufferedReader(  
                    new InputStreamReader(proc.getInputStream()));  
            String line;  
            while ((line = stdout.readLine()) != null) {  
            	sb.append(line); 
            }  
            proc.waitFor();     
            stdout.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		return sb.toString();
	}
}

class StreamGobbler extends Thread{
	InputStream is;
	String type;
	List<String> list;
	StreamGobbler(InputStream is, String type){
		this.is = is;
		this.type = type;
	}
	
	StreamGobbler(InputStream is, String type,List<String> list){
		this.is = is;
		this.type = type;
		this.list = list;
	}

	public void run(){
		try{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while((line = br.readLine())!= null) {
				if(list != null) {
					list.add(line);
				}
			}
		} catch (IOException ioe){
			ioe.printStackTrace(); 
		}
	}
}

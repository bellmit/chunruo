package com.chunruo.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.chunruo.core.util.FileFilter;

/**
 * @author kyle guo
 * 
 *        
 */
public class FileIO {

	public static void storeStringFile1(String fileName, String content) {

		File file = new File(fileName);
		FileWriter fw = null;
		PrintWriter pw = null;

		try {
			fw = new FileWriter(file);

			pw = new PrintWriter(fw);
			pw.print(content);
			pw.flush();
			fw.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				if (pw != null)
					pw.close();
				if (fw != null)
					fw.close();
			} catch (Exception ex) {

			}

		}
	}

	public static void storeStringFile(String fileName, String content) {

		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
			out.write(content);
			out.flush();
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void filesCopy(String src, String tar, Vector skip) {

		File srcFile = new File(src);
		File tarFile = new File(tar);

		if (srcFile.isDirectory()) {
			tarFile.mkdirs();
			File[] list = srcFile.listFiles(new FileFilter(""));
			for (int i = 0; i < list.length; i++) {
				filesCopy(src + "/" + list[i].getName(), tar + "/" + list[i].getName(), skip);
			}
		} else {
			if (skip != null && skip.contains(tarFile.getName())
					&& tarFile.exists()) {
				return;
			}

			tarFile.getParentFile().mkdirs();

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
				fnfe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (os != null)
						os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static void fileCopy(String src, String tar) {

		File srcFile = new File(src);
		File tarFile = new File(tar);

		if (srcFile.isDirectory() || tarFile.exists()) {
			return;
		} else {

			tarFile.getParentFile().mkdirs();

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
				fnfe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (os != null)
						os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * @author duguoc
	 * @param src  源文件名
	 * @param tar   目标文件名
	 * @param isDelete  ture：目标存在时删除     false:目标存在时不删除
	 */
	public static void fileCopy(String src, String tar,boolean isDelete){

		File srcFile = new File(src);
		File tarFile = new File(tar);

		if (srcFile.isDirectory()) {
			return;
		} else {
			if(isDelete&&tarFile.exists()){
				tarFile.delete();
			}else if(!isDelete&&tarFile.exists())return;
			tarFile.getParentFile().mkdirs();
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
				fnfe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (os != null)
						os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}


	public static void deleteFile(String filePath) {
		String path = filePath.toString();
		java.io.File delFile = new java.io.File(path);
		delFile.delete();

	}

	public static String readFile(String inFileName) {

		try {
			File inFile = new File(inFileName);
			if (!inFile.exists()) {
				return "";
			}

			StringBuffer sb = new StringBuffer();
			FileReader inFileReader = new FileReader(inFile);
			BufferedReader inBufferedReader = new BufferedReader(inFileReader);
			String strLine = "";

			while ((strLine = inBufferedReader.readLine()) != null) {
				if (strLine != null && !"".equals(strLine)) {
					strLine = strLine.replaceAll("'", "");
					sb.append(strLine);
					sb.append("\n");
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 读取一个文件有多少行
	 * 
	 * @param fileName
	 * @return
	 */
	public static String[] getLines(String fileName) {
		String str = null;
		FileReader fr = null;
		List<String> lines = new ArrayList<String>();
		try {
			fr = new FileReader(fileName);
		} catch (FileNotFoundException e) {
		}
		BufferedReader bfr = new BufferedReader(fr);
		try {
			while ((str = bfr.readLine()) != null) {
				if (str.trim().length() > 0) {
					lines.add(str);
				}
			}
		} catch (IOException e) {
		} finally {
			try {
				fr.close();
			} catch (IOException e) {
			}
		}
		String[] result = new String[lines.size()];
		lines.toArray(result);
		return result;
	}

	public static String file2String(String filePath){
		StringBuffer sb = new StringBuffer();
		InputStream inputStream = null;
		InputStreamReader inputStramReader = null;
		try {
			inputStream = new FileInputStream(new File(filePath));
			inputStramReader = new InputStreamReader(inputStream, "utf-8");
			int ch = -1;
			while ((ch = inputStramReader.read()) != -1)
				sb.append((char) ch);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStramReader != null) {
					inputStramReader.close();
					inputStramReader = null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String inputStream2String(InputStream is){
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader dis = new BufferedReader(new InputStreamReader(is,"utf-8"));   
			String str = "";                           
			while ((str = dis.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static void inputStreamToFile(InputStream is, String fileName) throws IOException{
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(is);
			out = new BufferedOutputStream(new FileOutputStream(fileName));
			int len = -1;
			byte[] b = new byte[1024];
			while((len = in.read(b)) != -1){
				out.write(b,0,len);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(in != null) {
				in.close();
			}
			if(out != null) {
				out.close();
			}
		}
	}

}

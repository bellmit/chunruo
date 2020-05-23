package com.chunruo.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class FileUploadUtil {

	public static boolean existFile(String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean createFolder(String path) {
		try {
			File folder = new File(path);
			if (!folder.exists()) {
				folder.mkdir();
			}
		} catch (Exception e) {
			System.out.println("error occur when creating folder");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean newFile(String fullName) {
		try {
			File file = new File(fullName);
			if (!file.exists()) {
				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs();
				file.createNewFile();
			}
		} catch (Exception e) {
			System.out.println("error occur when creating folder");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean newFile(String fullName, String text) {

		try {
			newFile(fullName);
			FileWriter out = new FileWriter(fullName, false);
			out.write(text);
			out.close();
		} catch (Exception e) {
			System.out.println("error occur when creating file");
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public static boolean appendFile(String fullName, String text) {

		try {
			newFile(fullName);
			FileWriter out = new FileWriter(fullName, true);
			out.write(text);
			out.close();
		} catch (Exception e) {
			System.out.println("error occur when creating file");
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public static boolean delFile(String fullName) {
		try {
			File file = new File(fullName);
			if (file.exists()) {
				return file.delete();
			} else {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void delFileOnExit(String fullName) {
		try {
			File file = new File(fullName);
			if (file.exists()) {
				file.deleteOnExit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean rename(String fileName, String distFile) {
		synchronized (fileName) {
			File oldFile = new File(fileName);
			if (oldFile.exists()) {
				try {
					oldFile.renameTo(new File(distFile));
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			} else
				return true;
		}
	}

	public static boolean removeFolder(String pathFolder,
			boolean recursiveRemove) {
		File folder = new File(pathFolder);
		if (folder.isDirectory()) {
			return removeFolder(folder, recursiveRemove);
		}
		return false;
	}

	public static boolean removeFolder(File folder, boolean removeRecursivly) {
		if (removeRecursivly) {
			for (File current : folder.listFiles()) {
				if (current.isDirectory()) {
					removeFolder(current, true);
				} else {
					current.delete();
				}
			}
		}
		return folder.delete();
	}

	public static boolean delFolder(String path) {
		return removeFolder(path, true);
	}

	public static boolean copyFile(String source, String target) {
		boolean isSuc = false;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(source);
			newFile(target);
			if (oldfile.exists()) {
				in = new FileInputStream(source);
				out = new FileOutputStream(target, false);// no append,
				// overwrite old.
				byte[] buffer = new byte[4096];
				while ((byteread = in.read(buffer)) != -1) {
					bytesum += byteread;
					out.write(buffer, 0, byteread);
				}
				isSuc = true;
			} else {
				System.err.println("File " + source + " not exists");
			}

		} catch (Exception e) {
			System.err.println("Exception occur when copying a file");
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
		return isSuc;
	}

	public static boolean copyFile(InputStream inputStream, String target) {
		boolean isSuc = false;
		FileOutputStream out = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			newFile(target);
			out = new FileOutputStream(target, false);
			byte[] buffer = new byte[4096];
			while ((byteread = inputStream.read(buffer)) != -1) {
				bytesum += byteread;
				out.write(buffer, 0, byteread);
			}
			isSuc = true;
		} catch (Exception e) {
			System.err.println("Exception occur when copying a file");
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
		return isSuc;
	}

	public static boolean copyFileNoOverWrite(String oldfile,
			String newfilepath, String newfile) {
		return copyFileNoOverWrite(oldfile, newfilepath + newfile);
	}

	public static boolean copyFileNoOverWrite(String oldfile, String newfile) {
		if (FileUtil.checkFileExists(oldfile)
				&& !FileUtil.checkFileExists(newfile)) { // 文件存在时
			return copyFile(oldfile, newfile);
		}
		return true;
	}

	public static void copyFolder(String source, String target) {

		if(target.indexOf(source) != -1){
			System.err.println("target is source's sub directory");
			return ;
		}

		try {
			File dir = new File(source);
			File[] listFiles = dir.listFiles();
			File fileSource = null;
			for (int i = 0; i < listFiles.length; i++) {
				fileSource = listFiles[i];
				if (fileSource.isDirectory()) {
					createFolder(target + "/" + fileSource.getName());
					copyFolder(fileSource.getAbsolutePath() + "/", target
							+ "/" + fileSource.getName() + "/");
				} else {
					copyFile(fileSource.getAbsolutePath(), target + "/"
							+ fileSource.getName());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean moveFile(String oldPath, String newFolderPath,
			String fileName) {
		return copyFile(oldPath, newFolderPath + fileName) && delFile(oldPath);
	}

	public static boolean moveFile(String oldPath, String newPath) {
		return copyFile(oldPath, newPath) && delFile(oldPath);
	}

	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}

	public static String getExtName(String filename) {
		int index = filename.lastIndexOf('.');
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index + 1);
		}
	}

}

package com.chunruo.easypay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("unchecked")
public class EasyZipUtils {
	   /**
     * 压缩zip格式文件
     *
     * @param targetFile  输出的文件。
     * @param sourceFiles 带压缩的文件数组。
     * @return 如果所有文件压缩成功，则返回true；如果有任何文件未成功压缩，则返回false。
     * @throws IOException 如果出错后无法删除目标文件或无法覆盖目标文件。
     */
    public static boolean compressZip(File targetFile, File... sourceFiles) throws IOException {
        ZipOutputStream zipOut;
        boolean flag;
        if (targetFile.exists() && !targetFile.delete()) {
            throw new IOException();
        }
        try {
            zipOut = new ZipOutputStream(new FileOutputStream(targetFile));
            BufferedOutputStream out = new BufferedOutputStream(zipOut);
            flag = compressZip(zipOut, out, "", sourceFiles);
            out.close();
            zipOut.close();
        } catch (IOException e) {
            targetFile.delete();
            throw new IOException(e);
        }
        return flag;
    }

    private static boolean compressZip(ZipOutputStream zipOut, BufferedOutputStream out, String filePath, File... sourceFiles)
            throws IOException {
        if (null != filePath && !"".equals(filePath)) {
            filePath += filePath.endsWith(File.separator) ? "" : File.separator;
        } else {
            filePath = "";
        }
        boolean flag = true;
        for (File file : sourceFiles) {
            if (null == file) {
                continue;
            }
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                if (null == fileList) {
                    return false;
                } else if (1 > fileList.length) {
                    zipOut.putNextEntry(new ZipEntry(filePath + file.getName() + File.separator));
                } else {
                    flag = compressZip(zipOut, out, filePath + File.separator + file.getName(), fileList) && flag; // 只要flag有一次为false，整个递归的结果都为false。
                }
            } else {
                zipOut.putNextEntry(new ZipEntry(filePath + file.getName()));
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                int bytesRead;
                while (-1 != (bytesRead = in.read())) {
                    out.write(bytesRead);
                }
                in.close();
            }
            out.flush();
        }
        return flag;
    }
    
	/**
	 * 压缩Zip
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] compressZip(byte[] data) throws IOException {
		byte[] b = null;
		
		ByteArrayOutputStream bos = null;
		ZipOutputStream zip = null;
		try {
			bos = new ByteArrayOutputStream();
			zip = new ZipOutputStream(bos);
			ZipEntry entry = new ZipEntry("zip");
			entry.setSize(data.length);
			zip.putNextEntry(entry);
			zip.write(data);
			zip.flush();
			zip.finish();
		    b = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(zip != null)
				zip.closeEntry();
				zip.close();
			if(bos != null)
				bos.close();
		}
		
		return b;
	}

	/**
	 * 解压Zip
	 * 
	 * @param data
	 * @return
	 * @throws IOException 
	 */
	public static byte[] decompressZip(byte[] data) throws IOException {
		byte[] b = null;
		
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		ZipInputStream zip = null;
		try {
			bais = new ByteArrayInputStream(data);
			zip = new ZipInputStream(bais);
			if (zip.getNextEntry() != null) {
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[1024];
				while ((len = zip.read(buf, 0, buf.length)) != -1) {
					baos.write(buf, 0, len);
				}
				baos.flush();
				b = baos.toByteArray();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if(zip != null)
				zip.close();
			if(bais != null)
				bais.close();
			if(baos != null)
				baos.close();
		}

		return b;
	}


    /**
     * 解压zip格式文件
     *
     * @param originFile zip文件。
     * @param targetDir  要解压到的目标路径。
     * @return 如果目标文件不是zip文件则返回false。
     * @throws IOException 如果发生I/O错误。
     */
    public static boolean decompressZip(File originFile, String targetDir) throws IOException {
        if (!isZipFile(originFile)) {
            return false;
        }
        if (!targetDir.endsWith(File.separator)) {
            targetDir += File.separator;
        }
        
        ZipFile zipFile = new ZipFile(originFile);
        ZipEntry zipEntry;
		Enumeration<ZipEntry> entry = (Enumeration<ZipEntry>) zipFile.entries() ;
        while (entry.hasMoreElements()) {
            zipEntry = entry.nextElement();
            String fileName = zipEntry.getName();
            File outputFile = new File(targetDir + fileName);
            if (zipEntry.isDirectory()) {
                forceMkdirs(outputFile);
                continue;
            } else if (!outputFile.getParentFile().exists()) {
                forceMkdirs(outputFile.getParent());
            }
            OutputStream outputStream = new FileOutputStream(outputFile);
            InputStream inputStream = zipFile.getInputStream(zipEntry);
            int len;
            byte[] buffer = new byte[8192];
            while (-1 != (len = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            inputStream.close();
        }
        zipFile.close();
        return true;
    }
    
    /**
     * 解压zip格式文件, 仅生成一个文件
     *
     * @param originFile zip文件。
     * @param targetDir  要解压到的目标路径。
     * @return 如果目标文件不是zip文件则返回false。
     * @throws IOException 如果发生I/O错误。
     */
    public static boolean decompressZip(File originFile, File targetFile) throws IOException {
        if (!isZipFile(originFile)) {
            return false;
        }

        ZipFile zipFile = new ZipFile(originFile);
        ZipEntry zipEntry;
        Enumeration<ZipEntry> entry = (Enumeration<ZipEntry>) zipFile.entries() ;
        if (entry.hasMoreElements()) {
            zipEntry = entry.nextElement();
            if (zipEntry.isDirectory()) {
                zipFile.close();
            	return false;
            } else if (!targetFile.getParentFile().exists()) {
                forceMkdirs(targetFile.getParent());
            }
            OutputStream outputStream = new FileOutputStream(targetFile);
            InputStream inputStream = zipFile.getInputStream(zipEntry);
            int len;
            byte[] buffer = new byte[8192];
            while (-1 != (len = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            inputStream.close();
        }
        zipFile.close();
        return true;
    }
    
    /**
     * 获取文件真实类型
     *
     * @param file 要获取类型的文件。
     * @return 文件类型枚举。
     */
    @SuppressWarnings("resource")
	public static boolean isZipFile(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        byte[] head = new byte[4];
        if (-1 == inputStream.read(head)) {
            return false;
        }
        
        inputStream.close();
        int headHex = 0;
        for (byte b : head) {
            headHex <<= 8;
            headHex |= b;
        }
        switch (headHex) {
            case 0x504B0304:
                return true ;
            default:
                return false;
        }
    }
    
    public static File forceMkdirs(File file) {
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
        return file;
    }

    public static File forceMkdirs(String pathName) {
        return forceMkdirs(new File(pathName));
    }

    public static File forceMkdirs(File parent, String child) {
        return forceMkdirs(new File(parent, child));
    }

    public static File forceMkdirs(String parent, String child) {
        return forceMkdirs(new File(parent, child));
    }
    
    /**
     * 文件转byte
     * @param filePath
     * @return
     * @throws IOException 
     */
    public static byte[] File2byte(String filePath) throws IOException {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        
        try {
            fis = new FileInputStream(new File(filePath));
            bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            buffer = bos.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        	if(bos != null)
        		bos.close();
        	if(fis != null)
        		fis.close();
        }
        return buffer;
    }

    /**
     * byte转文件
     * @param buf
     * @param filePath
     * @param fileName
     * @throws IOException 
     */
    public static void byte2File(byte[] data, String filePath, String fileName) throws IOException {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (bos != null)
                bos.close();
            if (fos != null)
                fos.close();
        }
    }
    
    public static void main(String[] args) throws IOException {
    	/*ZipUtils.decompressZip(new File("d://catalina.zip"), "d://") ;
    	System.out.println("done");*/

    	byte[] data = EasyZipUtils.File2byte("D://chunfang150-black.csv");

    	byte[] data2 = EasyZipUtils.compressZip(data);
    	data2 = EasyZipUtils.decompressZip(data2);
    	
    	EasyZipUtils.byte2File(data2, "D:", "chunfang150-black-1.csv");
	}
}


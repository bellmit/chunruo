package com.chunruo.core.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.chunruo.core.util.StringUtil;

/**
 * xls工具类
 * 
 * @author hjn
 * 
 */
@SuppressWarnings("resource")
public class XlsParserUtil {
	protected final static transient Log log = LogFactory.getLog(XlsParserUtil.class);
	
	/**
	 * xls转换成对象
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static List<String> readHeader(String filePath) throws IOException {
		return XlsParserUtil.readHeader(filePath, 0);
	}
	
	/**
	 * xls转换成对象
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static List<String> readHeader(String filePath, int startRowNum) throws IOException {
		List<String> objectList = new ArrayList<String> ();
		String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
		InputStream stream = new FileInputStream(filePath);
		Workbook wb = null;
		if (fileType.equals("xls")) {
			wb = new HSSFWorkbook(stream);
		} else if (fileType.equals("xlsx")) {
			wb = new XSSFWorkbook(stream);
		} else {
			try {
				throw new Exception("您导入的excel格式不正确");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Sheet sheet = wb.getSheetAt(0);
		if(sheet != null){
			Row row = sheet.getRow(startRowNum);
			for(Cell cell : row){
				objectList.add(getCellColumnValue(cell));
			}
		}
		return objectList;
	}
	
	
	/**
	 * xls转换成对象
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static List<Map<String,Object>> getAllRow(String filePath) throws IOException {
		List<Map<String,Object>> objectList = new ArrayList<Map<String,Object>> ();
		String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
		InputStream stream = new FileInputStream(filePath);
		Workbook wb = null;
		if (fileType.equals("xls")) {
			wb = new HSSFWorkbook(stream);
		} else if (fileType.equals("xlsx")) {
			wb = new XSSFWorkbook(stream);
		} else {
			try {
				throw new Exception("您导入的excel格式不正确");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Sheet sheet = wb.getSheetAt(0);
		if(sheet != null){
			int lastRowNum = sheet.getLastRowNum();
			if(lastRowNum >= 0) {
				Map<Integer,String> headerMap = new HashMap<Integer,String>();
				for(int i = 0 ;i <= lastRowNum;i++) {
					Row row = sheet.getRow(i);
					Map<String,Object> rowMap = new HashMap<String,Object>();
					for(Cell cell : row){
						if(i == 0) {
							headerMap.put(cell.getColumnIndex(), getCellColumnValue(cell));
						}else {
							String headerKey = headerMap.get(cell.getColumnIndex());
							rowMap.put(headerKey, getCellColumnValue(cell));
						}
					}
					objectList.add(rowMap);
				}
			}
		}
		return objectList;
	}
	
	/**
	 * xls转换成对象
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static List<Map<String, String>> read(String filePath) throws IOException {
		return XlsParserUtil.read(filePath, 0);
	}
	
	/**
	 * xls转换成对象
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static List<Map<String, String>> read(String filePath, int startRowNum) throws IOException {
		List<String> mapKeyList = new ArrayList<String> ();
		List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>> ();
		String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
		InputStream stream = new FileInputStream(filePath);
		Workbook wb = null;
		if (fileType.equals("xls")) {
			wb = new HSSFWorkbook(stream);
		} else if (fileType.equals("xlsx")) {
			wb = new XSSFWorkbook(stream);
		} else {
			try {
				throw new Exception("您导入的excel格式不正确");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Sheet sheet = wb.getSheetAt(0);
		if(sheet != null){
			for (Row row : sheet) { 
				try{
					Map<String, String> objectMap = new HashMap<String, String> ();
					for(Cell cell : row){
						if(StringUtil.compareObject(startRowNum, row.getRowNum())){
							mapKeyList.add(getCellColumnValue(cell));
						}else{
							if(mapKeyList != null && cell.getColumnIndex() <= mapKeyList.size() ){
								try{
									objectMap.put(mapKeyList.get(cell.getColumnIndex()), getCellColumnValue(cell));
								}catch(Exception e){
									continue;
								}
							}
						}
					}

					// 是否有效记录行
					if(objectMap != null && objectMap.size() > 0){
						objectMapList.add(objectMap);
					}
				}catch(Exception e){
					continue;
				}
			}
		}
		return objectMapList;
	}

	/**
	 * 对象转换成xls
	 * @param outPath
	 * @return
	 * @throws Exception
	 */
	public static boolean write(String outPath) throws Exception {
		String fileType = outPath.substring(outPath.lastIndexOf(".") + 1, outPath.length());
		log.debug(fileType);
		// 创建工作文档对象
		Workbook wb = null;
		if (fileType.equals("xls")) {
			wb = new HSSFWorkbook();
		} else if (fileType.equals("xlsx")) {
			wb = new XSSFWorkbook();
		} else {
			log.debug("您的文档格式不正确！");
			return false;
		}
		// 创建sheet对象
		Sheet sheet1 = (Sheet) wb.createSheet("sheet1");
		// 循环写入行数据
		for (int i = 0; i < 5; i++) {
			Row row = (Row) sheet1.createRow(i);
			// 循环写入列数据
			for (int j = 0; j < 8; j++) {
				Cell cell = row.createCell(j);
				cell.setCellValue("测试" + j);
			}
		}
		// 创建文件流
		OutputStream stream = new FileOutputStream(outPath);
		// 写入数据
		wb.write(stream);
		// 关闭文件流
		stream.close();
		return true;
	}
	
	/**
	 * 获取excel列值
	 * @param cell
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static String getCellColumnValue(Cell cell){
		cell.setCellType(CellType.STRING);
		return StringUtil.null2Str(cell.getRichStringCellValue());
	}
}

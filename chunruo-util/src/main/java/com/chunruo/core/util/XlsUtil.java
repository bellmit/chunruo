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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * xls工具类
 * @author hjn
 * 
 */
public class XlsUtil {
	private static int MAX_EXCEL_SIZE = 65535;

	/**
	 * xls转换成对象
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static List<Map<String, String>> read(String filePath) throws IOException {
		List<String> mapKeyList = new ArrayList<String> ();
		List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>> ();
		InputStream stream = new FileInputStream(filePath);
		HSSFWorkbook wb = new HSSFWorkbook(stream);
		for(int i = 0; i < wb.getNumberOfSheets(); i ++){
			Sheet sheet = wb.getSheetAt(i);
			if(sheet != null){
				for (Row row : sheet) {
					if(row.getRowNum() < 1){
						continue;
					}

					Map<String, String> objectMap = new HashMap<String, String> ();
					for(Cell cell : row){
						if(row.getRowNum() == 1){
							mapKeyList.add(StringUtil.null2Str(getCellColumnValue(cell)));
						}else{
							if(mapKeyList != null && cell.getColumnIndex() <= mapKeyList.size() ){
								objectMap.put(mapKeyList.get(cell.getColumnIndex()), StringUtil.null2Str(getCellColumnValue(cell)));
							}
						}
					}

					if(row.getRowNum() > 0){
						objectMapList.add(objectMap);
					}
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
	@SuppressWarnings("resource")
	public static boolean write(List<String> headerList, List<Map<String, String>> valueMapList, String outPath) throws Exception {
		// 创建工作文档对象
		HSSFWorkbook wb = new HSSFWorkbook();
		// 创建sheet对象
		HSSFSheet sheet1 = wb.createSheet("sheet1");
		// 循环写入行数据
		if(headerList != null && headerList.size() > 0){
			HSSFRow headerRow = sheet1.createRow(0);   
			for (int i = 0; i < headerList.size(); i++) {   
				HSSFCell headerCell = headerRow.createCell((short) i);    
				headerCell.setCellType(CellType.STRING);  
				headerCell.setCellValue(headerList.get(i));   
			}  

			int index = 1;
			if(valueMapList != null && valueMapList.size() > 0){
				HSSFCellStyle cellStyle2 = wb.createCellStyle();  
				HSSFDataFormat format = wb.createDataFormat();  
				cellStyle2.setDataFormat(format.getFormat("@")); 
			
				for(int i = 0; i < valueMapList.size(); i ++){
					Map<String, String> dataMap = valueMapList.get(i);

					// 循环写入列数据
					HSSFRow row =  sheet1.createRow(index++);
					for (int j = 0; j < headerList.size(); j++) {
						//创建第i个单元格     
						HSSFCell cell = row.createCell(j);   
						if(cell.getCellType() != CellType.STRING){ 
							cell.setCellType(CellType.STRING);    
						}  

						//新增的四句话，设置CELL格式为文本格式  
						cell.setCellStyle(cellStyle2); 
						cell.setCellValue(StringUtil.null2Str(dataMap.get(headerList.get(j))));   
						cell.setCellType(CellType.STRING); 
					}
				}
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
	 * 对象转换成xls
	 * @param outPath
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static boolean writeFile(List<String> headerList, List<Map<String, String>> valueMapList, String outPath) throws Exception {
		// 创建工作文档对象
		HSSFWorkbook wb = new HSSFWorkbook();
		//导出总数
		double totalSize = StringUtil.nullToDouble(valueMapList.size());
		//excel单表支持最大数据量
		double pageSize = totalSize / XlsUtil.MAX_EXCEL_SIZE;
		//sheet数量
		int sheetSize = (int) Math.ceil(pageSize);
		int currentSize = 0;
		for (int a = 0; a < sheetSize; a++) {
			// 创建sheet对象
			HSSFSheet sheet1 = wb.createSheet("sheet" + (a + 1));
			// 循环写入行数据
			if (headerList != null && headerList.size() > 0) {
				HSSFRow headerRow = sheet1.createRow(0);
				for (int i = 0; i < headerList.size(); i++) {
					HSSFCell headerCell = headerRow.createCell((short) i);
					headerCell.setCellType(CellType.STRING);
					headerCell.setCellValue(headerList.get(i));
				}

				int index = 1;
				if (valueMapList != null && valueMapList.size() > 0) {
					HSSFCellStyle cellStyle2 = wb.createCellStyle();
					HSSFDataFormat format = wb.createDataFormat();
					cellStyle2.setDataFormat(format.getFormat("@"));

					for (int i = currentSize; i < valueMapList.size(); i++) {
						if (i > 65534 * (a + 1)) {
							// 如果大于最大sheet数据，换sheet
							currentSize += XlsUtil.MAX_EXCEL_SIZE;
							break;
						}
						Map<String, String> dataMap = valueMapList.get(i);

						// 循环写入列数据
						HSSFRow row = sheet1.createRow(index++);
						for (int j = 0; j < headerList.size(); j++) {
							// 创建第i个单元格
							HSSFCell cell = row.createCell(j);
							if (cell.getCellType() != CellType.STRING) {
								cell.setCellType(CellType.STRING);
							}

							// 新增的四句话，设置CELL格式为文本格式
							cell.setCellStyle(cellStyle2);
							cell.setCellValue(StringUtil.null2Str(dataMap.get(headerList.get(j))));
							cell.setCellType(CellType.STRING);
						}
					}
				}
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
	@SuppressWarnings("incomplete-switch")
	private static String getCellColumnValue(Cell cell){
		String value = null;
		try{
			//cell.getCellType是获得cell里面保存的值的type   
			//如Cell.CELL_TYPE_STRING   
			switch(cell.getCellType()){   
				case BOOLEAN:   
					//得到Boolean对象的方法   
					value = StringUtil.null2Str(cell.getBooleanCellValue());   
					break;   
				case NUMERIC:   
					//先看是否是日期格式   
					if(DateUtil.isCellDateFormatted(cell)){   
						//读取日期格式   
						value = StringUtil.null2Str(com.chunruo.core.util.DateUtil.formatDate(com.chunruo.core.util.DateUtil.DATE_FORMAT_YEAR, cell.getDateCellValue()));
					}else{   
						//读取数字  
						value = StringUtil.null2Str(cell.getNumericCellValue());
					}   
					break;   
				case FORMULA:   
					//读取公式     
					value = StringUtil.null2Str(cell.getCellFormula());
					break;   
				case STRING:   
					//读取String    
					value = StringUtil.null2Str(cell.getRichStringCellValue());
					break;   
			}   
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
}

package com.chunruo.core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;

public class MyExcelExport {
	// 显示的导出表的标题
	private String title;
	// 导出表的列名
	private String[] rowName;

	private List<Object[]> dataList = new ArrayList<Object[]> ();

	HttpServletResponse response;

	// 构造方法，传入要导出的数据
	public MyExcelExport(String title, String[] rowName, List<Object[]> dataList2, HttpServletResponse response) {
		this.dataList = dataList2;
		this.rowName = rowName;
		this.title = title;
		this.response = response;
	}

	/**
	 * 导出数据
	 * @throws Exception
	 */
	public void export() throws Exception {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(); // 创建工作簿对象
			int sheetNumber = 0;
			if (null != dataList && dataList.size() > 0) {
				sheetNumber = dataList.size() / 10000;
			}
			for (int k = 0; k <= sheetNumber; k++) {
				HSSFSheet sheet = workbook.createSheet(title + "-" + (k + 1)); // 创建工作表

				// 产生表格标题行
				HSSFRow rowm = sheet.createRow(0);
				HSSFCell cellTiltle = rowm.createCell(0);

				// sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面 -
				// 可扩展】
				HSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);// 获取列头样式对象
				HSSFCellStyle style = this.getStyle(workbook); // 单元格样式对象

				sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, (rowName.length - 1)));
				cellTiltle.setCellStyle(columnTopStyle);
				cellTiltle.setCellValue(title);

				// 定义所需列数
				int columnNum = rowName.length;
				HSSFRow rowRowName = sheet.createRow(2); // 在索引2的位置创建行(最顶端的行开始的第二行)

				// 将列头设置到sheet的单元格中
				for (int n = 0; n < columnNum; n++) {
					HSSFCell cellRowName = rowRowName.createCell(n); // 创建列头对应个数的单元格
					cellRowName.setCellType(CellType.STRING); // 设置列头单元格的数据类型
					HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
					cellRowName.setCellValue(text); // 设置列头单元格的值
					cellRowName.setCellStyle(columnTopStyle); // 设置列头单元格样式
				}

				// 将查询出的数据设置到sheet对应的单元格中
				List<Object[]> spiltData = dataList.subList(k * 10000, (k + 1) * 10000 > dataList.size() ? dataList.size() : (k + 1) * 10000);
				for (int i = 0; i < spiltData.size(); i++) {
					Object[] obj = spiltData.get(i);// 遍历每个对象
					HSSFRow row = sheet.createRow(i + 3);// 创建所需的行数

					for (int j = 0; j < obj.length; j++) {
						HSSFCell cell = null; // 设置单元格的数据类型
						cell = row.createCell(j, CellType.STRING);
						if (!"".equals(obj[j]) && obj[j] != null) {
							cell.setCellValue(obj[j].toString()); // 设置单元格的值
						} else {
							cell.setCellValue("");
						}
						cell.setCellStyle(style); // 设置单元格样式
					}
				}
				
				// 让列宽随着导出的列长自动适应
				for (int colNum = 0; colNum < columnNum; colNum++) {
					int columnWidth = sheet.getColumnWidth(colNum) / 256;
					for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
						HSSFRow currentRow;
						// 当前行未被使用过
						if (sheet.getRow(rowNum) == null) {
							currentRow = sheet.createRow(rowNum);
						} else {
							currentRow = sheet.getRow(rowNum);
						}
						if (currentRow.getCell(colNum) != null) {
							HSSFCell currentCell = currentRow.getCell(colNum);
							if (currentCell.getCellType() == CellType.STRING) {
								int length = 100;
								if (null != currentCell.getStringCellValue()) {
									length = currentCell.getStringCellValue().getBytes().length;
								}
								if (columnWidth < length) {
									columnWidth = length;
								}
							}
						}
					}
					
					int colWidth = sheet.getColumnWidth(colNum)*2;
		            if(colWidth<255*256){
		                sheet.setColumnWidth(colNum, colWidth < 3000 ? 3000 : colWidth);    
		            }else{
		                sheet.setColumnWidth(colNum,6000 );
		            }
				}
			}

			if (workbook != null) {
				try {
					String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
					String headStr = "attachment; filename=\"" + fileName + "\"";
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition", headStr);
					OutputStream out = response.getOutputStream();
					workbook.write(out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 列头单元格样式
	 * @param workbook
	 * @return
	 */
	public HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {
		// 设置字体
		HSSFFont font = workbook.createFont();
		// 设置字体大小
		font.setFontHeightInPoints((short) 11);
		// 设置字体名字
		font.setFontName("Courier New");
		// 设置样式;
		HSSFCellStyle style = workbook.createCellStyle();
		// 在样式用应用设置的字体;
		style.setFont(font);
		// 设置自动换行;
		style.setWrapText(false);

		return style;
	}

	/**
	 * 列数据信息单元格样式
	 * @param workbook
	 * @return
	 */
	public HSSFCellStyle getStyle(HSSFWorkbook workbook) {
		// 设置字体
		HSSFFont font = workbook.createFont();
		// 设置字体名字
		font.setFontName("Courier New");
		// 设置样式;
		HSSFCellStyle style = workbook.createCellStyle();
		// 在样式用应用设置的字体;
		style.setFont(font);
		// 设置自动换行;
		style.setWrapText(false);
		return style;
	}
}

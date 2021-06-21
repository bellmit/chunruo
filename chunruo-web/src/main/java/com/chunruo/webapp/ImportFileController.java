package com.chunruo.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import com.chunruo.core.Constants;
import com.chunruo.core.model.Product;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.XlsParserUtil;

@Controller  
@RequestMapping("/import")
public class ImportFileController extends BaseController{
	@Autowired
	private MultipartResolver multipartResolver;
	
	/**
	 * 手动导入XLS
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/baseImportFile")
	public @ResponseBody Map<String, Object> baseImportFile(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String filePath = this.getServletFileUploadPath(request);
		if(StringUtil.isNull(filePath) || !FileUploadUtil.existFile(filePath)){
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", this.getText("ajax.import.file.error"));
			return resultMap;
		}
		
		
		try {
			List<String> headerList =  XlsParserUtil.readHeader(filePath);
			List<String> storeStrList =  new ArrayList<String> ();
			List<String> strHeaderList =  new ArrayList<String> ();
			List<String> keyValueHeaderList =  new ArrayList<String> ();
			Map<String, String> headerMap = new HashMap<String, String> ();
			if(headerList != null && headerList.size() > 0){
				for(int i = 0; i < headerList.size(); i ++ ){
					String headerKey = headerList.get(i);
					String indexHeader = String.format("header_%s", i);
					headerMap.put(headerKey, indexHeader);
					keyValueHeaderList.add(String.format("{key: '%s', value: '%s'}", indexHeader, headerKey));
					strHeaderList.add(String.format("{text: '%s', dataIndex: '%s', width: 150, sortable : true}", headerKey, indexHeader));
				}
				
				List<Map<String, String>> objectMapList = XlsParserUtil.read(filePath, 0);
				if(objectMapList != null && objectMapList.size() > 0){
					for(Map<String, String> objectMap : objectMapList){
						Map<String, String> storeMap = new HashMap<String, String> ();
						for(Entry<String, String> entry : objectMap.entrySet()){
							if(headerMap.containsKey(entry.getKey())){
								storeMap.put(headerMap.get(entry.getKey()), entry.getValue());
							}
						}
						storeStrList.add(StringUtil.mapStrToJson(storeMap));
					}
				}
			}
			
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("strHeaderList", strHeaderList);
			resultMap.put("storeStrList", storeStrList);
			resultMap.put("keyValueHeaderList", keyValueHeaderList);
			resultMap.put("message", this.getText("ajax.import.success"));
			return resultMap;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", this.getText("ajax.import.failure"));
		return resultMap;
	}
	
	/**
	 * 导入文件数据转换
	 * @param dataMapList
	 * @param headerMapList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> importDataToMapList(List<Object> dataMapList, List<Object> headerMapList){
		List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>> ();
		if(headerMapList != null 
				&& headerMapList.size() > 0
				&& dataMapList != null 
				&& dataMapList.size() > 0){
			Map<String, String> headerMap = new HashMap<String, String> ();
			for(Object headerDataMap : headerMapList){
				try{
					Map<String, Object> dataMap = (Map<String, Object>) headerDataMap;
					if(dataMap.containsKey("key") && dataMap.containsKey("value")){
						headerMap.put(StringUtil.null2Str(dataMap.get("key")), StringUtil.null2Str(dataMap.get("value")));
					}
				}catch(Exception e){
					continue;
				}
			}
			
			if(headerMap != null && headerMap.size() > 0){
				for(Object objectDataMap : dataMapList){
					try{
						Map<String, Object> dataMap = (Map<String, Object>) objectDataMap;
						if(dataMap != null && dataMap.size() > 0){
							Map<String, String> realDataMap = new HashMap<String, String> ();
							for(Entry<String, Object> entry : dataMap.entrySet()){
								if(headerMap.containsKey(StringUtil.null2Str(entry.getKey()))){
									realDataMap.put(headerMap.get(StringUtil.null2Str(entry.getKey())), StringUtil.null2Str(entry.getValue()));
								}
							}
							objectMapList.add(realDataMap);
						}
					}catch(Exception e){
						continue;
					}
				}
			}
		}
		return objectMapList;
	}
	
	/**
	 * Multipart文件上传
	 * @param request
	 * @return
	 */
	protected String getServletFileUploadPath(HttpServletRequest request){
		String filePath = null;
		try{
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<String> iter = multiRequest.getFileNames();
			    while (iter.hasNext()) {
			    	// 由CommonsMultipartFile继承而来,拥有上面的方法.
			        MultipartFile file = multiRequest.getFile(iter.next());
			        if (!file.isEmpty()) {
			        	String originFileName = file.getOriginalFilename();		
			        	String fileSuffix = FileUtil.getSuffixByFilename(originFileName);
			        	String tmpFileName = StringUtil.null2Str(UUID.randomUUID()) + fileSuffix;
	    				String fuallFilePath = Constants.DEPOSITORY_PATH + String.format("/temp/%s", new Object[] { tmpFileName });
	    				boolean result = FileUploadUtil.copyFile(file.getInputStream(),  fuallFilePath);
	    				if(result && (new File(fuallFilePath)).exists()){
	    					filePath = fuallFilePath;
	    				}
			        }
			     }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return filePath;
	}
	
	public static void main(String[] args) throws IOException {
		List<Map<String, String>> list = XlsParserUtil.read("C:\\chunruo\\product.xlsx");
		List<Map<String, Object>> allRow = XlsParserUtil.getAllRow("C:\\chunruo\\product.xlsx");
		System.out.println(list);
	
		System.out.println(allRow);
		
		
		String excelPath = "C:\\\\chunruo\\\\product.xlsx";
		try {
            //String encoding = "GBK";
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在

                String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
                Workbook wb;
                //根据文件后缀（xls/xlsx）进行判断
                if ( "xls".equals(split[1])){
                    FileInputStream fis = new FileInputStream(excel);   //文件流对象
                    wb = new HSSFWorkbook(fis);
                }else if ("xlsx".equals(split[1])){
                    wb = new XSSFWorkbook(excel);
                }else {
                    System.out.println("文件类型错误!");
                    return;
                }

                //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0

                int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: "+firstRowIndex);
                System.out.println("lastRowIndex: "+lastRowIndex);

                for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                    System.out.println("rIndex: " + rIndex);
                    Row row = sheet.getRow(rIndex);
                    if (row != null && rIndex >= 3) {
                        int firstCellIndex = row.getFirstCellNum();
                        int lastCellIndex = row.getLastCellNum();
                        for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
                            Cell cell = row.getCell(cIndex);
                            if (cell != null) {
                                System.out.println(cell.toString());
                            }
                        }
                        
                        Product product = new Product();
                        product.setIsDelete(true);
                        product.setName(row.getCell(firstCellIndex).toString());
                        firstCellIndex++;
                        String weight = row.getCell(firstCellIndex).toString();
                        Double realWeight = new Double("0");
                        if(weight.endsWith("ml")) {
                        	realWeight = StringUtil.nullToDouble(weight.substring(0,weight.indexOf("ml")));
                        }else if(weight.endsWith("g")) {
                        	realWeight = StringUtil.nullToDouble(weight.substring(0,weight.indexOf("g")));
                        }else if(weight.endsWith("L")) {
                        	realWeight = StringUtil.nullToDouble(weight.substring(0,weight.indexOf(""))) * 1000;
                        }else if(weight.endsWith("kg")) {
                        	realWeight = StringUtil.nullToDouble(weight.substring(0,weight.indexOf("kg"))) * 1000;
                        }
                        product.setWeigth(realWeight);
                        firstCellIndex++;
                        firstCellIndex++;

                        Double number = StringUtil.nullToDouble(row.getCell(firstCellIndex));
                        firstCellIndex++;
                        firstCellIndex++;
                        firstCellIndex++;
                        Double cost = StringUtil.nullToDouble(row.getCell(firstCellIndex));

                        
                        BigDecimal priceCost = BigDecimal.valueOf(cost).divide(BigDecimal.valueOf(number),2, RoundingMode.HALF_UP);

                        product.setPriceCost(priceCost.doubleValue());
                        product.setPriceRecommend(StringUtil.nullToDouble(priceCost.multiply(new BigDecimal(1.1D))));
                        product.setCategoryFids("1");
                        product.setCategoryIds("33");
                        product.setCreateTime(new Date());
                        product.setIsFreePostage(true);
                        product.setIsGroupProduct(false);
                        product.setIsMoreSpecProduct(false);
                        product.setIsPackage(false);
                        product.setIsShow(true);
                        product.setIsSoldout(false);
                        product.setIsSpceProduct(false);
                        product.setSalesNumber(1);
                        product.setSoldoutTime(new Date());
                        product.setStatus(true);
                        product.setStockNumber(999);
                        product.setTemplateId(1L);
                        product.setUpdateTime(new Date());
                    }
                }
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	
	}
	
	
	
	@RequestMapping(value="/imporeProduct")
	public @ResponseBody Map<String, Object> imporeProduct(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		
		
		try {
			
			ProductManager productManager = Constants.ctx.getBean(ProductManager.class);
			String excelPath = "C:\\\\chunruo\\\\product.xlsx";
			List<Product> productList = new ArrayList<>();
			
			
			 //String encoding = "GBK";
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在

                String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
                Workbook wb;
                //根据文件后缀（xls/xlsx）进行判断
                if ( "xls".equals(split[1])){
                    FileInputStream fis = new FileInputStream(excel);   //文件流对象
                    wb = new HSSFWorkbook(fis);
                }else if ("xlsx".equals(split[1])){
                    wb = new XSSFWorkbook(excel);
                }else {
                    System.out.println("文件类型错误!");
                    resultMap.put("error", false);
        			resultMap.put("success", true);
        			resultMap.put("strHeaderList", productList);
        			resultMap.put("message", this.getText("ajax.import.success"));
        			return resultMap;
                }

                //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0

                int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: "+firstRowIndex);
                System.out.println("lastRowIndex: "+lastRowIndex);

                for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                    System.out.println("rIndex: " + rIndex);
                    Row row = sheet.getRow(rIndex);
                    if (row != null && rIndex >= 3) {
                        int firstCellIndex = row.getFirstCellNum();
                        int lastCellIndex = row.getLastCellNum();
                        for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
                            Cell cell = row.getCell(cIndex);
                            if (cell != null) {
                                System.out.println(cell.toString());
                            }
                        }
                        
                        Product product = new Product();
                        product.setIsDelete(false);
                        product.setName(row.getCell(firstCellIndex).toString());
                        firstCellIndex++;
                        String weight = row.getCell(firstCellIndex).toString();
                        Double realWeight = StringUtil.nullToDouble(weight);
                        if(weight.endsWith("ml")) {
                        	realWeight = StringUtil.nullToDouble(weight.substring(0,weight.indexOf("ml")));
                        }else if(weight.endsWith("g")) {
                        	realWeight = StringUtil.nullToDouble(weight.substring(0,weight.indexOf("g")));
                        }else if(weight.endsWith("l")) {
                        	realWeight = StringUtil.nullToDouble(weight.substring(0,weight.indexOf(""))) * 1000;
                        }else if(weight.endsWith("kg")) {
                        	realWeight = StringUtil.nullToDouble(weight.substring(0,weight.indexOf("kg"))) * 1000;
                        }
                        product.setWeigth(realWeight);
                        firstCellIndex++;
                        firstCellIndex++;

                        Double number = StringUtil.nullToDouble(row.getCell(firstCellIndex));
                        firstCellIndex++;
                        firstCellIndex++;
                        firstCellIndex++;

                        Double cost = StringUtil.nullToDouble(row.getCell(firstCellIndex));

                        
                        BigDecimal priceCost = BigDecimal.valueOf(cost).divide(BigDecimal.valueOf(number),2, RoundingMode.HALF_UP);

                        product.setPriceCost(priceCost.doubleValue());
                        product.setPriceRecommend(StringUtil.nullToDouble(priceCost.multiply(new BigDecimal(1.1D))));
                        product.setCategoryFids("1");
                        product.setCategoryIds("33");
                        product.setCreateTime(new Date());
                        product.setIsFreePostage(true);
                        product.setIsGroupProduct(false);
                        product.setIsMoreSpecProduct(false);
                        product.setIsPackage(false);
                        product.setIsShow(true);
                        product.setIsSoldout(false);
                        product.setIsSpceProduct(false);
                        product.setSalesNumber(1);
                        product.setSoldoutTime(new Date());
                        product.setStatus(true);
                        product.setStockNumber(999);
                        product.setTemplateId(1L);
                        product.setUpdateTime(new Date());
                        productList.add(product);
                    }
                }
                
                productManager.batchInsert(productList, productList.size());
            } else {
                System.out.println("找不到指定的文件");
            }
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("strHeaderList", productList);
			resultMap.put("message", this.getText("ajax.import.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", this.getText("ajax.import.failure"));
		return resultMap;
	}
}

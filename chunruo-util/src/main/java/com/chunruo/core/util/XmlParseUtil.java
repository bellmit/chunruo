package com.chunruo.core.util;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.chunruo.core.util.XmlParseUtil;

public class XmlParseUtil {
	
	 /**
     * 获取指定xml文档的Document对象,xml文件必须在classpath中可以找到
     *
     * @param xmlFilePath xml文件路径
     * @return Document对象
     */ 
    public static Document parse2Document(String xmlFilePath){
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(new File(xmlFilePath));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return doc;
    }
    
    public static Document strToDocument(String xmlCont){
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
        	StringReader read = new StringReader(xmlCont);
            doc = reader.read(read);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return doc;
    }
    
	@SuppressWarnings("unchecked")
	public static Map<String, Object> xml2Map(String xmlFilePath) {
		// 将xml解析为Document对象
	    Document doc = XmlParseUtil.parse2Document(xmlFilePath);
		Map<String, Object> map = new HashMap<String, Object>();
		if (doc == null)
			return map;
		
		Element root = doc.getRootElement();
		for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
			Element e = (Element) iterator.next();
			List list = e.elements();
			if (list.size() > 0) {
				map.put(e.getName(), Dom2Map(e));
			} else
				map.put(e.getName(), e.getText());
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> xmlCont2Map(String xmlCont) {
		// 将xml解析为Document对象
	    Document doc = XmlParseUtil.strToDocument(xmlCont);
		Map<String, Object> map = new HashMap<String, Object>();
		if (doc == null)
			return map;
		
		Element root = doc.getRootElement();
		for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
			Element e = (Element) iterator.next();
			List list = e.elements();
			if (list.size() > 0) {
				map.put(e.getName(), Dom2Map(e));
			} else
				map.put(e.getName(), e.getText());
		}
		return map;
	}

	
	@SuppressWarnings("unchecked")
	public static Map Dom2Map(Element e) {
		Map map = new HashMap();
		List list = e.elements();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Element iter = (Element) list.get(i);
				List mapList = new ArrayList();

				if (iter.elements().size() > 0) {
					Map m = Dom2Map(iter);
					if (map.get(iter.getName()) != null) {
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = new ArrayList();
							mapList.add(obj);
							mapList.add(m);
						}
						if (obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = (List) obj;
							mapList.add(m);
						}
						map.put(iter.getName(), mapList);
					} else
						map.put(iter.getName(), m);
				} else {
					if (map.get(iter.getName()) != null) {
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = new ArrayList();
							mapList.add(obj);
							mapList.add(iter.getText());
						}
						if (obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = (List) obj;
							mapList.add(iter.getText());
						}
						map.put(iter.getName(), mapList);
					} else
						map.put(iter.getName(), iter.getText());
				}
			}
		} else
			map.put(e.getName(), e.getText());
		return map;
	}

	public static void main(String[] args) {
		Map map = XmlParseUtil.xml2Map("/Users/zhuzhx/workspace/java/test/xml/test2.xml");
		System.out.println(map.toString());
	}

}
package com.chunruo.core.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.chunruo.core.util.XmlUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class XmlUtil {
	private final static Log log = LogFactory.getLog(XmlUtil.class);
	public static final String TEMPLATE_ENCODE = "utf-8";

	public static boolean isAvalidXML(String filename){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.parse(new File(filename));
			return true;
		} catch (Exception e) {
			return false;
		}
	} 

	public static boolean isStrAvalidXML(String strXml){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader read = new StringReader(strXml);
			InputSource source = new InputSource(read);
			builder.parse(source);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Document openXml(String filename) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new File(filename));
	}

	public static org.dom4j.Document openXmlByDom4j(String filePath) throws DocumentException{
		SAXReader reader = new SAXReader();
		return reader.read(filePath);
	}

	public static Document getDocByStr(String strXml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringReader read = new StringReader(strXml);
		InputSource source = new InputSource(read);
		return builder.parse(source);
	}

	public static String getStrByTag(Element element, String tag) {
		NodeList nl = element.getElementsByTagName(tag);
		if (nl != null && nl.getLength() > 0 && nl.item(0) != null) {
			return nl.item(0).getNodeValue();
		}
		return "";
	}

	public static String getStrByTag(org.dom4j.Element element, String tag) {
		String elText = element.elementTextTrim(tag);
		return elText == null ? "" : elText;
	}

	public static String getElementText(Element element) {
		DOMSource source = new DOMSource(element);
		StringWriter out = new StringWriter();
		StreamResult result = new StreamResult(out);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);
			return out.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return "";
	}

	public static String generateXml(String templateFile, Map<String, Object> prams) throws IOException, TemplateException {
		return XmlUtil.generateXml(templateFile, prams, TEMPLATE_ENCODE);
	}

	public static String generateXml(String templateFile, Map<String, Object> prams, String templateEncode) throws IOException, TemplateException {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding(templateEncode);
		cfg.setEncoding(Locale.getDefault(), templateEncode);
		cfg.setStrictSyntaxMode(true);
		cfg.setNumberFormat("0");
		cfg.setClassForTemplateLoading(XmlUtil.class, "/");
		Template template = cfg.getTemplate(templateFile);
		StringWriter out = new StringWriter();
		template.process(prams, out);
		return out.toString();
	}

	// 创建XML文件到本地文件
	public static void generateXmlToFile(String templateFile, Map<String, Object> prams, String filePath) throws IOException, TemplateException {
		XmlUtil.generateXmlToFile(templateFile, prams, filePath, TEMPLATE_ENCODE);
	}

	// 创建XML文件到本地文件
	public static void generateXmlToFile(String templateFile, Map<String, Object> prams, String filePath, String outEncoding) throws IOException, TemplateException {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding(TEMPLATE_ENCODE);
		cfg.setEncoding(Locale.getDefault(), TEMPLATE_ENCODE);
		cfg.setStrictSyntaxMode(true);
		cfg.setNumberFormat("0");
		cfg.setClassForTemplateLoading(XmlUtil.class, "/");
		Template template = cfg.getTemplate(templateFile);		
		File localSyncFile = new File(filePath);
		if(!localSyncFile.getParentFile().exists()) {
			localSyncFile.getParentFile().mkdirs();
		}
		FileWriter out = new FileWriter(filePath);
		template.setOutputEncoding(outEncoding);
		template.process(prams, out);
		out.close();
		out=null;
	}

	// 将XMLStream转为为BEAN
	public static Object generateXmlStream2Bean(String xmlTag, Map<String, String> xmlTagMap, Class<?> beanClass, InputStream xmlStream) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate(xmlTag, beanClass);
		for (Iterator<String> it = xmlTagMap.keySet().iterator(); it.hasNext();) {
			String tag = (String) it.next();
			String value = xmlTagMap.get(tag);
			digester.addCallMethod(tag, value, 0);
		}
		return digester.parse(xmlStream);
	}

	// 通过XML TAG来求出此TAG下边的XML块的TEXT文本
	// filename :文件全路径
	// TAG：XML标记
	// INDEX:此标记在XML中的第几次出现
	public static String gerericTextByXMLTag(String fileName, String tag, int index) {
		Document doc = null;
		try {
			doc = XmlUtil.openXml(fileName);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return XmlUtil.getElementText((Element) doc.getElementsByTagName(tag).item(index));
	}
	
	public static String readerEmailXml(String path) {  
        String str = null;  
        str = readerXml(path);  
        return str;  
    }  

    private static String readerXml(String path) {  
        SAXReader reader = new SAXReader();  
        String str = null;  
        try {  
        	org.dom4j.Document d = reader.read(new File(path));  
        	org.dom4j.Element e = d.getRootElement();  
        	org.dom4j.Element htmle = e.element("html");  
            str = htmle.asXML();  
        } catch (DocumentException e) {  
            e.printStackTrace();  
        }  
        return str;  
    }  
    /** 
     * 将对象直接转换成String类型的 XML输出 
     *  
     * @param obj 
     * @return 
     */  
    public static String convertToXml(Object obj,Class<?> load) {
        JAXBContext context;
        try
        {
            context = JAXBContext.newInstance(load);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "GBK");
            StringWriter writer = new StringWriter();
            marshaller.marshal(obj,writer);
            return writer.toString();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }  
}

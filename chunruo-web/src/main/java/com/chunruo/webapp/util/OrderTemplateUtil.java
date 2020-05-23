package com.chunruo.webapp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;

@Service("templateManager")
public class OrderTemplateUtil{
	protected final static transient Log log = LogFactory.getLog(OrderTemplateUtil.class);
	public final static String TEMPLATE_ENCODE = "utf-8";
	public final static String TEMPLATE_XML = ".xml";

	public static String compile(Long objectId, String tplFilePath, String outFilePath, Map<String, Object> objectMap) throws Exception {
		//prepare configuration
		Configuration cfg = new Configuration();
		cfg.setEncoding(Locale.getDefault(), TEMPLATE_ENCODE);
		cfg.setStrictSyntaxMode(true);
		cfg.setWhitespaceStripping(true);
		cfg.setNumberFormat("0");
		
		File file = new File(tplFilePath);
		if(!file.exists()){
			throw new Exception("template_no_set" + tplFilePath);
		}

		//put template body
		StringTemplateLoader stl = new StringTemplateLoader();
		stl.putTemplate(StringUtil.null2Str(objectId), FileUtil.loadAFileToString(file));		
		cfg.setTemplateLoader(stl);
		Writer out = null;
		try {
			String fullFilePath = outFilePath + "/" + CoreUtil.idToNamePath(objectId) + TEMPLATE_XML;
			log.debug("fullFilePath==" + fullFilePath);
			FileUploadUtil.newFile(fullFilePath);
			out = new OutputStreamWriter(new FileOutputStream(fullFilePath), TEMPLATE_ENCODE);
			Template tpl = cfg.getTemplate(StringUtil.null2Str(objectId));
			Environment env = tpl.createProcessingEnvironment(objectMap, out);
			env.process();
			out.flush();
			out.close();
			out = null;
			return fullFilePath;
		} catch (Exception e) {
			log.error(e.getCause() + e.getMessage());
			throw e;
		} finally{
			if(out != null){
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					log.error(e.getCause() + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}

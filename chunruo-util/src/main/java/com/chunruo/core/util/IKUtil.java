package com.chunruo.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;


/**
 * @Description: ik分词
 */
public class IKUtil {
	
	public static List<String> getKeywordList(String keyword){
		IKSegmenter ik = null;
		List<String> result = new ArrayList<String>();
		try {
			StringReader sr = new StringReader(keyword);  
			ik = new IKSegmenter(sr, true);  

			Lexeme lex=null;  
			while((lex=ik.next()) != null){  
				result.add(lex.getLexemeText().toUpperCase());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			// 对象null快速回收内存
			if(ik != null) {
				ik = null;
			}
		}
		return result;
	}

}
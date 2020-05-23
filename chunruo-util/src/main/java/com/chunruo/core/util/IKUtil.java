package com.chunruo.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Dictionary;


/**
 * @Description: ik分词
 */
public class IKUtil {
	
	public static List<String> getKeywordList(String keyword, Set<String> wordsSet){
		IKSegmenter ik = null;
		List<String> result = new ArrayList<String>();
		try {
			StringReader sr = new StringReader(keyword);  
			ik = new IKSegmenter(sr, true);  

			//新增单词
			Dictionary dictionary =  Dictionary.getSingleton();
			dictionary.addWords(wordsSet);
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

	public static void main(String[] args) {
		Set<String> wordsSet = new HashSet<String>();
		wordsSet.add("蔓越霉");
		wordsSet.add("海德力");
		wordsSet.add("松江区");
		List<String> keywordList = getKeywordList("贺海13095520537安徽安庆宿松县佐坝碧岭村河北大海上海市海德力",wordsSet);
		System.out.println(StringUtil.objectToJSON(keywordList));
		List<String> list = new ArrayList<String>();
		Iterator<String> iterator = keywordList.iterator();
		while(iterator.hasNext()) {
			String keyword = iterator.next();
			if(StringUtil.compareObject(keyword, "宿松")) {
				iterator.remove();
			}else {
				list.add(keyword);
			}
		}
		for(String str : list) {
			System.out.println(str);
		}

	}
}
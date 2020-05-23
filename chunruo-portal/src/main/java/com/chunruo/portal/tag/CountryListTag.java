package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import com.chunruo.cache.portal.impl.CountryListCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Country;
import com.chunruo.core.util.PingYinUtil;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.vo.TagModel;

/**
 * 国际电话编码列表
 * @author chunruo
 *
 */
public class CountryListTag extends BaseTag {

	public TagModel<TreeMap<String, List<Country>>> getData(){
		TagModel<TreeMap<String, List<Country>>> tagModel = new TagModel<TreeMap<String, List<Country>>> ();
		try{
			CountryListCacheManager countryListCacheManager = Constants.ctx.getBean(CountryListCacheManager.class);
			List<Country> countryList = countryListCacheManager.getSession();
			if(countryList != null && countryList.size() > 0){
				//  排序
				Collections.sort(countryList, new Comparator<Country>(){
					public int compare(Country obj1, Country obj2){
						String firstStr1 = PingYinUtil.getTheFirstToUpperCase(obj1.getCountryName());
						String firstStr2 = PingYinUtil.getTheFirstToUpperCase(obj2.getCountryName());
						return firstStr1.compareTo(firstStr2);
					}
				}); 

				TreeMap<String, List<Country>> treeMap = new TreeMap<String, List<Country>> ();
				for(Country country : countryList){
					String firstStr = PingYinUtil.getTheFirstToUpperCase(country.getCountryName());
					if(treeMap.containsKey(firstStr)){
						treeMap.get(firstStr).add(country);
					}else{
						List<Country> list = new ArrayList<Country> ();
						list.add(country);
						treeMap.put(firstStr, list);
					}
				}
				tagModel.setData(treeMap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}
}

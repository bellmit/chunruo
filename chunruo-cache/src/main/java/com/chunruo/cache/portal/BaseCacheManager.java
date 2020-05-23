package com.chunruo.cache.portal;

import java.io.Serializable;
import org.springframework.stereotype.Service;

@Service("cachaManager")
public interface BaseCacheManager<T, PK extends Serializable> {
	
	public int execute()throws Exception;
	
	public String getCacheName();
	
}

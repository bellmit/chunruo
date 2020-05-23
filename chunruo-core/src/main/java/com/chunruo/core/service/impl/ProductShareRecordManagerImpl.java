package com.chunruo.core.service.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductShareRecord;
import com.chunruo.core.repository.ProductShareRecordRepository;
import com.chunruo.core.service.ProductShareRecordManager;
import com.chunruo.core.util.ShortUrlUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("productShareRecordManager")
public class ProductShareRecordManagerImpl extends GenericManagerImpl<ProductShareRecord, Long> implements ProductShareRecordManager{
	private Lock lock = new ReentrantLock();
	private ProductShareRecordRepository productShareRecordRepository;
	
	@Autowired
	public ProductShareRecordManagerImpl(ProductShareRecordRepository productShareRecordRepository) {
		super(productShareRecordRepository);
		this.productShareRecordRepository = productShareRecordRepository;
	}

	@Override
	public ProductShareRecord getProductShareRecordByToken(String token) {
       return this.productShareRecordRepository.getProductShareRecordByToken(token);
	}
	
	
	@Override
	public String getToken(){
		// 加锁
		lock.lock();
		try{
			int number = 0;
			while(number < 5){
				//只循环5次，若五次都没能取得唯一值，直接失败
				String token = StringUtil.null2Str(ShortUrlUtil.generateShortUuid());
				if(!StringUtil.isNull(token)) {
					ProductShareRecord productShareRecord = this.getProductShareRecordByToken(token);
					if(productShareRecord == null || productShareRecord.getRecordId() == null) {
						return token;
					}
				}
				number++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			// 释放锁
			lock.unlock();     
		}
		return null;
	}

}

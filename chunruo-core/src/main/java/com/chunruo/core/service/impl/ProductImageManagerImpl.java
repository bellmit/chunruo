package com.chunruo.core.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.repository.ProductImageRepository;
import com.chunruo.core.service.ProductImageManager;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("productImageManager")
public class ProductImageManagerImpl extends GenericManagerImpl<ProductImage, Long> implements ProductImageManager{
	private ProductImageRepository productImageRepository;

	@Autowired
	public ProductImageManagerImpl(ProductImageRepository productImageRepository) {
		super(productImageRepository);
		this.productImageRepository = productImageRepository;
	}

	@Override
	public List<ProductImage> getProductImageListByProductId(Long productId, Integer imageType) {
		return this.productImageRepository.getProductImageListByProductId(productId, imageType);
	}
	
	@Override
	public List<ProductImage> getImageListByUpdateTime(Date updateTime) {
		return this.productImageRepository.getImageListByUpdateTime(updateTime);
	}
	
	@Override
	public List<ProductImage> saveAndDelProductImage(Long productId, Integer imageType, List<ProductImage> saveRecordList){
		try {
			List<ProductImage> dbImageList = this.productImageRepository.getProductImageListByProductId(productId, imageType);
			List<ProductImage> imageList = this.copyImageFile(productId, saveRecordList, dbImageList);
			
			// 最新图片列表
			Map<Long, ProductImage> realImageMap = new HashMap<Long, ProductImage> ();
			if(imageList != null && imageList.size() > 0){
				for(ProductImage image : imageList){
					realImageMap.put(image.getImageId(), image);
				}
			}
			
			// 删除
			if(dbImageList != null && dbImageList.size() > 0){
				deleteImageFolder(realImageMap, dbImageList);
			}
			return imageList;
		} catch (Exception e) {
			this.log.debug(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 图片拷贝
	 * @param saveRecordList
	 * @param imageMap
	 * @throws Exception
	 */
	private List<ProductImage> copyImageFile(Long productId, List<ProductImage> saveRecordList, List<ProductImage> imageList)throws Exception{
		if ((saveRecordList == null) || (saveRecordList.size() == 0)) {
			return null;
		}
		
		Map<Long, ProductImage> imageMap = new HashMap<Long, ProductImage> ();
		if(imageList != null && imageList.size() > 0){
			for(ProductImage image : imageList){
				imageMap.put(image.getImageId(), image);
			}
		}
		
		List<ProductImage> list = new ArrayList<ProductImage> ();
		for (int i = 0; i < saveRecordList.size(); i++) {
			ProductImage image = saveRecordList.get(i);
			boolean isNewImage = false;
			if(image.getImageId() == null || !imageMap.containsKey(image.getImageId())){
				// 是否新增对象
				isNewImage = true;
			}
			
			try {
				if (isNewImage) {
					//新增
					File newFile = null;
					String filePath = CoreUtil.dateToPath("/images", image.getImagePath());
					String srcFilePath = Constants.DEPOSITORY_PATH + StringUtil.null2Str(image.getImagePath()).replace("depository", "");
					final String fullFilePath = Constants.EXTERNAL_IMAGE_PATH + "/upload" + filePath;
					boolean result = FileUploadUtil.moveFile(srcFilePath, fullFilePath);
					if (result == true 
							&& (newFile = new File(fullFilePath)) != null
							&& !newFile.exists()){
						this.log.info("Resource Leaking:  Could not remove uploaded file '" + fullFilePath + "'.");
						throw new Exception("NotFound File[filePaht=" + fullFilePath + "]");
					}else if (!result) {
						throw new Exception("NotFound File[filePaht=" + fullFilePath + "]");
					}
					
					image.setProductId(productId);
					image.setImageType(image.getImageType());
					image.setImageName(StringUtil.null2Str(image.getImageName()).trim());
					image.setImagePath(filePath);
					image.setSort(StringUtil.nullToByte(i));
					image.setUpdateTime(DateUtil.getCurrentDate());
					image.setCreateTime(DateUtil.getCurrentDate());
					image = this.save(image);
					list.add(image);
				}else{
					// 更新
					ProductImage dbImage = imageMap.get(image.getImageId());
					dbImage.setImageName(StringUtil.null2Str(image.getImageName()).trim());
					dbImage.setSort(StringUtil.nullToByte(i));
					dbImage.setUpdateTime(DateUtil.getCurrentDate());
					dbImage = this.update(dbImage);
					list.add(dbImage);
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return list;
	}

	/**
	 * 图片文件删除
	 * @param imageMap
	 * @param deleteImageList
	 * @param userId
	 */
	private void deleteImageFolder(Map<Long, ProductImage> imageMap, List<ProductImage> imageList){
		try{
			if (imageList != null && imageList.size() > 0) {
				List<Long> imageIdList = new ArrayList<Long> ();
				for(ProductImage image : imageList){
					try{
						// 不包含在新增和更新的图片,表示删除
						if(imageMap == null 
								|| imageMap.size() <= 0
								|| !imageMap.containsKey(image.getImageId())){
							imageIdList.add(image.getImageId());
						}
					}catch(Exception e){
						e.printStackTrace();
						log.debug(e.getMessage());
						continue;
					}
				}
				this.productImageRepository.deleteByIdList(imageIdList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.log.debug(e.getMessage());
		}
	}
}
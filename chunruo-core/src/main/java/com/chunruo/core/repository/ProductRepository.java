package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Product;

@Repository("productRepository")
public interface ProductRepository extends GenericRepository<Product, Long> {
	
	@Query("from Product where status =:status and isDelete = false and isShow=true")
	public List<Product> getProductList(@Param("status") boolean status);

	@Query("from Product where categoryId =:categoryId and  status =true and isDelete=false and isShow=true")
	public List<Product> getProductListByCategoryId(@Param("categoryId") Long categoryId);
	
	@Query("from Product where categoryId =:categoryId and status =:status and isDelete=false and isShow=true")
	public List<Product> getProductListByCategoryId(@Param("categoryId") Long categoryId, @Param("status")boolean status);
	
	@Query("from Product where categoryId in (:categoryIdList) and status =:status and isDelete=false and isShow=true")
	public List<Product> getProductListByCategoryIdList(@Param("categoryIdList") List<Long> categoryIdList, @Param("status")boolean status);
	
	@Query("from Product where categoryFid =:categoryFid  and  status =true and isDelete=false and isShow=true")
	public List<Product> getProductListByCategoryFid(@Param("categoryFid") Long categoryFid);
	
	@Query("from Product where categoryFid =:categoryFid and status =:status and isDelete=false and isShow=true")
	public List<Product> getProductListByCategoryFid(@Param("categoryFid") Long categoryFid, @Param("status")boolean status);
	
	@Modifying
	@Query("update Product set stockNumber = stockNumber - :number, isSoldout = :isSoldout, salesNumber = salesNumber + :number, updateTime = :modiDate where productId =:productId")
    void updateProductNumber(@Param("number")Integer number, @Param("isSoldout")Boolean isSoldout, @Param("productId") Long productId, @Param("modiDate") Date modiDate);
	
	@Modifying
	@Query("update Product set status =:status, updateTime = :modiDate where productId in (:idList)")
	void updateProductStatus(@Param("idList") List<Long> idList, @Param("status")boolean status, @Param("modiDate") Date modiDate);
	
	@Modifying
	@Query("update Product set isSoldout =:isSoldout, soldoutTime = :modiDate, updateTime = :modiDate where productId = :productId")
	void updateProductSoldoutStatus(@Param("productId") Long productId, @Param("isSoldout")Boolean isSoldout, @Param("modiDate") Date modiDate);
	
	@Modifying
	@Query("update Product set isSoldout =:isSoldout, soldoutTime = :modiDate, updateTime = :modiDate where productId in (:idList)")
	void updateProductSoldoutStatus(@Param("idList") List<Long> idList, @Param("isSoldout")Boolean isSoldout, @Param("modiDate") Date modiDate);
	
	@Query("from Product where updateTime >:updateTime")
	public List<Product> getProductByUpdateTime(@Param("updateTime")Date updateTime);
	
	@Modifying
	@Query("update Product set isPackage = false, updateTime = now() where isPackage = true")
	void updateProductPackageToFalse();
	
	@Modifying
	@Query("update Product set isPackage = true, updateTime = now() where productId = :productId")
	void updateProductPackageToTrue(@Param("productId")Long productId);
	
	@Query("from Product where isPackage = true")
	List<Product> getProductListByIsPackage();

	@Query("from Product where status =:status and isSoldout =:isSoldout and isShow=true and isDelete=false")
	public List<Product> getProductListByStatusAndIsSoldout(@Param("status")boolean status, @Param("isSoldout") boolean isSoldout);

	@Query("from Product where brandId =:brandId and status =:status and isDelete=false and isShow=true ")
	public List<Product> getProductListByBrandId(@Param("brandId") Long brandId, @Param("status")boolean status);
	
	@Query("from Product where brandId =:brandId and  status =true and isDelete=false and isShow=true")
	public List<Product> getProductListByBrandId(@Param("brandId") Long brandId);
	
	@Query("from Product where seckillId =:seckillId and  status =true and isDelete=false and isShow=true")
	public List<Product> getProductListBySeckillId(@Param("seckillId") Long seckillId);

	@Modifying
	@Query("update Product set isDelete =:isDelete, updateTime = :currentDate where productId in (:idList)")
	public void deleteProduct(@Param("idList") List<Long> idList, @Param("isDelete")Boolean isDelete, @Param("currentDate")Date currentDate);
	
	@Query("from Product where assemblyModelId =:assemblyModelId and  status =true and isDelete=false")
	public List<Product> getProductByAssemblyModelId(@Param("assemblyModelId") Long assemblyModelId);
	
	@Modifying
	@Query("update Product set seckillTotalStock=:seckillTotalStock,updateTime=now() where productId=:productId")
	public void updateProductSeckillTotalNumber(@Param("productId")Long productId, @Param("seckillTotalStock")Integer seckillTotalStock);

	@Query("from Product where isGuideProduct =:isGuideProduct and  status =true and isDelete=false")
	public List<Product> getProductListByIsGuideProduct(@Param("isGuideProduct") Boolean isGuideProduct);

	@Query("from Product where isOpenV2Price =:isOpenV2Price and  status =true and isDelete=false and isShow=true ")
	public List<Product> getProductListByIsOpenV2Price(@Param("isOpenV2Price")Boolean isOpenV2Price);
	
	@Query("from Product where isOpenV3Price =:isOpenV3Price and  status =true and isDelete=false and isShow=true ")
	public List<Product> getProductListByIsOpenV3Price(@Param("isOpenV3Price")Boolean isOpenV3Price);

	@Query("from Product where (isOpenV2Price = true or isOpenV3Price = true) and status = true and isDelete = false and isShow=true ")
	public List<Product> getProductListByIsOpenVPrice();

	@Query("from Product where productId =:productId and isDelete=:isDelete and status=true and isShow=true ")
	public Product getProductByProductIdAndIsDelete(@Param("productId")Long productId, @Param("isDelete")Boolean isDelete);

	@Modifying
	@Query("update Product set isShow =:isShow, updateTime = :modiDate where productId in (:idList)")
	public void updateProductShowStatus(@Param("idList")List<Long> idList, @Param("isShow")Boolean isShow, @Param("modiDate")Date currentDate);

	
	@Modifying
	@Query("update Product set  maxLimitNumber=0 ,lastRepTime = now() where productId in (:idList)")
	public void updateProductRepStatus(@Param("idList")List<Long> idList);
}

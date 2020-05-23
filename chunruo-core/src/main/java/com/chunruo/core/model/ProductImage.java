package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="jkd_product_image")
public class ProductImage {
	public final static Integer IMAGE_TYPE_HEADER = 0; 				//主图
	public final static Integer IMAGE_TYPE_MATERIAL = 1; 			//素材
	private Long imageId;
    private Long productId;
    private String imageName;
    private String imagePath;
    private Integer imageType;
    private Byte sort;
    private Date createTime;
    private Date updateTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}
	
	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	@Column(name="image_name")
    public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	@Column(name="image_type", length=20)
	public Integer getImageType() {
		return imageType;
	}

	public void setImageType(Integer imageType) {
		this.imageType = imageType;
	}

	@Column(name="image_path")
    public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath == null ? null : imagePath.trim();
	}

    @Column(name="sort")
    public Byte getSort() {
        return sort;
    }

    public void setSort(Byte sort) {
        this.sort = sort;
    }

    @Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name="update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
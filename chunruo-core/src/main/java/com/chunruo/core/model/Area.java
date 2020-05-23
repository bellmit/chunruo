package com.chunruo.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 区域列表
 * @author chunruo
 */
@Entity
@Table(name="jkd_area")
public class Area implements Serializable{
	private static final long serialVersionUID = 6064850367128321940L;
	public static final Integer LEVEL_PROVINCE = 1; 	//省份
	public static final Integer LEVEL_CITY = 2; 		//城市
	public static final Integer LEVEL_COUNTY = 3; 		//区县
    private Long areaId;		//序号
    private String areaName;	//区域名称
    private Long parentId;		//父类ID
    private String shortName;	//区域缩略名
    private Integer areacode;	//区域编码
    private Integer zipcode;	//区域邮编
    private String pinyin;		//拼音
    private String longitude;	//经度		
    private String latitude;	//纬度
    private Integer level;		//级别
    private String position;	//位置
    private Integer sort;		//排序
    private Boolean isDisUse = false;   //是否弃用
    private Date createTime;	//创建时间
    private Date updateTime;	//更新时间
    
    @Transient
    private Long lastTime = null;
    private List<Area> childAreaList = new ArrayList<Area> ();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

    @Column(name="area_name")
    public String getAreaName() {
        return areaName;
    }
    
	public void setAreaName(String areaName) {
        this.areaName = areaName == null ? null : areaName.trim();
    }

    @Column(name="parent_id")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Column(name="short_name")
    public String getShortName() {
        return shortName;
    }
    
    public void setShortName(String shortName) {
		this.shortName = shortName == null ? null : shortName.trim();
	}

    @Column(name="area_code")
    public Integer getAreacode() {
        return areacode;
    }

    public void setAreacode(Integer areacode) {
        this.areacode = areacode;
    }

    @Column(name="zip_code")
    public Integer getZipcode() {
        return zipcode;
    }

    public void setZipcode(Integer zipcode) {
        this.zipcode = zipcode;
    }

    @Column(name="pinyin")
    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin == null ? null : pinyin.trim();
    }

    @Column(name="longitude")
    public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude == null ? null : longitude.trim();
	}

    @Column(name="latitude")
    public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude == null ? null : latitude.trim();
	}

    @Column(name="level")
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Column(name="position")
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position == null ? null : position.trim();
    }

    @Column(name="sort")
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
    
    @Column(name="is_dis_use",nullable=false)
    public Boolean getIsDisUse() {
		return isDisUse;
	}

	public void setIsDisUse(Boolean isDisUse) {
		this.isDisUse = isDisUse;
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

	@Transient
	public List<Area> getChildAreaList() {
		return childAreaList;
	}

	public void setChildAreaList(List<Area> childAreaList) {
		this.childAreaList = childAreaList;
	}

	@Transient
	public Long getLastTime() {
		return lastTime;
	}

	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}
}
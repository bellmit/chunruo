package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 用户等级规律
 * @author chunruo
 *
 */
@Entity
@Table(name = "jkd_user_level_explain")
public class UserLevelExplain  {
	public final static Integer INVITE_TEAM_TYPE_MYSELF = 1;     // 自己
	public final static Integer INVITE_TEAM_TYPE_FRIEND = 2;     // 朋友
	public final static Integer INVITE_TEAM_TYPE_AGENT = 2;      // 总代时使用
	public final static Integer INVITE_TEAM_TYPE_WELFARE = 3;    // 升级返利
	public final static Integer INVITE_TEAM_TYPE_CALCULATE = 4;  // 计算省钱
	public final static Integer INVITE_TEAM_TYPE_COMMENT = 5;    // 评论
	public final static Integer INVITE_TEAM_TYPE_OTHER = 6;      // 其他  (视频等)
	public final static Integer INVITE_TEAM_TYPE_PUSHUSER = 1;   //推手
	public final static Integer INVITE_TEAM_TYPE_PUSHMANAGER = 2; //推广经理
	public final static Integer INVITE_TEAM_TYPE_PUSHOTHER = 3;   //其他
	
	private Long explainId;       	//等级说明ID
	private Integer level;			//等级     1.店长 2.经销商  3.总代
	private String imageUrl;        //图片
	private String headImage;       //头部图片
	private String title;			//抬头
	private String content;			//内容
	private Integer type;			//类型1，自己，2， 好友
	private Integer sort;           //排序
	private String description;     //描述
	private Date createTime;		//创建时间
	private Date updateTime;		//更新时间

	private List<String> textList = new ArrayList<String>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "explain_id", unique = true, nullable = false)
	public Long getExplainId() {
		return this.explainId;
	}

	public void setExplainId(Long explainId) {
		this.explainId = explainId;
	}

	@Column(name = "level")
	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "title")
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "content")
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time", length = 19)
	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Column(name = "sort")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	@Column(name = "imageUrl")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@Column(name = "head_image")
	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public List<String> getTextList() {
		return textList;
	}

	public void setTextList(List<String> textList) {
		this.textList = textList;
	}
	
	
}

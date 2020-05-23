package com.chunruo.security.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.Menu;

@SuppressWarnings("unused")
public class TreeNode implements Serializable, Cloneable {
	private static final long serialVersionUID = 9052801289931940765L;
	private Long menuId;		//序号
	private Menu parentMenu;	//父类ID
	private String namePath;	//名称路径
	private String name;		//菜单名称
	private String ctrl;		//菜单控制器名称
	private Integer sequence;	//排序索引
	private Boolean status;		//状态
	private String icon;		//图标
	private String desc;		//描述
	private Boolean isResource = false;
	private Integer enableType = 0;

	private Map<Long, TreeNode> children = new HashMap<Long, TreeNode> ();
	private List<TreeNode> childrenNode = new ArrayList<TreeNode> ();
	private Boolean expanded = Boolean.valueOf(false);

	public TreeNode() {
		this.expanded = Boolean.valueOf(false);
		this.children = new HashMap<Long, TreeNode> ();
	}

	public TreeNode clone(){
		TreeNode treeNode = new TreeNode ();
		try {
			treeNode = (TreeNode) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return treeNode;
	}

	public TreeNode(Menu menu){
		if(menu != null && menu.getMenuId() != null){
			this.menuId = menu.getMenuId();
			this.name= menu.getName();
			this.ctrl= menu.getCtrl();
			this.sequence= menu.getSequence();
			this.status= menu.getStatus();
			this.icon= menu.getIcon();
			this.desc= menu.getDesc();
			this.enableType = StringUtil.nullToInteger(menu.getEnableType());
			this.isResource = StringUtil.nullToBoolean(menu.getIsResource());
		}
		this.expanded = Boolean.valueOf(false);
		this.children = new HashMap<Long, TreeNode> ();
	}
	
	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	public Menu getParentMenu() {
		return parentMenu;
	}

	public void setParentMenu(Menu parentMenu) {
		this.parentMenu = parentMenu;
	}

	public String getNamePath() {
		return namePath;
	}

	public void setNamePath(String namePath) {
		this.namePath = namePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCtrl() {
		return ctrl;
	}

	public void setCtrl(String ctrl) {
		this.ctrl = ctrl;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean getIsResource() {
		return isResource;
	}

	public void setIsResource(Boolean isResource) {
		this.isResource = isResource;
	}

	public Integer getEnableType() {
		return enableType;
	}

	public void setEnableType(Integer enableType) {
		this.enableType = enableType;
	}

	public void addChildNode(Long nodeId, TreeNode childNode){
		this.children.put(nodeId, childNode);
	}

	public Map<Long, TreeNode> getChildren() {
		return this.children;
	}

	public void setChildren(Map<Long, TreeNode> children) {
		this.children = children;
	}

	public List<TreeNode> getChildrenNode() {
		List<TreeNode> list = new ArrayList<TreeNode> ();
		List<Map.Entry<Long, TreeNode>> arrayList = new ArrayList<Map.Entry<Long, TreeNode>> (this.children.entrySet());
		Collections.sort(arrayList, new Comparator<Map.Entry<Long, TreeNode>>() {
			public int compare(Map.Entry<Long, TreeNode> o1, Map.Entry<Long, TreeNode> o2) {
				return (o2.getValue().getSequence() < o1.getValue().getSequence()) ? 1 : 0;
			}
		}); 

		if(arrayList != null && arrayList.size() > 0){
			for(Iterator<Map.Entry<Long, TreeNode>> iter = arrayList.iterator(); iter.hasNext();) {
				Map.Entry<Long, TreeNode> entry = iter.next();
				list.add(entry.getValue());
			}

			Collections.sort(list, new Comparator<TreeNode>(){
				public int compare(TreeNode o1, TreeNode o2){
					return (o2.getSequence() < o1.getSequence()) ? 1 : -1;
				}
			});
		}
		return list;
	}
	
	public static void setNamePaths(List<TreeNode> list, String parentPath){
		if(list != null && list.size() > 0){
			for(TreeNode treeNode : list){
				treeNode.setNamePath(String.format("%s->%s", parentPath, treeNode.getName()));
				if(treeNode.getChildrenNode() != null && treeNode.getChildrenNode() != null){
					setNamePaths(treeNode.getChildrenNode(), treeNode.getNamePath());
				}
			}
		}
	}

	public void setChildrenNode(List<TreeNode> childrenNode) {
		this.childrenNode = childrenNode;
	}

	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append(" [");
		sb.append("menuId").append("='").append(getMenuId()).append("', ");
		sb.append("name").append("='").append(getName()).append("', ");
		sb.append("ctrl").append("='").append(getCtrl()).append("', ");
		sb.append("sequence").append("='").append(getSequence()).append("', ");
		sb.append("icon").append("='").append(getIcon()).append("', ");
		sb.append("desc").append("='").append(getDesc()).append("'");
		sb.append("]");
		return sb.toString();
	}

}

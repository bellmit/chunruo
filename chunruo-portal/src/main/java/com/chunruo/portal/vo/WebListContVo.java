package com.chunruo.portal.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.chunruo.core.util.FileIO;
import com.chunruo.core.util.StringUtil;

public class WebListContVo implements Serializable{
	private static final long serialVersionUID = 4211658621800735538L;
	public final static transient Log log = LogFactory.getLog(WebListContVo.class);
	public final static String contStrSplit = "X_BD_SPLIT"; 
	private String content;
	private List<WebViewFileVo> imageList = new ArrayList<WebViewFileVo> ();
	private List<WebViewFileVo> videoList = new ArrayList<WebViewFileVo> ();
	private List<WebViewFileVo> productList = new ArrayList<WebViewFileVo> ();
	
	public static List<String> fileTypeList = new ArrayList<String> ();
	
	static{
		fileTypeList.add("png");
		fileTypeList.add("jpg");
		fileTypeList.add("gif");
		fileTypeList.add("jpeg");
		fileTypeList.add("mp4");
	}
	
	/**
	 * 资源解析成对象
	 * @param htmlContent
	 * @return
	 */
	public static WebListContVo getHtmlVo(String htmlContent){
		WebListContVo htmlVo = new WebListContVo ();
		try{
			Parser parser = new Parser(String.format("<body>%s</body>", htmlContent));
			NodeIterator nodeIterator = parser.elements();
			while(nodeIterator.hasMoreNodes()){
				Node node = nodeIterator.nextNode();
				nextNodes(node, null, htmlVo);
				htmlVo.setContent(node.toPlainTextString());
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return htmlVo;
	}
	
	/**
	 * 去掉超链接标签
	 * @param parentNodeList
	 */
	private static void linkTagParser(NodeList parentNodeList){
		if(parentNodeList != null && parentNodeList.size() > 0){
			for(int i = 0; i < parentNodeList.size(); i ++){
				Node node = parentNodeList.elementAt(i);
				if(node instanceof LinkTag){
					// 去掉超链接标签
					NodeList childrenNode = node.getChildren();
					if(childrenNode != null && childrenNode.size() > 0){
						List<Node> addNodeList = new ArrayList<Node> ();
						for(int j = 0; j < childrenNode.size(); j ++){
							Node tarNode = childrenNode.elementAt(j);
							if(tarNode instanceof ImageTag){
								addNodeList.add(tarNode);
							}
						}

						parentNodeList.remove(node);
						if(addNodeList != null && addNodeList.size() > 0){
							for(Node addNode : addNodeList){
								parentNodeList.add(addNode);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 去掉超链接标签
	 * @param parentNodeList
	 */
	private static void videoTagParser(Node node, WebListContVo htmlUtil){
		boolean isChange = false;
		NodeList childNodeList = node.getChildren();
		if(childNodeList != null && childNodeList.size() > 0){
			List<Node> addNodeList = new ArrayList<Node> ();
			for(int i = 0; i < childNodeList.size(); i ++){
				Node childNode = childNodeList.elementAt(i);
				String nodeName = "";
				if(childNode instanceof Tag){
					nodeName= ((Tag) childNode).getTagName();
					if(StringUtil.compareObject("video", nodeName.toLowerCase())){
						isChange = true;
						Tag tag = (Tag) childNode;
						String srcCont = tag.getAttribute("data-src");
						if(!StringUtil.isNull(srcCont)){
							String strTextNode = String.format("<!--VIDEO#%s-->", htmlUtil.getVideoList().size());
							String videoURL = StringUtil.null2Str(srcCont);
							String fileType = "";
							if(videoURL.length() > 0 && videoURL.lastIndexOf(".") > 0){
								fileType = StringUtil.null2Str(videoURL.substring(videoURL.lastIndexOf(".") + 1)).toLowerCase();
								if(!fileTypeList.contains(fileType)){
									fileType = null;
								}
							}
							WebViewFileVo fileVo = new WebViewFileVo();
							fileVo.setRef(strTextNode);
							fileVo.setType(fileType);
							fileVo.setSrc(videoURL);
							fileVo.setShowType(2);
							htmlUtil.getVideoList().add(fileVo);
							
							TextNode textNode = new TextNode (String.format("%s%s%s", WebListContVo.contStrSplit, strTextNode, WebListContVo.contStrSplit));
							addNodeList.add(textNode);
						}
						continue;
					}
				}
				if(!StringUtil.compareObject("source", nodeName.toLowerCase()))
					addNodeList.add(childNode);
			}
			
			if(isChange){
				childNodeList.removeAll();
				if(addNodeList != null && addNodeList.size() > 0){
					for(Node addNode : addNodeList){
						childNodeList.add(addNode);
					}
				}
				
			}
		}
	}
	
	/**
	 * 图片视频资源递归解析
	 * @param node
	 * @param htmlVo
	 */
	private static void nextNodes(Node node, NodeList parentNodeList, WebListContVo htmlUtil){
		videoTagParser(node, htmlUtil);
		if(parentNodeList != null && parentNodeList.size() > 0){
			if(node instanceof TagNode && (node.getText().endsWith("em"))){
				// 去掉提示标签
				List<Node> addNodeList = new ArrayList<Node> ();
				for(int i = 0; i < parentNodeList.size(); i ++){
					Node tarNode = parentNodeList.elementAt(i);
					if(tarNode instanceof ImageTag){
						addNodeList.add(tarNode);
					}
				}
				parentNodeList.removeAll();
				if(addNodeList != null && addNodeList.size() > 0){
					for(Node addNode : addNodeList){
						parentNodeList.add(addNode);
					}
				}
				return;
			}
		}
		
		NodeList nodeList = node.getChildren();
		if(nodeList != null && nodeList.size() > 0){
			linkTagParser(nodeList);
			List<Node> deleteNodeList = new ArrayList<Node> ();
			for(int i = 0; i < nodeList.size(); i ++){
				Node childNode = nodeList.elementAt(i);
				if (childNode instanceof TableTag){
					TableTag  table = (TableTag) childNode;
					if (StringUtil.compareObject("porduct", table.getAttribute("class"))){
						NodeList childList = childNode.getChildren();
						for (int j = 0; j < childList.size(); j++){
							Node tableChildNode = childList.elementAt(j);
							if (tableChildNode instanceof TableRow){
								NodeList tableList = tableChildNode.getChildren();
								WebViewFileVo fileVo = new WebViewFileVo();
								for (int k = 0; k < tableList.size() ; k++){
									if (tableList.elementAt(k) instanceof TableColumn){
										NodeList columnList = tableList.elementAt(k).getChildren();
										
										for (int l=0 ; l<columnList.size() ; l++){
											if (columnList.elementAt(l) instanceof ImageTag){
												String imageURL = StringUtil.null2Str(((ImageTag) columnList.elementAt(l)).getImageURL());
												fileVo.setImgURL(imageURL);
											}else if (columnList.elementAt(l) instanceof Tag){
												Tag tag = (Tag)columnList.elementAt(l);
												if (StringUtil.compareObject("price", tag.getAttribute("class"))){
													String price = tag.getChildren().elementAt(0).getText();
													fileVo.setPrice(price);
												}else if (StringUtil.compareObject("brand", ((Tag) columnList.elementAt(l)).getAttribute("class"))){
													String brand = tag.getChildren().elementAt(0).getText();
													fileVo.setBrand(brand);
												}else if (StringUtil.compareObject("title", ((Tag) columnList.elementAt(l)).getAttribute("class"))){
													String title = tag.getChildren().elementAt(0).getText();
													fileVo.setTitle(title);
												}
											}
											
										}
									}
									
								}
								if (fileVo != null && (!StringUtil.isNull(fileVo.getPrice()) 
										|| !StringUtil.isNull(fileVo.getImgURL()) 
										|| !StringUtil.isNull(fileVo.getBrand())
										|| !StringUtil.isNull(fileVo.getTitle())
										)){
									String strTextNode = String.format("<!--PRODUCT#%s-->", htmlUtil.getProductList().size());
									TextNode textNode = new TextNode (String.format("%s%s%s", WebListContVo.contStrSplit, strTextNode, WebListContVo.contStrSplit));
									nodeList.add(textNode);
									fileVo.setRef(strTextNode);
									fileVo.setShowType(3);
									htmlUtil.getProductList().add(fileVo);
								}
							}
							
						}
						
					}
					deleteNodeList.add(childNode);
				}
					
				if(childNode instanceof ImageTag){
					try{
						ImageTag imageTag = (ImageTag) nodeList.elementAt(i);
						String strTextNode = String.format("<!--IMG#%s-->", htmlUtil.getImageList().size());
						TextNode textNode = new TextNode (String.format("%s%s%s", WebListContVo.contStrSplit, strTextNode, WebListContVo.contStrSplit));
						deleteNodeList.add(childNode);
						nodeList.add(textNode);

						String imageURL = StringUtil.null2Str(imageTag.getImageURL());
						String fileType = "";
						if(imageURL.length() > 0 && imageURL.lastIndexOf(".") > 0){
							fileType = StringUtil.null2Str(imageURL.substring(imageURL.lastIndexOf(".") + 1)).toLowerCase();
							if(!fileTypeList.contains(fileType)){
								fileType = null;
							}
						}
						
						WebViewFileVo fileVo = new WebViewFileVo();
						fileVo.setRef(strTextNode);
						fileVo.setType(fileType);
						fileVo.setShowType(1);
						fileVo.setSrc(imageURL);
						htmlUtil.getImageList().add(fileVo);
					}catch(Exception e){
						log.error(e.getMessage());
					}
				}else if(childNode instanceof LinkTag){
					deleteNodeList.add(childNode);
				}else{
					nextNodes(nodeList.elementAt(i), nodeList, htmlUtil);
				}
			}
			
			// 删除真实的IMAGE节点
			if(deleteNodeList != null && deleteNodeList.size() > 0){
				for(Node childNode : deleteNodeList){
					try{
						nodeList.remove(childNode);
					}catch(Exception e){
						continue;
					}
				}
			}
		}
	}
	
	public static int getHtmlContType(String content){
		if(!StringUtil.isNullStr(content) && content.startsWith("<!--VIDEO")){
			return WebHtmlContVo.CONT_TYPE_VIDEO;
		}else if(!StringUtil.isNullStr(content) && content.startsWith("<!--IMG")){
			return WebHtmlContVo.CONT_TYPE_IMAGE;
		}else if (!StringUtil.isNullStr(content) && content.startsWith("<!--PRODUCT")){
			return WebHtmlContVo.CONT_TYPE_PRODUCT;
		}
		return WebHtmlContVo.CONT_TYPE_HTML;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public List<WebViewFileVo> getImageList() {
		return imageList;
	}

	public void setImageList(List<WebViewFileVo> imageList) {
		this.imageList = imageList;
	}

	public List<WebViewFileVo> getVideoList() {
		return videoList;
	}

	public void setVideoList(List<WebViewFileVo> videoList) {
		this.videoList = videoList;
	}
	
	public List<WebViewFileVo> getProductList() {
		return productList;
	}

	public void setProductList(List<WebViewFileVo> productList) {
		this.productList = productList;
	}
	
	
}


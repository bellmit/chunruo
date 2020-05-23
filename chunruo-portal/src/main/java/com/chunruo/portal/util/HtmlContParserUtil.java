package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.vo.WebHtmlContVo;
import com.chunruo.portal.vo.WebListContVo;
import com.chunruo.portal.vo.WebViewFileVo;

/**
 * 客户端webview内容转换成list对象
 * @author chunruo
 *
 */
public class HtmlContParserUtil  {
	
	/**
	 * 解析HTML转换成对象
	 * @param htmlCont
	 * @return
	 */
	public static List<WebHtmlContVo> getHtmlContList(String htmlCont, String network, Long objectId){
		List<WebHtmlContVo> htmlContList = new ArrayList<WebHtmlContVo> ();
		WebListContVo htmlContVo = WebListContVo.getHtmlVo(StringUtil.null2Str(htmlCont));
		if(htmlContVo != null && !StringUtil.isNullStr(htmlContVo.getContent())){
			// 图片地址处理
			if(htmlContVo.getImageList() != null && htmlContVo.getImageList().size() > 0){
				ExecutorService executor = Executors.newFixedThreadPool(htmlContVo.getImageList().size());  
				List<Future<WebViewFileVo>> futureList = new ArrayList<Future<WebViewFileVo>>(); 
				
				int index = 0;
				Map<Integer, WebViewFileVo> webViewFileMap = new HashMap<Integer, WebViewFileVo> ();
				for(WebViewFileVo fileVo : htmlContVo.getImageList()){
					index = index + 1;
					fileVo.setUniqueId(index);
					webViewFileMap.put(index, fileVo);
					NetImageDownloadCallable callable = new NetImageDownloadCallable(fileVo, objectId, network);
					Future<WebViewFileVo> future = executor.submit(callable);
					futureList.add(future);
				}
				 
				// 关闭线程
				executor.shutdown();  
				
				// 获取所有并发任务的运行结果
				for (Future<WebViewFileVo> future : futureList) { 
					try {
						WebViewFileVo tempFileVo = future.get();
						if(tempFileVo != null && webViewFileMap.containsKey(tempFileVo.getUniqueId())){
							WebViewFileVo realFileVo = webViewFileMap.get(tempFileVo.getUniqueId());
							realFileVo.setType(realFileVo.getType());
							realFileVo.setSrc(realFileVo.getSrc());
							realFileVo.setPixel(realFileVo.getPixel());
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				} 
			}
			
			String[] htmlArrays = htmlContVo.getContent().split(WebListContVo.contStrSplit);
			if(htmlArrays != null && htmlArrays.length > 0){
				for(int i = 0; i < htmlArrays.length; i ++){
					String content = StringUtil.null2Str(htmlArrays[i]);
					if(!StringUtil.isNull(content)){
						int contType = WebListContVo.getHtmlContType(content);
						WebHtmlContVo webHtmlContVo = new WebHtmlContVo ();
						webHtmlContVo.setContType(contType);
						webHtmlContVo.setContent(content.replaceAll("\n\n\n\n", "\n\n").replaceAll("\n\n\n", "\n\n").replaceAll("&nbsp;", " "));
						webHtmlContVo.setWebViewFileVo(getWebViewFileVo(htmlContVo, contType, content));
						htmlContList.add(webHtmlContVo);
					}
				}
			}
		}
		return htmlContList;
	}
	
	/**
	 * 解析内容对象
	 * @param htmlContVo
	 * @param contType
	 * @param content
	 * @return
	 */
	private static WebViewFileVo getWebViewFileVo(WebListContVo htmlContVo, int contType, String content){
		if(StringUtil.compareObject(WebHtmlContVo.CONT_TYPE_IMAGE, contType)){
			if(htmlContVo.getImageList() != null && htmlContVo.getImageList().size() > 0){
				for(WebViewFileVo webViewFileVo : htmlContVo.getImageList()){
					if(StringUtil.compareObject(webViewFileVo.getRef(), content)){
						return webViewFileVo;
					}
				}
			}
		}else if(StringUtil.compareObject(WebHtmlContVo.CONT_TYPE_VIDEO, contType)){
			if(htmlContVo.getVideoList() != null && htmlContVo.getVideoList().size() > 0){
				for(WebViewFileVo webViewFileVo : htmlContVo.getVideoList()){
					if(StringUtil.compareObject(webViewFileVo.getRef(), content)){
						return webViewFileVo;
					}
				}
			}
		}else if (StringUtil.compareObject(WebHtmlContVo.CONT_TYPE_PRODUCT, contType)){
			if (htmlContVo.getProductList() != null && htmlContVo.getProductList().size() > 0){
				for(WebViewFileVo webViewFileVo : htmlContVo.getProductList()){
					if(StringUtil.compareObject(webViewFileVo.getRef(), content)){
						return webViewFileVo;
					}
				}
			}
		}
		return null;
	}
	
//	public static void main(String[] args) {
//		String str = "<p><a href=\"http://www.jikeduo.com.cn/wap/page.php?id=271\" target=\"_blank\"><img src=\"http://www.jikeduo.com.cn/upload/images/000/000/001/201607/5798502b92171.jpg\"/></a> &nbsp; &nbsp;<img src=\"http://www.jikeduo.com.cn/upload/images/000/000/005/201607/577b7c3f56e37.jpg\" data-pinit=\"registered\"/><img src=\"http://www.jikeduo.com.cn/upload/images/000/000/005/201607/577b7c3fafe03.jpg\" data-pinit=\"registered\"/><img src=\"http://www.jikeduo.com.cn/upload/images/000/000/005/201607/577b7c3f7be9e.jpg\" data-pinit=\"registered\"/><img src=\"http://www.jikeduo.com.cn/upload/images/000/000/005/201607/577b7c411e09a.jpg\" data-pinit=\"registered\"/><img src=\"http://www.jikeduo.com.cn/upload/images/000/000/005/201607/577b7c407eaa5.jpg\"/><img src=\"http://www.jikeduo.com.cn/upload/images/000/000/005/201607/577b7c42261d7.jpg\" data-pinit=\"registered\"/><img src=\"http://www.jikeduo.com.cn/upload/images/000/000/005/201607/577b7c4121625.jpg\"/><img src=\"http://www.jikeduo.com.cn/upload/images/000/000/005/201607/577b7c41b63b8.jpg\"/></p>";
//		WebListContVo htmlContVo = WebListContVo.getHtmlVo(StringUtil.null2Str(str));
//	}
}

package com.chunruo.portal.util;

import java.util.concurrent.Callable;

import com.chunruo.portal.vo.WebViewFileVo;

public class NetImageDownloadCallable implements Callable<WebViewFileVo> {
	private WebViewFileVo fileVo;
	private Long objectId;
	private String network;

	public NetImageDownloadCallable(WebViewFileVo fileVo, Long objectId, String network){
		this.fileVo = fileVo;
		this.objectId = objectId;
		this.network = network;
	}

	@Override
	public WebViewFileVo call() throws Exception {
		try{
//			if(this.fileVo == null || StringUtil.isNull(this.fileVo.getSrc())){
//				// 没有资源地址不做任何处理
//				return this.fileVo;
//			}
//			
//			fileVo.setIsLocal(true);
//			String imageURL = StringUtil.null2Str(fileVo.getSrc()).replace("\\/", "/");
//			if(imageURL.startsWith("http://") || imageURL.startsWith("https://")){
//				fileVo.setIsLocal(false);
//				String fileName = Md5Util.md5Code(imageURL) + "." + StringUtil.null2Str(fileVo.getType());
//				String filePath = UploadUtil.BASE_NET_DIR + CoreUtil.idToNamePath(objectId) + "/" + fileName;
//				if(network != null && !StringUtil.isNull(network)){
//					String networkdir = String.format("/%s/", network);
//					filePath = UploadUtil.BASE_NET_DIR + networkdir + CoreUtil.idToNamePath(objectId) + "/" + fileName;
//				}
//
//				File file = new File(Constants.SERVER_REAL_PATH + filePath);
//				if(!file.exists()){
//					// 网络下载图片
//					boolean result = FileUtil.downloadImage(imageURL, Constants.SERVER_REAL_PATH, filePath);
//					if(result){
//						fileVo.setSrc(filePath);
//						if(fileVo.getType() == null || StringUtil.isNull(fileVo.getType())){
//							fileVo.setType(FileUtil.getReaderImageType(file.getPath()));
//						}
//					}
//				}else{
//					// 已缓存的图片
//					fileVo.setSrc(filePath);
//					if(fileVo.getType() == null || StringUtil.isNull(fileVo.getType())){
//						fileVo.setType(FileUtil.getReaderImageType(file.getPath()));
//					}
//				}
//			}
//
//			String realImageURL = StringUtil.null2Str(fileVo.getSrc()).replace("\\/", "/");
//			ImageUtilCacheManager imageUtilCacheManager = (ImageUtilCacheManager) Constants.ctx.getBean("imageUtilCacheManager");
//			ImageVo imageVo = imageUtilCacheManager.get(realImageURL);
//			if(imageVo != null 
//					&& imageVo.getHeight() > 0 
//					&& imageVo.getWidth() > 0){
//				fileVo.setPixel(String.format("%s*%s", imageVo.getWidth(), imageVo.getHeight()));
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return this.fileVo;
	}
}

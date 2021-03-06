package com.chunruo.core.util.ftp;

import java.io.File;
import java.io.FileFilter;

/*
 * 1.用于找出合成后生成的分页文件，匹配规则为文件名相同或添加了_x的分页文件
 * 例如：index.html,index_1.html,index_2.html,
 * 例如：1.html,1_1.html
 * 2.找出资源文件,匹配规则不考虑后缀名
 * 例如: 12.jpg 12.png  全匹配 12.*
 */
public class PagingFileFilter implements FileFilter {
	private String fname;

	public PagingFileFilter(String fname) {
		this.fname = fname;
	}

	/*
	 * 
	 * filename包括以下几种情况:
	 * 
	 * 1) xxx*All.
	 *   为这两种情况:xxx.ext, xxx_.ext
	 * 2) xxx.*
	 *    仅一种情况: xxx.ext
	 * 3) xxx*
	 *    中间模糊匹配: 
	 * 4) xxx
	 *   
	 */

	public boolean accept(File fileName) {
		if ((fileName == null) || (this.fname == null)) {
			return false;
		}
		if (this.fname.indexOf("*All.*") > 0) {
			String[] names = this.fname.split("\\*All.*");
			if (names.length > 0) {
				if (fileName.getName().startsWith(names[0] + "."))
					return true;
				if (fileName.getName().startsWith(names[0] + "_"))
					return true;
			}
		} else if (this.fname.indexOf("*All.") > 0) {
			String[] names = this.fname.split("\\*All.");
			if (names.length > 0) {
				if (fileName.getName().startsWith(names[0] + "."))
					return true;
				if (fileName.getName().startsWith(names[0] + "_"))
					return true;
			}
		} else if (this.fname.indexOf(".*") > 0) {
			String[] names = this.fname.split("[.*]");
			if (fileName.getName().startsWith(names[0] + "."))
				return true;
		} else if (this.fname.indexOf("*") <= 0){
			//暂时屏蔽中间模糊的情况
//			String[] names = fname.split("\\*");
//			if (fileName.getName().startsWith(names[0])
//					&& fileName.getName().endsWith(names[1]))
//				return true;
		}
		return false;
	}
}


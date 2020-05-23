package com.chunruo.core.util;

import java.io.File;

public class FileFilter implements java.io.FileFilter {

	String ext;
	public FileFilter(String ext) {
		this.ext = ext;
	}

	public boolean accept(File pathname) {
		return pathname.getName().endsWith(ext)?true:false;
	}

}


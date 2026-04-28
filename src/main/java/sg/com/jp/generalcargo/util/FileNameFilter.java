package sg.com.jp.generalcargo.util;

import java.io.File;

/*
 * FileNameFilter.java
 *
 * Created on July 7, 2001, 1:34 PM
 */

/**
 *
 * @author  Administrator
 * @version 
 */
import java.io.FilenameFilter;

//package sg.com.ncs.util.File;
public class FileNameFilter implements FilenameFilter {
	private String fileExt;

	/** Creates new FileNameFilter */
	public FileNameFilter(String fileExt) {
		int index = fileExt.indexOf('.');
		if (index != -1) {
			this.fileExt = fileExt.substring(index + 1);
		} else {
			this.fileExt = fileExt;
		}
	}

	public boolean accept(File file, String filename) {
		int index = filename.indexOf('.');
		String ext = filename.substring(index + 1);
		if (fileExt.equalsIgnoreCase(ext)) {
			return true;
		}
		return false;
	}

}

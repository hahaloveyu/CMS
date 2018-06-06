package com.toptime.cmssync.util;

import java.io.File;

public class FileUtil {
	
	/**
	 * 按照路径拆分建立目录
	 * 
	 * @param localdir
	 *            物理路径信息
	 * @return 操作状态
	 */
	public static String mkdir(String localdir) {

		File dirFile = new File(localdir);
		try {
			boolean bFile = dirFile.exists();
			if (bFile == true) {
			} else {
				bFile = dirFile.mkdirs();
				if (bFile == true) {
				} else {
					// System.exit(1);
					System.out.println("目录创建失败");
				}
			}
		} catch (Exception e) {
		}

		return localdir;
	}

	/**
	 * 遍历目录下所有文件
	 * 
	 * @param dir
	 *            目录
	 * @return
	 */
	public String[] getFilelist(String dir) {
		// List<String> list = null;
		String[] fs = null;
		try {
			File file = new File(dir);
			fs = file.list();

			for (int i = 0; i < fs.length; i++) {
				/*
				 * if (fs[i].indexOf(".") != -1) { //System.out.println(fs[i]);
				 * list.add(fs[i]); }
				 */
				fs[i] = dir + "/" + fs[i];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return fs;

	}
	
	/**
	 * <B>判读文件路径是否存在</B>
	 * 
	 * @param filePath
	 * @return boolean
	 */
	public static boolean fileExist(String filePath) {
		File f1 = new File(filePath);
		if (!f1.exists())
			return false;
		if (f1.canRead())
			return true;
		return false;
	}
	
	/**
	 * 判断文件路径是相对还是绝对
	 * @param path
	 * @return
	 */
	public static boolean isAbsolutePath(String path) {
		 if (path.startsWith("/") || path.indexOf(":") > 0) {
		  return true;
		 }
		 return false;
	}


}

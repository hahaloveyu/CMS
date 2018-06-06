package com.toptime.cmssync.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <B>读写配置文件</B><br>
 * 配置文件为UTF-8格式
 * 
 * @author Tomws
 * @since 2009-12-11
 */
public class ConfigFile {

	/**
	 * 从配置文档中读取变量的值 配置文件为utf-8格式
	 * 
	 * @param section
	 *            要获取的变量所在段名称
	 * @param variable
	 *            要获取的变量名称
	 * @param defaultValue
	 *            变量名称不存在时的默认值
	 * @return 变量的值
	 * @throws IOException
	 *             抛出文档操作可能出现的io异常
	 */
	public static String getProfileString(String configFileName, String section, String variable, String defaultValue) {
		String filename = CommonProperty.configFilePath + configFileName;
		String value = null;

		BufferedReader bufferedReader = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try {
			fis = new FileInputStream(filename);
			isr = new InputStreamReader(fis, CommonProperty.configFileEncoding);
			bufferedReader = new BufferedReader(isr);
			value = getProfileString(bufferedReader, section, variable, defaultValue);
		} catch (Exception e) {
			return defaultValue;
		} finally {
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (Exception e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (Exception e) {
				}
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
					bufferedReader = null;
				} catch (Exception e) {
				}
			}
		}

		return value;
	}

	public static String getProfileString(BufferedReader bufferedReader, String section, String variable, String defaultValue) {
		String strLine, value = "";

		boolean isInSection = false;
		try {
			while ((strLine = bufferedReader.readLine()) != null) {

				strLine = strLine.trim();
				int indexSign = strLine.indexOf("#");
				if (indexSign > -1 && indexSign < 2) {
					continue;
				}
				Pattern p;
				Matcher m;
				p = Pattern.compile("\\[.*\\]");
				m = p.matcher((strLine));
				if (m.matches()) {
					p = Pattern.compile("\\[" + section + "\\]");
					m = p.matcher(strLine);
					if (m.matches()) {
						isInSection = true;
					} else {
						isInSection = false;
					}
					continue;
				}
				if (isInSection == true) {
					strLine = strLine.trim();
					String[] strArray = strLine.split("=");
					if (strArray.length == 1) {
						value = strArray[0].trim();
						if (value.equalsIgnoreCase(variable)) {
							value = "";
							return value;
						}
					} else if (strArray.length > 1) {
						value = strArray[0].trim();
						if (value.equalsIgnoreCase(variable)) {
							value = strLine.substring(strLine.indexOf("=") + 1).trim();
							return value;
						}
					}
					/**
					 * else if (strArray.length == 2) { value =
					 * strArray[0].trim(); if (value.equalsIgnoreCase(variable))
					 * { value = strArray[1].trim(); return value; } }
					 */
				}// if
			}// while
		} catch (Exception e) {
			try {
				bufferedReader.close();
			} catch (Exception e1) {
			}
		} finally {
		}
		return defaultValue;
	}

	/**
	 * 修改配置文档中变量的值
	 * 
	 * @param filePath
	 *            配置文档的路径
	 * @param section
	 *            要修改的变量所在段名称
	 * @param variable
	 *            要修改的变量名称
	 * @param value
	 *            变量的新值
	 * @throws IOException
	 *             抛出文档操作可能出现的io异常
	 */
	public static boolean setProfileString(String configFileName, String section, String variable, String value) {
		String fileContent, allLine, strLine, newLine;
		String getValue;
		boolean isInSection = false;
		fileContent = "";
		File ftest = new File(CommonProperty.configFilePath + configFileName);
		if (!ftest.exists()) {
			try {
				if (!ftest.createNewFile()) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}

		}
		if (!ftest.canWrite()) {
			return false;
		}
		if (!ftest.canRead()) {
			return false;
		}

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader bufferedReader = null;
		OutputStreamWriter out = null;
		try {
			fis = new FileInputStream(CommonProperty.configFilePath + CommonProperty.fileSep + configFileName);
			isr = new InputStreamReader(fis, CommonProperty.configFileEncoding);
			bufferedReader = new BufferedReader(isr);
			while ((allLine = bufferedReader.readLine()) != null) {
				allLine = allLine.trim();
				// System.out.println("allLine == " + allLine);
				strLine = allLine;
				Pattern p;
				Matcher m;
				p = Pattern.compile("\\[.+\\]");
				m = p.matcher((strLine));
				if (m.matches()) {
					// if isInSection 增加1行，填补剩余内容
					if (isInSection) {
						fileContent += allLine + "\r\n";
						break;
					}
					p = Pattern.compile("\\[" + section + "\\]");
					m = p.matcher(strLine);
					if (m.matches()) {
						isInSection = true;
					} else {
						isInSection = false;
					}
					fileContent += allLine + "\r\n";
					continue;
				}
				if (isInSection == true) {
					strLine = strLine.trim();
					String[] strArray = strLine.split("=");
					getValue = strArray[0].trim();
					if (getValue.equalsIgnoreCase(variable)) {
						break;
					}
				}
				fileContent += allLine + "\r\n";
			}
			if (!isInSection) {
				// 新增Section
				newLine = "[" + section + "]";
				fileContent += "\r\n" + newLine + "\r\n";
			}
			// 下面一段是增加新行，填补剩余内容
			newLine = variable + " = " + value + " ";
			fileContent += newLine + "\r\n";
			while ((allLine = bufferedReader.readLine()) != null) {
				fileContent += allLine + "\r\n";
			}
			bufferedReader.close();
		} catch (IOException ex) {
			try {
				bufferedReader.close();
			} catch (Exception e2) {
				return false;
			}
			return false;
		} finally {
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (Exception e) {
				}
			}
			if (isr != null) {
				try {
					isr.close();
					isr = null;
				} catch (Exception e) {
				}
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
					bufferedReader = null;
				} catch (Exception e) {
				}
			}
		}

		// BufferedWriter bufferedWriter=null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(CommonProperty.configFilePath + configFileName), CommonProperty.configFileEncoding);
			out.write(fileContent);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {

			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e1) {
				}
			}
		}
	}

}

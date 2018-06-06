package com.toptime.cmssync.common.core;

import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.entity.SystemInfo;

import sun.rmi.runtime.Log;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TsMethod {

	private static Logger logger = Logger.getLogger(TsMethod.class);

	private static DefaultHttpClient httpClient;

	static {
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setMaxTotal(100); // 连接池里的最大连接数
		cm.setDefaultMaxPerRoute(20); // 每个路由的默认最大连接数
		httpClient = new DefaultHttpClient(cm);
	}

	/**
	 * 将数据文件进行索引Userver
	 *
	 * @param si
	 * @param file
	 * @param dbName
	 */
	public static boolean importDataToUserver(SystemInfo si, File file, String dbName) {
		logger.info(
				"importToUserver --- indexHost:" + si.getTsHost() + "	indexPort:" + si.getTsIndexPort() + "	begin");
		boolean flag = false;
		// 如果文件大于设定的文件最大值
		if (file.length() > si.getMaxDataFileSize() * 1024L) {
			flag = readBigFile(si, file, dbName);
		} else {
			String data = new String();
			// 读取文件为String类型
			data = readFile(file.getAbsolutePath());

			logger.info("begin import the file " + file.getAbsolutePath() + " to the database " + dbName
					+ " at the host " + si.getTsHost());
			// 以post方式向数据库中添加记录,返回入库状态
			boolean status = dreAddDataToUserver(si, data, dbName);

			if (status) { // 入库成功
				logger.info("import the file " + file.getAbsolutePath() + " to the database " + dbName + " at the host "
						+ si.getTsHost() + " is success");
				flag = true;
				// 移除文件到备份目录
				String filePath = file.getAbsolutePath();
				String backupDir = CommonProperty.DATA_BACKUP_PATH;
				if (filePath.indexOf(CommonProperty.DATA_PATH) + CommonProperty.DATA_PATH.length() < filePath
						.lastIndexOf(CommonProperty.fileSep)) {
					backupDir = CommonProperty.DATA_BACKUP_PATH + filePath.substring(
							filePath.indexOf(CommonProperty.DATA_PATH) + CommonProperty.DATA_PATH.length(),
							filePath.lastIndexOf(CommonProperty.fileSep));
				}
				removeFile(file, backupDir);
			}
		}
		logger.info(
				"importToUserver --- indexHost:" + si.getTsHost() + "	indexPort:" + si.getTsIndexPort() + "	end");
		return flag;
	}

	/**
	 * 读取文件，返回要入索引的数据
	 *
	 * @param filePath
	 *            需要入库的文件路径
	 * @return
	 */
	public static String readFile(String filePath) {
		logger.info("开始读取文件" + filePath);
		String line = null;
		StringBuffer sb = new StringBuffer();

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader in = null;

		try {
			fis = new FileInputStream(filePath);
			isr = new InputStreamReader(fis, "utf8");
			in = new BufferedReader(isr);

			try {
				while ((line = in.readLine()) != null) {
					// System.out.println(line);
					sb.append(line);
					sb.append(CommonProperty.lineSep);
				}

			} catch (IOException e) {
				logger.error("读文件错误" + e.toString());// e1.printStackTrace();;
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("文件编码错误" + e.toString());// e.printStackTrace();;
		} catch (FileNotFoundException e) {
			logger.error("文件未找到：" + e.toString());// e.printStackTrace();;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (Exception e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}

		}

		logger.info("文件读取完成");
		return sb.toString();
	}

	/**
	 * 读取大文件,并执行数据入库Userver
	 *
	 * @param si
	 * @param file
	 * @param dbName
	 * @return
	 */
	private static boolean readBigFile(SystemInfo si, File file, String dbName) {

		logger.info("开始读取大文件：" + file.getAbsolutePath());
		String line = null;
		// 要入库的数据字符串
		StringBuffer sb = new StringBuffer();
		BufferedReader in = null;
		// 记录数据量，到达maxPostNum执行一次入库
		int count = 0;
		// 一个大文件是否在中间入库失败跳出的标志
		boolean isBreak = false;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf8"));
			// 一个大文件的入库次数
			int i = 0;
			try {
				while ((line = in.readLine()) != null) {

					if (line.startsWith("#DREENDDOC")) {
						count++;
					}
					sb.append(line + CommonProperty.lineSep);

					if (count >= si.getMaxPostNum()) { // 执行一次入库
						i++;
						boolean status = dreAddDataToUserver(si, sb.toString(), dbName);

						if (status) { // 入库成功
							logger.info("the " + i + " time's import the file " + file.getAbsolutePath()
									+ " to the database " + dbName + " at the host " + si.getTsHost() + " is success");
						} else {
							isBreak = true;
							logger.info("the " + i + " time's import the file " + file.getAbsolutePath()
									+ " to the database " + dbName + " at the host " + si.getTsHost() + " is failed");
							break;
						}

						sb.setLength(0);
						count = 0;
					}
				}

				// 入库剩余部分
				if (!isBreak) {
					if (sb.toString().length() > 0) {
						i++;
						boolean status = dreAddDataToUserver(si, sb.toString(), dbName);

						if (status) { // 入库成功
							logger.info("the " + i + " time's import the file " + file.getAbsolutePath()
									+ " to the database " + dbName + " at the host " + si.getTsHost() + " is success");
						} else {
							isBreak = true;
							logger.info("the " + i + " time's import the file " + file.getAbsolutePath()
									+ " to the database " + dbName + " at the host " + si.getTsHost() + " is failed");
						}
					}
				}
			} catch (IOException e) {
				logger.error("读文件错误" + e.toString());// e1.printStackTrace();;
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("文件编码错误" + e.toString());// e.printStackTrace();;
		} catch (FileNotFoundException e) {
			logger.error("文件未找到：" + e.toString());// e.printStackTrace();;
		} finally {

			try {
				in.close();
			} catch (IOException e) {
				logger.info(e.toString());// e.printStackTrace();;
			}
		}

		// 入库成功后，移除文件到备份目录
		if (!isBreak) {
			String filePath = file.getAbsolutePath();
			String backupDir = CommonProperty.DATA_BACKUP_PATH;
			if (filePath.indexOf(CommonProperty.DATA_PATH) + CommonProperty.DATA_PATH.length() < filePath
					.lastIndexOf(CommonProperty.fileSep)) {
				backupDir = CommonProperty.DATA_BACKUP_PATH + filePath.substring(
						filePath.indexOf(CommonProperty.DATA_PATH) + CommonProperty.DATA_PATH.length(),
						filePath.lastIndexOf(CommonProperty.fileSep));
			}
			removeFile(file, backupDir);
		}
		logger.info("读取大文件结束：" + file.getAbsolutePath());
		return !isBreak;
	}

	/**
	 * 以post方式向索引库中添加记录 用的是DREADDDATA这个命令
	 *
	 * @param si
	 * @param data
	 * @param dbName
	 * @return
	 */
	public static boolean dreAddDataToUserver(SystemInfo si, String data, String dbName) {
		// 入索引是否成功状态
		boolean status = false;

		if (si == null) {
			logger.error("SystemInfo为null，无法进行数据索引！");
			return status;
		}

		String url = "http://" + si.getTsHost() + ":" + si.getTsIndexPort() + "/DREADDDATA";

		HttpPost httpPost = new HttpPost(url);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// 将参数传入post方法中
		nvps.add(new BasicNameValuePair("Data", data));
		if (dbName != null) {
			nvps.add(new BasicNameValuePair("DREDbName", dbName));
		}
		try {

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			// 执行入索引
			HttpResponse response = httpClient.execute(httpPost);

			// 返回服务器响应
			int statusCode = response.getStatusLine().getStatusCode();

			// 从头中取出转向的地址
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				// 获取响应内容
				String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

				status = ParserMethod.parserDreAddData(responseString);

				// 销毁
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// httpClient.getConnectionManager().shutdown();
			httpPost.abort();
		}
		return status;
	}

	/**
	 * 根据reference删除索引 用的是DREDELETEREF 这个命令
	 *
	 * @param si
	 * @param reference
	 * @return
	 */
//	public static boolean deleteIndexFromUserver(SystemInfo si, String reference) {
	public static String deleteIndexFromUserver(SystemInfo si, String reference) {
		// 删除索引是否成功状态
		boolean status = false;

		if (si == null) {
			logger.error("SystemInfo为null，无法进行数据索引！");
//			return status;
			return "SystemInfo为null，无法进行数据索引！";
		}

		/**
		 * 获取要删除的稿件所在的索引库集合
		 */
		Set<String> set = getDataBaseMatch(si, reference);

		StringBuilder dbsBuilder = new StringBuilder();
		for (String databaseMatch : set) {

			dbsBuilder.append(databaseMatch + ",");
		}
		String dbNames = "";
		if (dbsBuilder.length() > 0) {
			// 将数据库参数转换为db1,db2,...,dbn格式
			dbNames = dbsBuilder.toString().substring(0, dbsBuilder.toString().length() - 1);
		}
		// TODO
		String url = "http://" + si.getTsHost() + ":" + si.getTsIndexPort() + "/DREDELETEREF";

		HttpPost httpPost = new HttpPost(url);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// 将参数传入post方法中 TODO
		nvps.add(new BasicNameValuePair("Docs", reference));
		
		// 可能存在的情况：索引库中没有增量接口中提供的要删除的稿件信息
		if (dbNames != null && dbNames != "") {
			nvps.add(new BasicNameValuePair("DreDbName", dbNames));
			logger.debug("要删除的数据基本信息:" + reference + "--->" + dbNames);
		} else {
//			return false;
			return "noDbs";
		}

		try {

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			// 执行post请求,获取响应对象
			HttpResponse response = httpClient.execute(httpPost);

			// 返回服务器响应状态码
			int statusCode = response.getStatusLine().getStatusCode();

			// 从头中取出转向的地址
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				// 获取响应内容
				String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
				// TODO
				status = ParserMethod.parserDreAddData(responseString);

				// 销毁
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// httpClient.getConnectionManager().shutdown();
			httpPost.abort();
		}
		//返回删除结果提示
//		return status;
		if(status){//稿件删除成功
			return "&"+dbNames;
		}else {
			return "false";
		}
	}

	/**
	 * 移动文件指定目录
	 *
	 * @param file
	 */
	private static void removeFile(File file, String backupDir) {
		File backFileDir = new File(backupDir);
		backFileDir.mkdirs();
		File backFile = new File(backupDir + CommonProperty.fileSep + file.getName());
		// 如果备份文件存在相同的文件名字，则重命名旧的文件名字
		if (backFile.exists()) {
			int i = 0;
			String renameFile = backupDir + CommonProperty.fileSep + file.getName() + "_" + i;
			File newFile = new File(renameFile);
			while (newFile.exists()) {
				i++;
				renameFile = backupDir + CommonProperty.fileSep
						+ file.getName().substring(0, file.getName().lastIndexOf(".")) + "_" + i
						+ file.getName().substring(file.getName().lastIndexOf("."));
				newFile = new File(renameFile);
			}
			backFile.renameTo(newFile);
		}
		logger.info("begin move the file : " + file.getAbsolutePath() + " to " + backFileDir.getAbsolutePath());
		try {
			FileUtils.moveFileToDirectory(file, backFileDir, true);
			logger.info("move the file " + file.getAbsolutePath() + " is success");
		} catch (IOException e) {
			logger.error("move file error : " + e.getMessage());
		}
	}

	/**
	 * 优化删除命令---先查询reference对应的稿件所在的索引库
	 * 
	 * @param si
	 * @param reference
	 * @return
	 */
	public static Set<String> getDataBaseMatch(SystemInfo si, String reference) {
		// 删除索引是否成功状态
		boolean status = false;
		// 将获取到的库名放到set集合中，方便去除重复库名
		HashSet<String> set = new HashSet<String>();
		if (si == null) {
			logger.error("SystemInfo为null，无法进行数据索引！");
			return null;
		}

		// 拼接要执行的url
		String url = "http://" + si.getTsHost() + ":" + si.getTsQueryPort() + "/Action=GetContent";

		HttpPost httpPost = new HttpPost(url);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// 将参数传入post方法中
		nvps.add(new BasicNameValuePair("Reference", reference));
		nvps.add(new BasicNameValuePair("DataBaseMatch", CommonProperty.systemInfo.getDeleteDbNames()));

		HttpEntity entity = null;
		try {

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			// 执行url
			HttpResponse response = httpClient.execute(httpPost);

			// 返回服务器响应
			int statusCode = response.getStatusLine().getStatusCode();

			// 从头中取出转向的地址
			if (statusCode == HttpStatus.SC_OK) {
				entity = response.getEntity();
				// 获取响应内容----responseString为将要删除的稿件的data格式信息。利用正则匹配“<DATABASE>库名</DATABASE>”可获取这些稿件所属的库，将库名排重后再组合删除语句
				String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
				// 正则表达式
				String regex = "<DATABASE>(.*?)</DATABASE>";
				Pattern compile = Pattern.compile(regex);
				Matcher matcher = compile.matcher(responseString);

				while (matcher.find()) {
					// 将匹配到的索引库名放到集合中
					set.add(matcher.group(1).trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 销毁
			try {
				if (entity != null) {
					EntityUtils.consume(entity);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			// httpClient.getConnectionManager().shutdown();
			httpPost.abort();
		}
		// 返回索引库集合
		return set;
	}

}

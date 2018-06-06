package com.toptime.cmssync.mission.job;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.common.core.TsMethod;
import com.toptime.cmssync.util.DateUtil;
import com.toptime.cmssync.util.StringUtils;

public class ImportDataJob implements Job {

	private static boolean isRuning = false;

	private static Logger logger = Logger.getLogger(ImportDataJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {

		if (!isRuning) {

			logger.info("import data job start  ...........................");
			isRuning = true;
			importData();
			isRuning = false;
			logger.info("import data job end    ...........................");
		} else {
			logger.info("last import data job is not finished .........");
		}
	}

	/**
	 * 数据入索引
	 */
	public static void importData() {

		logger.debug("触发时间：" + DateUtil.DateFormat(new Date(), "yyyy-MM-dd HH:mm:ss"));
		logger.debug("run dir at ：" + CommonProperty.userDir);
		// 数据文件存放目录的路径
		String dataDirPath = CommonProperty.DATA_PATH;
		File file = new File(dataDirPath);
		// 如果不存在此路径，进行创建
		if (!file.exists()) {
			file.mkdirs();
		}

		Date startDate = new Date();
		long start = System.currentTimeMillis();
		logger.info("任务开始时间 ：" + DateUtil.DateFormat(startDate, "yyyy-MM-dd HH:mm:ss"));

		try {
			postToServer(file);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		Date doneDate = new Date();
		long end = System.currentTimeMillis();
		logger.info("任务结束时间 : " + DateUtil.DateFormat(doneDate, "yyyy-MM-dd HH:mm:ss") + "  ,耗时:" + (end - start) / 1000 + "s");
	}

	/**
	 * 入索引到Server
	 * 
	 * @param dataDir
	 */
	private static void postToServer(File dataDir) {

		logger.debug("postToServer --- indexHost:" + CommonProperty.systemInfo.getTsHost() + "	indexPort:"
				+ CommonProperty.systemInfo.getTsIndexPort() + "	start=======================================");
		logger.debug("import file:" + dataDir.toString());
		long hostStart = System.currentTimeMillis();
		/**
		 * 遍历文件夹，进行入库
		 */
		iteratorDataDir(dataDir);

		long hostEnd = System.currentTimeMillis();
		logger.debug("耗时：" + (hostEnd - hostStart) / 1000 + "s  at the host " + CommonProperty.systemInfo.getTsHost());

		logger.debug("postToServer --- indexHost:" + CommonProperty.systemInfo.getTsHost() + "	indexPort:"
				+ CommonProperty.systemInfo.getTsIndexPort() + "	end=======================================");
	}

	/**
	 * 遍历data文件存放文件夹
	 * 
	 * @param file
	 * @param hostName
	 */
	private static void iteratorDataDir(File file) {

		// String currPath = file.getAbsolutePath();
		// File hostFile = new File(currPath);

		/**
		 * 列出所有文件夹
		 */
		File[] dbFileList = file.listFiles();

		/**
		 * 循环遍历文件夹
		 */
		for (File dbFile : dbFileList) {

			if (dbFile.isDirectory()) {// 遍历每个文件夹进行入库
				logger.info("开始遍历库文件夹" + dbFile.getName());
				if (!StringUtils.isEmpty(CommonProperty.systemInfo.getDbName())) {
					iteratorDBDir(dbFile, CommonProperty.systemInfo.getDbName());
				} else {// 不配置库名，以文件中的DREDBNAME为准
					iteratorDBDir(dbFile, null);
				}
				logger.info("完成遍历库文件夹" + dbFile.getName());
			} else {
				if (!StringUtils.isEmpty(CommonProperty.systemInfo.getDbName())) {
					importData(dbFile, CommonProperty.systemInfo.getDbName());
				} else {// 不配置库名，以文件中的DREDBNAME为准
					importData(dbFile, null);
				}

			}

		}
	}

	/**
	 * 递归遍历各个库文件夹，并且入库
	 * 
	 * @param file
	 *            遍历的文件
	 * @param dbName
	 *            索引库名称
	 */
	private static void iteratorDBDir(File file, String dbName) {
		File[] subFileList = file.listFiles();
		// 每次入库状态
		boolean status = false;
		for (File subFile : subFileList) {
			if (subFile.isDirectory()) {
				iteratorDBDir(subFile, dbName);
			} else {
				logger.debug("begin import the database [" + dbName + "] at the host : " + CommonProperty.systemInfo.getTsHost());
				status = importData(subFile, dbName);
				if (!status) {
					logger.error("提交索引失败，本次任务退出, 等待下次任务执行!");
					break;
				}
				// 休息指定时间再执行下次循环
				try {
					// logger.info("***********************WaitTime 5s ******************************");
					Thread.sleep(5000);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				logger.debug("end import the database [ " + dbName + "] at the host : " + CommonProperty.systemInfo.getTsHost());
			}
		}
	}

	/**
	 * 数据入库操作
	 * 
	 * @param file
	 * @param dbName
	 * @return
	 */
	private static boolean importData(File file, String dbName) {
		boolean status = false;
		if (file.getName().trim().endsWith("data") || file.getName().trim().endsWith("idx")) { // 是data文件则入库，否则跳过不处理

			// 开始导入数据
			status = TsMethod.importDataToUserver(CommonProperty.systemInfo, file, dbName);

		}
		return status;
	}

}

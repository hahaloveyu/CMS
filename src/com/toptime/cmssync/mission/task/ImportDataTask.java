package com.toptime.cmssync.mission.task;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.entity.SystemInfo;
import com.toptime.cmssync.mission.job.ImportDataJob;

/**
 * 数据入索引任务
 */
public class ImportDataTask {

	private static Logger logger = Logger.getLogger(ImportDataTask.class);

	private SystemInfo systemInfo;

	public ImportDataTask(SystemInfo systemInfo) {
		super();
		this.systemInfo = systemInfo;
	}

	public void start() throws SchedulerException {

		JobDetail jobDetail = new JobDetail("ImportDataJob", "ImportDataGroup", ImportDataJob.class);
		CronTrigger cronTrigger = new CronTrigger("ImportDataTrigger", "ImportDataGroup2");

		try {
			String expression = systemInfo.getImportDataSchedul();
			System.out.println("ImportData expression:" + expression);
			logger.info("索引入库定时配置：" + expression);
			CronExpression cexp = new CronExpression(expression);
			cronTrigger.setCronExpression(cexp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		CommonProperty.scheduler.scheduleJob(jobDetail, cronTrigger);
		CommonProperty.scheduler.start();
	}
}

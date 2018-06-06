package com.toptime.cmssync.mission.task;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.entity.SystemInfo;
import com.toptime.cmssync.mission.job.CmsSyncAllManuJob;

/**
 * 全量CMS稿件同步任务
 * 
 * @author
 * 
 */
public class CmsSyncAllManuTask {

	private static Logger logger = Logger.getLogger(CmsSyncAllManuTask.class);

	private SystemInfo systemInfo;

	public CmsSyncAllManuTask(SystemInfo systemInfo) {
		super();
		this.systemInfo = systemInfo;
	}

	public void start() throws SchedulerException {

		JobDetail jobDetail = new JobDetail("CmsSyncAllManuJob", "CmsSyncAllManuGroup", CmsSyncAllManuJob.class);
		CronTrigger cronTrigger = new CronTrigger("CmsSyncAllManuTrigger", "CmsSyncAllManuGroup2");

		try {
			String expression = systemInfo.getCmsSyncAllManuScheduler();
			System.out.println("CmsSyncAllManuTask expression:" + expression);
			logger.info("全量CMS稿件同步定时配置：" + expression);
			CronExpression cexp = new CronExpression(expression);
			cronTrigger.setCronExpression(cexp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		CommonProperty.scheduler.scheduleJob(jobDetail, cronTrigger);
		CommonProperty.scheduler.start();
	}

}

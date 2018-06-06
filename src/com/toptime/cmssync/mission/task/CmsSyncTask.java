package com.toptime.cmssync.mission.task;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.entity.SystemInfo;
import com.toptime.cmssync.mission.job.CmsSyncJob;

/**
 * 增量CMS稿件同步任务
 * 
 * @author
 * 
 */
public class CmsSyncTask {
	
	private static Logger logger = Logger.getLogger(CmsSyncTask.class);
	
	private SystemInfo systemInfo;
	
	public CmsSyncTask(SystemInfo systemInfo) {
		super();
		this.systemInfo = systemInfo;
	}

	public void start() throws SchedulerException {

		JobDetail jobDetail = new JobDetail("CmsSyncJob", "CmsSyncGroup", CmsSyncJob.class);
		CronTrigger cronTrigger = new CronTrigger("CmsSyncTrigger", "CmsSyncGroup2");

		try {
			String expression = systemInfo.getCmsSyncScheduler();
			System.out.println("CmsSyncTask expression:" + expression);
			logger.info("增量CMS稿件同步定时配置：" + expression);
			CronExpression cexp = new CronExpression(expression);
			cronTrigger.setCronExpression(cexp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		CommonProperty.scheduler.scheduleJob(jobDetail, cronTrigger);
		CommonProperty.scheduler.start();
	}

}

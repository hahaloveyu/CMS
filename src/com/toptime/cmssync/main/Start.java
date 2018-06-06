package com.toptime.cmssync.main;

import com.toptime.cmssync.common.CommonMethod;
import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.common.core.SyncLocalFileMethod;
import com.toptime.cmssync.mission.task.CmsSyncAllManuTask;
import com.toptime.cmssync.mission.task.CmsSyncTask;
import com.toptime.cmssync.util.CommonUtil;
import com.toptime.cmssync.util.FileUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 程序入口
 *
 * @author ws
 */
public class Start {

    private static Logger logger = Logger.getLogger(Start.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure(CommonProperty.log4jPath);
        logger.info("====cms sync init Start " + CommonProperty.log4jPath + "===================================");
        // 初始化
        init();

        logger.info("====cms sync init End =============================================================");
    }

    /**
     * 加载配置文件，初始化系统中的相关变量
     */
    public static void init() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            CommonProperty.scheduler = scheduler;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        // 初始化系统变量
        CommonProperty.systemInfo = CommonMethod.getSystemInfo();
        // 获取时间戳
        // CommonProperty.syncTimeStamp = CommonMethod.getSyncTimeStamp();
        CommonProperty.syncTimeStampMap = CommonMethod.getSyncTimeStampMap();
        // 获取顶级域名map
        CommonProperty.topDomainMap = CommonMethod.getTopDomainMap();
        // 获取域名映射map
        CommonProperty.domainMap = CommonMethod.getDomainMap();
        // 文件类型映射map
        CommonProperty.fileTypeMap = CommonMethod.getFileTypeMap();
        //获取data与稿件对应字段map
        CommonProperty.data_manuscript = CommonMethod.getData_Manuscript();
        //获取各个任务对应sitecode的map
        CommonProperty.sitecodeMap = CommonMethod.getSitecodeMap(CommonProperty.data_manuscript.get("sitecode"));
        //获取data字段根据稿件字段值变化而不同的Map
        CommonProperty.referMap = CommonMethod.getReferMap();
        //获取将某一字段按规则拆分成几个字段，对应规则的map
        CommonProperty.splitMap = CommonMethod.getSplitMap();
        CommonProperty.interfaceUrlList = CommonMethod.getInterfaceUrlList();

        if (FileUtil.isAbsolutePath(CommonProperty.systemInfo.getDataFilePath())) { // 配置的路径为绝对路径
            CommonProperty.DATA_PATH = CommonProperty.systemInfo.getDataFilePath();
        } else {
            CommonProperty.DATA_PATH = CommonProperty.userDir + CommonProperty.fileSep + CommonProperty.systemInfo.getDataFilePath() + CommonProperty.fileSep;
        }

        if (FileUtil.isAbsolutePath(CommonProperty.systemInfo.getDataFileBackupPath())) { // 配置的路径为绝对路径
            CommonProperty.DATA_BACKUP_PATH = CommonProperty.systemInfo.getDataFileBackupPath();
        } else {
            CommonProperty.DATA_BACKUP_PATH = CommonProperty.userDir + CommonProperty.fileSep + CommonProperty.systemInfo.getDataFileBackupPath() + CommonProperty.fileSep;
        }

        if (CommonProperty.systemInfo.getTaskType() == 0) { // 全量同步
            // 启动CmsSyncAllManuTask
            CmsSyncAllManuTask cmsSyncAllManuTask = new CmsSyncAllManuTask(CommonProperty.systemInfo);
            try {
                cmsSyncAllManuTask.start();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        } else { // 增量同步
            // 启动CmsSyncTask
            CmsSyncTask cmsSyncTask = new CmsSyncTask(CommonProperty.systemInfo);
            try {
                cmsSyncTask.start();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }

        // 配置了本地xml文件存放目录，则对该目录下的文件进行全量同步
        if (!CommonUtil.isEmpty(CommonProperty.systemInfo.getLocalFileDir())) {
            logger.info("本地xml文件存放目录 " + CommonProperty.systemInfo.getLocalFileDir());
            SyncLocalFileMethod.syncLocalFiles(CommonProperty.systemInfo.getLocalFileDir());
        }

        // 启动ImportDataTask
        /*ImportDataTask importDataTask = new ImportDataTask(CommonProperty.systemInfo);
        try {
			importDataTask.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}*/

    }
}

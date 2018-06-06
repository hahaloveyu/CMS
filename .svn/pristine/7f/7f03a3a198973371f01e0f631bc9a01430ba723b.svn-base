package com.toptime.cmssync.entity;

import java.util.List;
import java.util.Map;

public class SystemInfo {
    /**
     * 任务类别，0：全量同步 1：增量同步
     */
    private int taskType = 1;

    /**
     * 一次性同步稿件CMS地址
     */
    private String cmsInitUrl;
    /**
     * 增量同步稿件CMS地址
     */
    private String cmsUrl;

    /**
     * tsServer地址
     */
    private String tsHost;
    /**
     * tsServer入库端口号
     */
    private String tsIndexPort;
    /**
     * tsServer删除端口号
     */
    private String tsQueryPort;
    /**
     * 索引库名
     */
    private String dbName;
    /**
     * 需要同的步站点列表
     */
    private List<String> syncDbNames;
    /**
     * 删除稿件是，索引库范围，多个之间以半角逗号间隔，不配置则在server所有索引库中的进行删除操作
     */
    private String deleteDbNames;

    /**
     * 是否保存idx文件
     */
    private boolean isSaveIdx;
    /**
     * 同步来的idx文件是否入TsServer
     */
    private boolean isToTsServer;

    /**
     * 索引文件位置
     */
    private String dataFilePath;

    /**
     * 本地xml文件存放目录(绝对路径)，配置该值，则对该目录下的文件进行全量同步，不配置则不进行处理
     */
    private String localFileDir;
    /**
     * 文件备份路径
     */
    private String dataFileBackupPath;
    /**
     * 最大单个入库文件(kb)
     */
    private int maxDataFileSize;
    /**
     * 最大单次提交索引数量(条数)
     */
    private int maxPostNum;

    // //////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 站点code和索引库名的对应hash
     */
    private Map<String, String> siteToDbHS;
    /**
     * 栏目和索引字段的对应hash
     */
    private Map<String, String> channelToDbHS;
    /**
     * 栏目权重hash key为栏目ID，value为权重值
     */
    private Map<String, String> channelWeightHS;
    /**
     * 稿件的baseURL 由于cms传过来的稿件URL使用的可能是内网ip，需要转换成外网ip,或者是ip需要改成域名的
     */
    private String baseURL;
    // //////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 增量CMS稿件同步任务定时
     */
    private String cmsSyncScheduler;
    /**
     * 全量CMS稿件同步任务定时
     */
    private String cmsSyncAllManuScheduler;
    /**
     * 入库定时
     */
    private String importDataSchedul;

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getCmsInitUrl() {
        return cmsInitUrl;
    }

    public void setCmsInitUrl(String cmsInitUrl) {
        this.cmsInitUrl = cmsInitUrl;
    }

    public String getCmsUrl() {
        return cmsUrl;
    }

    public void setCmsUrl(String cmsUrl) {
        this.cmsUrl = cmsUrl;
    }

    public String getTsHost() {
        return tsHost;
    }

    public void setTsHost(String tsHost) {
        this.tsHost = tsHost;
    }

    public String getTsIndexPort() {
        return tsIndexPort;
    }

    public void setTsIndexPort(String tsIndexPort) {
        this.tsIndexPort = tsIndexPort;
    }

    public String getTsQueryPort() {
        return tsQueryPort;
    }

    public void setTsQueryPort(String tsQueryPort) {
        this.tsQueryPort = tsQueryPort;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<String> getSyncDbNames() {
        return syncDbNames;
    }

    public void setSyncDbNames(List<String> syncDbNames) {
        this.syncDbNames = syncDbNames;
    }

    public boolean isSaveIdx() {
        return isSaveIdx;
    }

    public void setSaveIdx(boolean isSaveIdx) {
        this.isSaveIdx = isSaveIdx;
    }

    public boolean isToTsServer() {
        return isToTsServer;
    }

    public void setToTsServer(boolean isToTsServer) {
        this.isToTsServer = isToTsServer;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public String getDataFileBackupPath() {
        return dataFileBackupPath;
    }

    public void setDataFileBackupPath(String dataFileBackupPath) {
        this.dataFileBackupPath = dataFileBackupPath;
    }

    public int getMaxDataFileSize() {
        return maxDataFileSize;
    }

    public void setMaxDataFileSize(int maxDataFileSize) {
        this.maxDataFileSize = maxDataFileSize;
    }

    public int getMaxPostNum() {
        return maxPostNum;
    }

    public void setMaxPostNum(int maxPostNum) {
        this.maxPostNum = maxPostNum;
    }

    public Map<String, String> getSiteToDbHS() {
        return siteToDbHS;
    }

    public void setSiteToDbHS(Map<String, String> siteToDbHS) {
        this.siteToDbHS = siteToDbHS;
    }

    public Map<String, String> getChannelToDbHS() {
        return channelToDbHS;
    }

    public void setChannelToDbHS(Map<String, String> channelToDbHS) {
        this.channelToDbHS = channelToDbHS;
    }

    public Map<String, String> getChannelWeightHS() {
        return channelWeightHS;
    }

    public void setChannelWeightHS(Map<String, String> channelWeightHS) {
        this.channelWeightHS = channelWeightHS;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getCmsSyncScheduler() {
        return cmsSyncScheduler;
    }

    public void setCmsSyncScheduler(String cmsSyncScheduler) {
        this.cmsSyncScheduler = cmsSyncScheduler;
    }

    public String getCmsSyncAllManuScheduler() {
        return cmsSyncAllManuScheduler;
    }

    public void setCmsSyncAllManuScheduler(String cmsSyncAllManuScheduler) {
        this.cmsSyncAllManuScheduler = cmsSyncAllManuScheduler;
    }

    public String getImportDataSchedul() {
        return importDataSchedul;
    }

    public void setImportDataSchedul(String importDataSchedul) {
        this.importDataSchedul = importDataSchedul;
    }

    public String getLocalFileDir() {
        return localFileDir;
    }

    public void setLocalFileDir(String localFileDir) {
        this.localFileDir = localFileDir;
    }

    public String getDeleteDbNames() {
        return deleteDbNames;
    }

    public void setDeleteDbNames(String deleteDbNames) {
        this.deleteDbNames = deleteDbNames;
    }
}

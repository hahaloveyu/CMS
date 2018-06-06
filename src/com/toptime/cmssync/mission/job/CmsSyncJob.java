package com.toptime.cmssync.mission.job;

import com.toptime.cmssync.common.CommonMethod;
import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.common.core.CmsMethod;
import com.toptime.cmssync.common.core.ContentMethod;
import com.toptime.cmssync.common.core.TsMethod;
import com.toptime.cmssync.entity.IndexInfo;
import com.toptime.cmssync.entity.TaskQueueManager;
import com.toptime.cmssync.util.CommonUtil;
import com.toptime.cmssync.util.DateUtil;
import com.toptime.cmssync.util.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.*;

/**
 * 增量CMS稿件同步任务
 */
public class CmsSyncJob implements Job {

    public static Logger logger = Logger.getLogger(CmsSyncJob.class);

    private static boolean isRuning = false;

    // 运行任务队列
    private static TaskQueueManager taskQueue = new TaskQueueManager();

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        if (!isRuning) {

            logger.info("CmsSyncTask start  ...........................");
            isRuning = true;
            // cmsSyncTask();
            // 多线程同步任务
            multiThreadCmsSyncTask();
            isRuning = false;
            logger.info("CmsSyncTask end    ...........................");
        } else {
            logger.debug("last CmsSyncTask job is not finished .........");
        }

    }

    /**
     * 多线程同步任务
     */
    private static void multiThreadCmsSyncTask() {
        List<String> interfaceUrlList = CommonProperty.interfaceUrlList;

        if (interfaceUrlList != null && interfaceUrlList.size() > 0) {
            try {
                // 遍历同步接口url列表，开启多线程同步任务
                for (final String url : interfaceUrlList) {
                    if (!CommonUtil.isEmpty(url)) {
                        if (!taskQueue.isInTaskQueue(url)) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    cmsSyncTask(url);
                                }
                            }).start();
                        } else {
                            logger.debug("interfaceUrl:" + url + "  ---> 上次同步任务未完成，本次不再启动新任务！");
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 增量CMS稿件同步任务
     */
    private static void cmsSyncTask(String interfaceUrl) {

        logger.debug("interfaceUrl:" + interfaceUrl + "  --->  同步" + DateUtil.DateFormat(new Date(), "yyyyMMdd") + "的稿件");

        try {
            // 将任务放入运行队列中
            taskQueue.addToTaskQueue(interfaceUrl);
            // 临时存储处理稿件的最新日期
            // long tempNewSyncTimeStamp = CommonProperty.syncTimeStamp;
            long tempNewSyncTimeStamp = CommonProperty.syncTimeStampMap.get(interfaceUrl) == null ? 0 : CommonProperty.syncTimeStampMap.get(interfaceUrl);
            long syncTimeStamp = tempNewSyncTimeStamp;
            logger.debug("interfaceUrl:" + interfaceUrl + "  --->  最新同步时间为：" + DateUtil.parseLongToDate(CommonProperty.syncTimeStamp, "yyyy-MM-dd HH:mm:ss"));

            // 读取列表页xml，获取一组稿件地址及相应操作(insert/update/delete)
            // List<IndexInfo> indexInfoList = CmsMethod.getIndexInfoList(CommonProperty.systemInfo.getCmsUrl() + "index_" + DateUtil.DateFormat(new Date(), "yyyyMMdd") + ".xml");
            //List<IndexInfo> indexInfoList = CmsMethod.getIndexInfoList(interfaceUrl + "index_" + DateUtil.DateFormat(new Date(), "yyyyMMdd") + ".xml");
           //System.out.println("++++++++++++++++++++++++"+interfaceUrl +"incremental_zcfg_gds_list.jsp?channelId=4eb01e18e55c4bf3aa1fbee9ff7b93de&currentDate=20180531");
            //List<IndexInfo> indexInfoList = CmsMethod.getIndexInfoList(interfaceUrl +"&currentDate=20180531");
            List<IndexInfo> indexInfoList = CmsMethod.getIndexInfoList(interfaceUrl +"&currentDate="+DateUtil.DateFormat(new Date(), "yyyyMMdd"));
            
            if (indexInfoList != null && indexInfoList.size() > 0) {
                // 记录同步稿件成功状态，一旦出错则跳出本次任务
                boolean flag = true;
                // 将稿件内容存储为规定格式的索引内容
                StringBuffer sb = new StringBuffer();
                // 已处理稿件数量
                int i = 0;
                // 生成索引文件是否成功
                boolean status = false;
                // 索引入库是否成功
                boolean importDataStatus = false;
                // 删除索引是否成功
                boolean deleteStatus = false;
                // 索引文件路径
                String filePath = "";
                
                // 删除稿件的reference，多个之间用,分隔
				String delDocReferences = "";


                // 临时保存稿件站点名，即入库的库名，同一个站点的稿件放到同一个stringbuffer中
                String webSiteCodeName = "";
                // sitecode，没有配置则为空
                String sitecode = CommonProperty.sitecodeMap.get(interfaceUrl) == null ? "" : CommonProperty.sitecodeMap.get(interfaceUrl);
                // 保存各个站点的稿件信息stringbuffer
                Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();

                //要同步的站点列表
                List<String> syncDbNames = CommonProperty.systemInfo.getSyncDbNames();
   
                // 遍历列表页
                for (IndexInfo info : indexInfoList) {
                    long lastmodifyTime = DateUtil.DateFormat(info.getLastmodifytime(), "yyyy-MM-dd HH:mm:ss").getTime();
                    // 稿件时间与本地保存的同步时间做比对，只处理同步时间之后的稿件
                    if (lastmodifyTime > syncTimeStamp) {
                        // 对稿件的操作类型(insert/update/delete)
                        String type = info.getType().trim();
                        if (type.equalsIgnoreCase("insert") || type.equalsIgnoreCase("update")) {// 新增稿件
                            i++;
                            // 获取稿件内容
                            // ManuscriptInfo mInfo = CmsMethod.getManuscriptInfo(CommonProperty.systemInfo.getCmsUrl() + info.getAddress().trim());
                            // LinkedHashMap<String, Object> dataMap = CmsMethod.getFieldsMap(CommonProperty.systemInfo.getCmsUrl() + info.getAddress().trim());
                            
                      // 通过接口详情页url获取稿件内容
                            System.out.println("_________________"+CommonProperty.systemInfo.getCmsUrl() +"load_zcfg_gds_content.jsp?manuscriptId="+ info.getId().trim());
                            LinkedHashMap<String, Object> dataMap = CmsMethod.getFieldsMap(CommonProperty.systemInfo.getCmsUrl() +"load_zcfg_gds_content.jsp?manuscriptId="+ info.getId().trim());
//                            LinkedHashMap<String, Object> dataMap = CmsMethod.getFieldsMap(CommonProperty.systemInfo.getCmsUrl() + info.getAddress().trim());
                            
                            //开普已修改稿件规则，不需要再单独处理
                            // dataMap.put("DREREFERENCE", info.getId());
                            if (dataMap != null) {

                                dataMap.put("sitecode", sitecode);
                                // webSiteCodeName = mInfo.getWebsiteInfo().getCodeName().trim();
                                webSiteCodeName = (String) dataMap.get("DREDBNAME");

                                //同步站点配置为空则无限制
                                //如果配置不为空则只同步包括在内的站点
                                if (syncDbNames == null || syncDbNames.size() == 0 || syncDbNames.contains(webSiteCodeName)) {

                                    if (map.get(webSiteCodeName) == null) { // 该站点还没有对应稿件索引文档内容
                                        StringBuffer buffer = new StringBuffer();
                                        // 生成data文件内容
                                        // buffer = ContentMethod.addIndexBuffer(mInfo, buffer);
                                        buffer = ContentMethod.addMap2Buffer(dataMap, buffer);

                                        map.put(webSiteCodeName, buffer);
                                    } else {
                                        sb = map.get(webSiteCodeName);
                                        // sb = ContentMethod.addIndexBuffer(mInfo, sb);
                                        sb = ContentMethod.addMap2Buffer(dataMap, sb);
                                    }

                                    // 生成data文件内容
                                    // sb = ContentMethod.addIndexBuffer(mInfo, sb);
                                }
                            } else {
                                // 获取稿件内容失败
                                flag = false;
                                break;
                            }

                        } else if (type.equalsIgnoreCase("delete")) { // 删除稿件
                            // 先将之前的稿件入库
                            if (map != null && map.size() > 0) {
                                for (String s : map.keySet()) {
                                    sb = map.get(s);
                                    if (sb != null && sb.length() > 0) {
                                        if (!CommonUtil.isEmpty(CommonProperty.systemInfo.getDbName())) {
                                            logger.debug("系统指定默认索引库：" + CommonProperty.systemInfo.getDbName());
                                            importDataStatus = TsMethod.dreAddDataToUserver(CommonProperty.systemInfo, sb.toString(), CommonProperty.systemInfo.getDbName());
                                        } else {
                                            importDataStatus = TsMethod.dreAddDataToUserver(CommonProperty.systemInfo, sb.toString(), null);
                                        }
                                        if (importDataStatus) {
                                            // 生成data文件，查问题用
                                            filePath = CommonProperty.DATA_PATH + StringUtils.md5Hex32(interfaceUrl) + CommonProperty.fileSep + DateUtil.parseLongToDate(System.currentTimeMillis(), "yyyyMMdd") + CommonProperty.fileSep + "delete_" + System.currentTimeMillis() + ".data";
                                            status = ContentMethod.writeToIdx(sb, filePath);
                                            if (status) {// 保存索引文件成功，则将StringBuffer中内容清空
                                                sb.setLength(0);
                                            }
                                            i = 0;
                                        } else {

                                        }
                                    }
                                }
                            }

						/*if (sb != null && sb.length() > 0) {
                            importDataStatus = TsMethod.dreAddDataToUserver(CommonProperty.systemInfo, sb.toString(), null);
							if (importDataStatus) {
								// 生成data文件，查问题用
								filePath = CommonProperty.DATA_PATH + System.currentTimeMillis() + ".data";
								status = ContentMethod.writeToIdx(sb, filePath);
								if (status) {// 保存索引文件成功，则将StringBuffer中内容清空
									sb.setLength(0);
								}
								i = 0;
							} else {

							}
						}*/

                         // 待删除稿件id，多值之间以,分隔
						delDocReferences += info.getId().trim() + ",";

                            /*deleteStatus = TsMethod.deleteIndexFromUserver(CommonProperty.systemInfo, info.getId().trim());
                            if (deleteStatus) {
                                logger.debug("interfaceUrl:" + interfaceUrl + "  --->  删除索引成功，reference=" + info.getId().trim());
                            } else {
                                logger.debug("interfaceUrl:" + interfaceUrl + "  --->  删除索引失败，reference=" + info.getId().trim());
                            }*/

                        }
                    /*else if (type.equalsIgnoreCase("update")) { // 更新稿件

					}*/

                        // 修改最新日期，等到本次任务成功结束，更新文件和内存中的最后同步时间
                        if (lastmodifyTime > tempNewSyncTimeStamp) {
                            tempNewSyncTimeStamp = lastmodifyTime;
                        }

                        // 100篇稿件内容生成一个索引文件
                        if (i > 0 && i % 100 == 0) {
                            // 入索引
                            if (map != null && map.size() > 0) {
                                for (String s : map.keySet()) {
                                    sb = map.get(s);
                                    if (sb != null && sb.length() > 0) {
                                        if (!CommonUtil.isEmpty(CommonProperty.systemInfo.getDbName())) {
                                            logger.debug("系统指定默认索引库：" + CommonProperty.systemInfo.getDbName());
                                            importDataStatus = TsMethod.dreAddDataToUserver(CommonProperty.systemInfo, sb.toString(), CommonProperty.systemInfo.getDbName());
                                        } else {
                                            importDataStatus = TsMethod.dreAddDataToUserver(CommonProperty.systemInfo, sb.toString(), null);
                                        }
                                        if (importDataStatus) {
                                            // 生成data文件，查问题用
                                            filePath = CommonProperty.DATA_PATH + StringUtils.md5Hex32(interfaceUrl) + CommonProperty.fileSep + DateUtil.parseLongToDate(System.currentTimeMillis(), "yyyyMMdd") + CommonProperty.fileSep + System.currentTimeMillis() + ".data";
                                            status = ContentMethod.writeToIdx(sb, filePath);
                                            if (status) {// 保存索引文件成功，则将StringBuffer中内容清空
                                                sb.setLength(0);
                                            }
                                            i = 0;
                                        } else {

                                        }
                                    }
                                }
                            }

						/*// 入索引
                        importDataStatus = TsMethod.dreAddDataToUserver(CommonProperty.systemInfo, sb.toString(), null);
						if (importDataStatus) {
							// 生成data文件，查问题用
							filePath = CommonProperty.DATA_PATH + System.currentTimeMillis() + ".data";
							status = ContentMethod.writeToIdx(sb, filePath);
							if (status) {// 保存索引文件成功，则将StringBuffer中内容清空
								sb.setLength(0);
							}
						} else {

						}*/
                        }

                    }
                }

                // 入索引
                if (map != null && map.size() > 0) {
                    for (String s : map.keySet()) {
                        sb = map.get(s);
                        if (sb != null && sb.length() > 0) {
                            if (!CommonUtil.isEmpty(CommonProperty.systemInfo.getDbName())) {
                                logger.debug("系统指定默认索引库：" + CommonProperty.systemInfo.getDbName());
                                importDataStatus = TsMethod.dreAddDataToUserver(CommonProperty.systemInfo, sb.toString(), CommonProperty.systemInfo.getDbName());
                            } else {
                                importDataStatus = TsMethod.dreAddDataToUserver(CommonProperty.systemInfo, sb.toString(), null);
                            }
                            if (importDataStatus) {
                                // 生成data文件，查问题用
                                filePath = CommonProperty.DATA_PATH + StringUtils.md5Hex32(interfaceUrl) + CommonProperty.fileSep + DateUtil.parseLongToDate(System.currentTimeMillis(), "yyyyMMdd") + CommonProperty.fileSep + System.currentTimeMillis() + ".data";
                                status = ContentMethod.writeToIdx(sb, filePath);
                                if (status) {// 保存索引文件成功，则将StringBuffer中内容清空
                                    sb.setLength(0);
                                }
                                i = 0;
                            } else {

                            }
                        }
                    }
                }
             // 批量删除索引
                String tips = "";
			if (!CommonUtil.isEmpty(delDocReferences)) {
				delDocReferences = delDocReferences.substring(0, delDocReferences.lastIndexOf(","));
//				deleteStatus = TsMethod.deleteIndexFromUserver(CommonProperty.systemInfo, delDocReferences);
//				if (deleteStatus) {
//					logger.debug("删除索引成功，reference=" + delDocReferences);
//				} else {
//					logger.debug("删除索引失败，reference=" + delDocReferences);
//				}
				
				tips = TsMethod.deleteIndexFromUserver(CommonProperty.systemInfo, delDocReferences);
				if("false".equals(tips)){
					logger.debug("删除索引失败！");
				}else if ("noDbs".equals(tips)){
					logger.debug("删除索引失败,索引库中没有与"+delDocReferences+"对应的稿件信息！");
				}else if(tips.startsWith("S")){
					logger.debug("删除索引失败！" + tips);
				}else if(tips.startsWith("&")){
					logger.debug("删除索引成功，reference=" + delDocReferences + ":" + tips);
				}
			}


			/*if (sb.length() > 0) { // 缓存中有内容未保存到索引文件中
                // 入索引
				importDataStatus = TsMethod.dreAddDataToUserver(CommonProperty.systemInfo, sb.toString(), null);
				if (importDataStatus) {
					// 生成data文件，查问题用
					filePath = CommonProperty.DATA_PATH + System.currentTimeMillis() + ".data";
					status = ContentMethod.writeToIdx(sb, filePath);
					if (status) {// 保存索引文件成功，则将StringBuffer中内容清空
						sb.setLength(0);
					}
				} else {

				}
			}*/

                if (!flag) {
                    logger.error("interfaceUrl:" + interfaceUrl + "  --->  同步稿件出错");
                }

                if (tempNewSyncTimeStamp > syncTimeStamp) {
                    // 修改内存中的最新同步时间
                    CommonProperty.syncTimeStampMap.put(interfaceUrl, tempNewSyncTimeStamp);
                    // 修改文件中的最新同步时间
                    // CommonMethod.newSyncTime(tempNewSyncTimeStamp);
                    CommonMethod.newSyncTime(interfaceUrl, tempNewSyncTimeStamp);
                    logger.debug("interfaceUrl:" + interfaceUrl + "  --->  最新同步时间修改为：" + DateUtil.parseLongToDate(CommonProperty.syncTimeStampMap.get(interfaceUrl), "yyyy-MM-dd HH:mm:ss"));
                } else {
                    logger.debug("interfaceUrl:" + interfaceUrl + "  --->  本次任务最新同步时间未改变");
                }

            } else {
                logger.debug("interfaceUrl:" + interfaceUrl + "  --->  " + DateUtil.DateFormat(new Date(), "yyyyMMdd") + "列表XML为空");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            taskQueue.removeFromTaskQueue(interfaceUrl);
        }

    }

}

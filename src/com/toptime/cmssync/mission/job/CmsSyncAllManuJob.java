package com.toptime.cmssync.mission.job;

import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.common.core.CmsMethod;
import com.toptime.cmssync.common.core.ContentMethod;
import com.toptime.cmssync.common.core.TsMethod;
import com.toptime.cmssync.entity.IndexInfo;
import com.toptime.cmssync.util.CommonUtil;
import com.toptime.cmssync.util.DateUtil;
import com.toptime.cmssync.util.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 全量CMS稿件同步任务
 */
public class CmsSyncAllManuJob implements Job {

    public static Logger logger = Logger.getLogger(CmsSyncAllManuJob.class);

    private static boolean isRuning = false;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        if (!isRuning) {

            logger.info("CmsSyncAllManuTask start  ...........................");
            isRuning = true;
            // cmsSyncTask();
            // 多线程同步任务
            multiThreadCmsSyncTask();
            //本地测试用
            //			LinkedHashMap<String, Object> dataMap = CmsMethod.getFieldsMapDebug("");
            //			if (dataMap != null) {
            //				StringBuffer buffer = new StringBuffer();
            //				buffer = ContentMethod.addMap2Buffer(dataMap, buffer);
            //				try {
            //					FileUtils.writeStringToFile(new File("D:\\work\\document\\cms\\test.data"), buffer.toString());
            //				} catch (IOException e) {
            //					// TODO Auto-generated catch block
            //					e.printStackTrace();
            //				}
            //			}

            isRuning = false;
            logger.info("CmsSyncAllManuTask end    ...........................");
        } else {
            logger.debug("last CmsSyncAllManuTask job is not finished .........");
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
                for (String url : interfaceUrlList) {
                    if (!CommonUtil.isEmpty(url)) {
                        // ss[0]:interfaceUrl ss[1]:baseUrl
                        final String[] ss = url.split(" @@@@@ ");
                        if (ss != null && ss.length == 2) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    cmsSyncTask(ss[0], ss[1]);
                                }
                            }).start();
                        }

                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 全量CMS稿件同步任务
     *
     * @param interfaceUrl 同步接口地址，即列表页访问地址
     * @param baseUrl      稿件详情相对url补全为绝对路径的根路径
     */
    private static void cmsSyncTask(String interfaceUrl, String baseUrl) {

        // 读取列表页xml，获取一组稿件地址及相应操作(insert/update/delete)
        List<IndexInfo> indexInfoList = CmsMethod.getIndexInfoList(interfaceUrl);

        if (indexInfoList != null && indexInfoList.size() > 0) {
            logger.debug("interfaceUrl:" + interfaceUrl + "  --->  全量同步稿件数量：" + indexInfoList.size() + "，URL=" + interfaceUrl);
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

            // 临时保存稿件站点名，即入库的库名，同一个站点的稿件放到同一个stringbuffer中
            String webSiteCodeName = "";
            // sitecode，没有配置则为空
            String sitecode = CommonProperty.sitecodeMap.get(interfaceUrl) == null ? "" : CommonProperty.sitecodeMap.get(interfaceUrl);
            // 保存各个站点的稿件信息stringbuffer
            Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();
            // 要同步的站点列表
            List<String> syncDbNames = CommonProperty.systemInfo.getSyncDbNames();

            // 已同步稿件数量
            int num = 0;
            // 遍历列表页
            for (IndexInfo info : indexInfoList) {
                num++;
                logger.debug("interfaceUrl:" + interfaceUrl + "  --->  正在同步稿件[" + num + "/" + indexInfoList.size() + "]：" + baseUrl + info.getAddress().trim());
                // 对稿件的操作类型(insert/update/delete)
                String type = info.getType().trim();
                if (type.equalsIgnoreCase("insert") || type.equalsIgnoreCase("update")) {// 新增稿件
                    i++;
                    // 获取稿件内容
//					ManuscriptInfo mInfo = CmsMethod.getManuscriptInfo(CommonProperty.systemInfo.getCmsUrl() + info.getAddress().trim());
              //通过详情页接口url       
//                    LinkedHashMap<String, Object> dataMap = CmsMethod.getFieldsMap(baseUrl +"singleManu4search.jsp?manuscriptId="+ info.getId().trim());
                    LinkedHashMap<String, Object> dataMap = CmsMethod.getFieldsMap(baseUrl + info.getAddress().trim());
                    
                    if (dataMap != null) {

                        dataMap.put("sitecode", sitecode);
                        // webSiteCodeName =
                        // mInfo.getWebsiteInfo().getCodeName().trim();
                        webSiteCodeName = (String) dataMap.get("DREDBNAME");

                        // 同步站点配置为空则无限制
                        // 如果配置不为空则只同步包括在内的站点
                        if (syncDbNames == null || syncDbNames.size() == 0 || syncDbNames.contains(webSiteCodeName)) {

                            if (map.get(webSiteCodeName) == null) { // 该站点还没有对应稿件索引文档内容
                                StringBuffer buffer = new StringBuffer();
                                // 生成data文件内容
                                // buffer = ContentMethod.addIndexBuffer(mInfo,
                                // buffer);
                                buffer = ContentMethod.addMap2Buffer(dataMap, buffer);

                                map.put(webSiteCodeName, buffer);
                            } else {
                                sb = map.get(webSiteCodeName);
                                // sb = ContentMethod.addIndexBuffer(mInfo, sb);
                                sb = ContentMethod.addMap2Buffer(dataMap, sb);
                            }
                        }

                    } else {
                        // 获取稿件内容失败
                        logger.error("全量同步稿件出错，" + baseUrl + info.getAddress().trim());
                        /*flag = false;
                        logger.error("全量同步稿件出错，" + CommonProperty.systemInfo.getCmsUrl() + info.getAddress().trim());
						break;*/
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

//                    deleteStatus = TsMethod.deleteIndexFromUserver(CommonProperty.systemInfo, info.getId().trim());
//                    if (deleteStatus) {
//                        logger.debug("interfaceUrl:" + interfaceUrl + "  --->  删除索引成功，reference=" + info.getId().trim());
//                    } else {
//                        logger.debug("interfaceUrl:" + interfaceUrl + "  --->  删除索引失败，reference=" + info.getId().trim());
//                    }
                    String tips = TsMethod.deleteIndexFromUserver(CommonProperty.systemInfo, info.getId().trim());
                    if("false".equals(tips)){
    					logger.debug("删除索引失败！");
    				}else if ("noDbs".equals(tips)){
    					logger.debug("删除索引失败,索引库中没有与"+info.getId().trim()+"对应的稿件信息！");
    				}else if(tips.startsWith("S")){
    					logger.debug("删除索引失败！" + tips);
    				}else if(tips.startsWith("&")){
    					logger.debug("删除索引成功，reference=" + info.getId().trim() + ":" + tips);
    				}

                }
                /*else if (type.equalsIgnoreCase("update")) { // 更新稿件

				}*/

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

                }
            }

            if (flag) {

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
            }

        } else {
            logger.debug("interfaceUrl:" + interfaceUrl + "  --->  全量稿件列表XML为空");
        }

    }

}

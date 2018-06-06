package com.toptime.cmssync.common;

import com.toptime.cmssync.entity.SystemInfo;
import com.toptime.cmssync.util.CommonUtil;
import com.toptime.cmssync.util.DateUtil;
import com.toptime.cmssync.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用方法
 *
 * @author
 */
public class CommonMethod {

    public static Logger log = Logger.getLogger(CommonMethod.class.getName());

    /**
     * 获取配置文件中的系统信息
     *
     * @return
     */
    public static SystemInfo getSystemInfo() {
        SystemInfo si = new SystemInfo();
        /**********************************************************
         * system信息获取
         **********************************************************/
        // 一次性同步稿件CMS地址
        String cmsInitUrl = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "cmsInitUrl", "");
        /*// 当路径不为空时,路径不是以 "/" 结尾,在路径最后加"/"
        if (!CommonUtil.isEmpty(cmsInitUrl) && !cmsInitUrl.endsWith("/")) {
			cmsInitUrl += "/";
		}*/
        si.setCmsInitUrl(cmsInitUrl);

        // 增量同步稿件CMS地址
        String cmsUrl = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "cmsUrl", "");
        // 当路径不为空时,路径不是以 "/" 结尾,在路径最后加"/"
        if (!CommonUtil.isEmpty(cmsUrl) && !cmsUrl.endsWith("/")) {
            cmsUrl += "/";
        }
        si.setCmsUrl(cmsUrl);

        si.setTsHost(ConfigFile.getProfileString(CommonProperty.configFileName, "system", "tsHost", ""));
        si.setTsQueryPort(ConfigFile.getProfileString(CommonProperty.configFileName, "system", "tsQueryPort", ""));
        si.setTsIndexPort(ConfigFile.getProfileString(CommonProperty.configFileName, "system", "tsIndexPort", ""));
        si.setDbName(ConfigFile.getProfileString(CommonProperty.configFileName, "system", "dbName", ""));
        String syncDbNameStr = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "syncDbNames", "");
        String[] syncDbNameArray = StringUtils.split(syncDbNameStr, ",");
        si.setSyncDbNames(Arrays.asList(syncDbNameArray));
        // 删除稿件时，索引库范围，多个之间以半角逗号间隔，不配置则在server所有索引库中的进行删除操作
        si.setDeleteDbNames(ConfigFile.getProfileString(CommonProperty.configFileName, "system", "deleteDbNames", ""));
        String isSaveIdx = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "isSaveIdx", "");
        if (isSaveIdx.equalsIgnoreCase("false")) {
            si.setSaveIdx(false);
        } else {
            si.setSaveIdx(true);
        }
        String isToTsServer = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "isToTsServer", "");
        if (isToTsServer.equalsIgnoreCase("false")) {
            si.setToTsServer(false);
        } else {
            si.setToTsServer(true);
        }

        // 索引文件路径（相对或绝对）
        String dataFilePath = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "dataFilePath", "");
        // 替换路径中的分隔符为当前系统的分隔符
        dataFilePath = dataFilePath.replace("\\", CommonProperty.fileSep);
        dataFilePath = dataFilePath.replace("/", CommonProperty.fileSep);
        si.setDataFilePath(dataFilePath);

        // 本地xml文件存放目录(绝对路径)，配置该值，则对该目录下的文件进行全量同步，不配置则不进行处理
        String localFileDir = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "localFileDir", "");
        // 替换路径中的分隔符为当前系统的分隔符
        localFileDir = localFileDir.replace("\\", CommonProperty.fileSep);
        localFileDir = localFileDir.replace("/", CommonProperty.fileSep);
        si.setLocalFileDir(localFileDir);

        // 索引文件备份路径（相对或绝对）
        String dataFileBackupPath = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "dataFileBackupPath", "");
        // 替换路径中的分隔符为当前系统的分隔符
        dataFileBackupPath = dataFileBackupPath.replace("\\", CommonProperty.fileSep);
        dataFileBackupPath = dataFileBackupPath.replace("/", CommonProperty.fileSep);
        si.setDataFileBackupPath(dataFileBackupPath);

        // 单个文件最大值
        si.setMaxDataFileSize(NumberUtils.toInt(ConfigFile.getProfileString(CommonProperty.configFileName, "system", "maxDataFileSize", "")));
        // 单次最大索引数
        si.setMaxPostNum(NumberUtils.toInt(ConfigFile.getProfileString(CommonProperty.configFileName, "system", "maxPostNum", "")));

		/*String webToDbSR = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "siteToDb", "").trim();
        Map<String, String> map = null;
		if (webToDbSR != null && !webToDbSR.equals("")) {
			map = new HashMap<String, String>();
			String[] array = webToDbSR.split(";");
			for (String sr : array) {
				if (sr != null && !sr.equals("")) {
					String[] arrayHs = sr.split(",");
					map.put(arrayHs[0], arrayHs[1]);
					log.info(arrayHs[0] + ":" + arrayHs[1]);
				}
			}
			si.setSiteToDbHS(map);
		}
		String channelToDbSR = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "channelToDb", "").trim();
		Map<String, String> channelMap = new HashMap<String, String>();
		if (channelToDbSR != null && !channelToDbSR.equals("")) {
			String[] array = channelToDbSR.split(";");
			for (String sr : array) {
				if (sr != null && !sr.equals("")) {
					String[] arrayHs = sr.split(",");
					channelMap.put(arrayHs[0], arrayHs[1]);
					log.info(arrayHs[0] + ":" + arrayHs[1]);
				}
			}
		}
		si.setChannelToDbHS(channelMap);*/

        /**********************************************************
         * 权重信息
         **********************************************************/
        /*// 栏目权重
        String channerWeightSR = ConfigFile.getProfileString(CommonProperty.configFileName, "system", "channelWeight", "").trim();
		Map<String, String> channelWeightMap = new HashMap<String, String>();
		if (channerWeightSR != null && !channerWeightSR.equals("")) {
			String[] array = channerWeightSR.split(";");
			for (String sr : array) {
				if (sr != null && !sr.equals("")) {
					String[] arrayHs = sr.split(",");
					channelWeightMap.put(arrayHs[0], arrayHs[1]);
					log.info("channelWeight----" + arrayHs[0] + ":" + arrayHs[1]);
				}
			}
		}
		si.setChannelWeightHS(channelWeightMap);
		// 稿件baseURL
		si.setBaseURL(ConfigFile.getProfileString(CommonProperty.configFileName, "system", "baseURL", "").trim());*/

        /**********************************************************
         * schedul信息获取
         **********************************************************/
        // 增量稿件同步定时
        si.setCmsSyncScheduler(ConfigFile.getProfileString(CommonProperty.configFileName, "scheduler", "cmsSyncScheduler", ""));
        // 全量稿件同步定时
        si.setCmsSyncAllManuScheduler(ConfigFile.getProfileString(CommonProperty.configFileName, "scheduler", "cmsSyncAllManuScheduler", ""));
        si.setImportDataSchedul(ConfigFile.getProfileString(CommonProperty.configFileName, "scheduler", "importDataSchedul", ""));

        return si;
    }

    /**
     * 获取同步稿件时间戳
     *
     * @return
     */
    public static long getSyncTimeStamp() {
        long timeStamp = 0L;

        // yyyy-MM-dd HH:mm:ss
        String timeStampStr = ConfigFile.getProfileString(CommonProperty.syncTimeFilePath, "syncTime", "syncTime", "");

        if (!CommonUtil.isEmpty(timeStampStr)) {
            timeStamp = DateUtil.DateFormat(timeStampStr, "yyyy-MM-dd HH:mm:ss").getTime();
        }
        return timeStamp;
    }

    /**
     * 获取同步稿件时间戳
     *
     * @return
     */
    public static Map<String, Long> getSyncTimeStampMap() {
        Map<String, Long> syncTimeStampMap = new HashMap<String, Long>();
        try {
            if (!CommonUtil.isEmpty(CommonProperty.syncTimeFilePath)) {
                List<String> lines = FileUtils.readLines(new File(CommonProperty.configFilePath + CommonProperty.syncTimeFilePath), CommonProperty.configFileEncoding);
                if (lines != null && lines.size() > 0) {
                    for (String line : lines) {
                        line = line.trim();
                        if (!CommonUtil.isEmpty(line) && line.startsWith("[") && line.endsWith("]")) {
                            String section = line.substring(1, line.length() - 1).trim();
                            long timeStamp = 0L;

                            // yyyy-MM-dd HH:mm:ss
                            String timeStampStr = ConfigFile.getProfileString(CommonProperty.syncTimeFilePath, section, "syncTime", "");

                            if (!CommonUtil.isEmpty(timeStampStr)) {
                                timeStamp = DateUtil.DateFormat(timeStampStr, "yyyy-MM-dd HH:mm:ss").getTime();
                            }

                            syncTimeStampMap.put(section, timeStamp);
                        }
                    }
                }

            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return syncTimeStampMap;
    }

    /**
     * 获取同步稿件接口地址url列表
     *
     * @return
     */
    public static List<String> getInterfaceUrlList() {
        List<String> interfaceUrlList = new ArrayList<String>();
        try {
            if (!CommonUtil.isEmpty(CommonProperty.interfaceUrlFilePath)) {
                LineIterator lineIterator = FileUtils.lineIterator(new File(CommonProperty.configFilePath + CommonProperty.interfaceUrlFilePath), CommonProperty.configFileEncoding);
                if (lineIterator != null) {
                    while (lineIterator.hasNext()) {
                        String s = lineIterator.nextLine().trim();
                        if (!CommonUtil.isEmpty(s)) {

                            if (s.startsWith("#")) {
                                continue;
                            }
                            if (s.startsWith("[") && s.endsWith("]")) {
                                int taskType = NumberUtils.toInt(s.substring(1, s.length() - 1).trim(), 1);
                                CommonProperty.systemInfo.setTaskType(taskType);
                                continue;
                            }
                            interfaceUrlList.add(s);
                        }
                    }
                }

            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("interfaceUrlList size:" + interfaceUrlList.size());

        return interfaceUrlList;
    }

    public static void main(String[] args) {
        List<String> list = getInterfaceUrlList();
        System.out.println(list.size());
    }

    /**
     * 将新的更新时间戳写入配置文件
     *
     * @param syncTimeStamp
     */
    public static void newSyncTime(long syncTimeStamp) {
        String syncTimeStr = DateUtil.parseLongToDate(syncTimeStamp, "yyyy-MM-dd HH:mm:ss");
        // 把新时间戳写入配置文件
        ConfigFile.setProfileString(CommonProperty.syncTimeFilePath, "syncTime", "syncTime", syncTimeStr);
    }

    /**
     * 将新的更新时间戳写入配置文件，接口url对应相应的时间戳
     *
     * @param interfaceUrl
     * @param syncTimeStamp
     */
    public static void newSyncTime(String interfaceUrl, long syncTimeStamp) {
        String syncTimeStr = DateUtil.parseLongToDate(syncTimeStamp, "yyyy-MM-dd HH:mm:ss");
        // 把新时间戳写入配置文件
        ConfigFile.setProfileString(CommonProperty.syncTimeFilePath, interfaceUrl, "syncTime", syncTimeStr);
    }

    /**
     * 获取本地的topdomain.txt
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Boolean> getTopDomainMap() {
        Map<String, Boolean> topDomainMap = new HashMap<String, Boolean>();
        try {
            List<String> topDomain = FileUtils.readLines(new File(CommonProperty.configFilePath + CommonProperty.topDomainFilePath), "UTF-8");
            for (String s : topDomain) {
                if (s != null && !s.equals("") && !s.equals(" ")) {
                    topDomainMap.put(s, true);
                }
            }
            log.info("Loaded all the top domain from the topdomain.txt. " + topDomainMap.size());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return topDomainMap;
    }

    /**
     * 获取本地的domain.txt
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getDomainMap() {
        Map<String, String> domainMap = new HashMap<String, String>();
        try {
            List<String> domain = FileUtils.readLines(new File(CommonProperty.configFilePath + CommonProperty.domainFilePath), "UTF-8");
            for (String s : domain) {
                String[] str = s.split(";");
                if (str.length == 2) {
                    domainMap.put(str[0].trim(), str[1].trim());
                }// 有不合法的字符存在.
            }
            log.info("loaded all the domain from the domain.txt: " + domainMap.size());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return domainMap;
    }

    /**
     * 生成文件类型映射map
     */
    public static Map<String, String> getFileTypeMap() {
        Map<String, String> fileTypeMap = new HashMap<String, String>();

        fileTypeMap.put(".doc", "doc");
        fileTypeMap.put(".docx", "docx");
        fileTypeMap.put(".xls", "xls");
        fileTypeMap.put(".xlsx", "xlsx");
        fileTypeMap.put(".pdf", "pdf");
        fileTypeMap.put(".pdfx", "pdfx");
        fileTypeMap.put(".ppt", "ppt");
        fileTypeMap.put(".pptx", "pptx");
        fileTypeMap.put(".txt", "txt");
        fileTypeMap.put(".rar", "rar");
        fileTypeMap.put(".zip", "zip");

        return fileTypeMap;
    }

    /**
     * 获取data与稿件对应字段表
     */
    public static LinkedHashMap<String, String> getData_Manuscript() {
        LinkedHashMap<String, String> data_Manuscrip = new LinkedHashMap<String, String>();
        try {
            @SuppressWarnings("unchecked")
            List<String> lineList = FileUtils.readLines(new File(CommonProperty.configFilePath + "templ.properties"), "UTF-8");
            for (String line : lineList) {
                if (!line.startsWith("#")) {
                    String[] str = line.split("=", 2);
                    if (str.length == 2) {
                        data_Manuscrip.put(str[0].trim(), str[1].trim());
                    }
                }
            }
            log.info("loaded all the data fields k_v from the data.idx: " + data_Manuscrip.size());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return data_Manuscrip;
    }

    /**
     * 根据配置的任务对应sitecode字符串，获得map
     *
     * @param sitecodeConfStr templ.properties中配置的sitecode字符串
     *                        样例：(http://www.url1.com @@@@@ sitecode1 ; http://www.url2.com @@@@@ sitecode2)
     */
    public static Map<String, String> getSitecodeMap(String sitecodeConfStr) {
        Map<String, String> sitecodeMap = new HashMap<String, String>();
        if (!CommonUtil.isEmpty(sitecodeConfStr)) {
            try {
                sitecodeConfStr = sitecodeConfStr.trim();
                if (sitecodeConfStr.startsWith("(") && sitecodeConfStr.endsWith(")")) {
                    sitecodeConfStr = sitecodeConfStr.substring(1, sitecodeConfStr.length() - 1);
                    String[] array = sitecodeConfStr.split(" ; ");
                    if (array != null && array.length > 0) {
                        for (String s : array) {
                            String[] ss = s.split(" @@@@@ ");
                            if (ss != null && ss.length == 2) {
                                sitecodeMap.put(ss[0].trim(), ss[1].trim());
                            }
                        }
                    }

                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return sitecodeMap;
    }

    /**
     * 获取data与稿件对应字段表
     */
    public static LinkedHashMap<String, Map<String, String>> getReferMap() {
        LinkedHashMap<String, Map<String, String>> dataF_ManuF_ManuV = new LinkedHashMap<String, Map<String, String>>();
        try {
            @SuppressWarnings("unchecked")
            List<String> lineList = FileUtils.readLines(new File(CommonProperty.configFilePath + "referMap.properties"), "UTF-8");
            Pattern keykeyPat = Pattern.compile("^\\[(.*?)\\]$");
            Matcher keykeyMat = null;
            String keykey = "";
            for (String line : lineList) {
                if (line != null && !line.startsWith("#")) {
                    keykeyMat = keykeyPat.matcher(line);
                    if (keykeyMat.find()) {
                        keykey = keykeyMat.group(1);
                        Map<String, String> kv = new HashMap<String, String>();
                        dataF_ManuF_ManuV.put(keykey, kv);
                    } else {
                        if (!StringUtils.isEmpty(keykey)) {
                            Map<String, String> map = dataF_ManuF_ManuV.get(keykey);
                            String[] kv = StringUtils.split(line, "=");
                            if (kv.length == 2) {
                                map.put(kv[0], kv[1]);
                                dataF_ManuF_ManuV.put(keykey, map);
                            }
                        }
                    }

                }
            }
            log.info("loaded all the data fields k_v from the data.idx: " + dataF_ManuF_ManuV.size());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return dataF_ManuF_ManuV;
    }

    /**
     * 获取将某一字段按规则拆分成几个字段，对应规则的map
     */
    public static LinkedHashMap<String, Map<String, String>> getSplitMap() {
        LinkedHashMap<String, Map<String, String>> dataF_ManuF_ManuV = new LinkedHashMap<String, Map<String, String>>();
        try {
            @SuppressWarnings("unchecked")
            List<String> lineList = FileUtils.readLines(new File(CommonProperty.configFilePath + "splitregex.properties"), "UTF-8");
            Pattern keykeyPat = Pattern.compile("^\\[(.*?)\\]$");
            Matcher keykeyMat = null;
            String keykey = "";
            for (String line : lineList) {
                if (line != null && !line.startsWith("#")) {
                    keykeyMat = keykeyPat.matcher(line);
                    if (keykeyMat.find()) {
                        keykey = keykeyMat.group(1);
                        Map<String, String> kv = new HashMap<String, String>();
                        dataF_ManuF_ManuV.put(keykey, kv);
                    } else {
                        if (!StringUtils.isEmpty(keykey)) {
                            Map<String, String> map = dataF_ManuF_ManuV.get(keykey);
                            String[] kv = StringUtils.split(line, "=");
                            if (kv.length == 2) {
                                map.put(kv[0], kv[1]);
                                dataF_ManuF_ManuV.put(keykey, map);
                            }
                        }
                    }

                }
            }
            log.info("loaded all the data fields k_v from the data.idx: " + dataF_ManuF_ManuV.size());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return dataF_ManuF_ManuV;
    }

}

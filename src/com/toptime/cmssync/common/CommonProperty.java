package com.toptime.cmssync.common;

import com.toptime.cmssync.entity.SystemInfo;
import org.quartz.Scheduler;

import java.util.*;

/**
 * <B>公共变量属性</B><br>
 * 系统启动时允许修改相应value,其他位置进展修改
 *
 * @author
 */
public class CommonProperty {
    public static final String fileSep = System.getProperty("file.separator");// 目录分隔符
    public static final String lineSep = System.getProperty("line.separator"); // 回车换行符.
    /**
     * 当前工作目录.(进行打包的时候注意使用下面的工作目录)
     */
    //本地测试用
//     public static final String userDir = System.getProperty("user.dir");
    //打包运行用
    public static final String userDir = System.getProperty("user.dir").substring(0, System.getProperty("user.dir").lastIndexOf(fileSep));

    /**
     * 去重字段
     */
    public static final String KILLDUPLICATES = "REFERENCE";
    /**
     * 是否覆盖 false：覆盖 true：不覆盖
     */
    public static final String KEEPEXISTING = "false";
    /**
     * 需要对入库数据进行排重的索引库
     */
    public static final String KILLDUPLICATESMATCHDBS = "";
    /**
     * 多库去重是否生效
     */
    public static final String KILLDUPLICATESMATCHTARGETDB = "false";

    /**
     * 配置文件物理路径
     */
    public static String configFilePath = userDir + fileSep + "conf" + fileSep;

    /**
     * log4j配置文件的路径
     */
    public static String log4jPath = configFilePath + "log4j.properties";
    /**
     * 默认系统配置文件名
     */
    public static String configFileName = "config.properties";
    /**
     * 配置文件编码,默认为UTF-8 针对所有配置文件生效
     */
    public static String configFileEncoding = "UTF-8";
    /**
     * 稿件同步的时间，文件里面只存放一个时间，就是上次正确同步的时间
     */
    public static final String syncTimeFilePath = "syncTime.txt";
    /**
     * 同步稿件接口地址配置文档
     */
    public static final String interfaceUrlFilePath = "interfaceUrls.txt";

    /**
     * 顶级域名文件
     */
    public static final String topDomainFilePath = "topdomain.txt";
    /**
     * 域名映射文件
     */
    public static final String domainFilePath = "domain.txt";

    /**
     * monitor定时任务
     */
    public static Scheduler scheduler;
    /**
     * 稿件同步时间戳
     */
    public static long syncTimeStamp;

    /**
     * 是否成功同步稿件
     */
    public static boolean isSyncSuccess;
    /**
     * 系统配置
     */
    public static SystemInfo systemInfo;

    /**
     * data文件目录
     */
    public static String DATA_PATH = "";

    /**
     * data文件备份目录
     */
    public static String DATA_BACKUP_PATH = "";

    /**
     * 顶级域名 初始化 org.cn,cn,com.cn
     */
    public static Map<String, Boolean> topDomainMap = new HashMap<String, Boolean>();

    /**
     * 域名映射表
     */
    public static Map<String, String> domainMap = new HashMap<String, String>();

    /**
     * 文件类型映射表
     */
    public static Map<String, String> fileTypeMap = new HashMap<String, String>();

    /**
     * data与稿件字段对应表
     */
    public static LinkedHashMap<String, String> data_manuscript = new LinkedHashMap<String, String>();
    /**
     * data字段依赖稿件字段属性值的不同而取值不同的对应Map
     */
    public static LinkedHashMap<String, Map<String, String>> referMap = new LinkedHashMap<String, Map<String, String>>();
    /**
     * 将某一字段按规则拆分成几个字段，对应规则的map
     */
    public static LinkedHashMap<String, Map<String, String>> splitMap = new LinkedHashMap<String, Map<String, String>>();

    /**
     * 同步稿件接口地址url列表
     */
    public static List<String> interfaceUrlList = new ArrayList<String>();

    /**
     * 稿件同步时间戳，key:接口地址url，value:最新稿件时间戳
     */
    public static Map<String, Long> syncTimeStampMap = new HashMap<String, Long>();

    /**
     * 各个同步任务对应sitecode的map，key:同步稿件接口地址 value:sitecode
     */
    public static Map<String, String> sitecodeMap = new HashMap<String, String>();

}

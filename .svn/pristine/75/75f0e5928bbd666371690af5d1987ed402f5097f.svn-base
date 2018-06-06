package com.toptime.cmssync.common.core;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.util.CommonUtil;

/**
 * 从本地XML文件同步稿件的方法
 * 
 */
public class SyncLocalFileMethod {

	private static Logger logger = Logger.getLogger(SyncLocalFileMethod.class);

	/**
	 * 从本地XML文件同步稿件
	 * 
	 * @param localFileDir
	 *            文件存放目录
	 */
	public static void syncLocalFiles(String localFileDir) {

		try {

			if (!CommonUtil.isEmpty(localFileDir)) {
				File dir = new File(localFileDir);
				if (dir.exists()) {

					if (dir.isDirectory()) {
						// 同步本地XML文件
						syncAllIndex(dir);
//						syncIncrementIndex(dir);

					} else {
						logger.info("配置路径不是目录，localFileDir = " + localFileDir);
					}

				} else {
					logger.info("本地xml文件目录不存在，localFileDir = " + localFileDir);
				}
			} else {
				logger.info("本地xml文件目录为空");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	/**
	 * 同步本地xml文件
	 * 
	 * @param dir
	 */
	private static void syncAllIndex(File dir) {
//		String contnt_id_sourceChannelId_targetChannelId = "";
//		String reference = "";
		try {
			File[] files = dir.listFiles();
			for (File file1 : files) {
				if(file1.isDirectory()) {
					for (File file2 : file1.listFiles()) {
						if(file2.isDirectory()) {
							for (File file3 : file2.listFiles()) {
								if(file3.isDirectory()) {
									for (File file : file3.listFiles()) {
										if(file.isDirectory()) {
											logger.info(file.getAbsolutePath());
											String[] localFilePaths = file.list();
											
											if (localFilePaths != null && localFilePaths.length > 0) {
												logger.info("本地XML文件数量：" + localFilePaths.length + "，localFileDir = " + file.getAbsolutePath());
												// 将稿件内容存储为规定格式的索引内容
												StringBuffer sb = new StringBuffer();
												// 已处理稿件数量
												int i = 0;
												// 生成索引文件是否成功
												boolean status = false;
												// 索引入库是否成功
												boolean importDataStatus = false;
												// 索引文件路径
												String filePath = "";
												
												// 临时保存稿件站点名，即入库的库名，同一个站点的稿件放到同一个stringbuffer中
												String webSiteCodeName = "";
												// 保存各个站点的稿件信息stringbuffer
												Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();
												// 要同步的站点列表
												List<String> syncDbNames = CommonProperty.systemInfo.getSyncDbNames();
												
												for (String localFilePath : localFilePaths) {
													i++;
													logger.info("正在同步本地XML文件[" + i + "/" + localFilePaths.length + "]：" + file.getAbsolutePath() + CommonProperty.fileSep + localFilePath);
													LinkedHashMap<String, Object> dataMap = getFieldsMapFromLocalFile(file.getAbsolutePath() + CommonProperty.fileSep + localFilePath);
													//开普已修改稿件规则，不需要再单独处理
//													/********************************************************************************
//													 * 更新reference
//													 * 稿件address组成规则：content_稿件id_栏目id_抄送/聚合栏目id,
//													 * 例如：content_24043_8612472_86id2.xml	//超送到86id2栏目
//													 *     content_24043_8612472_10572.xml	//超送到10572栏目
//													 *     content_24043_8612472_8612472.xml//两个栏目id相同，即为原栏目稿件
//													 * 即同一篇稿件会抄送/聚合到别的栏目中，所以稿件id不是唯一值，应该是栏目ID+稿件ID
//													 *********************************************************************************/
//													
//													contnt_id_sourceChannelId_targetChannelId = localFilePath.substring(0,localFilePath.lastIndexOf('.'));
//													String[] splitBy_ = StringUtils.split(contnt_id_sourceChannelId_targetChannelId, "_");
//													if(splitBy_.length == 4) {
//														reference = splitBy_[3]+"_"+splitBy_[1];
//														dataMap.put("DREREFERENCE", reference);
//													}
													
													
													if (dataMap != null) {
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
														logger.error("本地XML文件同步出错，" + file.getAbsolutePath() + CommonProperty.fileSep + localFilePath);
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
																		filePath = CommonProperty.DATA_PATH + System.currentTimeMillis() + ".data";
																		status = ContentMethod.writeToIdx(sb, filePath);
																		if (status) {// 保存索引文件成功，则将StringBuffer中内容清空
																			sb.setLength(0);
																		}
																	} else {
																		logger.error("入索引失败");
																	}
																}
															}
														}
														
													}
												}
												
												// 未索引的稿件入索引
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
																filePath = CommonProperty.DATA_PATH + System.currentTimeMillis() + ".data";
																status = ContentMethod.writeToIdx(sb, filePath);
																if (status) {// 保存索引文件成功，则将StringBuffer中内容清空
																	sb.setLength(0);
																}
															} else {
																logger.error("入索引失败");
															}
														}
													}
												}
												
											}
										}
									}
								}
							}
						}
					}
					
				}
			}

			logger.info("-----------本地XML文件同步结束-----------");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}
	/**
	 * 同步本地xml文件
	 * 
	 * @param dir
	 */
	private static void syncIncrementIndex(File dir) {
		
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if(file.isDirectory()) {
					logger.info(file.getAbsolutePath());
					String[] localFilePaths = file.list();
					
					if (localFilePaths != null && localFilePaths.length > 0) {
						logger.info("本地XML文件数量：" + localFilePaths.length + "，localFileDir = " + file.getAbsolutePath());
						// 将稿件内容存储为规定格式的索引内容
						StringBuffer sb = new StringBuffer();
						// 已处理稿件数量
						int i = 0;
						// 生成索引文件是否成功
						boolean status = false;
						// 索引入库是否成功
						boolean importDataStatus = false;
						// 索引文件路径
						String filePath = "";
						
						// 临时保存稿件站点名，即入库的库名，同一个站点的稿件放到同一个stringbuffer中
						String webSiteCodeName = "";
						// 保存各个站点的稿件信息stringbuffer
						Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();
						// 要同步的站点列表
						List<String> syncDbNames = CommonProperty.systemInfo.getSyncDbNames();
						
						for (String localFilePath : localFilePaths) {
							i++;
							logger.info("正在同步本地XML文件[" + i + "/" + localFilePaths.length + "]：" + file.getAbsolutePath() + CommonProperty.fileSep + localFilePath);
							LinkedHashMap<String, Object> dataMap = getFieldsMapFromLocalFile(file.getAbsolutePath() + CommonProperty.fileSep + localFilePath);
							if (dataMap != null) {
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
								logger.error("本地XML文件同步出错，" + file.getAbsolutePath() + CommonProperty.fileSep + localFilePath);
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
												filePath = CommonProperty.DATA_PATH + System.currentTimeMillis() + ".data";
												status = ContentMethod.writeToIdx(sb, filePath);
												if (status) {// 保存索引文件成功，则将StringBuffer中内容清空
													sb.setLength(0);
												}
											} else {
												logger.error("入索引失败");
											}
										}
									}
								}
								
							}
						}
						
						// 未索引的稿件入索引
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
										filePath = CommonProperty.DATA_PATH + System.currentTimeMillis() + ".data";
										status = ContentMethod.writeToIdx(sb, filePath);
										if (status) {// 保存索引文件成功，则将StringBuffer中内容清空
											sb.setLength(0);
										}
									} else {
										logger.error("入索引失败");
									}
								}
							}
						}
						
					}
				}
					
			}
			
			logger.info("-----------本地XML文件同步结束-----------");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}
	
	/**
	 * 获取稿件详情内容，来源为本地xml文件
	 * 
	 * @param filePath
	 * @return
	 */
	private static LinkedHashMap<String, Object> getFieldsMapFromLocalFile(String filePath) {
		LinkedHashMap<String, Object> info = null;

		if (!CommonUtil.isEmpty(filePath)) {
			String responseString = "";
			try {
				// 读取本地xml文件内容
				responseString = FileUtils.readFileToString(new File(filePath), "UTF-8");
				// 解析列表页XML
				info = ParserMethod.parserXml2Map(responseString);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.info("本地xml文件路径为空");
		}
		return info;
	}

}

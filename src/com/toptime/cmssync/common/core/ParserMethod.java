package com.toptime.cmssync.common.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.entity.AppendixInfo;
import com.toptime.cmssync.entity.ChannelInfo;
import com.toptime.cmssync.entity.IndexInfo;
import com.toptime.cmssync.entity.ManuscriptInfo;
import com.toptime.cmssync.entity.MetadataInfo;
import com.toptime.cmssync.entity.WebsiteInfo;
import com.toptime.cmssync.util.CommonUtil;
import com.toptime.cmssync.util.DateUtil;

/**
 * 解析方法
 * 
 */
public class ParserMethod {

	private static Logger logger = Logger.getLogger(ParserMethod.class);

	/**
	 * 索引列表页xml解析
	 * 
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<IndexInfo> parserIndex(String xml) {
		List<IndexInfo> list = new ArrayList<IndexInfo>();

		if (CommonUtil.isEmpty(xml)) {
			logger.debug("index xml is null");
			return list;
		}

		try {

			Document document = DocumentHelper.parseText(xml.trim());
			//获取文档根节点
			Element rootElement = document.getRootElement();
			//此处为从根节点下获取的子节点名称
			//TODO
			 //rootElement = rootElement.element("list");
			//获取有用信息节点
			List<Element> manuscriptElementList = rootElement.elements("manuscript");
			
			if (manuscriptElementList != null && manuscriptElementList.size() > 0) {
				IndexInfo info = null;
				for (Element e : manuscriptElementList) {
					info = new IndexInfo();
					info.setId(e.attributeValue("id"));
					info.setType(e.attributeValue("type"));
					info.setAddress(e.attributeValue("address"));
					//开普已修改稿件规则，不需要再单独处理
//					/********************************************************************************
//					 * 更新id
//					 * 稿件address组成规则：content_稿件id_栏目id_抄送/聚合栏目id,
//					 * 例如：content_24043_8612472_86id2.xml	//超送到86id2栏目
//					 *     content_24043_8612472_10572.xml	//超送到10572栏目
//					 *     content_24043_8612472_8612472.xml//两个栏目id相同，即为原栏目稿件
//					 * 即同一篇稿件会抄送/聚合到别的栏目中，所以稿件id不是唯一值，应该是栏目ID+稿件ID
//					 *********************************************************************************/
//					
//					String contnt_id_sourceChannelId_targetChannelId = info.getAddress().substring(info.getAddress().lastIndexOf('/')+1,info.getAddress().lastIndexOf('.'));
//					String[] splitBy_ = StringUtils.split(contnt_id_sourceChannelId_targetChannelId, "_");
//					if(splitBy_.length == 4) {
//						String id = splitBy_[3]+"_"+splitBy_[1];
//						info.setId(id);
//					}
					info.setLastmodifytime(e.attributeValue("lastmodifytime"));

					list.add(info);

				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 稿件详情页xml解析
	 * 
	 * @param xml
	 */
	@SuppressWarnings("unchecked")
	public static ManuscriptInfo parserComent(String xml) {
		ManuscriptInfo info = null;

		if (CommonUtil.isEmpty(xml)) {
			logger.debug("Coment xml is null");
			return info;
		}

		try {
			info = new ManuscriptInfo();

			Document document = DocumentHelper.parseText(xml.trim());

			Element rootElement = document.getRootElement();

			info.setManuscriptId(parserCDATA(rootElement.elementText("manuscript_id")));
			info.setTitle(parserCDATA(rootElement.elementText("title")));
			info.setSubTitle(parserCDATA(rootElement.elementText("sub_title")));
			info.setMemo(parserCDATA(rootElement.elementText("memo")));
			info.setKeyword(parserCDATA(rootElement.elementText("keyword")));
			info.setSeqNum(NumberUtils.toLong(rootElement.elementText("seq_num")));
			info.setPublishedTime(DateUtil.parseStringToDate(rootElement.elementText("published_time"), "yyyy-MM-dd HH:mm:ss").getTime());
			info.setType(NumberUtils.toInt(rootElement.elementText("type")));
			info.setContent(parserCDATA(rootElement.elementText("content")));
			info.setUrl(parserCDATA(rootElement.elementText("url")));

			Element websiteElement = rootElement.element("website");

			WebsiteInfo websiteInfo = new WebsiteInfo();
			websiteInfo.setWebsiteId(websiteElement.elementText("website_id"));
			websiteInfo.setCodeName(websiteElement.elementText("code_name"));
			websiteInfo.setName(websiteElement.elementText("name"));

			info.setWebsiteInfo(websiteInfo);

			Element channelElement = rootElement.element("channel");

			ChannelInfo channelInfo = new ChannelInfo();

			channelInfo.setChannelId(channelElement.elementText("channel_id"));
			channelInfo.setCodeName(channelElement.elementText("code_name"));
			channelInfo.setChannelName(channelElement.elementText("channel_name"));
			channelInfo.setChannelLevels(channelElement.elementText("channel_levels"));
			channelInfo.setChannelLevelsNames(channelElement.elementText("channel_levels_names"));
			channelInfo.setChannelTypeId(NumberUtils.toInt(channelElement.elementText("channel_type_id")));

			info.setChannelInfo(channelInfo);

			Element metadatasElement = rootElement.element("metadatas");
			List<Element> metadataElementList = metadatasElement.elements("metadata");

			List<MetadataInfo> metadataInfoList = new ArrayList<MetadataInfo>();
			if (metadataElementList != null && metadataElementList.size() > 0) {
				MetadataInfo metadataInfo = null;
				for (Element e : metadataElementList) {
					metadataInfo = new MetadataInfo();

					metadataInfo.setName(e.attributeValue("name"));
					metadataInfo.setKey(e.attributeValue("key"));
					metadataInfo.setValue(e.attributeValue("value"));

					metadataInfoList.add(metadataInfo);

				}
			}
			info.setMetadataInfoList(metadataInfoList);

			Element appendixsElement = rootElement.element("appendixs");
			List<Element> appendixElementList = appendixsElement.elements("appendix");

			List<AppendixInfo> appendixInfoList = new ArrayList<AppendixInfo>();
			if (appendixElementList != null && appendixElementList.size() > 0) {
				AppendixInfo appendixInfo = null;
				for (Element e : appendixElementList) {
					appendixInfo = new AppendixInfo();

					appendixInfo.setFileName(e.attributeValue("file_name"));
					appendixInfo.setType(e.attributeValue("type"));
					appendixInfo.setUrl(e.attributeValue("url"));

					appendixInfoList.add(appendixInfo);

				}
			}
			info.setAppendixInfoList(appendixInfoList);

		} catch (Exception e) {
			info = null;
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return info;
	}
	
	/**
	 * 稿件详情页xml解析
	 * 根据data.idx文件制定字段，设置data字段属性值
	 * 以Map方式返回
	 * @param xml
	 */
	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String,Object> parserXml2Map(String xml) {
		LinkedHashMap<String,Object> info = null;
		
		if (CommonUtil.isEmpty(xml)) {
			logger.debug("Coment xml is null");
			return info;
		}
		
		try {
			Document document = DocumentHelper.parseText(xml.trim());
			Element rootElement = document.getRootElement();
			
			info = new LinkedHashMap<String, Object>();
			Map<String,String> fieldsMap = CommonProperty.data_manuscript;
			String dataField = "";
			String manuscriptField = "";
			String nodeValue = "";
			Element ele = rootElement;
			
			
			for (Iterator<String> iterator = fieldsMap.keySet().iterator(); iterator.hasNext();) {
				dataField = (String) iterator.next();
				manuscriptField = fieldsMap.get(dataField);
				
				//多值类型匹配:[取值字段][分隔符号][分割后新值组合形式]
				Pattern mutiPat = Pattern.compile("\\[(.*?)\\]\\[(.*?)\\]\\[(.*?)\\]");
				Matcher mutiMat = mutiPat.matcher(manuscriptField);
				
				//依赖类型匹配:<取值字段><k,v>,具体值的对应关系，在referMap.properties中定义
				Pattern referPat = Pattern.compile("<(.*?)><k,v>");
				Matcher referMat = referPat.matcher(manuscriptField);
				
				//正则提取类型：<取值字段>[regex]"正则表达式"，根据"正则表达式"中的正则提取<取值字段>中的内容
//				Pattern regPat = Pattern.compile("<(.*?)>\\[regex\\]\"(.*?)\"");
//				Matcher regMat = regPat.matcher(manuscriptField);
				
				if(manuscriptField.startsWith("{") && manuscriptField.endsWith("}")) {//{xxx}格式直接取值xxx
					info.put(dataField, manuscriptField.replaceAll("\\{", "").replaceAll("\\}", ""));
				} else if(manuscriptField.startsWith("<!--") && manuscriptField.endsWith("-->")) {
					manuscriptField = manuscriptField.replaceAll("<!--", "").replaceAll("-->", "");
					if(manuscriptField.equals("published_time")) {
						info.put(dataField, DateUtil.parseStringToDate(rootElement.elementText("published_time"), "yyyy-MM-dd HH:mm:ss").getTime() / 1000);
					} else 	if(manuscriptField.equals("metadatas")) {
	                       info.put(dataField, DateUtil.parseStringToDate(rootElement.elementText("published_time"), "yyyy-MM-dd HH:mm:ss").getTime() / 1000);
						Element metadatasElement = rootElement.element("metadatas");
//						List<Element> domainmetaElementList = metadatasElement.elements("domainmetadata");
						List<Element> metadataElementList = metadatasElement.elements("metadata");
						if (metadataElementList != null && metadataElementList.size() > 0) {
							List<MetadataInfo> metadataInfoList = new ArrayList<MetadataInfo>();
//							String domainMetadataCode = "";
							for (Element metadata : metadataElementList) {
//								domainMetadataCode = domainmeta.attributeValue("domainMetadataCode");
								    MetadataInfo metadataInfo = null;
                                    metadataInfo = new MetadataInfo();
//                                  metadataInfo.setDomainMetadataCode(domainMetadataCode);
                                    metadataInfo.setName(metadata.attributeValue("name"));
                                    if (metadata.attributeValue("key").equals("lawfwjg")) {
                                        metadataInfo.setKey("PUBNAME");
                                    }else if (metadata.attributeValue("key").equals("lawzh")) {
                                        metadataInfo.setKey("DOCNO");
                                    }else{
                                        metadataInfo.setKey(metadata.attributeValue("key"));
                                    }
                                    metadataInfo.setValue(metadata.attributeValue("value"));
                                    metadataInfoList.add(metadataInfo);
									
									
							}
							info.put(dataField, metadataInfoList);
						}
/*						if (domainmetaElementList2 != null && domainmetaElementList2.size() > 0) {
						    List<MetadataInfo> metadataInfoList = new ArrayList<MetadataInfo>();
						    String domainMetadataCode = "";
						    for (Element domainmeta : domainmetaElementList2) {
						        domainMetadataCode = domainmeta.attributeValue("domainMetadataCode");
						        List<Element> metadataElementList = domainmeta.elements("metadata");
						        if (metadataElementList != null && metadataElementList.size() > 0) {
						            MetadataInfo metadataInfo = null;
						            for (Element e : metadataElementList) {
						                metadataInfo = new MetadataInfo();
						                metadataInfo.setDomainMetadataCode(domainMetadataCode);
						                metadataInfo.setName(e.attributeValue("name"));
						                metadataInfo.setKey(e.attributeValue("key"));
						                metadataInfo.setValue(e.attributeValue("value"));
						                metadataInfoList.add(metadataInfo);
						            }
						        }
						    }
						    info.put(dataField, metadataInfoList);
						}
*/						
					} else if(manuscriptField.equals("appendixs")) {
						Element appendixsElement = rootElement.element("appendixs");
						List<Element> appendixElementList = appendixsElement.elements("appendix");
						List<AppendixInfo> appendixInfoList = new ArrayList<AppendixInfo>();
						if (appendixElementList != null && appendixElementList.size() > 0) {
							AppendixInfo appendixInfo = null;
							for (Element e : appendixElementList) {
								appendixInfo = new AppendixInfo();
								appendixInfo.setFileName(e.attributeValue("file_name"));
								appendixInfo.setType(e.attributeValue("type"));
								appendixInfo.setUrl(e.attributeValue("url"));
								appendixInfoList.add(appendixInfo);
							}
						}
						info.put(dataField, appendixInfoList);
					} else {
						ele = rootElement;//重新从根目录开始
						nodeValue = getPathNodeValue(ele,manuscriptField);
						info.put(dataField, parserCDATA(nodeValue));
					}
				} else if(mutiMat.find()) {
					
					String nodePath = mutiMat.group(1);
					String splitStr = mutiMat.group(2);
					String groupType = mutiMat.group(3);
					
					ele = rootElement;//重新从根目录开始
					nodeValue = getPathNodeValue(rootElement,nodePath);
					
					String[] valueList = StringUtils.split(parserCDATA(nodeValue), splitStr);
					if(groupType.equals("0")) {
						info.put(dataField, valueList);
					} else if(groupType.equals("1")) {
						
					} else {
						
					}
					
				} else if(referMat.find()) {
					
					String nodePath = referMat.group(1);
					ele = rootElement;//重新从根目录开始;
					nodeValue = getPathNodeValue(ele,nodePath);
					
					info.put(dataField, com.toptime.cmssync.util.StringUtils.passerStr(CommonProperty.referMap.get(dataField+"∝"+nodePath).get(parserCDATA(nodeValue))));
					
//				} else if(regMat.find()) {
//					
//					String nodePath = regMat.group(1);
//					ele = rootElement;//重新从根目录开始;
//					nodeValue = getPathNodeValue(ele,nodePath);
//					String regex = regMat.group(2);
//					Pattern subRegex = Pattern.compile(regex);
//					Matcher subMat = subRegex.matcher(nodeValue);
//					if(subMat.find()) {
//						String subValue = subMat.group();
//						info.put(dataField, parserCDATA(subValue));
//					}
					
					
				} else {
					
				}
				
			}
			
		} catch (Exception e) {
			info = null;
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return info;
	}
	
	public static String getPathNodeValue(Element ele, String nodePath) {
		String nodeValue = "";
		String nodeName = "";
		
		String[] pathNodes = StringUtils.split(nodePath, "/");
		for (int i = 0; i < pathNodes.length; i++) {
			nodeName = pathNodes[i];
			if(i == pathNodes.length - 1) {
				nodeValue = com.toptime.cmssync.util.StringUtils.passerStr(ele.elementText(nodeName));
			} else {
				ele = ele.element(nodeName);
				if(ele == null) {
					nodeValue  = "";
					break;
				}
			}
		}
		
		return nodeValue;
	}

	/**
	 * 以post方式向索引库添加数据状态解析
	 * 
	 * @param xml
	 * @return
	 */
	public static boolean parserDreAddData(String xml) {

		boolean status = false;

		if (CommonUtil.isEmpty(xml))
			return status;

		try {

			Document document = DocumentHelper.parseText(xml);

			Element rootElement = document.getRootElement();
			String response = rootElement.elementText("response");
			if (response.equals("SUCCESS") || response.equals("TRUE")) {
				status = true;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return status;
	}

	/**
	 * 解析XML元素文本，删除<![CDATA[]]>
	 * 
	 * @param text
	 * @return
	 */
	private static String parserCDATA(String text) {
		String s = "";
		if (!CommonUtil.isEmpty(text)) {
			s = text.trim();
			if (s.startsWith("<![CDATA[") && s.endsWith("]]>")) {
				s = s.substring(9, s.length() - 3);
			}
		}

		return s;
	}
	/**
	 * 解析XML并且删除正文中不合适的垃圾元素元素文本，删除<![CDATA[]]>
	 * 
	 * @param text
	 * @return
	 */
	private static String parserCDATA2(String text) {
	    String s = "";
	    if (!CommonUtil.isEmpty(text)) {
	        s = text.trim();
	        if (s.startsWith("<![CDATA[") && s.endsWith("]]>")) {
	            s = s.substring(9, s.length() - 3);
	        }
	    }
	    
	    return s;
	}
	
	public static void main(String[] args) {
		String xml;
		try {
			xml = FileUtils.readFileToString(new File("D:\\work\\document\\cms\\test.xml"));
			Date b = new Date();
			Map<String,Object> dataMap = parserXml2Map(xml);
			StringBuffer buffer = new StringBuffer();
			ContentMethod.addMap2Buffer(dataMap, buffer);
			Date e = new Date();
			System.out.println(e.getTime()-b.getTime() + "ms");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

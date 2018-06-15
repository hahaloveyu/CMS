package com.toptime.cmssync.entity;

import java.util.List;


/**
 * 稿件详情实体类
 * 
 */
public class ManuscriptInfo {
	private String manuscriptId; // 稿件ID
	private String title; // 标题
	private String subTitle; // 显示标题
	private String memo; // 描述
	private String keyword; // 关键字
	private long seqNum; // 排序号
	private long publishedTime; // 发布时间
	private int type; // 类型：0,、普通稿件；1、图片稿件；5、元数据稿件
	private String content; // 稿件内容
	private String url; // URL地址
	private WebsiteInfo websiteInfo; // 站点对象
	private ChannelInfo channelInfo; // 栏目对象
	private List<MetadataInfo> metadataInfoList; // 元数据列表
	private List<AppendixInfo> appendixInfoList; // 附件列表

	public String getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(String manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public long getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(long seqNum) {
		this.seqNum = seqNum;
	}

	public long getPublishedTime() {
		return publishedTime;
	}

	public void setPublishedTime(long publishedTime) {
		this.publishedTime = publishedTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
	    
		return content.replaceAll("\\.TRS_Editor(.*?)}", "".trim());
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public WebsiteInfo getWebsiteInfo() {
		return websiteInfo;
	}

	public void setWebsiteInfo(WebsiteInfo websiteInfo) {
		this.websiteInfo = websiteInfo;
	}

	public ChannelInfo getChannelInfo() {
		return channelInfo;
	}

	public void setChannelInfo(ChannelInfo channelInfo) {
		this.channelInfo = channelInfo;
	}

	public List<MetadataInfo> getMetadataInfoList() {
		return metadataInfoList;
	}

	public void setMetadataInfoList(List<MetadataInfo> metadataInfoList) {
		this.metadataInfoList = metadataInfoList;
	}

	public List<AppendixInfo> getAppendixInfoList() {
		return appendixInfoList;
	}

	public void setAppendixInfoList(List<AppendixInfo> appendixInfoList) {
		this.appendixInfoList = appendixInfoList;
	}

}

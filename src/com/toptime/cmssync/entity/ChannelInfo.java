package com.toptime.cmssync.entity;
/**
 * 栏目对象实体类
 * 
 */
public class ChannelInfo {
	private String channelId; // 栏目ID
	private String codeName; // 栏目代号
	private String channelName; // 栏目名称
	private String channelLevels; // 栏目层级代号
	private String channelLevelsNames; // 栏目层级名称
	private int channelTypeId; // 栏目类型

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelLevels() {
		return channelLevels;
	}

	public void setChannelLevels(String channelLevels) {
		this.channelLevels = channelLevels;
	}

	public String getChannelLevelsNames() {
		return channelLevelsNames;
	}

	public void setChannelLevelsNames(String channelLevelsNames) {
		this.channelLevelsNames = channelLevelsNames;
	}

	public int getChannelTypeId() {
		return channelTypeId;
	}

	public void setChannelTypeId(int channelTypeId) {
		this.channelTypeId = channelTypeId;
	}

}

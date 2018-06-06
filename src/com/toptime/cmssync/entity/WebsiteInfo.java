package com.toptime.cmssync.entity;
/**
 * 站点对象实体类
 * 
 */
public class WebsiteInfo {
	private String websiteId; // 站点ID
	private String codeName; // 站点代号
	private String name; // 站点名称

	public String getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(String websiteId) {
		this.websiteId = websiteId;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

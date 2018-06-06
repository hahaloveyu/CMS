package com.toptime.cmssync.entity;

/**
 * 从domain.txt文件中获得domainsitename，
 * domainsite，parentsite，websitename 实体类
 * @author CFJ
 *
 */
public class SiteName {
	
	/**父url所在的域名**/
	private String parentSite="";
	
	/**站点名称**/
	private String WebsiteName="";

	/**主域名**/
	private String domainSite="";
	
	/**主域名名称**/
	private String domainSiteName="";
	
	
	/**
	 * 父url所在的域名
	 **/
	public String getParentSite() {
		return parentSite;
	}
	/**
	 * 父url所在的域名
	 **/
	public void setParentSite(String parentSite) {
		this.parentSite = parentSite;
	}
	
	/**
	 * 站点名称
     */
	public String getWebsiteName() {
		return WebsiteName;
	}
	/**
	 * 站点名称
     */
	public void setWebsiteName(String websiteName) {
		this.WebsiteName = websiteName;
	}
	
	/**
	 * 主域名
	 **/
	public String getDomainSite() {
		return domainSite;
	}
	
	/**
	 * 主域名
	 **/
	public void setDomainSite(String domainSite) {
		this.domainSite = domainSite;
	}
	
	/**
	 *主域名名称
	 */
	public String getDomainSiteName() {
		return domainSiteName;
	}
	
	/**
	 *主域名名称
	 */
	public void setDomainSiteName(String domainSiteName) {
		this.domainSiteName = domainSiteName;
	}
	

}

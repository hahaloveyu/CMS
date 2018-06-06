package com.toptime.cmssync.common.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.toptime.cmssync.common.CommonProperty;
import com.toptime.cmssync.entity.SiteName;

/**
 * 处理URL和linkUrl
 * 
 * @return domainsitename,parentsite,donmainsite
 * 
 * @author CFJ
 * 
 */
public class ParseSiteNameMethod {

	static Log logger = LogFactory.getLog(ParseSiteNameMethod.class);

	/**
	 * 得到domainsitename,domainsite
	 * 
	 * @param linkUrl
	 *            当前要处理的URL,可能转向
	 * @param data
	 * @return
	 */
	public static SiteName getDomainSiteName(String linkUrl) {
		SiteName data = new SiteName();
		String linkHost = "";
		String link_Host = "";

		try {

			linkHost = new URL(linkUrl).getHost();
			link_Host = linkHost;

			int a = 0;
			if (CommonProperty.topDomainMap.get(linkHost.substring(linkHost.indexOf(".") + 1)) != null) {
				a = -1; // . 的初始位置
			} else {
				while (linkHost.indexOf(".") != -1) {
					a = linkHost.indexOf("."); // . 的最终位置
					linkHost = linkHost.replaceFirst("\\.", ";");
					if (CommonProperty.topDomainMap.get(linkHost.substring(linkHost.indexOf(".") + 1)) != null) {
						break;
					}
				}
			}

			String sublinkHost = link_Host.substring(a + 1);
			if (link_Host.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
				sublinkHost = link_Host;
			}
			data.setDomainSite(sublinkHost);

			if (CommonProperty.topDomainMap.size() > 0 && CommonProperty.domainMap.get(link_Host.toLowerCase()) != null) {// 从外部文件获得
				data.setDomainSiteName(CommonProperty.domainMap.get(link_Host.toLowerCase()));
			} else if (!data.getWebsiteName().equals("")) {
				data.setDomainSiteName(data.getWebsiteName());
			} else {
				data.setDomainSiteName(sublinkHost);
			}
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
		}
		return data;
	}

}

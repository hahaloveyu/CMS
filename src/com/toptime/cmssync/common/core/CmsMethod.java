package com.toptime.cmssync.common.core;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.toptime.cmssync.entity.IndexInfo;
import com.toptime.cmssync.entity.ManuscriptInfo;
import com.toptime.cmssync.util.CommonUtil;

/**
 * 请求CMS接口
 * 
 * @author
 * 
 */
public class CmsMethod {

	private static Log logger = LogFactory.getLog(CmsMethod.class);

	private static DefaultHttpClient httpClient = null;

	private static final boolean DEBUG_STATUS = false;// 调试

	static {
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setMaxTotal(100);
		httpClient = new DefaultHttpClient(cm);// 多线程使用.
		// 连接超时(创建Socket)
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		// 获取响应内容超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
	}

	/**
	 * 获取列表页内容
	 * 
	 * @param url
	 * @return
	 */
	public static List<IndexInfo> getIndexInfoList(String url) {
		List<IndexInfo> list = null;

		if (!CommonUtil.isEmpty(url)) {
			HttpResponse response = null;
			// HttpPost httpPost = new HttpPost(url);
			HttpGet httpGet = new HttpGet(url);

			try {

				response = httpClient.execute(httpGet);
				int statusCode = response.getStatusLine().getStatusCode();

				// 返回服务器响应
				if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NOT_MODIFIED) {
					HttpEntity entity = response.getEntity();
					// 获取响应内容
					String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
					if (DEBUG_STATUS) {
						System.out.println(responseString);
					}

	/****************手动设置XML稿件信息**********潮州/茂名**************************/  
//					responseString = "<?xml version='1.0' encoding='UTF-8'?><index><msg state='SUCCESS-1'>&lt;![CDATA[query success]]&gt;</msg><list pageSize='100' pageCurrent='1' totalCount='1' totalPage='1' listCount='1'><manuscript id='eabc1208111342f2ae1d12ec8483f375' title='江门市地税局开展环保税宣传活动' type='delete' lastmodifytime='2018-01-31 15:09:04' address='singleManu4search.jsp?manuscriptId=eabc1208111342f2ae1d12ec8483f375' /><manuscript id='c8c6bdd59ed14f748d34b20b3ba5371e' title='韶关地方税务局2017年12月税费收入情况' type='delete' lastmodifytime='2018-01-31 15:09:04' address='singleManu4search.jsp?manuscriptId=c8c6bdd59ed14f748d34b20b3ba5371e' /><manuscript id='7392af06819b4c0281045d21360cce7e' title='关于基本医疗保险征收品目名称变更的通知' type='delete' lastmodifytime='2018-01-31 15:09:04' address='singleManu4search.jsp?manuscriptId=7392af06819b4c0281045d21360cce7e' /><manuscript id='4627ac2c10c84008901ca050045f519e' title='刀刃向内攻坚克难 征管体制改革持续加力' type='delete' lastmodifytime='2018-01-31 15:09:04' address='singleManu4search.jsp?manuscriptId=4627ac2c10c84008901ca050045f519e' /><manuscript id='dfb4f584749a40e3b24cdce918c92c1f' title='南沙地税推出《税收那些事儿》之“微信申报缴纳契税”动画片' type='delete' lastmodifytime='2018-01-31 15:09:04' address='singleManu4search.jsp?manuscriptId=dfb4f584749a40e3b24cdce918c92c1f' /><manuscript id='dc3afe4acfdb4164bf314aa0887e57bd' title='云浮罗定市国地税部门打造“四同”合作品牌助推地方经济发展' type='delete' lastmodifytime='2018-01-31 15:09:04' address='singleManu4search.jsp?manuscriptId=dc3afe4acfdb4164bf314aa0887e57bd' /></list></index>";
	/****************手动设置XML稿件信息************************************/  
					
					// 解析列表页XML
					list = ParserMethod.parserIndex(responseString);

					// 销毁
					EntityUtils.consume(entity);
				} else {
					logger.error("CMS列表页无法连通：" + url);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				try {
					httpGet.abort();
				} catch (Exception e2) {
					logger.error(e2.getMessage());
				}
			}
		}
		return list;
	}

	/**
	 * 获取稿件详情内容
	 * 
	 * @param url
	 * @return
	 */
	public static LinkedHashMap<String, Object> getFieldsMap(String url) {
		LinkedHashMap<String, Object> info = null;

		if (!CommonUtil.isEmpty(url)) {
			HttpResponse response = null;
			HttpGet httpGet = new HttpGet(url);
			
			/*******************接口详情页url****************************/
			logger.info("Details page url：" + url);
			/***********************************************/

			try {

				response = httpClient.execute(httpGet);
				int statusCode = response.getStatusLine().getStatusCode();

				// 返回服务器响应
				if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NOT_MODIFIED) {
					HttpEntity entity = response.getEntity();
					// 获取响应内容
					String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
					if (DEBUG_STATUS) {
						System.out.println(responseString);
					}

					// 解析列表页XML
					info = ParserMethod.parserXml2Map(responseString);

					// 销毁
					EntityUtils.consume(entity);
				} else {
					logger.error("CMS稿件详情页无法连通：" + url);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				try {
					httpGet.abort();
				} catch (Exception e2) {
					logger.error(e2.getMessage());
				}
			}
		}
		return info;
	}

	public static LinkedHashMap<String, Object> getFieldsMapDebug(String url) {
		LinkedHashMap<String, Object> info = null;
		String responseString;
		try {
			responseString = FileUtils.readFileToString(new File("C:\\Users\\leizhenyu\\Desktop\\1.xml"));
			// 解析列表页XML
			info = ParserMethod.parserXml2Map(responseString);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (DEBUG_STATUS) {
			System.out.println(responseString);
		}
		return info;
	}

	/**
	 * 获取稿件详情内容
	 * 
	 * @param url
	 * @return
	 */
	public static ManuscriptInfo getManuscriptInfo(String url) {
		ManuscriptInfo info = null;

		if (!CommonUtil.isEmpty(url)) {
			HttpResponse response = null;
			// HttpPost httpPost = new HttpPost(url);
			HttpGet httpGet = new HttpGet(url);

			try {

				response = httpClient.execute(httpGet);
				int statusCode = response.getStatusLine().getStatusCode();

				// 返回服务器响应
				if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NOT_MODIFIED) {
					HttpEntity entity = response.getEntity();
					// 获取响应内容
					String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
					if (DEBUG_STATUS) {
						System.out.println(responseString);
					}

					// 解析列表页XML
					info = ParserMethod.parserComent(responseString);

					// 销毁
					EntityUtils.consume(entity);
				} else {
					logger.error("CMS稿件详情页无法连通：" + url);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				try {
					httpGet.abort();
				} catch (Exception e2) {
					logger.error(e2.getMessage());
				}
			}
		}
		return info;
	}

}

package com.toptime.cmssync.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javaxt.io.Image;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.alibaba.dubbo.rpc.RpcException;
import com.toptime.cmssync.entity.ImgInfo;

/**
 * 缩略图片处理 Created by bjoso on 2017/8/14.
 */
public class ThumbnailParserUtil {
	private static Logger logger = Logger.getLogger(ThumbnailParserUtil.class);
	/**
	 * 图片 宽/高 比例阈值
	 */
	private static final double WIDTH_HEIGHT_RATIO = 3.3;
	/**
	 * 宽高 限值
	 */
	private static final int WIDTH_RATIO = 300;
	/**
	 * 高度限值
	 */
	private static final int HEIGHT_RATIO = 180;
	/**
	 * 生成新闻列表页缩略图的宽
	 */
	private static final int DEFAULT_NEWS_IMG_WIDTH = 121;
	/**
	 * 生成新闻列表页缩略图的高
	 */
	private static final int DEFAULT_NEWS_IMG_HEIGHT = 75;
	/**
	 * 生成大缩略图的宽
	 */
	private static final int DEFAULT_BIG_IMG_WIDTH = 180;
	/**
	 * 生成大缩略图的高
	 */
	private static final int DEFAULT_BIG_IMG_HEIGHT = 112;
	/**
	 * 符合要求的图片计数器
	 */
	private static final int COUNT = 3;
	/**
	 * 累计不符合要求的图片计数器
	 */
	private static final int NOHANDLE = 10;
	/**
	 * 获取图片超时 为null的计数器
	 */
	private static final int TIMEOUT = 2;
	
	/*** 图片类型 */
	private static Map<String, String> mimeMap = new HashMap<String, String>();

	static {
		mimeMap.put("image/jpeg", ".jpg");
		mimeMap.put("image/gif", ".gif");
		mimeMap.put("image/png", ".png");
	}

	/**
	 * 获取页面上所有是否符合的图片的base64
	 *
	 * @param url
	 *            页面URL
	 * @param html
	 *            页面源码
	 * @return 图片信息对象l集合
	 */
	public static List<ImgInfo> getAllImgBase64(String url, String html) {
		int count = 0;
		int nohandle = 0;
		int timeout = 0;
		List<ImgInfo> imgInfoList = new ArrayList<ImgInfo>();
		// 获取页面所有的图片链接
		LinkedHashMap<String, Map<String, String>> linkMap = getAllImgUrl(url, html);
		if (linkMap != null && linkMap.size() > 0) {
			for (String imageUrl : linkMap.keySet()) {
				// 只取最多三张图片
				if (count == COUNT || nohandle == NOHANDLE || timeout == TIMEOUT) {
					break;
				}
				try {
					ImgInfo imgInfo = new ImgInfo();
					imgInfo.setUrl(imageUrl);
					Image image = getImageX(imageUrl, imgInfo);
					// 过滤不合要求的图片
					if (isQualified(image, linkMap.get(imageUrl))) {
						// 新闻图片base64
						BufferedImage newsbi = disposeImage(image, DEFAULT_NEWS_IMG_WIDTH, DEFAULT_NEWS_IMG_HEIGHT);
						if (newsbi != null) {
							imgInfo.setNewsThumbnail(getImageBase64(newsbi, imgInfo.getType()));
						}
						// 大缩略图base64
						BufferedImage bi = disposeImage(image, DEFAULT_BIG_IMG_WIDTH, DEFAULT_BIG_IMG_HEIGHT);
						if (bi != null) {
							imgInfo.setThumbnail(getImageBase64(bi, imgInfo.getType()));
						}
						imgInfoList.add(imgInfo);
						count++;
					} else {
						nohandle++;
					}
				} catch (RpcException e) {
					timeout++;
					logger.error(e.getMessage());
				} catch (RuntimeException e) {
					logger.error(e + "url:" + url + " | imgUrl:" + imageUrl);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
		return imgInfoList;
	}


	/**
	 * 获取所有图片链接
	 *
	 * @param url
	 *            页面URL
	 * @param html
	 *            页面源码
	 * @return 返回map key:图片URL value:标题
	 */
	public static LinkedHashMap<String, Map<String, String>> getAllImgUrl(String url, String html) {
		LinkedHashMap<String, Map<String, String>> linkMap = new LinkedHashMap<String, Map<String, String>>();
		Map<String, String> suspectedUrlsMap = new HashMap<String, String>();
		// 图片链接
		Map<String, Map<String, String>> imgLinkMap = MyHtmlUtils.parserImgLinks(html, url, suspectedUrlsMap);
		if (imgLinkMap != null && imgLinkMap.size() > 0) {
			linkMap.putAll(imgLinkMap);
		}
		return linkMap;
	}

	/**
	 * 判断图片是否符合要求
	 *
	 * @param image
	 *            图片
	 * @return
	 */
	private static boolean isQualified(Image image, Map<String, String> attrMap) {
		if (image == null) {
			return false;
		}
		int imageHeight = image.getHeight();
		int imageWidth = image.getWidth();
		if (attrMap != null && attrMap.size() == 2) {
			String size = attrMap.get("size");
			String[] split = size.split("x");
			if (split.length == 2) {
				imageWidth = Integer.valueOf(split[0]);
				imageHeight = Integer.valueOf(split[1]);
			}
		}
		// 原始图片长或宽小于需要的图片大小,不符合要求
		if ((double) imageWidth / (double) imageHeight > WIDTH_HEIGHT_RATIO) {// 宽高比超过阈值
			return false;
		}
		return imageWidth >= WIDTH_RATIO && imageHeight >= HEIGHT_RATIO;
	}

	/**
	 * 获取图片
	 *
	 * @param imageUrl
	 *            图片地址
	 * @param imgInfo
	 *            图片对象
	 * @return
	 */
	private static Image getImageX(String imageUrl, ImgInfo imgInfo) {
		Image image = null;

		try {
			// 限定图片大小为3M，超过3M的图片不处理
			byte[] buf = new byte[1024 * 1024 * 3];
			HttpURLConnection conn = null;
			InputStream in = null;
			try {
				URL url = new URL(imageUrl);
				// HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36");
				conn.setConnectTimeout(30000);// 连接超时30s
				conn.setReadTimeout(30000);
				conn.setRequestMethod("GET");
				conn.setUseCaches(false);// 不使用缓存
				conn.setInstanceFollowRedirects(true);
				conn.connect();
				if (conn.getResponseCode() == 200) {
					in = conn.getInputStream();
					int readlen = 0;
					int contentlen = 0;
					do {
						// 一次读取10KB，如果返回值小于0可以认为一定读取完毕
						contentlen += readlen;
						readlen = in.read(buf, contentlen, 1024 * 10);
					} while (readlen > 0);
					image = new Image(buf);// 利用ImageIcon获取Image对象
					imgInfo.setType(mimeMap.get(conn.getContentType()));
	                imgInfo.setImageLength(buf.length + "");
	                // 图片长*宽
	                imgInfo.setImageSize(image.getWidth() + "×" + image.getHeight());
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				// logger.info("图片链接无法访问");
				image = null;
				return null;
			} finally {
				try {
					// 关闭流
					if (in != null) {
						in.close();
					}
					// 切断链接
					if (conn != null) {
						conn.disconnect();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return image;
	}

	/**
	 * 处理图片 裁剪 缩放 加水印等
	 *
	 * @param image
	 *            原始图片
	 * @param purpWidth
	 *            目标宽度
	 * @param purpHeight
	 *            目标高度
	 * @return
	 */
	private static BufferedImage disposeImage(Image image, int purpWidth, int purpHeight) {
		Image copy = image.copy();
		BufferedImage bufferedImage = null;
		int imageWidth = copy.getWidth();
		int imageHeight = copy.getHeight();
		if (imageWidth < purpHeight || imageHeight < purpHeight) {
			logger.error("图片实际宽高小于目标宽高,不能进行裁切缩放.");
			return null;
		}
		// 原始图片的宽高比
		double imageRatio = (double) imageWidth / (double) imageHeight;
		// 生成缩略图的宽高比
		double newThumbnailRatio = (double) purpWidth / (double) purpHeight;
		if (imageRatio > newThumbnailRatio) {
			// 图片宽高比大于缩略图的宽高比，高不变，宽度进行截取，取中
			int newImageWidth = (int) (newThumbnailRatio * imageHeight);
			// 裁切
			copy.crop((imageWidth - newImageWidth) / 2, 0, newImageWidth, imageHeight);
			// 缩放到指定宽高
			copy.setWidth(purpWidth);
			copy.setHeight(purpHeight);
			bufferedImage = copy.getBufferedImage();
		} else if (imageRatio < newThumbnailRatio) {
			// 图片宽高比小于缩略图的宽高比，宽不变，高度进行截取，取中
			int newImageHeight = (int) (imageWidth / newThumbnailRatio);
			// 裁切
			copy.crop(0, (imageHeight - newImageHeight) / 4, imageWidth, newImageHeight);
			// 缩放到指定宽高
			copy.setWidth(purpWidth);
			copy.setHeight(purpHeight);
			bufferedImage = copy.getBufferedImage();
		}
		return bufferedImage;
	}

	/**
	 * 获取图片base64
	 *
	 * @param bi
	 *            图片对象BufferedImage
	 * @return 返回图片base64码
	 */
	private static String getImageBase64(BufferedImage bi, String suffix) {
		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream();
			ImageIO.write(bi, suffix.replaceFirst("\\.", ""), output);
			byte[] bytes = output.toByteArray();
			// 去除空格回车换行
			return Base64.encodeBase64String(bytes).trim().replaceAll("[\\t\\n\\r]", "");
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return "";
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	public static void main(String[] args) {
		String c = "<div class='Custom_UnionStyle'>"+
				"<p align='center'><img style='border-left-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-top-width: 0px' alt uploadpic='http://www.spp.gov.cn/spp/zdgz/201801/611f76dffbd94ef38d634a4121531e83/images/3525fff6be514f7bb22f9735321e146e.jpg' src='367731/images/45a0359af6e74b2b9bc5182159593073.jpg' oldsrc='http://www.spp.gov.cn/spp/zdgz/201801/611f76dffbd94ef38d634a4121531e83/images/3525fff6be514f7bb22f9735321e146e.jpg' /></p>"+
				"<p align='center'>冯键 四川省人民检察院党组书记、代理检察长</p>"+
				"<p>　　党的十九大提出了在中国特色社会主义发展新时代具有全局性、战略性、前瞻性的行动纲领,明确了建设中国特色社会主义法治体系、建设社会主义法治国家的目标任务。四川省检察机关召开专题会议,细化工作方案,部署安排了一系列学习宣传贯彻党的十九大精神的措施,坚定不移以习近平新时代中国特色社会主义思想为指导,振奋精神,履职担当,努力为实现宏伟蓝图、书写新时代“四个伟大”辉煌篇章贡献检察力量。</p>"+
				"<p>　　<strong>一、旗帜鲜明讲政治,毫不动摇坚持党的领导。</strong>党的十九大最大的亮点就是科学提出了习近平新时代中国特色社会主义思想,并写入党章,作为我们根本指导思想和行动指南。检察机关作为党领导的法律监督机关,要深刻学习领会习近平新时代中国特色社会主义思想的核心要义、精神实质和丰富内涵,特别是坚持全面依法治国、深化依法治国实践的部署安排,真正学懂弄通做实。要通过常态化制度化推进“两学一做”学习教育,以及即将开展的“不忘初心、牢记使命”主题教育,系统加强对党的基本理论、基本路线、基本方略的学习,引导检察人员进一步坚定“四个自信”,坚定不移走中国特色社会主义法治道路,坚定不移做中国特色社会主义的践行者和守卫者。要始终坚持党对检察工作的领导,通过持续整改落实最高检巡视“回头看”反馈意见,深入开展“四对照四提升 做忠诚卫士”专题教育,切实强化“四个意识”,自觉维护以习近平同志为核心的党中央权威和集中统一领导。</p>"+
				"<p>　　<strong>二、围绕中心强保障,依法履行法律监督职责。</strong>要立足检察机关的宪法定位和法律职责,坚持以司法办案为中心,统筹做好打击犯罪、化解风险与维护稳定、促进发展各项工作,确保检察工作始终与中心工作同频共振。一是树立新发展理念,把握稳中求进工作总基调,围绕科教兴国、人才强国、创新驱动发展、乡村振兴、区域协调发展、可持续发展、军民融合发展和“一带一路”建设等重大战略部署,加强法律监督,更好运用法治思维和法治方式推动四川经济社会持续健康发展,为决胜全面小康提供有力法治保障。二是坚持总体国家安全观,更加自觉维护我国主权、安全、发展利益,依法打击影响群众安全感、幸福感的各类犯罪,积极参与社会治理创新,为建设更高水平的平安中国发挥好检察职能作用。三是实践以人民为中心的发展思想,围绕保障发展教育事业、加强社会保障体系建设、美丽中国、健康中国建设等重大战略部署,加强和改进民生检察工作,守卫人民群众对美好生活的向往。四是深化全面依法治国实践,把检察工作融入立法、执法、司法、守法各个环节,围绕完善法律体系,推进法治政府、法治社会建设和促进公正司法等方面的重点任务,找准新接口、拓展新内涵、彰显新成效,为推动治蜀兴川各项事业纳入法治轨道履职尽责。</p>"+
				"<p>　　<strong>三、主动作为谋发展,推动检察事业创新升级。</strong>坚持把改革创新作为引领检察事业发展的重要动力,突出重点,抓住关键,大胆探索,积极作为,始终用创新思维、创新手段破解检察工作中的难题。要聚焦法律监督职责,不断丰富、拓宽检察监督内涵和渠道,推动检察事业创新发展。一是进一步加大对侦查、审判、刑事执行的监督力度,改变重个案、轻类案的做法,防止碎片化,推动解决以言代法、以权压法、逐利违法、徇私枉法等问题。二是更加重视民事、行政检察工作,切实提高民事、行政诉讼监督和执行监督能力；稳步开展公益诉讼,依法保护国家利益和社会公共利益,促进依法行政和法治政府建设。三是进一步拓展和强化检察建议作用,结合司法办案为党委政府建言献策,加强跟踪监督,推动有关单位完善制度、提高治理水平。四是创新开展检察机关向社会开放工作,通过现场、实景互动式、体验式的宣传、教育,变事后惩治为提前预防。在未成年人检察工作中,充分利用未检工作区、警示教育基地等检察资源,采取“检察开放日”“检察法治课”等多种形式向学生、教师、家长等开放,实现法治教育从娃娃抓起。</p>"+
				"<p>　　<strong>四、统筹推进抓落实,确保改革全面落地见效。</strong>党的十九大对深化国家监察体制改革、深化司法体制改革作出重大决策部署。全省检察机关要深刻认识扩大国家监察体制改革试点是贯彻落实党的十九大精神、推动全面从严治党向纵深发展、巩固深化反腐败斗争压倒性态势的重大战略举措,深刻认识改革对于健全中国特色国家监察体制、强化党和国家自我监督的重大意义,切实增强“四个意识”,强化政治担当,坚决拥护、自觉支持、全力配合国家监察体制改革。当前,要在党委和改革试点工作小组领导下,全力配合做好职能划转和机构人员转隶等工作,做好思想政治工作,不折不扣抓好试点方案在全省检察机关的组织实施,确保如期高质量完成改革试点任务。转隶后,要加强与纪委、监察委的联系沟通,积极探索建立协调衔接机制,实现监察委调查与检察机关刑事诉讼无缝衔接。要进一步推进司法体制综合配套制度改革,全面落实司法责任制,加强检察官正规化专业化职业化建设,推进内设机构改革,健全完善检察官职业保障制度,深化以审判为中心的诉讼制度改革,大力发展智慧检务,不断增强改革的整体效能,努力让人民群众在每一个司法案件中感受到公平正义。</p>"+
				"<p>　<strong>　五、从严治检抓基础,锻造“五个过硬”检察铁军。</strong>在十九大报告中,习近平总书记鲜明指出“打铁必须自身硬”,对坚持党要管党、全面从严治党,建设高素质专业化干部队伍等提出明确要求。检察机关要始终保持“正人先正己”的警醒,全面加强党的建设,抓班子、带队伍、严纪律,着力打造一支政治过硬、业务过硬、责任过硬、纪律过硬、作风过硬的检察铁军。一是坚持政治立检,加强思想政治建设,树牢“四个意识”,把稳队伍建设的正确方向。二是坚持文化育检,大力弘扬社会主义核心价值观,坚定干事创业的理想信念；在全省检察机关组织开展“大学法”活动,树牢法治信仰。三是坚持人才强检,抓好引才、育才、聚才、用才工程,选优配强各级检察院领导班子,建设高素质专业化检察队伍,夯实干事创业的素能基础。四是坚持从严治检,狠抓管党治党主体责任落实,持之以恒正风肃纪,严格落实中央八项规定实施细则,持续推进规范司法行为专项整治,确保检察权依法正确行使。</p>"+
				"</div>";
		List<ImgInfo> sdf = getAllImgBase64("http://www.spp.gov.cn/spp/tt/201802/t20180225_367731.shtml", c);
		for (ImgInfo imgInfo : sdf) {
			System.out.println(imgInfo.getNewsThumbnail());
			System.out.println(imgInfo.getThumbnail());
		}
	}
}

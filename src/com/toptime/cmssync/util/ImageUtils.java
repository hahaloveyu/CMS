package com.toptime.cmssync.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.swing.ImageIcon;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import sun.misc.BASE64Encoder;

public class ImageUtils {

    // 图片 宽/高 比例阈值
    private static final double WIDTH_HEIGHT_RATIO = 3.3;
    // 生成新闻列表页缩略图的宽
    public static final int DEFAULT_NEWS_IMG_WIDTH = 121;
    // 生成新闻列表页缩略图的高
    public static final int DEFAULT_NEWS_IMG_HEIGHT = 75;
    // 生成大缩略图的宽
    public static final int DEFAULT_BIG_IMG_WIDTH = 180;
    // 生成大缩略图的高
    public static final int DEFAULT_BIG_IMG_HEIGHT = 112;
    //图片类型
    private static final List<String> pictype= Arrays.asList("image/jpeg","image/gif","image/png");

    /*private final static Map<String, String> pictype = new HashMap<String, String>() {{
        put("image/jpeg", ".jpg");
        put("image/gif", ".gif");
        put("image/png", ".png");
    }};*/

    // 生成正方形缩略图默认边长
    // private static final int DEFAULT_IMGLENGTH = 180;

    /**
     * 将图片流转为base64
     *
     * @param bi
     * @return
     */
    private static String getBase64(BufferedImage bi) {
        BASE64Encoder encoder = new BASE64Encoder();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos);
            byte[] bytes = baos.toByteArray();
            // 去除空格回车换行
            return encoder.encodeBuffer(bytes).trim().replaceAll("[\\t\\n\\r]", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获得图片的流
     *
     * @param imagePath
     * @return
     */
    public static BufferedImage BfImg(String imagePath, StringBuffer imageSize, StringBuffer imageLength, StringBuffer imageType)  {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new SSLTrustManager();
        trustAllCerts[0] = tm;
        SSLContext sc = null;
        
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier((HostnameVerifier) tm);
		} catch (NoSuchAlgorithmException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedImage bi = null;
        try {
            // 限定图片大小为3M，超过3M的图片不处理
            byte[] buf = new byte[1024 * 1024 * 3];
            Image image = null;
            HttpURLConnection conn = null;
            InputStream in = null;
            try {
                URL url = new URL(imagePath);
//                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36");
                conn.setConnectTimeout(30000);//连接超时30s
                conn.setReadTimeout(30000);
                conn.setRequestMethod("GET");
                conn.setUseCaches(false);//不使用缓存
                conn.setInstanceFollowRedirects(true);
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    //imageType=conn.getContentType();
                    imageType.append(conn.getContentType());
                    if (!pictype.contains(imageType.toString())) {//判断数据类型
                        System.out.println("This url data type is not a picture");
                        return null;
                    }
                    in = conn.getInputStream();
                    int readlen = 0;
                    int contentlen = 0;
                    do {
                        // 一次读取10KB，如果返回值小于0可以认为一定读取完毕
                        contentlen += readlen;
                        readlen = in.read(buf, contentlen, 1024 * 10);
                    } while (readlen > 0);
//                    MagicMatch
                    image = (new ImageIcon(buf)).getImage();// 利用ImageIcon获取Image对象
                    imageLength.append(contentlen);
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

            bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            imageSize.append(bi.getWidth() + "×" + bi.getHeight());
            Graphics g = bi.createGraphics();
            g.drawImage(image, 0, 0, Color.WHITE, null);
            g.dispose();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bi;
    }

    /**
     * 图片流处理，返回base64，指定宽和高
     *
     * @param bufferedImage
     * @param width
     * @param height
     * @return
     */
    public static String getThumbnail(BufferedImage bufferedImage, int width, int height) {
        if (bufferedImage != null) {
            try {
                int imageWidth = bufferedImage.getWidth();
                int imageHeight = bufferedImage.getHeight();
                // 原始图片的宽高比
                double imageRatio = (double) imageWidth / (double) imageHeight;
                // 生成缩略图的宽高比
                double newsThumbnailRatio = (double) width / (double) height;

                if (imageRatio > newsThumbnailRatio) {// 原始图片宽高比大于缩略图的宽高比，则原始图片高不变，宽度进行截取，取中

                    imageWidth = (int) (newsThumbnailRatio * imageHeight);
                    bufferedImage = Thumbnails.of(bufferedImage).sourceRegion(Positions.CENTER, imageWidth, imageHeight).size(width, height).asBufferedImage();

                } else if (imageRatio < newsThumbnailRatio) {// 原始图片宽高比小于缩略图的宽高比，则原始图片宽不变，高度进行截取

                    imageHeight = (int) (imageWidth / newsThumbnailRatio);
                    bufferedImage = Thumbnails.of(bufferedImage).sourceRegion(Positions.TOP_LEFT, imageWidth, imageHeight).size(width, height).asBufferedImage();

                } else {
                    bufferedImage = Thumbnails.of(bufferedImage).size(width, height).asBufferedImage();
                }

                return getBase64(bufferedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 图片流处理，返回base64，默认大小的新闻列表页缩略图121*75
     *
     * @param bufferedImage
     * @return
     */
    public static String getDefaultNewsThumbnail(BufferedImage bufferedImage) {

        return getThumbnail(bufferedImage, DEFAULT_NEWS_IMG_WIDTH, DEFAULT_NEWS_IMG_HEIGHT);
    }

    /**
     * 图片流处理，返回base64，默认大小的大缩略图180*112
     *
     * @param bufferedImage
     * @return
     */
    public static String getDefaultBigThumbnail(BufferedImage bufferedImage) {

        return getThumbnail(bufferedImage, DEFAULT_BIG_IMG_WIDTH, DEFAULT_BIG_IMG_HEIGHT);
    }

    /**
     * 判断图片是否符合要求，不符合要求的图片不进行后续处理
     *
     * @param bi
     * @param width
     * @param height
     * @return
     */
    public static boolean isQualified(BufferedImage bi, int width, int height) {
        boolean isQualified = true;

        int imageWidth = bi.getWidth();
        int imageHeight = bi.getHeight();

        if (imageWidth < width || imageHeight < height) {// 原始图片长或宽小于需要的图片大小,不符合要求
            isQualified = false;
        } else if ((double) imageWidth / (double) imageHeight > WIDTH_HEIGHT_RATIO) {// 宽高比超过阈值
            isQualified = false;
        }
        // TODO 其他判断条件

        return isQualified;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 图片流处理，返回base64，处理后图片为正方形
     *
     * @param bufferedImage
     * @param imgSize
     * @return
     */
    /*public static String getThumbnail(BufferedImage bufferedImage, int imgSize) {
        if (bufferedImage != null) {
			try {
				int imageWidth = bufferedImage.getWidth();
				int imageHeight = bufferedImage.getHeight();
				if (imageWidth != imageHeight) {
					int imgLength = imgSize;
					if (imageWidth > imageHeight) {
						imgLength = imageHeight;
						bufferedImage = Thumbnails.of(bufferedImage).sourceRegion(Positions.CENTER, imgLength, imgLength).size(imgSize, imgSize).asBufferedImage();
					} else {
						imgLength = imageWidth;
						bufferedImage = Thumbnails.of(bufferedImage).sourceRegion(Positions.CENTER, imgLength, imgLength).size(imgSize, imgSize).asBufferedImage();
					}
				} else {
					bufferedImage = Thumbnails.of(bufferedImage).size(imgSize, imgSize).asBufferedImage();
				}

				return getBase64(bufferedImage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}*/

    /**
     * 图片流处理，返回base64，处理后图片为正方形，180*180
     *
     * @param bufferedImage
     * @param imgSize
     * @return
     */
    /*public static String getThumbnail(BufferedImage bufferedImage) {
        if (bufferedImage != null) {
			try {
				int imageWidth = bufferedImage.getWidth();
				int imageHeight = bufferedImage.getHeight();
				if (imageWidth != imageHeight) {
					int imgLength = DEFAULT_IMGLENGTH;
					if (imageWidth > imageHeight) {
						imgLength = imageHeight;
						bufferedImage = Thumbnails.of(bufferedImage).sourceRegion(Positions.CENTER, imgLength, imgLength).size(DEFAULT_IMGLENGTH, DEFAULT_IMGLENGTH)
								.asBufferedImage();
					} else {
						imgLength = imageWidth;
						bufferedImage = Thumbnails.of(bufferedImage).sourceRegion(Positions.CENTER, imgLength, imgLength).size(DEFAULT_IMGLENGTH, DEFAULT_IMGLENGTH)
								.asBufferedImage();
					}
				} else {
					bufferedImage = Thumbnails.of(bufferedImage).size(DEFAULT_IMGLENGTH, DEFAULT_IMGLENGTH).asBufferedImage();
				}

				return getBase64(bufferedImage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}*/

}

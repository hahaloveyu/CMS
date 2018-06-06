package com.toptime.cmssync.util;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.top.time.http.MyHttpClient;
import com.top.time.http.WebHttpGet;
import com.top.time.http.WebResponse;

public class ThumbnailatorUtil {

	/**
	 * 
	 * @param imagePath  图片路径
	 * @return
	 */
	public static List<String> imgToThumbnail(List<String> imagePath,int imgSize){
		List<String> thumbList = new ArrayList<String>();
		for (int i = 0; i < imagePath.size(); i++) {
			thumbList.add(imgToThumbnail(imagePath.get(i),imgSize));
		}
		return thumbList;
	}
	
	/**
	 * 
	 * @param imagePath  图片路径
	 * @return
	 */
	public static String imgToThumbnail(String imagePath,int imgSize){
		try {
			BufferedImage image = ImageIO.read(new File(imagePath));  
	        int imageWidth = image.getWidth();  
	        int imageHeight = image.getHeight();  
	        if (imageWidth != imageHeight) {  
	        	int imgLength = imgSize;
		        if (imageWidth > imageHeight) {  
		        	imgLength = imageHeight;
		        	image = Thumbnails.of(image).sourceRegion(Positions.CENTER, imgLength, imgLength).size(imgSize, imgSize).asBufferedImage();
		        } else {  
		        	imgLength = imageWidth;
		        	image = Thumbnails.of(image).sourceRegion(Positions.TOP_LEFT, imgLength, imgLength).size(imgSize, imgSize).asBufferedImage();
		        }
	        } else {  
	        	image = Thumbnails.of(image).size(imgSize, imgSize).asBufferedImage();  
	        }  
	        
	       return getImageBinary(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 将图片转为  BASE 64
	 * @param bi
	 * @return
	 */
	public static String getImageBinary(BufferedImage bi){
		
		BASE64Encoder encoder = new BASE64Encoder();
		try {    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();    
            ImageIO.write(bi, "png", baos);    
            byte[] bytes = baos.toByteArray();    
            //去除空格回车换行
            return encoder.encodeBuffer(bytes).trim().replaceAll("[\\t\\n\\r]", "");    
        } catch (IOException e) {    
            e.printStackTrace();    
        }
		
        return ""; 
	}
	
	
	/**
	 * BASE 64 码转换为图片.
	 * @param base64String
	 * @param savePath 图片保存路径
	 */
	public static void base64StringToImage(String base64String, String savePath){  
		BASE64Decoder decoder = new BASE64Decoder();
        try {  
            byte[] bytes1 = decoder.decodeBuffer(base64String);                
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);  
            BufferedImage bi1 =ImageIO.read(bais);  
            File file = new File(savePath);//可以是jpg,png,gif格式  
            ImageIO.write(bi1, "jpg", file);//不管输出什么格式图片，此处不需改动  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 
	
	
	/**
	 * 
	 * @param destUrl  图片路径
	 * @return
	 */
	public static List<String> downImgToThumbnail(List<String> destUrl,int imgSize){
		List<String> thumbList = new ArrayList<String>();
		for (int i = 0; i < destUrl.size(); i++) {
			thumbList.add(downImgToThumbnail(destUrl.get(i),imgSize));
		}
		return thumbList;
	}
	
	
	
	/**
	 * 图片下载处理, 并返回 BASE64 码.
	 * @param destUrl   图片的绝对路径
	 * @param imgSize   ?
	 * @return  返回 BASE 64 码.
	 */
	public static String downImgToThumbnail(String destUrl,int imgSize) {
		
		 HttpURLConnection httpUrl = null;  
		 URL url = null; 
		 BufferedImage image = null;
		 try {  
			 url = new URL(destUrl);  
			 httpUrl = (HttpURLConnection) url.openConnection();  
			 httpUrl.connect();  
			 
			 image = ImageIO.read(httpUrl.getInputStream());  
		     
		 } catch (Exception e) {  
			 try {
				image = ImageIO.read(new ByteArrayInputStream(httpImage(destUrl)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			 //e.printStackTrace();
		 } finally {  
			 try {  
				  httpUrl.disconnect();  
			 } catch (Exception e) {  
				 e.printStackTrace();
			 }  
		 }  
		 
		 if (image == null) {
			return "";
		}
		try {
			 int imageWidth = image.getWidth(); //图片的宽度 
		     int imageHeight = image.getHeight();//图片的高度
		     //图片的宽度与高度不相等..............
		     if (imageWidth != imageHeight) {  
		        	int imgLength = imgSize;
			        if (imageWidth > imageHeight) {  
			        	imgLength = imageHeight;
			        	image = Thumbnails.of(image).sourceRegion(Positions.CENTER, imgLength, imgLength).size(imgSize, imgSize).asBufferedImage();
			        } else {  
			        	imgLength = imageWidth;
			        	image = Thumbnails.of(image).sourceRegion(Positions.TOP_LEFT, imgLength, imgLength).size(imgSize, imgSize).asBufferedImage();
			        }
		        } else {  
		        	image = Thumbnails.of(image).size(imgSize, imgSize).asBufferedImage();  
		        }  
		        
		        return getImageBinary(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 return "";
	}  
	
	//处理带中文图片
	private static byte[] httpImage(String imgUrl){
		
		MyHttpClient httpClient = null;
		byte[] content = null;
		try{
		httpClient = new MyHttpClient(true);
		WebHttpGet  webHttpClient = new WebHttpGet(httpClient.getHttpClient(),null);
		
		webHttpClient.setMimeType("image/jpeg",".jpg");
		webHttpClient.setMimeType("image/gif",".gif");
		webHttpClient.setMimeType("image/png",".png");
		 	
		WebResponse webResponse = webHttpClient.getWebResponse(imgUrl);
		content = webResponse.getContent();
		}finally{
			if(httpClient!=null){
				httpClient.getHttpClient().getConnectionManager().shutdown();
			}
			
		}
		return content;
	}

	
	public static void main(String[] args) {
		String str = "";
		/*List<String> imgPath = new ArrayList<String>();
		imgPath.add("C:\\Users\\toptime\\Desktop\\Thumbnailator\\11.jpg");
		str = imgToThumbnail(imgPath,70).get(0);
		System.out.println(str);
		base64StringToImage(str,"C:/Users/toptime/Desktop/Thumbnailator/test/x.jpg");*/
		//str = downImgToThumbnail("http://n.sinaimg.cn/news/transform/20151113/axeo-fxksqis4775583.jpg",70);
		//System.out.println(str);
		//base64StringToImage(str,"C:/Users/toptime/Desktop/Thumbnailator/test/x.jpg");
		str = downImgToThumbnail("http://www.gzii.gov.cn/gzgxw/uploads/resource/20071023100904984.gif",200);
		System.out.println(str);
	}
	
	
	
	
	

}

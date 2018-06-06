package com.toptime.cmssync.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
//		String value="穗府〔2016〕20号";
		String value="";
//		String value="穗府令第20号";
		Pattern regPat = Pattern.compile("(.*?)(〔.*?〕|第)(.*?)号");
		Matcher regMat = regPat.matcher(value.trim());
		
		if(regMat.find()) {
			
			for (int i = 0; i < 3; i++) {
				if(regMat.find(i)) {
					System.out.println(regMat.group(i+1));
				}
			}
		}
//		if(regMat.find()) {
//			System.out.println(regMat.group(1));
//			System.out.println(regMat.group(2));
//			System.out.println(regMat.group(3));
//			
//		}

	}

}

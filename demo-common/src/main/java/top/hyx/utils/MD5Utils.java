package top.hyx.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;

public class MD5Utils {

	/**
	 *
	 * @Title: MD5Utils.java
	 * @Description: 对字符串进行md5加密
	 */
	public static String getMD5Str(String strValue) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		String newstr = Base64.encodeBase64String(md5.digest(strValue.getBytes()));
		return newstr;
	}

	public static void main(String[] args) {
		try {
			String md5 = getMD5Str("4efe2524-ce83-448f-99be-ee65d30b5abb");
			System.out.println(md5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

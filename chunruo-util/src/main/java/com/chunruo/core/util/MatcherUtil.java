package com.chunruo.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtil {
	public static final String PATTERN_ACCOUNT = "^[a-zA-Z0-9_]{5,16}$";
	public static final String PATTERN_MOBILE = "^(1[3,5,8,7]{1}[\\d]{9})$";
	public static final String PATTERN_EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	
	/**
	 * 验证用户账号格式
	 * @param account
	 * @return true:正确 false:错误
	 */
	public static boolean matcherAccount(String account) {
		Pattern p = Pattern.compile(PATTERN_ACCOUNT);
		Matcher m = p.matcher(account);
		return m.matches();
	}
	
	public static boolean matcherMobile(String mobile) {
		Pattern p = Pattern.compile(PATTERN_MOBILE);
		Matcher m = p.matcher(mobile);
		return m.matches();
	}
	
	public static boolean matcherEmail(String email) {
		Pattern p = Pattern.compile(PATTERN_EMAIL);
		Matcher m = p.matcher(email);
		return m.matches();
	}
	

	public static void main(String[] args) {
		System.out.println("account="+matcherAccount("1test1"));
		System.out.println("mobile="+matcherMobile("133456789011"));
		System.out.println("email="+matcherEmail("1@css.com"));

	}

}

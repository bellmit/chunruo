package com.chunruo.portal.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginInterceptor {
	public static final String LOGIN = "login";
	public static final int CONT_HTML_TYPE = 0;	//html格式
	public static final int CONT_JOSN_TYPE = 1; //josn格式
	boolean loginAuth() default true;
	boolean deviceAuth() default true;
	String value() default "";
	int contType() default 0;
}

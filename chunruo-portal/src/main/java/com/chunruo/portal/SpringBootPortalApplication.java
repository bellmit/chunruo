package com.chunruo.portal;

import java.util.Collections;
import javax.servlet.MultipartConfigElement;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.chunruo.core.Constants;
import com.chunruo.core.util.Configuration;
import com.chunruo.portal.filter.PortalFilter;
import com.chunruo.portal.interceptor.LoginHandlerInterceptor;

@SpringBootApplication
@EnableAsync
@ImportResource({"classpath:velocity.xml"})
@ComponentScan(basePackages={ "com.chunruo.*" })
public class SpringBootPortalApplication extends SpringBootServletInitializer implements ApplicationContextAware{

	@Autowired
	private Environment env;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootPortalApplication.class); 
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootPortalApplication.class, args);
	}

	@Bean
	public HttpMessageConverters customConverters() {
		return new HttpMessageConverters(new TracksMediaTypeConverter());
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		Constants.ctx = ctx;
		Constants.conf = Configuration.getInstance(env);
	}

	@Bean
	public WebMvcConfigurerAdapter forwardToIndex() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**");
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
		        //第一个方法设置访问路径前缀，第二个方法设置资源路径
				registry.addResourceHandler("/upload/**").addResourceLocations("file:" + Constants.EXTERNAL_IMAGE_PATH + "/upload/");
				registry.addResourceHandler("/chunruo/**").addResourceLocations("file:" + Constants.EXTERNAL_IMAGE_PATH + "/chunruo/");
				registry.addResourceHandler("/depository/**").addResourceLocations("file:" + Constants.EXTERNAL_IMAGE_PATH + "/depository/");
				super.addResourceHandlers(registry);
			}
		};
	}
	
	@Bean   
	public MultipartConfigElement multipartConfigElement() {   
		MultipartConfigFactory factory = new MultipartConfigFactory();  
		//// 设置文件大小限制 ,超了，页面会抛出异常信息，这时候就需要进行异常信息的处理了;  
		factory.setMaxFileSize("50MB"); //KB,MB  
		/// 设置总上传数据总大小  
		factory.setMaxRequestSize("50MB");   
		return factory.createMultipartConfig();   
	} 

	@Bean  
	public FilterRegistrationBean corsFilter() {  
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();  
        CorsConfiguration config = new CorsConfiguration();  
        config.setAllowCredentials(true);  
        config.addAllowedOrigin(CorsConfiguration.ALL);  
        config.addAllowedHeader(CorsConfiguration.ALL);  
        config.addAllowedMethod(CorsConfiguration.ALL);  
        config.addExposedHeader(PortalConstants.X_TOKEN);
        config.addExposedHeader("Accept");
        config.addExposedHeader("Authorization");
        // 设置跨域缓存实践为30分钟
        config.setMaxAge(1800L);
        source.registerCorsConfiguration("/**", config);  

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));  
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);  
        return bean;
	} 

	@Bean
	public FilterRegistrationBean portalFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new PortalFilter());
		registration.addUrlPatterns("/clt/*");
		registration.setOrder(0);
		return registration;
	} 
}

class TracksMediaTypeConverter extends MappingJackson2HttpMessageConverter {
	public TracksMediaTypeConverter() {
		setSupportedMediaTypes(Collections.singletonList(MediaType.valueOf("application/json;charset=UTF-8")));
	}
}

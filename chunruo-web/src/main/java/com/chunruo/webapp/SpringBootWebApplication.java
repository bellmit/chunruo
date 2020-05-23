package com.chunruo.webapp;

import java.text.SimpleDateFormat;
import java.util.Collections;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.chunruo.core.Constants;
import com.chunruo.core.util.Configuration;
import com.chunruo.webapp.filter.HeaderMenuFilter;
import com.chunruo.webapp.filter.LogFilter;
import com.chunruo.webapp.interceptor.AuthorizeHandlerInterceptor;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { MultipartAutoConfiguration.class })
@ComponentScan(basePackages = { "com.chunruo.*" })
@PropertySources(value = { @PropertySource("classpath:messages_zh_CN.properties") })
@EnableTransactionManagement
@EnableAsync
public class SpringBootWebApplication extends SpringBootServletInitializer implements ApplicationContextAware {
	@Autowired
	private Environment env;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootWebApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebApplication.class, args);
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				container.setSessionTimeout(1800);// 单位为S
			}
		};
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
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		return filter;
	}

	@Bean(name = "multipartResolver")
	public MultipartResolver multipartResolver() {
		// 显示声明CommonsMultipartResolver为mutipartResolver
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("UTF-8");
		resolver.setResolveLazily(true);// resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
		resolver.setMaxInMemorySize(40960);
		resolver.setMaxUploadSize(50 * 1024 * 1024);// 上传文件大小 50M 50*1024*1024
		return resolver;
	}

	@Bean
	public WebMvcConfigurerAdapter forwardToIndex() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(new AuthorizeHandlerInterceptor()).addPathPatterns("/**");
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/upload/**").addResourceLocations("file:" + Constants.EXTERNAL_IMAGE_PATH + "/upload/");
				registry.addResourceHandler("/images/**").addResourceLocations("file:" + Constants.EXTERNAL_IMAGE_PATH + "/upload/images/");
				registry.addResourceHandler("/depository/**").addResourceLocations("file:" + Constants.EXTERNAL_IMAGE_PATH + "/depository/");
				super.addResourceHandlers(registry);
			}
		};
	}

	@Bean
	public FilterRegistrationBean gzipFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new LogFilter());
		registration.addUrlPatterns("/*");
		return registration;
	}

	@Bean
	public FilterRegistrationBean headerMenuFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new HeaderMenuFilter());
		registration.addUrlPatterns("/scripts/HeaderToolbar.js");
		return registration;
	}
}

class TracksMediaTypeConverter extends MappingJackson2HttpMessageConverter {
	public TracksMediaTypeConverter() {
		setSupportedMediaTypes(Collections.singletonList(
				MediaType.valueOf("application/json;charset=UTF-8")
				));

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		setObjectMapper(objectMapper);
	}
}

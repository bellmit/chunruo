package com.chunruo.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.dangdang.ddframe.rdb.sharding.api.MasterSlaveDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.strategy.slave.MasterSlaveLoadBalanceStrategyType;
import com.chunruo.core.base.DataSourceConfig;
import com.chunruo.core.base.GenericRepositoryFactoryBean;
import com.chunruo.core.util.StringUtil;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Druid 数据源配置
 * @author chunruo
 */
@Configuration
@EnableTransactionManagement
@ConfigurationProperties("spring")
@EnableJpaRepositories(
		entityManagerFactoryRef = "entityManagerFactory",
		transactionManagerRef = "transactionManager",
		basePackages = {"com.chunruo.core.*"},
		repositoryFactoryBeanClass=GenericRepositoryFactoryBean.class)
@EnableAspectJAutoProxy(proxyTargetClass=true, exposeProxy=true)
public class DruidDataSourceConfig{
	private Logger logger = LoggerFactory.getLogger(DruidDataSourceConfig.class);
    private Map<String, DataSourceConfig> databases = new HashMap<String, DataSourceConfig> ();
    private static final String KEY_MASTER = "master";
	@Resource
	private JpaProperties jpaProperties;

	/**
	 * 在同样的DataSource中，首先使用被标注的DataSource
	 * @return
	 */
	@Bean 
	@Primary 
	public DataSource dataSource() {
		try {
			 //设置从库数据源集合
	        Map<String, DataSource> slaveDataSourceMap = new HashMap<>();
	        for(Entry<String, DataSourceConfig> entry : databases.entrySet()) {
				String databaseName = entry.getKey();
				if (!StringUtil.compareObject(databaseName, KEY_MASTER)){
					slaveDataSourceMap.put(databaseName, this.createDataSource(entry.getValue()));
	            }
			}
	 
	        //获取数据源对象
	        DataSource masterDataSource = this.createDataSource(databases.get(KEY_MASTER));
	        if(slaveDataSourceMap != null && slaveDataSourceMap.size() > 0) {
	        	// 读写分离数据库分离器
		        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource("masterSlave", KEY_MASTER, 
		        		masterDataSource, slaveDataSourceMap, MasterSlaveLoadBalanceStrategyType.getDefaultStrategyType());
		        return dataSource;
	        }else {
	        	return masterDataSource;
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 创建数据库连接源
	 * @param config
	 * @return
	 */
	public DataSource createDataSource(DataSourceConfig config) {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(config.getUrl());
		dataSource.setUsername(config.getUsername());
		dataSource.setPassword(config.getPassword());
		dataSource.setDriverClassName(config.getDriverClassName());
		dataSource.setInitialSize(config.getInitialSize());
		dataSource.setMinIdle(config.getMinIdle());
		dataSource.setMaxActive(config.getMaxActive());
		dataSource.setMaxWait(config.getMaxWait());
		dataSource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
		dataSource.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
		dataSource.setValidationQuery(config.getValidationQuery());
		dataSource.setTestWhileIdle(config.isTestWhileIdle());
		dataSource.setTestOnBorrow(config.isTestOnBorrow());
		dataSource.setTestOnReturn(config.isTestOnReturn());
		dataSource.setPoolPreparedStatements(config.isPoolPreparedStatements());
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(config.getMaxPoolPreparedStatementPerConnectionSize());
		
		try { 
			dataSource.setFilters(config.getFilters()); 
		} catch (SQLException e) { 
			System.err.println("druid configuration initialization filter: " + e); 
		} 
		dataSource.setConnectProperties(config.getConnectionProperties()); 
		return dataSource;
    }

	/**
	 * 设置实体类所在位置
	 */
	@Primary
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
		return builder
				.dataSource(this.dataSource())
				.packages("com.chunruo.core.*")
				.persistenceUnit("persistenceUnit")
				.properties(jpaProperties.getHibernateProperties(this.dataSource()))
				.build();
	}

	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
		return new JpaTransactionManager(entityManagerFactory(builder).getObject());
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		logger.info("init Druid Monitor Servlet ...");
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
		// 控制台管理用户
		servletRegistrationBean.addInitParameter("loginUsername", "chunruo");
		servletRegistrationBean.addInitParameter("loginPassword", "chunruo@2343df");
		// 是否能够重置数据 禁用HTML页面上的“Reset All”功能
		servletRegistrationBean.addInitParameter("resetEnable", "false");
		return servletRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		return filterRegistrationBean;
	}

	public Map<String, DataSourceConfig> getDatabases() {
		return databases;
	}

	public void setDatabases(Map<String, DataSourceConfig> databases) {
		this.databases = databases;
	}
}
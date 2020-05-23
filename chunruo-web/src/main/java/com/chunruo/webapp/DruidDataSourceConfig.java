package com.chunruo.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.druid.pool.DruidDataSource;
import com.chunruo.core.base.DataSourceConfig;
import com.chunruo.core.base.GenericRepositoryFactoryBean;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
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
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Druid 数据源配置
 * @author chunruo
 */
@Configuration
@EnableTransactionManagement
@ConfigurationProperties("spring.datasource")
@EnableJpaRepositories(
		entityManagerFactoryRef = "entityManagerFactory",
		transactionManagerRef = "transactionManager",
		basePackages = {"com.chunruo.core.*", "com.chunruo.security.*"},
		repositoryFactoryBeanClass=GenericRepositoryFactoryBean.class)
@EnableAspectJAutoProxy(proxyTargetClass=true, exposeProxy=true)
public class DruidDataSourceConfig extends DataSourceConfig{
	private Logger logger = LoggerFactory.getLogger(DruidDataSourceConfig.class);
	@Resource
	private JpaProperties jpaProperties;
	
	/**
	 * 在同样的DataSource中，首先使用被标注的DataSource
	 * @return
	 */
	@Bean 
	@Primary 
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(this.getUrl());
		dataSource.setUsername(this.getUsername());
		dataSource.setPassword(this.getPassword());
		dataSource.setDriverClassName(this.getDriverClassName());
		dataSource.setInitialSize(this.getInitialSize());
		dataSource.setMinIdle(this.getMinIdle());
		dataSource.setMaxActive(this.getMaxActive());
		dataSource.setMaxWait(this.getMaxWait());
		dataSource.setTimeBetweenEvictionRunsMillis(this.getTimeBetweenEvictionRunsMillis());
		dataSource.setMinEvictableIdleTimeMillis(this.getMinEvictableIdleTimeMillis());
		dataSource.setValidationQuery(this.getValidationQuery());
		dataSource.setTestWhileIdle(this.isTestWhileIdle());
		dataSource.setTestOnBorrow(this.isTestOnBorrow());
		dataSource.setTestOnReturn(this.isTestOnReturn());
		dataSource.setPoolPreparedStatements(this.isPoolPreparedStatements());
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(this.getMaxPoolPreparedStatementPerConnectionSize());
		
		try { 
			dataSource.setFilters(this.getFilters()); 
		} catch (SQLException e) { 
			System.err.println("druid configuration initialization filter: " + e); 
		} 
		dataSource.setConnectProperties(this.getConnectionProperties()); 
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
				.packages("com.chunruo.core.*", "com.chunruo.security.*")
				.persistenceUnit("persistenceUnit")
				.properties(jpaProperties.getHibernateProperties(this.dataSource()))
				.build();
	}

	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
		return new JpaTransactionManager(entityManagerFactory(builder).getObject());
	}
}
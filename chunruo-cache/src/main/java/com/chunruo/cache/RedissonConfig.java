package com.chunruo.cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedissonConfig {

	@Value("${redisson.url}")
	private String url;

	@Value("${redisson.database}")
	private Integer database;
	
	@Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

	@Bean(destroyMethod = "shutdown")
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer().setAddress(url);
		config.useSingleServer().setDatabase(database);
		return Redisson.create(config);
	}
	
	
}

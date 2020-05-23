package com.chunruo.cache;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching
@Primary
public class RedisConfig {
	Logger log = LoggerFactory.getLogger(RedisConfig.class);

	@Bean(name = "jedisPoolConfig")
	@Primary
	public JedisPoolConfig poolCofig(@Value(value = "${portalurl.session.redis.pool.max-idle}") int maxIdle,
			@Value(value = "${portalurl.session.redis.pool.max-total}") int maxTotal,
			@Value(value = "${portalurl.session.redis.pool.max-waitMillis}") long maxWaitMillis,
			@Value(value = "${portalurl.session.redis.pool.testOnBorrow}") boolean testOnBorrow) {

		JedisPoolConfig poolCofig = new JedisPoolConfig();
		poolCofig.setMaxIdle(maxIdle);
		poolCofig.setMaxTotal(maxTotal);
		poolCofig.setMaxWaitMillis(maxWaitMillis);
		poolCofig.setTestOnBorrow(testOnBorrow);

		log.info("create JedisPoolConfig [maxIdle:" + maxIdle + ",maxTotal:" + maxTotal + ",maxWaitMillis:" + maxWaitMillis + ",testOnBorrow:"
				+ testOnBorrow + "]");
		return poolCofig;
	}

	@Bean(name = "jedisConnectionFactory")
	@Primary
	public JedisConnectionFactory jedisConnectionFactory(@Value(value = "${portalurl.session.redis.hostName}") String hostName,
			@Value(value = "${portalurl.session.redis.port}") int port, @Value(value = "${portalurl.session.redis.timeout}") int timeout,
			@Value(value = "${portalurl.session.redis.password}") String password,
			@Qualifier("jedisPoolConfig") JedisPoolConfig poolConfig) {

		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(hostName);
		jedisConnectionFactory.setPort(port);
		jedisConnectionFactory.setTimeout(timeout);
		jedisConnectionFactory.setPassword(password);
		jedisConnectionFactory.setPoolConfig(poolConfig);

		log.info("hostName:" + hostName + ",port:" + port + ",timeout:" + timeout + ",password:" + password
				+ ",poolConfig:" + poolConfig + ",jedisConnectionFactory:" + jedisConnectionFactory);
		return jedisConnectionFactory;
	}

	@Bean(name = "redisTemplate")
	@Primary
	public RedisTemplate<String, String> redisTemplate(
			@Qualifier("jedisConnectionFactory") RedisConnectionFactory factory) {
		StringRedisTemplate template = new StringRedisTemplate(factory);
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setKeySerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		
		return template;
	}

	@Bean(name = "redisCacheManager")
	@Primary
	public CacheManager cacheManager(@Qualifier("redisTemplate") RedisTemplate redisTemplate) {
		RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
		// 设置缓存过期时间
		// rcm.setDefaultExpiration(60);//秒
		return rcm;
	}

	@Bean
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getName());
				sb.append(method.getName());
				for (Object obj : params) {
					sb.append(obj.toString());
				}
				log.info("key:" + sb.toString());
				return sb.toString();
			}
		};
	}

}

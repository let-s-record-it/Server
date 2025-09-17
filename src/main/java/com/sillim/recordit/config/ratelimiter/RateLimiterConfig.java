package com.sillim.recordit.config.ratelimiter;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import java.time.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RateLimiterConfig {

	private final RedisClient redisClient;

	@Value("${rate-limiter.capacity:5}")
	private int capacity;

	@Value("${rate-limiter.refill-token-amount:5}")
	private int refillTokenAmount;

	@Value("${rate-limiter.refill-duration-seconds:10}")
	private long refillDurationSeconds;

	@Value("${rate-limiter.bucket-ttl-seconds:600}")
	private long bucketTTLSeconds;

	@Bean
	public LettuceBasedProxyManager<String> proxyManager() {
		StatefulRedisConnection<String, byte[]> connect =
				redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
		return Bucket4jLettuce.casBasedBuilder(connect)
				.expirationAfterWrite(
						ExpirationAfterWriteStrategy.fixedTimeToLive(
								Duration.ofSeconds(bucketTTLSeconds)))
				.build();
	}

	@Bean
	public BucketConfiguration bucketConfiguration() {
		return BucketConfiguration.builder()
				.addLimit(
						limit ->
								limit.capacity(capacity)
										.refillIntervally(
												refillTokenAmount,
												Duration.ofSeconds(refillDurationSeconds)))
				.build();
	}
}

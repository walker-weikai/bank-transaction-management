package com.hometask.wk.management.config;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: weikai
 */
@Configuration
public class CommonConfig {
    @Bean
    public BloomFilter<Long> bloomFilterTransactionId() {
        //预期数据量 100000 误差 0.01
        return BloomFilter.create(Funnels.longFunnel(), 100000, 0.01);
    }
}

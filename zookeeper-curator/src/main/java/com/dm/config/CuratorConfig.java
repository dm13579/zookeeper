package com.dm.config;

import com.dm.constants.ZookeeperConstants;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @className CuratorConfig
 * @cescription Curator 配置类
 * @Author dm
 * @date 2020/12/8 15:37
 * @slogan: 我自横刀向天笑，笑完我就去睡觉
 * @Version 1.0
 **/
@Configuration
public class CuratorConfig {
    @Bean(initMethod = "start")
    public CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(ZookeeperConstants.BASE_SLEEP_TIME_MS, ZookeeperConstants.MAX_RETRIES);
        return CuratorFrameworkFactory.newClient(ZookeeperConstants.SERVER, ZookeeperConstants.SESSION_TIME_OUT, ZookeeperConstants.CONNECTION_TIME_OUT, retryPolicy);
    }
}

package com.dm.base;


import com.dm.constants.ZookeeperConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;

@Slf4j
public class CuratorClusterBase {

    private static CuratorFramework curatorFramework;

    @Before
    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5000, 30);
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZookeeperConstants.CLUSTER_SERVER)
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(ZookeeperConstants.SESSION_TIME_OUT)
                .connectionTimeoutMs(ZookeeperConstants.CONNECTION_TIME_OUT)
                .canBeReadOnly(true)
                .build();
        curatorFramework.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.CONNECTED) {
                log.info("连接成功！");
            }

        });
        log.info("连接中......");
        curatorFramework.start();
    }

    public void createIfNeed(String path) throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(path);
        if (stat == null) {
            String s = curatorFramework.create().forPath(path);
            log.info("path {} created! ", s);
        }
    }

    public static CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }
}

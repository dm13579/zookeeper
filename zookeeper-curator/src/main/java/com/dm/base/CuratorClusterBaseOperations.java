package com.dm.base;

import com.dm.base.CuratorClusterBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorClusterBaseOperations extends CuratorClusterBase {

    @Test
    public void testCluster() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        String pathWithParent = "/test";
        byte[] bytes = curatorFramework
                .getData()
                .forPath(pathWithParent);
        log.info(new String(bytes));
        while (true) {
            try {
                byte[] bytes2 = curatorFramework
                        .getData()
                        .forPath(pathWithParent);
                log.info(new String(bytes2));
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
                testCluster();
            }
        }
    }
}

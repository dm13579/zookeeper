package com.dm.base;

import com.dm.constants.ZookeeperConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Slf4j
public abstract class StandaloneBase {

    private static ZooKeeper zooKeeper = null;

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if (event.getState() == Event.KeeperState.SyncConnected
                    && event.getType() == Event.EventType.None) {
                countDownLatch.countDown();
                log.info("连接建立");
            }
        }
    };

    public static ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    @Before
    public void init() {
        try {
            log.info(" start to connect to zookeeper server: {}", ZookeeperConstants.SERVER);
            zooKeeper = new ZooKeeper(ZookeeperConstants.SERVER, ZookeeperConstants.SESSION_TIME_OUT, watcher);
            log.info(" 连接中...");
            countDownLatch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

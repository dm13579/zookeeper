package com.dm;

import com.dm.constants.ZookeeperConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.server.util.ConfigUtils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 集群节点变更 自动重新配置
 *
 * @author dm
 * @date 2023/12/08
 */
@Slf4j
public class ReconfigApp{

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ZooKeeper zookeeper = null;

    private static final Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.None
                    && event.getState() == Event.KeeperState.SyncConnected) {
                // 链接建立执行逻辑
                countDownLatch.countDown();
                log.info(" 连接建立");
                // start to watch config
                try {
                    // 链接建立对config节点数据进行监听
                    log.info(" 开始监听：{}", ZooDefs.CONFIG_NODE);
                    zookeeper.getConfig(true, null);
                } catch (KeeperException | InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            } else if (event.getPath() != null && event.getPath().equals(ZooDefs.CONFIG_NODE)) {
                // /zookeeper/config 数据发生变更执行逻辑
                try {
                    // 获取数据时再次监听
                    byte[] config = zookeeper.getConfig(this, null);
                    String clientConfigStr = ConfigUtils.getClientConfigStr(new String(config));
                    log.info(" 配置发生变更: {}", clientConfigStr);
                    // 更新集群server List
                    zookeeper.updateServerList(clientConfigStr.split(" ")[1]);
                } catch (KeeperException | InterruptedException | IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    };


    public static void main(String[] args) throws IOException, InterruptedException {
        zookeeper = new ZooKeeper(ZookeeperConstants.CLUSTER_SERVER, ZookeeperConstants.SESSION_TIME_OUT, watcher);
        countDownLatch.await();
    }
}

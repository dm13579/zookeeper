package com.dm.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CuratorBaseOperations extends CuratorStandaloneBase {

    /**
     * 递归创建子节点
     */
    @Test
    public void testCreateWithParent() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        String path = curatorFramework
                .create()
                .creatingParentsIfNeeded()
                .forPath("/node-parent/sub-node-1");
        log.info("curator create node :{}  successfully.", path);
    }

    /**
     * protection 模式，防止由于异常原因，导致僵尸节点
     */
    @Test
    public void testCreate() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        String forPath = curatorFramework
                .create()
                .withProtection()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/curator-node", "some-data".getBytes());
        log.info("curator create node :{}  successfully.", forPath);
    }

    /**
     * 获取数据
     */
    @Test
    public void testGetData() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        byte[] bytes = curatorFramework
                .getData()
                .forPath("/curator-node");
        log.info("get data from  node :{}  successfully.", new String(bytes));
    }


    /**
     * 设置属性值
     */
    @Test
    public void testSetData() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        curatorFramework
                .setData()
                .forPath("/curator-node", "changed!".getBytes());
        byte[] bytes = curatorFramework
                .getData()
                .forPath("/curator-node");
        log.info("get data from  node /curator-node :{}  successfully.", new String(bytes));
    }

    /**
     * 删除节点数据
     */
    @Test
    public void testDelete() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        curatorFramework
                .delete()
                .guaranteed()
                .deletingChildrenIfNeeded()
                .forPath("/node-parent");
    }

    /**
     * 监听子节点
     */
    @Test
    public void testListChildren() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        List<String> strings = curatorFramework
                .getChildren()
                .forPath("/discovery/example");
        strings.forEach(System.out::println);
    }

    @Test
    public void testThreadPool() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        // 手动定义线程池 不用内置线程池
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        curatorFramework
                .getData()
                .inBackground((client, event) -> {
                    log.info(" background: {}", event);
                }, executorService)
                .forPath("/zk-node");

    }
}

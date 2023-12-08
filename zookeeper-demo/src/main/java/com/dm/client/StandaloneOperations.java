package com.dm.client;

import com.dm.base.StandaloneBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StandaloneOperations extends StandaloneBase {

    @Test
    public void createData() throws KeeperException, InterruptedException {
        List<ACL> list = new ArrayList<>();
        // ZooDefs.Perms
        int perm = ZooDefs.Perms.ADMIN | ZooDefs.Perms.READ;
        ACL acl = new ACL(perm, new Id("world", "anyone"));
        list.add(acl);
        getZooKeeper().create("/temp", "hello".getBytes(), list, CreateMode.PERSISTENT);
    }

    /**
     * zk 获取数据
     */
    @Test
    public void getData() throws KeeperException, InterruptedException {
        byte[] data = getZooKeeper().getData("/temp", false, null);
        log.info(new String(data));
    }

    /**
     * 设置监听监听只能有效一次
     */
    @Test
    public void getDataByWatch() throws KeeperException, InterruptedException {
        byte[] data = getZooKeeper().getData("/temp", true, null);
        log.info(new String(data));
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 设置自定义监听,持久监听
     */
    @Test
    public void getDataByCustomerWatch() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        getZooKeeper().getData("/temp", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    getZooKeeper().getData(event.getPath(), this, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log.info(event.getPath());
                log.info(String.valueOf(stat));
            }
        }, stat);
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 设置
     */
    @Test
    public void getDataByDataCallback() throws InterruptedException {
        getZooKeeper().getData("/temp", false, (rc, path, ctx, data, stat) -> System.out.println(stat), "");
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 获取子节点
     */
    @Test
    public void getChild() throws KeeperException, InterruptedException {
        List<String> children = getZooKeeper().getChildren("/temp", false);
        children.forEach(System.out::println);
    }

    /**
     * 获取子节点，伴随着监听，只有增删子节点时才会触发监听，子节点值修改不触发监听
     */
    @Test
    public void getChildByWatch() throws KeeperException, InterruptedException {
        List<String> children = getZooKeeper().getChildren("/temp", event -> {
            // 此时获取的path是父节点的path,不是子节点的path
            System.out.println(event.getPath());
            // 获取子节点
            try {
                List<String> children1 = getZooKeeper().getChildren(event.getPath(), false);
                children1.forEach(System.out::println);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        children.forEach(System.out::println);
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void testDelete() throws KeeperException, InterruptedException {
        // -1 代表匹配所有版本，直接删除
        // 任意大于 -1 的代表可以指定数据版本删除
        getZooKeeper().delete("/config",-1);

    }

    @Test
    public void  asyncTest(){
        String userId="xxx";
        getZooKeeper().getData("/test", false, (rc, path, ctx, data, stat) -> {
            Thread thread = Thread.currentThread();

            log.info(" Thread Name: {},   rc:{}, path:{}, ctx:{}, data:{}, stat:{}",thread.getName(),rc, path, ctx, data, stat);
        },"test");
        log.info(" over .");

    }
}

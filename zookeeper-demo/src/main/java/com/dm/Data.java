package com.dm;

import com.dm.constants.ZookeeperConstants;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Data {

    ZooKeeper zooKeeper;

    @Before
    public void init() throws IOException {
        zooKeeper = new ZooKeeper(ZookeeperConstants.SERVER, ZookeeperConstants.SESSION_TIME_OUT, event -> {
            System.out.println(event.getPath());
            System.out.println(event);
        });
    }

    /**
     * zk 获取数据
     */
    @Test
    public void getData() throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData("/temp", false, null);
        System.out.println(new String(data));
    }

    /**
     * 设置监听监听只能有效一次
     */
    @Test
    public void getDataByWatch() throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData("/temp", true, null);
        System.out.println(new String(data));
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 设置自定义监听,持久监听
     */
    @Test
    public void getDataByCustomerWatch() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        zooKeeper.getData("/temp", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    zooKeeper.getData(event.getPath(), this, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(event.getPath());
                System.out.println(stat);
            }
        }, stat);
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 设置
     */
    @Test
    public void getDataByDataCallback() throws InterruptedException {
        zooKeeper.getData("/temp", false, (rc, path, ctx, data, stat) -> System.out.println(stat), "");
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 获取子节点
     */
    @Test
    public void getChild() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/temp", false);
        children.forEach(System.out::println);
    }

    /**
     * 获取子节点，伴随着监听，只有增删子节点时才会触发监听，子节点值修改不触发监听
     */
    @Test
    public void getChildByWatch() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/temp", event -> {
            // 此时获取的path是父节点的path,不是子节点的path
            System.out.println(event.getPath());
            // 获取子节点
            try {
                List<String> children1 = zooKeeper.getChildren(event.getPath(), false);
                children1.forEach(System.out::println);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        children.forEach(System.out::println);
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void createData() throws KeeperException, InterruptedException {
        List<ACL> list = new ArrayList<>();
        // ZooDefs.Perms
        int perm = ZooDefs.Perms.ADMIN | ZooDefs.Perms.READ;
        ACL acl = new ACL(perm, new Id("world", "anyone"));
        list.add(acl);
        zooKeeper.create("/temp", "hello".getBytes(), list, CreateMode.PERSISTENT);
    }

}

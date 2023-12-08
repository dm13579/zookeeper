package com.dm.client;

import com.dm.base.StandaloneBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;

/**
 * acl 权限操作
 *
 * @author dm
 * @date 2023/12/08
 */
@Slf4j
public class AclOperations extends StandaloneBase {

    /**
     * 用 world 模式创建节点
     */
    @Test
    public void createWithAclTest1() throws KeeperException, InterruptedException {

        int perms = ZooDefs.Perms.ADMIN | ZooDefs.Perms.READ;

        Id id = new Id();
        id.setId("anyone");
        id.setScheme("world");

        String s = getZooKeeper().create("/zk-node-1", "dm".getBytes(), Collections.singletonList(new ACL(perms, id)), CreateMode.PERSISTENT);
        log.info("create path: {}", s);
    }

    /**
     * 用授权模式创建节点
     */
    @Test
    public void createWithAclTest2() throws KeeperException, InterruptedException {

        // 对连接添加授权信息
        getZooKeeper().addAuthInfo("digest", "u400:p400".getBytes());

        int perms = ZooDefs.Perms.ADMIN | ZooDefs.Perms.READ;

        Id id = new Id();
        id.setId("u400:p400");
        id.setScheme("auth");

        String s = getZooKeeper().create("/zk-node-2", "dm".getBytes(), Collections.singletonList(new ACL(perms, id)), CreateMode.PERSISTENT);
        log.info("create path: {}", s);
    }

    /**
     * 用授权模式读取节点数据
     */
    @Test
    public void createWithAclTest3() throws KeeperException, InterruptedException {

        // 对连接添加授权信息
        getZooKeeper().addAuthInfo("digest", "u400:p400".getBytes());

        byte[] data = getZooKeeper().getData("/test", false, null);
        log.info("GET_DATA : {}", new String(data));
    }


    public static void main(String[] args) throws NoSuchAlgorithmException {
        String sId = DigestAuthenticationProvider.generateDigest("gj:123");
        System.out.println(sId);
        //  -Dzookeeper.DigestAuthenticationProvider.superDigest=gj:X/NSthOB0fD/OT6iilJ55WJVado=
    }
}

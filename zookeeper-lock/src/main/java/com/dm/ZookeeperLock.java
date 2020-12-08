package com.dm;

import com.dm.constants.ZookeeperConstants;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * zk分布式锁
 * @author dm
 */
public class ZookeeperLock {

    private ZkClient zkClient;
    private static final String ROOT_PATH = "/dm-lock";

    public ZookeeperLock() {
        zkClient = new ZkClient(ZookeeperConstants.SERVER, ZookeeperConstants.SESSION_TIME_OUT, ZookeeperConstants.CONNECTION_TIME_OUT);
        if (!zkClient.exists(ROOT_PATH)) {
            zkClient.createPersistent(ROOT_PATH);
        }
    }

    /**
     * 加锁
     */
    public Lock lock(String lockId, long timeout) {
        // 创建临时序号节点
        Lock lockNode = createLockNode(lockId);
        // 尝试拿锁
        lockNode = tryActiveLock(lockNode);
        // 没有拿到锁
        if (!lockNode.isActive()) {
            try {
                synchronized (lockNode) {
                    // 把线程hang在这里然后等待监听事件触发，拿到锁，释放线程
                    // 在tryActiveLockelse逻辑里面
                    lockNode.wait(timeout);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // 线程hang住的时间里面还没拿到锁就抛异常
        if (!lockNode.isActive()) {
            throw new RuntimeException("lock timeout");
        }
        return lockNode;
    }

    /**
     * s=释放锁
     */
    public void unlock(Lock lock) {
        if (lock.isActive()) {
            zkClient.delete(lock.getPath());
        }
    }

    /**
     * 尝试激活锁
     */
    private Lock tryActiveLock(Lock lockNode) {

        //  获取根节点下面所有的排好序的子节点
        List<String> list = zkClient.getChildren(ROOT_PATH)
                .stream()
                .sorted()
                .map(p -> ROOT_PATH + "/" + p)
                .collect(Collectors.toList());

        // 取最小的节点，这个节点就应该是应该加锁的节点
        String firstNodePath = list.get(0);
        // 最小节点是不是当前节点,是就直接加锁
        if (firstNodePath.equals(lockNode.getPath())) {
            lockNode.setActive(true);
        } else {
            // 取得当前节点减一，要监听这个节点
            String upNodePath = list.get(list.indexOf(lockNode.getPath()) - 1);
            zkClient.subscribeDataChanges(upNodePath, new IZkDataListener() {
                @Override
                public void handleDataChange(String dataPath, Object data) {

                }

                // 前一个节点被释放回调
                @Override
                public void handleDataDeleted(String dataPath) {
                    // 重新尝试拿锁
                    Lock lock = tryActiveLock(lockNode);
                    synchronized (lockNode) {
                        // 之前同步块的线程释放
                        if (lock.isActive()) {
                            lockNode.notify();
                        }
                    }
                    zkClient.unsubscribeDataChanges(upNodePath, this);
                }
            });
        }
        return lockNode;
    }

    /**
     * 创建临时序号节点
     */
    public Lock createLockNode(String lockId) {
        String nodePath = zkClient.createEphemeralSequential(ROOT_PATH + "/" + lockId, "w");
        return new Lock(lockId, nodePath);
    }
}


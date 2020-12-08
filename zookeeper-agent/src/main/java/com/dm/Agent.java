package com.dm;

import com.dm.constants.ZookeeperConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.I0Itec.zkclient.ZkClient;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * java agent 监控程序
 */
public class Agent {

    private static ZkClient zkClient;
    private static String rootPath = "/dm-master";
    private static String servicePath = rootPath + "/service";
    private static String nodePath;

    private static Agent curInstance = new Agent();

    public static Agent getInstance() {
        return curInstance;
    }

    /**
     * java agent 数据监控
     */
    public static void premain(String args, Instrumentation instrumentation) {
        Agent.getInstance().init();
    }

    public void init() {
        zkClient = new ZkClient(ZookeeperConstants.SERVER, ZookeeperConstants.SESSION_TIME_OUT, ZookeeperConstants.CONNECTION_TIME_OUT);
        System.out.println("zk连接成功" + ZookeeperConstants.SERVER);

        buildRoot();
        createServerNode();

        // 刷新节点数据
        Thread startThread = new Thread(() -> {
            while (true) {
                updateServerNode();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "zk_startThread");

        startThread.setDaemon(true);
        startThread.start();
    }

    private void updateServerNode() {
        zkClient.writeData(nodePath, getOsInfo());
    }

    private void buildRoot() {
        if (!zkClient.exists(rootPath)) {
            zkClient.createPersistent(rootPath);
        }
    }

    private void createServerNode() {
        nodePath = zkClient.createEphemeralSequential(servicePath, getOsInfo());
        System.out.println("创建临时序号节点" + nodePath);
    }

    private String getOsInfo() {
        OsBean bean = new OsBean();
        bean.lastUpdateTime = System.currentTimeMillis();
        bean.ip = getLocalIp();
        bean.cpu = CPUMonitorCalc.getInstance().getProcessCpu();
        MemoryUsage memoryUsag = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        bean.usedMemorySize = memoryUsag.getUsed() / 1024 / 1024;
        bean.usableMemorySize = memoryUsag.getMax() / 1024 / 1024;
        bean.pid = ManagementFactory.getRuntimeMXBean().getName();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getLocalIp() {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return addr.getHostAddress();
    }
}

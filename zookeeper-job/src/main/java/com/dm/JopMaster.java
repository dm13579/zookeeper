package com.dm;

import com.dm.constants.ZookeeperConstants;
import org.I0Itec.zkclient.ZkClient;

import java.util.Map;
import java.util.stream.Collectors;

/**
  *                  ,;,,;
  *                ,;;'(    
  *      __      ,;;' ' \   
  *   /'  '\'~~'~' \ /'\.)  
  * ,;(      )    /  |.     
  *,;' \    /-.,,(   ) \    
  *     ) /       ) / )|    
  *     ||        ||  \)     
  *    (_\       (_\
  *@ClassName JopMaster
  *@Description 多节点，Master选举，有节点宕机进行master选举  选举，最小节点
  *@Author dm
  *@Date 2020/6/26 13:41
  *@slogan: 我自横刀向天笑，笑完我就去睡觉
  *@Version 1.0
  **/
public class JopMaster {

    private ZkClient zkClient;
    private static final String ROOT_PATH = "/jop-master";
    private static final String SERVICE_PATH = ROOT_PATH + "/service";
    private String nodePath;
    private volatile boolean master = false;
    private static JopMaster jopMaster;

    public static JopMaster getInstance() {
        if (jopMaster == null) {
            jopMaster= new JopMaster();
        }
        return jopMaster;
    }

    private JopMaster() {
        zkClient = new ZkClient(ZookeeperConstants.SERVER, ZookeeperConstants.SESSION_TIME_OUT, ZookeeperConstants.CONNECTION_TIME_OUT);
        buildRoot();
        createServerNode();
    }

    private void buildRoot() {
        if (!zkClient.exists(ROOT_PATH)) {
            zkClient.createPersistent(ROOT_PATH);
        }
    }

    private void createServerNode() {
        nodePath = zkClient.createEphemeralSequential(SERVICE_PATH, "slave");
        System.out.println("创建service节点:" + nodePath);
        initMaster();
        initListener();
    }

    private void initMaster() {
        boolean existMaster = zkClient.getChildren(ROOT_PATH)
                .stream()
                .map(p -> ROOT_PATH + "/" + p)
                .map(p -> zkClient.readData(p))
                .anyMatch("master"::equals);
        if (!existMaster) {
            doElection();
            System.out.println("当前当选master");
        }
    }

    private void initListener() {
        zkClient.subscribeChildChanges(ROOT_PATH, (parentPath, currentChilds) -> doElection());
    }

    /**
     * 执行选举
     */
    public void doElection() {
        Map<String, Object> childData = zkClient.getChildren(ROOT_PATH)
                .stream()
                .map(p -> ROOT_PATH + "/" + p)
                .collect(Collectors.toMap(p -> p, p -> zkClient.readData(p)));
        if (childData.containsValue("master")) {
            return;
        }

        childData.keySet().stream().sorted().findFirst().ifPresent(p -> {
            if (p.equals(nodePath)) { // 设置最小值序号为master 节点
                zkClient.writeData(nodePath, "master");
                master = true;
                System.out.println("当前当选master" + nodePath);
            }
        });

    }

    public static boolean isMaster() {
        return getInstance().master;
    }

}

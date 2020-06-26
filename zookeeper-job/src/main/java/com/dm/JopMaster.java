package com.dm;

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
  *@Description TODO
  *@Author dm
  *@Date 2020/6/26 13:41
  *@slogan: 我自横刀向天笑，笑完我就去睡觉
  *@Version 1.0
  **/
public class JopMaster {

    private String server = "122.51.157.42:2181";
    private ZkClient zkClient;
    private static final String rootPath = "/jop-master";
    private static final String servicePath = rootPath + "/service";
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
        zkClient = new ZkClient(server, 2000, 10000);
        buildRoot();
        createServerNode();
    }

    private void buildRoot() {
        if (!zkClient.exists(rootPath)) {
            zkClient.createPersistent(rootPath);
        }
    }

    private void createServerNode() {
        nodePath = zkClient.createEphemeralSequential(servicePath, "slave");
        System.out.println("创建service节点:" + nodePath);
        initMaster();
        initListener();
    }

    private void initMaster() {
        boolean existMaster = zkClient.getChildren(rootPath)
                .stream()
                .map(p -> rootPath + "/" + p)
                .map(p -> zkClient.readData(p))
                .anyMatch(d -> "master".equals(d));
        if (!existMaster) {
            doElection();
            System.out.println("当前当选master");
        }
    }

    private void initListener() {
        zkClient.subscribeChildChanges(rootPath, (parentPath, currentChilds) -> {
            doElection();
        });
    }

    /**
     * 执行选举
     */
    public void doElection() {
        Map<String, Object> childData = zkClient.getChildren(rootPath)
                .stream()
                .map(p -> rootPath + "/" + p)
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

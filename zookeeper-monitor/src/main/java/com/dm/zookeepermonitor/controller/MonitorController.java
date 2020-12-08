package com.dm.zookeepermonitor.controller;

import com.dm.OsBean;
import com.dm.constants.ZookeeperConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ,;,,;
 * ,;;'(
 * __      ,;;' ' \
 * /'  '\'~~'~' \ /'\.)
 * ,;(      )    /  |.
 * ,;' \    /-.,,(   ) \
 * ) /       ) / )|
 * ||        ||  \)
 * (_\       (_\
 *
 * @ClassName MonitorController
 * @Description 监控控制器--监控系统运行参数记录在zookeeper
 * @Author dm
 * @Date 2020/6/26 10:45
 * @slogan: 我自横刀向天笑，笑完我就去睡觉
 * @Version 1.0
 **/
@Controller
public class MonitorController implements InitializingBean {

    private ZkClient zkClient;

    private static final String ROOT_PATH = "/dm-master";

    Map<String, OsBean> map = new HashMap<>();

    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("items", getCurrentOsBeans());
        return "monitorlist";
    }

    private List<OsBean> getCurrentOsBeans() {
        return zkClient.getChildren(ROOT_PATH).stream()
                .map(p -> ROOT_PATH + "/" + p)
                .map(p -> convert(zkClient.readData(p)))
                .collect(Collectors.toList());
    }

    private OsBean convert(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, OsBean.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        zkClient = new ZkClient(ZookeeperConstants.SERVER, ZookeeperConstants.SESSION_TIME_OUT, ZookeeperConstants.CONNECTION_TIME_OUT);
        initSubscribeListener();
    }

    /**
     * 初始化订阅事件
     */
    public void initSubscribeListener() {
        zkClient.unsubscribeAll();
        // 获取所有子节点
        zkClient.getChildren(ROOT_PATH)
                .stream()
                // 得出子节点完整路径
                .map(p -> ROOT_PATH + "/" + p)
                .forEach(p -> {
                    zkClient.subscribeDataChanges(p, new DataChanges());// 数据变更的监听
                });
        //  监听子节点，的变更 增加，删除
        zkClient.subscribeChildChanges(ROOT_PATH, (parentPath, currentChilds) -> initSubscribeListener());
    }

    /**
     * 子节点数据变化
     */
    private class DataChanges implements IZkDataListener {

        @Override
        public void handleDataChange(String dataPath, Object data) {
            OsBean bean = convert((String) data);
            map.put(dataPath, bean);
            doFilter(bean);
        }

        @Override
        public void handleDataDeleted(String dataPath) {
            if (map.containsKey(dataPath)) {
                OsBean bean = map.get(dataPath);
                System.err.println("服务已下线:" + bean);
                map.remove(dataPath);
            }
        }
    }

    /**
     * 警告过滤
     */
    private void doFilter(OsBean bean) {
        // cpu 超过10% 报警
        if (bean.getCpu() > 10) {
            System.err.println("CPU 报警..." + bean.getCpu());
        }
    }

}

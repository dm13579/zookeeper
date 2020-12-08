package com.dm.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sound.sampled.FloatControl;
import java.util.Random;

/**
 * @className MutexLock
 * @cescription zk 互斥锁测试
 * @Author dm
 * @date 2020/12/8 15:32
 * @slogan: 我自横刀向天笑，笑完我就去睡觉
 * @Version 1.0
 **/
@Service
public class MutexLock {

    @Autowired
    CuratorFramework curatorFramework;

    public String handler(Integer id) throws Exception {
        InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework, "/product_"+id);
        try {
            interProcessMutex.acquire();
            // 模拟业务执行
            Thread.sleep((new Random()).nextInt(10)*1000);

        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e;
            }
        }finally {
            interProcessMutex.release();
        }
        return "ok";
    }
}

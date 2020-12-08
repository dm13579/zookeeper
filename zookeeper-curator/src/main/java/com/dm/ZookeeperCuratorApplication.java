package com.dm;

import com.dm.lock.MutexLock;
import org.apache.curator.utils.ZookeeperFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
  *@className zookeeperCuratorApplication
  *@cescription 启动类
  *@Author dm
  *@date 2020/12/8 15:26
  *@slogan: 我自横刀向天笑，笑完我就去睡觉
  *@Version 1.0
  **/
@SpringBootApplication
public class ZookeeperCuratorApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ZookeeperCuratorApplication.class, args);
    }
}

package com.dm.constants;

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
  *@className ZookeeperConstants
  *@cescription zk 公共类
  *@Author dm
  *@date 2020/12/8 15:40
  *@slogan: 我自横刀向天笑，笑完我就去睡觉
  *@Version 1.0
  **/
public class ZookeeperConstants {

    /**
     * zk服务端地址
     */
    public final static String SERVER = "*:2181";

    public final static String CLUSTER_SERVER = "*:2181,*:2182,*:2183";
    /**
     * zk session 超时时间
     */
    public final static Integer SESSION_TIME_OUT = 50000;

    /**
     * zk session 连接时间
     */
    public final static Integer CONNECTION_TIME_OUT = 10000;

    /**
     * 重试等待时间
     */
    public final static Integer BASE_SLEEP_TIME_MS = 1000;

    /**
     * 最大重试次数
     */
    public final static Integer MAX_RETRIES = 3;



}

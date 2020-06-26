package com.dm;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    ZookeeperLock zookeeperLock;
    static Long count = 0L;

    @Before
    public void init() {
        zookeeperLock = new ZookeeperLock();
    }

    @Test
    public void getLockTest() throws InterruptedException {
        Lock lock = zookeeperLock.lock("dm", 60 * 1000);
        System.out.println("成功获取锁");
        Thread.sleep(Long.MAX_VALUE);
        assert lock != null;
    }


    @Test
    public void run() throws InterruptedException, IOException {
        // 写数字 0+100 =100
        File file = new File("d:/test.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                Lock lock = zookeeperLock.lock(file.getPath(), 60 * 1000);
                try {
                    String firstLine = Files.lines(file.toPath()).findFirst().orElse("0");
                    int count = Integer.parseInt(firstLine);
                    count++;
                    Files.write(file.toPath(), String.valueOf(count).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    zookeeperLock.unlock(lock);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        String firstLine = Files.lines(file.toPath()).findFirst().orElse("0");
        System.out.println(firstLine);
    }

}
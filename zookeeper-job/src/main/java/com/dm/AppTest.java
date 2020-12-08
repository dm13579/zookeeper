package com.dm;

import org.junit.Test;

public class AppTest 
{
    @Test
    public void MasterTest() throws InterruptedException {
        System.out.println("master:" + JopMaster.isMaster());
        Thread.sleep(Long.MAX_VALUE);
    }
}

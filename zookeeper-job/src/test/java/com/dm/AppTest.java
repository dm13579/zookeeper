package com.dm;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AppTest 
{
    @Test
    public void MasterTest() throws InterruptedException {
        JopMaster instance = JopMaster.getInstance();
        System.out.println("master:" + JopMaster.isMaster());
        Thread.sleep(Long.MAX_VALUE);
    }
}

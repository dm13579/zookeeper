package com.dm.controller;

import com.dm.lock.MutexLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
  *@className Controller
  *@cescription TODO
  *@Author dm
  *@date 2020/12/8 16:23
  *@slogan: 我自横刀向天笑，笑完我就去睡觉
  *@Version 1.0
  **/
@RestController
@RequestMapping("/test")
public class Controller {

    @Autowired
    MutexLock mutexLock;
    @RequestMapping(value = "/lock/{id}", method = RequestMethod.GET)
    public String lock(@PathVariable Integer id) throws Exception {
        return mutexLock.handler(id);
    }

}

package com.example.activitidemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tlj
 * @date 2019/7/19
 */

@RestController
@RequestMapping("/test")
public interface ActivityConsumerService {
    /**
     * 流程demo
     * @return
     */
    @RequestMapping(value="/activitiDemo",method=RequestMethod.GET)
    boolean startActivityDemo();

}

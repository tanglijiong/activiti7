package com.example.activitidemo.controller;

import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tlj
 * @date 2019/9/5
 */
@RestController
public class XXXController {
    @PostMapping(value="/demo1/start1")
//    @ResponseBody
    public JSONObject start() {
        return new JSONObject();
    }

}

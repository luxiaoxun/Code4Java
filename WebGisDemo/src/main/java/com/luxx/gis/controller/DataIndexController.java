package com.luxx.gis.controller;

import com.luxx.gis.executor.IndexExecutor;
import com.luxx.gis.model.ResultData;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data")
@Api(tags = "data")
@Slf4j
public class DataIndexController {
    @Autowired
    @Qualifier("poiIndexExecutor")
    private IndexExecutor indexExecutor;

    @PostMapping(value = "/index")
    public ResultData indexData() {
        log.info("Start to index data");
        indexExecutor.start();
        ResultData msg = new ResultData();
        msg.setMsg("ok");
        return msg;
    }
}

package com.luxx.web.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/map")
@Api(tags = "map")
@Slf4j
public class MapController {

    @GetMapping("/map")
    public ModelAndView map() {
        return new ModelAndView("map");
    }

}

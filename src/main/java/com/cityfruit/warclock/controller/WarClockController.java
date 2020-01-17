package com.cityfruit.warclock.controller;

import com.cityfruit.warclock.param.CreateOrUpdateCaseParam;
import com.cityfruit.warclock.play.AudioPlayRedio;

import com.cityfruit.warclock.service.WarClockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 * @author TongTong
 * @date 2019/12/24
 */
@Controller
public class WarClockController {

    @Autowired
    private WarClockService warClockService;

    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String startRedio() throws IOException {
        AudioPlayRedio.startPlay();
        return "开始";
    }

    @ResponseBody
    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public String stopRedio() throws IOException {
        AudioPlayRedio.stopPlay();
        return "停止";
    }

    @ResponseBody
    @RequestMapping(value = "/ok", method = RequestMethod.GET)
    public String okRedio() {
        AudioPlayRedio.okPlay();
        return "ok";
    }

    @ResponseBody
    @RequestMapping(value = "/get_safe_time", method = RequestMethod.GET)
    public Map<String, Object> getSafeTime() {
        return warClockService.getLiveSafeTime();
    }

    @ResponseBody
    @RequestMapping(value = "/get_longest_safe_time", method = RequestMethod.GET)
    public Map<String, Object> getLongestSafeTime() {
        return warClockService.getLongestSafeTime();
    }

    @ResponseBody
    @RequestMapping(value = "/create_or_update_cases")
    public String CreateOrUpdateCase(@RequestBody CreateOrUpdateCaseParam param) throws IOException {
        //首先根据群组查看最新的一条记录
        return warClockService.createOrUpdateCases(param);
    }

    @RequestMapping(value = "/war_clock_index")
    public String warClockIndex() {
        return "index";
    }

}

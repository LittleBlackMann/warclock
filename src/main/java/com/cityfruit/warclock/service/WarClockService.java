package com.cityfruit.warclock.service;

import com.cityfruit.warclock.param.CreateOrUpdateCaseParam;

import java.io.IOException;
import java.util.Map;

/**
 * @author TongTong
 * @date 2019/12/24
 */
public interface WarClockService {
    public Map<String, Object> getLiveSafeTime();

    public String createOrUpdateCases(CreateOrUpdateCaseParam param) throws IOException;

    /**
     * 获取最长稳定时长
     *
     * @return 各团队最长稳定时长
     */
    Map<String, Object> getLongestSafeTime();
}

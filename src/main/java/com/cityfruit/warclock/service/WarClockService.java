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
}

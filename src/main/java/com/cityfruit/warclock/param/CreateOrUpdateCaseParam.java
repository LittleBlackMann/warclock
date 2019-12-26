package com.cityfruit.warclock.param;

import lombok.Data;

/**
 * @author TongTong
 * @date 2019/12/25
 */
@Data
public class CreateOrUpdateCaseParam {
    private String team = "friday";
    private String caseName = "default";
    private String input;
}

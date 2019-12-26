package com.cityfruit.warclock.enums;

import lombok.Getter;

/**
 * @author TongTong
 * @date 2019/12/24
 */
@Getter
public enum StateEnum {
    //创建事件中
    CREATE(1, "创建事件"),
    //事件处理中
    DEALING(2, "问题解决中"),
    //事件已完成
    DONE(3, "已完成");

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态名称
     */
    private String state;

    StateEnum(Integer code, String state) {

        this.code = code;
        this.state = state;
    }
}

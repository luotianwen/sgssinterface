package com.sgss.www.swagger.model.enums;

/**
 * 协议 枚举
 *
 * @author lee
 * @version V1.0.0
 * @date 2017/12/11
 */
public enum Scheme {
    /**
     * 协议类型
     */
    HTTP("http"),
    HTTPS("https");

    private String name;

    private Scheme(String name) {
        this.name = name;
    }
}

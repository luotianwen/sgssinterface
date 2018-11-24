package com.sgss.www.swagger.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 全局参数
 *
 * @author lee
 * @version V1.0.0
 * @date 2018/1/5
 */
public class SwaggerGlobalPara {
    private static List<SwaggerPath.Parameter> parameterList = Lists.newArrayList();

    public static List<SwaggerPath.Parameter> getParameterList() {
        return parameterList;
    }

    public static void addPara(SwaggerPath.Parameter parameter) {
        parameterList.add(parameter);
    }
}

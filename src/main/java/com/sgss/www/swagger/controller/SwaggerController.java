package com.sgss.www.swagger.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.hongkzh.www.swagger.annotation.*;
import com.sgss.www.swagger.model.SwaggerDoc;
import com.sgss.www.swagger.model.SwaggerGlobalPara;
import com.sgss.www.swagger.model.SwaggerPath;
import com.sgss.www.swagger.model.SwaggerResponse;
import com.sgss.www.swagger.utils.ClassHelper;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.sgss.www.swagger.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * swagger
 *

 * @author martins
 * @version V1.0.0
 * @date 2017/7/7
 */
public class SwaggerController extends Controller {

    public void index() {
        render("shop.html");
    }

    public void api() {
        SwaggerDoc doc = new SwaggerDoc();
        Map<String, Map<String, SwaggerPath.ApiMethod>> paths = new HashMap<>();
        Map<String, String> classMap = Maps.newHashMap();
        Set<Class<?>> classSet = ClassHelper.getBeanClassSet();
        for (Class<?> cls : classSet) {
            if (cls.isAnnotationPresent(Api.class)) {
                Api api = cls.getAnnotation(Api.class);

                if (!classMap.containsKey(api.tag())) {
                    classMap.put(api.tag(), api.description());
                }

                Method[] methods = cls.getMethods();

                for (Method method : methods) {
                    Annotation[] annotations = method.getAnnotations();
                    SwaggerPath.ApiMethod apiMethod = new SwaggerPath.ApiMethod();
                    apiMethod.setOperationId("");
                    apiMethod.addProduce("application/json");

                    List<SwaggerPath.Parameter> parameterList = SwaggerGlobalPara.getParameterList();
                    if (parameterList != null && parameterList.size() > 0) {
                        for (SwaggerPath.Parameter parameter : parameterList) {
                            apiMethod.addParameter(parameter);
                        }
                    }

                    for (Annotation annotation : annotations) {
                        Class<? extends Annotation> annotationType = annotation.annotationType();
                        if (ApiOperation.class == annotationType) {
                            ApiOperation apiOperation = (ApiOperation) annotation;
                            Map<String, SwaggerPath.ApiMethod> methodMap = new HashMap<>();
                            apiMethod.setSummary(apiOperation.description());
                            apiMethod.setDescription(apiOperation.description());
                            apiMethod.addTag(apiOperation.tag());
                            apiMethod.addConsume(apiOperation.consumes());
                            methodMap.put(apiOperation.httpMethod(), apiMethod);
                            paths.put(apiOperation.url(), methodMap);
                        } else if (Params.class == annotationType) {
                            Params apiOperation = (Params) annotation;
                            Param[] params = apiOperation.value();
                            for (Param apiParam : params) {
                                if ("file".equals(apiParam.dataType())) {
                                    apiMethod.addParameter(new SwaggerPath.Parameter(apiParam.name(), "formData", apiParam.description(), apiParam.required(), apiParam.dataType(), apiParam.format(), apiParam.defaultValue()));
                                } else {
                                    apiMethod.addParameter(new SwaggerPath.Parameter(apiParam.name(), apiParam.description(), apiParam.required(), apiParam.dataType(), apiParam.format(), apiParam.defaultValue()));
                                }
                            }
                        } else if (Param.class == annotationType) {
                            Param apiParam = (Param) annotation;
                            apiMethod.addParameter(new SwaggerPath.Parameter(apiParam.name(), apiParam.description(), apiParam.required(), apiParam.dataType(), apiParam.format(), apiParam.defaultValue()));
                        }
                        else if (ApiResponses.class == annotationType) {
                            ApiResponses responses = (ApiResponses) annotation;
                            ApiResponse[] response = responses.value();
                            for (ApiResponse apiParam : response) {
                                    apiMethod.addResponse(apiParam.code()+"",new SwaggerResponse(apiParam.message()));
                                    ResponseHeader[] responseHeaders =apiParam.responseHeaders();
                                    Map map=new HashMap();
                                    for (ResponseHeader responseHeader : responseHeaders) {
                                        map.put(responseHeader.name(),responseHeader.description());
                                    }
                                System.out.println(JsonKit.toJson(map));
                                  apiMethod.addResponse("",new SwaggerResponse(JsonKit.toJson(map)));


                                    //apiMethod.addParameter(new SwaggerPath.Parameter(apiParam.name(), apiParam.description(), apiParam.required(), apiParam.dataType(), apiParam.format(), apiParam.defaultValue()));

                            }
                            //apiMethod.addResponse("",new SwaggerResponse(apiParam.value()));
                            //apiMethod.addParameter(new SwaggerPath.Parameter(apiParam.name(), apiParam.description(), apiParam.required(), apiParam.dataType(), apiParam.format(), apiParam.defaultValue()));
                        }
                       /* else if (ApiModelProperty.class == annotationType) {
                            ApiModelProperty apiModelProperty = (ApiModelProperty) annotation;
                             //apiMethod.addResponse("",new SwaggerResponse(apiModelProperty.value()));
                              apiMethod.addParameter(new SwaggerPath.Parameter(apiModelProperty.name(), apiModelProperty.notes(), apiModelProperty.required(), apiModelProperty.dataType(), apiModelProperty.notes(), apiModelProperty.value()));
                        }*/
                    }
                }
            }
        }

        if (classMap.size() > 0) {
            for (String key : classMap.keySet()) {
                doc.addTags(new SwaggerDoc.TagBean(key, classMap.get(key)));
            }
        }
        doc.setPaths(paths);

        // swaggerUI 使用Java的关键字default作为默认值,此处将生成的JSON替换
        renderText(JSON.toJSONString(doc).replaceAll("\"defaultValue\"", "\"default\""));
    }
}

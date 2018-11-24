package com.sgss.www.swagger.config.routes;

import com.sgss.www.swagger.controller.SwaggerController;
import com.jfinal.config.Routes;

/**
 * 默认路由
 *

 * @author martins
 * @version V1.0.0
 * @date 2017/7/8
 */
public class SwaggerRoutes extends Routes {

    @Override
    public void config() {
        setBaseViewPath("/WEB-INF/views");
        add("/swagger", SwaggerController.class);
    }

}

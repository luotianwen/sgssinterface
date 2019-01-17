package com.sgss.www.routes;

import com.jfinal.config.Routes;
import com.sgss.www.shop.controller.ShopController;
import com.sgss.www.user.controller.UserController;

/**
 * @author martins
 */
public class V1Routes extends Routes {
    @Override
    public void config() {
        add("/v1/user", UserController.class);
        add("/v1/shop", ShopController.class);
    }
}

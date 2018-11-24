package com.sgss.www.shop.controller;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.sgss.www.conmon.BaseController;
import com.sgss.www.conmon.ReqResponse;
import com.sgss.www.shop.service.ShopService;
import com.sgss.www.swagger.annotation.*;

@Api(tag = "shop", description = "电商")
public class ShopController extends BaseController {
    @Inject
    ShopService shopService;
    @ApiOperation(url = "/v1/shop/index", tag = "shop", httpMethod = "post", description = "首页数据")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " carousels[logo 图片 goodsId 商品id] " +
                            "coupons[couponId 优惠券id  name 名称 full 满多少 reduction 减多少]"+
                            "brands[brandId 品牌id  name 名称 logo]"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void index() {
        ReqResponse<Record> r = new ReqResponse();
        Record returnData=new Record();
        //轮播图
        returnData.set("carousels",shopService.getCarousel());
        //优惠券
        returnData.set("coupons",shopService.getCoupons());
        //品牌
        returnData.set("brands",shopService.getBrands());
        r.setData(returnData);
        renderJson(r);
    }
    @ApiOperation(url = "/v1/shop/goodsList", tag = "shop", httpMethod = "post", description = "首页商品数据")
    @Params({
            @Param(name = "pageNumber", description = "页数", required = true, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " goodsId id name名称logo 主图price价格 sales 销量"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void goodsList() {
        int pageNumber=getParaToInt("pageNumber");
        ReqResponse<Page<Record>> r = new ReqResponse();
        r.setData(shopService.goodsList(pageNumber));
        renderJson(r);
    }
}

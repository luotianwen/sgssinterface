package com.sgss.www.shop.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.sgss.www.conmon.BaseController;
import com.sgss.www.conmon.BusinessException;
import com.sgss.www.conmon.RedisTool;
import com.sgss.www.conmon.ReqResponse;
import com.sgss.www.shop.service.ShopService;
import com.sgss.www.swagger.annotation.*;
import net.arccode.wechat.pay.api.common.exception.WXPayApiException;
import net.arccode.wechat.pay.api.common.util.*;
import net.arccode.wechat.pay.api.protocol.unified_order.UnifiedOrderRequest;
import net.arccode.wechat.pay.api.protocol.unified_order.UnifiedOrderResponse;
import net.arccode.wechat.pay.api.service.WXPayClient;

import java.util.List;

@Api(tag = "shop", description = "电商")
public class ShopController extends BaseController {
    Log log = Log.getLog(ShopController.class);
    @Inject
    ShopService shopService;
    @ApiOperation(url = "/v1/shop/hotData", tag = "shop", httpMethod = "post", description = "热搜数据")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void hotData() {
        ReqResponse<List<Record>> r = new ReqResponse();

        r.setData(shopService.hotData());
        renderJson(r);
    }
    @ApiOperation(url = "/v1/shop/indexData", tag = "shop", httpMethod = "post", description = "首页数据")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " carousels[logo 图片 goodsId 商品id] " +
                            "coupons[couponId 优惠券id  name 名称 full 满多少 reduction 减多少]" +
                            "brands[brandId 品牌id  name 名称 logo]"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void indexData() {
        ReqResponse<Record> r = new ReqResponse();
        Record returnData = new Record();
        //轮播图
        returnData.set("carousels", shopService.getCarousel());
        //优惠券
        returnData.set("coupons", shopService.getCoupons());
        //品牌
        returnData.set("brands", shopService.getBrands());
        r.setData(returnData);
        renderJson(r);
    }
    @ApiOperation(url = "/v1/shop/brandList", tag = "shop", httpMethod = "post", description = "首页数据")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void brandList() {
        ReqResponse<List<Record>> r = new ReqResponse();
        r.setData( shopService.getBrands());
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
        int pageNumber = getParaToInt("pageNumber");
        ReqResponse<Page<Record>> r = new ReqResponse();
        r.setData(shopService.goodsList(pageNumber));
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/allCategory", tag = "shop", httpMethod = "post", description = "分类数据")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " categoryId,logo,name "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void allCategory() {
        ReqResponse<List<Record>> r = new ReqResponse();
        r.setData(shopService.allCategory());
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/subCategory", tag = "shop", httpMethod = "post", description = "分类数据")
    @Params({
            @Param(name = "categoryId", description = "分类id", required = true, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " categoryId,logo,name "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void subCategory() {
        String categoryId = getPara("categoryId");
        ReqResponse<List<Record>> r = new ReqResponse();
        r.setData(shopService.subCategory(categoryId));
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/getSearchGoods", tag = "shop", httpMethod = "post", description = "搜索商品")
    @Params({
            @Param(name = "brandId", description = "品牌id", required = false, dataType = "string"),
            @Param(name = "content", description = "搜索内容", required = false, dataType = "string"),
            @Param(name = "pageNumber", description = "页数", required = true, dataType = "string"),
            @Param(name = "orderby", description = "1 销量 2时间 3价格低 4价格高", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " goodId,logo,name price sales"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void getSearchGoods() {
        String brandId = getPara("brandId");
        String content = getPara("content");
        String orderby = getPara("orderby");
        int pageNumber = getParaToInt("pageNumber");
        ReqResponse<Page<Record>> r = new ReqResponse();
        r.setData(shopService.getSearchGoods(brandId, content,pageNumber,orderby));
        renderJson(r);
    }
    @ApiOperation(url = "/v1/shop/getCategoryGoods", tag = "shop", httpMethod = "post", description = "分类商品数据")
    @Params({
            @Param(name = "categoryId", description = "分类id", required = true, dataType = "string"),
            @Param(name = "pageNumber", description = "页数", required = true, dataType = "string"),
            @Param(name = "orderby", description = "1 销量 2时间 3价格低 4价格高", required = true, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " goodId,logo,name price sales"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void getCategoryGoods() {
        String categoryId = getPara("categoryId");
        int pageNumber = getParaToInt("pageNumber");
        String orderby = getPara("orderby");
        ReqResponse<Page<Record>> r = new ReqResponse();
        r.setData(shopService.getCategoryGoods(categoryId, pageNumber,orderby));
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/getHomeGoods", tag = "shop", httpMethod = "post", description = "首页分类数据")
    @Params({
            @Param(name = "pageNumber", description = "页数", required = true, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " goodId,logo,name price sales"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void getHomeGoods() {
        int pageNumber = getParaToInt("pageNumber");
        ReqResponse<Page<Record>> r = new ReqResponse();
        r.setData(shopService.getHomeGoods(pageNumber));
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/getGoodsByGooId", tag = "shop", httpMethod = "post", description = " 商品详情")
    @Params({
            @Param(name = "goodsId", description = "商品id", required = true, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " goodId,logo,name price sales"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void getGoodsByGooId() {
        String goodsId = getPara("goodsId");

        ReqResponse<Record> r = new ReqResponse();
        r.setData(shopService.getGoodsByGooId(goodsId));
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/addCart", tag = "shop", httpMethod = "post", description = "加入购物车")
    @Params({
            @Param(name = "skuId", description = "skuId", required = true, dataType = "string"),
            @Param(name = "tokenId", description = "tokenId", required = true, dataType = "string"),
            @Param(name = "number", description = "数量", required = true, dataType = "int"),
            @Param(name = "type", description = "0加入购物车1立即购买", required = true, dataType = "int"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "cartCount  cartId"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void addCart() {
        String skuId = getPara("skuId");
        String userId = getAttr("userId");
        int number = getParaToInt("number");
        int type = getParaToInt("type");
        ReqResponse<Record> r = new ReqResponse();
        r.setData(shopService.addCart(skuId, userId, number, type));
        renderJson(r);
    }
    @ApiOperation(url = "/v1/shop/getCartData", tag = "shop", httpMethod = "post", description = "购物车总数")
    @Params({
            @Param(name = "tokenId", description = "tokenId", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "cartCount  cartId"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void getCartData() {
        String userId = getAttr("userId");
        ReqResponse<Integer> r = new ReqResponse();
        r.setData(shopService.cartCount(userId));
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/syncCart", tag = "shop", httpMethod = "post", description = "操作购物车")
    @Params({
            @Param(name = "cartId", description = "cartId", required = true, dataType = "string"),
            @Param(name = "tokenId", description = "tokenId", required = true, dataType = "string"),
            @Param(name = "number", description = "数量", required = true, dataType = "int"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void syncCart() {
        String cartId = getPara("cartId");
        String userId = getAttr("userId");
        int number = getParaToInt("number");
        ReqResponse<String> r = new ReqResponse();
        try {
            shopService.syncCart(cartId, userId, number);
        } catch (Exception e) {
            r.setCode(1);
            r.setMsg("操作失败");
            r.setData("");
        }
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/cartList", tag = "shop", httpMethod = "post", description = "购物车列表")
    @Params({
            @Param(name = "tokenId", description = "tokenId", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void cartList() {
        String userId = getAttr("userId");
        ReqResponse<List<Record>> r = new ReqResponse();
        r.setData(shopService.cartList(userId));
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/removeCart", tag = "shop", httpMethod = "post", description = "删除购物车")
    @Params({
            @Param(name = "cartId", description = "skuId", required = true, dataType = "string"),
            @Param(name = "tokenId", description = "tokenId", required = true, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void removeCart() {
        String cartId = getPara("cartId");
        String userId = getAttr("userId");

        ReqResponse<Record> r = new ReqResponse();
        shopService.removeCart(cartId, userId);
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/preorder", tag = "shop", httpMethod = "post", description = "预下单")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "cartId", description = "购物车id 数组", required = true, dataType = "string"),
            @Param(name = "addressId", description = "地址id", required = false, dataType = "string"),
            @Param(name = "couponId", description = "  ", required = false, dataType = "string"),
    })

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void preorder() {
        String userId = getAttr("userId");
        String addressId = getPara("addressId");
        String[] cartIds = getPara("cartId").split(",");
        String couponId = getPara("couponId");
        ReqResponse<Record> r = new ReqResponse();

        try {
            r.setData(shopService.preorder(userId, cartIds, addressId, couponId));
        } catch (BusinessException e) {
            r.setCode(1);
            r.setMsg(e.getErrMsg());
        }
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/saveOrder", tag = "shop", httpMethod = "post", description = "结算订单")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "cartId", description = "购物车id ", required = true, dataType = "string"),
            @Param(name = "addressId", description = "地址id", required = true, dataType = "string"),
            @Param(name = "couponId", description = "   ", required = false, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void saveOrder() {
        String userId = getAttr("userId");
        String[] cartIds = getPara("cartId").split(",");
        String addressId = getPara("addressId");
        String couponId = getPara("couponId");
        ReqResponse<Record> r = new ReqResponse();
        try {
            r.setData(shopService.saveOrder(userId, cartIds, addressId, couponId));

        } catch (BusinessException e) {
            r.setCode(1);
            r.setMsg(e.getErrMsg());
        }
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/orderOk", tag = "shop", httpMethod = "post", description = "订单确认 ")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "ordersId", description = "订单号", required = false, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "   "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void orderOk() {
        String userId = getAttr("userId");
        String ordersId = getPara("ordersId");
        ReqResponse<Record> r = new ReqResponse();
        try {
            shopService.orderOk(userId, ordersId);
        } catch (Exception e) {
            r.setCode(1);
            r.setMsg("请求失败");
        }
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/orderDelete", tag = "shop", httpMethod = "post", description = "订单删除")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "ordersId", description = "订单号", required = false, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "   "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void orderDelete() {
        String userId = getAttr("userId");
        String ordersId = getPara("ordersId");
        ReqResponse<Record> r = new ReqResponse();
        try {
            shopService.orderDelete(userId, ordersId);
        } catch (Exception e) {
            r.setCode(1);
            r.setMsg("请求失败");
        }
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/orderList", tag = "shop", httpMethod = "post", description = "订单列表")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "name", description = "内容", required = false, dataType = "string"),
            @Param(name = "pageNumber", description = "页码", required = true, dataType = "string"),
            @Param(name = "type", description = "类型 ''全部订单 10等待付款 20待收货 50已完成 40 已取消 ", required = false, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  totalPrice 支付现金 limitTime 支付倒计时 orderNumber 订单号  currentTime 当前时间 " +
                            "expressName 快递公司 invoiceNo 订单号 state 订单状态 0全部 10等待付款 20 已支付  30 已发货 50 已完成  40 已取消 60已关闭" +
                            "subList 订单商品[colorName 颜色名称 title 标题 imgSrc 商品图  number  数量 price 价格 specName规格名称] "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void orderList() {
        String userId = getAttr("userId");
        String type = getPara("type");
        String name = getPara("name");
        int pageNumber = getParaToInt("pageNumber");
        ReqResponse<Page<Record>> r = new ReqResponse();
        Page<Record> record = shopService.orderList(userId, type, pageNumber, name);
        r.setData(record);
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/weixinpay", tag = "shop", httpMethod = "post", description = "微信创建支付")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "ordersId", description = "订单id 逗号隔开", required = true, dataType = "string"),
            @Param(name = "type", description = " 1订单", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void weixinpay() {
        String userId = getAttr("userId");
        String ordersId = getPara("ordersId");
        String type = getPara("type");
        String openId = ((Record) RedisTool.getObject(userId)).getStr("openId");
        ReqResponse<Record> r = new ReqResponse();

         WXPayClient wxPayClient = new WXPayClient(PropKit.get("weixin.AppID"), PropKit.get("weixin.MCHID"), PropKit.get("weixin.KEY"));

        String noncestr = SDKUtils.genRandomStringByLength(32);
        String body = "";
        String tradeTyp = "JSAPI";
        if (type.equals("1")) {
            body = "购买商品";
        }
        String ip = PropKit.get("weixin.ip");
        int totalFee = shopService.getTotalFeeByOrderId(type, ordersId);
        String url = PropKit.get("weixin.notifyUrl");

        UnifiedOrderRequest request = new UnifiedOrderRequest(body, SDKUtils.genOutTradeNo(),
                totalFee, ip, url, tradeTyp, noncestr);
        request.setOpenId(openId);
        request.setAttach(type + "-" + ordersId);
        try {
            UnifiedOrderResponse response = wxPayClient.execute(request);
            if (response != null && "SUCCESS".equals(response.getReturnCode()) && "SUCCESS".equals(response.getResultCode())) {

                try {
                    Record record = new Record();
                    String appid = PropKit.get("weixin.AppID");
                    noncestr = SDKUtils.genRandomStringByLength(32);
                    String package1 = "prepay_id=" + response.getPrepayId();
                    long timeStamp = System.currentTimeMillis() / 1000;
                    record.set("timeStamp", timeStamp);
                    record.set("nonceStr", noncestr);
                    record.set("package", package1);
                    RequestParametersHolder requestParametersHolder = new RequestParametersHolder();
                    ACHashMap acHashMap = new ACHashMap();
                    acHashMap.put("appId", appid);
                    acHashMap.put("package", package1);
                    acHashMap.put("nonceStr", noncestr);
                    acHashMap.put("timeStamp", timeStamp);
                    requestParametersHolder.setApplicationParams(acHashMap);
                    String requestBoyStr = MapUtils.map2XmlString(acHashMap);
                    System.out.println(requestBoyStr);
                    String signContent = WXPaySignUtils.getSignatureContent(requestParametersHolder);
                    String sign = WXPaySignUtils.md5Sign(signContent, PropKit.get("weixin.KEY"), "UTF-8").toUpperCase();
                    record.set("sign", sign);
                    log.debug(sign);
                    System.out.println(sign);
                    r.setData(record);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.debug(e.getMessage());
                    r.setCode(1);
                    r.setMsg("请求支付失败");
                }
            } else {
                r.setCode(1);
                r.setMsg("签名错误");

            }
        } catch (WXPayApiException e) {
            e.printStackTrace();
            log.debug(e.getMessage());
            r.setCode(1);
            r.setMsg("支付错误"+e.getMessage());
        }

        renderJson(r);
    }


    @ApiOperation(url = "/v1/shop/logistics", tag = "shop", httpMethod = "post", description = "物流信息")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "courier", description = "快递公司", required = true, dataType = "string"),
            @Param(name = "courierNumber", description = "快递单号", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "{\"message\":\"ok\",\"status\":\"1\",\"state\":\"3\",\"data\":[{\"time\":\"2012-07-07 13:35:14\",\"context\":\"客户已签收\"},{\"time\":\"2012-07-07 09:10:10\",\"context\":\"离开 [北京_房山营业所_石景山营业厅] 派送中，递送员[温]，电话[]\"},{\"time\":\"2012-07-06 19:46:38\",\"context\":\"到达 [北京_房山营业所_石景山营业厅]\"},{\"time\":\"2012-07-06 15:22:32\",\"context\":\"离开 [北京_房山营业所_石景山营业厅] 派送中，递送员[温]，电话[]\"},{\"time\":\"2012-07-06 15:05:00\",\"context\":\"到达 [北京_房山营业所_石景山营业厅]\"},{\"time\":\"2012-07-06 13:37:52\",\"context\":\"离开 [北京_同城中转站] 发往 [北京_房山营业所_石景山营业厅]\"},{\"time\":\"2012-07-06 12:54:41\",\"context\":\"到达 [北京_同城中转站]\"},{\"time\":\"2012-07-06 11:11:03\",\"context\":\"离开 [北京运转中心_航空_驻站班组] 发往 [北京_同城中转站]\"},{\"time\":\"2012-07-06 10:43:21\",\"context\":\"到达 [北京运转中心_航空_驻站班组]\"},{\"time\":\"2012-07-05 21:18:53\",\"context\":\"离开 [福建_厦门支公司] 发往 [北京运转中心_航空]\"},{\"time\":\"2012-07-05 20:07:27\",\"context\":\"已取件，到达 [福建_厦门支公司]\"}]}"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void logistics() {
        String courier = getPara("courier");
        String courierNumber = getPara("courierNumber");
        ReqResponse<String> r = new ReqResponse();
        String str = shopService.logistics(courier, courierNumber);
        r.setData(str);
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/saveAfterOrder", tag = "shop", httpMethod = "post", description = "申请售后")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "orderNumber", description = "订单号", required = true, dataType = "string"),
            @Param(name = "type", description = "1退货2换货", required = true, dataType = "string"),
            @Param(name = "content", description = "售后原因", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void saveAfterOrder() {
        String orderNumber = getPara("orderNumber");
        String type = getPara("type");
        String content = getPara("content");
        String userId = getAttr("userId");
        ReqResponse<String> r = new ReqResponse();
        try {
            shopService.saveAfterOrder(orderNumber, type, content, userId);
        } catch (BusinessException e) {
            r.setCode(1);
            r.setMsg(e.getErrMsg());
        }
        renderJson(r);
    }
    @ApiOperation(url = "/v1/shop/orderDetail", tag = "shop", httpMethod = "post", description = "订单详情")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "orderNumber", description = "订单号", required = true, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void orderDetail() {
        String orderNumber = getPara("orderNumber");

        String userId = getAttr("userId");
        ReqResponse<Record> r = new ReqResponse();
        try {
           r.setData(shopService.orderDetail(orderNumber, userId));
        } catch (BusinessException e) {
            r.setCode(1);
            r.setMsg(e.getErrMsg());
        }
        renderJson(r);
    }
    @ApiOperation(url = "/v1/shop/cancelOrder", tag = "shop", httpMethod = "post", description = "取消订单")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "orderNumber", description = "订单号", required = true, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void cancelOrder() {
        String orderNumber = getPara("orderNumber");
        String userId = getAttr("userId");
        ReqResponse<String> r = new ReqResponse();
        try {
            shopService.cancelOrder(orderNumber, userId) ;
        } catch (BusinessException e) {
            r.setCode(1);
            r.setMsg(e.getErrMsg());
        }
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/afterOrderList", tag = "shop", httpMethod = "post", description = "售后订单列表")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "pageNumber", description = "页码", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  totalPrice 支付现金 limitTime 支付倒计时 orderNumber 订单号  currentTime 当前时间 " +
                            "expressName 快递公司 invoiceNo 订单号 state 订单状态 0全部 10等待付款 20 已支付  30 已发货 50 已完成  40 已取消 60已关闭" +
                            "subList 订单商品[colorName 颜色名称 title 标题 imgSrc 商品图  number  数量 price 价格 specName规格名称] "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void afterOrderList() {
        String userId = getAttr("userId");
        int pageNumber = getParaToInt("pageNumber");
        ReqResponse<Page<Record>> r = new ReqResponse();
        Page<Record> record = shopService.afterOrderList(userId,  pageNumber );
        r.setData(record);
        renderJson(r);
    }
    @ApiOperation(url = "/v1/shop/cancelAfterOrder", tag = "shop", httpMethod = "post", description = "取消售后订单")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "afterOrderId", description = "单号", required = true, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void cancelAfterOrder() {
        String afterOrderId = getPara("afterOrderId");
        String userId = getAttr("userId");
        ReqResponse<String> r = new ReqResponse();
        try {
            shopService.cancelAfterOrder(afterOrderId, userId) ;
        } catch (BusinessException e) {
            r.setCode(1);
            r.setMsg(e.getErrMsg());
        }
        renderJson(r);
    }


    @ApiOperation(url = "/v1/shop/afterOrderDetail", tag = "shop", httpMethod = "post", description = "取消售后订单")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "afterOrderId", description = "单号", required = true, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void afterOrderDetail() {
        String userId = getAttr("userId");
        String afterOrderId = getPara("afterOrderId");
        ReqResponse<Record> r = new ReqResponse();
        r.setData(shopService.afterOrderDetail(userId, afterOrderId ));
        renderJson(r);
    }
    @ApiOperation(url = "/v1/shop/getExpress", tag = "shop", httpMethod = "post", description = "快递公司信息")

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void getExpress() {

        ReqResponse<List<Record>> r = new ReqResponse();
        r.setData(shopService.getExpress());
        renderJson(r);
    }

    @ApiOperation(url = "/v1/shop/returnExpress", tag = "shop", httpMethod = "post", description = "退货快递")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "afterOrderId", description = "单号", required = true, dataType = "string"),
            @Param(name = "returnInvoiceNo", description = "快递单号", required = true, dataType = "string"),
            @Param(name = "returnExpressName", description = "快递公司", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void returnExpress() {
        String userId = getAttr("userId");
        String afterOrderId = getPara("afterOrderId");
        String returnInvoiceNo = getPara("returnInvoiceNo");
        String returnExpressName = getPara("returnExpressName");
        ReqResponse<String> r = new ReqResponse();
        try {
            shopService.returnExpress(userId, afterOrderId, returnInvoiceNo, returnExpressName);
        }catch (BusinessException e){
            r.setCode(1);
            r.setMsg(e.getErrMsg());
        }
        renderJson(r);
    }

}

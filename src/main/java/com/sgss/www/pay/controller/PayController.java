package com.sgss.www.pay.controller;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.sgss.www.conmon.*;
import com.sgss.www.swagger.annotation.*;
import net.arccode.wechat.pay.api.common.exception.WXPayApiException;
import net.arccode.wechat.pay.api.common.util.ACHashMap;
import net.arccode.wechat.pay.api.common.util.RequestParametersHolder;
import net.arccode.wechat.pay.api.common.util.SDKUtils;
import net.arccode.wechat.pay.api.common.util.WXPaySignUtils;
import net.arccode.wechat.pay.api.protocol.pay_notify.PayNotifyResponse;
import net.arccode.wechat.pay.api.protocol.unified_order.UnifiedOrderRequest;
import net.arccode.wechat.pay.api.protocol.unified_order.UnifiedOrderResponse;
import net.arccode.wechat.pay.api.service.WXPayClient;
import org.dom4j.DocumentException;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Map;

/**
 * @author martins
 */
@Api(tag = "pay", description = "支付")
public class PayController extends BaseController {
    Log log = Log.getLog(PayController.class);

    @ApiOperation(url = "/v1/pay/weixinpay", tag = "pay", httpMethod = "post", description = "微信创建支付")
    @Params({
            @Param(name = "loginUid", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "ordersId", description = "订单id 逗号隔开", required = true, dataType = "string"),
            @Param(name = "type", description = " 1乐购订单 2app充值 3后台充值4游戏租店铺5游戏充值 ", required = true, dataType = "string"),
            @Param(name = "backUrl", description = "回调地址", required = false, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void weixinpay() {
        //String loginUid=getAttr("loginUid");
        String ordersId = getPara("ordersId");
        String type = getPara("type");
        String backUrl = getPara("backUrl");

        ReqResponse<Record> r = new ReqResponse();
        WXPayClient wxPayClient = new WXPayClient(PropKit.get("weixin.AppID"), PropKit.get("weixin.MCHID"), PropKit.get("weixin.KEY"));

        String noncestr = SDKUtils.genRandomStringByLength(32);
        String body = "";
        String tradeTyp = "APP";
        if (type.equals("1")) {
            body = "购买乐购商品";
        } else if (type.equals("3")) {
            body = "优惠券充值";
            tradeTyp = "NATIVE";
        } else if (type.equals("2")) {
            body = "app充值";
        } else if (type.equals("4")) {
            body = "租店铺";
        } else if (type.equals("5")) {
            body = "充值";
        }
        String ip = "47.95.5.235";
        int totalFee = 0;
        String url = backUrl;
        // if(StrKit.isBlank(url)){
        url = PropKit.get("weixin.notifyUrl");
        // }
        UnifiedOrderRequest request = new UnifiedOrderRequest(body, SDKUtils.genOutTradeNo(),
                totalFee, ip, url, tradeTyp, noncestr);
        request.setAttach(type + "-" + ordersId);
        try {
            UnifiedOrderResponse response = wxPayClient.execute(request);
            if (response != null && "SUCCESS".equals(response.getReturnCode()) && "SUCCESS".equals(response.getResultCode())) {

                try {
                    Record record = new Record();
                    String appid = PropKit.get("weixin.AppID");
                    String partnerid = PropKit.get("weixin.MCHID");
                    noncestr = SDKUtils.genRandomStringByLength(32);
                    String package1 = "Sign=WXPay";
                    String prepayid = response.getPrepayId();
                    long timeStamp = System.currentTimeMillis() / 1000;
                    record.set("appid", appid);
                    record.set("partnerid", partnerid);
                    record.set("timeStamp", timeStamp);
                    record.set("nonceStr", noncestr);
                    record.set("package", package1);
                    record.set("prepayid", prepayid);
                    record.set("codeurl", response.getCodeUrl());
                    RequestParametersHolder requestParametersHolder = new RequestParametersHolder();
                    ACHashMap acHashMap = new ACHashMap();
                    acHashMap.put("appid", appid);
                    acHashMap.put("partnerid", partnerid);
                    acHashMap.put("prepayid", prepayid);
                    acHashMap.put("package", package1);
                    acHashMap.put("noncestr", noncestr);
                    acHashMap.put("timestamp", timeStamp);
                    requestParametersHolder.setApplicationParams(acHashMap);
                    //String requestBoyStr = MapUtils.map2XmlString(acHashMap);
                    //System.out.println(requestBoyStr);
                    String signContent = WXPaySignUtils.getSignatureContent(requestParametersHolder);
                    String sign = WXPaySignUtils.md5Sign(signContent, PropKit.get("weixin.KEY"), "UTF-8").toUpperCase();
                    record.set("sign", sign);
                    log.debug(sign);
                    //System.out.println(sign);
                    r.setData(record);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.debug(e.getMessage());
                    r.setCode(1);
                    r.setMsg("请求支付失败");
                }
            }
        } catch (WXPayApiException e) {
            e.printStackTrace();
            log.debug(e.getMessage());
            throw new BusinessException("签名错误");
        }

        renderJson(r);
    }

    @ApiOperation(url = "/v1/pay/weixinnotify", tag = "pay", httpMethod = "post", description = "微信异步通知")

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })

    public void weixinnotify() throws IOException, DocumentException, WXPayApiException {
        System.out.print("微信支付回调获取数据开始");
        log.debug("微信支付回调获取数据开始");
        String notifyTxt = HttpKit.readData(getRequest());
        log.debug("微信支付回调获取数据" + notifyTxt);
        System.out.println("notifyTxtnotifyTxtnotifyTxtnotifyTxt" + notifyTxt);
        WXPayClient wxPayClient = new WXPayClient(PropKit.get("weixin.APPID"), PropKit.get("weixin.MCHID"), PropKit.get("weixin.KEY"));
        PayNotifyResponse response = wxPayClient.parseNotify(notifyTxt, PayNotifyResponse.class);
        String text;
        if ("SUCCESS".equals(response.getResultCode()) && "SUCCESS".equals(response.getReturnCode())) {
            text = "<xml><return_code><![CDATA[SUCCESS]]></return_code> <return_msg><![CDATA[OK]]></return_msg></xml>";
            int totalFee = response.getTotalFee();
            String attachs[] = response.getAttach().split("-");
            String type = attachs[0];// 1乐购订单 2app充值 3后台充值4游戏订单5游戏充值
            String ordersId = attachs[1];
            String transaction_id = response.getTransactionId();
            System.out.println(response.getAttach());
            int totalFee2 = 0;
            if (totalFee != totalFee2) {
                text = "<xml><return_code><![CDATA[ERROR]]></return_code> <return_msg><![CDATA[NO]]></return_msg></xml>";
            } else {
                //orderService.updateState(type, transaction_id, ordersId);
            }

        } else {
            text = "<xml><return_code><![CDATA[ERROR]]></return_code> <return_msg><![CDATA[NO]]></return_msg></xml>";
        }

        renderText(text);
    }


    @ApiOperation(url = "/v1/pay/refundNotifyUrl", tag = "pay", httpMethod = "post", description = "微信退款异步通知")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void refundNotifyUrl() throws Exception {
        String refundNotifyTxt = HttpKit.readData(getRequest());
        Map<String, String> map = WXPayUtil.xmlToMap(refundNotifyTxt);
        String req_info = map.get("req_info");
        log.debug("微信退款回调获取数据" + map);
        System.out.println("req_info" + req_info);
        //对结果做base64解密
        byte[] bytes = Encodes.decodeBase64(req_info);
        //对商户key做md5，得到32位小写key*
        SecretKeySpec key = new SecretKeySpec(WXPayUtil.MD5(PropKit.get("weixin.KEY")).toLowerCase().getBytes(), "AES");
        String decrypt = WXPayUtil.decryptData(bytes, key);
        Map<String, String> decMap = WXPayUtil.xmlToMap(decrypt);
        System.out.println("decMap" + decMap);
        String text;
        if ("SUCCESS".equals(map.get("return_code"))) {
            //orderService.insetAfter(decMap);
            log.info("微信退款回调完成");
            text = "<xml><return_code><![CDATA[SUCCESS]]></return_code> <return_msg><![CDATA[OK]]></return_msg></xml>";

        } else {
            text = "<xml><return_code><![CDATA[ERROR]]></return_code> <return_msg><![CDATA[NO]]></return_msg></xml>";
        }
        renderText(text);
    }


}

package com.sgss.www.shop.service;

import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.sgss.www.conmon.*;
import net.arccode.wechat.pay.api.protocol.refund.RefundResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShopService extends BaseService {

    public List<Record> getCarousel() {
        List<Record> carousels = Db.find(Db.getSqlPara("shop.getCarousel"));
        for (Record c : carousels
        ) {
            doImgPath(c);
        }
        return carousels;
    }

    public List<Record> getCoupons() {
        List<Record> coupons = Db.find(Db.getSqlPara("shop.getCoupons"));
        return coupons;
    }

    public List<Record> getBrands(int i) {

        List<Record> coupons = null;
        if (i == 1) {
            coupons = Db.find(Db.getSqlPara("shop.getBrands"));
        } else {
            coupons = Db.find(Db.getSqlPara("shop.getBrandsList"));
        }

        for (Record c : coupons
        ) {
            doImgPath(c);
        }
        return coupons;
    }

    public Page<Record> goodsList(int pageNumber) {
        Page<Record> page = Db.paginate(pageNumber, StaticPublic.PAGESIZE, Db.getSqlPara("shop.goodsList"));
        for (Record r : page.getList()
        ) {
            doImgPath(r);
        }
        return page;
    }

    public List<Record> allCategory() {
        List<Record> cs = Db.find(Db.getSqlPara("shop.allCategory"));
        for (Record r : cs
        ) {
            doImgPath(r);
        }
        return cs;
    }

    public List<Record> subCategory(String categoryId) {
        List<Record> cs = Db.find(Db.getSqlPara("shop.subCategory", categoryId));
        for (Record r : cs
        ) {
            doImgPath(r);
        }
        return cs;
    }

    public Page<Record> getCategoryGoods(String categoryId, int pageNumber, String orderby) {
        Kv cond = Kv.by("categoryId", categoryId);
        cond.set("orderby", orderby);
        Page<Record> page = Db.paginate(pageNumber, StaticPublic.PAGESIZE, Db.getSqlPara("shop.getCategoryGoods", cond));
        for (Record r : page.getList()
        ) {
            doImgPath(r);
        }
        return page;
    }

    public Record getGoodsByGooId(String goodsId) {
        Record r = Db.findFirst(Db.getSqlPara("shop.getGoodsByGooId", goodsId));
        doImgPath(r);
        List<String> imgss = new ArrayList<>();
        if (StrKit.notBlank(r.getStr("imgs"))) {
            String[] imgs = r.getStr("imgs").split("\\|");
            for (String i : imgs
            ) {
                if (StrKit.notBlank(i) ) {
                    if(i.indexOf("http") < 0) {
                        imgss.add(PropKit.get("fileServer") + i);
                    }
                    else{
                        imgss.add(i);
                    }
                }
            }
        }
        String descript = r.getStr("details");
        if(StrKit.notBlank(descript)) {
            descript=JsoupJavaUtils.replaceImgStyle(descript);
        }
        r.set("details", descript);

        r.set("images", imgss);
        List<Record> skus = Db.find(Db.getSqlPara("shop.getSkusByGooId", goodsId));
        List<String> spec1 = Lists.newArrayList();
        List<String> spec2 = Lists.newArrayList();
        for (Record s : skus) {
            if (!spec1.contains(s.getStr("spec1"))) {
                spec1.add(s.getStr("spec1"));
            }
            if (!spec2.contains(s.getStr("spec2"))) {
                spec2.add(s.getStr("spec2"));
            }
        }
        r.set("spec1s", spec1);
        r.set("spec2s", spec2);
        r.set("skus", skus);
        return r;
    }

    public Page<Record> getHomeGoods(int pageNumber) {

        Page<Record> page = Db.paginate(pageNumber, StaticPublic.PAGESIZE, Db.getSqlPara("shop.getHomeGoods"));
        for (Record r : page.getList()
        ) {
            doImgPath(r);
        }
        return page;
    }

    @Before(Tx.class)
    public void syncCart(String cartId, String userId, int number) {
        Db.update(Db.getSqlPara("shop.syncCart", userId, cartId, number));
    }

    @Before(Tx.class)
    public Record addCart(String skuId, String userId, int number, int type) {
        String cartId = "";
        Record sku = Db.findFirst(Db.getSqlPara("shop.getGoodsInfo", skuId));
        if (null != sku) {
            Record cart = Db.findFirst(Db.getSqlPara("shop.selectCartBySkuId", userId, skuId));
            if (cart != null) {
                cartId = cart.get("id");
                if (type == 1) {
                    Db.update(Db.getSqlPara("shop.updateCartByNow", userId, skuId));
                } else {
                    Db.update(Db.getSqlPara("shop.updateCart", userId, skuId, number));
                }
            } else {
                cartId = IdGen.uuid();
                sku.set("id", cartId);
                sku.set("number", number);
                sku.set("userId", userId);
                Db.update(Db.getSqlPara("shop.addCart", sku));
            }
        }
        return new Record().set("cartCount", this.cartCount(userId)).set("cartId", cartId);
    }

    public Integer cartCount(String userId) {
        return Db.findFirst(Db.getSqlPara("shop.cartCount", userId)).getInt("count");
    }

    public List<Record> cartList(String userId) {
        List<Record> list = Db.find(Db.getSqlPara("shop.cartList", userId));
        for (Record r : list
        ) {
            doImgPath(r);
        }
        return list;

    }

    @Before(Tx.class)
    public void removeCart(String cartId, String userId) {
        Db.update(Db.getSqlPara("shop.removeCart", userId, cartId));
    }

    public Record preorder(String userId, String[] cartIds, String addressId, String couponId) {
        Record r = new Record();
        SqlPara sqlPara;
        Record address;
        if (StrKit.notBlank(addressId)) {
            sqlPara = Db.getSqlPara("shop.getUserDefalutDeliveryAddressById", addressId);
            address = Db.findFirst(sqlPara);
        } else {
            sqlPara = Db.getSqlPara("shop.getUserDefalutDeliveryAddressByUserId", userId);
            address = Db.findFirst(sqlPara);
        }
        String provinceId = null;
        r.set("address", "");
        if (address != null) {
            r.set("address", address);
            provinceId = address.getStr("provinceId");
        }
        List<Record> carts = Lists.newArrayList();
        Record cart;
        double product = 0;
        int freight = 0;
        double coupon = 0;
        for (String str : cartIds
        ) {
            cart = Db.findFirst(Db.getSqlPara("shop.selectCartById", userId, str));
            if (cart == null) {
                throw new BusinessException("购物车已删除或者已下单");
            }
            doImgPath(cart);
            carts.add(cart);
            int num = cart.getInt("number");
            String goodsId = cart.getStr("goodsId");
            product += cart.getDouble("product");
            freight += this.countShopFreight(goodsId, num, provinceId);
        }
        if (StrKit.notBlank(couponId)) {
            Record cp = Db.findFirst(Db.getSqlPara("shop.getMyCouponsById", userId, couponId, product));
            if (null != cp) {
                coupon = cp.getDouble("reduction");
            }
        }


        DecimalFormat df = new DecimalFormat("#0.00");
        r.set("carts", carts);
        r.set("coupon", coupon);
        r.set("coupons", Db.findFirst(Db.getSqlPara("shop.getMyCouponsCount", userId)).getInt("coupons"));
        r.set("freight", freight);
        r.set("product", df.format(product));
        r.set("total", df.format(product + freight - coupon));

        return r;
    }

    private int countShopFreight(String goodsId, int number, String provinceId) {

        //子运费
        Record aeraFreight = Db.findFirst(Db.getSqlPara("shop.selectAreaFreightByFreightId", provinceId));
        if (aeraFreight != null) {
            return doNumberPrice(number, aeraFreight);
        } else {
            //主运费
            aeraFreight = Db.findFirst(Db.getSqlPara("shop.selectFreightById"));
            if (aeraFreight == null) {
                return 0;
            }
            if ("0".equals(aeraFreight.getStr("isExcept"))) {
                return doNumberPrice(number, aeraFreight);
            } else {

                if (aeraFreight.getStr("provinceId").contains(provinceId)) {
                    return 0;
                } else {
                    return doNumberPrice(number, aeraFreight);

                }
            }

        }

    }

    private int doNumberPrice(int num, Record aeraFreight) {
        int piece = aeraFreight.getInt("piece");
        int money = aeraFreight.getInt("money");
        int addPiece = aeraFreight.getInt("addPiece");
        int addMoney = aeraFreight.getInt("addMoney");
        int i = 0;
        if (addPiece == 0) {
            return 0;
        }
        if (num <= piece) {
            i = money;
        } else {
            if (num - piece > 0) {
                double nu = (double) (num - piece) / (double) addPiece;
                if (nu > 1) {
                    if ((double) (num - piece) % (double) addPiece != 0) {
                        nu += 1;
                    }
                } else {
                    nu = 1;
                }
                i = money + ((int) nu * addMoney);
            }
            /*else{
                i = money + (((num - piece) / addPiece) * addMoney);
            }*/
        }

        return i;
    }

    @Before(Tx.class)
    public Record saveOrder(String userId, String[] cartIds, String addressId, String couponId) {
        Record r = new Record();
        SqlPara sqlPara;
        Record address = null;
        if (StrKit.notBlank(addressId)) {
            sqlPara = Db.getSqlPara("shop.getUserDefalutDeliveryAddressById", addressId);
            address = Db.findFirst(sqlPara);
        }
        String provinceId = null;

        if (address != null) {
            r.set("address", address);
            provinceId = address.getStr("provinceId");
        }
        List<Record> carts = Lists.newArrayList();
        Record cart;
        double product = 0;
        int freight = 0;
        double coupon = 0;
        for (String str : cartIds
        ) {
            cart = Db.findFirst(Db.getSqlPara("shop.selectCartById", userId, str));
            if (cart == null) {
                throw new BusinessException("购物车已删除或者已下单");
            }
            doImgPath(cart);
            carts.add(cart);
            int num = cart.getInt("number");
            String goodsId = cart.getStr("goodsId");
            product += cart.getDouble("product");
            freight += this.countShopFreight(goodsId, num, provinceId);
        }
        if (StrKit.notBlank(couponId)) {
            Record cp = Db.findFirst(Db.getSqlPara("shop.getMyCouponsById", userId, couponId, product));
            if (null != cp) {
                coupon = cp.getDouble("reduction");
                Db.update(Db.getSqlPara("shop.updateCouponsById", userId, couponId));
            }

        }
        DecimalFormat df = new DecimalFormat("#0.00");
        String total = df.format(product + freight - coupon);

        deleteCart(userId, cartIds);
        Kv cond = Kv.by("user_id", userId);
        String orderId = IdGen.uuid();
        String orderNumber = IdGen.getOrderIdByUUId();
        for (Record cart1 : carts
        ) {
            Kv cond2 = Kv.by("id", IdGen.uuid());
            cond2.set("name", cart1.get("name"));
            cond2.set("orderNumber", orderNumber);
            cond2.set("goods_id", cart1.get("goodsId"));
            cond2.set("sku_id", cart1.get("skuId"));
            cond2.set("logo", cart1.get("logo"));
            cond2.set("price", cart1.get("price"));
            cond2.set("artNo", cart1.get("artNo"));
            cond2.set("spec1", cart1.get("spec1"));
            cond2.set("spec2", cart1.get("spec2"));
            cond2.set("number", cart1.get("number"));
            Db.update(Db.getSqlPara("shop.saveOrderDetail", cond2));
        }

        cond.set("id", orderId);
        cond.set("orderNumber", orderNumber);
        cond.set("phone", address.getStr("phone"));
        cond.set("addressId", address.getStr("addressId"));
        cond.set("address", address.getStr("address"));
        cond.set("consignee", address.getStr("consignee"));

        cond.set("province_id", address.getStr("provinceId"));
        cond.set("city_id", address.getStr("cityId"));
        cond.set("area_id", address.getStr("areaId"));
        cond.set("goods_price", product);
        cond.set("freight", freight);
        cond.set("favorable_price", coupon);
        cond.set("total_price", total);
        cond.set("pay_type", "1");
        cond.set("state", "10");
        cond.set("couponId", couponId);
        Db.update(Db.getSqlPara("shop.saveOrder", cond));

        Record ret = new Record();
        ret.set("ordersId", orderNumber);
        ret.set("coupon", coupon);
        ret.set("freight", freight);
        ret.set("product", df.format(product));
        ret.set("total", df.format(product + freight - coupon));
        return ret;
    }

    private void deleteCart(String userId, String[] cartIds) {
        for (String str : cartIds) {
            Db.update(Db.getSqlPara("shop.deleteCartByid", userId, str));
        }
    }

    public Page<Record> orderList(String userId, String type, int pageNumber, String name) {
        SqlPara sqlPara = null;
        Page<Record> records = null;
        Kv cond = Kv.by("del", '0');
        cond.set("userId", userId);
        cond.set("type", type);
        cond.set("name", name);

        sqlPara = Db.getSqlPara("shop.userOrderList", cond);
        records = Db.paginate(pageNumber, StaticPublic.PAGESIZE, sqlPara);
        List<Record> list = records.getList();
        List<Record> subList = Lists.newArrayList();
        for (Record r : list) {
            subList = Db.find(Db.getSqlPara("shop.userOrderDetailList", r.getStr("orderNumber"), userId));
            for (Record s : subList) {
                doImgPath(s);
            }
            r.set("subList", subList);
        }
        return records;
    }

    public int getTotalFeeByOrderId(String type, String ordersId) {
        int price = Db.findFirst(Db.getSqlPara("shop.getTotalPriceById", ordersId)).getInt("price");
        return price;
    }

    @Before(Tx.class)
    public void orderOk(String userId, String ordersId) {
        Db.update(Db.getSqlPara("shop.orderOk", userId, ordersId));
    }

    @Before(Tx.class)
    public void orderDelete(String userId, String ordersId) {
        Db.update(Db.getSqlPara("shop.orderDelete", userId, ordersId));
    }


    public String logistics(String courier, String courierNumber) {
        String key = RedisTool.hget("expresss:" + courier, "code");
        String dataKey = key + "-" + courierNumber;
        String str = (String) RedisTool.getObject(dataKey);
        if (StrKit.notBlank(str)) {
            return str;
        }
        str = Kuaidi.getKuaiDiInfo(key, courierNumber);
        RedisTool.setexObject(dataKey, StaticPublic.SHOPEXPRESS, str);
        return str;
    }

    @Before(Tx.class)
    public void saveAfterOrder(String orderNumber, String type, String content, String userId) {
        Record r = Db.findFirst(Db.getSqlPara("shop.selectAterOrderByOrderNumber", orderNumber, userId));
        if (null != r) {
            throw new BusinessException("此订单已经存在售后");
        }
        try {
            Db.update(Db.getSqlPara("shop.saveAfterOrder", IdGen.uuid(), orderNumber, type, content, userId));
        } catch (Exception e) {
            throw new BusinessException("申请售后异常,请联系客服");
        }
    }

    public Record orderDetail(String orderNumber, String userId) {
        Record r = Db.findFirst(Db.getSqlPara("shop.userOrderDetailByOrderNumber", userId, orderNumber));
        if (null == r) {
            throw new BusinessException("订单不存在");
        }
        List<Record> subList = Db.find(Db.getSqlPara("shop.userOrderDetailList", r.getStr("orderNumber"), userId));
        for (Record s : subList) {
            doImgPath(s);
        }
        r.set("subList", subList);

        return r;
    }

    @Before(Tx.class)
    public void cancelOrder(String orderNumber, String userId) {
        Db.update(Db.getSqlPara("shop.cancelOrder", userId, orderNumber));
    }

    public Page<Record> afterOrderList(String userId, int pageNumber) {
        SqlPara sqlPara = null;
        Page<Record> records = null;
        Kv cond = Kv.by("del", '0');
        cond.set("userId", userId);
        sqlPara = Db.getSqlPara("shop.afterOrderList", cond);
        records = Db.paginate(pageNumber, StaticPublic.PAGESIZE, sqlPara);
        List<Record> list = records.getList();
        List<Record> subList = Lists.newArrayList();
        for (Record r : list) {
            if (r.getStr("state").equals("10")) {
                if (r.getStr("type").equals("2")) {
                    r.set("state", "12");
                }
            }
            subList = Db.find(Db.getSqlPara("shop.userOrderDetailList", r.getStr("orderNumber"), userId));
            for (Record s : subList) {
                doImgPath(s);
            }
            r.set("subList", subList);
        }
        return records;
    }

    @Before(Tx.class)
    public void cancelAfterOrder(String afterOrderId, String userId) {
        Db.update(Db.getSqlPara("shop.cancelAfterOrder", userId, afterOrderId));
    }

    public Record afterOrderDetail(String userId, String afterOrderId) {
        Record r = Db.findFirst(Db.getSqlPara("shop.afterOrderDetail", userId, afterOrderId));

        return r;
    }

    public List<Record> getExpress() {
        List<Record> shopexpress = Db.find(Db.getSqlPara("shop.expresss"));
        return shopexpress;
    }

    @Before(Tx.class)
    public void returnExpress(String userId, String afterOrderId, String returnInvoiceNo, String returnExpressName) {
        Db.update(Db.getSqlPara("shop.returnExpress", userId, afterOrderId, returnExpressName, returnInvoiceNo));

    }

    public List<Record> hotData() {
        return Db.find(Db.getSqlPara("shop.hotData"));
    }

    public Page<Record> getSearchGoods(String brandId, String content, int pageNumber, String orderby) {
        Kv cond = Kv.by("brandId", brandId);
        cond.set("content", content);
        cond.set("orderby", orderby);
        Page<Record> page = Db.paginate(pageNumber, StaticPublic.PAGESIZE, Db.getSqlPara("shop.getSearchGoods", cond));
        for (Record r : page.getList()
        ) {
            doImgPath(r);
        }
        return page;
    }

    @Before(Tx.class)
    public void updateState(String type, String transaction_id, String ordersId, String outTradeNo) {
        Db.update(Db.getSqlPara("shop.updateOrderPayInfoTransactionid", ordersId, transaction_id,outTradeNo));
    }

    public Record getOrderInfoById(String orderNumber) {
        return Db.findFirst(Db.getSqlPara("shop.getOrderInfoById", orderNumber));
    }
    @Before(Tx.class)
    public void weixinreturn(String orderNumber, RefundResponse response) {
        Db.update(Db.getSqlPara("shop.weixinreturn",orderNumber, response.getTotalFee(), response.getOutRefundNo(),response.getRefundId()));

    }
}

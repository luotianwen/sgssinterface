#sql("getCarousel")
select
logo,
goods_id as "goodsId"
from
s_carousel c
where c.del_flag=0
order by c.sort asc
#end

#sql("getCoupons")
select
id as couponId,
name,
full ,
reduction
from
s_coupon c
where c.del_flag=0
and c.state=1
and date(now()) between  c.begin_date and c.end_date
#end

#sql("getBrands")
select
b.id as "brandId",
name,
logo
from s_brand b
where b.del_flag=0
and b.state=1
order by b.sort asc
limit 12
#end

#sql("getBrandsList")
select
b.id as "brandId",
name,
logo
from s_brand b
where b.del_flag=0
and b.state=1
order by b.sort asc
#end

#sql("goodsList")
select
b.id as "goodsId",
name,
logo,
price,
sales
from s_goods b
where b.del_flag=0
and b.state=1
order by b.create_date desc

#end

#sql("updateGoods")
update s_goods set imgs=#para(1) where id=#para(0)
#end

#sql("goodsPicList")
select
logo
from s_goods_pic b
where goods_id=#para(0)

#end


#sql("allCategory")
select
b.id as "categoryId",
name,
logo
from s_category b
where b.del_flag=0
and parent_id=0
order by b.sort asc

#end

#sql("subCategory")
select
b.id as "categoryId",
name,
logo
from s_category b
where b.del_flag=0
and parent_id=#para(0)
order by b.sort asc

#end


#sql("getSearchGoods")
SELECT
g.logo,
g.id as goodId,
g.`name`,
g.price,
g.sales
from s_goods g
where


  g.del_flag=0
and g.state=1

#if(sk.notBlank(brandId))
and g.brand_id=#para(brandId)
 #end
#if(sk.notBlank(content))
and g.name like concat('%',#para(content),'%')
 #end

  #if(orderby.equals('1'))
   ORDER BY g.sales desc


   #elseif(orderby.equals('2'))
   ORDER BY g.create_date desc

  #elseif(orderby.equals('3'))
   ORDER BY g.price asc

  #elseif(orderby.equals('4'))
   ORDER BY g.price desc
  #end



#end



#sql("getCategoryGoods")
SELECT
g.logo,
g.id as goodId,
g.`name`,
g.price,
g.sales
from s_goods g ,s_goods_category gc
where g.id=gc.goods_id
and gc.category_id=#para(categoryId)
and g.del_flag=0
and g.state=1

  #if(orderby.equals('1'))
   ORDER BY g.sales desc


   #elseif(orderby.equals('2'))
   ORDER BY g.create_date desc

  #elseif(orderby.equals('3'))
   ORDER BY g.price asc

  #elseif(orderby.equals('4'))
   ORDER BY g.price desc
  #end

#end

#sql("getGoodsByGooId")
SELECT
g.logo,
g.id as goodId,
g.`name`,
g.price,
g.sales,
g.spec1,
g.spec2,
gc.details,
g.imgs
from s_goods g ,s_goods_detail gc
where g.id=gc.goods_id
and g.id=#para(0)
and g.del_flag=0
and g.state=1
#end

#sql("getSkusByGooId")
SELECT
g.id as skuId,
g.spec1,
g.spec2,
g.price,
g.stock
from s_goods_sku g
where  g.goods_id =#para(0)

#end

#sql("getHomeGoods")
SELECT
g.logo,
g.id as goodId,
g.`name`,
g.price,
g.sales
from s_goods g
where
  g.del_flag=0
and g.state=1
 ORDER BY g.sales desc

#end

#sql("getGoodsInfo")
SELECT
g.logo,
g.id as goodsId,
g.`name`,
s.price,
s.id as "skuId",
s.spec1,
s.spec2
from s_goods g,
s_goods_sku s
where
s.goods_id=g.id
and s.id=#para(0)
and g.del_flag=0
and g.state=1

#end

#sql("selectCartBySkuId")
select id from
s_cart
where user_id=#para(0) and sku_id=#para(1)
#end

#sql("cartList")
select
 c.id as "cartId",
 d.spec1,
 d.spec2,
 d.price,
 c.number,
 g.name,
 g.logo,
 d.stock,
 g.id as "goodsId"
 from
s_cart c,
s_goods g,
s_goods_sku d
where c.user_id=#para(0)
and c.goods_id=g.id
and c.sku_id =d.id
and d.goods_id=g.id
and g.state=1
order by c.create_date desc
#end


#sql("addCart")
  INSERT INTO s_cart (id,create_date,goods_id, sku_id , number, user_id)
  VALUES (#para(id), now(),#para(goodsId), #para(skuId),  #para(number), #para(userId) );
#end

#sql("removeCart")
delete  from s_cart   where  user_id=#para(0)  and  id=#para(1)
#end
#sql("updateCart")
update
s_cart c
set c.number=c.number+#para(2),c.create_date=now()
where c.user_id=#para(0) and sku_id=#para(1)
#end
#sql("syncCart")
update
s_cart c
set c.number=#para(2),c.create_date=now()
where c.user_id=#para(0) and id=#para(1)
#end
#sql("updateCartByNow")
update
s_cart c
set c.number=1 ,c.create_date=now()
where c.user_id=#para(0) and sku_id=#para(1)
#end


#sql("cartCount")
select
count(c.id) as count
 from s_cart c
where c.user_id=#para(0)
#end


#sql("getUserDefalutDeliveryAddressByUserId")

  SELECT
    a.isDefault as isDefault,
    a.id AS addressId,
    a.user_id AS uid,
    a.consignee as consignee,
    a.phone as phone,
    a.address as address,
    a.province_id as provinceId,
    a.city_id as cityId,
    a.area_id as areaId
  from s_user_delivery_address a
   where a.user_id=#para(0)
   and  a.isDefault =1

 #end

#sql("getUserDefalutDeliveryAddressById")

  SELECT
    a.isDefault as isDefault,
    a.id AS addressId,
    a.user_id AS uid,
    a.consignee as consignee,
    a.phone as phone,
    a.address as address,
    a.province_id as provinceId,
    a.city_id as cityId,
    a.area_id as areaId,
    p.name as provinceName,
    c.name as cityName,
    ar.name as areaName
  from s_user_delivery_address a
   left join sys_area p on a.province_id=p.id
  left join sys_area c on a.city_id=c.id
  left join sys_area ar on a.area_id =ar.id
   where a.id=#para(0)


 #end

#sql("selectCartById")

select
 c.id as "cartId",
 d.spec1,
 d.spec2,
 d.price,
 c.number,
 g.name,
 g.logo,
 d.stock,
 g.artNo,
 g.id as "goodsId",
 d.id as "skuId",
 (d.price*c.number) as "product"
 from
s_cart c,
s_goods g,
s_goods_sku d
where c.user_id=#para(0)
and c.goods_id=g.id
and c.sku_id =d.id
and d.goods_id=g.id
and g.state=1
and c.id=#para(1)
#end
#sql("saveOrderDetail")
  INSERT INTO s_order_detail
  (id,create_date,del_flag,goods_id, sku_id,logo ,price,name,artNo,spec1,spec2,number,orderNumber  )
  VALUES (#para(id), now(),0,#para(goods_id),
  #para(sku_id),#para(logo),#para(price),#para(name),
  #para(artNo),#para(spec1),#para(spec2),#para(number), #para(orderNumber));
#end

#sql("saveOrder")
  INSERT INTO s_order
  (id,create_date,del_flag, orderNumber,phone,consignee,address,user_id,order_time,
  state,goods_price,freight,favorable_price,total_price,pay_type,coupon_id,province_id,city_id
  ,area_id
  )
  VALUES (#para(id), now(),0, #para(orderNumber), #para(phone), #para(consignee)
  , #para(address), #para(user_id), now(), #para(state)
  , #para(goods_price), #para(freight), #para(favorable_price), #para(total_price)
  , #para(pay_type), #para(couponId), #para(province_id), #para(city_id)
  , #para(area_id));
#end
#sql("getTotalPriceById")
select total_price*100 as  price from s_order
where orderNumber=#para(0)
#end
#sql("getOrderInfoById")
select total_price*100 as  price ,outTradeNo  from s_order
where orderNumber=#para(0)
#end
#sql("updateOrderPayInfoTransactionid")
update s_order
set pay_time=now() ,
transactionid=#para(1),
outTradeNo=#para(2),
state=20
where orderNumber=#para(0)
and state=10
#end

 #sql("getMyCouponsCount")
SELECT
  count(p.coupon_id)as coupons
from s_user_coupon p
where  p.user_id = #para(0)
  and p.state = 1
  and now() between p.begin_date and p.end_date
  #end

 #sql("getMyCouponsById")
SELECT
  p.coupon_id as couponId,
  p.full,
  p.reduction
from s_user_coupon p
where p.coupon_id = #para(1)
  and p.user_id = #para(0)
  and p.state = 1
  and p.full<=#para(2)
  and now() between p.begin_date and p.end_date
  #end


#sql("selectFreightById")
select
isExcept,
province_id as "provinceId",
piece ,
money,
add_piece as "addPiece",
add_money as "addMoney"
from
s_system_freight f

#end


#sql("selectAreaFreightByFreightId")
select
piece ,
money,
add_piece as "addPiece",
add_money as "addMoney"
from
s_system_area_freight f
where
f.province_id like concat('%',#para(0),'%')

#end


 #sql("updateCouponsById")
update s_user_coupon p
set p.state=2
where p.user_id=#para(0)
  and p.coupon_id=#para(1)
  #end

#sql("deleteCartByid")
delete from s_cart
where  user_id=#para(0)
and id=#para(1)
#end

#sql("hotData")
select
name
from s_search_hot order by  num asc
#end

#sql("userOrderList")
SELECT
o.orderNumber,
o.total_price as totalPrice,
o.limitTime,
express_name as expressName ,
invoice_no as invoiceNo,
now() AS currentTime,
o.state,
s.id as "salesId"
from  s_order o
left join s_order_after_sales s on s.orderNumber=o.orderNumber
where o.del_flag=0
 #if(sk.notBlank(userId))
and o.user_id=#para(userId)
 #end
 #if(sk.notBlank(type))
   #if(type.equals('10'))
     and o.state=#para(type)
    #elseif(type.equals('20'))
     and (o.state=20 or o.state=30 )
   #elseif(type.equals('50'))
     and o.state=#para(type)
    #elseif(type.equals('40'))
     and (o.state=40 or o.state=60 )
   #end
  #end

order by o.create_date desc
#end



#sql("userOrderDetailList")
SELECT
o.orderNumber,
o.artNo,
o.spec1,
o.spec2,
o.sku_id as skuId,
o.goods_id as goodsId,
now() AS currentTime,
o.number,
o.name,
o.price,
o.logo as logo
from  s_order_detail o
where o.del_flag=0
  and o.orderNumber=#para(0)
order by o.create_date desc
#end

#sql("orderOk")
update s_order
set state=50
where
user_id=#para(0)
and
orderNumber=#para(1)
and
state=30
#end

#sql("orderDelete")
update s_order
set del_flag=1,
update_date=now()
where
user_id=#para(0)
and
orderNumber=#para(1)

#end

#sql("cancelOrder")
update s_order
set state=40,
update_date=now()
where
user_id=#para(0)
and
orderNumber=#para(1)

#end

#sql("expresss")
 select
 code,
 name
 from s_express
 where state=1
#end
#sql("selectAterOrderByOrderNumber")
select
id
from s_order_after_sales s
where s.orderNumber=#para(0)
and s.user_id=#para(1)
and  s.del_flag=0
#end

#sql("saveAfterOrder")
  INSERT INTO s_order_after_sales
  (id,create_date,del_flag, orderNumber,apply_time,type,content,user_id,state
  )
  VALUES (#para(0), now(),0, #para(1), now(), #para(2)
  , #para(3), #para(4) ,'10');
#end




#sql("userOrderDetailByOrderNumber")
SELECT
o.orderNumber,
o.total_price as totalPrice,
o.goods_price as goodsPrice,
o.freight as freight,
o.favorable_price as favorablePrice,
o.limitTime,
express_name as expressName ,
invoice_no as invoiceNo,
order_time AS orderTime,
pay_type as "payType",
o.state,
consignee,
address,
phone,
(select name from sys_area where id=o.province_id) as provinceName,
(select name from sys_area where id=o.city_id) as cityName,
(select name from sys_area where id=o.area_id) as areaName

from  s_order o
where o.del_flag=0
and o.user_id=#para(0)
and o.orderNumber=#para(1)
#end



#sql("afterOrderList")
SELECT
o.id as "afterOrderId",
o.orderNumber,
o.state,
o.type
from  s_order_after_sales o
where o.del_flag=0
and o.user_id=#para(userId)
order by o.create_date desc
#end


#sql("cancelAfterOrder")
 delete from  s_order_after_sales
where   id=#para(1)
and  user_id=#para(0)
#end

#sql("afterOrderDetail")
select
s.id as afterOrderId,
s.orderNumber,
s.apply_time as applyTime,
s.type,
s.content,
s.exchange_phone as exchangePhone,
s.exchange_consignee as exchangeConsignee,
s.exchange_address as exchangeAddress,
s.exchange_invoice_no as exchangeInvoiceNo,
s.exchange_express_name as exchangeExpressName,
s.exchange_freight as exchangeFreight,
s.exchange_time as exchangeTime,
s.return_invoice_no as returnInvoiceNo,
s.return_express_name as returnExpressName,
s.return_phone as returnPhone,
s.return_consignee as returnConsignee,
s.return_address as returnAddress,
s.return_time as returnTime,
s.state,
s.refuse_content as refuseContent,
s.update_date as updateDate,
return_amount as returnAmount
from
s_order_after_sales s
where s.user_id=#para(0)
and s.id=#para(1)
#end

#sql("returnExpress")
update s_order_after_sales
set return_time=now(),
update_date=now(),
return_invoice_no=#para(3),
return_express_name=#para(2)
where user_id=#para(0)
and id=#para(1)
#end


#sql("weixinreturn")
update s_order_after_sales
set return_amount=#para(1),
refund_time=now(),
refund_state=1,
refund_id=#para(3),
out_refund_no=#para(2)
where orderNumber=#para(0)
#end

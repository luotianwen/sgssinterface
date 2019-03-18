#sql("getUserByopenId")
  select
  id as "id",
  name as "name",
  head_img as "headImg",
  integral as "integral",
  nickname as "nickname",
  phone as "phone",
  openId
  from
  s_user s
  where
  s.openId=#para(0)
  and s.state=1
  and s.del_flag=0
#end

#sql("bindPhone")
update s_user
set phone=#para(1),
update_date=now()
where id=#para(0)
#end

#sql("findByMobile")
 select
  id as "id",
  name as "name",
  head_img as "headImg",
  integral as "integral",
  nickname as "nickname",
  phone as "phone",
  openId
  from
  s_user s
  where
  s.phone=#para(0)

#end
#sql("saveUser")
 INSERT INTO s_user (id,create_date,del_flag,openId, name,head_img,integral,nickname,state,agentId)
     VALUES (#para(0), now(),0,#para(1),#para(2),#para(3),0,#para(2),1,#para(4));
#end


#sql("addresslist")

select
id as "addressId",
isDefault ,
consignee,
phone,
address,
(select name from sys_area where id=province_id) as provinceName,
(select name from sys_area where id=city_id) as cityName,
(select name from sys_area where id=area_id) as areaName,
area_id as "areaId"
from
s_user_delivery_address
where user_id=#para(0)
order by isDefault desc ,create_date desc
#end




#sql("deleteAddress")
 delete from  s_user_delivery_address
where   id=#para(1)
and  user_id=#para(0)
#end

#sql("updateAddressIsDefault")
update s_user_delivery_address
set isDefault=0
where    user_id=#para(0)
#end
#sql("updateAddress")
update s_user_delivery_address
set isDefault=#para(isDefault),
consignee=#para(consignee),
phone=#para(phone),
address=#para(address),
province_id=#para(provinceId),
city_id=#para(cityId),
area_id=#para(areaId)
where   id=#para(addressId)
and  user_id=#para(userId)
#end


#sql("saveAddress")
  INSERT INTO s_user_delivery_address
  (id,create_date,isDefault, phone,consignee,address,user_id, province_id,city_id ,area_id
  )
  VALUES (#para(addressId), now(), #para(isDefault), #para(phone), #para(consignee)
  , #para(address), #para(userId),  #para(provinceId), #para(cityId) , #para(areaId));
#end

#sql("getCouponList")
SELECT
  u.coupon_id as couponId,
	u.begin_date AS beginDate,
	u.end_date AS endDate,
	u.state,
	u.`full`,
	u.reduction,
	c.`name`
FROM
	s_user_coupon u, s_coupon c
	 where c.id = u.coupon_id
 and u.user_id=#para(0)
	and u.state=#para(1)
#end

#sql("getCouponById")
SELECT
  c.id as couponId,
	c.begin_date AS beginDate,
	c.end_date AS endDate,
	c.`full`,
	c.reduction
FROM
	  s_coupon c
  where  c.id=#para(0)
	and c.state=1
	and c.num>0
	and date(now())between c.begin_date and c.end_date

#end

#sql("updateCouponNum")
update s_coupon set num=num-1
where id=#para(0)
#end

#sql("gainCouponById")
INSERT INTO s_user_coupon (user_id,coupon_id,begin_date,end_date,state,full,reduction)
VALUE (#para(userId),#para(couponId),#para(beginDate),#para(endDate),1,#para(full),#para(reduction))
#end

#sql("removeCoupon")
delete from s_user_coupon
where  user_id=#para(0)
and coupon_id=#para(1)
#end

#sql("feedback")
INSERT INTO s_feedback (id,user_id,content,create_date,del_flag,state) VALUE (#para(0),#para(1),#para(2),now(),0,0)
#end
#sql("feedbackImgs")
INSERT INTO s_feedback_img (id,feedback_id,img_src,create_date,del_flag) VALUE (#para(0),#para(1),#para(2),now(),0)
#end

#sql("getUserData")
 select
  (select count(c.coupon_id) from s_user_coupon c where c.user_id=#para(0) and c.state=1 )  as coupons,
  (select count(c.id) from s_order c where c.user_id=#para(0) and c.state=10 )  as orders,
  (select count(c.id) from s_order_after_sales c where c.user_id=#para(0) and c.state=10 )  as afterOrders
 from dual
#end

#sql("getUserAgentData")
select
state as vshow
from s_agent
where userid=#para(0)
#end

#sql("saveUserAgentData")
INSERT INTO s_agent (id,userid,mobile,create_date,del_flag,state,name) VALUE (#para(0),#para(1),#para(2),now(),0,2,#para(3))
#end

#sql("getUserAgent")
select g.id from s_agent g,s_user u where u.id=#para(0) and g.userid=u.agentId
#end

#sql("getAgent")
select id from s_agent where userid=#para(0)
#end

#sql("getDiscount")
select d.discount as discount  from s_agent s,s_discount d where s.userid=#para(0)
and d.id=s.discountId
#end
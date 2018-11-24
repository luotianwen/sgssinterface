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
limit 8
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

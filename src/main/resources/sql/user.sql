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

#sql("saveUser")
 INSERT INTO s_user (id,create_date,del_flag,openId, name,head_img,integral,nickname,state)
     VALUES (#para(0), now(),0,#para(1),#para(2),#para(3),0,#para(2),1);
#end
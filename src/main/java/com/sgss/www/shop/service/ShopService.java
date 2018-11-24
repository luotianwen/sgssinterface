package com.sgss.www.shop.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.sgss.www.conmon.BaseService;
import com.sgss.www.conmon.StaticPublic;

import java.util.List;

public class ShopService extends BaseService {

    public List<Record> getCarousel(){
        List<Record> carousels= Db.find(Db.getSqlPara("shop.getCarousel"));
        for (Record c:carousels
             ) {
            doImgPath(c);
        }
        return carousels;
    }

    public  List<Record>  getCoupons() {
        List<Record> coupons= Db.find(Db.getSqlPara("shop.getCoupons"));
        return coupons;
    }

    public  List<Record>  getBrands() {
        List<Record> coupons= Db.find(Db.getSqlPara("shop.getBrands"));
        for (Record c:coupons
        ) {
            doImgPath(c);
        }
        return coupons;
    }

    public Page<Record> goodsList(int pageNumber) {
        Page<Record> page=Db.paginate(pageNumber, StaticPublic.PAGESIZE,Db.getSqlPara("shop.goodsList"));
        for (Record r:page.getList()
             ) {
            doImgPath(r);
        }
        return page;
    }
}

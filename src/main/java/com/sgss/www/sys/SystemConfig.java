package com.sgss.www.sys;

import com.jfinal.config.*;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.sgss.www.conmon.AuthInterceptor;
import com.sgss.www.conmon.FileHandler;
import com.sgss.www.conmon.MyRenderFactory;
import com.sgss.www.conmon.RedisTool;
import com.sgss.www.routes.V1Routes;
import com.sgss.www.swagger.config.routes.SwaggerRoutes;

import java.io.File;
import java.util.List;

/**
 * @author martins
 */
public class SystemConfig  extends JFinalConfig {
    @Override
    public void afterJFinalStart() {

        //快递公司
        RedisTool.del("expresss");
        List<Record> shopexpress= Db.find(Db.getSqlPara("shop.expresss"));
        for (Record r:shopexpress){
            RedisTool.hset("expresss:"+r.get("name"),"code",r.get("code"));
            RedisTool.sadd("expresss",r);
        }
       /* List<Record> goods=Db.find(Db.getSqlPara("shop.goodsList"));
        List<Record> pics=null;
        String p="";
        for(Record r:goods){
            p="";
            pics=Db.find(Db.getSqlPara("shop.goodsPicList",r.getStr("goodsId")));
            if(null!=pics&&pics.size()>0){
                for (Record pic:pics){
                    p="|"+pic.getStr("logo");
                }
            }
            if(StrKit.notBlank(p)){
                Db.update(Db.getSqlPara("shop.updateGoods",r.getStr("goodsId"),p));
            }
        }*/

        super.afterJFinalStart();
    }

    @Override
    public void configConstant(Constants me) {
        PropKit.use("a_little_config.txt");
        me.setDevMode(PropKit.getBoolean("devMode", false));
        me.setBaseUploadPath(PropKit.get("userfiles.basedir"));
        me.setMaxPostSize(104857600);
        me.setBaseDownloadPath(PropKit.get("userfiles.basedir"));
        me.setRenderFactory(new MyRenderFactory());
        me.setInjectDependency(true);
    }
    @Override
    public void configRoute(Routes me) {

        me.add(new V1Routes());
        me.add(new SwaggerRoutes());

    }
    @Override
    public void configEngine(Engine me) {
        //me.addSharedMethod(new StrKit());
        //me.addSharedObject("sk",new com.jfinal.kit.StrKit());
    }
    @Override
    public void configPlugin(Plugins me) {
        // 配置 druid 数据库连接池插件
        DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("jdbc.url"), PropKit.get("jdbc.username"), PropKit.get("jdbc.password").trim());
        me.add(druidPlugin);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        arp.setBaseSqlTemplatePath(PathKit.getRootClassPath()+ File.separator+"sql");
        arp.setDevMode(true);
        arp.setShowSql(true);
        arp.addSqlTemplate("all.sql");
        arp.getEngine().addSharedObject("sk",new com.jfinal.kit.StrKit());
        me.add(arp);

    }
    @Override
    public void configInterceptor(Interceptors me) {
        me.add(new AuthInterceptor());
    }
    @Override
    public void configHandler(Handlers me) {
        me.add(new FileHandler());
    }

}
//package com.scaffold.orm.extension;
//
//import com.github.pagehelper.page.PageAutoDialect;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
///**
// * 在spring boot启动完成后将LocalMySqlDialect注册进pagehelper
// *
// * @Author xiuvee
// * @Date 2024/3/4 11:10
// **/
//@Component
//public class DialectInit implements ApplicationRunner {
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        //因为数据库换成了mariadb，因此这里也修改
//        PageAutoDialect.registerDialectAlias("mariadb", LocalMySqlDialect.class);
//    }
//}

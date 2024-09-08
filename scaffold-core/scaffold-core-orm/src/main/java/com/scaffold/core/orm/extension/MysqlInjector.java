//package com.scaffold.orm.extension;
//
//import com.baomidou.mybatisplus.core.injector.AbstractMethod;
//import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
//import com.baomidou.mybatisplus.core.metadata.TableInfo;
//import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
//
//import java.util.List;
//
//public class MysqlInjector extends DefaultSqlInjector {
//
//    @Override
//    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
//        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
//        methodList.add(new InsertBatchSomeColumn());
//        methodList.add(new InsertOrUpdateBatch(f-> "id".equals(f.getField().toString())));
//        return methodList;
//    }
//}

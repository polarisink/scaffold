package com.scaffold.orm;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import org.apache.ibatis.session.Configuration;

import java.util.List;

import static com.scaffold.orm.BaseAuditable.AUDITABLE_FIELDS;

public class MysqlInjector extends DefaultSqlInjector {


    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
        methodList.add(new InsertBatchSomeColumn());
        //审计字段的下面几个不会被强制覆盖，只会被审计器管理
        methodList.add(new AlwaysUpdateSomeColumnById(
                fieldInfo -> !AUDITABLE_FIELDS.contains(fieldInfo.getProperty())
        ));
        return methodList;
    }
}

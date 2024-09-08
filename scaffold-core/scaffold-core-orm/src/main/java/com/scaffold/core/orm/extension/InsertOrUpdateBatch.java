//package com.scaffold.orm.extension;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.core.injector.AbstractMethod;
//import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
//import com.baomidou.mybatisplus.core.metadata.TableInfo;
//import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
//import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
//import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
//import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
//import org.apache.ibatis.executor.keygen.KeyGenerator;
//import org.apache.ibatis.executor.keygen.NoKeyGenerator;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.mapping.SqlSource;
//
//import java.util.List;
//import java.util.function.Predicate;
//
//public class InsertOrUpdateBatch extends AbstractMethod {
//
//    private static final String NAME = "insertOrUpdateBatch";
//    private Predicate<TableFieldInfo> predicate;
//
//    public InsertOrUpdateBatch() {
//        super(NAME);
//    }
//
//    public InsertOrUpdateBatch(Predicate<TableFieldInfo> predicate) {
//        super(NAME);
//        this.predicate = predicate;
//    }
//
//    public InsertOrUpdateBatch(String name, Predicate<TableFieldInfo> predicate) {
//        super(name);
//        this.predicate = predicate;
//    }
//
//    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
//        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
//        String sqlTemplate = """
//                <script>
//                    INSERT INTO %s %s VALUES %s on duplicate key update %s
//                </script>
//                """;
//        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
//        String insertSqlColumn = tableInfo.getKeyInsertSqlColumn(true, null, false) + this.filterTableFieldInfo(fieldList, this.predicate, TableFieldInfo::getInsertSqlColumn, "");
//        String columnScript = "(" + insertSqlColumn.substring(0, insertSqlColumn.length() - 1) + ")";
//        String insertSqlProperty = tableInfo.getKeyInsertSqlProperty(true, "et.", false) + this.filterTableFieldInfo(fieldList, this.predicate, (i) -> i.getInsertSqlProperty("et."), "");
//        insertSqlProperty = "(" + insertSqlProperty.substring(0, insertSqlProperty.length() - 1) + ")";
//        String valuesScript = SqlScriptUtils.convertForeach(insertSqlProperty, "list", null, "et", ",");
//        String valueUpdateScript = this.filterTableFieldInfo(fieldList, this.predicate, t -> {
//            String col = t.getInsertSqlColumn().replace(",", "");
//            return String.format("%s = values(%s)", col, col);
//        }, ",");
//        String keyProperty = null;
//        String keyColumn = null;
//        if (tableInfo.havePK()) {
//            if (tableInfo.getIdType() == IdType.AUTO) {
//                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
//                keyProperty = tableInfo.getKeyProperty();
//                keyColumn = SqlInjectionUtils.removeEscapeCharacter(tableInfo.getKeyColumn());
//            } else if (null != tableInfo.getKeySequence()) {
//                keyGenerator = TableInfoHelper.genKeyGenerator(this.methodName, tableInfo, this.builderAssistant);
//                keyProperty = tableInfo.getKeyProperty();
//                keyColumn = tableInfo.getKeyColumn();
//            }
//        }
//        String sql = String.format(sqlTemplate, tableInfo.getTableName(), columnScript, valuesScript, valueUpdateScript);
//        SqlSource sqlSource = super.createSqlSource(this.configuration, sql, modelClass);
//        return this.addInsertMappedStatement(mapperClass, modelClass, this.methodName, sqlSource, keyGenerator, keyProperty, keyColumn);
//    }
//}

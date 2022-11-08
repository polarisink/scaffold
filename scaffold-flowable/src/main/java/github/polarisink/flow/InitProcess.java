package github.polarisink.flow;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

/**
 * @author aries
 * @date 2022/11/8
 */
public class InitProcess {
  public static void main(String[] args) {
    // 流程引擎配置
    ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
        .setJdbcUrl("jdbc:mysql://1.13.169.163:3306/flow?autoRec&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false")
        .setJdbcUsername("root")
        .setJdbcPassword("vgy87dgy16rhd61fAGHbcgA")
        .setJdbcDriver("com.mysql.cj.jdbc.Driver")
        // 初始化基础表，不需要的可以改为 DB_SCHEMA_UPDATE_FALSE
        .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
    // 初始化流程引擎对象
    ProcessEngine processEngine = cfg.buildProcessEngine();
  }
}

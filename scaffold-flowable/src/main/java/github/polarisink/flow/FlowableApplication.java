package github.polarisink.flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author aries
 * @date 2022/11/8
 */
@SpringBootApplication
public class FlowableApplication {

  public static void main(String[] args) {
    SpringApplication.run(FlowableApplication.class, args);
    // 初始化基础表，不需要的可以改为 DB_SCHEMA_UPDATE_FALSE
    //ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration().setJdbcUrl("jdbc:mysql://1.13.169.163:3306/flow?autoRec&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false")
    // .setJdbcUsername("root").setJdbcPassword("vgy87dgy16rhd61fAGHbcgA").setJdbcDriver("com.mysql.cj.jdbc.Driver").setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
  }
}

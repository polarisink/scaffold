package github.polarisink.dao;

import github.polarisink.BaseJunit4;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author aries
 * @date 2022/12/12
 */
public class TdTest extends BaseJunit4 {

  @Autowired
  JdbcTemplate jdbcTemplate;
  @Test
  public void showDatabases(){
    System.out.println(jdbcTemplate.queryForList("show databases", String.class));
  }

}

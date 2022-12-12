package github.polarisink.dao.entity.td;

import io.xream.sqli.annotation.X;
import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Data;

/**
 * @Author Sim
 * TDengine
 * 1. create table if not exists t_heat_supply (id TIMESTAMP,value FLOAT,sn NCHAR(20)) tags(city NCHAR(20),zone NCHAR(20));
 */
@Data
 public class HeatSupply {

    @X.Key
    //System.currentTimeMillis()
    private Timestamp id;
    private BigDecimal value;
    @X.Tag
    private String city;
    @X.Tag
    private String zone;
    // 标注sn作为子表名后缀
    @X.TagTarget
    private String sn;

}

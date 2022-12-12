package github.polarisink.api.handler;

import github.polarisink.api.service.HeatSupplyService;
import github.polarisink.dao.entity.td.HeatSupply;
import io.xream.internal.util.StringUtil;
import io.xream.sqli.builder.Criteria;
import io.xream.sqli.builder.CriteriaBuilder;
import io.xream.sqli.builder.ReduceType;
import io.xream.x7.base.web.ViewEntity;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Sim
 */
@RestController
@RequestMapping("/heatsupply")
@RequiredArgsConstructor
public class HeatSupplyController {

  private final HeatSupplyService service;

  @GetMapping(value = "/create")
  public ViewEntity create() {

    HeatSupply heatSupply = new HeatSupply();
    heatSupply.setId(new Timestamp(System.currentTimeMillis()));
    heatSupply.setCity("北京市");
    heatSupply.setZone("朝阳区");
    heatSupply.setSn("H100003");
    heatSupply.setValue(new BigDecimal(15));

    HeatSupply heatSupply1 = new HeatSupply();
    heatSupply1.setId(new Timestamp(System.currentTimeMillis()));
    heatSupply1.setCity("北京市");
    heatSupply1.setZone("海淀区");
    heatSupply1.setSn("H100004");
    heatSupply1.setValue(new BigDecimal(16));

    List<HeatSupply> list = new ArrayList<>();
    list.add(heatSupply);
    list.add(heatSupply1);

    this.service.create(list);

    return ViewEntity.ok();
  }

  @GetMapping(value = "/find/{interval}")
  public ViewEntity find(@PathVariable String interval) {

    CriteriaBuilder.ResultMapBuilder criteriaBuilder = CriteriaBuilder.resultMapBuilder();
    criteriaBuilder.reduce(ReduceType.SUM, "value");
    criteriaBuilder.eq("city", "北京市").eq("zone", "朝阳区");
//        criteriaBuilder.xAggr("INTERVAL(?)", interval); //报语法错误 INTERVAL('2s'), 等TDengine官方支持
    if (StringUtil.isNotNull(interval)) {//需要显示判断是否为null
      criteriaBuilder.xAggr("INTERVAL(" + interval + ")");//INTERVAL(2s)
    }

    criteriaBuilder.paged().page(1).rows(10).ignoreTotalRows();
    Criteria.ResultMapCriteria criteria = criteriaBuilder.build();

    List<Map<String, Object>> list = this.service.find(criteria);

    return ViewEntity.ok(list);
  }

  @GetMapping("/list")
  public List<HeatSupply> list() {
    return service.list();
  }
}

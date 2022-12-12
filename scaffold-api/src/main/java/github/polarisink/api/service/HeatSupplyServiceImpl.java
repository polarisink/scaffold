package github.polarisink.api.service;

import github.polarisink.dao.entity.td.HeatSupply;
import github.polarisink.dao.repo.td.HeatSupplyRepo;
import io.xream.sqli.builder.Criteria;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author Sim
 */
@Service
@RequiredArgsConstructor
public class HeatSupplyServiceImpl implements HeatSupplyService {

  private final HeatSupplyRepo repository;

  @Override
  public boolean create(HeatSupply heatSupply) {
    return this.repository.create(heatSupply);
  }

  @Override
  public boolean create(List<HeatSupply> list) {
    return this.repository.createBatch(list);
  }

  @Override
  public List<Map<String, Object>> find(Criteria.ResultMapCriteria criteria) {
    return this.repository.list(criteria);
  }

  @Override
  public List<HeatSupply> list() {
    return repository.list();
  }
}

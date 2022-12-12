package github.polarisink.dao.repo.td;

import github.polarisink.dao.entity.td.HeatSupply;
import io.xream.sqli.api.BaseRepository;
import io.xream.sqli.api.ResultMapRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author Sim
 */
@Repository
public interface HeatSupplyRepo extends BaseRepository<HeatSupply>, ResultMapRepository {

}

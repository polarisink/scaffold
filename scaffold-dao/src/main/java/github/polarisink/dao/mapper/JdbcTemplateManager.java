package github.polarisink.dao.mapper;

import github.polarisink.dao.entity.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JdbcTemplateManager {
    private final JdbcTemplate jdbcTemplate;

    public int[] addPersons(List<Object[]> args) {
        return jdbcTemplate.batchUpdate("insert into person (name,age,address,goodAt) values (?,?,?,?)", args);
    }

    public int[][] addPersons2(List<Person> personList) {
        return jdbcTemplate.batchUpdate("insert into person (name,age,address,goodAt) values (?,?,?,?)", personList, 50, (ps, p) -> {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getAge());
            ps.setString(3, p.getAddress());
            ps.setString(4, p.getGoodAt());
        });
    }
}

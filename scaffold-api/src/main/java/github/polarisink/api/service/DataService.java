package github.polarisink.api.service;

import github.polarisink.dao.entity.Person;
import github.polarisink.dao.mapper.PersonMapper;
import github.polarisink.dao.mapper.PersonRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DataService {
    private final PersonMapper personMapper;
    private final PersonRepo personRepo;

    @Transactional(rollbackFor = Exception.class)
    public void get() {
        Person lqs = new Person().setName("lqs").setAge(23).setAddress("湖北省武汉市").setGoodAt("coding");
        personMapper.insert(lqs);
        if (true) {
            throw new IllegalStateException("111111");
        }
        Person lvping = new Person().setName("吕品").setGoodAt("shopping").setAge(28).setAddress("湖北咸宁");
        personRepo.saveAndFlush(lvping);
    }
}

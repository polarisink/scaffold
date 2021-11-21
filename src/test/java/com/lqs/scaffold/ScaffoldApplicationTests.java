package com.lqs.scaffold;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lqs.scaffold.entity.Step;
import com.lqs.scaffold.repository.StepRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ScaffoldApplicationTests {

	@Autowired
	StepRepository stepRepository;
	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void contextLoads() throws JsonProcessingException {
		test(1);
		test(10);
		test(100);
		test(1000);
		test(10000);
	}

	void test(int total) throws JsonProcessingException {
		long start = System.currentTimeMillis();
		String s = "wdufnpuiweognsduighnusidnvuisdfnfouweghjfpw9ghnvpuwe " +
			"ifh2oguwhjo9f8vnyuq30gu3q8mrgu3rghgredewciuhgdcwkpfkmv[pasodkv[" +
			"w0ivgbfi0fj9piwjuf908wh08a7hf078qyf97d967vgr97fvg9786g896w7tefg9678j -";
		Step step = new Step();
		step.setName(s);
		step.setAssembly(s);
		step.setSelfCheck(s);
		step.setAssembly(s);
		step.setSpecialCheck(s);
		step.setOrderNum(s);
		List<Step> steps = new ArrayList<>();
		int times = total;
		while (times > 1) {
			steps.add(step);
			times--;
		}
		times = total;
		step.setData(objectMapper.writeValueAsString(steps));
		Step saveAndFlush = stepRepository.saveAndFlush(step);
		String data = saveAndFlush.getData();
		List<Step> stepList = objectMapper.readValue(data, new TypeReference<List<Step>>() {
		});
		long end = System.currentTimeMillis();
		log.info("存取{}个数据用时:{} ms", times, end - start);
	}

}

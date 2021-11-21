package com.lqs.scaffold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author lqs
 */
@SpringBootApplication
@EnableCaching
public class ScaffoldApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScaffoldApplication.class, args);
	}

}

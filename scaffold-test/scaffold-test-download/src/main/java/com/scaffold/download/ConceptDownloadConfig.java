package com.scaffold.download;

import com.github.linyuzai.download.coroutines.loader.CoroutinesSourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConceptDownloadConfig {

    @Bean
    public CoroutinesSourceLoader coroutinesSourceLoader() {
        return new CoroutinesSourceLoader();
    }
}

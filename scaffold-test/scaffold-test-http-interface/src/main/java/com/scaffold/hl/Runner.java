package com.scaffold.hl;

import com.scaffold.hl.api.RestApi;
import com.scaffold.hl.api.RetrofitApi;
import com.scaffold.hl.api.WebApi;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Runner implements ApplicationRunner {
    private final RetrofitApi retrofitApi;
    private final RestApi restApi;
    private final WebApi webApi;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            System.out.println("remoteApi: " + retrofitApi.hello().execute().body());
            System.out.println("restApi: " + restApi.hello());
            webApi.hello().subscribe(x -> System.out.println("webApi#hello" + x));
            webApi.sse().subscribe(x -> System.out.println("webApi#sse" + x));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

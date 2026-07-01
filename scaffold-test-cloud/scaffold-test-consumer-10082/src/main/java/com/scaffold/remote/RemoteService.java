package com.scaffold.remote;

import com.scaffold.base.util.R;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Map;

@HttpExchange("/api")
public interface RemoteService {
    @GetExchange("/echo")
    R<Map<String, String>> echo(@RequestParam(name = "message", defaultValue = "world") String message);
}

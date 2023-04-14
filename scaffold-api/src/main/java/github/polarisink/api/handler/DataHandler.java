package github.polarisink.api.handler;

import github.polarisink.api.service.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataHandler {
    private final DataService dataService;
    @GetMapping("/get")
    public void get(){
        dataService.get();
    }
}

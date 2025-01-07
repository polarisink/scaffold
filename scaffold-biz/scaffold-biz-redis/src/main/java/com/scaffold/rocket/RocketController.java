package com.scaffold.rocket;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RocketController {

    private final RocketService rocketService;

    @Operation(summary = "选择火箭进行发射")
    @GetMapping("/launch")
    public String launchRocket(@RequestParam String  rocketId) {
        rocketService.launchRocket(rocketId);
        return "Rocket ID " + rocketId + " launched successfully.";
    }

    @Operation(summary = "修改训练步进")
    @PostMapping("/step/{trainId}/{step}")
    public void updateStep(@PathVariable("trainId") String trainId, @PathVariable("step") double step) {
        rocketService.updateStep(trainId, step);
    }

    @Operation(summary = "暂停/启动训练")
    @PutMapping("/pause/{trainId}/{status}")
    public void pause(@PathVariable String trainId, @PathVariable boolean status) {
        rocketService.runningStatus(trainId,status);
    }
}
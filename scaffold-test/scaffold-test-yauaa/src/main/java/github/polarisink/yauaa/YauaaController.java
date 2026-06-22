package github.polarisink.yauaa;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/yauaa")
public class YauaaController {
    final UserAgentAnalyzer uaa;

    public YauaaController(UserAgentAnalyzer agentAnalyzer) {
        this.uaa = agentAnalyzer;
    }

    @GetMapping
    public ResponseEntity<?> useragent(@RequestHeader("User-Agent") String userAgent) {
        UserAgent agent = uaa.parse(userAgent);
        Map<String, Object> ret = new HashMap<>();
        for (String fieldName : agent.getAvailableFieldNamesSorted()) {
            ret.put(fieldName, agent.getValue(fieldName));
        }
        return ResponseEntity.ok(ret);
    }
}

package kr.hhplus.be.server.apps.mock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/mock/api")

public class mockAPI {
    @GetMapping
    public String mockAPI() {
        return "MOCK API OK";
    }
}




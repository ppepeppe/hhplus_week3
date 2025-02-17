package kr.hhplus.be.server.apps.mock;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data-platform")
public class MockDataPlatformController {
    
    @PostMapping("/orders")
    public ResponseEntity<Void> receiveOrderData(@RequestBody OrderDataRequest request) {
        // 실제 처리는 하지 않고 200 OK 응답만 반환
        return ResponseEntity.ok().build();
    }
}
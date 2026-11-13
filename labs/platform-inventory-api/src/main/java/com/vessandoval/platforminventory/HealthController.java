package com.vessandoval.platforminventory;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public Map<String,String> healt() {

        return Map.of(
            "application", "platform-inventory-apu",
            "status", "UP",
            "versioni", "1.0.1"
        );
    }    
}

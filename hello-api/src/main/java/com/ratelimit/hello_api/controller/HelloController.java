package com.ratelimit.hello_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/hello")
public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @GetMapping
    public String getHello(HttpServletRequest request){
        String path = request.getRequestURI();
        log.info("hello-api received request to {}", path);

        // Log X-User-Id if present and a summary of headers for debugging
        String xUser = request.getHeader("X-User-Id");
        if (xUser != null) {
            log.info("hello-api: X-User-Id header present: {}", xUser);
        } else {
            log.info("hello-api: X-User-Id header not present");
        }

        // Optionally log a few common headers
        String auth = request.getHeader("Authorization");
        if (auth != null) {
            log.debug("hello-api: Authorization header present (length={})", auth.length());
        }

        return "hello";
    }
}
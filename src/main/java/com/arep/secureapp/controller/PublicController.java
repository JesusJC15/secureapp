package com.arep.secureapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arep.secureapp.dto.PublicInfoResponse;

@RestController
public class PublicController {

    @GetMapping("/api/public/info")
    public PublicInfoResponse publicInfo() {
        return new PublicInfoResponse(
                "Secure Application Lab",
                "HTTPS only",
                "Spring Boot REST API behind Apache reverse proxy",
                "Register -> login -> use Bearer token for protected endpoints");
    }
}

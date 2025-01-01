package com.example.dncompany.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {
    @GetMapping("/hospital")
    public String hospital() {
        return "map/hospital";
    }
    @GetMapping("/pet-service")
    public String petService() {
        return "map/pet-service";
    }
}

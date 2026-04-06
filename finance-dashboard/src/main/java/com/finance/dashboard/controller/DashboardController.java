package com.finance.dashboard.controller;

import com.finance.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonView;
import com.finance.dashboard.view.Views;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @JsonView(Views.Public.class)
    public Map<String,Object> getSummary(){
        return service.summary();
    }
}
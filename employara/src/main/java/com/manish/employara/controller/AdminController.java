package com.manish.employara.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manish.employara.service.AdminService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/company/{id}/{status}")
    public ResponseEntity<?> updateCompanyStatus(@PathVariable String id, @PathVariable String status, HttpServletRequest request) {
        return adminService.updateCompanyStatus(id, status, request);
    }
}

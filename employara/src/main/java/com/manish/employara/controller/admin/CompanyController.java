package com.manish.employara.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.manish.employara.models.recruiter.Company;
import com.manish.employara.models.Status;
import com.manish.employara.service.admin.CompanyManagementService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
public class CompanyController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private CompanyManagementService companyManagementService;

    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(companyManagementService.getAllCompanies());
    }
    
    @PatchMapping("/{companyId}/status")
    public ResponseEntity<Company> updateCompanyStatus(
            @PathVariable String companyId,
            @RequestBody Map<String, String> request) {
        Status newStatus = Status.valueOf(request.get("status"));
        return ResponseEntity.ok(companyManagementService.updateCompanyStatus(companyId, newStatus));
    }
}

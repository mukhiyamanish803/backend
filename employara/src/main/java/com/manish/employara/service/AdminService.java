package com.manish.employara.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.manish.employara.models.Role;
import com.manish.employara.models.Status;
import com.manish.employara.models.User;
import com.manish.employara.models.recruiter.Company;
import com.manish.employara.repository.CompanyRepository;
import com.manish.employara.utils.VerifyUser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final VerifyUser verifyUser;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;

    public ResponseEntity<?> getAllCompanies(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        if (!user.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admins only");
        }

        List<Company> companies = companyRepository.findAll();

        if (companies.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No companies found");
        }
        // Fetch and return the list of companies
        return ResponseEntity.ok(companies);
    }

    public ResponseEntity<?> updateCompanyStatus(String id, String status, HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        if (!user.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admins only");
        }

        Company company = companyRepository.findById(id).orElse(null);
        if (company == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found");
        }

        try {
            company.setStatus(Status.valueOf(status.toUpperCase()));
            companyRepository.save(company);
            Map<String, String> message = Map.of("message", "Company status has been updated.");

            if ("ACTIVE".equalsIgnoreCase(status)) {
                message = Map.of("message", "Your company has been approved and is now active.");
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                message = Map.of("message", "Your company has been rejected. Please contact support for more details.");
            }

            notificationService.notifyUser(company.getUserId(), message, "COMPANY_STATUS_UPDATE");
            return ResponseEntity.ok("Company status updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status value");
        }
    }
}

package com.manish.employara.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.manish.employara.service.AdminDashboardService;
import com.manish.employara.models.User;
import java.util.Map;
import java.util.HashMap;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/user/admin")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/users/count")
    public ResponseEntity<Map<String, Long>> getUserCounts() {
        return ResponseEntity.ok(adminDashboardService.getUserCounts());
    }

    @GetMapping("/jobs/count")
    public ResponseEntity<Map<String, Long>> getJobCounts() {
        return ResponseEntity.ok(adminDashboardService.getJobCounts());
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {

        Page<User> usersPage = adminDashboardService.getUsers(page, size, search, role, status);

        Map<String, Object> response = new HashMap<>();
        response.put("users", usersPage.getContent());
        response.put("currentPage", usersPage.getNumber());
        response.put("totalItems", usersPage.getTotalElements());
        response.put("totalPages", usersPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {

        String newStatus = request.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Status is required"));
        }

        User updatedUser = adminDashboardService.updateUserStatus(userId, newStatus);
        return ResponseEntity.ok(updatedUser);
    }
}

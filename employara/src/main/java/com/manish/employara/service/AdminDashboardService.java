package com.manish.employara.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.manish.employara.repository.JobRepository;
import com.manish.employara.repository.UserRepository;
import com.manish.employara.models.Status;
import com.manish.employara.models.Role;
import com.manish.employara.models.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminDashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    public Map<String, Long> getUserCounts() {
        Map<String, Long> counts = new HashMap<>();
        long totalUsers = userRepository.count();
        counts.put("total", totalUsers);
        return counts;
    }

    public Map<String, Long> getJobCounts() {
        Map<String, Long> counts = new HashMap<>();
        long totalJobs = jobRepository.count();
        long activeJobs = jobRepository.countByStatus(Status.ACTIVE);

        counts.put("total", totalJobs);
        counts.put("active", activeJobs);
        return counts;
    }

    public Page<User> getUsers(int page, int size, String search, String roleStr, String statusStr) {
        Pageable pageable = PageRequest.of(page, size);

        Role role = (roleStr != null && !roleStr.equals("ALL")) ? Role.valueOf(roleStr) : null;
        Status status = (statusStr != null && !statusStr.equals("ALL")) ? Status.valueOf(statusStr) : null;

        if (search != null && !search.trim().isEmpty()) {
            if (role != null) {
                if (status != null) {
                    return userRepository.findBySearchTermAndRoleAndStatus(search, role, status, pageable);
                }
                return userRepository.findBySearchTermAndRole(search, role, pageable);
            }
            return userRepository.findBySearchTerm(search, pageable);
        }

        if (role != null) {
            if (status != null) {
                return userRepository.findByRoleAndStatus(role, status, pageable);
            }
            return userRepository.findByRole(role, pageable);
        }

        if (status != null) {
            return userRepository.findByStatus(status, pageable);
        }

        return userRepository.findAll(pageable);
    }

    public User updateUserStatus(String userId, String statusStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Status status = Status.valueOf(statusStr);
        // Only allow ACTIVE or BLOCKED status for users
        if (status != Status.ACTIVE && status != Status.BLOCKED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status for user");
        }

        user.setStatus(status);
        return userRepository.save(user);
    }
}

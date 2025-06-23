package com.manish.employara.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manish.employara.dto.SearchRequestDTO;
import com.manish.employara.models.DialCode;
import com.manish.employara.models.admin.management.Category;
import com.manish.employara.repository.DialCodeRepository;
import com.manish.employara.repository.admin.management.CategoryRepository;
import com.manish.employara.service.AuthService;
import com.manish.employara.service.PublicService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final CategoryRepository categoryRepository;
    private final DialCodeRepository dialCodeRepository;
    private final PublicService publicService;
    private final AuthService authService;

    @GetMapping("/categories")
    public ResponseEntity<?> getCategory() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/dial-code")
    public ResponseEntity<?> getDialCode() {
        List<DialCode> dialCodes = dialCodeRepository.findAll();
        return ResponseEntity.ok(dialCodes);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }

    @GetMapping("/find-all-jobs")
    public ResponseEntity<?> findAllJobs() {
        return publicService.findAllJobs();
    }

    @GetMapping("find-job/{id}")
    public ResponseEntity<?> findJob(@PathVariable String id) {
        return publicService.findJob(id);
    }

    @PostMapping("/search-jobs")
    public ResponseEntity<?> searchJobs(@RequestBody SearchRequestDTO searchRequest) {
        return publicService.searchJobs(searchRequest);
    }

    @GetMapping("/find-jobs-by-category/{category}")
    public ResponseEntity<?> findJobsByCategory(@PathVariable String category) {
        return publicService.findJobsByCategory(category);
    }

    @GetMapping("/notification")
    public ResponseEntity<?> getNotifications(HttpServletRequest request) {
        return publicService.getNotifications(request);
    }

    @GetMapping("/is-api-working")
    public ResponseEntity<?> isApiWorking() {
        return ResponseEntity.ok("Yes, your api is working.");
    }

}

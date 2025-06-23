package com.manish.employara.controller.admin;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manish.employara.dto.DialCodeDTO;
import com.manish.employara.dto.admin.management.CategoryDTO;
import com.manish.employara.service.admin.ManagementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/admin")
public class ManagementController {

    private final ManagementService managementService;

    @PostMapping("/add-dial-code")
    public ResponseEntity<?> addDialCode(HttpServletRequest request,
            @Valid @RequestBody DialCodeDTO dialCodeRequestDTO) {
        return managementService.addDialCode(request, dialCodeRequestDTO);
    }

    @PostMapping("/add-category")
    public ResponseEntity<?> addCategory(HttpServletRequest request, @Valid @RequestBody CategoryDTO categoryDTO){
        return managementService.addCategory(request, categoryDTO);
    }


    @DeleteMapping("/remove-dial-code")
    public ResponseEntity<?> removeDialCode(
            HttpServletRequest request,
            @Valid @RequestBody DialCodeDTO dialCodeRequestDTO) {
        return managementService.removeDialCode(request, dialCodeRequestDTO);
    }


}

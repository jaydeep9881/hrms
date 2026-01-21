package com.hrms.hrms.reimbursement.controller;

import com.hrms.hrms.reimbursement.dto.ReimbursementRequest;
import com.hrms.hrms.reimbursement.dto.ReimbursementResponse;
import com.hrms.hrms.reimbursement.service.ReimbursementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/reimbursements")
public class ReimbursementController {
    @Autowired
    private ReimbursementService reimbursementService;

    @GetMapping
    public ResponseEntity<List<ReimbursementResponse>> getAllReimbursements() {
        List<ReimbursementResponse> reimbursements = reimbursementService.getAllReimbursements();
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReimbursementResponse> getReimbursementById(@PathVariable Long id) {
        ReimbursementResponse reimbursement = reimbursementService.getReimbursementById(id);
        return ResponseEntity.ok(reimbursement);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ReimbursementResponse> createReimbursement(
            @ModelAttribute @Valid ReimbursementRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        ReimbursementResponse createdReimbursement = reimbursementService.createReimbursement(request, image);
        return ResponseEntity.ok(createdReimbursement);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ReimbursementResponse> updateReimbursement(
            @PathVariable Long id,
            @ModelAttribute @Valid ReimbursementRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        ReimbursementResponse updatedReimbursement = reimbursementService.updateReimbursement(id, request, image);
        return ResponseEntity.ok(updatedReimbursement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReimbursement(@PathVariable Long id) {
        reimbursementService.deleteReimbursement(id);
        return ResponseEntity.ok("Reimbursement deleted successfully");
    }
}


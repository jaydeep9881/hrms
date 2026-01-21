package com.hrms.hrms.leave.controller;

import com.hrms.hrms.leave.dto.LeaveRequest;
import com.hrms.hrms.leave.dto.LeaveResponse;
import com.hrms.hrms.leave.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    // Create leave request
    @PostMapping
    public ResponseEntity<LeaveResponse> createLeave(@RequestBody @Valid LeaveRequest request) {
        LeaveResponse response = leaveService.createLeave(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all leaves
    @GetMapping
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {
        List<LeaveResponse> leaves = leaveService.getAllLeaves();
        return ResponseEntity.ok(leaves);
    }

    // Get leave by ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable Long id) {
        LeaveResponse leave = leaveService.getLeaveById(id);
        return ResponseEntity.ok(leave);
    }

    // Get leaves for an employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByEmployee(@PathVariable Long employeeId) {
        List<LeaveResponse> leaves = leaveService.getLeavesByEmployee(employeeId);
        return ResponseEntity.ok(leaves);
    }

    // Get leaves by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByStatus(@PathVariable String status) {
        List<LeaveResponse> leaves = leaveService.getLeavesByStatus(status);
        return ResponseEntity.ok(leaves);
    }

    // Get leaves by leave type
    @GetMapping("/type/{leaveType}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByType(@PathVariable String leaveType) {
        List<LeaveResponse> leaves = leaveService.getLeavesByType(leaveType);
        return ResponseEntity.ok(leaves);
    }

    // Approve leave
    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveResponse> approveLeave(
            @PathVariable Long id,
            @RequestParam String approvedBy) {
        LeaveResponse response = leaveService.approveLeave(id, approvedBy);
        return ResponseEntity.ok(response);
    }

    // Reject leave
    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveResponse> rejectLeave(
            @PathVariable Long id,
            @RequestParam String rejectedBy,
            @RequestParam(required = false) String rejectionReason) {
        LeaveResponse response = leaveService.rejectLeave(id, rejectedBy, rejectionReason);
        return ResponseEntity.ok(response);
    }

    // Cancel leave
    @PutMapping("/{id}/cancel")
    public ResponseEntity<LeaveResponse> cancelLeave(@PathVariable Long id) {
        LeaveResponse response = leaveService.cancelLeave(id);
        return ResponseEntity.ok(response);
    }

    // Update leave record
    @PutMapping("/{id}")
    public ResponseEntity<LeaveResponse> updateLeave(
            @PathVariable Long id,
            @RequestBody @Valid LeaveRequest request) {
        LeaveResponse response = leaveService.updateLeave(id, request);
        return ResponseEntity.ok(response);
    }

    // Delete leave record
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeave(@PathVariable Long id) {
        leaveService.deleteLeave(id);
        return ResponseEntity.ok("Leave deleted successfully");
    }
}


package com.hrms.hrms.salary.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hrms.hrms.salary.dto.SalaryRequest;
import com.hrms.hrms.salary.dto.SalaryResponse;
import com.hrms.hrms.salary.service.SalaryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/salaries")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    // Create salary record
    @PostMapping
    public ResponseEntity<SalaryResponse> createSalary(@RequestBody @Valid SalaryRequest request) {
        SalaryResponse response = salaryService.createSalary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all salaries
    @GetMapping
    public ResponseEntity<List<SalaryResponse>> getAllSalaries() {
        List<SalaryResponse> salaries = salaryService.getAllSalaries();
        return ResponseEntity.ok(salaries);
    }

    // Get salary by ID
    @GetMapping("/{id}")
    public ResponseEntity<SalaryResponse> getSalaryById(@PathVariable Long id) {
        SalaryResponse salary = salaryService.getSalaryById(id);
        return ResponseEntity.ok(salary);
    }

    // Get salaries for an employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<SalaryResponse>> getSalariesByEmployee(@PathVariable Long employeeId) {
        List<SalaryResponse> salaries = salaryService.getSalariesByEmployee(employeeId);
        return ResponseEntity.ok(salaries);
    }

    // Get salaries by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SalaryResponse>> getSalariesByStatus(@PathVariable String status) {
        List<SalaryResponse> salaries = salaryService.getSalariesByStatus(status);
        return ResponseEntity.ok(salaries);
    }

    // Update salary record
    @PutMapping("/{id}")
    public ResponseEntity<SalaryResponse> updateSalary(
            @PathVariable Long id,
            @RequestBody @Valid SalaryRequest request) {
        SalaryResponse response = salaryService.updateSalary(id, request);
        return ResponseEntity.ok(response);
    }

    // Delete salary record
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSalary(@PathVariable Long id) {
        salaryService.deleteSalary(id);
        return ResponseEntity.ok("Salary deleted successfully");
    }
}


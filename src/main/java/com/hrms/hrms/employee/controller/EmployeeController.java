package com.hrms.hrms.employee.controller;

import com.hrms.hrms.employee.dto.EmployeeRequest;
import com.hrms.hrms.employee.dto.EmployeeResponse;
import com.hrms.hrms.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/employees")
    public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody @Valid EmployeeRequest employeeRequest) {
        EmployeeResponse emp= employeeService.addEmployee(employeeRequest);
        return ResponseEntity.ok(emp);
    }
    @GetMapping("/employee/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse emp= employeeService.getEmployeeById(id);
        return ResponseEntity.ok(emp);
    }
    @PutMapping("/update_employee/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id,@RequestBody EmployeeRequest employeeRequest) {
        EmployeeResponse  emp=employeeService.updateEmployee(id,employeeRequest);
        return ResponseEntity.ok(emp);
    }

    @DeleteMapping("/delete_employee/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}

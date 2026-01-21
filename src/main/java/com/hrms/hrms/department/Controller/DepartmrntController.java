package com.hrms.hrms.department.Controller;

import com.hrms.hrms.department.dto.DeopartmentRequest;
import com.hrms.hrms.department.model.Department;
import com.hrms.hrms.department.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/department")
    public ResponseEntity<List<Department>> getAllDepartment(){
        List<Department> departments=departmentService.getAllDepartment();
        return ResponseEntity.ok(departments);
    }
    @PostMapping("/department")
    public ResponseEntity<Department> addDepartment(@RequestBody Department departmentRequest) {
         Department department = departmentService.addDepartment(departmentRequest);
        return ResponseEntity.ok(department);
    }
    @PutMapping("/department/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id,@RequestBody Department department){
        Department department1=departmentService.updateDepartment(id,department);
        return ResponseEntity.ok(department1);
    }
    @DeleteMapping("/department/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        // Implementation for deleting a department can be added here
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok("Department deleted successfully");
    }
}

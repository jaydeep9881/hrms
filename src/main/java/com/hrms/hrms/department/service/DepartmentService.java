package com.hrms.hrms.department.service;

import com.hrms.hrms.department.dto.DeopartmentRequest;
import com.hrms.hrms.department.model.Department;
import com.hrms.hrms.department.repositary.DepartmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepo departmentRepo;
    public List<Department> getAllDepartment() {
        List<Department> list=departmentRepo.findAll();
        return list;
    }
    public Department addDepartment(Department departmentRequest) {
        Department department = departmentRepo.save(departmentRequest);
    return department;
    }

    public Department updateDepartment(Long id, Department department) {
        Department existingDepartment = departmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        existingDepartment.setName(department.getName());
        return departmentRepo.save(existingDepartment);

    }

    public void deleteDepartment(Long id) {
        if (!departmentRepo.existsById(id)) {
            throw new RuntimeException("Deparment not found with id: " + id);
        }
        departmentRepo.deleteById(id);

    }
}

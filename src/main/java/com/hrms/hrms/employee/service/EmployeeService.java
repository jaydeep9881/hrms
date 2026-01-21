package com.hrms.hrms.employee.service;

import com.hrms.hrms.department.model.Department;
import com.hrms.hrms.department.repositary.DepartmentRepo;
import com.hrms.hrms.employee.dto.EmployeeRequest;
import com.hrms.hrms.employee.dto.EmployeeResponse;
import com.hrms.hrms.employee.model.Employee;
import com.hrms.hrms.employee.repository.EmployeeRepo;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private DepartmentRepo departmentRepo;

    // Convert Employee model to EmployeeResponse DTO
    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .DepartmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .role(employee.getRole())
                .salary(employee.getSalary())
                .joiningDate(employee.getJoiningDate())
                .isActive(employee.getIsActive())
                .about(employee.getAbout())
                .address(employee.getAddress())
                .city(employee.getCity())
                .country(employee.getCountry())
                .mobileNumber(employee.getMobileNumber())
                .website(employee.getWebsite())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

    // Convert EmployeeRequest DTO to Employee model
    private Employee mapToEmployee(EmployeeRequest request) {
        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getDepartmentId()));


        return Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .department(department)
                .role(request.getRole())
                .salary(request.getSalary())
                .joiningDate(request.getJoiningDate())
                .isActive(request.getIsActive())
                .city(request.getCity())
                .about(request.getAbout())
                .address(request.getAddress())
                .country(request.getCountry())
                .mobileNumber(request.getMobileNumber())
                .website(request.getWebsite())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepo.findAll();
        return employees.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public EmployeeResponse addEmployee(EmployeeRequest request) {
        Employee employee = mapToEmployee(request);
        Employee savedEmployee = employeeRepo.save(employee);
        return mapToResponse(savedEmployee);
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee existingEmployee = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Apply updates from request
        if( request.getFirstName() != null) {
            existingEmployee.setFirstName(request.getFirstName());
        }
        if( request.getLastName() != null) {
            existingEmployee.setLastName(request.getLastName());
        }
        if( request.getEmail() != null) {
            existingEmployee.setEmail(request.getEmail());
        }
        if( request.getRole() != null) {
            existingEmployee.setRole(request.getRole());
        }
        if( request.getSalary() != null) {
            existingEmployee.setSalary(request.getSalary());
        }
        if( request.getJoiningDate() != null) {
            existingEmployee.setJoiningDate(request.getJoiningDate());
        }
        if( request.getIsActive() != null) {
            existingEmployee.setIsActive(request.getIsActive());
        }
        if( request.getAbout() != null) {
            existingEmployee.setAbout(request.getAbout());
        }
        if( request.getAddress() != null) {
            existingEmployee.setAddress(request.getAddress());
        }
        if( request.getCity() != null) {
            existingEmployee.setCity(request.getCity());
        }
        if( request.getCountry() != null) {
            existingEmployee.setCountry(request.getCountry());
        }
        if( request.getMobileNumber() != null) {
            existingEmployee.setMobileNumber(request.getMobileNumber());
        }
        if( request.getWebsite() != null) {
            existingEmployee.setWebsite(request.getWebsite());
        }

        // Update department
        if ( request.getDepartmentId()  != null) {
            Department department = departmentRepo.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            existingEmployee.setDepartment(department);
        }

        Employee updatedEmployee = employeeRepo.save(existingEmployee);
        return mapToResponse(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepo.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepo.deleteById(id);
    }
}

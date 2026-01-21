package com.hrms.hrms.employee.dto;

import com.hrms.hrms.department.model.Department;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long departmentId;
    private String DepartmentName;
    private String role;
    private Double salary;
    private LocalDate joiningDate;
    private Boolean isActive;
    private Department department;
    private String mobileNumber;
    private String address;
    private String website;
    private String country;
    private String city;
    private String about;
    private LocalDateTime updatedAt;
}

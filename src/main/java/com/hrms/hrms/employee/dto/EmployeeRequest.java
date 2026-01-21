package com.hrms.hrms.employee.dto;

import com.hrms.hrms.department.model.Department;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private Long departmentId;


    @NotBlank(message = "Role is required")
    private String role;

    @Positive(message = "Salary must be greater than 0")
    private Double salary;

    @PastOrPresent(message = "Joining date cannot be in the future")
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

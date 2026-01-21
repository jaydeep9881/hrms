package com.hrms.hrms.employee.model;

import com.hrms.hrms.department.model.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message ="Employee name is mandatory")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message ="Employee name is mandatory")
    @Column(nullable = false)
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(nullable = false, unique = true)
    private String email;

//    @NotBlank(message = "Department is required")
//    private String department;

    @NotBlank(message = "Role is required")
    private String role;

    @Positive(message = "Salary must be greater than 0")
    private Double salary;

    @PastOrPresent(message = "Joining date cannot be in the future")
    private LocalDate joiningDate;

    private Boolean isActive =true;

    private LocalDateTime createdAt ;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")

    // added later while creating a front end
    private Department department;
    private String mobileNumber;
    private String address;
    private String website;
    private String country;
    private String city;
    private String about;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate(){
        updatedAt =LocalDateTime.now();
    }

}

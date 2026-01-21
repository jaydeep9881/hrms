package com.hrms.hrms.salary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private Double baseSalary;
    private Double allowances;
    private Double deductions;
    private Double tax;
    private Double netSalary;
    private String status;
    private LocalDate paymentDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


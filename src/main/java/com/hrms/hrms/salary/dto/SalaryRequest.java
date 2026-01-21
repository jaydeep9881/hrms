package com.hrms.hrms.salary.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryRequest {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Pay period start date is required")
    private LocalDate payPeriodStart;

    @NotNull(message = "Pay period end date is required")
    private LocalDate payPeriodEnd;

    @NotNull(message = "Base salary is required")
    @Positive(message = "Base salary must be greater than 0")
    private Double baseSalary;

    private Double allowances;

    private Double deductions;

    private Double tax;

    @NotNull(message = "Net salary is required")
    @Positive(message = "Net salary must be greater than 0")
    private Double netSalary;

    private String status; // PENDING, PROCESSED, PAID, CANCELLED

    private LocalDate paymentDate;

    private String notes;
}


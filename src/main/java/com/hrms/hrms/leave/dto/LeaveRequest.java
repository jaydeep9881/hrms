package com.hrms.hrms.leave.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequest {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Leave type is required")
    private String leaveType; // SICK, VACATION, PERSONAL, etc.

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String reason;

    private String status; // PENDING, APPROVED, REJECTED, CANCELLED

    private String approvedBy;

    private String rejectionReason;

    private LocalDate approvedDate;
}


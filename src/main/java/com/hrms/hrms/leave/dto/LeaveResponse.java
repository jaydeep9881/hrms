package com.hrms.hrms.leave.dto;

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
public class LeaveResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfDays;
    private String reason;
    private String status;
    private String approvedBy;
    private String rejectionReason;
    private LocalDate approvedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


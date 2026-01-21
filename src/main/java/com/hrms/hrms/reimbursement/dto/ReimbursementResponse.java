package com.hrms.hrms.reimbursement.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ReimbursementResponse {
    private Long id;
    private LocalDate date;
    private String description;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private Double amount;
    private String type;
    private String imageName;
    private String imageType;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


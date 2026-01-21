package com.hrms.hrms.attendance.dto;

import com.hrms.hrms.attendance.model.Attendance;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRequest {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Date is required")
    private LocalDate attendanceDate;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    // Location fields
    private Double latitude;
    private Double longitude;
    private String locationAddress;
    private String checkInLocation;
    private String checkOutLocation;

    private Attendance.AttendanceStatus status;

    private String notes;

    private Boolean isLate;
    private Boolean isEarlyLeave;
    private Double overtimeHours;
}


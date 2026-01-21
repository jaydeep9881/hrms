package com.hrms.hrms.attendance.dto;

import com.hrms.hrms.attendance.model.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private LocalDate attendanceDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Double workingHours;
    private Attendance.AttendanceStatus status;
    private Double latitude;
    private Double longitude;
    private String locationAddress;
    private String checkInLocation;
    private String checkOutLocation;
    private String notes;
    private Boolean isLate;
    private Boolean isEarlyLeave;
    private Double overtimeHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


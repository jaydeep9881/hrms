package com.hrms.hrms.attendance.model;

import com.hrms.hrms.employee.model.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendances")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Employee is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "working_hours")
    private Double workingHours; // in hours (e.g., 8.5 for 8 hours 30 minutes)

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    // Location fields
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location_address", length = 500)
    private String locationAddress;

    @Column(name = "check_in_location", length = 500)
    private String checkInLocation;

    @Column(name = "check_out_location", length = 500)
    private String checkOutLocation;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "is_late")
    @Builder.Default
    private Boolean isLate = false;

    @Column(name = "is_early_leave")
    @Builder.Default
    private Boolean isEarlyLeave = false;

    @Column(name = "overtime_hours")
    private Double overtimeHours;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        LATE,
        HALF_DAY,
        ON_LEAVE,
        HOLIDAY
    }
}


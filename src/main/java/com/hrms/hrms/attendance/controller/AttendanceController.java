package com.hrms.hrms.attendance.controller;

import com.hrms.hrms.attendance.dto.AttendanceRequest;
import com.hrms.hrms.attendance.dto.AttendanceResponse;
import com.hrms.hrms.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // Check-in endpoint
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(@RequestBody @Valid AttendanceRequest request) {
        AttendanceResponse response = attendanceService.checkIn(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Check-out endpoint
    @PostMapping("/check-out")
    public ResponseEntity<AttendanceResponse> checkOut(
            @RequestParam Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime checkOutTime,
            @RequestParam(required = false) String checkOutLocation,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        AttendanceResponse response = attendanceService.checkOut(employeeId, date, checkOutTime, checkOutLocation, latitude, longitude);
        return ResponseEntity.ok(response);
    }

    // Create or update attendance record
    @PostMapping
    public ResponseEntity<AttendanceResponse> createAttendance(@RequestBody @Valid AttendanceRequest request) {
        AttendanceResponse response = attendanceService.createOrUpdateAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all attendance records
    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance() {
        List<AttendanceResponse> attendances = attendanceService.getAllAttendance();
        return ResponseEntity.ok(attendances);
    }

    // Get attendance by ID
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable Long id) {
        AttendanceResponse attendance = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(attendance);
    }

    // Get attendance records for an employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByEmployee(@PathVariable Long employeeId) {
        List<AttendanceResponse> attendances = attendanceService.getAttendanceByEmployee(employeeId);
        return ResponseEntity.ok(attendances);
    }

    // Get attendance records for an employee within date range
    @GetMapping("/employee/{employeeId}/range")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByEmployeeAndDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceResponse> attendances = attendanceService.getAttendanceByEmployeeAndDateRange(employeeId, startDate, endDate);
        return ResponseEntity.ok(attendances);
    }

    // Get attendance records for a specific date
    @GetMapping("/date/{date}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceResponse> attendances = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(attendances);
    }

    // Get attendance records within date range
    @GetMapping("/range")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceResponse> attendances = attendanceService.getAttendanceByDateRange(startDate, endDate);
        return ResponseEntity.ok(attendances);
    }

    // Update attendance record
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable Long id,
            @RequestBody @Valid AttendanceRequest request) {
        AttendanceResponse response = attendanceService.createOrUpdateAttendance(request);
        return ResponseEntity.ok(response);
    }

    // Delete attendance record
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok("Attendance deleted successfully");
    }
}


package com.hrms.hrms.attendance.service;

import com.hrms.hrms.attendance.dto.AttendanceRequest;
import com.hrms.hrms.attendance.dto.AttendanceResponse;
import com.hrms.hrms.attendance.model.Attendance;
import com.hrms.hrms.attendance.repository.AttendanceRepository;
import com.hrms.hrms.employee.model.Employee;
import com.hrms.hrms.employee.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepo employeeRepo;

    // Convert Attendance entity to AttendanceResponse DTO
    private AttendanceResponse mapToResponse(Attendance attendance) {
        Employee employee = attendance.getEmployee();
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .employeeEmail(employee.getEmail())
                .attendanceDate(attendance.getAttendanceDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .workingHours(attendance.getWorkingHours())
                .status(attendance.getStatus())
                .latitude(attendance.getLatitude())
                .longitude(attendance.getLongitude())
                .locationAddress(attendance.getLocationAddress())
                .checkInLocation(attendance.getCheckInLocation())
                .checkOutLocation(attendance.getCheckOutLocation())
                .notes(attendance.getNotes())
                .isLate(attendance.getIsLate())
                .isEarlyLeave(attendance.getIsEarlyLeave())
                .overtimeHours(attendance.getOvertimeHours())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }

    // Convert AttendanceRequest DTO to Attendance entity
    private Attendance mapToAttendance(AttendanceRequest request) {
        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));

        Attendance.AttendanceStatus status = request.getStatus();
        if (status == null) {
            status = Attendance.AttendanceStatus.PRESENT;
        }

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .attendanceDate(request.getAttendanceDate())
                .checkInTime(request.getCheckInTime())
                .checkOutTime(request.getCheckOutTime())
                .status(status)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .locationAddress(request.getLocationAddress())
                .checkInLocation(request.getCheckInLocation())
                .checkOutLocation(request.getCheckOutLocation())
                .notes(request.getNotes())
                .isLate(request.getIsLate())
                .isEarlyLeave(request.getIsEarlyLeave())
                .overtimeHours(request.getOvertimeHours())
                .build();

        // Calculate working hours if both check-in and check-out times are provided
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            attendance.setWorkingHours(calculateWorkingHours(attendance.getCheckInTime(), attendance.getCheckOutTime()));
        }

        return attendance;
    }

    // Calculate working hours between check-in and check-out
    private Double calculateWorkingHours(LocalTime checkIn, LocalTime checkOut) {
        if (checkIn == null || checkOut == null) {
            return null;
        }
        
        // Handle case where check-out is next day (e.g., night shift)
        Duration duration;
        if (checkOut.isBefore(checkIn) || checkOut.equals(checkIn)) {
            // Assuming it's next day
            duration = Duration.between(checkIn, checkOut.plusHours(24));
        } else {
            duration = Duration.between(checkIn, checkOut);
        }
        
        return duration.toMinutes() / 60.0; // Convert to hours with decimal
    }

    // Mark check-in for an employee
    @Transactional
    public AttendanceResponse checkIn(AttendanceRequest request) {
        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));

        LocalDate today = request.getAttendanceDate() != null ? request.getAttendanceDate() : LocalDate.now();
        
        // Check if attendance already exists for today
        Optional<Attendance> existingAttendance = attendanceRepository.findByEmployeeAndAttendanceDate(employee, today);
        
        Attendance attendance;
        if (existingAttendance.isPresent()) {
            attendance = existingAttendance.get();
            // Update check-in if not already set
            if (attendance.getCheckInTime() == null) {
                attendance.setCheckInTime(request.getCheckInTime() != null ? request.getCheckInTime() : LocalTime.now());
                attendance.setCheckInLocation(request.getCheckInLocation());
                attendance.setLatitude(request.getLatitude());
                attendance.setLongitude(request.getLongitude());
                attendance.setLocationAddress(request.getLocationAddress());
                
                // Set status if not set
                if (attendance.getStatus() == null) {
                    attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
                }
            } else {
                throw new RuntimeException("Check-in already recorded for this date");
            }
        } else {
            // Create new attendance record
            attendance = Attendance.builder()
                    .employee(employee)
                    .attendanceDate(today)
                    .checkInTime(request.getCheckInTime() != null ? request.getCheckInTime() : LocalTime.now())
                    .status(Attendance.AttendanceStatus.PRESENT)
                    .checkInLocation(request.getCheckInLocation())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .locationAddress(request.getLocationAddress())
                    .isLate(false)
                    .isEarlyLeave(false)
                    .build();
        }

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapToResponse(savedAttendance);
    }

    // Mark check-out for an employee
    @Transactional
    public AttendanceResponse checkOut(Long employeeId, LocalDate date, LocalTime checkOutTime, String checkOutLocation, Double latitude, Double longitude) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        LocalDate attendanceDate = date != null ? date : LocalDate.now();
        LocalTime checkout = checkOutTime != null ? checkOutTime : LocalTime.now();

        Attendance attendance = attendanceRepository.findByEmployeeAndAttendanceDate(employee, attendanceDate)
                .orElseThrow(() -> new RuntimeException("No check-in found for this date. Please check in first."));

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Check-out already recorded for this date");
        }

        attendance.setCheckOutTime(checkout);
        attendance.setCheckOutLocation(checkOutLocation);
        
        // Update location if provided
        if (latitude != null) {
            attendance.setLatitude(latitude);
        }
        if (longitude != null) {
            attendance.setLongitude(longitude);
        }

        // Calculate working hours
        if (attendance.getCheckInTime() != null) {
            attendance.setWorkingHours(calculateWorkingHours(attendance.getCheckInTime(), checkout));
        }

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapToResponse(savedAttendance);
    }

    // Create or update attendance record
    @Transactional
    public AttendanceResponse createOrUpdateAttendance(AttendanceRequest request) {
        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));

        LocalDate attendanceDate = request.getAttendanceDate() != null ? request.getAttendanceDate() : LocalDate.now();
        
        Optional<Attendance> existingAttendance = attendanceRepository.findByEmployeeAndAttendanceDate(employee, attendanceDate);
        
        Attendance attendance;
        if (existingAttendance.isPresent()) {
            // Update existing attendance
            attendance = existingAttendance.get();
            if (request.getCheckInTime() != null) {
                attendance.setCheckInTime(request.getCheckInTime());
            }
            if (request.getCheckOutTime() != null) {
                attendance.setCheckOutTime(request.getCheckOutTime());
            }
            if (request.getStatus() != null) {
                attendance.setStatus(request.getStatus());
            }
            if (request.getCheckInLocation() != null) {
                attendance.setCheckInLocation(request.getCheckInLocation());
            }
            if (request.getCheckOutLocation() != null) {
                attendance.setCheckOutLocation(request.getCheckOutLocation());
            }
            if (request.getLatitude() != null) {
                attendance.setLatitude(request.getLatitude());
            }
            if (request.getLongitude() != null) {
                attendance.setLongitude(request.getLongitude());
            }
            if (request.getLocationAddress() != null) {
                attendance.setLocationAddress(request.getLocationAddress());
            }
            if (request.getNotes() != null) {
                attendance.setNotes(request.getNotes());
            }
            if (request.getIsLate() != null) {
                attendance.setIsLate(request.getIsLate());
            }
            if (request.getIsEarlyLeave() != null) {
                attendance.setIsEarlyLeave(request.getIsEarlyLeave());
            }
            if (request.getOvertimeHours() != null) {
                attendance.setOvertimeHours(request.getOvertimeHours());
            }
            
            // Recalculate working hours if times changed
            if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
                attendance.setWorkingHours(calculateWorkingHours(attendance.getCheckInTime(), attendance.getCheckOutTime()));
            }
        } else {
            // Create new attendance
            attendance = mapToAttendance(request);
            if (attendance.getAttendanceDate() == null) {
                attendance.setAttendanceDate(LocalDate.now());
            }
        }

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapToResponse(savedAttendance);
    }

    // Get all attendance records
    public List<AttendanceResponse> getAllAttendance() {
        List<Attendance> attendances = attendanceRepository.findAll();
        return attendances.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get attendance by ID
    public AttendanceResponse getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found with id: " + id));
        return mapToResponse(attendance);
    }

    // Get attendance records for an employee
    public List<AttendanceResponse> getAttendanceByEmployee(Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        
        List<Attendance> attendances = attendanceRepository.findByEmployeeOrderByAttendanceDateDesc(employee);
        return attendances.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get attendance records for an employee within date range
    public List<AttendanceResponse> getAttendanceByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        
        List<Attendance> attendances = attendanceRepository.findByEmployeeAndAttendanceDateBetween(employee, startDate, endDate);
        return attendances.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get attendance records for a specific date
    public List<AttendanceResponse> getAttendanceByDate(LocalDate date) {
        List<Attendance> attendances = attendanceRepository.findByAttendanceDate(date);
        return attendances.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get attendance records within date range
    public List<AttendanceResponse> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByAttendanceDateBetween(startDate, endDate);
        return attendances.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Delete attendance record
    @Transactional
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new RuntimeException("Attendance not found with id: " + id);
        }
        attendanceRepository.deleteById(id);
    }
}


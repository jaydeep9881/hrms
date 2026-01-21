package com.hrms.hrms.attendance.repository;

import com.hrms.hrms.attendance.model.Attendance;
import com.hrms.hrms.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    // Find attendance by employee and date
    Optional<Attendance> findByEmployeeAndAttendanceDate(Employee employee, LocalDate date);
    
    // Find all attendance records for an employee
    List<Attendance> findByEmployeeOrderByAttendanceDateDesc(Employee employee);
    
    // Find attendance records for an employee within date range
    List<Attendance> findByEmployeeAndAttendanceDateBetween(
            Employee employee, 
            LocalDate startDate, 
            LocalDate endDate
    );
    
    // Find all attendance records for a specific date
    List<Attendance> findByAttendanceDate(LocalDate date);
    
    // Find attendance records within date range
    List<Attendance> findByAttendanceDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Check if attendance exists for employee on a specific date
    boolean existsByEmployeeAndAttendanceDate(Employee employee, LocalDate date);
    
    // Count attendance by status for an employee
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee = :employee AND a.status = :status")
    Long countByEmployeeAndStatus(@Param("employee") Employee employee, @Param("status") Attendance.AttendanceStatus status);
}


package com.hrms.hrms.leave.repository;

import com.hrms.hrms.employee.model.Employee;
import com.hrms.hrms.leave.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    
    // Find all leaves for an employee
    List<Leave> findByEmployeeOrderByStartDateDesc(Employee employee);
    
    // Find leaves for an employee within date range
    List<Leave> findByEmployeeAndStartDateBetween(
            Employee employee, 
            LocalDate startDate, 
            LocalDate endDate
    );
    
    // Find leaves by status
    List<Leave> findByStatusOrderByStartDateDesc(Leave.LeaveStatus status);
    
    // Find leaves by leave type
    List<Leave> findByLeaveTypeOrderByStartDateDesc(Leave.LeaveType leaveType);
    
    // Find leaves by employee and status
    List<Leave> findByEmployeeAndStatus(Employee employee, Leave.LeaveStatus status);
    
    // Find overlapping leaves for an employee
    List<Leave> findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Employee employee, 
            LocalDate endDate, 
            LocalDate startDate
    );
    
    // Find leaves within date range
    List<Leave> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find pending leaves
    List<Leave> findByStatus(Leave.LeaveStatus status);
}


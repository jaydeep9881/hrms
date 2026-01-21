package com.hrms.hrms.salary.repository;

import com.hrms.hrms.employee.model.Employee;
import com.hrms.hrms.salary.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    
    // Find all salaries for an employee
    List<Salary> findByEmployeeOrderByPayPeriodStartDesc(Employee employee);
    
    // Find salary for an employee in a specific pay period
    Optional<Salary> findByEmployeeAndPayPeriodStartAndPayPeriodEnd(
            Employee employee, 
            LocalDate payPeriodStart, 
            LocalDate payPeriodEnd
    );
    
    // Find salaries for an employee within date range
    List<Salary> findByEmployeeAndPayPeriodStartBetween(
            Employee employee, 
            LocalDate startDate, 
            LocalDate endDate
    );
    
    // Find salaries by status
    List<Salary> findByStatusOrderByPayPeriodStartDesc(Salary.SalaryStatus status);
    
    // Find salaries within pay period date range
    List<Salary> findByPayPeriodStartBetween(LocalDate startDate, LocalDate endDate);
    
    // Find salaries by payment date
    List<Salary> findByPaymentDate(LocalDate paymentDate);
}


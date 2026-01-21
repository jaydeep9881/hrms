package com.hrms.hrms.employee.repository;

import com.hrms.hrms.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo  extends JpaRepository<Employee,Long> {

}

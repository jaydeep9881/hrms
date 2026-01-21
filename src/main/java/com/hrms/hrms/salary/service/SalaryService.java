package com.hrms.hrms.salary.service;

import com.hrms.hrms.employee.model.Employee;
import com.hrms.hrms.employee.repository.EmployeeRepo;
import com.hrms.hrms.salary.dto.SalaryRequest;
import com.hrms.hrms.salary.dto.SalaryResponse;
import com.hrms.hrms.salary.model.Salary;
import com.hrms.hrms.salary.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private EmployeeRepo employeeRepo;

    // Convert Salary entity to SalaryResponse DTO
    private SalaryResponse mapToResponse(Salary salary) {
        Employee employee = salary.getEmployee();
        return SalaryResponse.builder()
                .id(salary.getId())
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .employeeEmail(employee.getEmail())
                .payPeriodStart(salary.getPayPeriodStart())
                .payPeriodEnd(salary.getPayPeriodEnd())
                .baseSalary(salary.getBaseSalary())
                .allowances(salary.getAllowances() != null ? salary.getAllowances() : 0.0)
                .deductions(salary.getDeductions() != null ? salary.getDeductions() : 0.0)
                .tax(salary.getTax() != null ? salary.getTax() : 0.0)
                .netSalary(salary.getNetSalary())
                .status(salary.getStatus() != null ? salary.getStatus().name() : null)
                .paymentDate(salary.getPaymentDate())
                .notes(salary.getNotes())
                .createdAt(salary.getCreatedAt())
                .updatedAt(salary.getUpdatedAt())
                .build();
    }

    // Convert SalaryRequest DTO to Salary entity
    private Salary mapToSalary(SalaryRequest request) {
        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));

        Salary.SalaryStatus status = Salary.SalaryStatus.PENDING;
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                status = Salary.SalaryStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                status = Salary.SalaryStatus.PENDING;
            }
        }

        return Salary.builder()
                .employee(employee)
                .payPeriodStart(request.getPayPeriodStart())
                .payPeriodEnd(request.getPayPeriodEnd())
                .baseSalary(request.getBaseSalary())
                .allowances(request.getAllowances() != null ? request.getAllowances() : 0.0)
                .deductions(request.getDeductions() != null ? request.getDeductions() : 0.0)
                .tax(request.getTax() != null ? request.getTax() : 0.0)
                .netSalary(request.getNetSalary())
                .status(status)
                .paymentDate(request.getPaymentDate())
                .notes(request.getNotes())
                .build();
    }

    // Calculate net salary from base salary, allowances, deductions, and tax
    private Double calculateNetSalary(Double baseSalary, Double allowances, Double deductions, Double tax) {
        if (baseSalary == null) {
            return 0.0;
        }
        double totalAllowances = allowances != null ? allowances : 0.0;
        double totalDeductions = deductions != null ? deductions : 0.0;
        double totalTax = tax != null ? tax : 0.0;
        
        return baseSalary + totalAllowances - totalDeductions - totalTax;
    }

    // Create salary record
    @Transactional
    public SalaryResponse createSalary(SalaryRequest request) {
        // Calculate net salary if not provided
        if (request.getNetSalary() == null) {
            Double calculatedNet = calculateNetSalary(
                    request.getBaseSalary(),
                    request.getAllowances(),
                    request.getDeductions(),
                    request.getTax()
            );
            request.setNetSalary(calculatedNet);
        }

        Salary salary = mapToSalary(request);
        Salary savedSalary = salaryRepository.save(salary);
        return mapToResponse(savedSalary);
    }

    // Get all salaries
    public List<SalaryResponse> getAllSalaries() {
        List<Salary> salaries = salaryRepository.findAll();
        return salaries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get salary by ID
    public SalaryResponse getSalaryById(Long id) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary not found with id: " + id));
        return mapToResponse(salary);
    }

    // Get salaries for an employee
    public List<SalaryResponse> getSalariesByEmployee(Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        
        List<Salary> salaries = salaryRepository.findByEmployeeOrderByPayPeriodStartDesc(employee);
        return salaries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get salaries by status
    public List<SalaryResponse> getSalariesByStatus(String status) {
        try {
            Salary.SalaryStatus salaryStatus = Salary.SalaryStatus.valueOf(status.toUpperCase());
            List<Salary> salaries = salaryRepository.findByStatusOrderByPayPeriodStartDesc(salaryStatus);
            return salaries.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    // Update salary record
    @Transactional
    public SalaryResponse updateSalary(Long id, SalaryRequest request) {
        Salary existingSalary = salaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary not found with id: " + id));

        // Update fields from request
        if (request.getEmployeeId() != null) {
            Employee employee = employeeRepo.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));
            existingSalary.setEmployee(employee);
        }
        if (request.getPayPeriodStart() != null) {
            existingSalary.setPayPeriodStart(request.getPayPeriodStart());
        }
        if (request.getPayPeriodEnd() != null) {
            existingSalary.setPayPeriodEnd(request.getPayPeriodEnd());
        }
        if (request.getBaseSalary() != null) {
            existingSalary.setBaseSalary(request.getBaseSalary());
        }
        if (request.getAllowances() != null) {
            existingSalary.setAllowances(request.getAllowances());
        }
        if (request.getDeductions() != null) {
            existingSalary.setDeductions(request.getDeductions());
        }
        if (request.getTax() != null) {
            existingSalary.setTax(request.getTax());
        }
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                Salary.SalaryStatus status = Salary.SalaryStatus.valueOf(request.getStatus().toUpperCase());
                existingSalary.setStatus(status);
            } catch (IllegalArgumentException e) {
                // Invalid status, keep existing status
            }
        }
        if (request.getPaymentDate() != null) {
            existingSalary.setPaymentDate(request.getPaymentDate());
        }
        if (request.getNotes() != null) {
            existingSalary.setNotes(request.getNotes());
        }

        // Recalculate net salary if any financial fields changed
        if (request.getBaseSalary() != null || request.getAllowances() != null || 
            request.getDeductions() != null || request.getTax() != null) {
            Double calculatedNet = calculateNetSalary(
                    existingSalary.getBaseSalary(),
                    existingSalary.getAllowances(),
                    existingSalary.getDeductions(),
                    existingSalary.getTax()
            );
            existingSalary.setNetSalary(calculatedNet);
        } else if (request.getNetSalary() != null) {
            existingSalary.setNetSalary(request.getNetSalary());
        }

        Salary updatedSalary = salaryRepository.save(existingSalary);
        return mapToResponse(updatedSalary);
    }

    // Delete salary record
    @Transactional
    public void deleteSalary(Long id) {
        if (!salaryRepository.existsById(id)) {
            throw new RuntimeException("Salary not found with id: " + id);
        }
        salaryRepository.deleteById(id);
    }
}


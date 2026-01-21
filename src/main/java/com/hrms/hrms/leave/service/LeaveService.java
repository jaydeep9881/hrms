package com.hrms.hrms.leave.service;

import com.hrms.hrms.employee.model.Employee;
import com.hrms.hrms.employee.repository.EmployeeRepo;
import com.hrms.hrms.leave.dto.LeaveRequest;
import com.hrms.hrms.leave.dto.LeaveResponse;
import com.hrms.hrms.leave.model.Leave;
import com.hrms.hrms.leave.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepo employeeRepo;

    // Convert Leave entity to LeaveResponse DTO
    private LeaveResponse mapToResponse(Leave leave) {
        Employee employee = leave.getEmployee();
        return LeaveResponse.builder()
                .id(leave.getId())
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .employeeEmail(employee.getEmail())
                .leaveType(leave.getLeaveType() != null ? leave.getLeaveType().name() : null)
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .numberOfDays(leave.getNumberOfDays())
                .reason(leave.getReason())
                .status(leave.getStatus() != null ? leave.getStatus().name() : null)
                .approvedBy(leave.getApprovedBy())
                .rejectionReason(leave.getRejectionReason())
                .approvedDate(leave.getApprovedDate())
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .build();
    }

    // Convert LeaveRequest DTO to Leave entity
    private Leave mapToLeave(LeaveRequest request) {
        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));

        Leave.LeaveType leaveType = Leave.LeaveType.OTHER;
        if (request.getLeaveType() != null && !request.getLeaveType().isEmpty()) {
            try {
                leaveType = Leave.LeaveType.valueOf(request.getLeaveType().toUpperCase());
            } catch (IllegalArgumentException e) {
                leaveType = Leave.LeaveType.OTHER;
            }
        }

        Leave.LeaveStatus status = Leave.LeaveStatus.PENDING;
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                status = Leave.LeaveStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                status = Leave.LeaveStatus.PENDING;
            }
        }

        // Calculate number of days
        Integer numberOfDays = calculateDays(request.getStartDate(), request.getEndDate());

        return Leave.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfDays(numberOfDays)
                .reason(request.getReason())
                .status(status)
                .approvedBy(request.getApprovedBy())
                .rejectionReason(request.getRejectionReason())
                .approvedDate(request.getApprovedDate())
                .build();
    }

    // Calculate number of days between start and end date (inclusive)
    private Integer calculateDays(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }

    // Create leave request
    @Transactional
    public LeaveResponse createLeave(LeaveRequest request) {
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        // Check for overlapping leaves (optional validation)
        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));
        
        List<Leave> overlappingLeaves = leaveRepository.findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employee, 
                request.getEndDate(), 
                request.getStartDate()
        );
        
        // Filter out cancelled and rejected leaves
        overlappingLeaves = overlappingLeaves.stream()
                .filter(leave -> leave.getStatus() != Leave.LeaveStatus.CANCELLED 
                        && leave.getStatus() != Leave.LeaveStatus.REJECTED)
                .collect(Collectors.toList());
        
        if (!overlappingLeaves.isEmpty()) {
            throw new RuntimeException("Leave request overlaps with existing approved or pending leave");
        }

        Leave leave = mapToLeave(request);
        Leave savedLeave = leaveRepository.save(leave);
        return mapToResponse(savedLeave);
    }

    // Get all leaves
    public List<LeaveResponse> getAllLeaves() {
        List<Leave> leaves = leaveRepository.findAll();
        return leaves.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get leave by ID
    public LeaveResponse getLeaveById(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));
        return mapToResponse(leave);
    }

    // Get leaves for an employee
    public List<LeaveResponse> getLeavesByEmployee(Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        
        List<Leave> leaves = leaveRepository.findByEmployeeOrderByStartDateDesc(employee);
        return leaves.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get leaves by status
    public List<LeaveResponse> getLeavesByStatus(String status) {
        try {
            Leave.LeaveStatus leaveStatus = Leave.LeaveStatus.valueOf(status.toUpperCase());
            List<Leave> leaves = leaveRepository.findByStatusOrderByStartDateDesc(leaveStatus);
            return leaves.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    // Get leaves by leave type
    public List<LeaveResponse> getLeavesByType(String leaveType) {
        try {
            Leave.LeaveType type = Leave.LeaveType.valueOf(leaveType.toUpperCase());
            List<Leave> leaves = leaveRepository.findByLeaveTypeOrderByStartDateDesc(type);
            return leaves.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid leave type: " + leaveType);
        }
    }

    // Approve leave
    @Transactional
    public LeaveResponse approveLeave(Long id, String approvedBy) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));
        
        if (leave.getStatus() != Leave.LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leaves can be approved");
        }
        
        leave.setStatus(Leave.LeaveStatus.APPROVED);
        leave.setApprovedBy(approvedBy);
        leave.setApprovedDate(LocalDate.now());
        
        Leave updatedLeave = leaveRepository.save(leave);
        return mapToResponse(updatedLeave);
    }

    // Reject leave
    @Transactional
    public LeaveResponse rejectLeave(Long id, String rejectedBy, String rejectionReason) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));
        
        if (leave.getStatus() != Leave.LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leaves can be rejected");
        }
        
        leave.setStatus(Leave.LeaveStatus.REJECTED);
        leave.setApprovedBy(rejectedBy);
        leave.setRejectionReason(rejectionReason);
        leave.setApprovedDate(LocalDate.now());
        
        Leave updatedLeave = leaveRepository.save(leave);
        return mapToResponse(updatedLeave);
    }

    // Cancel leave
    @Transactional
    public LeaveResponse cancelLeave(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));
        
        if (leave.getStatus() == Leave.LeaveStatus.APPROVED && leave.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot cancel an approved leave that has already started");
        }
        
        leave.setStatus(Leave.LeaveStatus.CANCELLED);
        
        Leave updatedLeave = leaveRepository.save(leave);
        return mapToResponse(updatedLeave);
    }

    // Update leave record
    @Transactional
    public LeaveResponse updateLeave(Long id, LeaveRequest request) {
        Leave existingLeave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));

        // Only allow updates to pending leaves
        if (existingLeave.getStatus() != Leave.LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leaves can be updated");
        }

        // Update fields from request
        if (request.getEmployeeId() != null) {
            Employee employee = employeeRepo.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));
            existingLeave.setEmployee(employee);
        }
        if (request.getLeaveType() != null && !request.getLeaveType().isEmpty()) {
            try {
                Leave.LeaveType leaveType = Leave.LeaveType.valueOf(request.getLeaveType().toUpperCase());
                existingLeave.setLeaveType(leaveType);
            } catch (IllegalArgumentException e) {
                // Invalid type, keep existing
            }
        }
        if (request.getStartDate() != null) {
            existingLeave.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            existingLeave.setEndDate(request.getEndDate());
        }
        if (request.getReason() != null) {
            existingLeave.setReason(request.getReason());
        }
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                Leave.LeaveStatus status = Leave.LeaveStatus.valueOf(request.getStatus().toUpperCase());
                existingLeave.setStatus(status);
            } catch (IllegalArgumentException e) {
                // Invalid status, keep existing
            }
        }

        // Recalculate number of days if dates changed
        if (request.getStartDate() != null || request.getEndDate() != null) {
            Integer numberOfDays = calculateDays(existingLeave.getStartDate(), existingLeave.getEndDate());
            existingLeave.setNumberOfDays(numberOfDays);
        }

        Leave updatedLeave = leaveRepository.save(existingLeave);
        return mapToResponse(updatedLeave);
    }

    // Delete leave record
    @Transactional
    public void deleteLeave(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));
        
        // Only allow deletion of pending or cancelled leaves
        if (leave.getStatus() == Leave.LeaveStatus.APPROVED || leave.getStatus() == Leave.LeaveStatus.REJECTED) {
            throw new RuntimeException("Cannot delete approved or rejected leaves");
        }
        
        leaveRepository.deleteById(id);
    }
}


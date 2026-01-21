package com.hrms.hrms.reimbursement.service;

import com.hrms.hrms.employee.model.Employee;
import com.hrms.hrms.employee.repository.EmployeeRepo;
import com.hrms.hrms.reimbursement.dto.ReimbursementRequest;
import com.hrms.hrms.reimbursement.dto.ReimbursementResponse;
import com.hrms.hrms.reimbursement.model.Reimbursement;
import com.hrms.hrms.reimbursement.repository.ReimbursementRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReimbursementService {
    @Autowired
    private ReimbursementRepo reimbursementRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    // Convert Reimbursement model to ReimbursementResponse DTO
    private ReimbursementResponse mapToResponse(Reimbursement reimbursement) {
        return ReimbursementResponse.builder()
                .id(reimbursement.getId())
                .date(reimbursement.getDate())
                .description(reimbursement.getDescription())
                .employeeId(reimbursement.getEmployee() != null ? reimbursement.getEmployee().getId() : null)
                .employeeName(reimbursement.getEmployee() != null 
                    ? reimbursement.getEmployee().getFirstName() + " " + reimbursement.getEmployee().getLastName() 
                    : null)
                .employeeEmail(reimbursement.getEmployee() != null ? reimbursement.getEmployee().getEmail() : null)
                .amount(reimbursement.getAmount())
                .type(reimbursement.getType())
                .imageName(reimbursement.getImageName())
                .imageType(reimbursement.getImageType())
                .status(reimbursement.getStatus() != null ? reimbursement.getStatus().name() : null)
                .createdAt(reimbursement.getCreatedAt())
                .updatedAt(reimbursement.getUpdatedAt())
                .build();
    }

    // Convert ReimbursementRequest DTO to Reimbursement model
    private Reimbursement mapToReimbursement(ReimbursementRequest request) {
        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));

        Reimbursement.ReimbursementStatus status = Reimbursement.ReimbursementStatus.PENDING;
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                status = Reimbursement.ReimbursementStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                status = Reimbursement.ReimbursementStatus.PENDING;
            }
        }

        return Reimbursement.builder()
                .date(request.getDate())
                .description(request.getDescription())
                .employee(employee)
                .amount(request.getAmount())
                .type(request.getType())
                .status(status)
                .build();
    }

    public List<ReimbursementResponse> getAllReimbursements() {
        List<Reimbursement> reimbursements = reimbursementRepo.findAll();
        return reimbursements.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReimbursementResponse getReimbursementById(Long id) {
        Reimbursement reimbursement = reimbursementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reimbursement not found with id: " + id));
        return mapToResponse(reimbursement);
    }

    public ReimbursementResponse createReimbursement(ReimbursementRequest request, MultipartFile image) throws IOException {
        Reimbursement reimbursement = mapToReimbursement(request);

        // Handle image upload if provided
        if (image != null && !image.isEmpty()) {
            reimbursement.setImageName(image.getOriginalFilename());
            reimbursement.setImageType(image.getContentType());
            reimbursement.setImageData(image.getBytes());
        }

        Reimbursement savedReimbursement = reimbursementRepo.save(reimbursement);
        return mapToResponse(savedReimbursement);
    }

    public ReimbursementResponse updateReimbursement(Long id, ReimbursementRequest request, MultipartFile image) throws IOException {
        Reimbursement existingReimbursement = reimbursementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reimbursement not found with id: " + id));

        // Update fields from request
        if (request.getDate() != null) {
            existingReimbursement.setDate(request.getDate());
        }
        if (request.getDescription() != null) {
            existingReimbursement.setDescription(request.getDescription());
        }
        if (request.getEmployeeId() != null) {
            Employee employee = employeeRepo.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));
            existingReimbursement.setEmployee(employee);
        }
        if (request.getAmount() != null) {
            existingReimbursement.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            existingReimbursement.setType(request.getType());
        }
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                Reimbursement.ReimbursementStatus status = Reimbursement.ReimbursementStatus.valueOf(request.getStatus().toUpperCase());
                existingReimbursement.setStatus(status);
            } catch (IllegalArgumentException e) {
                // Invalid status, keep existing status
            }
        }

        // Update image if provided
        if (image != null && !image.isEmpty()) {
            existingReimbursement.setImageName(image.getOriginalFilename());
            existingReimbursement.setImageType(image.getContentType());
            existingReimbursement.setImageData(image.getBytes());
        }

        Reimbursement updatedReimbursement = reimbursementRepo.save(existingReimbursement);
        return mapToResponse(updatedReimbursement);
    }

    public void deleteReimbursement(Long id) {
        if (!reimbursementRepo.existsById(id)) {
            throw new RuntimeException("Reimbursement not found with id: " + id);
        }
        reimbursementRepo.deleteById(id);
    }
}


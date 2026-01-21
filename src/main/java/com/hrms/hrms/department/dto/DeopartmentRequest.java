package com.hrms.hrms.department.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;
@Data
@Builder
public class DeopartmentRequest {
    private Long id;
    private String name;
    private LocalDateTime createdAt ;
    private LocalDateTime updatedAt;

}

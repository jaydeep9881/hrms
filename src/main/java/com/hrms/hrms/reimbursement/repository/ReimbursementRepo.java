package com.hrms.hrms.reimbursement.repository;

import com.hrms.hrms.reimbursement.model.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReimbursementRepo extends JpaRepository<Reimbursement, Long> {
}


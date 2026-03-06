package com.hrms.hrms.user.repositary;

import com.hrms.hrms.user.controller.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<Users, Long> {
    Users findByUsername(String username);



}

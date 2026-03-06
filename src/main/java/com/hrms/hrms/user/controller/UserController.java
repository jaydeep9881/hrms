package com.hrms.hrms.user.controller;

import com.hrms.hrms.user.DTO.UserRequest;
import com.hrms.hrms.user.DTO.UserResponse;
import com.hrms.hrms.user.controller.Users;
import com.hrms.hrms.user.service.UserSerivice;
import com.hrms.hrms.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    private UserSerivice serivice;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public UserResponse register(@RequestBody UserRequest user){
        return serivice.addUser(user);
    }
    @GetMapping("/users")
    private List<UserResponse> getAllUsers(){
        return serivice.getAllUsers();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest user) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            UserDetails userDetails= serivice.loadUserByUsername(user.getUsername());
            String role = userDetails.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();
            // Get employeeId from database not from request
             System.out.println(userDetails.toString());
             Long employeeId = serivice.getEmployeeIdByUsername(user.getUsername());
            String token= jwtUtil.generateToken(userDetails.getUsername(),role,employeeId);
            return new ResponseEntity<>(token,HttpStatus.OK);
        }catch (Exception e) {
            return  new ResponseEntity<>("no usert found",HttpStatus.UNAUTHORIZED);
        }

    }
    // Update user
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest user) {
        return ResponseEntity.ok(serivice.updateUser(id, user));
    }

    // Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        serivice.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}


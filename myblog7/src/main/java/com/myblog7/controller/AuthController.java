package com.myblog7.controller;

import com.myblog7.entity.User;
import com.myblog7.payload.LoginDto;
import com.myblog7.payload.SignupDto;
import com.myblog7.repository.RoleRepository;
import com.myblog7.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    //http://localhost:8080/api/auth/signin
    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return new ResponseEntity<>("User Signed-in Successfully!",HttpStatus.OK);
    }

    //http://localhost:8080/api/auth/signup
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupDto signupDto){

        Boolean emailExist = userRepo.existsByEmail(signupDto.getEmail());
        if(emailExist){
            return new ResponseEntity<>("Email Id Exist", HttpStatus.BAD_REQUEST);
        }
        Boolean usernameExist = userRepo.existsByUsername(signupDto.getUsername());
        if(usernameExist){
            return  new ResponseEntity<>("Username Exist",HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(signupDto.getName());
        user.setEmail(signupDto.getEmail());
        user.setUsername(signupDto.getUsername());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        userRepo.save(user);
        return new ResponseEntity<>("User is Registered", HttpStatus.CREATED);
    }
}

package org.example.testapi.auth;


import org.example.testapi.config.jwt.JwtResponse;
import org.example.testapi.config.jwt.JwtService;
import org.example.testapi.user.User;
import org.example.testapi.user.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserServiceInterface userDetailsService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserServiceInterface userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> currentUser = userDetailsService.findByUserEmail(user.getEmail());

        if (!currentUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Người dùng không tìm thấy");
        }

        if (!currentUser.get().getActive()) {
            return ResponseEntity.status(403).body("Người dùng bị ban vĩnh viễn");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtService.generateTokenLogin(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();


            return ResponseEntity.ok(new JwtResponse(currentUser.get().getId(), jwt, userDetails.getUsername(), currentUser.get().getName(), currentUser.get().getRoles().toString()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thông tin đăng nhập không chính xác");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

}



package com.revelvol.JWT.controller;

//rest api to allow creation and delete of user that need to be authenticated


import com.revelvol.JWT.exception.InvalidPasswordException;
import com.revelvol.JWT.exception.UserAlreadyExistsException;
import com.revelvol.JWT.exception.UserNotFoundException;
import com.revelvol.JWT.model.User;
import com.revelvol.JWT.repository.UserRepository;
import com.revelvol.JWT.request.AuthenticationRequest;
import com.revelvol.JWT.request.RegisterRequest;
import com.revelvol.JWT.response.ApiResponse;
import com.revelvol.JWT.response.UserResponse;
import com.revelvol.JWT.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;

    }


    @GetMapping
    public ResponseEntity<ApiResponse>  validateToken(
            @RequestHeader HttpHeaders headers
    ) {
        String authHeader = headers.getFirst("Authorization");
        String token = authHeader.substring(7);
        ApiResponse response;
        try {
            response = authenticationService.getUserData(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response = new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        ApiResponse response;
        try {
            response = authenticationService.register(request);
            return ResponseEntity.ok(response);

        } catch (UserAlreadyExistsException e) {

            response = new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.badRequest().body(response);


        }


    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        ApiResponse response;

        try {
            response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException |InvalidPasswordException  e) {
            response = new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }

}

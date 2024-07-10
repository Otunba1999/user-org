package com.otunba.controllers;

import com.otunba.dtos.*;
import com.otunba.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userdto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ErrorDto> errors = getErrorDtos(bindingResult);
            return ResponseEntity.badRequest().body(new ErrorResponse(errors));
        }

        try {
            var savedUser = authService.registerUser(userdto);
            var location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{userId}")
                    .buildAndExpand(savedUser.getUserId())
                    .toUri();
            var response = authService.loginUser(new LoginRequest(userdto.getEmail(), userdto.getPassword()));
            return ResponseEntity.created(location).body(
                    Response.builder()
                            .status("Success")
                            .message("Registration successful")
                            .data(response.getData())
                            .user(response.getUser())
                            .build()
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .status("Bad request")
                            .message("Registration unsuccessful")
                            .statusCode(400)
                            .build()
            );
        }
    }


    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUser( @RequestBody LoginRequest request) {
        try {
            var response = authService.loginUser(request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(
                    Response.builder()
                            .status("Bad request")
                            .message(e.getMessage())
                            .statusCode(401)
                            .build()
            );
        }
    }

    public static List<ErrorDto> getErrorDtos(BindingResult bindingResult) {
        List<ErrorDto> errors =
                (List<ErrorDto>) bindingResult.getAllErrors().stream()
                        .map(err -> {
                            String fieldName = ((FieldError) err).getField();
                            return ErrorDto.builder()
                                    .field(fieldName)
                                    .message(err.getDefaultMessage())
                                    .build();
                        }).toList();
        return errors;
    }
}

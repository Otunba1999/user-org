package com.otunba.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String userId;
    @NotNull(message = "firstname cannot be null, firstname is required")
    @NotBlank(message = "firstname cannot be blank, firstname is required")
    private String firstName;
    @NotNull(message = "lastname cannot be null, lastname is required")
    @NotBlank(message = "lastname cannot be blank, lastname is required")
    private String lastName;
    @Email(message = "email must be valid")
    @NotNull(message = "email cannot be null, email is required")
    @NotBlank(message = "email cannot be blank, email is required")
    @Column(unique = true)
    private String email;
    @NotNull(message = "password cannot be null, password is required")
    @NotBlank(message = "password cannot be blank, password is required")
    private String password;
    private String phone;
}

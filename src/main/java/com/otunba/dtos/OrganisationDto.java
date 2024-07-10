package com.otunba.dtos;

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
public class OrganisationDto {
    private String orgId;
    @NotNull(message = "Organisation name is a required field")
    @NotBlank(message = "name cannot be blank, name is required")
    private String name;
    private String description;
}

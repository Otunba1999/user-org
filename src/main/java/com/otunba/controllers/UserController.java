package com.otunba.controllers;

import com.otunba.dtos.*;
import com.otunba.services.OrganisationService;
import com.otunba.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

import static com.otunba.controllers.AuthController.getErrorDtos;
import static com.otunba.mappers.UserMapper.toOrganisationDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final OrganisationService organisationService;

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String userId) {
        try {
            var user = userService.findByUserId(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    Response.builder()
                            .statusCode(401)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping(path ="/organisations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOrganisations() {
        var organisations = organisationService.getUserOrganisation();
        return ResponseEntity.ok(organisations);
    }

    @GetMapping("organisations/{orgId}")
    public ResponseEntity<?> getOrganisationById(@PathVariable("orgId") String orgId) {
        return ResponseEntity.ok(organisationService.getOrganisationById(orgId));
    }

    @PostMapping("/organisations")
    public ResponseEntity<?> addOrganisation(@Valid @RequestBody OrganisationDto organisationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ErrorDto> errors = getErrorDtos(bindingResult);
            return ResponseEntity.badRequest().body(new ErrorResponse(errors));
        }
        try {
            var org = organisationService.createOrganisation(organisationDto);
            var location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(org.getOrgId())
                    .toUri();
            return ResponseEntity.created(location).body(
                    Response.builder()
                            .status("Success")
                            .message("Organisation created successfully")
                            .data(Map.of("organisation", org))
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .status("Success")
                            .message("Client error")
                            .statusCode(400)
                            .build()
            );
        }
    }

    @PostMapping("/organisations/{orgId}/users")
    public ResponseEntity<?> addUserToOrganisation(@PathVariable("orgId") String orgId,  @RequestBody AddUserRequest request) {
        var response = userService.addUserToOrganisation(orgId, request.getUserId());
        try {
            return ResponseEntity.ok(response);
        }catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
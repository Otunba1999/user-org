package com.otunba.services.impl;

import com.otunba.dtos.OrganisationDto;
import com.otunba.dtos.Response;
import com.otunba.mappers.UserMapper;
import com.otunba.models.Organisation;
import com.otunba.repository.OrganisationRepository;
import com.otunba.repository.UserRepository;
import com.otunba.services.OrganisationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.otunba.mappers.UserMapper.toOrganisationDto;
import static com.otunba.services.impl.UserServiceImpl.getUsername;

@Service
@RequiredArgsConstructor
public class OrganisationServiceImpl implements OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;

    @Override
    public Response getUserOrganisation() {
        var username = getUsername();
        Set<OrganisationDto> organisationDtos = organisationRepository.findByUserEmail(username).stream().map(
                UserMapper::toOrganisationDto
        ).collect(Collectors.toSet());
        return Response.builder()
                .status("success")
                .message("Here are the organisations you currently belong to")
                .data(Map.of("organisations", organisationDtos))
                .build();

    }

    @Override
    public Response getOrganisationById(String orgId) {
        var org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid organisation id"));
            return Response.builder()
                    .status("success")
                    .message("organisation is found")
                    .data(Map.of("organisations", toOrganisationDto(org)))
                    .build();


    }

    @Override
    @Transactional
    public OrganisationDto createOrganisation(OrganisationDto organisationDto) {
        var user = userRepository.findByEmail(getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
        var organisation = new Organisation();
        organisation.setName(organisationDto.getName());
        organisation.setDescription(organisationDto.getDescription());
        organisation.getUsers().add(user);
         var saved  = organisationRepository.save(organisation);
        return toOrganisationDto(saved);
    }
}

package com.otunba.services;

import com.otunba.dtos.OrganisationDto;
import com.otunba.dtos.Response;

import java.util.Set;

public interface OrganisationService {
    Response getUserOrganisation();
    Response getOrganisationById(String orgId);
    OrganisationDto createOrganisation(OrganisationDto organisationDto);
}

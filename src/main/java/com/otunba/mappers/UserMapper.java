package com.otunba.mappers;

import com.otunba.dtos.OrganisationDto;
import com.otunba.dtos.UserDto;
import com.otunba.models.Organisation;
import com.otunba.models.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userId(user.getUserId())
                .build();
    }
    public static OrganisationDto toOrganisationDto(Organisation organisation) {
        return OrganisationDto.builder()
                .orgId(organisation.getOrgId())
                .name(organisation.getName())
                .description(organisation.getDescription())
                .build();
    }
}

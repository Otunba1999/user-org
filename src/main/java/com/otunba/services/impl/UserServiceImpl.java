package com.otunba.services.impl;

import com.otunba.dtos.Response;
import com.otunba.dtos.UserDto;
import com.otunba.models.User;
import com.otunba.repository.OrganisationRepository;
import com.otunba.repository.UserRepository;
import com.otunba.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.otunba.mappers.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> {throw new UsernameNotFoundException("User not found");});
    }

    @Override
    public Response findByUserId(String userId) {
        var authenticatedUser = userRepository.findByEmail(getUsername())
                .orElseThrow(() -> {
                    throw new IllegalStateException("Invalid user email");
                });

        var user = getUser(userId);
        if(canUserAccessAnotherUser(authenticatedUser.getUserId(), user.getUserId()))
        return Response.builder()
                .status("success")
                .message("Access granted")
                .user(toUserDto(user))
                .build();
        else if(authenticatedUser.getUserId().equals(userId))
            return Response.builder()
                    .status("success")
                    .message("Access granted")
                    .user(toUserDto(user))
                    .build();

        throw new IllegalStateException("Access denied: User do not belong to same organisation");
    }

    @Override
    public Response addUserToOrganisation(String orgId, String userid) {
        var organisation = organisationRepository.findById(orgId)
                .orElseThrow(() -> new IllegalStateException("Invalid organisation id"));
        var user = getUser(userid);
        organisation.getUsers().add(user);
        organisationRepository.save(organisation);
        return Response.builder()
                .status("success")
                .message("User added to Organisation successfully")
                .build();
    }

    private User getUser(String userId) {
        return userRepository.findById(userId).
                orElseThrow(() -> {
                    throw new IllegalStateException("Invalid user id");
                });
    }

    public boolean canUserAccessAnotherUser(String requestedUserId, String userId) {
        return organisationRepository.areUserInSameOrganisation(requestedUserId, userId);
    }

    public static String getUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}

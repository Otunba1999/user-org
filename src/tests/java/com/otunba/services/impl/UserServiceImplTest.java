package com.otunba.services.impl;

import com.otunba.repository.OrganisationRepository;
import com.otunba.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.otunba.TestModels.getUser1;
import static com.otunba.TestModels.getUser2;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrganisationRepository organisationRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Spy
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void test_that_user_cant_view_other_user_record_if_not_in_same_organisation() {
        var authenticatedUserEmail = "test@otunba.com";
        var authenticatedUserId = "testId";
        var otherUserId = "otherId";
        var authenticatedUser = getUser1();
        authenticatedUser.setUserId(authenticatedUserId);
        var otherUser = getUser2();
        otherUser.setUserId(otherUserId);
        when(authentication.getName()).thenReturn(authenticatedUser.getEmail());
        when(userRepository.findByEmail(authenticatedUser.getEmail())).thenReturn(Optional.of(authenticatedUser));
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));
        doReturn(false).when(userService).canUserAccessAnotherUser(authenticatedUserId, otherUserId);

        Exception exception = assertThrows(IllegalStateException.class,
                () -> userService.findByUserId(otherUserId));
        assertEquals("Access denied: User do not belong to same organisation", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(authenticatedUser.getEmail());
        verify(userRepository, times(1)).findById(otherUserId);
        verify(userService, times(1)).canUserAccessAnotherUser(authenticatedUserId, otherUserId);

    }

}
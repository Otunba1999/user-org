package com.otunba.services;

import com.otunba.dtos.LoginRequest;
import com.otunba.dtos.Response;
import com.otunba.dtos.UserDto;
import com.otunba.models.Organisation;
import com.otunba.models.User;
import com.otunba.repository.OrganisationRepository;
import com.otunba.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrganisationRepository organisationRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager manager;
    @Mock
    private JwtTokenService jwtTokenService;
    @InjectMocks
    private AuthService authService;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }

    @Test
    public void test_that_organisation_is_created_with_default_name_user_firstname_plus_organisation(){
        var userDto = UserDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password")
                .build();
        authService.saveUser(userDto);
        ArgumentCaptor<Organisation> argumentCaptor = ArgumentCaptor.forClass(Organisation.class);
        verify(organisationRepository).save(argumentCaptor.capture());
        var saveOrg = argumentCaptor.getValue();
        assertEquals("John's Organisation", saveOrg.getName());
    }

    @Test
    public void user_wont_be_created_if_email_already_exist(){
        var existingEmail = "john.doe@example.com";
        UserDto userDto = UserDto.builder()
                .email(existingEmail)
                .password("password")
                .build();
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(new User()));
        try {
            authService.registerUser(userDto);
            fail("Expected IllegalStateException");
        }catch (IllegalStateException e){
            assertEquals(String.format("User already exist with email: %s", existingEmail), e.getMessage());
        }
    }
    @Test
    public void test_that_user_will_be_login_Successful_If_correct_credentials_is_provided(){
        String email = "johndoe@example.com";
        String password = "password";
        LoginRequest request = new LoginRequest(email, password);
        User user = new User();
        user.setEmail("johndoe@example.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenService.generateToken(authentication)).thenReturn("mockToken");
        Response response = authService.loginUser(request);
        assertEquals("success", response.getStatus());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getData().get("accessToken"));
        assertEquals("mockToken", response.getData().get("accessToken"));
        assertEquals(user.getEmail(), response.getUser().getEmail());

    }
    @Test
    public void test_user_wont_be_logged_in_if_credentials_is_not_correct(){
        String email = "johndoe@example.com";
        String password = "password";
        LoginRequest request = new LoginRequest(email, password);

        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("Authentication failed") {
        });
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> authService.loginUser(request));
        assertEquals("Authentication failed", thrown.getMessage());
    }


}
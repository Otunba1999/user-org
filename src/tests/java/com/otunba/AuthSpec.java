package com.otunba;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otunba.controllers.AuthController;
import com.otunba.controllers.UserController;
import com.otunba.dtos.ErrorResponse;
import com.otunba.dtos.LoginRequest;
import com.otunba.dtos.Response;
import com.otunba.dtos.UserDto;
import com.otunba.models.User;
import com.otunba.services.AuthService;
import com.otunba.services.JwtTokenService;
import com.otunba.services.OrganisationService;
import com.otunba.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;

import static com.otunba.mappers.UserMapper.toUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = {AuthController.class, UserController.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthSpec {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtTokenService jwtTokenBean;
    @MockBean
    private UserService userService;
    @MockBean
    private OrganisationService organisationService;
    @Autowired
    private ObjectMapper objectMapper;
    private UserDto userDto;
    private User user;
    private Response response;

    @Mock
    private AuthService authServiceMock;
    @InjectMocks
    private AuthController authController;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setEmail("john@example.com");
        userDto.setPassword("password");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");

        user = new User();
        user.setUserId("123");
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setFirstname(userDto.getFirstName());
        user.setLastname(userDto.getLastName());

        response = new Response();
        response.setStatus("Success");
        response.setMessage("Registration successful");
        response.setUser(toUserDto(user));
        response.setData(Map.of("accessToken", "access_token"));
        MockitoAnnotations.openMocks(this);
    }

    @Test

    public void user_cannot_view_Other_user_info_if_not_in_same_org() throws Exception {
        var userId = "123";
        when(userService.findByUserId(any(String.class)))
                .thenThrow(new IllegalStateException("Access denied: User do not belong to same organisation"));
        mockMvc.perform(get("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value("Access denied: User do not belong to same organisation"));

    }

    @Test
    public void test_user_will_be_registered_successfully() throws Exception {
        when(authService.registerUser(any(UserDto.class))).thenReturn(user);
        when(authService.loginUser(any(LoginRequest.class))).thenReturn(response);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.data.accessToken").value("access_token"))
                .andExpect(jsonPath("$.user.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.user.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.user.lastName").value(userDto.getLastName()));

    }

    @Test
    public void test_request_will_fail_if_required_fields_are_null()  {
        UserDto userDto = new UserDto();
        userDto.setEmail("");
        userDto.setPassword("password");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError("email")).thenReturn(new FieldError("UserDto", "email", "Email field is required"));
        ResponseEntity<?> response = authController.registerUser(userDto, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);

    }
    @Test
    public void test_to_login_user_successfully_if_credentials_are_valid() throws Exception {
        String email = "john.doe@example.com";
        String password = "password";
        LoginRequest loginRequest = new LoginRequest(email, password);
        User user = new User();
        user.setEmail(email);
        Response expectedResponse = Response.builder()
                .status("success")
                .message("Login successful")
                .data(Map.of("accessToken", "mockedToken"))
                .user(toUserDto(user))
                .build();
        when(authService.loginUser(any(LoginRequest.class))).thenReturn(expectedResponse);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").value("mockedToken"))
                .andExpect(jsonPath("$.user.email").value(email));

    }
    @Test
    public void test_to_login_user_ussuccessfully_if_credentials_are_invalid() throws Exception {
        String email = "john.doe@example.com";
        String password = "password";
        LoginRequest loginRequest = new LoginRequest(email, password);
        when(authService.loginUser(any(LoginRequest.class)))
                .thenThrow(new IllegalStateException("Authentication failed"));
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Authentication failed"))
                .andExpect(jsonPath("$.statusCode").value(401));
    }

    @Test
    public void response_will_be_bad_request_if_email_already_exists(){
        var existingEmail = "john.doe@example.com";
        UserDto userDto = UserDto.builder()
                .email(existingEmail)
                .password("password")
                .build();
        when(authService.registerUser(userDto)).thenThrow(new IllegalStateException("User already exists with email: " + existingEmail));
        ResponseEntity<?> response = authController.registerUser(userDto, mockBindingResult());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Response responseError = (Response) response.getBody();
        assert responseError != null;
        assertEquals("Registration unsuccessful", responseError.getMessage());
    }

    private BindingResult mockBindingResult() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false); // No validation errors
        return bindingResult;
    }


}
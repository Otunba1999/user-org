package com.otunba.services;

import com.otunba.dtos.LoginRequest;
import com.otunba.dtos.Response;
import com.otunba.dtos.UserDto;
import com.otunba.models.Organisation;
import com.otunba.models.User;
import com.otunba.repository.OrganisationRepository;
import com.otunba.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.otunba.mappers.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganisationRepository orgRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;

    public User registerUser(UserDto userdto) {
        var user = userRepository.findByEmail(userdto.getEmail());
        if(user.isPresent()) {
            throw new IllegalStateException(String.format("User already exist with email: %s", userdto.getEmail()));
        }

        return saveUser(userdto);
    }

    public Response loginUser(LoginRequest request) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            User user = (User) authentication.getPrincipal();
            String token = tokenService.generateToken(authentication);
            return Response.builder()
                    .status("success")
                    .message("Login successful")
                    .data(Map.of("accessToken", token))
                    .user(toUserDto(user))
                    .build();
        }catch (AuthenticationException e){
            throw new IllegalStateException("Authentication failed");
        }
    }

    @Transactional
    public User saveUser(UserDto userdto) {
        var user = new User();
                user.setEmail(userdto.getEmail());
                user.setPassword(passwordEncoder.encode(userdto.getPassword()));
                user.setFirstname(userdto.getFirstName());
                user.setLastname(userdto.getLastName());
                user.setPhone(userdto.getPhone());
        var savedUser = userRepository.save(user);
        var org = new Organisation();
                org.setName(user.getFirstname() + "'s Organisation");
                org.setDescription("some description");
        org.getUsers().add(user);

        orgRepository.save(org);

        return savedUser;
    }
}

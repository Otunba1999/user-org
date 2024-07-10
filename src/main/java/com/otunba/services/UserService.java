package com.otunba.services;

import com.otunba.dtos.Response;
import com.otunba.dtos.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    Response findByUserId(String userId);

    Response addUserToOrganisation(String orgId, String userid);


//    UserDto registerUser(UserDto userdto);
//
//    Response loginUser(UserDto userdto);
}

package com.otunba;

import com.otunba.models.Organisation;
import com.otunba.models.User;

public class TestModels {
    public static User getUser1(){
        User user = new User();
        user.setEmail("test1@otunba.com");
        user.setPassword("password");
        user.setFirstname("otunba1");
        user.setLastname("otunba1");
        return user;
    }
    public static User getUser2(){
        User user = new User();
        user.setEmail("test2@otunba.com");
        user.setPassword("password");
        user.setFirstname("otunba2");
        user.setLastname("otunba2");
        return user;
    }
    public static User getUser3(){
        User user = new User();
        user.setEmail("test3@otunba.com");
        user.setPassword("password");
        user.setFirstname("otunba3");
        user.setLastname("otunba3");
        return user;
    }
    public static Organisation getOrg(){
        Organisation organisation = new Organisation();
        organisation.setName("OTUNBA");
        organisation.setDescription("OTUNBA organisation");
        return organisation;
    }
}

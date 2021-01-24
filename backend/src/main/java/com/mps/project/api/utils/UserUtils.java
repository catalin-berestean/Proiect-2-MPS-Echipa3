package com.mps.project.api.utils;

import com.mps.project.api.model.User;

public class UserUtils {

    private UserUtils() {
        // utils class
    }
    public static User getUserJson(User userDb) {
        User user = new User();
        user.setRole(userDb.getRole());
        user.setOrganization(userDb.getOrganization());
        user.setUsername(userDb.getUsername());
        user.setFirstName(userDb.getFirstName());
        user.setLastName(userDb.getLastName());

        return user;
    }
}

package com.spotify_final_project.mappers;

import com.spotify_final_project.dto.register.UserRegisterRequest;
import com.spotify_final_project.model.User;

public class UserMapper {
    public static User mapToEntity(UserRegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthDate(request.getBirthDate());
        return user;
    }
}

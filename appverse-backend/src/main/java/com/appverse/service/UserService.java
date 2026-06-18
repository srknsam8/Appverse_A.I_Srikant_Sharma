package com.appverse.service;

import com.appverse.dto.UserDTO;
import com.appverse.entity.User;
import com.appverse.dto.LoginDTO;

public interface UserService {
    User registerUser(UserDTO userDTO);
    User getUserByEmail(String email);
    String loginUser(LoginDTO loginDTO);
}
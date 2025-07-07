package org.example.core.dto;


import lombok.Data;
import org.example.core.model.enums.UserType;

import java.util.List;


@Data
public class CreateUserRequest {

    private String username;
    private String password;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private List<String> roles;
    private UserType userType;


}

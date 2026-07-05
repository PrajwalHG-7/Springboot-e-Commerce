package com.app.ecom.dto;

import com.app.ecom.models.UserRoles;

public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRoles role = UserRoles.CUSTOMER;
}
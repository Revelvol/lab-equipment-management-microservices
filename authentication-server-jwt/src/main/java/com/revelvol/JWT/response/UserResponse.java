package com.revelvol.JWT.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revelvol.JWT.model.Role;

import java.util.HashSet;
import java.util.Set;

public class UserResponse extends ApiResponse {
    private int id;
    private String email;
    private Set<Role> userRoles = new HashSet<>();


    public UserResponse(int statusCode, String message) {
        super(statusCode, message);
    }

    public UserResponse(int statusCode, String message, int id, String email, Set<Role> userRoles) {
        super(statusCode, message);
        this.id = id;
        this.email = email;
        this.userRoles = userRoles;

    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        addData("id", id);
    }

    @JsonIgnore
    public String getEmail() {
        return email;

    }

    public void setEmail(String email) {
        this.email = email;
        addData("email", email);
    }

    @JsonIgnore
    public Set<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<Role> userRoles) {
        this.userRoles = userRoles;
        addData("userRoles", userRoles);
    }
}


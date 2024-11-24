package org.example.testapi.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
@Data
@Builder
public class AddUserRequest {
    private String email;
    private String password;
    private String name;
    private String phone;
    private LocalDate birthday;
    private Boolean active;
    private Set<Role> roles;
}

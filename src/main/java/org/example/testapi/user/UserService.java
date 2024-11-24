package org.example.testapi.user;

import org.example.testapi.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class UserService {
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void save(AddUserRequest request) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate1 = formatter.format(date);
        LocalDate sqlDate1 = java.sql.Date.valueOf(formattedDate1).toLocalDate();
        var user = User.builder()
                .email(request.getEmail())
                .password(encodePassword(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .birthday(request.getBirthday())
                .creationDate(sqlDate1)
                .profilePicture("")
                .active(request.getActive() != null ? request.getActive() : true)
                .roles(new HashSet<>(Set.of(Role.ROLE_USER)))
                .build();
        userRepository.save(user);
    }

    private FindUserResponse convertToFindUserRequest(User user) {
        return FindUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .active(user.getActive())
                .profilePicture(user.getProfilePicture())
                .address(user.getAddress())
                .build();
    }

    public List<FindUserResponse> findByName(String name) throws Exception {
        if (name == null || name.isEmpty()) {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(this::convertToFindUserRequest)
                    .collect(Collectors.toList());
        } else {
            List<User> users = userRepository.findByNameContainingIgnoreCase(name);
            if (users == null || users.isEmpty()) {
                throw new Exception("User not found");
            }
            return users.stream()
                    .map(this::convertToFindUserRequest)
                    .collect(Collectors.toList());
        }
    }
    public User updateActive(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User userEntity = user.get();
            userEntity.setActive(!userEntity.getActive());
            return userRepository.save(userEntity);
        } else {
            throw new UserNotFoundException(id);
        }
    }
}

package org.example.testapi.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceDetails implements UserServiceInterface, UserDetailsService {

    public static User build(Optional<User> optionalUser) {
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles())
                .active(user.isEnabled())
                .name(user.getName())
                .gender(user.getGender())
                .profilePicture(user.getProfilePicture())
                .phone(user.getPhone())
                .birthday(user.getBirthday())
                .address(user.getAddress())
                .build();
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(int id) {

        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void save(AddUserRequest user) {

    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        return build(user);

    }

    @Override
    public Optional<User> findByUserEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImp;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceImp userService;

    @Autowired
    public CustomUserDetailsService(UserServiceImp userService) {
        this.userService = userService;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userService.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s does not exist!", username));
        }
        return user;
    }
}


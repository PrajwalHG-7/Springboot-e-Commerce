package com.app.ecom.services;

import com.app.ecom.models.User;
import com.app.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> fetchAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> fetchUser(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(User newUser, Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(newUser.getFirstName() != null ? newUser.getFirstName() : user.getFirstName());
                    user.setLastName(newUser.getLastName() != null ? newUser.getLastName() : user.getLastName());
                    userRepository.save(user);
                    return user;
                }).orElse(null);
    }

    public Optional<?> removeUser(Long id) {
        Optional<User> delUser = userRepository.findById(id);
        if(delUser.isPresent())
            userRepository.deleteById(id);

        return delUser;
    }
}

package com.app.ecom.services;

import com.app.ecom.dto.AddressDTO;
import com.app.ecom.dto.UserRequest;
import com.app.ecom.dto.UserResponse;
import com.app.ecom.exception.EmailAlreadyExistsException;
import com.app.ecom.exception.PhoneAlreadyExistsException;
import com.app.ecom.models.Address;
import com.app.ecom.models.User;
import com.app.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(String.valueOf(user.getId()));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());

        if(user.getAddress() != null) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setZipcode(user.getAddress().getZipcode());

            response.setAddress(addressDTO);
        }

        return response;
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if (userRepository.existsByPhone(userRequest.getPhone())) {
            throw new PhoneAlreadyExistsException("Phone number already exists");
        }

        user.setFirstName(userRequest.getFirstName() != null ? userRequest.getFirstName() : user.getFirstName());
        user.setLastName(userRequest.getLastName() != null ? userRequest.getLastName() : user.getLastName());
        user.setEmail(userRequest.getEmail() != null ? userRequest.getEmail() : user.getEmail());
        user.setPhone(userRequest.getPhone() != null ? userRequest.getPhone() : user.getPhone());

        if(userRequest.getAddress() != null) {
            Address address = user.getAddress();
            if(address == null) {
                address = new Address();
                user.setAddress(address);
            }
            address.setStreet(userRequest.getAddress().getStreet() != null ? userRequest.getAddress().getStreet() : user.getAddress().getStreet());
            address.setCity(userRequest.getAddress().getCity() != null ? userRequest.getAddress().getCity() : user.getAddress().getCity());
            address.setState(userRequest.getAddress().getState() != null ? userRequest.getAddress().getState() : user.getAddress().getState());
            address.setCountry(userRequest.getAddress().getCountry() != null ? userRequest.getAddress().getCountry() : user.getAddress().getCountry());
            address.setZipcode(userRequest.getAddress().getZipcode() != null ? userRequest.getAddress().getZipcode() : user.getAddress().getZipcode());
        }
    }

    public UserResponse addUser(UserRequest userRequest) {
        User user = new User();
        updateUserFromRequest(user, userRequest);
        return mapToUserResponse(userRepository.save(user));
    }

    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    public Optional<UserResponse> fetchUser(Long id) {
        return userRepository.findById(id).map(this::mapToUserResponse);
    }

    public UserResponse updateUser(UserRequest updatedUserRequest, Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    updateUserFromRequest(user, updatedUserRequest);
                    userRepository.save(user);
                    return mapToUserResponse(user);
                }).orElse(null);
    }

    public Optional<?> removeUser(Long id) {
        Optional<UserResponse> delUser = userRepository.findById(id).map(this::mapToUserResponse);
        if(delUser.isPresent())
            userRepository.deleteById(id);

        return delUser;
    }
}

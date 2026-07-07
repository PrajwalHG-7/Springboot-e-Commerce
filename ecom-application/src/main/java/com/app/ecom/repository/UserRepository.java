package com.app.ecom.repository;

import com.app.ecom.models.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Long> {

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
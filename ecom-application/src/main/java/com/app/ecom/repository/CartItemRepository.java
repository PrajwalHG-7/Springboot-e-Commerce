package com.app.ecom.repository;

import com.app.ecom.models.CartItem;
import com.app.ecom.models.Product;
import com.app.ecom.models.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface CartItemRepository extends JpaRepository<@NonNull CartItem, @NonNull Long> {
    CartItem findByUserAndProduct(User user, Product product);

    CartItem deleteByUserAndProduct(User user, Product product);

    List<CartItem> findByUserId(Long userId);

    void deleteByUser(@NonNull User user);
}
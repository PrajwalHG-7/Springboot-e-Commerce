package com.app.ecom.repository;

import com.app.ecom.models.CartItem;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<@NonNull CartItem, @NonNull Long> {
}
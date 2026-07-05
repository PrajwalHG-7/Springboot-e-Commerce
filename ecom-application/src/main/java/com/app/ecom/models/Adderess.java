package com.app.ecom.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
public class Adderess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Address is required")
    private String street;
    private String city;
    private String state;
    private String country;
    @NotNull(message = "Zip code is required")
    private  Long zipcode;
}

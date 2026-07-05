package com.app.ecom.dto;

import jakarta.validation.constraints.NotNull;

public class AddressDTO {
    private String street;
    private String city;
    private String state;
    private String country;
    private  Long zipcode;
}
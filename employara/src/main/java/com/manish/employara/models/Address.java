package com.manish.employara.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "address")
public class Address {
    @Id
    private String id;

    private String zipCode;
    private String country;
    private String state;
    private String city;
    private String street;
}

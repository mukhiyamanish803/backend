package com.manish.employara.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "dialCode")
public class DialCode {
    @Id
    private String id;
    private String countryName;
    private String countryCode;
    private String dialCode;

}

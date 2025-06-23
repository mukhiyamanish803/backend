package com.manish.employara.models.admin.management;

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
@Document(collection = "category")
public class Category {
    @Id
    private String category;

    @Builder.Default
    private int jobCount = 0;
}

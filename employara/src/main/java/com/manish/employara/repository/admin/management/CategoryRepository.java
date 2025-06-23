package com.manish.employara.repository.admin.management;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import com.manish.employara.models.admin.management.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findByCategory(String category);

    boolean existsByCategory(String category);

    @Query("{ 'category' : ?0 }")
    @Update("{ '$inc' : { 'jobCount' : 1 } }")
    void incrementJobCount(String category);

    @Query("{ 'category' : ?0 }")
    @Update("{ '$inc' : { 'jobCount' : -1 } }")
    void decrementJobCount(String category);
}

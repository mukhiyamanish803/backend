package com.manish.employara.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.manish.employara.models.User;
import com.manish.employara.models.Role;
import com.manish.employara.models.Status;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    // Search with role and status
    @Query("{ $and: [ " +
            "{ $or: [ " +
            "{ 'firstName': { $regex: ?0, $options: 'i' }}, " +
            "{ 'lastName': { $regex: ?0, $options: 'i' }}, " +
            "{ 'email': { $regex: ?0, $options: 'i' }} " +
            "] }, " +
            "{ 'role': ?1 }, " +
            "{ 'status': ?2 } " +
            "] }")
    Page<User> findBySearchTermAndRoleAndStatus(String searchTerm, Role role, Status status, Pageable pageable);

    // Search with role only
    @Query("{ $and: [ " +
            "{ $or: [ " +
            "{ 'firstName': { $regex: ?0, $options: 'i' }}, " +
            "{ 'lastName': { $regex: ?0, $options: 'i' }}, " +
            "{ 'email': { $regex: ?0, $options: 'i' }} " +
            "] }, " +
            "{ 'role': ?1 } " +
            "] }")
    Page<User> findBySearchTermAndRole(String searchTerm, Role role, Pageable pageable);

    // Search only
    @Query("{ $or: [ " +
            "{ 'firstName': { $regex: ?0, $options: 'i' }}, " +
            "{ 'lastName': { $regex: ?0, $options: 'i' }}, " +
            "{ 'email': { $regex: ?0, $options: 'i' }} " +
            "] }")
    Page<User> findBySearchTerm(String searchTerm, Pageable pageable);

    // Filter by role and status
    Page<User> findByRoleAndStatus(Role role, Status status, Pageable pageable);

    // Filter by role only
    Page<User> findByRole(Role role, Pageable pageable);

    // Filter by status only
    Page<User> findByStatus(Status status, Pageable pageable);
}

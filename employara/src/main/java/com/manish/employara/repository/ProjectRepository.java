package com.manish.employara.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.jobseeker.Project;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByUserId(String userId);
}

package com.manish.employara.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.recruiter.Hackathon;

public interface HackathonRepository extends MongoRepository<Hackathon, String> {
    List<Hackathon> findAllByRecruiterId(String recruiterId);
}

package com.manish.employara.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.User;
import com.manish.employara.models.jobseeker.Certificate;

public interface CertificateRepository extends MongoRepository<Certificate, String> {
    List<Certificate> findByUser(User user);
}

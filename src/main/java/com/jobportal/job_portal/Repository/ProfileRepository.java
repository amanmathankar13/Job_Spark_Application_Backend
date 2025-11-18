package com.jobportal.job_portal.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jobportal.job_portal.Entity.Profile;

@Repository
public interface ProfileRepository extends MongoRepository<Profile,Long> {
    
}

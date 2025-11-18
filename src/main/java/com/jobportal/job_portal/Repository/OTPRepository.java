package com.jobportal.job_portal.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jobportal.job_portal.Entity.OTP;
import java.util.List;
import java.time.LocalDateTime;


@Repository
public interface OTPRepository extends MongoRepository<OTP,String> {

    List<OTP> findByCreationTimeBefore(LocalDateTime otpGeneratedAt);
    
}

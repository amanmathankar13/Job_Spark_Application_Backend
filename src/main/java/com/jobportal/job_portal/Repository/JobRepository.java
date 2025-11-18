package com.jobportal.job_portal.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jobportal.job_portal.Entity.Job;
import java.util.List;


@Repository
public interface JobRepository extends MongoRepository<Job, Long> {

    public List<Job> findByPostedBy(Long postedBy);
    
}

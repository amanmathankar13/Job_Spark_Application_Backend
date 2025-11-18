package com.jobportal.job_portal.Utility;

import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.jobportal.job_portal.Entity.Sequence;
import com.jobportal.job_portal.Exceptions.JobPortalException;

@Component
public class Utilities {

    private static MongoOperations mongoOperations;

    @Autowired
    public void setMongoOperation(MongoOperations mongoOperations){
        Utilities.mongoOperations = mongoOperations;
    }

    public static Long getNextSequence(String key) throws JobPortalException{

        Query query = new Query(Criteria.where("_id").is(key));
        Update update = new Update();
        update.inc("seq", 1);
        FindAndModifyOptions options=new FindAndModifyOptions();
        options.returnNew(true);
        Sequence sequence = mongoOperations.findAndModify(query, update, options, Sequence.class);
        if (sequence==null) {
            throw new JobPortalException("Unable to get sequence id for key : "+ key);
        }
        return sequence.getSeq();
    }

    public static String generateOtp() {
        StringBuilder otp = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}

package com.jobportal.job_portal.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jobportal.job_portal.DTO.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User {


    @Id
    private Long id;
    private String name;
    @Indexed(unique=true)
    private String email;
    private String password;
    private AccountType accountType;
    private Long profileId;



}

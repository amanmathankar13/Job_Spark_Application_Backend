package com.jobportal.job_portal.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.job_portal.DTO.ApplicantDTO;
import com.jobportal.job_portal.DTO.Application;
import com.jobportal.job_portal.DTO.ApplicationStatus;
import com.jobportal.job_portal.DTO.JobDTO;
import com.jobportal.job_portal.DTO.JobStatus;
import com.jobportal.job_portal.DTO.NotificationDTO;
import com.jobportal.job_portal.Entity.Applicant;
import com.jobportal.job_portal.Entity.Job;
import com.jobportal.job_portal.Exceptions.JobPortalException;
import com.jobportal.job_portal.Repository.JobRepository;
import com.jobportal.job_portal.Utility.Utilities;

@Service
public class JobServiceImpl implements JobService  {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public JobDTO postJob(JobDTO jobDTO) throws JobPortalException {
      if(jobDTO.getId()==0){
        jobDTO.setId(Utilities.getNextSequence("jobs"));
        jobDTO.setPostTime(LocalDateTime.now());
        NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setAction("Job Posted");
            notificationDTO.setMessage("Job Posted Successfully for "+jobDTO.getJobTitle()+" at "+ jobDTO.getCompany());
            notificationDTO.setRoute("/posted-job/"+jobDTO.getId());
            notificationDTO.setUserId(jobDTO.getPostedBy());
            notificationService.sendNotification(notificationDTO);
      }
      else{
        Job job = jobRepository.findById(jobDTO.getId()).orElseThrow(()-> new JobPortalException("job.not.found"));
        if(job.getJobStatus().equals(JobStatus.DRAFT)||job.getJobStatus().equals(JobStatus.CLOSED)){
          jobDTO.setPostTime(LocalDateTime.now());
        }
      }
        Job job = new Job(jobDTO.getId(), jobDTO.getJobTitle(), jobDTO.getCompany(), jobDTO.getApplicants()!=null?jobDTO.getApplicants().stream().map((x)->x.toEntity()).toList():null, jobDTO.getAbout(), jobDTO.getExperience(), jobDTO.getJobType(), jobDTO.getLocation(), jobDTO.getPackageOffered(), jobDTO.getPostTime(), jobDTO.getDescription(), jobDTO.getSkillsRequired(), jobDTO.getJobStatus(), jobDTO.getPostedBy());
        jobRepository.save(job);
        JobDTO jobDTO2 =new JobDTO(job.getId(), job.getJobTitle(), job.getCompany(), job.getApplicants()!=null?job.getApplicants().stream().map((x)->x.toDTO()).toList():null, job.getAbout(), job.getExperience(), job.getJobType(), job.getLocation(), job.getPackageOffered(), job.getPostTime(), job.getDescription(), job.getSkillsRequired(), job.getJobStatus(), job.getPostedBy());
        return jobDTO2;
    }

    @Override
    public List<JobDTO> getAllJobs() throws JobPortalException {

      List<Job> jobs = jobRepository.findAll();
      List<JobDTO> jobDTOs = new ArrayList<>();
      for(Job job : jobs){
        JobDTO jobDTO2 =new JobDTO(job.getId(), job.getJobTitle(), job.getCompany(), job.getApplicants()!=null?job.getApplicants().stream().map((x)->x.toDTO()).toList():null, job.getAbout(), job.getExperience(), job.getJobType(), job.getLocation(), job.getPackageOffered(), job.getPostTime(), job.getDescription(), job.getSkillsRequired(), job.getJobStatus(), job.getPostedBy());
        jobDTOs.add(jobDTO2);
      }
      return jobDTOs;
    }

    @Override
    public JobDTO getJob(Long id) throws JobPortalException {
      Job job = jobRepository.findById(id).orElseThrow(()-> new JobPortalException("job.not.found"));
      JobDTO jobDTO2 =new JobDTO(job.getId(), job.getJobTitle(), job.getCompany(), job.getApplicants()!=null?job.getApplicants().stream().map((x)->x.toDTO()).toList():null, job.getAbout(), job.getExperience(), job.getJobType(), job.getLocation(), job.getPackageOffered(), job.getPostTime(), job.getDescription(), job.getSkillsRequired(), job.getJobStatus(), job.getPostedBy());
      return jobDTO2;
    }

    @Override
    public void applyJob(Long id, ApplicantDTO applicantDTO) throws JobPortalException {
      Job job = jobRepository.findById(id).orElseThrow(()-> new JobPortalException("job.not.found"));
      List<Applicant> applicants = job.getApplicants();
      if(applicants==null) {
        applicants = new ArrayList<>();
      }
      if(applicants.stream().filter((x)->x.getApplicantId()==applicantDTO.getApplicantId()).toList().size()>0) throw new JobPortalException("job.applied.already");
      applicantDTO.setApplicationStatus(ApplicationStatus.APPLIED);
      Applicant applicant = applicantDTO.toEntity();
      applicants.add(applicant);
      job.setApplicants(applicants);
      jobRepository.save(job);
    }

    @Override
    public List<JobDTO> getJobPostedBy(Long postedBy) {
      List<Job> jobs = jobRepository.findByPostedBy(postedBy);
      List<JobDTO> jobDTOs = new ArrayList<>();
      for(Job job : jobs){
        JobDTO jobDTO2 =new JobDTO(job.getId(), job.getJobTitle(), job.getCompany(), job.getApplicants()!=null?job.getApplicants().stream().map((x)->x.toDTO()).toList():null, job.getAbout(), job.getExperience(), job.getJobType(), job.getLocation(), job.getPackageOffered(), job.getPostTime(), job.getDescription(), job.getSkillsRequired(), job.getJobStatus(), job.getPostedBy());
        jobDTOs.add(jobDTO2);
      }
      return jobDTOs;
    }

    @Override
    public void changeAppStatus(Application application) throws JobPortalException {
      Job job = jobRepository.findById(application.getId()).orElseThrow(()-> new JobPortalException("job.not.found"));
      List<Applicant> applicants = job.getApplicants().stream().map((x)->{
        if(application.getApplicantId()==x.getApplicantId()){
          x.setApplicationStatus(application.getApplicationStatus());
          if(application.getApplicationStatus().equals(ApplicationStatus.INTERVIEWING)){
            x.setInterviewTime(application.getInterviewTime());
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setAction("Interview Scheduled");
            notificationDTO.setMessage("Interview Scheduled for job id"+application.getId());
            notificationDTO.setRoute("/job-history");
            notificationDTO.setUserId(application.getApplicantId());
            try {
              notificationService.sendNotification(notificationDTO);
            } catch (JobPortalException e) {
              e.printStackTrace();
            }
          }
        }
        return x;
      }).toList();
      job.setApplicants(applicants);
      jobRepository.save(job);
    }
}

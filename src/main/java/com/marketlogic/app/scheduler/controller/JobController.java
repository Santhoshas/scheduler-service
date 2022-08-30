package com.marketlogic.app.scheduler.controller;

import com.marketlogic.app.scheduler.dto.Message;
import com.marketlogic.app.scheduler.dto.ScheduleJob;
import com.marketlogic.app.scheduler.service.JobActivity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/job", produces = MediaType.APPLICATION_JSON_VALUE)
public class JobController {

    private static Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private JobActivity jobActivity;

    @GetMapping
    @ApiOperation("Get all jobs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched jobs"),
            @ApiResponse(code = 500, message = "Errors while processing")
    })
    public List<ScheduleJob> getJobs() throws SchedulerException {
        return jobActivity.getAllJobList();
    }

    @PostMapping("/pause-job")
    @ApiOperation("pause the job by job id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully paused the job"),
            @ApiResponse(code = 500, message = "Errors while processing")})
    public Object pauseJob(@RequestParam String jobId) {
        Message message = Message.failure();
        try {
            jobActivity.pauseJob(jobId);
            message = Message.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            logger.error("resumeJob ex:", e);
        }
        return message;
    }

    @PostMapping("/resume-job")
    @ApiOperation("resume the job by job id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully resumed the job"),
            @ApiResponse(code = 500, message = "Errors while processing")})
    public Object resumeJob(@RequestParam String jobId) {
        Message message = Message.failure();
        try {
            jobActivity.resumeJob(jobId);
            message = Message.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            logger.error("resumeJob ex:", e);
        }
        return message;
    }

    @PostMapping("/delete")
    @ApiOperation("delete a job")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully resume job"),
            @ApiResponse(code = 500, message = "Errors while processing")})
    public String deleteJob(@RequestBody ScheduleJob job) {
        logger.info("params, job = {}", job);
        Message message = Message.failure();
        try {
            jobActivity.deleteJob(job);
            message = Message.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            logger.error("deleteJob ex:", e);
        }
        return message.getMsg();
    }

    @PostMapping
    @ApiOperation("add a job")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully save the job"),
            @ApiResponse(code = 500, message = "Errors while processing")})
    public String addJob(@RequestBody ScheduleJob job) {
        logger.info("params, job = {}", job);
        Message message = Message.failure();
        try {
            jobActivity.addJob(job);
            message = Message.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            logger.error("updateCron ex:", e);
        }
        return message.getMsg();
    }

    @PostMapping("/update")
    @ApiOperation("Update the job if exists else create a new job")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully save the job"),
            @ApiResponse(code = 500, message = "Errors while processing")})
    public String addOrUpdateJob(
            @ApiParam(name = "jobName", value = "jobName of job") @RequestParam(name = "jobName") String jobName,
            @ApiParam(name = "jobGroup", value = "group name of job") @RequestParam(name = "jobGroup") String jobGroup,
            @RequestBody ScheduleJob job) {
        logger.info("params, job = {}", job);
        Message message = Message.failure();
        try {
            jobActivity.updateJob(jobName, jobGroup, job);
            message = Message.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            logger.error("updateCron ex:", e);
        }
        return message.getMsg();
    }

}
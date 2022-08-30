package com.marketlogic.app.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleJob implements Serializable {

    private static final long serialVersionUID = 1L;

    private String jobId;
    private String jobName;
    private String jobGroup;
    private String jobStatus;
    private String cronExpression;
    private String newsLetterId;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Unable to convert ScheduleJob:object to String");
            return this.toString();
        }
    }

    public ScheduleJob toObject(String jsonString) {
        try {
            return new ObjectMapper().readValue(jsonString, ScheduleJob.class);
        } catch (Exception e) {
            log.error("Unable to convert String to ScheduleJob:Object");
            return new ScheduleJob();
        }
    }
}
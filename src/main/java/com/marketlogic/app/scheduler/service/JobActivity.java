package com.marketlogic.app.scheduler.service;

import com.google.common.base.Preconditions;
import com.marketlogic.app.scheduler.dto.ScheduleJob;
import com.marketlogic.app.scheduler.exception.MessageResultCode;
import com.marketlogic.app.scheduler.exception.SchedulerServiceException;
import com.marketlogic.app.scheduler.job.QuartzJobFactory;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class JobActivity {

    @Autowired
    private Scheduler scheduler;

    public List<ScheduleJob> getAllJobList() throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<ScheduleJob> jobList = new ArrayList<>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                ScheduleJob job = new ScheduleJob();
                job.setJobName(jobKey.getName());
                job.setJobGroup(jobKey.getGroup());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronExpression(cronExpression);
                }
                jobList.add(job);
            }
        }
        return jobList;
    }

    public void addJob(ScheduleJob scheduleJob) throws Exception {
        checkNotNull(scheduleJob);

        Preconditions.checkNotNull(scheduleJob.getCronExpression(), "CronExpression is null");

        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (trigger != null) {
            throw new Exception("job already exists!");
        }

        // simulate job info db persist operation
        scheduleJob.setJobId(String.valueOf(QuartzJobFactory.jobList.size() + 1));
        QuartzJobFactory.jobList.add(scheduleJob);

        JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class)
                .withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup()).build();
        jobDetail.getJobDataMap().put("scheduleJob", scheduleJob.toString());

        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
                .cronSchedule(scheduleJob.getCronExpression())
                .withMisfireHandlingInstructionIgnoreMisfires();
        trigger = TriggerBuilder.newTrigger().withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup())
                .withSchedule(cronScheduleBuilder).build();

        scheduler.scheduleJob(jobDetail, trigger);

    }

    public void resumeJob(String jobId) throws SchedulerException {
        if (StringUtils.isEmpty(jobId)) {
            throw new SchedulerServiceException("Invalid jobId", MessageResultCode.INVALID_REQUEST);
        }
        getAllJobList().forEach(scheduleJob -> {
            if (jobId.equals(scheduleJob.getJobName())) {
                JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
                try {
                    scheduler.resumeJob(jobKey);
                    log.debug("Successfully resumed the job" + jobKey);
                } catch (SchedulerException e) {
                    log.error("Unable to resume the job");
                }
            }
        });

    }

    public void pauseJob(String jobId) throws SchedulerException {
        if (StringUtils.isEmpty(jobId)) {
            throw new SchedulerServiceException("Invalid jobId", MessageResultCode.INVALID_REQUEST);
        }
        getAllJobList().forEach(scheduleJob -> {
            if (jobId.equals(scheduleJob.getJobName())) {
                JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
                try {
                    scheduler.pauseJob(jobKey);
                    log.debug("Successfully paused the job" + jobKey);
                } catch (SchedulerException e) {
                    log.error("Unable to pause the job");
                }
            }
        });

    }

    public void deleteJob(ScheduleJob scheduleJob) throws SchedulerException {
        checkNotNull(scheduleJob);
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.deleteJob(jobKey);
    }

    public void updateJob(String jobName, String jobGroup, ScheduleJob scheduleJob) throws Exception {
        checkNotNull(scheduleJob);
        Preconditions.checkNotNull(scheduleJob.getCronExpression(), "CronExpression is null");
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);

        scheduler.deleteJob(jobKey);

        QuartzJobFactory.jobList.add(scheduleJob);
        JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class)
                .withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup()).build();
        jobDetail.getJobDataMap().put("scheduleJob", scheduleJob.toString());
        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (trigger != null) {
            throw new Exception("job already exists!");
        }

        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
                .cronSchedule(scheduleJob.getCronExpression())
                .withMisfireHandlingInstructionIgnoreMisfires();
        trigger = TriggerBuilder.newTrigger().withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup())
                .withSchedule(cronScheduleBuilder).build();

        scheduler.scheduleJob(jobDetail, trigger);

    }

    private void checkNotNull(ScheduleJob scheduleJob) {
        Preconditions.checkNotNull(scheduleJob, "job is null");
        Preconditions.checkNotNull(scheduleJob.getJobName(), "jobName is null");
        Preconditions.checkNotNull(scheduleJob.getJobGroup(), "jobGroup is null");
    }

}

package com.marketlogic.app.scheduler.job;

import com.google.common.collect.Lists;
import com.marketlogic.app.scheduler.dto.ScheduleJob;
import com.marketlogic.app.scheduler.service.JobActivity;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class QuartzJobFactory implements Job {

    @Autowired
    private JobActivity jobActivity;

    @Value("${app.failed-job-retrigger-delay-in-mins}")
    private long failedJobReTriggerDelay;

    @Value("${app.topicTriggerNewsLetter}")
    private String topicTriggerNewsLetter;

    public static List<ScheduleJob> jobList = Lists.newArrayList();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        String jsonString = (String) jobExecutionContext.getMergedJobDataMap().get("scheduleJob");
        ScheduleJob scheduleJob = new ScheduleJob().toObject(jsonString);
        if (scheduleJob != null) {
            pushItToTopic(scheduleJob);
        } else {
            log.error("Unable to get job details! {}", jsonString);
        }
    }

    private void pushItToTopic(ScheduleJob scheduleJob) {
        try {
            log.info("Notifying newsletter to consumers through Kafka -> {}", scheduleJob.getNewsLetterId());
            this.kafkaTemplate.send(topicTriggerNewsLetter,
                    String.format("{\"id\":%s}", scheduleJob.getNewsLetterId()));
            log.info("Notified Successfully");
        } catch (Exception e) {
            log.error("Exception while notifying newsletter changes {} through Kafka: ", scheduleJob.getNewsLetterId(), e);
            getCronExpressionForFailedJob();
        }
    }

    private String getCronExpressionForFailedJob() {
        LocalDateTime cronExprToTriggerInNext30Minutes = LocalDateTime.now().plusMinutes(failedJobReTriggerDelay);
        return String.format(
                "%1$s %2$s %3$s %4$s %5$s %6$s %7$s",
                cronExprToTriggerInNext30Minutes.getSecond(),
                cronExprToTriggerInNext30Minutes.getMinute(),
                cronExprToTriggerInNext30Minutes.getHour(),
                cronExprToTriggerInNext30Minutes.getDayOfMonth(),
                cronExprToTriggerInNext30Minutes.getMonth().getValue(),
                "?",
                cronExprToTriggerInNext30Minutes.getYear()
        );
    }
}
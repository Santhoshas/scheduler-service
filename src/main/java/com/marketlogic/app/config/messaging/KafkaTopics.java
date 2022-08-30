package com.marketlogic.app.config.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopics extends HashMap<String, String> {
}

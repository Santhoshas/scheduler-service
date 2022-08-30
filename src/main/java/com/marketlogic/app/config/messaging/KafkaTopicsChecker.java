package com.marketlogic.app.config.messaging;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Profile("!mock")
@Component
@Slf4j
public class KafkaTopicsChecker {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Autowired
    private KafkaTopics kafkaTopics;

    @Value("${kafka.bootstrap.servers}")
    private String kafkaBootstrapServers;

    @EventListener
    public void checkTopics(ContextRefreshedEvent contextRefreshedEvent) {
        try (var client = AdminClient.create(Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers))) {
            var listTopicsResult = client.listTopics();
            var existingTopics = listTopicsResult.names().get();
            var missingTopics = kafkaTopics
                    .values()
                    .stream()
                    .filter(topic -> !existingTopics.contains(topic))
                    .collect(Collectors.toSet());
            if (!missingTopics.isEmpty()) {
                log.error("Kafka topics are missing: [{}]. Cannot continue.", String.join("],[", missingTopics));
                System.exit(1);
            }
        } catch (Exception e) {
            log.error("Error in accessing KAFKA. Please check");
            System.exit(1);
        }
    }
}

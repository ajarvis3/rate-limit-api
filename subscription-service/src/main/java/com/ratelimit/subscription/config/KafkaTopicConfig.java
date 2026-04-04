package com.ratelimit.subscription.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String SUBCRIPTION_BILLING_TOPIC = "subscription-billing";

    @Bean
    public NewTopic usageAggregateBillingTopic() {
        return TopicBuilder.name(SUBCRIPTION_BILLING_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

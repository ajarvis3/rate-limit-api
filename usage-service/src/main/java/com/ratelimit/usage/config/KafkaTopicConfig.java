package com.ratelimit.usage.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String USAGE_AGGREGATE_BILLING_TOPIC = "usage-aggregate-billing";

    @Bean
    public NewTopic usageAggregateBillingTopic() {
        return TopicBuilder.name(USAGE_AGGREGATE_BILLING_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

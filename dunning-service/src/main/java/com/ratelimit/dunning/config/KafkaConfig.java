package com.ratelimit.dunning.config;

import com.ratelimit.dunning.dto.FailedBillingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, FailedBillingEvent> producerFactory(ObjectMapper mapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Use a copy of the application's ObjectMapper and ensure Java Time handling
        ObjectMapper kafkaMapper = mapper.copy();
        kafkaMapper.registerModule(new JavaTimeModule());
        kafkaMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Use a simple Jackson-based Serializer implementation to avoid relying on specific spring-kafka helper classes
        Serializer<FailedBillingEvent> valueSerializer = new JacksonSerializer<>(kafkaMapper);

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), valueSerializer);
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper kafkaObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    private static class JacksonSerializer<T> implements Serializer<T> {
        private final ObjectMapper mapper;

        public JacksonSerializer(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) { }

        @Override
        public byte[] serialize(String topic, T data) {
            if (data == null) return null;
            try {
                return mapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize JSON", e);
            }
        }

        @Override
        public void close() { }
    }

    @Bean
    public KafkaTemplate<String, FailedBillingEvent> kafkaTemplate(
            ProducerFactory<String, FailedBillingEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}

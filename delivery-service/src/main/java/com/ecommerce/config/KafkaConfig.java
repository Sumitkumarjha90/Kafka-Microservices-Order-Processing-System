package com.ecommerce.config;

import com.ecommerce.event.DeliveryCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic deliveryCreatedTopic() {
        return TopicBuilder.name("delivery-created")
                .partitions(3)
                .replicas(1)
                .build();
    }
}

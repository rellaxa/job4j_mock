package ru.checkdev.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

	@Bean
	public NewTopic createNotify() {
		return TopicBuilder.name("notify")
				.partitions(3)
				.replicas(3)
				.build();
	}
}

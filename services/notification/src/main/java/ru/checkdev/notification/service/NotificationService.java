package ru.checkdev.notification.service;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.Notify;

@Service
@AllArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void put(final Notify notify) {
        kafkaTemplate.send("notify", notify);
    }
}

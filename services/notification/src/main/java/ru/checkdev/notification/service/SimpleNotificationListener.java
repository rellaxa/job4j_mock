package ru.checkdev.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.Notify;

@Service
@Slf4j
public class SimpleNotificationListener {

	@KafkaListener(topics = "notify")
	public void receiveNotify( Notify notify) {
		log.debug(notify.toString());
	}
}

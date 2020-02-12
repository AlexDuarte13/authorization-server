package com.ebix.easi.authorizationserver.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ebix.easi.authorizationserver.enums.NotificationStatus;
import com.ebix.easi.authorizationserver.model.entities.Notification;
import com.ebix.easi.authorizationserver.model.service.NotificationService;

@Component
public class NotificationEmailAttempt {
	
	@Autowired
	private NotificationService service;
	
	@Scheduled(fixedDelay = 300000)
	public void notificationAttempt() {
		
		List<Notification> failedNotifications = service.findByStatus(NotificationStatus.FAIL);
		
		failedNotifications.stream().forEach(notification -> service.doNewAttempt(notification));
		
	}

}

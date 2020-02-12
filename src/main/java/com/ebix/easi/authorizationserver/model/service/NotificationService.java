package com.ebix.easi.authorizationserver.model.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebix.easi.authorizationserver.enums.NotificationStatus;
import com.ebix.easi.authorizationserver.exception.EasiInternalErrorException;
import com.ebix.easi.authorizationserver.model.entities.Notification;
import com.ebix.easi.authorizationserver.model.entities.User;
import com.ebix.easi.authorizationserver.model.repository.NotificationRepository;
import com.ebix.easi.authorizationserver.model.repository.UserRepository;

@Service
public class NotificationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationRepository repository;

	@Autowired
	private AuthorizationServerService authService;

	public List<Notification> findByStatus(NotificationStatus status) {
		return repository.findByStatus(status);
	}

	public void save(Notification notification) {
		repository.save(notification);
	}

	public void doNewAttempt(Notification notification) {

		Optional<User> user = userRepository.findById(notification.getUserId());

		if (user.isPresent()) {

			try {

				authService.sendRegistrationMail(user.get());

				updateNotificationStatus(notification, NotificationStatus.SUCCESS, null);

				repository.save(notification);

			} catch (EasiInternalErrorException e) {

				updateNotificationStatus(notification, NotificationStatus.FAIL, e.getMessage());

			}

		} else {

			updateNotificationStatus(notification, NotificationStatus.FAIL,
					String.format("Usuário de id %d não encontrado", notification.getUserId()));

		}
	}

	private void updateNotificationStatus(Notification notification, NotificationStatus status, String error) {
		notification.setError(error);
		notification.setStatus(status);
		notification.setSendDate(new Date());
		repository.save(notification);
	}

}

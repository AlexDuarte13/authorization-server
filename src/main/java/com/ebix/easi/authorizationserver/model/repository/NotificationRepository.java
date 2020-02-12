package com.ebix.easi.authorizationserver.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ebix.easi.authorizationserver.enums.NotificationStatus;
import com.ebix.easi.authorizationserver.model.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>{

	@Query("select n from Notification n where n.status = ?1")
	public List<Notification> findByStatus(NotificationStatus status);
	
}

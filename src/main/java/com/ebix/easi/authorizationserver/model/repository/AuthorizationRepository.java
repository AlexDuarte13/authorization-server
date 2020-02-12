package com.ebix.easi.authorizationserver.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ebix.easi.authorizationserver.model.entities.Authorization;

public interface AuthorizationRepository extends JpaRepository<Authorization, Long> {
	
	@Query("select a from Authorization a where a.user.id = ?1")
	Optional<Authorization> findByUser(Long userId);
	
	@Query("select a from Authorization a where a.jwt = ?1")
	Optional<Authorization> findByToken(String token);

}

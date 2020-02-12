package com.ebix.easi.authorizationserver.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ebix.easi.authorizationserver.model.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("select u from User u where u.email = ?1 and u.password = ?2")
	public Optional<User> findByLogin(String email, String password);
	
	@Query("select u from User u where u.email = ?1")
	public Optional<User> findByEmail(String email);

}

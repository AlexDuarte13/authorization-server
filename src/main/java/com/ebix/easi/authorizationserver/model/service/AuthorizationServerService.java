package com.ebix.easi.authorizationserver.model.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ebix.easi.authorizationserver.enums.NotificationStatus;
import com.ebix.easi.authorizationserver.enums.Profile;
import com.ebix.easi.authorizationserver.exception.EasiBusinessException;
import com.ebix.easi.authorizationserver.exception.EasiInternalErrorException;
import com.ebix.easi.authorizationserver.model.entities.Authorization;
import com.ebix.easi.authorizationserver.model.entities.Notification;
import com.ebix.easi.authorizationserver.model.entities.User;
import com.ebix.easi.authorizationserver.model.repository.AuthorizationRepository;
import com.ebix.easi.authorizationserver.model.repository.NotificationRepository;
import com.ebix.easi.authorizationserver.model.repository.UserRepository;
import com.ebix.easi.authorizationserver.util.JWTUtil;
import com.ebix.easi.authorizationserver.util.PasswordUtil;
import com.ebix.easi.authorizationserver.util.email.EmailContentType;
import com.ebix.easi.authorizationserver.util.email.EmailSender;
import com.ebix.easi.authorizationserver.util.email.EmailTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuthorizationServerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationServerService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthorizationRepository authorizationRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Value("${easi.appurlaccess}")
	private String appURL;
	
	public void createUserAccess(User user) throws EasiBusinessException {
		
		if(userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new EasiBusinessException("Já existe um usuário com este e-mail cadastrado.");
		}
		
		user = createUser(user);
		
		Notification notification = new Notification();
		
		notification.setUserId(user.getId());
		notification.setEmail(user.getEmail());
		notification.setSendDate(new Date());
		
		try {
			
			sendRegistrationMail(user);
			
			notification.setStatus(NotificationStatus.SUCCESS);
			
		} catch (EasiInternalErrorException e) {
			
			notification.setStatus(NotificationStatus.FAIL);
			notification.setError(e.getMessage());
			
		} finally {
			
			notificationRepository.save(notification);
			
		}
	}

	public User createUser(User user) throws EasiBusinessException {
		
		try {
			user.setPassword(PasswordUtil.randomPassword());
			return userRepository.save(user);
			
		} catch (DataIntegrityViolationException e) {
			throw new EasiBusinessException("Não foi possível cadastrar o usuário: " + e.getMessage());
		}

	}

	public void sendRegistrationMail(User user) throws EasiInternalErrorException {
		
		try {
			
			EmailSender.send("Acesso EASI - Ebix Assurance Self Inspection", user.getEmail(),
					EmailTemplate.authenticationLink(user, appURL), EmailContentType.TEXT_HTML_UTF8);
			
		} catch (Exception e) {
			
			LOGGER.error(e.getMessage(), e);
			
			throw new EasiInternalErrorException(String.format("O e-mail não pode ser enviado: %s", e.getMessage()));
			
		}
	}

	public Authorization login(String email, String password) {
		
		return userRepository.findByLogin(email, password).map(user -> {
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.HOUR_OF_DAY, 6);
			Date expireAt = calendar.getTime();

			Authorization authorization = authorizationRepository.findByUser(user.getId()).orElse(new Authorization());
			authorization.setUser(new User());
			authorization.getUser().setId(user.getId());
			authorization.getUser().setName(user.getName());
			authorization.getUser().setProfile(user.getProfile());
			authorization.getUser().setEmail(user.getEmail());
			authorization.setJwt(null);
			authorization.setExpirationDate(expireAt);
			
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> payload = objectMapper.convertValue(authorization, Map.class);
			
			authorization.setJwt(JWTUtil.createJWT(payload, expireAt));

			return authorizationRepository.save(authorization);
			
		}).orElse(null);
		
	}
	
	public boolean tokenIsValid(String token) {
		return authorizationRepository.findByToken(token)
				.map(auth -> new Date().before(auth.getExpirationDate()))
				.orElse(false);
	}
	
	public boolean tokenIsValidAdmin(String token) {
		
		User user = new ObjectMapper().convertValue(JWTUtil.decodeJWT(token).get("user"), User.class);
		
		boolean isAdim = userRepository.findById(user.getId()).map(userFound -> Profile.ADMIN.equals(user.getProfile())).orElse(false);
		
		return authorizationRepository.findByToken(token)
				.map(auth -> new Date().before(auth.getExpirationDate()) && isAdim)
				.orElse(false);
	}

}

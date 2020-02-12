package com.ebix.easi.authorizationserver.util.email;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
public class EmailSender {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);

	private static String smtpHost;
	private static String smtpAuth;
	private static String smtpPort;
	private static String smtpStartTls;
	private static String smptAuthUser;
	private static String smtpAuthPassword;
	private static String fromAddress;

	public static void send(String subject, String to, String content, EmailContentType contentType)
			throws MessagingException, FileNotFoundException {

		TimeZone.setDefault(TimeZone.getTimeZone("GMT-3:00"));

		Properties props = new Properties();
		props.put("mail.smtp.auth", smtpAuth);
		props.put("mail.smtp.starttls.enable", smtpStartTls);// it’s optional in Mailtrap
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtps.auth", "true");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smptAuthUser, smtpAuthPassword);
			}
		});

		Message message = new MimeMessage(session);

		message.setFrom(new InternetAddress(fromAddress));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

		message.setSentDate(new Date());

		message.setSubject(subject);
		
		MimeMultipart multipart = new MimeMultipart("related");
		
		BodyPart messageBodyPart = new MimeBodyPart();
		
		messageBodyPart.setContent(content, contentType.getValue());
		
		multipart.addBodyPart(messageBodyPart);
		
		messageBodyPart = new MimeBodyPart();
		
		DataSource fds = new FileDataSource(
				ResourceUtils.getFile("classpath:img/ebix-logo_tn.jpg").getAbsolutePath());
		
		messageBodyPart.setDataHandler(new DataHandler(fds));
        
		messageBodyPart.setHeader("Content-ID", "<image>");
		
		multipart.addBodyPart(messageBodyPart);
        
        message.setContent(multipart);
		
		bypassSSL();
		
		Transport.send(message);

	}

	public static void bypassSSL() {
		final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
			}

			@Override
			public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };
		try {
			final SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			SSLContext.setDefault(sc);
		} catch (final Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Value("${ebix.smtp.host}")
	private void setSmtpHost(String smtpHost) {
		EmailSender.smtpHost = smtpHost;
	}

	@Value("${ebix.smtp.auth}")
	private void setSmtpAuth(String smtpAuth) {
		EmailSender.smtpAuth = smtpAuth;
	}

	@Value("${ebix.smtp.port}")
	private void setSmtpPort(String smtpPort) {
		EmailSender.smtpPort = smtpPort;
	}

	@Value("${ebix.smtp.starttls}")
	private void setSmtpStartTls(String smtpStartTls) {
		EmailSender.smtpStartTls = smtpStartTls;
	}

	@Value("${ebix.email.authentication.user}")
	private void setSmptAuthUser(String smptAuthUser) {
		EmailSender.smptAuthUser = smptAuthUser;
	}

	@Value("${ebix.email.authentication.password}")
	private void setSmtpAuthPassword(String smtpAuthPassword) {
		EmailSender.smtpAuthPassword = smtpAuthPassword;
	}

	@Value("${ebix.email.address}")
	private void setFromAddress(String fromAddress) {
		EmailSender.fromAddress = fromAddress;
	}

}

package com.ebix.easi.authorizationserver.util.email;

import java.util.regex.Matcher;

import com.ebix.easi.authorizationserver.model.entities.User;

public class EmailTemplate {

	public static String authenticationLink(User user, String appURL) {

		return AUTH_LINK.replaceFirst("\\{\\{userName\\}\\}", user.getName())
				.replaceFirst("\\{\\{userEmail\\}\\}", user.getEmail())
				.replaceFirst("\\{\\{userPassword\\}\\}", Matcher.quoteReplacement(user.getPassword()))
				.replaceFirst("\\{\\{authenticationLink\\}\\}", appURL);

	}

	private static String AUTH_LINK = "<p>Prezado(a) {{userName}},</p>\r\n"
			+ "<p>Voc&ecirc; foi eleito(a) &agrave; uma vistoria de autom&oacute;vel! Clique <strong><a href=\"{{authenticationLink}}\">aqui</a></strong> para efetuar seu primeiro acesso.</p>\r\n"
			+ "<ul>\r\n" + "<li>Login: {{userEmail}}</li>\r\n" + "<li>Senha: {{userPassword}}</li>\r\n" + "</ul>\r\n"
			+ "<p>Atenciosamente,</p>\r\n" + "<div><img src=\"cid:image\" width=\"75\" />&nbsp;</div>\r\n"
			+ "<div><strong>Easi - Ebix Assurance Self Inspection</strong></div>";

}

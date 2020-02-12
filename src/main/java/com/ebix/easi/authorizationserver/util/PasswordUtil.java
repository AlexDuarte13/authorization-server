package com.ebix.easi.authorizationserver.util;

public class PasswordUtil {
	
	public static String randomPassword() {
		int maxCharacteres = 8;
	    String[] characters = { "0", "1", "b", "2", "4", "5", "6", "7", "8",
	                "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
	                "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
	                "x", "y", "z", "@", "#", "&", "$", "_", "-", "%", "=", "*"};
	    
		StringBuilder password = new StringBuilder();

        for (int i = 0; i < maxCharacteres; i++) {
            int posicao = (int) (Math.random() * characters.length);
            password.append(characters[posicao]);
        }
        return password.toString();
	}

}

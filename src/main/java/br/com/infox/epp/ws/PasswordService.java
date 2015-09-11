package br.com.infox.epp.ws;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "passwordServiceWS";
	private final String hex = "0123456789ABCDEF";

	public String generatePasswordSalt() {
		return bin2hex(generateRandomSalt());
	}

	public String generatePasswordHash(String password, String saltHex) {
		byte[] salt = hex2bin(saltHex);
		try {
			return createPasswordKey(password.toCharArray(), salt, 1000).substring(0, 40);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	private byte[] generateRandomSalt() {
		int saltLength = 8;
		byte[] salt = new byte[saltLength];
		new SecureRandom().nextBytes(salt);
		return salt;
	}

	private String createPasswordKey(char[] password, byte[] salt, int iterations)
			throws GeneralSecurityException {
		PBEKeySpec passwordKeySpec = new PBEKeySpec(password, salt, iterations, 256);
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecretKey passwordKey = secretKeyFactory.generateSecret(passwordKeySpec);
		passwordKeySpec.clearPassword();
		return bin2hex(passwordKey.getEncoded());
	}
	
	private String bin2hex(final byte[] b) {
		if (b == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer(2 * b.length);
		for (int i = 0; i < b.length; i++) {
			int v = (256 + b[i]) % 256;
			sb.append(hex.charAt((v / 16) & 15));
			sb.append(hex.charAt((v % 16) & 15));
		}
		return sb.toString();
	}

	private byte[] hex2bin(final String s) {
		String m = s;
		if (s == null) {
			// Allow empty input string.
			m = "";
		} else if (s.length() % 2 != 0) {
			// Assume leading zero for odd string length
			m = "0" + s;
		}
		byte r[] = new byte[m.length() / 2];
		for (int i = 0, n = 0; i < m.length(); n++) {
			char h = m.charAt(i++);
			char l = m.charAt(i++);
			r[n] = (byte) (hex2bin(h) * 16 + hex2bin(l));
		}
		return r;
	}

	private int hex2bin(char c) {
		if (c >= '0' && c <= '9') {
			return (c - '0');
		}
		if (c >= 'A' && c <= 'F') {
			return (c - 'A' + 10);
		}
		if (c >= 'a' && c <= 'f') {
			return (c - 'a' + 10);
		}
		throw new IllegalArgumentException("Input string may only contain hex digits, but found '" + c + "'");
	}
}

package br.com.infox.ibpm.util;

import java.util.UUID;

public class BpmUtil {
	public static String generateKey() {
		return "key_" + UUID.randomUUID();
	}
}

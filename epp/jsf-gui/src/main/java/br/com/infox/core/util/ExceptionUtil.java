package br.com.infox.core.util;

import javax.persistence.OptimisticLockException;

public class ExceptionUtil {
	public static boolean isLockException(Exception exception) {
		return exception.getCause() instanceof OptimisticLockException || exception instanceof OptimisticLockException;
	}
}

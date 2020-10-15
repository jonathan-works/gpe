package br.com.infox.core.util;

import javax.persistence.OptimisticLockException;

public class ExceptionUtil {
	public static boolean isLockException(Exception exception) {
		return exception.getCause() instanceof OptimisticLockException || exception instanceof OptimisticLockException;
	}

    @SuppressWarnings("unchecked")
    public static <T> T findException(Exception exception, Class<T> exceptionClass) {
        if (exceptionClass.isInstance(exception)) {
            return (T) exception;
        }

        if (exception != null) {
            if (exception.getCause() instanceof Exception) {
                return findException((Exception) exception.getCause(), exceptionClass);
            }
        }

        return null;
    }

}

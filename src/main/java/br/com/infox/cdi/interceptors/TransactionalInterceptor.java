package br.com.infox.cdi.interceptors;

import static org.jboss.seam.util.EJB.APPLICATION_EXCEPTION;
import static org.jboss.seam.util.EJB.rollback;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.util.Persistence;

import br.com.infox.cdi.annotations.Transactional;

@Transactional
@Interceptor
public class TransactionalInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Resource
	private UserTransaction userTransaction;
	
	@AroundInvoke
	public Object openIfNoTransaction(InvocationContext ic) throws Exception {
		boolean startedTransaction = false;
		if (userTransaction.getStatus() != Status.STATUS_ACTIVE) {
			userTransaction.begin();
			startedTransaction = true;
		}
		Object result;
		try {
			result = ic.proceed();
			if (startedTransaction) {
				userTransaction.commit();
			}
		} catch (Exception e) {
			if (startedTransaction && isRollbackRequired(e)) {
				userTransaction.rollback();
			} else {
				userTransaction.commit();
			}
			throw e;
		}
		return result;
	}
	
	private static boolean isRollbackRequired(Exception e)
	   {
	      Class<? extends Exception> clazz = e.getClass();
	      return ( isSystemException(e, clazz) ) || 
	            ( clazz.isAnnotationPresent(APPLICATION_EXCEPTION) && rollback( clazz.getAnnotation(APPLICATION_EXCEPTION) ) ) ||
	            ( clazz.isAnnotationPresent(ApplicationException.class) && clazz.getAnnotation(ApplicationException.class).rollback() );
	   }

	   private static boolean isSystemException(Exception e, Class<? extends Exception> clazz)
	   {
	      return (e instanceof RuntimeException) && 
	            !clazz.isAnnotationPresent(APPLICATION_EXCEPTION) && 
	            !clazz.isAnnotationPresent(ApplicationException.class) &&
	            !Persistence.NO_RESULT_EXCEPTION.isInstance(e) && 
	            !Persistence.NON_UNIQUE_RESULT_EXCEPTION.isInstance(e);
	   }
}

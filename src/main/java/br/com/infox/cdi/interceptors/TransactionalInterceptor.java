package br.com.infox.cdi.interceptors;

import static org.jboss.seam.util.EJB.APPLICATION_EXCEPTION;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRequiredException;

import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.util.EJB;
import org.jboss.seam.util.Persistence;

import br.com.infox.cdi.annotations.Transactional;

@Transactional
@Interceptor
public class TransactionalInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Resource(lookup="java:jboss/TransactionManager")
	private TransactionManager transactionManager;
	
	@AroundInvoke
	public Object openIfNoTransaction(InvocationContext invocationContext) throws Exception {
		boolean startedTransaction = false;
		Transaction transactionSuspended = null;
		Transactional transactional = invocationContext.getMethod().getAnnotation(Transactional.class);
		switch (transactional.value()) {
		case REQUIRED:
			if (transactionManager.getTransaction() == null) {
				transactionManager.begin();
				startedTransaction = true;
			}
			break;
		case MANDATORY:
			if (transactionManager.getTransaction() == null) {
				throw new TransactionRequiredException("Transaction required");
			}
			break;
		case REQUIRES_NEW:
			if (transactionManager.getTransaction() == null) {
				transactionManager.begin();
				startedTransaction = true;
			} else if (transactionManager.getTransaction().getStatus() == Status.STATUS_ACTIVE) {
				transactionSuspended = transactionManager.suspend();
				transactionManager.begin();
				startedTransaction = true;
			}
			break;
		case NOT_SUPPORTED:
			if (transactionManager.getTransaction() != null) {
				transactionSuspended = transactionManager.suspend();
			}
			break;
		case NEVER:
			if (transactionManager.getTransaction() != null) {
				throw new TransactionRequiredException("Method should be called with no transaction");
			}
			break;
		case SUPPORTS: 
			// Do nothing
			break;
		}
		Object result = null;
		try {
			result = invocationContext.proceed();
			if (startedTransaction) {
				transactionManager.commit();
			}
		} catch (Exception e) {
			if (startedTransaction && isRollbackRequired(e, transactional)) {
				transactionManager.rollback();
				if (transactionSuspended == null) {
					throw e;
				}
			} else if (transactionManager.getTransaction() != null) {
				transactionManager.commit();
			}
		} finally {
			if (transactionSuspended != null) {
				transactionManager.resume(transactionSuspended);
			}
		}
		return result;
	}
	
	private boolean isRollbackRequired(Exception e, Transactional transactional) {
		Class<? extends Exception> clazz = e.getClass();
		return ( isClazzInstanceContainedInClasses(clazz, transactional.rollbackOn()) )
				|| ( isSystemException(e, clazz) )
				|| ( clazz.isAnnotationPresent(APPLICATION_EXCEPTION) && EJB.rollback(clazz.getAnnotation(APPLICATION_EXCEPTION)) )
				|| ( clazz.isAnnotationPresent(ApplicationException.class) && clazz.getAnnotation(ApplicationException.class).rollback() ) ;
	}

	private boolean isSystemException(Exception e, Class<? extends Exception> clazz) {
		return (e instanceof RuntimeException) && 
	            !clazz.isAnnotationPresent(APPLICATION_EXCEPTION) && 
	            !clazz.isAnnotationPresent(ApplicationException.class) &&
	            !Persistence.NO_RESULT_EXCEPTION.isInstance(e) && 
	            !Persistence.NON_UNIQUE_RESULT_EXCEPTION.isInstance(e);
	}
	
	private boolean isClazzInstanceContainedInClasses(Class<? extends Exception> clazz, Class<? extends Exception>[] classes) {
		for (Class<?> klass : classes) {
			if (klass.equals(clazz)) {
				return true;
			}
		}
		return false;
	}
}

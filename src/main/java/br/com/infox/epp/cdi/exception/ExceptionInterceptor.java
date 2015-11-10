package br.com.infox.epp.cdi.exception;

import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@ExceptionHandled
@Interceptor
public class ExceptionInterceptor implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(ExceptionInterceptor.class);
	
	@AroundInvoke
	public Object handleException(InvocationContext context) throws Exception {
		ExceptionHandled annotation = context.getMethod().getAnnotation(ExceptionHandled.class);
		try {
			Object result = context.proceed();
			switch (annotation.value()) {
			case INACTIVE:
			case REMOVE:
				FacesMessages.instance().add(annotation.removedMessage());
				break;
			case PERSIST:
				FacesMessages.instance().add(annotation.createdMessage());
				break;
			case UPDATE:
				FacesMessages.instance().add(annotation.updatedMessage());
				break;
			default:
				break;
			}
			return result;
		} catch (Exception e) {
			LOG.error("", e);
			ActionMessagesService actionMessagesService = BeanManager.INSTANCE.getReference(ActionMessagesService.class);
			if (actionMessagesService != null) {
				actionMessagesService.handleGenericException(e, annotation.lockExceptionMessage());
			}
		}
		return null;
	}
}

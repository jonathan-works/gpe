package br.com.infox.epp.cdi.exception;

import java.io.Serializable;

import javax.ejb.EJBException;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.seam.faces.FacesMessages;

import com.google.common.base.Strings;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.log.LogErrorService;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.log.LogErro;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@ExceptionHandled
@Interceptor
public class ExceptionInterceptor implements Serializable {
    
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(ExceptionInterceptor.class);
	
	@AroundInvoke
	public Object handleException(InvocationContext context) throws Exception {
		ExceptionHandled annotation = context.getMethod().getAnnotation(ExceptionHandled.class);
		if (annotation == null) {
			annotation = context.getMethod().getDeclaringClass().getAnnotation(ExceptionHandled.class);
		}
		try {
			Object result = context.proceed();
			switch (annotation.value()) {
			case INACTIVE:
				FacesMessages.instance().add(annotation.inactivatedMessage());
				break;
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
			    if (!StringUtil.isEmpty(annotation.successMessage())) {
			        FacesMessages.instance().add(annotation.successMessage());
			    }
				break;
			}
			return result;
		} catch (BusinessException e) {
		    FacesMessages.instance().add(e.getMessage());
		} catch (Exception e) {
            if (e.getCause() != null && (e instanceof EJBException)){
                e = (Exception) e.getCause();
            }
		    if (annotation.createLogErro()) {
		        createLogErro(e);
		    } else {
		        LOG.error("", e);
		        ActionMessagesService actionMessagesService = Beans.getReference(ActionMessagesService.class);
		        if (actionMessagesService != null) {
		            if (!Strings.isNullOrEmpty(annotation.lockExceptionMessage())) {
		            	actionMessagesService.handleGenericException(e, annotation.lockExceptionMessage());
		            } else {
		            	actionMessagesService.handleGenericException(e);
		            }
		        }
		    }
		    if (!StringUtil.isEmpty(annotation.errorMessage())) {
	            FacesMessages.instance().add(annotation.errorMessage());
	        }
		}
		return null;
	}
	
    private void createLogErro(Exception e) {
        LogErro logErro = getLogErroService().log(e);
        LOG.error(logErro.getCodigo(), e);
        FacesMessages.instance().add("Código de Erro: " + logErro.getCodigo() + " Mensagem: " + e.getMessage());
    }
	
	private LogErrorService getLogErroService() {
	    return Beans.getReference(LogErrorService.class);
	}
}

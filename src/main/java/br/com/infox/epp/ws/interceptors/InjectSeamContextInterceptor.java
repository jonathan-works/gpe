package br.com.infox.epp.ws.interceptors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.seam.contexts.Lifecycle;

@InjectSeamContext @Interceptor
public class InjectSeamContextInterceptor {

	@AroundInvoke
	private Object injetarCOntextoSeam(InvocationContext ctx) throws Exception {
		Lifecycle.beginCall();
		try {
			return ctx.proceed();
		} finally {
			Lifecycle.endCall();
		}
	}

}

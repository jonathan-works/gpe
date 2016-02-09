package br.com.infox.epp.quartz.client;

import br.com.infox.core.report.RequestInternalPageService;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.quartz.ws.QuartzResource;
import br.com.infox.epp.quartz.ws.QuartzRest;
import br.com.infox.ws.factory.RestClientFactory;

public class QuartzRestFactory {
    
    public static QuartzResource create() {
        return RestClientFactory.create(getUrl(), QuartzRest.class).getQuartzResource(getKey());
    }
    
    private static String getUrl() {
        return getRequestInternalPageService().getResquestUrlRest();
    }
    
    private static String getKey() {
        return getRequestInternalPageService().getKey().toString();
    }
    
    private static RequestInternalPageService getRequestInternalPageService() {
        return BeanManager.INSTANCE.getReference(RequestInternalPageService.class);
    }

}

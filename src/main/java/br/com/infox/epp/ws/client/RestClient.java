package br.com.infox.epp.ws.client;

import java.text.SimpleDateFormat;

import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.cdi.config.BeanManager;

public class RestClient {
    
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    
    public static <T>  T construct(Class<T> resourceClass, String url) {
        JAXRSClientFactoryBean factoryBean = new JAXRSClientFactoryBean();
        factoryBean.setAddress(url);
        factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_PATTERN));
        factoryBean.setProvider(new JacksonJsonProvider(Annotations.values()));
        factoryBean.setResourceClass(resourceClass);
        return resourceClass.cast(factoryBean.create(resourceClass));
    }
    
    public static <T>  T constructInternal(Class<T> resourceClass) {
        JAXRSClientFactoryBean factoryBean = new JAXRSClientFactoryBean();
        factoryBean.setAddress(BeanManager.INSTANCE.getReference(ApplicationServerService.class).getBaseResquestUrl()+"/rest");
        factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_PATTERN));
        factoryBean.setProvider(new JacksonJsonProvider(Annotations.values()));
        factoryBean.setResourceClass(resourceClass);
        return resourceClass.cast(factoryBean.create(resourceClass));
    }

}

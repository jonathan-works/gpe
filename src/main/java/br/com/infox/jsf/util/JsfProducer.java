package br.com.infox.jsf.util;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.Nonbinding;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Qualifier;

import br.com.infox.core.util.ObjectUtil;
import br.com.infox.core.util.StringUtil;
import lombok.NoArgsConstructor;


public class JsfProducer {

    @Inject
    private JsfUtil jsfUtil;
    
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface RequestParam {
        
        @Nonbinding String name() default "";
        
        @Nonbinding Class<? extends Converter> converterClass() default Converter.class;
         
    }
    
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface FlashParam {
        
        @Nonbinding String name() default "";
        
    }
    

    @Produces 
    @RequestParam
    @SuppressWarnings("unchecked")
    public <V> ParamValue<V> produceRequest(InjectionPoint injectionPoint) {
        RequestParam requestParam = injectionPoint.getAnnotated().getAnnotation(RequestParam.class);
        String paramName = getParamName(injectionPoint, requestParam.name());
        String submittedValue = jsfUtil.getRequestParameter(paramName);
        Object convertedValue = null;
        Converter converter = getConverter(requestParam, getTargetType(injectionPoint));
        if (converter != null) {
            convertedValue = converter.getAsObject(FacesContext.getCurrentInstance(), FacesContext.getCurrentInstance().getViewRoot(), submittedValue);
        } else {
            convertedValue = submittedValue;
        }
        return (ParamValue<V>) new RequestParamValue<Object>(submittedValue, requestParam, getTargetType(injectionPoint), convertedValue);
    }
    
    @Produces
    @FlashParam
    @SuppressWarnings("unchecked")
    public <V> ParamValue<V> produceFlash(InjectionPoint injectionPoint) {
        FlashParam requestParam = injectionPoint.getAnnotated().getAnnotation(FlashParam.class);
        String paramName = getParamName(injectionPoint, requestParam.name());
        Object submittedValue = jsfUtil.getFlashParam(paramName, getTargetType(injectionPoint));
        return (ParamValue<V>) new FlashParamValue<Object>(submittedValue);
    }
    
    public static Converter getConverter(RequestParam requestParameter, Class<?> targetType) {

        Class<? extends Converter> converterClass = requestParameter.converterClass();

        Converter converter = null;

        if (!converterClass.equals(Converter.class)) {
            converter = instance(converterClass);
        }

        if (converter == null) {
            try {
                converter = FacesContext.getCurrentInstance().getApplication().createConverter(targetType);
            } catch (Exception e) {
                return null;
            }
        }

        return converter;
    }
    
    public static String getParamName(InjectionPoint injectionPoint, String paramName) {
        if ( StringUtil.isEmpty(paramName) ) {
            return injectionPoint.getMember().getName();
        } else {
            return paramName;
        }
    }
    
    @SuppressWarnings("unchecked")
    private <V> Class<V> getTargetType(InjectionPoint injectionPoint) {
        Type type = injectionPoint.getType();
        if (type instanceof ParameterizedType) {
            return (Class<V>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        return null;
    }
    
    private static <T> T instance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    @NoArgsConstructor
    public static abstract class ParamValue<V> implements Serializable {
        
        private static final long serialVersionUID = 1L;

        public abstract V getValue();
        
        public boolean isNull() {
            return ObjectUtil.isEmpty(getValue());
        }
    }
    
    public static class RequestParamValue<V> extends ParamValue<V> {
        
        private static final long serialVersionUID = 1L;
        
        private final String submittedValue;
        private final RequestParam requestParameter;
        private final Class<?> targetType;
        
        private transient V value;
        private transient boolean valueSet;
        
        public RequestParamValue(String submittedValue, RequestParam requestParameter, Class<?> targetType, V value) {
            this.submittedValue = submittedValue;
            this.requestParameter = requestParameter;
            this.targetType = targetType;
            this.value = value;
            valueSet = true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V getValue() {
            
            if (!valueSet) {
                
                Converter converter = JsfProducer.getConverter(requestParameter, targetType);
                
                Object convertedValue;
                if (converter != null) {
                    convertedValue = converter.getAsObject(FacesContext.getCurrentInstance(), FacesContext.getCurrentInstance().getViewRoot(), submittedValue);
                } else {
                    convertedValue = submittedValue;
                }
                
                value = (V) convertedValue;
                
                valueSet = true;
                
            }
            return value;
        }

        
    }
    
    public static class FlashParamValue<V> extends ParamValue<V> {
        
        private static final long serialVersionUID = 1L;
        
        private final V value;
        
        
        public FlashParamValue(V value) {
            this.value = value;
        }

        @Override
        public V getValue() {
            return value;
        }
    }
    
}


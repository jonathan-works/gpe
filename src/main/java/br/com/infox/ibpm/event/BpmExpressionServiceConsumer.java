package br.com.infox.ibpm.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class BpmExpressionServiceConsumer {

    private static final class LazyLoader {
        private static final BpmExpressionServiceConsumer INSTANCE = new BpmExpressionServiceConsumer();
    }
    
    public static final BpmExpressionServiceConsumer instance(){
        return LazyLoader.INSTANCE;
    }
    
    private BpmExpressionServiceConsumer() {
    }

    public final List<ExternalMethod> getExternalMethods(BpmExpressionService bpmExpressionService){
        List<ExternalMethod> methods = new ArrayList<>();
        for (Method method : bpmExpressionService.getClass().getMethods()) {
            if (method.isAnnotationPresent(External.class)) {
                methods.add(new ExternalMethod(method));
            }
        }
        return methods;
    }
    
}
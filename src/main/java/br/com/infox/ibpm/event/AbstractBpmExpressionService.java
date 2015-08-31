package br.com.infox.ibpm.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AbstractBpmExpressionService implements BpmExpressionService {

    public AbstractBpmExpressionService() {
    }

    public List<Method> getMethods() {
        List<Method> methods = new ArrayList<>();
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(External.class)) {
                methods.add(method);
            }
        }
        return methods;
    }

}
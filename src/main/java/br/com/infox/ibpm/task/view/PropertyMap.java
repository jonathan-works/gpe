package br.com.infox.ibpm.task.view;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.LinkedHashMap;

import org.jboss.seam.core.Expressions;

@SuppressWarnings(UNCHECKED)
public class PropertyMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1L;

    public PropertyMap() {
    }

    @Override
    public V get(Object key) {
        V o = super.get(key);
        if (o instanceof String) {
            String s = (String) o;
            if (s.startsWith("#{")) {
                Object value = Expressions.instance().createValueExpression(s).getValue();
                o = (V) value;
            }
        }
        return o;
    }
}

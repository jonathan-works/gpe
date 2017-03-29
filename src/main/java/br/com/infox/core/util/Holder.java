package br.com.infox.core.util;

import java.io.Serializable;

public class Holder<V> implements Serializable {

    private static final long serialVersionUID = 1L;

    public V value;
    
    public Holder() {
    }

    public Holder(V value) {
        this.value = value;
    }
    
}

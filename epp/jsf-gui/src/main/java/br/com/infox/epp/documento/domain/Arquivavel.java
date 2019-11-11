package br.com.infox.epp.documento.domain;

import java.io.Serializable;

public interface Arquivavel {
    Serializable getId();
    Pasta getPasta();
    Boolean getExcluido();
}

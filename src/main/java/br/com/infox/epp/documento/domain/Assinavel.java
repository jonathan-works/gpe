package br.com.infox.epp.documento.domain;

import java.io.Serializable;
import java.util.List;

public interface Assinavel {
    Serializable getId();

    List<? extends RegraAssinatura> getRegrasAssinatura();
    
    List<? extends Assinatura> getAssinaturas();
}

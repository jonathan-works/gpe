package br.com.infox.epp.documento.domain;

import java.io.Serializable;

import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;

public interface Pasta {
    public static interface Permissao {
        Integer getAlvo();
        PastaRestricaoEnum getTipoPastaRestricao();
        Boolean getRead();
        Boolean getWrite();
        Boolean getRemove();
        Boolean getLogicDelete();
    }

    Serializable getId();
    String getNome();
    
}

package br.com.infox.epp.documento.domain;

import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;

public interface RegraAssinatura{

    Papel getPapel();

    TipoAssinaturaEnum getTipoAssinatura();

    Boolean getAssinaturasMultiplas();
    
}
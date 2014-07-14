package br.com.infox.epp.processo.documento.assinatura;

import br.com.infox.epp.documento.entity.TipoProcessoDocumento;

public class DadosDocumentoAssinavel {
    private TipoProcessoDocumento classificacao;
    private Integer idDocumento;
    
    public TipoProcessoDocumento getClassificacao() {
        return classificacao;
    }
    
    public void setClassificacao(TipoProcessoDocumento classificacao) {
        this.classificacao = classificacao;
    }
    
    public Integer getIdDocumento() {
        return idDocumento;
    }
    
    public void setIdDocumento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }
}

package br.com.infox.epp.processo.documento.assinatura;

import br.com.infox.epp.documento.entity.TipoProcessoDocumento;

public class DadosDocumentoAssinavel {
    private TipoProcessoDocumento classificacao;
    private Integer idDocumento;
    private String signature;
    private String certChain;
    
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCertChain() {
        return certChain;
    }

    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }
    
}

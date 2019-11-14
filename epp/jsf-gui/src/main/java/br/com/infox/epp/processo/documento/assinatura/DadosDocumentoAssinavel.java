package br.com.infox.epp.processo.documento.assinatura;

import br.com.infox.epp.documento.entity.ClassificacaoDocumento;

public class DadosDocumentoAssinavel {
    private ClassificacaoDocumento classificacao;
    private Integer idDocumento;
    private String signature;
    private String certChain;
    private boolean minuta = false;
    
    public ClassificacaoDocumento getClassificacao() {
        return classificacao;
    }
    
    public void setClassificacao(ClassificacaoDocumento classificacao) {
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
    
    public boolean isMinuta() {
		return minuta;
	}
    
    public void setMinuta(boolean minuta) {
		this.minuta = minuta;
	}
}

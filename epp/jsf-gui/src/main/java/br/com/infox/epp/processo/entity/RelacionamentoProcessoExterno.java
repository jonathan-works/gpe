package br.com.infox.epp.processo.entity;

import static br.com.infox.constants.LengthConstants.FLAG;
import static br.com.infox.constants.LengthConstants.NUMERACAO_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.NUMERO_PROCESSO;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@DiscriminatorValue(RelacionamentoProcesso.DISCRIMINATOR_RELACIONAMENTOS_EXTERNOS)
public class RelacionamentoProcessoExterno extends RelacionamentoProcesso {
	
	private static final long serialVersionUID = 1L;
	
    @NotNull
    @Size(min = FLAG, max = NUMERACAO_PROCESSO)
    @Column(name = NUMERO_PROCESSO, length = NUMERACAO_PROCESSO, nullable = false, unique = true)
	private String numeroProcesso;
    
    public RelacionamentoProcessoExterno() {
	}
	
    public RelacionamentoProcessoExterno(Relacionamento relacionamento, String numeroProcesso) {
    	this.relacionamento = relacionamento;
    	this.numeroProcesso = numeroProcesso;
	}
    
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

}

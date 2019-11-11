package br.com.infox.epp.processo.entity;

import static br.com.infox.epp.processo.query.ProcessoQuery.ID_PROCESSO;
import static javax.persistence.FetchType.LAZY;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue(RelacionamentoProcesso.DISCRIMINATOR_RELACIONAMENTOS_INTERNOS)
public class RelacionamentoProcessoInterno extends RelacionamentoProcesso {

	private static final long serialVersionUID = 1L;
	
	@NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ID_PROCESSO, nullable = true, unique = true)
    private Processo processo;

	protected RelacionamentoProcessoInterno() {
	}
	
    public RelacionamentoProcessoInterno(Relacionamento relacionamento, Processo processo) {
    	this.relacionamento = relacionamento;
    	this.processo = processo;
	}
    
    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

}

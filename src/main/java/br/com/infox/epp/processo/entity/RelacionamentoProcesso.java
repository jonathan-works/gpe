package br.com.infox.epp.processo.entity;

import static br.com.infox.constants.LengthConstants.FLAG;
import static br.com.infox.constants.LengthConstants.NUMERACAO_PROCESSO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.ID_RELACIONAMENTO_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.RELACIONAMENTO_BY_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.RELACIONAMENTO_BY_PROCESSO_QUERY;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.TABLE_NAME;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.ID_RELACIONAMENTO;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.type.Displayable;

@Entity
@Table(name = TABLE_NAME, uniqueConstraints = { @UniqueConstraint(columnNames = {
        NUMERO_PROCESSO, ID_RELACIONAMENTO }) })
@NamedQueries(value = { @NamedQuery(name = RELACIONAMENTO_BY_PROCESSO, query = RELACIONAMENTO_BY_PROCESSO_QUERY) })
public class RelacionamentoProcesso implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idRelacionamentoProcesso;
    private Processo processo;
    private Relacionamento relacionamento;
    private String numeroProcesso;
    private TipoProcesso tipoProcesso;
    
    

    public enum TipoProcesso implements Displayable {
        FIS("Físico"), ELE("Eletrônico");

        private String label;

        TipoProcesso(String label) {
            this.label = label;
        }

        @Override
        public String getLabel() {
            return label;
        }
    	
    }
    
    public RelacionamentoProcesso() {
    }

    public RelacionamentoProcesso(Relacionamento relacionamento,
            String numeroProcesso, TipoProcesso tipoProcesso) {
        this.relacionamento = relacionamento;
        this.numeroProcesso = numeroProcesso;
        this.tipoProcesso = tipoProcesso;
    }

    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_RELACIONAMENTO_PROCESSO, unique = true, nullable = false)
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_NAME)
    public Integer getIdRelacionamentoProcesso() {
        return idRelacionamentoProcesso;
    }

    public void setIdRelacionamentoProcesso(
            final Integer idRelacionamentoProcesso) {
        this.idRelacionamentoProcesso = idRelacionamentoProcesso;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ID_PROCESSO, nullable = true, unique = true)
    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    @NotNull
    @Size(min = FLAG, max = NUMERACAO_PROCESSO)
    @Column(name = NUMERO_PROCESSO, length = NUMERACAO_PROCESSO, nullable = false, unique = true)
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ID_RELACIONAMENTO, nullable = false)
    public Relacionamento getRelacionamento() {
        return relacionamento;
    }

    public void setRelacionamento(final Relacionamento relacionamento) {
        this.relacionamento = relacionamento;
    }

    @NotNull
    @Column(name = "tp_processo", nullable = false)
    @Enumerated(EnumType.STRING)
	public TipoProcesso getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(TipoProcesso tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeroProcesso == null) ? 0 : numeroProcesso.hashCode());
		result = prime * result + ((relacionamento == null) ? 0 : relacionamento.hashCode());
		result = prime * result + ((tipoProcesso == null) ? 0 : tipoProcesso.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelacionamentoProcesso other = (RelacionamentoProcesso) obj;
		if (numeroProcesso == null) {
			if (other.numeroProcesso != null)
				return false;
		} else if (!numeroProcesso.equals(other.numeroProcesso))
			return false;
		if (relacionamento == null) {
			if (other.relacionamento != null)
				return false;
		} else if (!relacionamento.equals(other.relacionamento))
			return false;
		if (tipoProcesso != other.tipoProcesso)
			return false;
		return true;
	}
	
	

}

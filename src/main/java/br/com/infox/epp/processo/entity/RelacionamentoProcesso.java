package br.com.infox.epp.processo.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.ID_RELACIONAMENTO_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.RELACIONAMENTO_BY_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.RELACIONAMENTO_BY_PROCESSO_QUERY;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.TABLE_NAME;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.ID_RELACIONAMENTO;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = TABLE_NAME)
@NamedQueries(value = { @NamedQuery(name = RELACIONAMENTO_BY_PROCESSO, query = RELACIONAMENTO_BY_PROCESSO_QUERY) })
@DiscriminatorColumn(name="tp_processo", discriminatorType = DiscriminatorType.STRING)
public abstract class RelacionamentoProcesso implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String DISCRIMINATOR_RELACIONAMENTOS_EXTERNOS = "EXT";
    public static final String DISCRIMINATOR_RELACIONAMENTOS_INTERNOS = "INT";

    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_RELACIONAMENTO_PROCESSO, unique = true, nullable = false)
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_NAME)
    protected Integer idRelacionamentoProcesso;
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ID_RELACIONAMENTO, nullable = false)
    protected Relacionamento relacionamento;
    
    public Integer getIdRelacionamentoProcesso() {
        return idRelacionamentoProcesso;
    }

    public void setIdRelacionamentoProcesso(
            final Integer idRelacionamentoProcesso) {
        this.idRelacionamentoProcesso = idRelacionamentoProcesso;
    }

    public Relacionamento getRelacionamento() {
        return relacionamento;
    }

    public void setRelacionamento(final Relacionamento relacionamento) {
        this.relacionamento = relacionamento;
    }

	
	

}

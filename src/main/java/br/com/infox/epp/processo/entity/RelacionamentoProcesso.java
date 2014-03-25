package br.com.infox.epp.processo.entity;

import static br.com.infox.core.constants.LengthConstants.FLAG;
import static br.com.infox.core.constants.LengthConstants.NUMERACAO_PROCESSO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = TABLE_NAME, schema = PUBLIC, uniqueConstraints = { @UniqueConstraint(columnNames = {
        NUMERO_PROCESSO, ID_RELACIONAMENTO }) })
@NamedQueries(value = { @NamedQuery(name = RELACIONAMENTO_BY_PROCESSO, query = RELACIONAMENTO_BY_PROCESSO_QUERY) })
public class RelacionamentoProcesso implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idRelacionamentoProcesso;
    private ProcessoEpa processo;
    private Relacionamento relacionamento;
    private String numeroProcesso;

    public RelacionamentoProcesso() {
    }

    public RelacionamentoProcesso(Relacionamento relacionamento,
            String numeroProcesso) {
        this.relacionamento = relacionamento;
        this.numeroProcesso = numeroProcesso;
    }

    @Id
    @GeneratedValue(generator = GENERATOR)
    @Column(name = ID_RELACIONAMENTO_PROCESSO, unique = true, nullable = false)
    @SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_NAME)
    public Integer getIdRelacionamentoProcesso() {
        return idRelacionamentoProcesso;
    }

    public void setIdRelacionamentoProcesso(
            final Integer idRelacionamentoProcesso) {
        this.idRelacionamentoProcesso = idRelacionamentoProcesso;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ID_PROCESSO, nullable = true, unique = true)
    public ProcessoEpa getProcesso() {
        return processo;
    }

    public void setProcesso(final ProcessoEpa processo) {
        this.processo = processo;
    }

    @NotNull
    @Length(min = FLAG, max = NUMERACAO_PROCESSO)
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((idRelacionamentoProcesso == null) ? 0
                        : idRelacionamentoProcesso.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RelacionamentoProcesso)) {
            return false;
        }
        final RelacionamentoProcesso other = (RelacionamentoProcesso) obj;
        if (idRelacionamentoProcesso == null) {
            if (other.idRelacionamentoProcesso != null) {
                return false;
            }
        } else if (!idRelacionamentoProcesso
                .equals(other.idRelacionamentoProcesso)) {
            return false;
        }
        return true;
    }

}
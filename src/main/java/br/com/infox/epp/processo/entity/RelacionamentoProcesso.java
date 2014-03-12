package br.com.infox.epp.processo.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_MEDIA;
import static br.com.infox.core.constants.LengthConstants.FLAG;
import static br.com.infox.core.constants.LengthConstants.NUMERACAO_PROCESSO;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.DATA_RELACIONAMENTO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.ID_RELACIONAMENTO_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.MOTIVO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.NOME_USUARIO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.RELACIONAMENTO_BY_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.RELACIONAMENTO_BY_PROCESSO_QUERY;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.TABLE_NAME;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.ID_RELACIONAMENTO;
import static br.com.infox.epp.processo.query.TipoRelacionamentoProcessoQuery.ID_TIPO_RELACIONAMENTO_PROCESSO;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = TABLE_NAME, schema = PUBLIC, uniqueConstraints = {
    @UniqueConstraint(columnNames = { ID_PROCESSO }),
    @UniqueConstraint(columnNames = { NUMERO_PROCESSO }) })
@NamedQueries(value = { @NamedQuery(name = RELACIONAMENTO_BY_PROCESSO, query = RELACIONAMENTO_BY_PROCESSO_QUERY) })
public class RelacionamentoProcesso implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idRelacionamentoProcesso;
    private TipoRelacionamentoProcesso tipoRelacionamentoProcesso;
    private ProcessoEpa processo;
    private Relacionamento relacionamento;
    private Date dataRelacionamento;
    private String numeroProcesso;
    private String motivo;
    private String nomeUsuario;
    private Boolean ativo;

    public RelacionamentoProcesso() {
    }

    public RelacionamentoProcesso(
            TipoRelacionamentoProcesso tipoRelacionamentoProcesso,
            Relacionamento relacionamento, ProcessoEpa processo, String motivo) {
        this.tipoRelacionamentoProcesso = tipoRelacionamentoProcesso;
        this.relacionamento = relacionamento;
        this.numeroProcesso = processo.getNumeroProcesso();
        this.processo = processo;
        this.motivo = motivo;
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

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ID_TIPO_RELACIONAMENTO_PROCESSO, nullable = false)
    public TipoRelacionamentoProcesso getTipoRelacionamentoProcesso() {
        return tipoRelacionamentoProcesso;
    }

    public void setTipoRelacionamentoProcesso(
            final TipoRelacionamentoProcesso tipoRelacionamentoProcesso) {
        this.tipoRelacionamentoProcesso = tipoRelacionamentoProcesso;
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

    @NotNull
    @Temporal(TIMESTAMP)
    @Column(name = DATA_RELACIONAMENTO, nullable = false)
    public Date getDataRelacionamento() {
        return dataRelacionamento;
    }

    public void setDataRelacionamento(final Date dataRelacionamento) {
        this.dataRelacionamento = dataRelacionamento;
    }

    @NotNull
    @Length(min = FLAG)
    @Column(name = MOTIVO, nullable = false)
    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(final String motivo) {
        this.motivo = motivo;
    }

    @NotNull
    @Length(min = FLAG, max = DESCRICAO_MEDIA)
    @Column(name = NOME_USUARIO, length = DESCRICAO_MEDIA, nullable = false)
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(final String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    @NotNull
    @Column(name = ATIVO, nullable = false)
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(final Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((idRelacionamentoProcesso == null) ? 0 : idRelacionamentoProcesso.hashCode());
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
        } else if (!idRelacionamentoProcesso.equals(other.idRelacionamentoProcesso)) {
            return false;
        }
        return true;
    }

}

package br.com.infox.epp.processo.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_MEDIA;
import static br.com.infox.core.constants.LengthConstants.FLAG;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.DATA_RELACIONAMENTO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.ID_RELACIONAMENTO_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.MOTIVO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.NOME_USUARIO;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name=TABLE_NAME, schema=PUBLIC, uniqueConstraints={
    @UniqueConstraint(columnNames={ID_PROCESSO})
})
public class RelacionamentoProcesso implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer idRelacionamentoProcesso;
    private TipoRelacionamentoProcesso tipoRelacionamentoProcesso;
    private Processo processo;
    private Relacionamento relacionamento;
    private Date dataRelacionamento;
    private String motivo;
    private String nomeUsuario;
    private Boolean ativo;
    
    @Id
    @GeneratedValue(generator=GENERATOR)
    @Column(name=ID_RELACIONAMENTO_PROCESSO, unique=true, nullable=false)
    @SequenceGenerator(name=GENERATOR, sequenceName=SEQUENCE_NAME)
    public Integer getIdRelacionamentoProcesso() {
        return idRelacionamentoProcesso;
    }
    public void setIdRelacionamentoProcesso(Integer idRelacionamentoProcesso) {
        this.idRelacionamentoProcesso = idRelacionamentoProcesso;
    }

    @NotNull
    @ManyToOne(fetch=LAZY)
    @JoinColumn(name=ID_TIPO_RELACIONAMENTO_PROCESSO, nullable=false)
    public TipoRelacionamentoProcesso getTipoRelacionamentoProcesso() {
        return tipoRelacionamentoProcesso;
    }
    public void setTipoRelacionamentoProcesso(
            TipoRelacionamentoProcesso tipoRelacionamentoProcesso) {
        this.tipoRelacionamentoProcesso = tipoRelacionamentoProcesso;
    }

    @NotNull
    @ManyToOne(fetch=LAZY)
    @JoinColumn(name=ID_PROCESSO, nullable=false, unique=true)
    public Processo getProcesso() {
        return processo;
    }
    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    @NotNull
    @ManyToOne(fetch=LAZY)
    @JoinColumn(name=ID_RELACIONAMENTO, nullable=false)
    public Relacionamento getRelacionamento() {
        return relacionamento;
    }
    public void setRelacionamento(Relacionamento relacionamento) {
        this.relacionamento = relacionamento;
    }

    @NotNull
    @Temporal(TIMESTAMP)
    @Column(name=DATA_RELACIONAMENTO, nullable=false)
    public Date getDataRelacionamento() {
        return dataRelacionamento;
    }
    public void setDataRelacionamento(Date dataRelacionamento) {
        this.dataRelacionamento = dataRelacionamento;
    }

    @NotNull
    @Length(min=FLAG)
    @Column(name=MOTIVO, nullable=false)
    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @NotNull
    @Length(min=FLAG, max=DESCRICAO_MEDIA)
    @Column(name=NOME_USUARIO, length=DESCRICAO_MEDIA, nullable=false)
    public String getNomeUsuario() {
        return nomeUsuario;
    }
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    @NotNull
    @Column(name=ATIVO, nullable=false)
    public Boolean getAtivo() {
        return ativo;
    }
    public void setAtivo(Boolean ativo) {
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RelacionamentoProcesso)) {
            return false;
        }
        RelacionamentoProcesso other = (RelacionamentoProcesso) obj;
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

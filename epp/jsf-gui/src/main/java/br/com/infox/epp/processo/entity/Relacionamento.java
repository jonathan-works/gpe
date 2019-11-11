package br.com.infox.epp.processo.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_MEDIA;
import static br.com.infox.constants.LengthConstants.FLAG;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.DATA_RELACIONAMENTO;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.ID_RELACIONAMENTO;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.MOTIVO;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.NOME_USUARIO;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.query.RelacionamentoQuery.TABLE_NAME;
import static br.com.infox.epp.processo.query.TipoRelacionamentoProcessoQuery.ID_TIPO_RELACIONAMENTO_PROCESSO;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = TABLE_NAME)
public class Relacionamento implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer idRelacionamento;
    private String motivo;
    private TipoRelacionamentoProcesso tipoRelacionamentoProcesso;
    private String nomeUsuario;
    private Date dataRelacionamento;
    private Boolean ativo;
    private Set<RelacionamentoProcesso> relacionamentosProcessos;

    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_RELACIONAMENTO, unique = true, nullable = false)
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_NAME)
    public Integer getIdRelacionamento() {
        return this.idRelacionamento;
    }

    public void setIdRelacionamento(final Integer idRelacionamento) {
        this.idRelacionamento = idRelacionamento;
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
    @Size(min = FLAG)
    @Column(name = MOTIVO, nullable = false)
    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(final String motivo) {
        this.motivo = motivo;
    }

    @NotNull
    @Size(min = FLAG, max = DESCRICAO_MEDIA)
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime
                * result)
                + ((idRelacionamento == null) ? 0 : idRelacionamento.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Relacionamento other = (Relacionamento) obj;
        if (idRelacionamento == null) {
            if (other.idRelacionamento != null) {
                return false;
            }
        } else if (!idRelacionamento.equals(other.idRelacionamento)) {
            return false;
        }
        return true;
    }

    @OneToMany(mappedBy="relacionamento", fetch=FetchType.LAZY)
	public Set<RelacionamentoProcesso> getRelacionamentosProcessos() {
		return relacionamentosProcessos;
	}

	public void setRelacionamentosProcessos(Set<RelacionamentoProcesso> relacionamentosProcessos) {
		this.relacionamentosProcessos = relacionamentosProcessos;
	}

}

package br.com.infox.epp.processo.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.constants.LengthConstants.FLAG;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.processo.query.TipoRelacionamentoProcessoQuery.ID_TIPO_RELACIONAMENTO_PROCESSO;
import static br.com.infox.epp.processo.query.TipoRelacionamentoProcessoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.query.TipoRelacionamentoProcessoQuery.TABLE_NAME;
import static br.com.infox.epp.processo.query.TipoRelacionamentoProcessoQuery.TIPO_RELACIONAMENTO;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = TABLE_NAME, schema = PUBLIC, uniqueConstraints = @UniqueConstraint(columnNames = { TIPO_RELACIONAMENTO }))
public class TipoRelacionamentoProcesso implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idTipoRelacionamento;
    private String tipoRelacionamento;
    private Boolean ativo;

    @Id
    @GeneratedValue(generator = GENERATOR)
    @Column(name = ID_TIPO_RELACIONAMENTO_PROCESSO, unique = true, nullable = false)
    @SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_NAME)
    public Integer getIdTipoRelacionamento() {
        return idTipoRelacionamento;
    }

    public void setIdTipoRelacionamento(final Integer idTipoRelacionamento) {
        this.idTipoRelacionamento = idTipoRelacionamento;
    }

    @NotNull
    @Length(min = FLAG, max = DESCRICAO_PADRAO)
    @Column(name = TIPO_RELACIONAMENTO, nullable = false, length = DESCRICAO_PADRAO, unique = true)
    public String getTipoRelacionamento() {
        return tipoRelacionamento;
    }

    public void setTipoRelacionamento(final String tipoRelacionamento) {
        this.tipoRelacionamento = tipoRelacionamento;
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
                + ((idTipoRelacionamento == null) ? 0 : idTipoRelacionamento.hashCode());
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
        final TipoRelacionamentoProcesso other = (TipoRelacionamentoProcesso) obj;
        if (idTipoRelacionamento == null) {
            if (other.idTipoRelacionamento != null) {
                return false;
            }
        } else if (!idTipoRelacionamento.equals(other.idTipoRelacionamento)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return tipoRelacionamento;
    }

}

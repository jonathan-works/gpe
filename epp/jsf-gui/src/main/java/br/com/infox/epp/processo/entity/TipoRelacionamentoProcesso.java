package br.com.infox.epp.processo.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.constants.LengthConstants.FLAG;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.processo.query.TipoRelacionamentoProcessoQuery.ID_TIPO_RELACIONAMENTO_PROCESSO;
import static br.com.infox.epp.processo.query.TipoRelacionamentoProcessoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.query.TipoRelacionamentoProcessoQuery.TABLE_NAME;
import static br.com.infox.epp.processo.query.TipoRelacionamentoProcessoQuery.TIPO_RELACIONAMENTO;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name = TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = { TIPO_RELACIONAMENTO }))
public class TipoRelacionamentoProcesso implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idTipoRelacionamento;    
    private String codigo;
	private String tipoRelacionamento;
    private Boolean ativo;

    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_TIPO_RELACIONAMENTO_PROCESSO, unique = true, nullable = false)
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_NAME)
    public Integer getIdTipoRelacionamento() {
        return idTipoRelacionamento;
    }

    public void setIdTipoRelacionamento(final Integer idTipoRelacionamento) {
        this.idTipoRelacionamento = idTipoRelacionamento;
    }

    @NotNull
    @Size(min = FLAG, max = DESCRICAO_PADRAO)
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
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		TipoRelacionamentoProcesso other = (TipoRelacionamentoProcesso) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return tipoRelacionamento;
    }

    @NotNull
    @Size(min = FLAG, max = LengthConstants.CODIGO_DOCUMENTO)
    @Column(name = "cd_tipo_relaciona_processo")
    public String getCodigo() {
    	return codigo;
    }
    
    public void setCodigo(String codigo) {
    	this.codigo = codigo;
    }
}

package br.com.infox.ibpm.variable.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.COLUMN_DOMINIO;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.COLUMN_ID;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.COLUMN_NOME;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.SEQUENCE_NAME;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.TABLE_NAME;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name = TABLE_NAME)
public class DominioVariavelTarefa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = COLUMN_ID)
    private Integer id;

    @Column(name = COLUMN_DOMINIO, nullable = false, columnDefinition = "TEXT")
    private String dominio;

    @Column(name = COLUMN_NOME, nullable = false, length = LengthConstants.NOME_MEDIO)
    private String nome;
    
    @NotNull
    @Size(min = 1, max = LengthConstants.DESCRICAO_PEQUENA)
    @Column(name = "cd_dominio_variavel_tarefa", length = LengthConstants.DESCRICAO_PEQUENA)
    private String codigo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    @Transient
    public boolean isDominioSqlQuery(){
    	return dominio.startsWith("select");
    }

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DominioVariavelTarefa))
			return false;
		DominioVariavelTarefa other = (DominioVariavelTarefa) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
    
}

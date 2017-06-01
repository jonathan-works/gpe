package br.com.infox.epp.access.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.persistence.ORConstants;
import br.com.infox.epp.access.query.EstruturaQuery;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = EstruturaQuery.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = EstruturaQuery.COLUMN_NOME))
@NamedQueries({
    @NamedQuery(name = EstruturaQuery.ESTRUTURAS_DISPONIVEIS, query = EstruturaQuery.ESTRUTURAS_DISPONIVEIS_QUERY),
    @NamedQuery(name = EstruturaQuery.ESTRUTURA_BY_NOME, query = EstruturaQuery.ESTRUTURA_BY_NOME_QUERY)
})
public class Estrutura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = ORConstants.GENERATOR, sequenceName = EstruturaQuery.SEQUENCE_NAME, allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = ORConstants.GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = EstruturaQuery.COLUMN_ID)
    private Integer id;

    @NotNull
    @Size(max = LengthConstants.DESCRICAO_ENTIDADE)
    @Column(name = "cd_estrutura", length = LengthConstants.CODIGO_DOCUMENTO, unique = true)
    @Getter @Setter
    private String codigo;
    
    @Size(min = LengthConstants.FLAG, max = LengthConstants.DESCRICAO_ENTIDADE)
    @Column(name = EstruturaQuery.COLUMN_NOME, length = LengthConstants.DESCRICAO_ENTIDADE, nullable = false, unique = true)
    private String nome;

    @Column(name = ORConstants.ATIVO, nullable = false)
    private Boolean ativo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "estruturaPai")
    private List<Localizacao> localizacoes = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<Localizacao> getLocalizacoes() {
        return localizacoes;
    }

    public void setLocalizacoes(List<Localizacao> localizacoes) {
        this.localizacoes = localizacoes;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((getId() == null) ? 0 : getId().hashCode());
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
        if (!(obj instanceof Estrutura)) {
            return false;
        }
        Estrutura other = (Estrutura) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }
}

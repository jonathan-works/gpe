package br.com.infox.epp.documento.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.documento.query.VariavelQuery.DESCRICAO_VARIAVEL;
import static br.com.infox.epp.documento.query.VariavelQuery.ID_VARIAVEL;
import static br.com.infox.epp.documento.query.VariavelQuery.SEQUENCE_VARIAVEL;
import static br.com.infox.epp.documento.query.VariavelQuery.TABLE_VARIAVEL;
import static br.com.infox.epp.documento.query.VariavelQuery.VALOR_VARIAVEL;
import static br.com.infox.epp.documento.query.VariavelQuery.VARIAVEL_ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name = TABLE_VARIAVEL, uniqueConstraints = @UniqueConstraint(columnNames = DESCRICAO_VARIAVEL) )
public class Variavel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idVariavel;
    private String variavel;
    private String valorVariavel;
    private Boolean ativo;

    private List<VariavelTipoModelo> variavelTipoModeloList = new ArrayList<VariavelTipoModelo>(0);

    public Variavel() {
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_VARIAVEL)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_VARIAVEL, unique = true, nullable = false)
    public int getIdVariavel() {
        return this.idVariavel;
    }

    public void setIdVariavel(int idVariavel) {
        this.idVariavel = idVariavel;
    }

    @Column(name = DESCRICAO_VARIAVEL, nullable = false, length = LengthConstants.DESCRICAO_PADRAO, unique = true)
    @NotNull
    @Size(min = 2, max = LengthConstants.DESCRICAO_PADRAO)
    public String getVariavel() {
        return this.variavel;
    }

    public void setVariavel(String variavel) {
        String var = "";
        if ((variavel != null) && (variavel.trim().length() > 0)) {
            var = variavel.replace(" ", "_");
        }
        this.variavel = var;
    }

    @Column(name = VALOR_VARIAVEL, nullable = false, length = LengthConstants.DESCRICAO_PADRAO_DOBRO)
    @NotNull
    @Size(min = LengthConstants.FLAG, max = LengthConstants.DESCRICAO_PADRAO_DOBRO)
    public String getValorVariavel() {
        return this.valorVariavel;
    }

    public void setValorVariavel(String valorVariavel) {
        this.valorVariavel = valorVariavel;
    }

    @Column(name = ATIVO, nullable = false)
    @NotNull
    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = VARIAVEL_ATTRIBUTE)
    public List<VariavelTipoModelo> getVariavelTipoModeloList() {
        return variavelTipoModeloList;
    }

    public void setVariavelTipoModeloList(
            List<VariavelTipoModelo> variavelTipoModeloList) {
        this.variavelTipoModeloList = variavelTipoModeloList;
    }

    @Override
    public String toString() {
        return variavel;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Variavel)) {
            return false;
        }
        Variavel other = (Variavel) obj;
        return getIdVariavel() == other.getIdVariavel();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + getIdVariavel();
        return result;
    }
}

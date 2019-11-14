package br.com.infox.epp.processo.partes.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.processo.partes.query.TipoParteQuery;

@Entity
@Table(name = TipoParte.TABLE_NAME)
@NamedQueries(value = {
		@NamedQuery(name=TipoParteQuery.TIPO_PARTE_BY_IDENTIFICADOR, query=TipoParteQuery.TIPO_PARTE_BY_IDENTIFICADOR_QUERY)
})
public class TipoParte implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_tipo_parte";

    @Id
    @SequenceGenerator(name = "TipoParteGenerator", allocationSize = 1, sequenceName = "sq_tipo_parte")
    @GeneratedValue(generator = "TipoParteGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_parte")
    private Long id;
    
    @NotNull
    @Size(min= 1, max = 50)
    @Column(name = "nm_tipo_parte", nullable = false, unique = true, length = 500)
    private String identificador;
    
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "ds_tipo_parte", nullable = false, length = 200)
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result
                + ((getIdentificador() == null) ? 0 : getIdentificador().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TipoParte))
            return false;
        TipoParte other = (TipoParte) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        if (getIdentificador() == null) {
            if (other.getIdentificador() != null)
                return false;
        } else if (!getIdentificador().equals(other.getIdentificador()))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}

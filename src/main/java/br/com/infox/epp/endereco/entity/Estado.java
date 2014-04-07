package br.com.infox.epp.endereco.entity;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;

@Entity
@Table(name = "tb_estado")
public class Estado implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idEstado;
    private String codEstado;
    private String estado;
    private Boolean ativo;
    private List<Municipio> municipioList = new ArrayList<Municipio>(0);

    public Estado() {
    }

    @SequenceGenerator(name = "generator", sequenceName = "sq_tb_estado")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_estado", unique = true, nullable = false)
    public int getIdEstado() {
        return this.idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    @Column(name = "ds_estado", length = LengthConstants.DESCRICAO_PEQUENA)
    @Size(max = LengthConstants.DESCRICAO_PEQUENA)
    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Column(name = "cd_estado", length = LengthConstants.UF)
    @Size(max = LengthConstants.UF)
    public String getCodEstado() {
        return this.codEstado;
    }

    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }

    @Column(name = "in_ativo", nullable = false)
    @NotNull
    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
        CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = "estado")
    public List<Municipio> getMunicipioList() {
        return municipioList;
    }

    public void setMunicipioList(List<Municipio> municipioList) {
        this.municipioList = municipioList;
    }

    @Override
    public String toString() {
        return estado;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Estado)) {
            return false;
        }
        Estado other = (Estado) obj;
        if (getIdEstado() != other.getIdEstado()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdEstado();
        return result;
    }
}

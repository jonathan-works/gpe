package br.com.infox.epp.endereco.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;

@Entity
@Table(name = "tb_municipio", schema = "public")
public class Municipio implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idMunicipio;
    private String municipio;
    private Estado estado;
    private Boolean ativo;

    private List<Cep> cepList = new ArrayList<Cep>(0);

    public Municipio() {
    }

    @SequenceGenerator(name = "generator",
            sequenceName = "public.sq_tb_municipio")
    @Id
    @GeneratedValue(generator = "generator")
    @Column(name = "id_municipio", unique = true, nullable = false)
    public int getIdMunicipio() {
        return this.idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    @Column(name = "ds_municipio",
            length = LengthConstants.DESCRICAO_PADRAO_METADE)
    @Size(max = LengthConstants.DESCRICAO_PADRAO_METADE)
    public String getMunicipio() {
        return this.municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estado", nullable = false)
    @NotNull
    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
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
        CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = "municipio")
    public List<Cep> getCepList() {
        return cepList;
    }

    public void setCepList(List<Cep> cepList) {
        this.cepList = cepList;
    }

    @Override
    public String toString() {
        return municipio;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Municipio)) {
            return false;
        }
        Municipio other = (Municipio) obj;
        if (getIdMunicipio() != other.getIdMunicipio()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdMunicipio();
        return result;
    }
}

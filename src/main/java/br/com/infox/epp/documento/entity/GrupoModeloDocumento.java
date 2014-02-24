package br.com.infox.epp.documento.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PEQUENA;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.documento.query.GrupoModeloDocumentoQuery.DESCRICAO_GRUPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.GrupoModeloDocumentoQuery.ID_GRUPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.GrupoModeloDocumentoQuery.SEQUENCE_GRUPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.GrupoModeloDocumentoQuery.TABLE_GRUPO_MODELO_DOCUMENTO;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.util.HibernateUtil;

@Entity
@Table(name = TABLE_GRUPO_MODELO_DOCUMENTO, schema = PUBLIC)
public class GrupoModeloDocumento implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idGrupoModeloDocumento;
    private String grupoModeloDocumento;
    private Boolean ativo;
    private List<ItemTipoDocumento> itemTipoDocumentoList = new ArrayList<ItemTipoDocumento>(0);
    private List<TipoModeloDocumento> tipoModeloDocumentoList = new ArrayList<TipoModeloDocumento>(0);

    public GrupoModeloDocumento() {
    }

    @SequenceGenerator(name = GENERATOR,
            sequenceName = SEQUENCE_GRUPO_MODELO_DOCUMENTO)
    @Id
    @GeneratedValue(generator = GENERATOR)
    @Column(name = ID_GRUPO_MODELO_DOCUMENTO, unique = true, nullable = false)
    public int getIdGrupoModeloDocumento() {
        return this.idGrupoModeloDocumento;
    }

    public void setIdGrupoModeloDocumento(int idGrupoModeloDocumento) {
        this.idGrupoModeloDocumento = idGrupoModeloDocumento;
    }

    @Column(name = DESCRICAO_GRUPO_MODELO_DOCUMENTO, nullable = false,
            length = DESCRICAO_PEQUENA, unique = true)
    @Size(max = DESCRICAO_PEQUENA)
    @NotNull
    public String getGrupoModeloDocumento() {
        return this.grupoModeloDocumento;
    }

    public void setGrupoModeloDocumento(String grupoModeloDocumento) {
        this.grupoModeloDocumento = grupoModeloDocumento;
    }

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = FetchType.LAZY,
            mappedBy = "grupoModeloDocumento")
    public List<ItemTipoDocumento> getItemTipoDocumentoList() {
        return this.itemTipoDocumentoList;
    }

    public void setItemTipoDocumentoList(
            List<ItemTipoDocumento> itemTipoDocumentoList) {
        this.itemTipoDocumentoList = itemTipoDocumentoList;
    }

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = FetchType.LAZY,
            mappedBy = "grupoModeloDocumento")
    public List<TipoModeloDocumento> getTipoModeloDocumentoList() {
        return this.tipoModeloDocumentoList;
    }

    public void setTipoModeloDocumentoList(
            List<TipoModeloDocumento> tipoModeloDocumentoList) {
        this.tipoModeloDocumentoList = tipoModeloDocumentoList;
    }

    @Column(name = ATIVO, nullable = false)
    @NotNull
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return grupoModeloDocumento;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GrupoModeloDocumento)) {
            return false;
        }
        GrupoModeloDocumento other = (GrupoModeloDocumento) HibernateUtil.removeProxy(obj);
        if (getIdGrupoModeloDocumento() != other.getIdGrupoModeloDocumento()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdGrupoModeloDocumento();
        return result;
    }
}

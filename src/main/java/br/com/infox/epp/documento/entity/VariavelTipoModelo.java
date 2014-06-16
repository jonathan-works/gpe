package br.com.infox.epp.documento.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.documento.query.VariavelTipoModeloQuery.ID_TIPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.VariavelTipoModeloQuery.ID_VARIAVEL;
import static br.com.infox.epp.documento.query.VariavelTipoModeloQuery.ID_VARIAVEL_TIPO_MODELO;
import static br.com.infox.epp.documento.query.VariavelTipoModeloQuery.SEQUENCE_VARIAVEL_TIPO_MODELO;
import static br.com.infox.epp.documento.query.VariavelTipoModeloQuery.TABLE_VARIAVEL_TIPO_MODELO;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = TABLE_VARIAVEL_TIPO_MODELO)
public class VariavelTipoModelo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer idVariavelTipoModelo;
    private Variavel variavel;
    private TipoModeloDocumento tipoModeloDocumento;

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_VARIAVEL_TIPO_MODELO)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_VARIAVEL_TIPO_MODELO, unique = true, nullable = false)
    public Integer getIdVariavelTipoModelo() {
        return idVariavelTipoModelo;
    }

    public void setIdVariavelTipoModelo(Integer idVariavelTipoModelo) {
        this.idVariavelTipoModelo = idVariavelTipoModelo;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_VARIAVEL, nullable = false)
    @NotNull
    public Variavel getVariavel() {
        return variavel;
    }

    public void setVariavel(Variavel variavel) {
        this.variavel = variavel;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_TIPO_MODELO_DOCUMENTO, nullable = false)
    @NotNull
    public TipoModeloDocumento getTipoModeloDocumento() {
        return tipoModeloDocumento;
    }

    public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
        this.tipoModeloDocumento = tipoModeloDocumento;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getIdVariavelTipoModelo() == null) {
            return false;
        }
        if (!(obj instanceof VariavelTipoModelo)) {
            return false;
        }
        VariavelTipoModelo other = (VariavelTipoModelo) obj;
        return getIdVariavelTipoModelo().equals(other.getIdVariavelTipoModelo());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdVariavelTipoModelo();
        return result;
    }
}

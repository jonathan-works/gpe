package br.com.infox.epp.documento.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.CONTEUDO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.ID_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.ID_TIPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.LIST_ATIVOS;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.LIST_ATIVOS_QUERY;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_GRUPO_AND_TIPO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_GRUPO_AND_TIPO_QUERY;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_LISTA_IDS;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_LISTA_IDS_QUERY;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_TITULO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_TITULO_QUERY;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.SEQUENCE_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.TABLE_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.TITULO_MODELO_DOCUMENTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name = TABLE_MODELO_DOCUMENTO)
@NamedQueries(value = {
    @NamedQuery(name = LIST_ATIVOS, query = LIST_ATIVOS_QUERY),
    @NamedQuery(name = MODELO_BY_TITULO, query = MODELO_BY_TITULO_QUERY),
    @NamedQuery(name = MODELO_BY_GRUPO_AND_TIPO, query = MODELO_BY_GRUPO_AND_TIPO_QUERY),
    @NamedQuery(name = MODELO_BY_LISTA_IDS, query = MODELO_BY_LISTA_IDS_QUERY) })
public class ModeloDocumento implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_MODELO_DOCUMENTO)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_MODELO_DOCUMENTO, unique = true, nullable = false)
    private int idModeloDocumento;
    
    @NotNull
    @Size(min = 1, max = LengthConstants.DESCRICAO_PEQUENA)
    @Column(name = "cd_modelo_documento", nullable = false, unique = true)
    private String codigo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_TIPO_MODELO_DOCUMENTO, nullable = false)
    @NotNull
    private TipoModeloDocumento tipoModeloDocumento;
    
    @Column(name = TITULO_MODELO_DOCUMENTO, nullable = false, length = 80)
    @Size(max = 80)
    @NotNull
    private String tituloModeloDocumento;
    
    @Column(name = CONTEUDO_MODELO_DOCUMENTO, nullable = false)
    @NotNull
    private String modeloDocumento;
    
    @Column(name = ATIVO, nullable = false)
    @NotNull
    private Boolean ativo;

    public ModeloDocumento() {
    }

    public int getIdModeloDocumento() {
        return this.idModeloDocumento;
    }

    public void setIdModeloDocumento(int idModeloDocumento) {
        this.idModeloDocumento = idModeloDocumento;
    }

    public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public TipoModeloDocumento getTipoModeloDocumento() {
        return this.tipoModeloDocumento;
    }

    public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
        this.tipoModeloDocumento = tipoModeloDocumento;
    }

    public String getTituloModeloDocumento() {
        return this.tituloModeloDocumento;
    }

    public void setTituloModeloDocumento(String tituloModeloDocumento) {
        this.tituloModeloDocumento = tituloModeloDocumento;
    }

    public String getModeloDocumento() {
        return this.modeloDocumento;
    }

    public void setModeloDocumento(String modeloDocumento) {
        this.modeloDocumento = modeloDocumento;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return tituloModeloDocumento;
    }

    public boolean hasChanges(ModeloDocumento modelo) {
        if (modelo == null) {
            return true;
        }
        return !modelo.modeloDocumento.equals(this.modeloDocumento)
                || !modelo.tipoModeloDocumento.equals(this.tipoModeloDocumento)
                || !modelo.tituloModeloDocumento.equals(this.tituloModeloDocumento)
                || !modelo.ativo.equals(this.ativo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ModeloDocumento)) {
            return false;
        }
        ModeloDocumento other = (ModeloDocumento) obj;
        if (getIdModeloDocumento() != other.getIdModeloDocumento()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdModeloDocumento();
        return result;
    }
}

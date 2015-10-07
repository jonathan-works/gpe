package br.com.infox.epp.documento.entity;

import java.io.Serializable;

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

import static br.com.infox.epp.documento.query.VinculoClassificacaoTipoDocumentoQuery.*;

@Entity
@Table(name = VinculoClassificacaoTipoDocumento.TABLE_NAME,schema="tce")
@NamedQueries({ @NamedQuery(name = FIND_BY_TIPO_CLASSIFICACAO, query = FIND_BY_TIPO_CLASSIFICACAO_QUERY),
				@NamedQuery(name = FIND_BY_CLASSIFICACAO, query = FIND_BY_CLASSIFICACAO_QUERY)})
public class VinculoClassificacaoTipoDocumento implements Serializable {

    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR, sequenceName = SEQUENCE_NAME)
    @Column(name = COL_ID, unique = true, nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = COL_ID_TIPO_MODELO_DOCUMENTO, nullable = false)
    @NotNull
    private TipoModeloDocumento tipoModeloDocumento;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = COL_ID_CLASSIFICACAO_DOCUMENTO, nullable = false)
    private ClassificacaoDocumento classificacaoDocumento;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoModeloDocumento getTipoModeloDocumento() {
        return tipoModeloDocumento;
    }

    public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
        this.tipoModeloDocumento = tipoModeloDocumento;
    }

    public ClassificacaoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }

    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        this.classificacaoDocumento = classificacaoDocumento;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classificacaoDocumento == null) ? 0 : classificacaoDocumento.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((tipoModeloDocumento == null) ? 0 : tipoModeloDocumento.hashCode());
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
        if (!(obj instanceof VinculoClassificacaoTipoDocumento)) {
            return false;
        }
        VinculoClassificacaoTipoDocumento other = (VinculoClassificacaoTipoDocumento) obj;
        if (classificacaoDocumento == null) {
            if (other.classificacaoDocumento != null) {
                return false;
            }
        } else if (!classificacaoDocumento.equals(other.classificacaoDocumento)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (tipoModeloDocumento == null) {
            if (other.tipoModeloDocumento != null) {
                return false;
            }
        } else if (!tipoModeloDocumento.equals(other.tipoModeloDocumento)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return tipoModeloDocumento.toString();
    }

    public static final String TABLE_NAME = "tb_vinculo_class_tipo_doc";
    private static final String SEQUENCE_NAME = "tce.sq_vinculo_class_tipo_doc";
    private static final String COL_ID = "id_vinculo_class_tipo_doc";
    private static final String COL_ID_TIPO_MODELO_DOCUMENTO = "id_tipo_modelo_documento";
    private static final String COL_ID_CLASSIFICACAO_DOCUMENTO = "id_classificacao_documento";

    private static final long serialVersionUID = 1L;
    private static final String GENERATOR = "VinculoClassificacaoTipoDocumentoGenerator";
}

package br.com.infox.epp.tce.prestacaocontas.modelo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;

import br.com.infox.epp.documento.entity.TipoProcessoDocumento;

@Entity
@Table(name = "tb_mod_pres_contas_tp_proc_doc", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_modelo_prestacao_contas", "id_tipo_processo_documento"})
})
public class ModeloPrestacaoContasClassificacaoDocumento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ModeloPrestacaoContasClassificacaoDocumentoGenerator", sequenceName = "sq_mod_pres_contas_tp_proc_doc", allocationSize = 1)
    @GeneratedValue(generator = "ModeloPrestacaoContasClassificacaoDocumentoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_mod_pres_contas_tp_proc_doc")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_modelo_prestacao_contas", nullable = false)
    private ModeloPrestacaoContas modeloPrestacaoContas;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_processo_documento", nullable = false)
    private TipoProcessoDocumento classificacaoDocumento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModeloPrestacaoContas getModeloPrestacaoContas() {
        return modeloPrestacaoContas;
    }

    public void setModeloPrestacaoContas(ModeloPrestacaoContas modeloPrestacaoContas) {
        this.modeloPrestacaoContas = modeloPrestacaoContas;
    }

    public TipoProcessoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }

    public void setClassificacaoDocumento(TipoProcessoDocumento classificacaoDocumento) {
        this.classificacaoDocumento = classificacaoDocumento;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((getClassificacaoDocumento() == null) ? 0
                        : getClassificacaoDocumento().hashCode());
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime
                * result
                + ((getModeloPrestacaoContas() == null) ? 0 : getModeloPrestacaoContas()
                        .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ModeloPrestacaoContasClassificacaoDocumento))
            return false;
        ModeloPrestacaoContasClassificacaoDocumento other = (ModeloPrestacaoContasClassificacaoDocumento) obj;
        if (getClassificacaoDocumento() == null) {
            if (other.getClassificacaoDocumento() != null)
                return false;
        } else if (!getClassificacaoDocumento().equals(other.getClassificacaoDocumento()))
            return false;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        if (getModeloPrestacaoContas() == null) {
            if (other.getModeloPrestacaoContas() != null)
                return false;
        } else if (!getModeloPrestacaoContas().equals(other.getModeloPrestacaoContas()))
            return false;
        return true;
    }
}

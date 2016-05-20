package br.com.infox.epp.entrega.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.documento.entity.ClassificacaoDocumento;

@Entity
@Table(name = "tb_modelo_entrega_clas_doc")
public class ClassificacaoDocumentoEntrega implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final String GENERATOR_NAME="GeneratorClassificacaoDocumentoEntrega";
    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR_NAME, sequenceName = "sq_modelo_entrega_clas_doc")
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_modelo_entrega_clas_doc", unique = true, nullable = false)
    private Integer id;
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_classificacao_documento", nullable=false)
    private ClassificacaoDocumento classificacaoDocumento;
    @NotNull
    @Column(name="in_multiplos_documentos", nullable=false)
    private Boolean multiplosDocumentos;
    @NotNull
    @Column(name="in_obrigatorio", nullable=false)
    private Boolean obrigatorio;
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_modelo_entrega", nullable=false)
    private ModeloEntrega modeloEntrega;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ClassificacaoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }

    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        this.classificacaoDocumento = classificacaoDocumento;
    }

    public Boolean getMultiplosDocumentos() {
        return multiplosDocumentos;
    }

    public void setMultiplosDocumentos(Boolean multiplosDocumentos) {
        this.multiplosDocumentos = multiplosDocumentos;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }
    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }
    
    public ModeloEntrega getModeloEntrega() {
        return modeloEntrega;
    }

    public void setModeloEntrega(ModeloEntrega modeloEntrega) {
        this.modeloEntrega = modeloEntrega;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClassificacaoDocumentoEntrega other = (ClassificacaoDocumentoEntrega) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getClassificacaoDocumento().getDescricao();
    }

}

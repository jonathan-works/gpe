package br.com.infox.epp.entrega.modelo;

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

import br.com.infox.epp.processo.partes.entity.TipoParte;

@Entity
@Table(name="tb_modelo_entrega_responsavel")
public class TipoResponsavelEntrega {
    private static final String GENERATOR_NAME="GeneratorTipoResponsavelEntrega";
    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR_NAME, sequenceName = "sq_modelo_entrega_responsavel")
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_modelo_entrega_responsavel", unique = true, nullable = false)
    private Integer id;
    
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_tipo_parte", nullable=false)
    private TipoParte tipoParte;
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

    public TipoParte getTipoParte() {
        return tipoParte;
    }

    public void setTipoParte(TipoParte tipoParte) {
        this.tipoParte = tipoParte;
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
        TipoResponsavelEntrega other = (TipoResponsavelEntrega) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getTipoParte().getDescricao();
    }
    
}

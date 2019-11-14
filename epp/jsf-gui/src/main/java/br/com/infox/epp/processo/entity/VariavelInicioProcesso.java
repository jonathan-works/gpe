package br.com.infox.epp.processo.entity;

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

import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.epp.processo.form.variable.value.ValueTypes;

@Entity
@Table(name = "tb_variavel_inicio_processo")
public class VariavelInicioProcesso implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(initialValue=1, allocationSize=1, name="VariavelInicioProcessoGenerator", sequenceName="sq_variavel_inicio_processo")
    @GeneratedValue(generator = "VariavelInicioProcessoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_variavel_inicio_processo", nullable = false, unique = true)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false)
    private Processo processo;
    
    @NotNull
    @Column(name = "nm_variavel", nullable = false)
    private String name;
    
    @Column(name = "vl_variavel")
    private String value;
    
    @Column(name = "ds_tipo")
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public Object getTypedValue() {
        ValueType valueType = ValueTypes.create(type);
        return valueType.convertToModelValue(value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getProcesso() == null) ? 0 : getProcesso().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof VariavelInicioProcesso))
            return false;
        VariavelInicioProcesso other = (VariavelInicioProcesso) obj;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        if (getProcesso() == null) {
            if (other.getProcesso() != null)
                return false;
        } else if (!getProcesso().equals(other.getProcesso()))
            return false;
        return true;
    }
    
}

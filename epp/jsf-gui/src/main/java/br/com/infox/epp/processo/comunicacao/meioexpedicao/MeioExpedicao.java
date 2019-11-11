package br.com.infox.epp.processo.comunicacao.meioexpedicao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "tb_meio_expedicao")
public class MeioExpedicao implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "MeioExpedicaoGenerator", allocationSize = 1, initialValue = 1, sequenceName = "sq_meio_expedicao")
    @GeneratedValue(generator = "MeioExpedicaoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_meio_expedicao")
    private Long id;
    
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "cd_meio_expedicao", nullable = false, length = 50)
    private String codigo;
    
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "ds_meio_expedicao", nullable = false, length = 100)
    private String descricao;
    
    @NotNull
    @Column(name = "in_eletronico", nullable = false)
    private Boolean eletronico = false;
    
    @NotNull
    @Column(name = "in_ativo", nullable = false)
    private Boolean ativo = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getEletronico() {
        return eletronico;
    }

    public void setEletronico(Boolean eletronico) {
        this.eletronico = eletronico;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof MeioExpedicao))
            return false;
        MeioExpedicao other = (MeioExpedicao) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return getDescricao();
    }
    
    @Transient
    public String getLabel() {
        return toString();
    }
}

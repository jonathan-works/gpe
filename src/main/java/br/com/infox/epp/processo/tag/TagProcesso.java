package br.com.infox.epp.processo.tag;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = "tb_tag_processo")
public class TagProcesso implements Serializable {

    private static final long serialVersionUID = 4127549369863376217L;
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "TagProcessoGenerator", sequenceName = "sq_tag_processo")
    @GeneratedValue(generator = "TagProcessoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tag_processo")
    private Long id;
    
    @NotNull
    @Size(max = 255)
    @Column(name = "cd_tag_processo")
    private String codigo;
    
    @NotNull
    @JoinColumn(name = "id_processo")
    private Processo processo;

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

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
        result = prime * result + ((getProcesso() == null) ? 0 : getProcesso().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TagProcesso))
            return false;
        TagProcesso other = (TagProcesso) obj;
        if (getCodigo() == null) {
            if (other.getCodigo() != null)
                return false;
        } else if (!getCodigo().equals(other.getCodigo()))
            return false;
        if (getProcesso() == null) {
            if (other.getProcesso() != null)
                return false;
        } else if (!getProcesso().equals(other.getProcesso()))
            return false;
        return true;
    }
    
}

package br.com.infox.epp.tce.prestacaocontas.modelo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "tb_grupo_prestacao_contas")
public class GrupoPrestacaoContas implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "GrupoPrestacaoContasGenerator", allocationSize = 1, sequenceName = "sq_grupo_prestacao_contas")
    @GeneratedValue(generator = "GrupoPrestacaoContasGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_grupo_prestacao_contas")
    private Long id;
    
    @NotNull
    @Size(max = 50)
    @Column(name = "nm_grupo_prestacao_contas", nullable = false, unique = true, length = 50)
    private String nome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getNome() == null) ? 0 : getNome().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof GrupoPrestacaoContas))
            return false;
        GrupoPrestacaoContas other = (GrupoPrestacaoContas) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        if (getNome() == null) {
            if (other.getNome() != null)
                return false;
        } else if (!getNome().equals(other.getNome()))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return nome;
    }
}

package br.com.infox.ibpm.sinal;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tb_signal")
public class Signal implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 1, sequenceName = "sq_signal", name = "SignalGenerator")
    @GeneratedValue(generator= "SignalGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_signal", unique = true, nullable = false)
    private Long id;
    
    @NotNull
    @Column(name = "cd_signal", nullable = false)
    private String codigo;
    
    @NotNull
    @Column(name = "nm_signal", nullable = false)
    private String nome;
    
    @NotNull
    @Column(name = "in_ativo", nullable = false)
    private Boolean ativo;

    @NotNull
    @Column(name = "in_sistema", nullable = false)
    private Boolean sistema;
    
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public Boolean getSistema() {
		return sistema;
	}

	public void setSistema(Boolean sistema) {
		this.sistema = sistema;
	}
	
    @Override
    public String toString() {
        return getNome();
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
        if (!(obj instanceof Signal))
            return false;
        Signal other = (Signal) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

}

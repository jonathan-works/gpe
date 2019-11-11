package br.com.infox.epp.endereco;

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

import br.com.infox.epp.pessoa.entity.Pessoa;

@Entity
@Table(name = PessoaEndereco.TABLE_NAME)
public class PessoaEndereco implements Serializable {
    static final String TABLE_NAME = "tb_pessoa_endereco";
    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name="PessoaEnderecoGenerator", sequenceName="sq_pessoa_endereco", initialValue=1, allocationSize=1)
    @GeneratedValue(generator="PessoaEnderecoGenerator", strategy=GenerationType.SEQUENCE)
    @Column(name="id_pessoa_endereco", insertable = false, updatable = false, unique = true)
    private Integer id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_endereco", nullable = false)
    private Endereco endereco;

    public PessoaEndereco() {
        
    }
    
    public PessoaEndereco(Pessoa pessoa, Endereco endereco) {
        this.pessoa = pessoa;
        this.endereco = endereco;
    }
    
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
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
		if (!(obj instanceof PessoaEndereco))
			return false;
		PessoaEndereco other = (PessoaEndereco) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
    
}

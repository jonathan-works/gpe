package br.com.infox.epp.processo.comunicacao;

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

import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = "tb_pessoa_resposta_comunicacao")
public class PessoaRespostaComunicacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "PessoaRespostaComunicacaoGenerator", sequenceName = "sq_pessoa_resposta_comunicacao", initialValue = 1, allocationSize = 1)
    @GeneratedValue(generator = "PessoaRespostaComunicacaoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_pessoa_resposta_comunicacao")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_processo", nullable = false)
    private Processo comunicacao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pessoa_fisica", nullable = false)
    private PessoaFisica pessoaFisica;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Processo getComunicacao() {
        return comunicacao;
    }

    public void setComunicacao(Processo comunicacao) {
        this.comunicacao = comunicacao;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
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
        if (!(obj instanceof PessoaRespostaComunicacao))
            return false;
        PessoaRespostaComunicacao other = (PessoaRespostaComunicacao) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

}

package br.com.infox.epp.processo.partes.entity;

import static br.com.infox.epp.processo.partes.query.ParteProcessoQuery.PARTE_PROCESSO_BY_PESSOA_PROCESSO;
import static br.com.infox.epp.processo.partes.query.ParteProcessoQuery.PARTE_PROCESSO_BY_PESSOA_PROCESSO_QUERY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.processo.entity.ProcessoEpa;

@Entity
@Table(name = ParteProcesso.TABLE_NAME)
@NamedQueries(value={
		@NamedQuery(name=PARTE_PROCESSO_BY_PESSOA_PROCESSO, query=PARTE_PROCESSO_BY_PESSOA_PROCESSO_QUERY)
})
public class ParteProcesso implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_parte_processo";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_tb_parte_processo")
    @Column(name = "id_parte_processo")
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    private Integer idParteProcesso;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable=false)
    private ProcessoEpa processo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable=false)
    private Pessoa pessoa;
    
    @Column(name = "in_ativo")
    private Boolean ativo = true;

    public ParteProcesso() {
    }

    public ParteProcesso(ProcessoEpa processo, Pessoa pessoa) {
        this.processo = processo;
        this.pessoa = pessoa;
    }

    public Integer getIdParteProcesso() {
        return idParteProcesso;
    }

    public void setIdParteProcesso(Integer idParteProcesso) {
        this.idParteProcesso = idParteProcesso;
    }

    public ProcessoEpa getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoEpa processo) {
        this.processo = processo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Transient
    public String getNomeParte() {
        return getPessoa().getNome();
    }

    public void setNomeParte(String nome) {
        getPessoa().setNome(nome);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getIdParteProcesso() == null) ? 0 : getIdParteProcesso().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ParteProcesso))
			return false;
		ParteProcesso other = (ParteProcesso) obj;
		if (getIdParteProcesso() == null) {
			if (other.getIdParteProcesso() != null)
				return false;
		} else if (!getIdParteProcesso().equals(other.getIdParteProcesso()))
			return false;
		return true;
	}
    
}

/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.entity;
// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.util.constants.LengthConstants;
/**
 * Endereco generated by hbm2java
 */

@Entity
@Table(name = "tb_endereco", schema="public")
public class Endereco implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idEndereco;
	private Cep cep;
	private UsuarioLogin usuario;
	private UsuarioLogin usuarioCadastrador;
	private String nomeLogradouro;
	private String nomeBairro;

	private String nomeCidade;
	private String nomeEstado;
	private String numeroEndereco;

	private String complemento;
	private Boolean correspondencia;
	private Date dataAlteracao;
	
	private List<Localizacao> localizacaoList = new ArrayList<Localizacao>(0);

	public Endereco() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_endereco")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_endereco", unique = true, nullable = false)
	public int getIdEndereco() {
		return this.idEndereco;
	}

	public void setIdEndereco(int idEndereco) {
		this.idEndereco = idEndereco;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cep", nullable = false)
	@NotNull
	public Cep getCep() {
		return this.cep;
	}

	public void setCep(Cep cep) {
		this.cep = cep;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	public UsuarioLogin getUsuario() {
		return this.usuario;
	}

	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}

	@Column(name = "nm_logradouro", length=LengthConstants.NOME_LOGRADOURO)
	@Size(max=LengthConstants.NOME_LOGRADOURO)
	public String getNomeLogradouro() {
		return this.nomeLogradouro;
	}

	public void setNomeLogradouro(String nomeLogradouro) {
		this.nomeLogradouro = nomeLogradouro;
	}

	@Column(name = "nr_endereco", length=LengthConstants.NUMERO_ENDERECO)
	@Size(max=LengthConstants.NUMERO_ENDERECO)
	public String getNumeroEndereco() {
		return this.numeroEndereco;
	}

	public void setNumeroEndereco(String numeroEndereco) {
		this.numeroEndereco = numeroEndereco;
	}

	@Column(name = "ds_complemento", length=LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name = "nm_bairro", length=LengthConstants.NOME_BAIRRO)
	@Size(max=LengthConstants.NOME_BAIRRO)
	public String getNomeBairro() {
		return this.nomeBairro;
	}

	public void setNomeBairro(String nomeBairro) {
		this.nomeBairro = nomeBairro;
	}

	@Transient
	public String getNomeCidade() {
		if (this.nomeCidade != null) {
			return this.nomeCidade;
		}
		if (cep != null && cep.getMunicipio() != null) {
			return cep.getMunicipio().getMunicipio();
		}
		return null;
	}

	public void setNomeCidade(String nomeCidade) {
		this.nomeCidade = nomeCidade;
	}

	@Transient
	public String getNomeEstado() {
		if (this.nomeEstado != null) {
			return this.nomeEstado;
		}
		if (cep != null && cep.getMunicipio() != null && cep.getMunicipio().getEstado() != null) {
			return cep.getMunicipio().getEstado().getEstado();
		}
		return null;
	}

	public void setNomeEstado(String nomeEstado) {
		this.nomeEstado = nomeEstado;
	}

	@Column(name = "in_correspondencia")
	public Boolean getCorrespondencia() {
		return this.correspondencia;
	}

	public void setCorrespondencia(Boolean correspondencia) {
		this.correspondencia = correspondencia;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_alteracao_endereco")
	public Date getDataAlteracao() {
		return this.dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "endereco")
	public List<Localizacao> getLocalizacaoList() {
		return this.localizacaoList;
	}

	public void setLocalizacaoList(List<Localizacao> localizacaoList) {
		this.localizacaoList = localizacaoList;
	}

	@Override
	public String toString() {
		return nomeLogradouro != null ? nomeLogradouro : "";
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cadastrador")
	public UsuarioLogin getUsuarioCadastrador() {
		return this.usuarioCadastrador;
	}

	public void setUsuarioCadastrador(UsuarioLogin usuarioCadastrador) {
		this.usuarioCadastrador = usuarioCadastrador;
	}
	
	@Transient
	public Boolean getAtivo() {
        return true;
    }
    public void setAtivo(Boolean ativo) {
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Endereco)) {
			return false;
		}
		Endereco other = (Endereco) obj;
		if (getIdEndereco() != other.getIdEndereco()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdEndereco();
		return result;
	}
}
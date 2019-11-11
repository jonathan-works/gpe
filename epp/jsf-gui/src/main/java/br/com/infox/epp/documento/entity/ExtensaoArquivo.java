package br.com.infox.epp.documento.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_PEQUENA;
import static br.com.infox.constants.LengthConstants.FLAG;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.documento.query.ExtensaoArquivoQuery.LIMITE_EXTENSAO;
import static br.com.infox.epp.documento.query.ExtensaoArquivoQuery.LIMITE_EXTENSAO_QUERY;

import java.io.Serializable;

import javax.persistence.Cacheable;
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
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = ExtensaoArquivo.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name=LIMITE_EXTENSAO, query=LIMITE_EXTENSAO_QUERY,
		    hints = {@QueryHint(name="org.hibernate.cacheable", value="true"), @QueryHint(name="org.hibernate.cacheRegion", value="br.com.infox.epp.documento.entity.ExtensaoArquivo")})
})
@Cacheable
public class ExtensaoArquivo implements Serializable {
    
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_extensao_arquivo";

	@Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = "sq_tb_extensao_arquivo")
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_extensao_arquivo", unique = true, nullable = false)
    private Integer idExtensaoArquivo;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_classificacao_documento", nullable = false)
    private ClassificacaoDocumento classificacaoDocumento;
	
	@NotNull
	@Size(min = FLAG, max = DESCRICAO_PEQUENA)
	@Column(name = "nm_extensao", nullable = false, length = DESCRICAO_PEQUENA, unique = true)
    private String nomeExtensao;
	
	@NotNull
	@Size(min = FLAG, max = DESCRICAO_PEQUENA)
	@Column(name = "ds_extensao", nullable = false, length = DESCRICAO_PEQUENA, unique = true)
    private String extensao;
	
	@Min(1)
	@NotNull
	@Column(name = "nr_tamanho", nullable = false)
    private Integer tamanho;
	
	@NotNull
	@Column(name = "in_paginavel", nullable = false)
    private Boolean paginavel;
	
	@Min(1)
	@Column(name = "nr_tamanho_pagina", nullable = true)
    private Integer tamanhoPorPagina;

	public Integer getIdExtensaoArquivo() {
		return idExtensaoArquivo;
	}

	public void setIdExtensaoArquivo(Integer idExtensaoArquivo) {
		this.idExtensaoArquivo = idExtensaoArquivo;
	}

	public ClassificacaoDocumento getClassificacaoDocumento() {
		return classificacaoDocumento;
	}

	public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
		this.classificacaoDocumento = classificacaoDocumento;
	}

	public String getNomeExtensao() {
		return nomeExtensao;
	}

	public void setNomeExtensao(String nomeExtensao) {
		this.nomeExtensao = nomeExtensao;
	}

	public String getExtensao() {
		return extensao;
	}

	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}

	public Integer getTamanho() {
		return tamanho;
	}

	public void setTamanho(Integer tamanho) {
		this.tamanho = tamanho;
	}

	public Boolean getPaginavel() {
		return paginavel;
	}

	public void setPaginavel(Boolean paginavel) {
		this.paginavel = paginavel;
	}

	public Integer getTamanhoPorPagina() {
		return tamanhoPorPagina;
	}

	public void setTamanhoPorPagina(Integer tamanhoPorPagina) {
		this.tamanhoPorPagina = tamanhoPorPagina;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getIdExtensaoArquivo() == null) ? 0 : getIdExtensaoArquivo()
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ExtensaoArquivo))
			return false;
		ExtensaoArquivo other = (ExtensaoArquivo) obj;
		if (getIdExtensaoArquivo() == null) {
			if (other.getIdExtensaoArquivo() != null)
				return false;
		} else if (!getIdExtensaoArquivo().equals(other.getIdExtensaoArquivo()))
			return false;
		return true;
	}
}


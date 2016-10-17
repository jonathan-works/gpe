package br.com.infox.epp.documento.publicacao;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.processo.documento.entity.Documento;

@Entity
@Table(name="tb_publicacao_documento")
public class PublicacaoDocumento {
	
    private static final String GENERATOR_NAME = "PublicacaoDocumentoGenerator";
    private static final String SEQUENCE_NAME = "sq_publicacao_documento";
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = SEQUENCE_NAME, name = GENERATOR_NAME )
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_publicacao_documento")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="id_documento")
    private Documento documento;
    
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_local_publicacao")
    private LocalPublicacao localPublicacao;
    
	@Size(max=LengthConstants.CODIGO_DOCUMENTO)
    @Column(name="nr_publicacao")
    private String numero;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dt_publicacao")
    private Date dataPublicacao;
    
    @Column(name="nr_pagina")
    private Integer pagina;
    
    @Size(max=LengthConstants.TEXTO)
    @Column(name="ds_observacoes")
    private String observacoes;
    
    @ManyToOne
    @JoinColumn(name="id_documento_certidao")
    private Documento certidao;

    public Long getId() {
    	return id;
    }
    
	public LocalPublicacao getLocalPublicacao() {
		return localPublicacao;
	}

	public void setLocalPublicacao(LocalPublicacao localPublicacao) {
		this.localPublicacao = localPublicacao;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public Integer getPagina() {
		return pagina;
	}

	public void setPagina(Integer pagina) {
		this.pagina = pagina;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Documento getCertidao() {
		return certidao;
	}

	public void setCertidao(Documento certidaoPublicacao) {
		this.certidao = certidaoPublicacao;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}
    
}

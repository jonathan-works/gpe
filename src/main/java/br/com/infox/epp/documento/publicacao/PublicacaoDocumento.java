package br.com.infox.epp.documento.publicacao;

import java.io.Serializable;
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
public class PublicacaoDocumento implements Serializable,Cloneable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
    
	public interface PublicacaoBuilder {
		public LocalBuilder documento(Documento documento);		
	}
	
	public interface LocalBuilder {
		public BuilderFinal local(LocalPublicacao local);		
	}
	
	public interface BuilderFinal {
		public BuilderFinal numero(String numero);		
		public BuilderFinal data(Date data);
		public BuilderFinal pagina(Integer pagina);
		public BuilderFinal observacoes(String observacoes);
		public BuilderFinal certidao(Documento certidao);
		public PublicacaoDocumento build();
	}
	
	public static PublicacaoBuilder builder() {
		return new PublicacaoBuilderImpl();
	}
	
	public static class PublicacaoBuilderImpl implements PublicacaoBuilder, LocalBuilder, BuilderFinal {

		private PublicacaoDocumento publicacao = new PublicacaoDocumento();
		
		public PublicacaoBuilderImpl() {
			publicacao.setDataPublicacao(new Date());
		}
		
		@Override
		public BuilderFinal numero(String numero) {
			publicacao.setNumero(numero);
			return this;
		}

		@Override
		public BuilderFinal data(Date data) {
			publicacao.setDataPublicacao(data);
			return this;
		}

		@Override
		public BuilderFinal pagina(Integer pagina) {
			publicacao.setPagina(pagina);
			return this;
		}

		@Override
		public BuilderFinal observacoes(String observacoes) {
			publicacao.setObservacoes(observacoes);
			return this;
		}

		@Override
		public PublicacaoDocumento build() {
			return publicacao;
		}

		@Override
		public BuilderFinal local(LocalPublicacao local) {
			publicacao.setLocalPublicacao(local);
			return this;
		}

		@Override
		public LocalBuilder documento(Documento documento) {
			publicacao.setDocumento(documento);
			return this;
		}

		@Override
		public BuilderFinal certidao(Documento certidao) {
			publicacao.setCertidao(certidao);
			return this;
		}
		
	}
    
    
	public void setId(Long id) {
		this.id = id;
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
	
	public PublicacaoDocumento makeCopy() throws CloneNotSupportedException {
		PublicacaoDocumento clone = (PublicacaoDocumento) clone();
		clone.setId(null);
		clone.setDocumento(null);
		return clone;
	}
    
}

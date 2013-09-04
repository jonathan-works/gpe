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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.ibpm.type.TipoDocumentoEnum;
import br.com.infox.ibpm.type.TipoNumeracaoEnum;
import br.com.infox.ibpm.type.VisibilidadeEnum;
import br.com.infox.util.constants.LengthConstants;

/**
 * @author Desconhecido
 * @author Erik Liberal
 * @version $Name$ $revision$ $Date$
 */
@Entity
@Table(name = "tb_tipo_processo_documento", schema="public")
@Inheritance(strategy=InheritanceType.JOINED)
public class TipoProcessoDocumento implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private int idTipoProcessoDocumento;
	private String tipoProcessoDocumento;
	private String codigoDocumento;
	private String tipoProcessoDocumentoObservacao;
	private TipoDocumentoEnum inTipoDocumento;
	private TipoNumeracaoEnum tipoNumeracao;
	private VisibilidadeEnum visibilidade;
	private Boolean ativo;
	private Boolean numera = Boolean.FALSE;
	private Boolean sistema = Boolean.FALSE;

	private List<ProcessoDocumento> processoDocumentoList = new ArrayList<ProcessoDocumento>(0);

    private Boolean publico;

	public TipoProcessoDocumento() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_tipo_processo_documento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_tipo_processo_documento", unique = true, nullable = false)
	public int getIdTipoProcessoDocumento() {
		return this.idTipoProcessoDocumento;
	}

	public void setIdTipoProcessoDocumento(int idTipoProcessoDocumento) {
		this.idTipoProcessoDocumento = idTipoProcessoDocumento;
	}

	@Column(name = "ds_tipo_processo_documento", nullable = false, length=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	public String getTipoProcessoDocumento() {
		return this.tipoProcessoDocumento;
	}

	public void setTipoProcessoDocumento(String tipoProcessoDocumento) {
		this.tipoProcessoDocumento = tipoProcessoDocumento;
	}
	
	@Column(name = "cd_documento", length=LengthConstants.CODIGO_DOCUMENTO)
	@Size(max=LengthConstants.CODIGO_DOCUMENTO)
	public String getCodigoDocumento() {
		return this.codigoDocumento;
	}

	public void setCodigoDocumento(String codigoDocumento) {
		this.codigoDocumento = codigoDocumento;
	}
	
	@Column(name = "in_publico", nullable = false)
    @NotNull
    public Boolean getPublico() {
        return this.publico;
    }

    public void setPublico(Boolean publico) {
        this.publico = publico;
    }

	@Column(name = "in_ativo", nullable = false)
	@NotNull
	
	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "tipoProcessoDocumento")
	public List<ProcessoDocumento> getProcessoDocumentoList() {
		return this.processoDocumentoList;
	}

	public void setProcessoDocumentoList(
			List<ProcessoDocumento> processoDocumentoList) {
		this.processoDocumentoList = processoDocumentoList;
	}
	
	@Column(name = "in_tipo_documento")
	@Enumerated(EnumType.STRING)
	public TipoDocumentoEnum getInTipoDocumento() {
		return this.inTipoDocumento;
	}

	public void setInTipoDocumento(TipoDocumentoEnum inTipoDocumento) {
		this.inTipoDocumento = inTipoDocumento;
	}
	
	@Column(name = "tp_visibilidade")
	@Enumerated(EnumType.STRING)
	public VisibilidadeEnum getVisibilidade() {
		return this.visibilidade;
	}

	public void setVisibilidade(VisibilidadeEnum visibilidade) {
		this.visibilidade = visibilidade;
	}
	
	@Column(name = "in_numera")
	public Boolean getNumera() {
		return this.numera;
	}

	public void setNumera(Boolean numera) {
		this.numera = numera;
	}
	
	@Column(name = "ds_tipo_processo_documento_observacao", length=LengthConstants.DESCRICAO_PADRAO_DOBRO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO_DOBRO)
	public String getTipoProcessoDocumentoObservacao() {
		return this.tipoProcessoDocumentoObservacao;
	}

	public void setTipoProcessoDocumentoObservacao(String tipoProcessoDocumentoObservacao) {
		this.tipoProcessoDocumentoObservacao = tipoProcessoDocumentoObservacao;
	}

	@Transient
	public TipoNumeracaoEnum getTipoNumeracao() {
		return tipoNumeracao;
	}
	public void setTipoNumeracao(TipoNumeracaoEnum tipoNumeracao) {
		this.tipoNumeracao = tipoNumeracao;
	}

	@Column( name="in_sistema")
	public Boolean getSistema() {
		return sistema;
	}
	public void setSistema(Boolean sistema) {
		this.sistema = sistema;
	}

	@Override
	public String toString() {
		return tipoProcessoDocumento;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TipoProcessoDocumento)) {
			return false;
		}
		TipoProcessoDocumento other = (TipoProcessoDocumento) obj;
		if (getIdTipoProcessoDocumento() != other.getIdTipoProcessoDocumento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdTipoProcessoDocumento();
		return result;
	}
}
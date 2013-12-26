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
package br.com.infox.epp.processo.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.constants.LengthConstants.NUMERACAO_PROCESSO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.processo.query.ProcessoQuery.*;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.InheritanceType.JOINED;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;

@Entity
@Table(name=TABLE_PROCESSO, schema=PUBLIC)
@Inheritance(strategy=JOINED)
@NamedNativeQueries(value={
    @NamedNativeQuery(name=APAGA_ACTOR_ID_DO_PROCESSO, query=APAGA_ACTOR_ID_DO_PROCESSO_QUERY),
    @NamedNativeQuery(name=REMOVE_PROCESSO_DA_CAIXA_ATUAL, query=REMOVE_PROCESSO_DA_CAIXA_ATUAL_QUERY),
    @NamedNativeQuery(name=ANULA_ACTOR_ID, query=ANULA_ACTOR_ID_QUERY)
})
@NamedQueries(value={
    @NamedQuery(name=LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID, query=LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID_QUERY),
})
public class Processo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idProcesso;
	private UsuarioLogin usuarioCadastroProcesso;
	private String numeroProcesso;
	private String numeroProcessoOrigem;
	private String complemento;
	private Date dataInicio;
	private Date dataFim;
	private Long duracao;
	private Caixa caixa;
	
	private Long idJbpm;
	
	private List<ProcessoDocumento> processoDocumentoList = new ArrayList<ProcessoDocumento>(0);
	private List<Processo> processoConexoListForIdProcesso = new ArrayList<Processo>(0);
	private List<Processo> processoConexoListForIdProcessoConexo = new ArrayList<Processo>(0);

	private String actorId;

	public Processo() {
	}

	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_PROCESSO)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_PROCESSO, unique = true, nullable = false)
	public int getIdProcesso() {
		return this.idProcesso;
	}

	public void setIdProcesso(int idProcesso) {
		this.idProcesso = idProcesso;
	}
	
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = ID_USUARIO_CADASTRO_PROCESSO)
	public UsuarioLogin getUsuarioCadastroProcesso() {
		return this.usuarioCadastroProcesso;
	}

	public void setUsuarioCadastroProcesso(UsuarioLogin usuarioCadastroProcesso) {
		this.usuarioCadastroProcesso = usuarioCadastroProcesso;
	}

	@Column(name = NUMERO_PROCESSO, nullable = false, length=NUMERACAO_PROCESSO)
	@NotNull
	@Size(max=NUMERACAO_PROCESSO)
	public String getNumeroProcesso() {
		return this.numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	@Column(name = NUMERO_PROCESSO_ORIGEM, length=NUMERACAO_PROCESSO)
	@Size(max=NUMERACAO_PROCESSO)
	public String getNumeroProcessoOrigem() {
		return this.numeroProcessoOrigem;
	}

	public void setNumeroProcessoOrigem(String numeroProcessoOrigem) {
		this.numeroProcessoOrigem = numeroProcessoOrigem;
	}

	@Column(name = COMPLEMENTO, length=DESCRICAO_PADRAO)
	@Size(max=DESCRICAO_PADRAO)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	@Temporal(TIMESTAMP)
	@Column(name = DATA_INICIO, nullable = false)
	@NotNull
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Temporal(TIMESTAMP)
	@Column(name = DATA_FIM)
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		if (dataFim != null && dataInicio != null) {
			setDuracao(dataFim.getTime() - dataInicio.getTime());
		}
		this.dataFim = dataFim;
	}
	
	@Column(name = DURACAO)
	public Long getDuracao() {
		return duracao;
	}
	
	public void setDuracao(Long duracao) {
		this.duracao = duracao;
	}	

	@OneToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY, mappedBy = PROCESSO_ATTRIBUTE)
	public List<ProcessoDocumento> getProcessoDocumentoList() {
		return this.processoDocumentoList;
	}

	public void setProcessoDocumentoList(
			List<ProcessoDocumento> processoDocumentoList) {
		this.processoDocumentoList = processoDocumentoList;
	}
	
	@ManyToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY)
	@JoinTable(name = TABLE_PROCESSO_CONEXAO, schema = PUBLIC, 
	    joinColumns = {@JoinColumn(name = ID_PROCESSO, nullable = false, updatable = false)}, 
	    inverseJoinColumns = {@JoinColumn(name = ID_PROCESSO_CONEXO, nullable = false, updatable = false)})
	public List<Processo> getProcessoConexoListForIdProcesso() {
		return processoConexoListForIdProcesso;
	}

	public void setProcessoConexoListForIdProcesso(
			List<Processo> processoConexoListForIdProcesso) {
		this.processoConexoListForIdProcesso = processoConexoListForIdProcesso;
	}
	

	@ManyToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY)
	@JoinTable(name = TABLE_PROCESSO_CONEXAO, schema = PUBLIC, 
	    joinColumns = {@JoinColumn(name = ID_PROCESSO_CONEXO, nullable = false, updatable = false)},
	    inverseJoinColumns = {@JoinColumn(name = ID_PROCESSO, nullable = false, updatable = false)})
	public List<Processo> getProcessoConexoListForIdProcessoConexo() {
		return processoConexoListForIdProcessoConexo;
	}
	
	public void setProcessoConexoListForIdProcessoConexo(
			List<Processo> processoConexoListForIdProcessoConexo) {
		this.processoConexoListForIdProcessoConexo = processoConexoListForIdProcessoConexo;
	}
	
	@Override
	public String toString() {
		return numeroProcesso;
	}

	@Column(name = ID_JBPM)
	public Long getIdJbpm() {
		return idJbpm;
	}

	public void setIdJbpm(Long idJbpm) {
		this.idJbpm = idJbpm;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	@Column(name = NOME_ACTOR_ID)
	public String getActorId() {
		return actorId;
	}

	@ManyToOne(fetch=LAZY)
	@JoinColumn(name=ID_CAIXA)
	public Caixa getCaixa() {
		return caixa;
	}
	
	public void setCaixa(Caixa caixa) {
		this.caixa = caixa;
	}

	@Transient
	public List<ProcessoDocumento> getProcessoDocumentoList(boolean binario){
		List<ProcessoDocumento> list = new ArrayList<ProcessoDocumento>();
		for (ProcessoDocumento processoDocumento : processoDocumentoList) {
			if (processoDocumento.getProcessoDocumentoBin() != null) {
				boolean isBin = processoDocumento.getProcessoDocumentoBin()
						.getModeloDocumento() == null;
				if ((binario && isBin) || (!binario && !isBin)) {
					list.add(processoDocumento);
				}
			}
		}
		return list;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Processo)) {
			return false;
		}
		Processo other = (Processo) obj;
		if (getIdProcesso() != other.getIdProcesso()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdProcesso();
		return result;
	}
	
}
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
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.access.entity.UsuarioLogin;

@Entity
@Table(name=Processo.TABLE_NAME, schema="public")
@Inheritance(strategy=InheritanceType.JOINED)
public class Processo implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_processo";
	private static final long serialVersionUID = 1L;

	private int idProcesso;
	private UsuarioLogin usuarioCadastroProcesso;
	private String nomeUsuarioCadastroProcesso;
	private String numeroProcesso;
	private String numeroProcessoOrigem;
	private String complemento;
	private Date dataInicio;
	private Date dataFim;
	private Long duracao;
	private Caixa caixa;
	private Status status;
	
	private Long idJbpm;
	
	private List<ProcessoDocumento> processoDocumentoList = new ArrayList<ProcessoDocumento>(0);
	private List<Processo> processoConexoListForIdProcesso = new ArrayList<Processo>(0);
	private List<Processo> processoConexoListForIdProcessoConexo = new ArrayList<Processo>(0);
	private List<Estatistica> estatisticaList = new ArrayList<Estatistica>(0);

	private String actorId;

	public Processo() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_processo", allocationSize = 1)
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_processo", unique = true, nullable = false)
	public int getIdProcesso() {
		return this.idProcesso;
	}

	public void setIdProcesso(int idProcesso) {
		this.idProcesso = idProcesso;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cadastro_processo")
	public UsuarioLogin getUsuarioCadastroProcesso() {
		return this.usuarioCadastroProcesso;
	}

	public void setUsuarioCadastroProcesso(UsuarioLogin usuarioCadastroProcesso) {
		this.usuarioCadastroProcesso = usuarioCadastroProcesso;
	}

	@Column(name = "nr_processo", nullable = false, length = 30)
	@NotNull
	@Size(max = 30)
	public String getNumeroProcesso() {
		return this.numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	@Column(name = "nr_processo_origem", length = 30)
	@Size(max = 30)
	public String getNumeroProcessoOrigem() {
		return this.numeroProcessoOrigem;
	}

	public void setNumeroProcessoOrigem(String numeroProcessoOrigem) {
		this.numeroProcessoOrigem = numeroProcessoOrigem;
	}

	@Column(name = "ds_complemento", length = 100)
	@Size(max = 100)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_inicio", nullable = false)
	@NotNull
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_fim")
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		if (dataFim != null && dataInicio != null) {
			setDuracao(dataFim.getTime() - dataInicio.getTime());
		}
		this.dataFim = dataFim;
	}
	
	@Column(name = "nr_duracao")
	public Long getDuracao() {
		return duracao;
	}
	
	public void setDuracao(Long duracao) {
		this.duracao = duracao;
	}	

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "processo")
	public List<ProcessoDocumento> getProcessoDocumentoList() {
		return this.processoDocumentoList;
	}

	public void setProcessoDocumentoList(
			List<ProcessoDocumento> processoDocumentoList) {
		this.processoDocumentoList = processoDocumentoList;
	}
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY)
	@JoinTable(name = "tb_processo_conexao", schema = "public", joinColumns = {@JoinColumn(name = "id_processo", nullable = false, updatable = false)}, inverseJoinColumns = {@JoinColumn(name = "id_processo_conexo", nullable = false, updatable = false)})
	public List<Processo> getProcessoConexoListForIdProcesso() {
		return processoConexoListForIdProcesso;
	}

	public void setProcessoConexoListForIdProcesso(
			List<Processo> processoConexoListForIdProcesso) {
		this.processoConexoListForIdProcesso = processoConexoListForIdProcesso;
	}
	

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY)
	@JoinTable(name = "tb_processo_conexao", schema = "public", joinColumns = {@JoinColumn(name = "id_processo_conexo", nullable = false, updatable = false)}, inverseJoinColumns = {@JoinColumn(name = "id_processo", nullable = false, updatable = false)})
	public List<Processo> getProcessoConexoListForIdProcessoConexo() {
		return processoConexoListForIdProcessoConexo;
	}
	
	public void setProcessoConexoListForIdProcessoConexo(
			List<Processo> processoConexoListForIdProcessoConexo) {
		this.processoConexoListForIdProcessoConexo = processoConexoListForIdProcessoConexo;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "processo")
	@OrderBy("dataInicio")
	public List<Estatistica> getEstatisticaList() {
		return estatisticaList;
	}
	
	public void setEstatisticaList(List<Estatistica> estatisticaList) {
		this.estatisticaList = estatisticaList;
	}
	
	@Transient
	public Estatistica getLastEstatistica() {
		Estatistica e = null;
		try {
			if (estatisticaList.size() > 0) {
				e = estatisticaList.get(estatisticaList.size() - 1);
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return e;
	}
	
	@Override
	public String toString() {
		return numeroProcesso;
	}

	@Column(name = "id_jbpm")
	public Long getIdJbpm() {
		return idJbpm;
	}

	public void setIdJbpm(Long idJbpm) {
		this.idJbpm = idJbpm;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	@Column(name = "nm_actor_id")
	public String getActorId() {
		return actorId;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_caixa")
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_status")
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	//Adicionado
	
	@Column(name = "ds_nome_usuario_cadastro_processo", length = 100)
	@Size(max = 100)
	public String getNomeUsuarioCadastroProcesso() {
		return nomeUsuarioCadastroProcesso;
	}

	public void setNomeUsuarioCadastroProcesso(String nomeUsuarioCadastroProcesso) {
		this.nomeUsuarioCadastroProcesso = nomeUsuarioCadastroProcesso;
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
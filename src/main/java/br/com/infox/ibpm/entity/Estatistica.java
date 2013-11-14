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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.util.constants.LengthConstants;


@Entity
@Table(name = "tb_estatistica", schema="public")
public class Estatistica implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idEstatistica;
	private Processo processo;
	private String taskName;
	private String nodeName;
	private String nomeFluxo;
	private Fluxo fluxo;
	private Localizacao localizacao;
	private Date dataInicio;
	private Date dataFim;
	private Long duracao;

	public Estatistica() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_estatistica")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_estatistica", unique = true, nullable = false)
	public int getIdEstatistica() {
		return idEstatistica;
	}
	
	public void setIdEstatistica(int idEstatistica) {
		this.idEstatistica = idEstatistica;
	}

	@Column(name = "nm_task", nullable = false, length=LengthConstants.NOME_MEDIO)
	@Size(max=LengthConstants.NOME_MEDIO)
	@NotNull
	public String getTaskName() {
		return taskName;
	}
	
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Column(name = "nm_node", length=LengthConstants.NOME_MEDIO)
	@Size(max=LengthConstants.NOME_MEDIO)
	public String getNodeName() {
		return nodeName;
	}
	
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	@Column(name = "ds_fluxo", length=LengthConstants.DESCRICAO_MEDIA)
	@Size(max=LengthConstants.DESCRICAO_MEDIA)
	public String getNomeFluxo() {
		return nomeFluxo;
	}
	
	public void setNomeFluxo(String fluxo) {
		this.nomeFluxo = fluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao")
	public Localizacao getLocalizacao() {
		return localizacao;
	}
	
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_fluxo")
	public Fluxo getFluxo() {
		return fluxo;
	}
	
	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo", nullable = false)
	@NotNull
	public Processo getProcesso() {
		return processo;
	}
	
	public void setProcesso(Processo processo) {
		this.processo = processo;
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
	
	@Override
	public String toString() {
		return nomeFluxo + " / " + taskName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Estatistica)) {
			return false;
		}
		Estatistica other = (Estatistica) obj;
		if (getIdEstatistica() != other.getIdEstatistica()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdEstatistica();
		return result;
	}
}
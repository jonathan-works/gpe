package br.com.infox.ibpm.component.tree;

import java.io.Serializable;

import br.com.infox.ibpm.entity.Evento;


public class EventoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Evento evento; 
	private Integer quantidade;
	private Boolean multiplo;
	private Boolean excluir;
	private Integer idProcessoDocumento;
	private Integer idTipoProcessoDocumento;
	private Long idJbpmTask;
	
	public Evento getEvento() {
		return evento;
	}
	public void setEvento(Evento evento) {
		this.evento = evento;
	}
	public Boolean getMultiplo() {
		return multiplo;
	}
	public void setMultiplo(Boolean multiplo) {
		this.multiplo = multiplo;
	}
	public Boolean getExcluir() {
		return excluir;
	}
	public void setExcluir(Boolean excluir) {
		this.excluir = excluir;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public Integer getIdProcessoDocumento() {
		return idProcessoDocumento;
	}
	public void setIdProcessoDocumento(Integer idProcessoDocumento) {
		this.idProcessoDocumento = idProcessoDocumento;
	}
	public Integer getIdTipoProcessoDocumento() {
		return idTipoProcessoDocumento;
	}
	public void setIdTipoProcessoDocumento(Integer idTipoProcessoDocumento) {
		this.idTipoProcessoDocumento = idTipoProcessoDocumento;
	}
	public Long getIdJbpmTask() {
		return idJbpmTask;
	}
	public void setIdJbpmTask(Long idJbpmTask) {
		this.idJbpmTask = idJbpmTask;
	}


}
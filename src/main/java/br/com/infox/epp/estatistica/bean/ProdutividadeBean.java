package br.com.infox.epp.estatistica.bean;

import java.io.Serializable;

public class ProdutividadeBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long quantidadeTarefas;
	private Integer tempoPrevisto;
	private String localizacao;
	private String papel;
	private String usuario;
	private String tarefa;
	private Double mediaTempoGasto;
	private Integer minimoTempoGasto;
	private Integer maximoTempoGasto;

	public ProdutividadeBean(Integer tempoPrevisto, String localizacao, String papel, String usuario, String tarefa, 
			Double mediaTempoGasto, Integer minimoTempoGasto, Integer maximoTempoGasto, Long quantidadeTarefas) {
		this.tempoPrevisto = tempoPrevisto;
		this.localizacao = localizacao;
		this.papel = papel;
		this.usuario = usuario;
		this.tarefa = tarefa;
		this.mediaTempoGasto = mediaTempoGasto;
		this.minimoTempoGasto = minimoTempoGasto;
		this.maximoTempoGasto = maximoTempoGasto;
		this.quantidadeTarefas = quantidadeTarefas;
	}
	
	public ProdutividadeBean() {
	}

	public Long getQuantidadeTarefas() {
		return quantidadeTarefas;
	}

	public void setQuantidadeTarefas(Long quantidadeTarefas) {
		this.quantidadeTarefas = quantidadeTarefas;
	}

	public Integer getTempoPrevisto() {
		return tempoPrevisto;
	}

	public void setTempoPrevisto(Integer tempoPrevisto) {
		this.tempoPrevisto = tempoPrevisto;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getPapel() {
		return papel;
	}

	public void setPapel(String papel) {
		this.papel = papel;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getTarefa() {
		return tarefa;
	}

	public void setTarefa(String tarefa) {
		this.tarefa = tarefa;
	}

	public Double getMediaTempoGasto() {
		return mediaTempoGasto;
	}

	public void setMediaTempoGasto(Double mediaTempoGasto) {
		this.mediaTempoGasto = mediaTempoGasto;
	}

	public Integer getMinimoTempoGasto() {
		return minimoTempoGasto;
	}

	public void setMinimoTempoGasto(Integer minimoTempoGasto) {
		this.minimoTempoGasto = minimoTempoGasto;
	}

	public Integer getMaximoTempoGasto() {
		return maximoTempoGasto;
	}

	public void setMaximoTempoGasto(Integer maximoTempoGasto) {
		this.maximoTempoGasto = maximoTempoGasto;
	}
}

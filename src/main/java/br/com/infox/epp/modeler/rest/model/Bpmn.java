package br.com.infox.epp.modeler.rest.model;

import br.com.infox.epp.fluxo.entity.Fluxo;

public class Bpmn {
	private Integer id;
	private String bpmn;
	private String descricao;
	private String codigo;
	
	public Bpmn(Fluxo fluxo) {
		this.id = fluxo.getIdFluxo();
		this.bpmn = fluxo.getBpmnXml();
		this.descricao = fluxo.getFluxo();
		this.codigo = fluxo.getCodFluxo();
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getBpmn() {
		return bpmn;
	}
	
	public void setBpmn(String bpmn) {
		this.bpmn = bpmn;
	}
}

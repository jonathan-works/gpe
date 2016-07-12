package br.com.infox.epp.fluxo.exportador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;

@XmlRootElement
public class FluxoConfiguration implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String FLUXO_XML = "fluxo.xml";
	public static final String PROCESS_VARIABLES_XML = "processVariables.xml";

	private List<VariavelProcesso> variavelProcesso;
	
	public List<VariavelProcesso> getVariavelProcesso() {
		return variavelProcesso;
	}

	public void setVariavelProcesso(List<VariavelProcesso> variavelProcesso) {
		this.variavelProcesso = variavelProcesso;
	}

	public void populateVariaveisProcessoConfiguration(List<DefinicaoVariavelProcesso> definicaoVariaveisProcesso) {
		setVariavelProcesso(new ArrayList<FluxoConfiguration.VariavelProcesso>());
		for (DefinicaoVariavelProcesso definicaoVariavelProcesso : definicaoVariaveisProcesso) {
			VariavelProcesso variavel = new VariavelProcesso();
			variavel.setName(definicaoVariavelProcesso.getNome());
	        variavel.setLabel(definicaoVariavelProcesso.getLabel());
	        variavel.setDefaultValue(definicaoVariavelProcesso.getValorPadrao());
	        variavel.setVisible(definicaoVariavelProcesso.getVisivel());
	        variavel.setPanelVisible(definicaoVariavelProcesso.getVisivelPainel());
	        variavel.setOrder(definicaoVariavelProcesso.getOrdem());
			this.getVariavelProcesso().add(variavel);
		}
	}
	
	public List<DefinicaoVariavelProcesso> createDefinicaoVariavelProcessoList() {
		List<DefinicaoVariavelProcesso> listDefinicaoVariavelProcesso = new ArrayList<DefinicaoVariavelProcesso>();
		if (getVariavelProcesso() != null && !getVariavelProcesso().isEmpty()) {
			for (VariavelProcesso var : getVariavelProcesso()) {
				DefinicaoVariavelProcesso definicaoVariavel = new DefinicaoVariavelProcesso();
				definicaoVariavel.setNome(var.getName());
				definicaoVariavel.setLabel(var.getLabel());
				definicaoVariavel.setValorPadrao(var.getDefaultValue());
				definicaoVariavel.setVisivel(var.getVisible());
				definicaoVariavel.setVisivelPainel(var.getPanelVisible());
				definicaoVariavel.setOrdem(var.getOrder());
				listDefinicaoVariavelProcesso.add(definicaoVariavel);
			}
		}
		return listDefinicaoVariavelProcesso;
	}
	
	private static class VariavelProcesso implements Serializable{
		private static final long serialVersionUID = 1L;

		String name;
		String label;
		String defaultValue;
		Boolean visible;
		Boolean panelVisible;
		Integer order;
		
		public VariavelProcesso () {}
		
		public String getName() {
		        return name;
		}
		
		public void setName(String name) {
		        this.name = name;
		}
		
		public String getLabel() {
		        return label;
		}
		
		public void setLabel(String label) {
		        this.label = label;
		}
		
		public String getDefaultValue() {
		        return defaultValue;
		}
		
		public void setDefaultValue(String defaultValue) {
		        this.defaultValue = defaultValue;
		}
		
		public Boolean getVisible() {
		        return visible;
		}
		
		public void setVisible(Boolean visible) {
		        this.visible = visible;
		}
		
		public Boolean getPanelVisible() {
		        return panelVisible;
		}
		
		public void setPanelVisible(Boolean panelVisible) {
		        this.panelVisible = panelVisible;
		}
		
		public Integer getOrder() {
		        return order;
		}
		
		public void setOrder(Integer order) {
			this.order = order;
		}
	}
}

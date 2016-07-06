package br.com.infox.epp.fluxo.definicaovariavel;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Named
@ViewScoped
public class DefinicaoVariavelProcessoList extends DataList<DefinicaoVariavelProcesso> {
	private static final long serialVersionUID = 1L;

	private Fluxo fluxo;
	
	private String nome;
	private String label;
	private Boolean visivelPainel;
	
	@Override
	protected String getDefaultOrder() {
		return "nome";
	}

	@Override
	protected String getDefaultEjbql() {
		return "select o from DefinicaoVariavelProcesso o";
	}
	
	@Override
	protected String getDefaultWhere() {
		return "where o.fluxo = #{definicaoVariavelProcessoList.fluxo}";
	}
	
	@Override
	protected void addRestrictionFields() {
		addRestrictionField("nome", RestrictionType.contendoLower);
		addRestrictionField("label", RestrictionType.contendoLower);
		addRestrictionField("visivelPainel", RestrictionType.igual);
	}
	
	public Fluxo getFluxo() {
		return fluxo;
	}
	
	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Boolean getVisivelPainel() {
		return visivelPainel;
	}
	
	public void setVisivelPainel(Boolean visivelPainel) {
		this.visivelPainel = visivelPainel;
	}
}

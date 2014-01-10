package br.com.infox.epp.processo.consulta.home;


import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.processo.consulta.bean.ConsultaProcesso;

@Scope(ScopeType.CONVERSATION)
@Name(ConsultaProcessoHome.NAME)
@Install(precedence=Install.FRAMEWORK)
public class ConsultaProcessoHome implements Serializable {
	
	public static final String NAME = "consultaProcessoHome";
	private static final long serialVersionUID = 1L;
	private ConsultaProcesso instance = new ConsultaProcesso();
	
	private List<Natureza> naturezaList;
	private List<Categoria> categoriaList;
	
	@In
	private GenericManager genericManager;
	
	@Create
	public void init() {
		naturezaList = genericManager.findAll(Natureza.class);
		categoriaList = genericManager.findAll(Categoria.class);
	}
	
	public void newInstance() {
	    instance = new ConsultaProcesso();
	}
	
	public ConsultaProcesso getInstance() {
		return instance;
	}
	
	public void setInstance(ConsultaProcesso instance) {
		this.instance = instance;
	}
	
	public boolean isEditable() {
		return true;
	}	
	
	
	/**
	 * Retorna os resultados do grid
	 * 
	 * @return lista de processos
	 */
	
	public String getHomeName() {
		return NAME;
	}

	public Class<ConsultaProcesso> getEntityClass() {
		return ConsultaProcesso.class;
	}
	
	public static ConsultaProcessoHome instance() {
		return (ConsultaProcessoHome) Contexts.getConversationContext().get(NAME);
	}

	public void setNaturezaList(List<Natureza> naturezaList) {
		this.naturezaList = naturezaList;
	}

	public List<Natureza> getNaturezaList() {
		return naturezaList;
	}

	public void setCategoriaList(List<Categoria> categoriaList) {
		this.categoriaList = categoriaList;
	}

	public List<Categoria> getCategoriaList() {
		return categoriaList;
	}

}
package br.com.infox.ibpm.variable.action;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.ibpm.variable.VariableAccessHandler;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaDAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;

@Named
@ViewScoped
public class DominioVariavelTarefaCrudAction implements Serializable {

	private static final String TAB_SEARCH = "search";
	private static final String TAB_FORM = "form";
	private static final long serialVersionUID = 1L;
	
	@Inject
    private DominioVariavelTarefaDAO dominioVariavelTarefaDAO;

	private VariableAccessHandler currentVariable;
	private Integer id;
	private String codigo;
	private String nome;
	private String dominio;
	
	private String tab = TAB_SEARCH;

	public void onClickSearchTab() {
		newInstance();
		setTab(TAB_SEARCH);
	}

	public void onClickFormTab() {
		newInstance();
		setTab(TAB_FORM);
	}
	
	public boolean isManaged() {
		return getId() != null;
	}

	public void editDominio(DominioVariavelTarefa dominioVariavelTarefa) {
		setId(dominioVariavelTarefa.getId());
		setCodigo(dominioVariavelTarefa.getCodigo());
		setNome(dominioVariavelTarefa.getNome());
		setDominio(dominioVariavelTarefa.getDominio());
		setTab(TAB_FORM);
	}

	public void newInstance() {
		setId(null);
		setCodigo(null);
		setNome(null);
		setDominio(null);
	}
	
	public void setDominioConfig() {
		getCurrentVariable().getDominioHandler().setDominioVariavelTarefa(createInstance());
		newInstance();
		setTab(TAB_SEARCH);
	}
	
	private DominioVariavelTarefa createInstance() {
		DominioVariavelTarefa instance = new DominioVariavelTarefa();
		instance.setId(getId());
		instance.setCodigo(getCodigo());
		instance.setNome(getNome());
		instance.setDominio(getDominio());
		return instance;
	}

	@ExceptionHandled(MethodType.PERSIST)
	public void save() {
		dominioVariavelTarefaDAO.persist(createInstance());
	}
	
	@ExceptionHandled(MethodType.UPDATE)
	public void update() {
		dominioVariavelTarefaDAO.update(createInstance());
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public VariableAccessHandler getCurrentVariable() {
		return currentVariable;
	}

	public void setCurrentVariable(VariableAccessHandler currentVariable) {
		this.currentVariable = currentVariable;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDominio() {
		return dominio;
	}

	public void setDominio(String dominio) {
		this.dominio = dominio;
	}

}

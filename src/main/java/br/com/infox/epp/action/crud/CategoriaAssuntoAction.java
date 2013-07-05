package br.com.infox.epp.action.crud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.CategoriaAssunto;
import br.com.infox.epp.manager.CategoriaAssuntoManager;
import br.com.infox.ibpm.entity.Assunto;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(CategoriaAssuntoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class CategoriaAssuntoAction extends AbstractHome<CategoriaAssunto> {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "categoriaAssuntoAction";

	@In
	private CategoriaAssuntoManager categoriaAssuntoManager;
	
	private List<CategoriaAssunto> categoriaAssuntoList;
	private List<Assunto> assuntoList;
	private Categoria categoria;
	
	@Override
	protected boolean beforePersistOrUpdate() {
		getInstance().setCategoria(categoria);
		if (!violaConstraintsDeUnicidade()) {
			return super.beforePersistOrUpdate();
		} else {
			return false;
		}
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
		newInstance();
		listByCategoria();
		return super.afterPersistOrUpdate(ret);
	}
	
	@Override
	public String remove(CategoriaAssunto obj) {
		String remove = super.remove(obj);
		if(remove != null) {
			categoriaAssuntoList.remove(obj);
		}
		newInstance();
		return remove;
	}

	public void removeAll() {
		try {
			for (Iterator<CategoriaAssunto>  iterator = categoriaAssuntoList.iterator(); iterator.hasNext();) {
				CategoriaAssunto ca = iterator.next();
					getEntityManager().remove(ca);
				iterator.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		FacesMessages.instance().add("Registros removidos com sucesso!");
	}
		
	public void init() {
		CategoriaAction naturezaAction = (CategoriaAction) Component.getInstance(CategoriaAction.NAME);
		categoria = naturezaAction.getInstance();
		listByCategoria();
		assuntoList = categoriaAssuntoManager.findAll(Assunto.class);
		newInstance();
	}

	private void listByCategoria() {
		categoriaAssuntoList = categoriaAssuntoManager.listByCategoria(categoria);
	}

	public void setCategoriaAssuntoList(List<CategoriaAssunto> categoriaAssuntoList) {
		this.categoriaAssuntoList = categoriaAssuntoList;
	}

	public List<CategoriaAssunto> getCategoriaAssuntoList() {
		return categoriaAssuntoList;
	}

	public void setAssuntoList(List<Assunto> assuntoList) {
		this.assuntoList = assuntoList;
	}

	public List<Assunto> getAssuntoList() {
		return assuntoList;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public List<Assunto> getFolhas(int idPai)	{
		Assunto pai = getEntityManager().find(Assunto.class, idPai);
		List<Assunto> list = pai.getAssuntoList();
		
		List<Assunto> result = new ArrayList<Assunto>();
		if (list.size() == 0)	{
			result.add(pai);
			return result;
		}
		
		for (Assunto filho : list) {
			result.addAll(getFolhas(filho.getIdAssunto()));
		}
		
		return result;
	}
	
	@Override
	public String persist() {
		List<Assunto> folhas = getFolhas(getInstance().getAssunto().getIdAssunto());
		
		for (Assunto assunto : folhas) {
			if (assunto.getAtivo())	{
				getInstance().setAssunto(assunto);
				super.persist();
			}
		}
		return "persisted";
	}
	
	private boolean violaConstraintsDeUnicidade(){
		return (violaConstraintsDeUnicidade());
	}
	
	private boolean violaUnicidadeDeCategoriaAssunto(){
		if (categoriaAssuntoManager.violaUnicidadeDeCategoriaAssunto(instance)){
			FacesMessages.instance().add(Severity.ERROR,"Combina��o de Categoria e Assunto j� existe!");
			return true;
		}
		return false;
	}
	
}
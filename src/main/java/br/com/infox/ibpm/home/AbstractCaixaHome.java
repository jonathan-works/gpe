package br.com.infox.ibpm.home;

import org.jboss.seam.annotations.In;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.ibpm.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.entity.Caixa;
import br.com.infox.ibpm.manager.CaixaManager;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

public abstract class AbstractCaixaHome<T> extends AbstractHome<Caixa> {

	private static final long serialVersionUID = 1L;
	
	@In protected CaixaManager caixaManager;

	public void setCaixaIdCaixa(Integer id) {
		setId(id);
	}

	public Integer getCaixaIdCaixa() {
		return (Integer) getId();
	}
	
	@Override
	protected Caixa createInstance() {
		return new Caixa();
	}
	
	@Override
	public String remove(Caixa obj) {
		setInstance(obj);
		String ret = remove();
		newInstance();
		return ret;
	}
	
	public void removeCaixa(int idCaixa) {
		if(idCaixa == 0) {
			return;
		}
		instance = EntityUtil.find(Caixa.class, idCaixa);
		if(instance != null){
			remove();
		}else{
			FacesMessages.instance().add(Severity.ERROR, "Por favor, selecione a caixa que deseja excluir!");
		}
		TarefasTreeHandler tree = ComponentUtil.getComponent("tarefasTree");
		tree.clearTree();	
	}
	
	@Override
	public String remove() {
		StringBuilder sb = new StringBuilder();
		sb.append("update Processo set caixa = :caixa ");
		sb.append("where caixa.idCaixa = :idCaixa");
		javax.persistence.Query q = getEntityManager().createQuery(sb.toString());
		q.setParameter("caixa", null);
		q.setParameter("idCaixa", instance.getIdCaixa());
		q.executeUpdate();
		return super.remove();
	}
	
}
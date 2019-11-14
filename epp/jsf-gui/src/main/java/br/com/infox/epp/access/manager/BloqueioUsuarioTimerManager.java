package br.com.infox.epp.access.manager;

import java.util.Date;
import java.util.HashMap;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.contexts.Contexts;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.timer.BloqueioUsuarioProcessor;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.seam.util.ComponentUtil;

@Name(BloqueioUsuarioTimerManager.NAME)
@AutoCreate
public class BloqueioUsuarioTimerManager extends Manager<GenericDAO, Object> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "bloqueioUsuarioTimerManager";
	
	public void createTimerInstance(String cronExpression,
    		String idDesbloquearUsuarioTimerParameter, String description,
    		BloqueioUsuarioProcessor processor) throws SchedulerException, DAOException {
    	QuartzTriggerHandle handle = processor.processBloqueioUsuario(cronExpression);
    	Trigger trigger = handle.getTrigger();
    	saveSystemParameter(idDesbloquearUsuarioTimerParameter, trigger.getKey().getName(), description);
    }

	private void saveSystemParameter(String nome, String valor,
			String descricao) throws DAOException {
		Parametro p = new Parametro();
		p.setNomeVariavel(nome);
		p.setValorVariavel(valor);
		p.setDescricaoVariavel(descricao);
		p.setDataAtualizacao(new Date());
		p.setSistema(true);
		p.setAtivo(true);
		persist(p);
	}
	
	public String getParametro(String nome) {
		String valor = ComponentUtil.getComponent(nome, ScopeType.APPLICATION);
		if (valor == null) {
			final HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("nome", nome);
			final String hql = "select p from Parametro p where nomeVariavel = :nome";
			
			final GenericDAO dao = ComponentUtil.getComponent(GenericDAO.NAME);
			final Parametro result = (Parametro) dao.getSingleResult(hql, params);
			if (result != null) {
				valor = result.getValorVariavel();
				Contexts.getApplicationContext().set(nome, valor);
			}
		}
		return valor;
	}

}

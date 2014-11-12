package br.com.infox.epp.processo.comunicacao.manager;

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
import br.com.infox.epp.processo.comunicacao.timer.ContabilizarPrazoProcessor;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.seam.util.ComponentUtil;

@Name(ContabilizarPrazoTimerManager.NAME)
@AutoCreate
public class ContabilizarPrazoTimerManager extends Manager<GenericDAO, Object> {
    public static final String NAME = "contabilizarPrazoTimerManager";
    private static final long serialVersionUID = 1L;
    
    public void createTimerInstance(String cronExrepssion, String idContabilizarPrazoParameter, String description, ContabilizarPrazoProcessor processor) throws SchedulerException, DAOException {
        QuartzTriggerHandle handle = processor.processContabilizarPrazo(cronExrepssion);
        Trigger trigger = handle.getTrigger();
        saveSystemParameter(idContabilizarPrazoParameter, trigger.getKey().getName(), description);
    }
    
    private void saveSystemParameter(String nome, String valor, String descricao) throws DAOException {
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

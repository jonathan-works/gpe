package br.com.infox.epp.estatistica.manager;

import java.util.Date;
import java.util.HashMap;

import javax.ejb.Stateless;

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
import br.com.infox.epp.estatistica.abstracts.BamTimerProcessor;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.seam.util.ComponentUtil;

@Stateless
@Name(BamTimerManager.NAME)
@AutoCreate
public class BamTimerManager extends Manager<GenericDAO, Object> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "bamTimerManager";

    public void createTimerInstance(String cronExpression,
            String idIniciarProcessoTimerParameter, String description,
            BamTimerProcessor processor) throws SchedulerException, DAOException {
        QuartzTriggerHandle handle = processor.increaseTimeSpent(cronExpression);
        Trigger trigger = handle.getTrigger();
        saveSystemParameter(idIniciarProcessoTimerParameter, trigger.getKey().getName(), description);
    }

    private void saveSystemParameter(String nomeVariavel, String valorVariavel,
            String descricaoVariavel) throws DAOException {
        Parametro p = new Parametro();
        p.setNomeVariavel(nomeVariavel);
        p.setValorVariavel(valorVariavel);
        p.setDescricaoVariavel(descricaoVariavel);
        p.setDataAtualizacao(new Date());
        p.setSistema(true);
        p.setAtivo(true);
        persist(p);
    }

    public String getParametro(String nome) {
        String valor = ComponentUtil.getComponent(nome, ScopeType.APPLICATION);
        if (valor == null) {
            final HashMap<String, Object> params = new HashMap<>();
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

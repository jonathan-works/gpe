package br.com.infox.epp.estatistica.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.quartz.SchedulerException;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.abstracts.BamTimerProcessor;
import br.com.infox.epp.system.entity.Parametro;
import br.com.itx.util.ComponentUtil;

@Name(BamTimerManager.NAME)
@AutoCreate
public class BamTimerManager extends GenericManager {
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	private static final long serialVersionUID = 1L;
    public static final String NAME = "bamTimerManager";
    
    public void createTimerInstance(String cronExpression, String idIniciarProcessoTimerParameter, String description, BamTimerProcessor processor) throws SchedulerException, DAOException {
        processor.increaseTimeSpent(cronExpression);
        saveSystemParameter(idIniciarProcessoTimerParameter, new SimpleDateFormat(DATE_FORMAT).format(new Date()), description);
    }

    private void saveSystemParameter(String nomeVariavel,String valorVariavel, String descricaoVariavel) throws DAOException {
        Parametro p = getParametro(nomeVariavel);
        if (p != null) {
        	updateUltimoDisparo(valorVariavel, nomeVariavel);
        } else {
        	p = new Parametro();
            p.setNomeVariavel(nomeVariavel);
            p.setValorVariavel(valorVariavel);
            p.setDescricaoVariavel(descricaoVariavel);
            p.setDataAtualizacao(new Date());
            p.setSistema(true);
            p.setAtivo(true);
            persist(p);
        }
    }

    public void updateUltimoDisparo(Date ultimoDisparo, String nomeParametro) throws DAOException {
    	updateUltimoDisparo(new SimpleDateFormat(DATE_FORMAT).format(ultimoDisparo), nomeParametro);
    }
    
    public void updateUltimoDisparo(String ultimoDisparo, String nomeParametro) throws DAOException {
    	Parametro p = getParametro(nomeParametro);
    	updateUltimoDisparo(ultimoDisparo, p);
    }
    
	public void updateUltimoDisparo(String ultimoDisparo, Parametro p) throws DAOException {
		p.setValorVariavel(ultimoDisparo);
		p.setDataAtualizacao(new Date());
		update(p);
	}
    
    public Parametro getParametro(String nome) {
        final HashMap<String,Object> params = new HashMap<>();
        params.put("nome", nome);
        final String hql = "select p from Parametro p where nomeVariavel = :nome";
        
        final GenericDAO dao = ComponentUtil.getComponent(GenericDAO.NAME);
        return dao.getSingleResult(hql, params);
    }
}

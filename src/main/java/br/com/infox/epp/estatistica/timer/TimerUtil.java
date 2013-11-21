package br.com.infox.epp.estatistica.timer;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.epp.system.entity.Parametro;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

public final class TimerUtil {
    
    private TimerUtil(){
        super();
    }
	
	/**
	 * Foi necessário duplicar esse método já existente em ParametroHome pois
	 * o método dessa classe que o invoca não possui provavelmente o mesmo 
	 * classLoader do seam, o que gerava um ClassNotFoundException
	 * @param nome - Nome do parametro
	 * @return Valor do parametro
	 */
	@SuppressWarnings(WarningConstants.UNCHECKED)	
	public static String getParametro(String nome) {
		String valor = ComponentUtil.getComponent(nome, ScopeType.APPLICATION);
		if (valor == null) {
			EntityManager em = EntityUtil.getEntityManager();
			List<Parametro> resultList = em.createQuery(
				"select p from Parametro p where " +
					"nomeVariavel = :nome").setParameter("nome", nome).getResultList();
			if (!resultList.isEmpty()) {
				valor = resultList.get(0).getValorVariavel();
				Contexts.getApplicationContext().set(nome, valor);
			}
		}
		return valor;
	}
	


}

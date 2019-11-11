package br.com.infox.epp.estatistica.timer;

import java.util.HashMap;

import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.seam.util.ComponentUtil;

public final class TimerUtil {

    private TimerUtil() {
        super();
    }

    /**
     * Foi necessário duplicar esse método já existente em ParametroHome pois o
     * método dessa classe que o invoca não possui provavelmente o mesmo
     * classLoader do seam, o que gerava um ClassNotFoundException
     * 
     * @param nome - Nome do parametro
     * @return Valor do parametro
     */
    public static String getParametro(final String nome) {
        String valor = ComponentUtil.getComponent(nome, ScopeType.APPLICATION);
        if (valor == null) {
            final GenericDAO dao = ComponentUtil.getComponent(GenericDAO.NAME);
            final HashMap<String, Object> params = new HashMap<>();
            params.put("nome", nome);
            final String hql = "select p from Parametro p where nomeVariavel = :nome";
            final Parametro result = (Parametro) dao.getSingleResult(hql, params);
            if (result != null) {
                valor = result.getValorVariavel();
                Contexts.getApplicationContext().set(nome, valor);
            }
        }
        return valor;
    }

}

package br.com.infox.core.suggest;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.time.StopWatch;

import br.com.infox.componentes.suggest.SuggestItem;
import br.com.infox.componentes.suggest.SuggestProvider;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public abstract class AbstractSuggestBean<T> implements SuggestProvider<T>, Serializable {

    private static final int LIMIT_SUGGEST_DEFAULT = 15;

    private static final LogProvider LOG = Logging.getLogProvider(AbstractSuggestBean.class);

    private static final long serialVersionUID = 1L;

    protected static final String INPUT_PARAMETER = "input";

    protected transient EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);

    @SuppressWarnings(UNCHECKED)
    @Override
    public List<SuggestItem> getSuggestions(String typed) {
        StopWatch sw = new StopWatch();
        sw.start();
        List<SuggestItem> result = null;
        if (getEjbql(typed) != null) {
            Query query = entityManager.createQuery(getEjbql(typed)).setParameter(INPUT_PARAMETER, typed);
            if (getLimitSuggest() != null) {
                query.setMaxResults(getLimitSuggest());
            }
            result = query.getResultList();
        } else {
            result = new ArrayList<SuggestItem>();
        }
        LOG.info("suggestList(" + typed + ") :" + sw.getTime());
        return result;
    }

    /**
     * Metodo que devolve o limite para o resultado do suggest. Caso queira
     * retirar esse limite basta sobrescrever este metodo retornando null.
     * 
     * @return
     */
    public Integer getLimitSuggest() {
        return LIMIT_SUGGEST_DEFAULT;
    }

    public abstract String getEjbql(String typed);
}

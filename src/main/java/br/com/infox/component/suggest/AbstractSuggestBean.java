/*
 * IBPM - Ferramenta de produtividade Java Copyright (c) 1986-2009 Infox
 * Tecnologia da Informa��o Ltda.
 * 
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo sob
 * os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela Free
 * Software Foundation; vers�o 2 da Licen�a. Este programa � distribu�do na
 * expectativa de que seja �til, por�m, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia impl�cita de COMERCIABILIDADE OU ADEQUA��O A UMA FINALIDADE
 * ESPEC�FICA.
 * 
 * Consulte a GNU GPL para mais detalhes. Voc� deve ter recebido uma c�pia da
 * GNU GPL junto com este programa; se n�o, veja em http://www.gnu.org/licenses/
 */
package br.com.infox.component.suggest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.componentes.suggest.SuggestItem;
import br.com.infox.componentes.suggest.SuggestProvider;

@Scope(ScopeType.STATELESS)
public abstract class AbstractSuggestBean<T> implements SuggestProvider<T>, Serializable {

    private static final int LIMIT_SUGGEST_DEFAULT = 15;

    private static final LogProvider LOG = Logging.getLogProvider(AbstractSuggestBean.class);

    private static final long serialVersionUID = 1L;

    protected static final String INPUT_PARAMETER = "input";
    
    @In
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<SuggestItem> getSuggestions(String typed) {
        StopWatch sw = new StopWatch();
        sw.start();
        List<SuggestItem> result = null;
        if (getEjbql() != null) {
            Query query = entityManager.createQuery(getEjbql()).setParameter(INPUT_PARAMETER, typed);
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

    public abstract String getEjbql();
}

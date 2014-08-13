package br.com.infox.epp.tce.prestacaocontas.modelo.suggest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.componentes.suggest.SuggestItem;
import br.com.infox.core.suggest.AbstractSuggestBean;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.tce.prestacaocontas.modelo.action.ModeloPrestacaoContasAction;

@Name(TipoProcessoDocumentoSuggestBean.NAME)
public class TipoProcessoDocumentoSuggestBean extends AbstractSuggestBean<TipoProcessoDocumento> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoProcessoDocumentoSuggestBean";
    
    @In
    private ModeloPrestacaoContasAction modeloPrestacaoContasAction;
    
    @Override
    public List<SuggestItem> getSuggestions(String typed) {
        List<SuggestItem> suggestions = super.getSuggestions(typed);
        Map<Integer, SuggestItem> items = new HashMap<>();
        for (SuggestItem item : suggestions) {
            items.put((Integer) item.getValue(), item);
        }
        for (TipoProcessoDocumento tipoProcessoDocumento : modeloPrestacaoContasAction.getInstance().getClassificacoesDocumento()) {
            items.remove(tipoProcessoDocumento.getIdTipoProcessoDocumento());
        }
        return new ArrayList<>(items.values());
    }
    
    @Override
    public String getEjbql() {
        return " select new br.com.infox.componentes.suggest.SuggestItem(o.idTipoProcessoDocumento, o.tipoProcessoDocumento) "
                + " from TipoProcessoDocumento o "
                + " where lower(o.tipoProcessoDocumento) like concat('%', lower(:" + INPUT_PARAMETER + "), '%') "
                + " and o.ativo = true "
                + " order by o.tipoProcessoDocumento";
    }

    @Override
    public TipoProcessoDocumento load(Object id) {
        return entityManager.find(TipoProcessoDocumento.class, id);
    }
}

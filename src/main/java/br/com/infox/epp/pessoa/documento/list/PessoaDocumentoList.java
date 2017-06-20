package br.com.infox.epp.pessoa.documento.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;

@Named
@ViewScoped
public class PessoaDocumentoList extends EntityList<PessoaDocumento> {
	
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from PessoaDocumento o";
    private static final String DEFAULT_ORDER = "o.idPessoaDocumento";
    
    @Override
    protected void addSearchFields() {
        addSearchField("pessoa", SearchCriteria.IGUAL);
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }
    
}
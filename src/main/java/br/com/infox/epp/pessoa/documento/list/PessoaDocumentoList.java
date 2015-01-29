package br.com.infox.epp.pessoa.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;

@Name(PessoaDocumentoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class PessoaDocumentoList extends EntityList<PessoaDocumento> {
	
    private static final long serialVersionUID = 1L;
    public static final String NAME = "pessoaDocumentoList";

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
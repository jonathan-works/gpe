package br.com.infox.epp.processo.documento.search;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.processo.documento.dao.ProcessoDocumentoDAO;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;

@Scope(ScopeType.CONVERSATION)
@Name(ProcessoDocumentoSearch.NAME)
public class ProcessoDocumentoSearch {
    
    @In
    private ProcessoDocumentoDAO processoDocumentoDAO;
    
    private static final Integer PAGE_SIZE = 15;
    
    private String palavraPesquisada;
    private List<ProcessoDocumento> resultadoPesquisa = new ArrayList<>();

    public static final String NAME = "processoDocumentoSearch";
    
    public Integer getPageSize() {
        return PAGE_SIZE;
    }

    public String getPalavraPesquisada() {
        return palavraPesquisada;
    }

    public void setPalavraPesquisada(String palavraPesquisada) {
        this.palavraPesquisada = palavraPesquisada;
        pesquisar();
    }

    public List<ProcessoDocumento> getResultadoPesquisa() {
        return resultadoPesquisa;
    }

    public void setResultadoPesquisa(List<ProcessoDocumento> resultadoPesquisa) {
        this.resultadoPesquisa = resultadoPesquisa;
    }

    public void pesquisar() {
        setResultadoPesquisa(processoDocumentoDAO.pesquisar(getPalavraPesquisada()));
    }
    
}

package br.com.infox.epp.search;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery.TooManyClauses;
import org.apache.lucene.util.Version;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.constants.FloatFormatConstants;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.handler.ProcessoHandler;
import br.com.infox.epp.processo.search.ProcessoSearcher;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.Variavel;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name("search")
@Scope(ScopeType.CONVERSATION)
@Transactional
public class SearchHandler implements Serializable {

    private static final long serialVersionUID = 1L;
    private String searchText;
    private List<Map<String, Object>> searchResult;
    private static final LogProvider LOG = Logging.getLogProvider(SearchHandler.class);

    @In
    private DocumentoManager documentoManager;
    @In
    private ProcessoSearcher processoSearcher;
    @In
    private ProcessoHandler processoHandler;
    
    private String tab;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public List<Map<String, Object>> getSearchResult() {
        return searchResult;
    }
    
    /**
     * Método que realiza busca no sistema de acordo com o texto contido
     * 
     * Analisa se existe texto a ser buscado e confere se o texto a ser buscado
     * é Numero de Processo, Id de Processo ({@link #searchProcesso()}), ou se é
     * texto normal ({@link #searchIndexer()})
     */
    public String search() {
    	String value = "/Pesquisa/indexedSearch.seam";
        if (searchText == null || "".equals(searchText.trim())) {
            return null;
        }
        processoHandler.clear();
        if ( processoSearcher.searchProcesso(searchText) ) {
        	value = "/Processo/Consulta/list.seam";
        }
        return value;
    }

    public String getTextoDestacado(Variavel v) {
        Object value = v.getValue();
        if (value == null) {
            return null;
        }

        String texto = null;
        String type = v.getType();
        if (JbpmUtil.isTypeEditor(type)) {
            texto = documentoManager.valorDocumento((Integer) value);
        } else if (VariableType.BOOLEAN.name().equals(type)) {
            texto = Boolean.valueOf(value.toString()) ? "Sim" : "Não";
        } else if (VariableType.MONETARY.name().equalsIgnoreCase(type)) {
            texto = "R$ " + String.format(FloatFormatConstants.F2, value);
        } else if (VariableType.DATE.toString().equals(type)) {
            texto = DateFormat.getDateInstance().format((Date)value);
        } else if (VariableType.FILE.toString().equals(type)) {
            texto = documentoManager.find(value).getDescricao();
        } else {
            texto = value.toString();
        }

        if (searchText != null) {
            QueryParser parser = new QueryParser(Version.LUCENE_36, "conteudo", new BrazilianAnalyzer(Version.LUCENE_36));
            try {
                org.apache.lucene.search.Query query = parser.parse(searchText);
                String highlighted = SearchService.highlightText(query, texto, false);
                if (!"".equals(highlighted)) {
                    texto = highlighted;
                }
            } catch (TooManyClauses e) {
                LOG.warn("", e);
                FacesMessages.instance().clear();
                FacesMessages.instance().add("Não foi possível realizar a pesquisa, muitos termos de busca");
                return "";
            } catch (ParseException e) {
                LOG.error("", e);
                FacesMessages.instance().clear();
                FacesMessages.instance().add("Erro ao realizar a pesquisa, favor tentar novamente");
                return "";
            }
        }
        return texto;
    }
    
    public String getTab() {
        return tab;
    }
    
    public void setTab(String tab) {
        this.tab = tab;
    }
    
}

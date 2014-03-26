package br.com.infox.epp.search;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.constants.FloatFormatConstants;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.search.ProcessoSearcher;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.VariableHandler;
import br.com.infox.ibpm.variable.Variavel;

@Name("search")
@Scope(ScopeType.CONVERSATION)
public class SearchHandler implements Serializable {

    private static final long serialVersionUID = 1L;
    private String searchText;
    private List<Map<String, Object>> searchResult;
    private Integer resultSize;
    private int pageSize = 8;
    private int page;
    private int maxPageSize = 100;
    private static final LogProvider LOG = Logging.getLogProvider(SearchHandler.class);

    @In
    private ProcessoDocumentoManager processoDocumentoManager;
    @In
    private ProcessoSearcher processoSearcher;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        page = 0;
        this.searchText = searchText;
    }

    public List<Map<String, Object>> getSearchResult() {
        return searchResult;
    }

    /**
     * Método que realiza a busca indexada pelo conteúdo do site
     * 
     * @throws IOException Ao construir o Indexer
     * @throws ParseException Ao retornar a busca no método getQuery do Indexer
     */
    private void searchIndexer() throws IOException, ParseException {
        searchResult = new ArrayList<Map<String, Object>>();
        Indexer indexer = new Indexer();
        String[] fields = new String[] { "conteudo", "texto" };
        Query query = indexer.getQuery(searchText, fields);
        List<Document> search = indexer.search(searchText, fields, 200);
        Session session = ManagedJbpmContext.instance().getSession();

        for (Document d : search) {
            long taskId = Long.parseLong(d.get("id"));
            TaskInstance ti = (TaskInstance) session.get(TaskInstance.class, taskId);

            if (ti == null) {
                LOG.warn("Task não encontrada: " + taskId);
            } else {
                String s = SearchService.getBestFragments(query, getConteudo(ti));
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("texto", s);
                m.put("taskName", ti.getTask().getName());
                m.put("taskId", ti.getId());
                m.put("processo", ti.getProcessInstance().getContextInstance().getVariable("processo"));
                if (s == null || "".equals(s)) {
                    m.put("nomePdf", d.get("nomePdf"));
                }
                searchResult.add(m);
            }
        }
        resultSize = searchResult.size();
    }

    /**
     * Método que realiza busca no sistema de acordo com o texto contido
     * 
     * Analisa se existe texto a ser buscado e confere se o texto a ser buscado
     * é Numero de Processo, Id de Processo ({@link #searchProcesso()}), ou se é
     * texto normal ({@link #searchIndexer()})
     */
    public void search() {
        if (searchText == null || "".equals(searchText.trim())) {
            return;
        }

        boolean isProcesso = processoSearcher.searchProcesso(searchText);

        if (!isProcesso) {
            try {
                searchIndexer();
            } catch (IOException | ParseException e) {
                LOG.debug(e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings(UNCHECKED)
    public static String getConteudo(TaskInstance ti) {
        StringBuilder sb = new StringBuilder();
        TaskController taskController = ti.getTask().getTaskController();
        if (taskController != null) {
            List<VariableAccess> vaList = taskController.getVariableAccesses();
            for (VariableAccess v : vaList) {
                Object conteudo = ti.getVariable(v.getMappedName());
                if (v.isWritable() && conteudo != null) {
                    conteudo = JbpmUtil.instance().getConteudo(v, ti);
                    sb.append(VariableHandler.getLabel(v.getVariableName())).append(": ").append(conteudo).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public int getResultSize() {
        if (resultSize == null) {
            search();
        }
        return resultSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void nextPage() {
        page++;
        search();
    }

    public void previousPage() {
        page--;
        search();
    }

    public void firstPage() {
        page = 0;
        search();
    }

    public void lastPage() {
        page = (resultSize / pageSize);
        if (resultSize % pageSize == 0) {
            page--;
        }
        search();
    }

    public boolean isNextPageAvailable() {
        return resultSize > ((page * pageSize) + pageSize);
    }

    public boolean isPreviousPageAvailable() {
        return page > 0;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;
    }

    public long getFirstRow() {
        return page * pageSize + 1;
    }

    public long getLastRow() {
        return (page * pageSize + pageSize) > resultSize ? resultSize : page
                * pageSize + pageSize;
    }

    public String getTextoDestacado(Variavel v) {
        Object value = v.getValue();
        if (value == null) {
            return null;
        }

        String texto = null;
        String type = v.getType();
        if (JbpmUtil.isTypeEditor(type)) {
            texto = processoDocumentoManager.valorProcessoDocumento((Integer) value);
        } else if ("sim_nao".equals(type)) {
            texto = Boolean.valueOf(value.toString()) ? "Sim" : "Não";
        } else if ("numberMoney".equalsIgnoreCase(type)) {
            texto = String.format(FloatFormatConstants.F2, value);
        } else {
            texto = value.toString();
        }

        if (searchText != null) {
            String[] fields = new String[] { "conteudo" };
            QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, SearchService.getAnalyzer());
            try {
                org.apache.lucene.search.Query query = parser.parse(searchText);
                String highlighted = SearchService.highlightText(query, texto, false);
                if (!"".equals(highlighted)) {
                    texto = highlighted;
                }
            } catch (ParseException e) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return texto;
    }

}

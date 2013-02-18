/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.search;

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
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.help.HelpUtil;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.handler.VariableHandler;
import br.com.infox.ibpm.jbpm.handler.VariableHandler.Variavel;
import br.com.infox.search.Indexer;
import br.com.itx.util.EntityUtil;


@Name("search")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class SearchHandler implements Serializable {

	private static final String NUMERO_PROCESSO_PATTERN = "^\\d{7}-\\d{2}\\.\\d{4}\\.\\d\\.\\d{2}\\.\\d{4}";
	private static final long serialVersionUID = 1L;
	private String searchText;
	private List<Map<String,Object>> searchResult;
	private Integer resultSize;
	private int pageSize = 8;
	private int page;
	private int maxPageSize = 100;
	private static final LogProvider LOG = Logging.getLogProvider(SearchHandler.class);
	
	
	public String getSearchText() {
		return searchText;
	}
	
	public void setSearchText(String searchText) {
		page = 0;
		this.searchText = searchText;
	}
	
	private FullTextEntityManager getEntityManager() {
		return (FullTextEntityManager) EntityUtil.getEntityManager();
	}	
	
	public List<Map<String, Object>> getSearchResult() {
		return searchResult;
	}
	 
	/**
	 * Busca o processo pelo seu id
	 * 
	 * @return	Processo cuja id seja igual o valor buscado, ou null
	 */
	private Processo searchIdProcesso()	{
		int prc = -1;
		try {
			prc = Integer.parseInt(searchText);
		}	catch (NumberFormatException e) {
			LOG.debug(e.getMessage());
		}
		return getEntityManager().find(Processo.class, prc);
	}
	
	/**
	 * Busca o processo pelo seu Numero de Processo
	 * 
	 * @return	Processo cujo valor do numeroProcesso seja igual ao texto de busca, ou null
	 */
	private Processo searchNrProcesso()	{
		javax.persistence.Query query = EntityUtil.getEntityManager().createQuery(
				"select p from Processo p where p.numeroProcesso = :processo")
				.setParameter("processo", searchText);
		return EntityUtil.getSingleResult(query); 
	}
	
	/**
	 * Método realiza busca de processos no sistema
	 * 
	 * 		Caso o texto de busca seja número de processo realiza uma busca
	 * 	por este valor {@link #searchNrProcesso()}, caso não seja,
	 * 	tenta uma busca de processo pelo ID {@link #searchIdProcesso()}
	 * 
	 * 		Se qualquer dos métodos de busca retornar um processo, este
	 * 	é chamado na página {@link #visualizarProcesso(Processo)}
	 * 
	 * @return	TRUE se o resultado for um processo, FALSE do contrário
	 */
	public boolean searchProcesso()	{
		Processo processo = null;
		boolean hasProcesso = false;
		if (searchText.matches(NUMERO_PROCESSO_PATTERN)) {
			processo = searchNrProcesso();
		} else {
			processo = searchIdProcesso();
		}
		
		hasProcesso = processo != null;
		if (hasProcesso) {
			visualizarProcesso(processo);
		}
		return hasProcesso;
	}
	
	/**
	 * 	Método redireciona para visualização do processo escolhido no paginador
	 * 
	 * @param processo	Processo a ser visualizado no paginador
	 */
	public void visualizarProcesso(Processo processo)	{
		Redirect.instance().setConversationPropagationEnabled(false);
		Redirect.instance().setViewId("/Processo/Consulta/list.xhtml");
		Redirect.instance().setParameter("id", processo.getIdProcesso());
		Redirect.instance().setParameter("idJbpm", processo.getIdJbpm());
		Redirect.instance().execute();
	}
	
	/**
	 * Método que realiza a busca indexada pelo conteúdo do site
	 * 
	 * @throws IOException		Ao construir o Indexer
	 * @throws ParseException	Ao retornar a busca no método getQuery do Indexer
	 */
	private void searchIndexer() throws IOException, ParseException	{
		searchResult = new ArrayList<Map<String,Object>>();
		Indexer indexer = new Indexer();
		String[] fields = new String[]{"conteudo", "texto"};
		Query query = indexer.getQuery(searchText, fields);
		List<Document> search = indexer.search(searchText, fields, 200);
		Session session = ManagedJbpmContext.instance().getSession();
		
		for (Document d : search) {
			long taskId = Long.parseLong(d.get("id"));
			TaskInstance ti = (TaskInstance) session.get(TaskInstance.class, taskId);
			
			if (ti == null) {
				LOG.warn("Task não encontrada: " + taskId);
			} else {
				String s = HelpUtil.getBestFragments(query, getConteudo(ti));
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("texto", s);
				m.put("taskName", ti.getTask().getName());
				m.put("taskId", ti.getId());
				m.put("processo", ti.getProcessInstance().getContextInstance().getVariable("processo"));
				searchResult.add(m);
			}
		}
		resultSize = searchResult.size();
	}
	
	/**
	 * Método que realiza busca no sistema de acordo com o texto contido
	 * 
	 *   Analisa se existe texto a ser buscado e confere se o texto a ser
	 *   buscado é Numero de Processo, Id de Processo ({@link #searchProcesso()}),
	 *   ou se é texto normal ({@link #searchIndexer()})
	 */
	public void search() {
		if (searchText == null || "".equals(searchText.trim())) {
			return;
		}
		
		boolean isProcesso = searchProcesso();
		
		if (!isProcesso)	{
			try {
				searchIndexer();
			} catch (IOException e) {
				LOG.debug(e.getMessage());
			} catch (ParseException e) {
				LOG.debug(e.getMessage());
			}
		}
	}
	
	public static String getConteudo(TaskInstance ti) {
		StringBuilder sb = new StringBuilder();
		TaskController taskController = ti.getTask().getTaskController();
		if (taskController != null) {
			List<VariableAccess> vaList = taskController.getVariableAccesses();
			for (VariableAccess v : vaList) {
				Object conteudo = ti.getVariable(v.getMappedName());
				if (v.isWritable() && conteudo != null) {
					conteudo = JbpmUtil.instance().getConteudo(v, ti);
					sb.append(VariableHandler.getLabel(v.getVariableName()))
						.append(": ")
						.append(conteudo)
						.append("\n");
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
        if (resultSize % pageSize == 0) page--;
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
        return (page * pageSize + pageSize) > resultSize
                ? resultSize
                : page * pageSize + pageSize;
    }
    
	public String getTextoDestacado(Variavel v) {
		Object value = v.getValue();
		if (value == null){
			return null;
		}
		
		String texto = null;
		String type = v.getType();
		if (JbpmUtil.isTypeEditor(type)){
			texto = JbpmUtil.instance().valorProcessoDocumento((Integer) value);
		} else if("sim_nao".equals(type)) {
			texto = (Boolean)value ? "Sim" : "Não";
		} else {
			texto = value.toString();
		}
		
		if (searchText != null) {
			String[] fields = new String[]{"conteudo"};
			QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, HelpUtil.getAnalyzer());
			try {
				org.apache.lucene.search.Query query = parser.parse(searchText);
				String highlighted = HelpUtil.highlightText(query, texto, false);
				if (!highlighted.equals("")) {
					texto = highlighted;
				}
			} catch (ParseException e) {
				LOG.debug(e.getMessage());
			}
		}
		return texto;
	}
	
}
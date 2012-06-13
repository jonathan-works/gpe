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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
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

import br.com.infox.ibpm.component.ControleFiltros;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.help.HelpUtil;
import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.handler.VariableHandler;
import br.com.infox.ibpm.jbpm.handler.VariableHandler.Variavel;
import br.com.infox.search.Indexer;
import br.com.itx.component.AbstractHome;
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
	private static final LogProvider log = Logging.getLogProvider(SearchHandler.class);
	
	
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
	 
	
	public void search() {
		if (searchText == null || "".equals(searchText.trim())) {
			return;
		} 
		Processo processo = null;
		try {
			int prc = Integer.parseInt(searchText);
			processo = getEntityManager().find(Processo.class, prc);
		}  catch (Exception e) {
		}
		if (searchText.matches(NUMERO_PROCESSO_PATTERN)) {
			javax.persistence.Query query = EntityUtil.getEntityManager().createQuery(
					"select p from Processo p where p.numeroProcesso = :processo")
					.setParameter("processo", searchText);
			processo = EntityUtil.getSingleResult(query);
			
		}
		if (processo != null) {
			Redirect.instance().setConversationPropagationEnabled(false);
			Redirect.instance().setViewId("/Processo/Consulta/list.xhtml");
			Redirect.instance().setParameter("id", processo.getIdProcesso());
			Redirect.instance().setParameter("idJbpm", processo.getIdJbpm());
			Redirect.instance().execute();
			return; 
		}
		
		searchResult = new ArrayList<Map<String,Object>>();
		try {
			Indexer indexer = new Indexer();
			String[] fields = new String[]{"conteudo", "texto"};
			Query query = indexer.getQuery(searchText, fields);
			List<Document> search = indexer.search(searchText, fields, 200);
			Session session = ManagedJbpmContext.instance().getSession();
			for (Document d : search) {
				long taskId = Long.parseLong(d.get("id"));
				TaskInstance ti = (TaskInstance) session.get(TaskInstance.class, taskId);
				if (ti != null) {
					String s = HelpUtil.getBestFragments(query, getConteudo(ti));
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("texto", s);
					m.put("taskName", ti.getTask().getName());
					m.put("taskId", ti.getId());
					m.put("processo", ti.getProcessInstance().getContextInstance().getVariable("processo"));
					searchResult.add(m);
				} else {
					log.warn("Task não encontrada: " + taskId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultSize = searchResult.size();
	}
	
	@SuppressWarnings("unchecked")
	private List<Long> getIdListOrdenadaTarefasVisiveis(List<Document> search) {
		if (search.size() == 0) {
			return Collections.emptyList();
		}
		List<Long> listIdTaskInstance = new ArrayList<Long>(search.size());
		for (Document d : search) {
			listIdTaskInstance.add(Long.parseLong(d.get("id")));
		}
		ControleFiltros.instance().iniciarFiltro();
		javax.persistence.Query query = EntityUtil.createQuery("select o.idTaskInstance from SituacaoProcesso o where " +
				"o.idTaskInstance in (:listIdTaskInstance)");
		query.setParameter("listIdTaskInstance", listIdTaskInstance);
		List<Long> list = query.getResultList();
		Collections.sort(list);
		return list;
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
		if (v.getValue() == null){
			return null;
		}
		String texto = null;
		if (JbpmUtil.isTypeEditor(v.getType())){
			texto = JbpmUtil.instance().valorProcessoDocumento((Integer) v.getValue());
		} else {
			texto = v.getValue().toString();
		}
		if (searchText != null) {
			String[] fields = new String[]{"conteudo"};
			QueryParser parser = new MultiFieldQueryParser(fields, HelpUtil.getAnalyzer());
			try {
				org.apache.lucene.search.Query query = parser.parse(searchText);
				String highlighted = HelpUtil.highlightText(query, texto, false);
				if (!highlighted.equals("")) {
					texto = highlighted;
				}
			} catch (ParseException e) {
			}
		}
		return texto;
	}
	
}
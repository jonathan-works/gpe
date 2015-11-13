package br.com.infox.ibpm.task.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery.TooManyClauses;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.service.SigiloProcessoService;
import br.com.infox.ibpm.task.entity.TaskConteudo;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.VariableHandler;

@Name(TaskConteudoDAO.NAME)
@AutoCreate
public class TaskConteudoDAO extends DAO<TaskConteudo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskConteudoDAO";

    @In
    private SigiloProcessoService sigiloProcessoService;

    @SuppressWarnings(UNCHECKED)
    private String extractConteudo(Long taskId) {
        Session session = ManagedJbpmContext.instance().getSession();
        TaskInstance ti = (TaskInstance) session.get(TaskInstance.class, taskId);
        StringBuilder sb = new StringBuilder();
        TaskController taskController = ti.getTask().getTaskController();
        if (taskController != null) {
            List<VariableAccess> vaList = taskController.getVariableAccesses();
            for (VariableAccess v : vaList) {
                Object conteudo = ti.getVariable(v.getMappedName());
                if (v.isWritable() && conteudo != null) {
                    conteudo = JbpmUtil.instance().getConteudo(v, ti);
                    sb.append(VariableHandler.getLabel(v.getVariableName())).append(conteudo).append("\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public TaskConteudo persist(TaskConteudo taskConteudo) throws DAOException {
        taskConteudo.setConteudo(extractConteudo(taskConteudo.getIdTaskInstance()));
        return super.persist(taskConteudo);
    }

    @Override
    @Transactional
    public TaskConteudo update(TaskConteudo taskConteudo) throws DAOException {
        taskConteudo.setConteudo(extractConteudo(taskConteudo.getIdTaskInstance()));
        return super.update(taskConteudo);
    }

    @Override
    public FullTextEntityManager getEntityManager() {
        return Search.getFullTextEntityManager(super.getEntityManager());
    }

    @SuppressWarnings(UNCHECKED)
    public List<TaskConteudo> pesquisar(String searchPattern) throws TooManyClauses, ParseException {
    	FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(getEntityManager());
        List<TaskConteudo> ret = new ArrayList<TaskConteudo>();
        QueryParser parser = new QueryParser(Version.LUCENE_36, "conteudo", new BrazilianAnalyzer(Version.LUCENE_36));
        Query luceneQuery = parser.parse(searchPattern);
        FullTextQuery query = fullTextEntityManager.createFullTextQuery(luceneQuery, TaskConteudo.class);
        query.setMaxResults(50);
        List<TaskConteudo> results = query.getResultList();
        int passo = 0;
        UsuarioLogin usuario = Authenticator.getUsuarioLogado();
        while (ret.size() < 50 && results != null && !results.isEmpty()) {
        	passo++;
	        for (int i = 0; i < results.size() && ret.size() < 50; i++) {
	        	TaskConteudo taskConteudo = results.get(i);
	        	Processo processo = getEntityManager().find(Processo.class, taskConteudo.getNumeroProcesso());
	        	if (sigiloProcessoService.usuarioPossuiPermissao(usuario, processo)){
	                ret.add(taskConteudo);
	            }
	        }
	        if (ret.size() < 50) {
	        	query.setFirstResult(passo * 50);
	        	results = query.getResultList();
	        }
	        if (passo * 50 > 1000) {
	        	break;
	        }
        }
        return ret;
    }
}

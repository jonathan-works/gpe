package br.com.infox.epp.access.assignment;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.bpm.TaskInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.transaction.TransactionService;
import br.com.infox.seam.util.ComponentUtil;

@Name(LocalizacaoAssignment.NAME)
@Install(precedence = Install.FRAMEWORK)
@Transactional
public class LocalizacaoAssignment implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(LocalizacaoAssignment.class);
    private static final String IBPM_QUERY_INSERT = "insert into tb_processo_localizacao_ibpm "
            + "(id_task_jbpm, id_processinstance_jbpm, id_processo, "
            + "id_localizacao, id_papel, in_contabilizar, id_task_instance) "
            + "values (:idTaskJbpm, :idProcessInstance, :idProcesso, "
            + ":idLocalizacao, :idPapel, :contabilizar, :taskInstance)";
    public static final String NAME = "localizacaoAssignment";
    private org.jbpm.taskmgmt.exe.TaskInstance currentTaskInstance;
    
    @In
    private InfoxMessages infoxMessages;

    @SuppressWarnings(UNCHECKED)
    public Set<String> getPooledActors(String... localPapel) {
        boolean opened = TransactionService.beginTransaction();
        addLocalizacaoPapel(localPapel);
        if (opened) {
            TransactionService.commitTransction();
        }
        return Collections.EMPTY_SET;
    }

    protected boolean addLocalizacaoPapel(String... localPapel) {
        Processo processo = JbpmUtil.getProcesso();
        JbpmUtil.getJbpmSession().flush();
        if (currentTaskInstance == null) {
            currentTaskInstance = TaskInstance.instance();
        }
        if (localPapel == null || allNullElements(localPapel)) {
            throw new BusinessException(infoxMessages.get("process.swimlane.notDefined"));
        }
        if (currentTaskInstance == null || processo == null) {
            return false;
        }
        boolean inserted = false;
        PerfilTemplateManager perfilTemplateManager = ComponentUtil.getComponent(PerfilTemplateManager.NAME);
        for (String s : localPapel) {
            insertProcessoLocalizacaoIbpm(perfilTemplateManager.find(Integer.valueOf(s)), processo);
            inserted = true;
        }
        return inserted;
    }

    /**
     * Verifica se todos os elementos do Array são null
     * 
     * @param o Array que deseja-se verificar se é vazio
     * @return True se for vazio
     */
    private boolean allNullElements(String[] o) {
        for (int i = 0; i < o.length; i++) {
            
            if (o[i] != null && o[i].trim().length()!=0) {
                return false;
            }
        }
        return true;
    }

    protected void insertProcessoLocalizacaoIbpm(PerfilTemplate perfilTemplate, Processo processo) {
        org.hibernate.Query q = JbpmUtil.getJbpmSession().createSQLQuery(IBPM_QUERY_INSERT);
        Long taskId = currentTaskInstance.getTask().getId();
        q.setParameter("idTaskJbpm", taskId);
        q.setParameter("idProcessInstance", ProcessInstance.instance().getId());
        q.setParameter("idProcesso", processo.getIdProcesso());
        q.setParameter("idLocalizacao", perfilTemplate.getLocalizacao().getIdLocalizacao());
        q.setParameter("idPapel", perfilTemplate.getPapel().getIdPapel());
        q.setParameter("contabilizar", true);
        q.setParameter("taskInstance", currentTaskInstance.getId());
        q.executeUpdate();
    }

    public Set<String> getPooledActors(String expression) {
        return getPooledActors(parse(expression));
    }

    public static String[] parse(String expression) {
        return expression.split(",");
    }

    public void setPooledActors(String expression) {
        getPooledActors(expression);
    }

    @Observer(Event.EVENTTYPE_TASK_CREATE)
    public void onTaskCreate(ExecutionContext context) {
        try {
            String expression = context.getTask().getSwimlane().getPooledActorsExpression();
            this.currentTaskInstance = context.getTaskInstance();
            getPooledActors(expression);
        } catch (BusinessException e) {
        	String msg = String.format("Erro ao inserir processo localização: %s. Contate o Administrador do Sistema.", e.getLocalizedMessage());
        	TransactionService.rollbackTransaction();
        	throw new BusinessException(msg);
        }
        catch (Exception ex) {
            LOG.error(".onTaskCreate", ex);
            String action = "inserir processo localização: ";
            LOG.warn(action, ex);
            throw new ApplicationException(ApplicationException.createMessage(action
                    + ex.getLocalizedMessage(), "onTaskCreate()", "LocalizacaoAssignment", "BPM"), ex);
        }
    }

    public static LocalizacaoAssignment instance() {
        return ComponentUtil.getComponent(NAME);
    }

}

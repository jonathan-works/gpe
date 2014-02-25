package br.com.infox.epp.access.assignment;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import org.hibernate.type.IntegerType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.core.exception.ApplicationException;
import br.com.infox.core.transaction.TransactionService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.itx.component.Util;
import br.com.itx.util.ComponentUtil;

@Name(LocalizacaoAssignment.NAME)
@BypassInterceptors
@Install(precedence = Install.FRAMEWORK)
public class LocalizacaoAssignment implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(LocalizacaoAssignment.class);
    private static final String IBPM_QUERY_INSERT = "insert into public.tb_processo_localizacao_ibpm "
            + "(id_task_jbpm, id_processinstance_jbpm, id_processo, "
            + "id_localizacao, id_papel, in_contabilizar, id_task_instance) "
            + "values (:idTaskJbpm, :idProcessInstance, :idProcesso, "
            + ":idLocalizacao, :idPapel, :contabilizar, :taskInstance)";
    public static final String NAME = "localizacaoAssignment";
    private org.jbpm.taskmgmt.exe.TaskInstance currentTaskInstance;

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
            return false;
        }
        if (currentTaskInstance == null || processo == null) {
            return false;
        }
        boolean inserted = false;
        for (String s : localPapel) {
            insertProcessoLocalizacaoIbpm(s, processo);
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
            if (o[i] != null) {
                return false;
            }
        }
        return true;
    }

    protected void insertProcessoLocalizacaoIbpm(String s, Processo processo) {
        org.hibernate.Query q = JbpmUtil.getJbpmSession().createSQLQuery(IBPM_QUERY_INSERT);
        Long taskId = currentTaskInstance.getTask().getId();
        String[] localizacaoPapel = splitLocalizacaoPapel(s);
        q.setParameter("idTaskJbpm", taskId);
        q.setParameter("idProcessInstance", ProcessInstance.instance().getId());
        q.setParameter("idProcesso", processo.getIdProcesso());
        if (localizacaoPapel[0] == null) {
            String action = "inserir Localização. Não existe Localização com o id = "
                    + s.split(":")[0];
            LOG.warn(action);
            throw new ApplicationException(ApplicationException.createMessage(action, "getPooledActors()", "LocalizacaoAssignment", "BPM"));
        }
        q.setParameter("idLocalizacao", Integer.parseInt(localizacaoPapel[0]));
        if (localizacaoPapel[1] == null) {
            q.setParameter("idPapel", null, new IntegerType());
        } else {
            if ("true".equals(localizacaoPapel[1])
                    || "false".equals(localizacaoPapel[1])) {
                q.setParameter("idPapel", null, new IntegerType());
                q.setParameter("contabilizar", Boolean.getBoolean(localizacaoPapel[1]));
            } else {
                q.setParameter("idPapel", Integer.parseInt(localizacaoPapel[1]));
                if (localizacaoPapel[2] == null
                        || "false".equals(localizacaoPapel[2])) {
                    q.setParameter("contabilizar", false);
                } else {
                    q.setParameter("contabilizar", true);
                }
            }
        }
        q.setParameter("taskInstance", currentTaskInstance.getId());
        q.executeUpdate();
    }

    private static String[] splitLocalizacaoPapel(String localPapel) {
        String[] ret = new String[3];
        if (localPapel.contains(":")) {
            String[] split = localPapel.split(":");
            ret[0] = split[0];
            if (split.length >= 2) {
                ret[1] = split[1];
            }
            if (split.length == 3) {
                ret[2] = split[2];
            }
        }
        return ret;
    }

    public Set<String> getPooledActors(String expression) {
        return getPooledActors(parse(expression));
    }

    public static String[] parse(String expression) {
        String auxiliarExpression = expression;
        if (auxiliarExpression == null) {
            return null;
        }
        auxiliarExpression = auxiliarExpression.substring(auxiliarExpression.indexOf('(') + 1);
        auxiliarExpression = auxiliarExpression.replaceAll("'", "");
        auxiliarExpression = auxiliarExpression.replace(")", "");
        auxiliarExpression = auxiliarExpression.replace("}", "");
        return auxiliarExpression.split(",");
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
        } catch (Exception ex) {
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
